package com.cshisan.core.api.auth

import com.cshisan.core.api.KtorClient
import com.cshisan.core.model.Result
import javax.inject.Inject

class AuthApiImpl @Inject constructor(
  private val client: KtorClient
) : AuthApi {

  override suspend fun captcha(): Result<String> {
    return client.post("$baseUrl/captcha", Unit)
  }

  override suspend fun login(params: LoginRequest): Result<LoginResponse> {
    return client.post("$baseUrl/captcha", params)
  }
}