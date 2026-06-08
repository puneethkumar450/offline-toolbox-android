package com.puneeth450.offlinetoolbox.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tool_history")
data class ToolHistoryEntity(
    @PrimaryKey val id: String,
    val toolId: String,
    val title: String,
    val input: String,
    val output: String,
    val createdAt: Long
)
