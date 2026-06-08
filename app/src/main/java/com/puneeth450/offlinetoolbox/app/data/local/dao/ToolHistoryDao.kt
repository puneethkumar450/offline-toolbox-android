package com.puneeth450.offlinetoolbox.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.puneeth450.offlinetoolbox.app.data.local.entity.ToolHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ToolHistoryEntity)

    @Query("SELECT * FROM tool_history ORDER BY createdAt DESC LIMIT :limit")
    fun observeRecent(limit: Int = 20): Flow<List<ToolHistoryEntity>>

    @Query("SELECT * FROM tool_history WHERE toolId = :toolId ORDER BY createdAt DESC LIMIT :limit")
    fun observeByTool(toolId: String, limit: Int = 20): Flow<List<ToolHistoryEntity>>

    @Query("DELETE FROM tool_history WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM tool_history")
    suspend fun clear()
}
