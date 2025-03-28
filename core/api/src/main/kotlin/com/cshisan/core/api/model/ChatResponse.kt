package com.cshisan.core.api.model

import kotlinx.serialization.Serializable

/**
 * 从AI服务接收的聊天响应
 * 
 * @param id 响应的唯一标识符
 * @param message 响应消息
 * @param modelId 使用的模型ID
 * @param usage 资源使用信息
 */
@Serializable
data class ChatResponse(
  val id: String,
  val message: ChatResponseMessage,
  val modelId: String,
  val usage: TokenUsage
)

/**
 * 聊天响应中的消息格式
 * 
 * @param role 消息角色（通常是assistant）
 * @param content 消息内容
 */
@Serializable
data class ChatResponseMessage(
    val role: String,
    val content: String
)

/**
 * 令牌使用情况
 * 
 * @param promptTokens 提示使用的令牌数
 * @param completionTokens 完成使用的令牌数
 * @param totalTokens 总令牌数
 */
@Serializable
data class TokenUsage(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int
) 