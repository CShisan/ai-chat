package com.cshisan.common.utils

import com.google.gson.Gson

private val gson: Gson = Gson()
  .newBuilder()
  .setDateFormat("yyyy-MM-dd HH:mm:ss") // 可选配置
  .create()

object Gson {
  fun toJson(obj: Any): String {
    return gson.toJson(obj)
  }

  fun <T> toEntity(json: String, type: Class<T>): T {
    return gson.fromJson(json, type)
  }

  fun <T1, T2> e2e(entity: T1, type: Class<T2>): T2 {
    return gson.fromJson(gson.toJson(entity), type)
  }
}
