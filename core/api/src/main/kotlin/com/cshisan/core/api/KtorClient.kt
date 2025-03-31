package com.cshisan.core.api

import com.cshisan.common.utils.Gson
import com.cshisan.core.model.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KtorClient @Inject constructor() {
  val instance = HttpClient {
    install(ContentNegotiation) {
      json(Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
      })
    }
    install(HttpTimeout) {
      requestTimeoutMillis = 30000
      connectTimeoutMillis = 15000
      socketTimeoutMillis = 30000
    }
    defaultRequest {
      url("http://10.0.2.2:8013")
    }
  }

  suspend inline fun <reified REQ, RESP> post(url: String, params: REQ?): Result<RESP> {
    return try {
      val response = instance.post {
        url(url)
        contentType(ContentType.Application.Json)
        params?.let { setBody(it) }
      }
      response.body<Result<RESP>>()
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

  suspend inline fun <reified REQ, RESP> get(url: String, params: REQ?): Result<RESP> {
    return try {
      val response = instance.get {
        url(url)
        params?.let {
          val map = Gson.e2e(it, Map::class.java)
          map.forEach { (key, value) -> parameter(key.toString(), value) }
        }
      }
      response.body<Result<RESP>>()
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
}