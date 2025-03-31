package com.cshisan.core.api.chat

import kotlinx.serialization.Serializable

/**
 * 发送到AI服务的聊天请求
 * 
 * @param messages 消息历史
 * @param modelId 使用的模型ID
 * @param temperature 模型温度参数
 * @param maxTokens 最大令牌数
 */
@Serializable
data class ChatRequest(
  val messages: List<Message>,
  val modelId: String,
  val temperature: Float = 0.7f,
  val maxTokens: Int = 2048
)

/**
 * 聊天请求中的消息格式
 * 
 * @param role 消息角色（用户或助手）
 * @param content 消息内容
 */
@Serializable
data class Message(
    val role: String,
    val content: String
) {
    companion object {
        const val ROLE_USER = "user"
        const val ROLE_ASSISTANT = "assistant"
        const val ROLE_SYSTEM = "system"
    }
} 