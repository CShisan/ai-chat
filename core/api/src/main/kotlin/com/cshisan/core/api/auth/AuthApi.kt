package com.cshisan.core.api.auth

import com.cshisan.core.model.Result

interface AuthApi {
  val baseUrl: String
    get() = "/auth"

  /**
   * 验证码
   */
  suspend fun captcha(): Result<String>

  /**
   * 验证码
   */
  suspend fun login(params: LoginRequest): Result<LoginResponse>
}