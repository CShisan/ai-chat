package com.cshisan.core.api.chat

import com.cshisan.core.model.Result

/**
 * AI聊天服务接口
 */
interface ChatApi {
  val baseUrl: String
    get() = "/chat"

  /**
     * 发送聊天请求到AI服务
     * 
     * @param request 聊天请求
     * @return 聊天响应的结果
     */
    suspend fun sendChatRequest(request: ChatRequest): Result<ChatResponse>
    
    /**
     * 获取可用的AI模型列表
     * 
     * @return 可用AI模型列表
     */
    suspend fun aiModels(): Result<List<String>>
} 