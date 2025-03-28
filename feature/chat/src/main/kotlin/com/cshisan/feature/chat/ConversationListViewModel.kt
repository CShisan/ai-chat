package com.cshisan.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cshisan.core.model.Conversation
import com.cshisan.core.model.Result
import com.cshisan.core.repository.ChatRepository
import com.cshisan.core.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

/**
 * 会话列表视图模型
 * 
 * @param chatRepository 聊天仓库
 * @param userRepository 用户仓库
 */
@HiltViewModel
class ConversationListViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ConversationListUiState())
    val uiState: StateFlow<ConversationListUiState> = _uiState.asStateFlow()
    
    init {
        loadConversations()
    }
    
    /**
     * 加载会话列表
     */
    private fun loadConversations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // 获取当前用户
                val currentUser = userRepository.getCurrentUser().first()
                
                if (currentUser != null) {
                    // 监听用户会话列表
                    chatRepository.getConversations(currentUser.id)
                        .collect { conversations ->
                            _uiState.value = _uiState.value.copy(
                                conversations = conversations,
                                isLoading = false,
                                error = ""
                            )
                        }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "请先登录"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "加载会话失败"
                )
            }
        }
    }
    
    /**
     * 创建新会话
     * 
     * @param initialMessage 初始消息
     */
    fun createNewConversation(initialMessage: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // 获取当前用户
                val currentUser = userRepository.getCurrentUser().first()
                
                if (currentUser != null) {
                    // 创建新会话
                    val conversation = Conversation(
                        id = UUID.randomUUID().toString(),
                        title = initialMessage.take(20) + if (initialMessage.length > 20) "..." else "",
                        userId = currentUser.id,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                    
                    when (val result = chatRepository.createConversation(conversation)) {
                        is Result.Success -> {
                            // 发送初始消息给ChatViewModel处理
                            // 这里什么都不做，会话已创建并添加到列表中
                            _uiState.value = _uiState.value.copy(isLoading = false)
                        }
                        is Result.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = result.errorMessage
                            )
                        }
                        is Result.Loading -> {
                            // 已设置加载状态
                        }
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "请先登录"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "创建会话失败"
                )
            }
        }
    }
    
    /**
     * 删除会话
     * 
     * @param conversationId 会话ID
     */
    fun deleteConversation(conversationId: String) {
        viewModelScope.launch {
            when (val result = chatRepository.deleteConversation(conversationId)) {
                is Result.Success -> {
                    // 成功删除，会话列表会自动更新
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        error = result.errorMessage
                    )
                }
                is Result.Loading -> {
                    // 不处理
                }
            }
        }
    }
}

/**
 * 会话列表UI状态
 * 
 * @param conversations 会话列表
 * @param isLoading 是否加载中
 * @param error 错误信息
 */
data class ConversationListUiState(
    val conversations: List<Conversation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = ""
) 