package com.cshisan.core.model

/**
 * 表示AI聊天模型
 * 
 * @param id 模型唯一标识符
 * @param name 模型名称
 * @param description 模型描述
 * @param apiEndpoint API端点URL
 * @param maxTokens 最大令牌数
 * @param temperature 模型温度参数（创造性）
 * @param isDefault 是否为默认模型
 */
data class AiModel(
    val id: String,
    val name: String,
    val description: String,
    val apiEndpoint: String,
    val maxTokens: Int = 2048,
    val temperature: Float = 0.7f,
    val isDefault: Boolean = false
) 