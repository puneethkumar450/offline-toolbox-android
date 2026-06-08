package com.puneeth450.offlinetoolbox.app.data.repository

import com.puneeth450.offlinetoolbox.app.data.local.dao.ToolHistoryDao
import com.puneeth450.offlinetoolbox.app.data.local.entity.ToolHistoryEntity
import java.util.UUID

class ToolHistoryRepository(private val dao: ToolHistoryDao) {
    suspend fun save(toolId: String, title: String, input: String, output: String) {
        dao.insert(
            ToolHistoryEntity(
                id = UUID.randomUUID().toString(),
                toolId = toolId,
                title = title,
                input = input,
                output = output,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    fun observeRecent(limit: Int = 20) = dao.observeRecent(limit)
    fun observeByTool(toolId: String, limit: Int = 20) = dao.observeByTool(toolId, limit)
}
