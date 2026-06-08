package com.puneeth450.offlinetoolbox.app.feature.device.info

import android.os.Build
import androidx.compose.runtime.Composable
import com.puneeth450.offlinetoolbox.app.ui.components.ResultCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolScaffold

@Composable
fun DeviceInfoScreen() {
    ToolScaffold("Device Information") {
        ResultCard("Manufacturer", Build.MANUFACTURER.orEmpty())
        ResultCard("Model", Build.MODEL.orEmpty())
        ResultCard("Device", Build.DEVICE.orEmpty())
        ResultCard("Android Version", "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
        ResultCard("Hardware", Build.HARDWARE.orEmpty())
        ResultCard("Supported ABIs", Build.SUPPORTED_ABIS.joinToString())
    }
}
