package com.cshisan.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cshisan.core.api.service.ChatService
import com.cshisan.core.model.ChatMessage
import com.cshisan.core.model.Conversation
import com.cshisan.core.model.Result
import com.cshisan.core.api.model.ChatRequest
import com.cshisan.core.api.model.ChatRequestMessage
import com.cshisan.core.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

/**
 * 聊天视图模型
 * 
 * @param chatRepository 聊天仓库
 * @param chatService 聊天服务
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val chatService: ChatService
) : ViewModel() {
    
    // 会话状态
    private val _conversationState = MutableStateFlow(ConversationState())
    val conversationState: StateFlow<ConversationState> = _conversationState.asStateFlow()
    
    // 消息状态
    private val _messagesState = MutableStateFlow(MessagesState())
    val messagesState: StateFlow<MessagesState> = _messagesState.asStateFlow()
    
    /**
     * 加载会话信息
     * 
     * @param conversationId 会话ID
     */
    fun loadConversation(conversationId: String) {
        viewModelScope.launch {
            _messagesState.value = _messagesState.value.copy(isLoading = true)
            
            when (val result = chatRepository.getConversation(conversationId)) {
                is Result.Success -> {
                    _conversationState.value = _conversationState.value.copy(
                        conversation = result.data,
                        isLoading = false,
                        error = null
                    )
                }
                is Result.Error -> {
                    _conversationState.value = _conversationState.value.copy(
                        isLoading = false,
                        error = result.errorMessage
                    )
                }
                is Result.Loading -> {
                    _conversationState.value = _conversationState.value.copy(isLoading = true)
                }
            }
            
            // 监听消息列表
            chatRepository.getMessages(conversationId)
                .onEach { messages ->
                    _messagesState.value = _messagesState.value.copy(
                        messages = messages,
                        isLoading = false
                    )
                }
                .launchIn(viewModelScope)
        }
    }
    
    /**
     * 发送消息
     * 
     * @param content 消息内容
     * @param conversationId 会话ID
     */
    fun sendMessage(content: String, conversationId: String) {
        viewModelScope.launch {
            _messagesState.value = _messagesState.value.copy(isLoading = true)
            
            // 保存用户消息
            val userMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                content = content,
                isFromUser = true,
                timestamp = LocalDateTime.now(),
                conversationId = conversationId
            )
            
            chatRepository.addMessage(userMessage)
            
            // 构建聊天历史
            val chatHistory = _messagesState.value.messages.map { message ->
                ChatRequestMessage(
                    role = if (message.isFromUser) 
                            ChatRequestMessage.ROLE_USER
                          else 
                            ChatRequestMessage.ROLE_ASSISTANT,
                    content = message.content
                )
            }
            
            // 添加当前用户消息
            val allMessages = chatHistory + ChatRequestMessage(
                role = ChatRequestMessage.ROLE_USER,
                content = content
            )
            
            // 发送请求
            val request = ChatRequest(
                messages = allMessages,
                modelId = "gpt-3.5-turbo" // 默认模型
            )
            
            when (val response = chatService.sendChatRequest(request)) {
                is Result.Success -> {
                    // 保存AI回复
                    val aiMessage = ChatMessage(
                        id = response.data.id,
                        content = response.data.message.content,
                        isFromUser = false,
                        timestamp = LocalDateTime.now(),
                        conversationId = conversationId
                    )
                    chatRepository.addMessage(aiMessage)
                    _messagesState.value = _messagesState.value.copy(isLoading = false)
                }
                is Result.Error -> {
                    // 创建错误消息
                    val errorMessage = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        content = "回复失败: ${response.errorMessage}",
                        isFromUser = false,
                        timestamp = LocalDateTime.now(),
                        conversationId = conversationId,
                        isError = true
                    )
                    chatRepository.addMessage(errorMessage)
                    _messagesState.value = _messagesState.value.copy(
                        isLoading = false,
                        error = response.errorMessage
                    )
                }
                is Result.Loading -> {
                    // 已在前面设置loading状态
                }
            }
        }
    }
    
    /**
     * 创建新会话
     * 
     * @param userId 用户ID
     * @param initialMessage 初始消息
     */
    fun createNewConversation(userId: String, initialMessage: String) {
        viewModelScope.launch {
            // 创建新会话
            val conversation = Conversation(
                id = UUID.randomUUID().toString(),
                title = initialMessage.take(20) + if (initialMessage.length > 20) "..." else "",
                userId = userId,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            
            when (val result = chatRepository.createConversation(conversation)) {
                is Result.Success -> {
                    val conversationId = result.data
                    // 发送初始消息
                    sendMessage(initialMessage, conversationId)
                    // 更新当前会话
                    _conversationState.value = _conversationState.value.copy(
                        conversation = conversation.copy(id = conversationId),
                        isLoading = false
                    )
                }
                is Result.Error -> {
                    _conversationState.value = _conversationState.value.copy(
                        isLoading = false,
                        error = result.errorMessage
                    )
                }
                is Result.Loading -> {
                    _conversationState.value = _conversationState.value.copy(isLoading = true)
                }
            }
        }
    }
}

/**
 * 会话状态
 * 
 * @param conversation 当前会话
 * @param isLoading 是否加载中
 * @param error 错误信息
 */
data class ConversationState(
    val conversation: Conversation? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * 消息状态
 * 
 * @param messages 消息列表
 * @param isLoading 是否加载中
 * @param error 错误信息
 */
data class MessagesState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) 