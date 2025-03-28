package com.cshisan.core.model

import java.time.LocalDateTime

/**
 * 表示一个聊天会话
 * 
 * @param id 会话唯一标识符
 * @param title 会话标题
 * @param createdAt 会话创建时间
 * @param updatedAt 会话最后更新时间
 * @param userId 用户ID
 */
data class Conversation(
    val id: String,
    val title: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val userId: String
) 