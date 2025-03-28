package com.cshisan.core.api

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
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
}