package com.cshisan.core.api.auth

interface LoginRequest

data class PhonePwLoginRequest(
  val phone: String,
  val password: String,
  val captchaCode: String,
  val captchaSign: String
) : LoginRequest

