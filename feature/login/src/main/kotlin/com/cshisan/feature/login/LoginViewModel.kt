package com.cshisan.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cshisan.core.model.Result
import com.cshisan.core.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 登录视图模型
 *
 * @param userRepository 用户仓库
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    /**
     * 登录
     *
     * @param email 邮箱
     * @param password 密码
     * @param onSuccess 登录成功回调
     */
    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            // 输入验证
            if (email.isBlank()) {
                _uiState.value = _uiState.value.copy(errorMessage = "邮箱不能为空")
                return@launch
            }
            
            if (password.isBlank()) {
                _uiState.value = _uiState.value.copy(errorMessage = "密码不能为空")
                return@launch
            }
            
            // 设置加载状态
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage =  ""
            )
            
            // 登录
            when (val result = userRepository.login(email, password)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage =  ""
                    )
                    onSuccess()
                }
                
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.errorMessage
                    )
                }
                
                is Result.Loading -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = true,
                        errorMessage =  ""
                    )
                }
            }
        }
    }
    
    /**
     * 注册
     *
     * @param email 邮箱
     * @param username 用户名
     * @param password 密码
     * @param onSuccess 注册成功回调
     */
    fun register(email: String, username: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            // 输入验证
            if (email.isBlank() || username.isBlank() || password.isBlank()) {
                _uiState.value = _uiState.value.copy(errorMessage = "所有字段不能为空")
                return@launch
            }
            
            // 设置加载状态
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage =  ""
            )
            
            // 注册
            when (val result = userRepository.register(email, username, password)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = ""
                    )
                    onSuccess()
                }
                
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.errorMessage
                    )
                }
                
                is Result.Loading -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = true,
                        errorMessage = ""
                    )
                }
            }
        }
    }
}

/**
 * 登录界面状态
 *
 * @param isLoading 是否加载中
 * @param errorMessage 错误信息
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = ""
) 