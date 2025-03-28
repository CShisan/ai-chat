package com.cshisan.core.repository.impl

import com.cshisan.core.model.ChatMessage
import com.cshisan.core.model.Conversation
import com.cshisan.core.model.Result
import com.cshisan.core.repository.ChatRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 基于Firebase实现的聊天仓库
 * 
 * @param firestore Firestore实例
 */
@Singleton
class FirebaseChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChatRepository {
    
    private val conversationsCollection = firestore.collection("conversations")
    private val messagesCollection = firestore.collection("messages")
    
    override fun getConversations(userId: String): Flow<List<Conversation>> = callbackFlow {
        val listener = conversationsCollection
            .whereEqualTo("userId", userId)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // 发生错误时，只发送一个空列表，不关闭流
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                
                val conversations = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val id = doc.id
                        val title = doc.getString("title") ?: ""
                        val uid = doc.getString("userId") ?: ""
                        val createdAtTimestamp = doc.getTimestamp("createdAt")
                        val updatedAtTimestamp = doc.getTimestamp("updatedAt")
                        
                        val createdAt = createdAtTimestamp?.toDate()?.toInstant()
                            ?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
                            ?: LocalDateTime.now()
                        
                        val updatedAt = updatedAtTimestamp?.toDate()?.toInstant()
                            ?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
                            ?: LocalDateTime.now()
                        
                        Conversation(id, title, createdAt, updatedAt, uid)
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                
                trySend(conversations)
            }
        
        awaitClose { listener.remove() }
    }
    
    override suspend fun getConversation(conversationId: String): Result<Conversation> {
        return try {
            val document = conversationsCollection.document(conversationId).get().await()
            
            if (document.exists()) {
                val id = document.id
                val title = document.getString("title") ?: ""
                val userId = document.getString("userId") ?: ""
                val createdAtTimestamp = document.getTimestamp("createdAt")
                val updatedAtTimestamp = document.getTimestamp("updatedAt")
                
                val createdAt = createdAtTimestamp?.toDate()?.toInstant()
                    ?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
                    ?: LocalDateTime.now()
                
                val updatedAt = updatedAtTimestamp?.toDate()?.toInstant()
                    ?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
                    ?: LocalDateTime.now()
                
                Result.Success(Conversation(id, title, createdAt, updatedAt, userId))
            } else {
                Result.Error("会话不存在")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "获取会话失败")
        }
    }
    
    override suspend fun createConversation(conversation: Conversation): Result<String> {
        return try {
            val data = hashMapOf(
                "title" to conversation.title,
                "userId" to conversation.userId,
                "createdAt" to Date.from(conversation.createdAt.atZone(ZoneId.systemDefault()).toInstant()),
                "updatedAt" to Date.from(conversation.updatedAt.atZone(ZoneId.systemDefault()).toInstant())
            )
            
            val documentRef = if (conversation.id.isEmpty()) {
                conversationsCollection.document()
            } else {
                conversationsCollection.document(conversation.id)
            }
            
            documentRef.set(data).await()
            Result.Success(documentRef.id)
        } catch (e: Exception) {
            Result.Error(e.message ?: "创建会话失败")
        }
    }
    
    override suspend fun updateConversation(conversation: Conversation): Result<Unit> {
        return try {
            val data = hashMapOf(
                "title" to conversation.title,
                "updatedAt" to Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
            )
            
            conversationsCollection.document(conversation.id).update(data as Map<String, Any>).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "更新会话失败")
        }
    }
    
    override suspend fun deleteConversation(conversationId: String): Result<Unit> {
        return try {
            // 先删除会话下的所有消息
            val querySnapshot = messagesCollection
                .whereEqualTo("conversationId", conversationId)
                .get()
                .await()
            
            // 批量删除消息
            val batch = firestore.batch()
            querySnapshot.documents.forEach { document ->
                batch.delete(messagesCollection.document(document.id))
            }
            
            // 删除会话
            batch.delete(conversationsCollection.document(conversationId))
            batch.commit().await()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "删除会话失败")
        }
    }
    
    override fun getMessages(conversationId: String): Flow<List<ChatMessage>> = callbackFlow {
        val listener = messagesCollection
            .whereEqualTo("conversationId", conversationId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                
                val messages = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val id = doc.id
                        val content = doc.getString("content") ?: ""
                        val isFromUser = doc.getBoolean("isFromUser") ?: false
                        val timestamp = doc.getTimestamp("timestamp")
                        val convId = doc.getString("conversationId") ?: ""
                        val isError = doc.getBoolean("isError") ?: false
                        
                        val messageTime = timestamp?.toDate()?.toInstant()
                            ?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
                            ?: LocalDateTime.now()
                        
                        ChatMessage(id, content, isFromUser, messageTime, convId, isError)
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                
                trySend(messages)
            }
        
        awaitClose { listener.remove() }
    }
    
    override suspend fun addMessage(message: ChatMessage): Result<String> {
        return try {
            val data = hashMapOf(
                "content" to message.content,
                "isFromUser" to message.isFromUser,
                "timestamp" to Date.from(message.timestamp.atZone(ZoneId.systemDefault()).toInstant()),
                "conversationId" to message.conversationId,
                "isError" to message.isError
            )
            
            val documentRef = if (message.id.isEmpty()) {
                messagesCollection.document()
            } else {
                messagesCollection.document(message.id)
            }
            
            documentRef.set(data).await()
            
            // 更新会话的最后更新时间
            val updateData = hashMapOf(
                "updatedAt" to Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
            )
            conversationsCollection.document(message.conversationId).update(updateData as Map<String, Any>).await()
            
            Result.Success(documentRef.id)
        } catch (e: Exception) {
            Result.Error(e.message ?: "添加消息失败")
        }
    }
    
    override suspend fun deleteMessage(messageId: String): Result<Unit> {
        return try {
            messagesCollection.document(messageId).delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "删除消息失败")
        }
    }
    
    override suspend fun clearMessages(conversationId: String): Result<Unit> {
        return try {
            val querySnapshot = messagesCollection
                .whereEqualTo("conversationId", conversationId)
                .get()
                .await()
            
            val batch = firestore.batch()
            querySnapshot.documents.forEach { document ->
                batch.delete(messagesCollection.document(document.id))
            }
            
            batch.commit().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "清空消息失败")
        }
    }
} 