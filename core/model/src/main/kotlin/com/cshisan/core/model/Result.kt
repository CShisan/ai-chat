package com.cshisan.core.model

/**
 * 封装网络请求结果
 * 
 * @param T 成功时返回的数据类型
 */
sealed class Result<out T> {
    /**
     * 操作成功
     * 
     * @param data 成功返回的数据
     */
    data class Success<T>(val data: T) : Result<T>()
    
    /**
     * 操作失败
     * 
     * @param errorMessage 错误信息
     * @param code 错误代码
     */
    data class Error(val errorMessage: String, val code: Int = 0) : Result<Nothing>()
    
    /**
     * 正在加载
     */
    data object Loading : Result<Nothing>()
    
    /**
     * 判断结果是否成功
     */
    val isSuccess: Boolean
        get() = this is Success
    
    /**
     * 获取成功数据，如果失败则返回null
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    /**
     * 获取错误信息，如果成功则返回null
     */
    fun errorOrNull(): String? = when (this) {
        is Error -> errorMessage
        else -> null
    }
} 