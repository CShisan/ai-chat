package com.cshisan.core.api.chat

import com.cshisan.core.api.KtorClient
import com.cshisan.core.model.Result
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 聊天服务实现
 *
 * @param client HTTP客户端
 */
@Singleton
class ChatApiImpl @Inject constructor(
  private val client: KtorClient
) : ChatApi {

  override suspend fun sendChatRequest(request: ChatRequest): Result<ChatResponse> {
    return try {
      val response = client.instance.post {
        url("$baseUrl/chat")
        contentType(ContentType.Application.Json)
        setBody(request)
      }

      response.body<Result<ChatResponse>>()
    } catch (e: Exception) {
      when (e) {
        is ClientRequestException -> Result.Error(
          "请求错误: ${e.response.status.description}",
          e.response.status.value
        )
        is ServerResponseException -> Result.Error("服务器错误", e.response.status.value)
        is HttpRequestTimeoutException -> Result.Error("请求超时", 408)
        else -> Result.Error("网络错误: ${e.message}")
      }
    }
  }

  override suspend fun aiModels(): Result<List<String>> {
    return try {
      val response = client.instance.get {
        url("$baseUrl/models")
      }

      response.body<Result<List<String>>>()
    } catch (e: Exception) {
      Result.Error("获取模型失败: ${e.message}")
    }
  }
} 