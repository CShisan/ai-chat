package com.cshisan.core.repository

import com.cshisan.core.model.ChatMessage
import com.cshisan.core.model.Conversation
import com.cshisan.core.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * 聊天相关的数据仓库接口
 */
interface ChatRepository {
    /**
     * 获取所有会话
     * 
     * @param userId 用户ID
     * @return 会话列表
     */
    fun getConversations(userId: String): Flow<List<Conversation>>
    
    /**
     * 获取单个会话
     * 
     * @param conversationId 会话ID
     * @return 会话详情
     */
    suspend fun getConversation(conversationId: String): Result<Conversation>
    
    /**
     * 创建新会话
     * 
     * @param conversation 会话信息
     * @return 创建结果
     */
    suspend fun createConversation(conversation: Conversation): Result<String>
    
    /**
     * 更新会话
     * 
     * @param conversation 会话信息
     * @return 更新结果
     */
    suspend fun updateConversation(conversation: Conversation): Result<Unit>
    
    /**
     * 删除会话
     * 
     * @param conversationId 会话ID
     * @return 删除结果
     */
    suspend fun deleteConversation(conversationId: String): Result<Unit>
    
    /**
     * 获取会话中的所有消息
     * 
     * @param conversationId 会话ID
     * @return 消息列表
     */
    fun getMessages(conversationId: String): Flow<List<ChatMessage>>
    
    /**
     * 添加消息
     * 
     * @param message 消息内容
     * @return 添加结果
     */
    suspend fun addMessage(message: ChatMessage): Result<String>
    
    /**
     * 删除消息
     * 
     * @param messageId 消息ID
     * @return 删除结果
     */
    suspend fun deleteMessage(messageId: String): Result<Unit>
    
    /**
     * 清空会话中的所有消息
     * 
     * @param conversationId 会话ID
     * @return 清空结果
     */
    suspend fun clearMessages(conversationId: String): Result<Unit>
} 