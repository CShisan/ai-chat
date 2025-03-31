package com.cshisan.core.repository

import com.cshisan.core.model.Result
import com.cshisan.core.model.User
import kotlinx.coroutines.flow.Flow

/**
 * 用户数据仓库接口
 */
interface UserRepository {
    /**
     * 获取当前用户信息
     * 
     * @return 用户信息流
     */
    fun getCurrentUser(): Flow<User?>
    
    /**
     * 根据ID获取用户
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    suspend fun getUser(userId: String): Result<User>
    
    /**
     * 用户注册
     * 
     * @param email 邮箱
     * @param username 用户名
     * @param password 密码
     * @return 注册结果
     */
    suspend fun register(email: String, password: String): Result<User>
    
    /**
     * 用户登录
     * 
     * @param email 邮箱
     * @param password 密码
     * @return 登录结果
     */
    suspend fun login(email: String, password: String): Result<User>
    
    /**
     * 用户登出
     * 
     * @return 登出结果
     */
    suspend fun logout(): Result<Unit>
    
    /**
     * 更新用户信息
     * 
     * @param user 更新后的用户信息
     * @return 更新结果
     */
    suspend fun updateUserProfile(user: User): Result<User>
} 