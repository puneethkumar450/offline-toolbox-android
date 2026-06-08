package com.puneeth450.offlinetoolbox.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.puneeth450.offlinetoolbox.app.data.local.dao.ToolHistoryDao
import com.puneeth450.offlinetoolbox.app.data.local.entity.ToolHistoryEntity

@Database(
    entities = [ToolHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun toolHistoryDao(): ToolHistoryDao
}
