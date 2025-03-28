package com.cshisan.core.model

import java.time.LocalDateTime

/**
 * 表示一条聊天消息
 * 
 * @param id 消息唯一标识符
 * @param content 消息内容
 * @param isFromUser 消息是否来自用户（否则为AI回复）
 * @param timestamp 消息发送时间
 * @param conversationId 所属的对话ID
 * @param isError 是否为错误消息
 */
data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val conversationId: String,
    val isError: Boolean = false
) 