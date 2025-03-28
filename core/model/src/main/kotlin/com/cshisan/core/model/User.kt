package com.cshisan.core.model

import java.time.LocalDateTime

/**
 * 表示应用用户
 * 
 * @param id 用户唯一标识符
 * @param username 用户名
 * @param email 用户邮箱
 * @param profilePictureUrl 用户头像URL
 * @param createdAt 用户创建时间
 */
data class User(
    val id: String,
    val username: String,
    val email: String,
    val profilePictureUrl: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
) 