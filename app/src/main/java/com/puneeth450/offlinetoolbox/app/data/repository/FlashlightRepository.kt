package com.puneeth450.offlinetoolbox.app.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

data class FlashlightStatus(
    val isSupported: Boolean,
    val isEnabled: Boolean = false,
    val isAvailable: Boolean = false,
    val error: String? = null
)

@Singleton
class FlashlightRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val packageManager: PackageManager = context.packageManager
    private val cameraManager: CameraManager = context.getSystemService(CameraManager::class.java)

    fun observeTorchState(): Flow<FlashlightStatus> = callbackFlow {
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            trySend(FlashlightStatus(isSupported = false, error = "This device does not have a flash unit."))
            close()
            return@callbackFlow
        }

        val cameraId = findTorchCameraId()
        if (cameraId == null) {
            trySend(FlashlightStatus(isSupported = false, error = "No compatible rear flash camera was found."))
            close()
            return@callbackFlow
        }

        val callback = object : CameraManager.TorchCallback() {
            override fun onTorchModeChanged(cameraIdArg: String, enabled: Boolean) {
                if (cameraIdArg == cameraId) {
                    trySend(FlashlightStatus(isSupported = true, isEnabled = enabled, isAvailable = true))
                }
            }

            override fun onTorchModeUnavailable(cameraIdArg: String) {
                if (cameraIdArg == cameraId) {
                    trySend(
                        FlashlightStatus(
                            isSupported = true,
                            isEnabled = false,
                            isAvailable = false,
                            error = "Flashlight is temporarily unavailable because the camera is in use."
                        )
                    )
                }
            }
        }

        try {
            cameraManager.registerTorchCallback(callback, null)
            trySend(FlashlightStatus(isSupported = true, isEnabled = false, isAvailable = true))
        } catch (error: Exception) {
            trySend(FlashlightStatus(isSupported = false, error = error.message ?: "Unable to access flashlight state."))
            close()
            return@callbackFlow
        }

        awaitClose { cameraManager.unregisterTorchCallback(callback) }
    }

    fun setTorchEnabled(enabled: Boolean): Result<Unit> = runCatching {
        val cameraId = findTorchCameraId() ?: error("No compatible rear flash camera was found.")
        try {
            cameraManager.setTorchMode(cameraId, enabled)
        } catch (error: CameraAccessException) {
            throw IllegalStateException("Camera access failed while switching the flashlight.")
        } catch (error: SecurityException) {
            throw IllegalStateException("Flashlight access is blocked on this device.")
        }
    }

    private fun findTorchCameraId(): String? {
        return cameraManager.cameraIdList.firstOrNull { cameraId ->
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
            hasFlash && lensFacing == CameraCharacteristics.LENS_FACING_BACK
        }
    }
}
