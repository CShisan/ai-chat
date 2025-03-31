package com.cshisan.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cshisan.core.api.auth.AuthApi
import com.cshisan.core.api.auth.LoginRequest
import com.cshisan.core.api.auth.PhonePwLoginRequest
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
  private val authApi: AuthApi,
  private val userRepository: UserRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(LoginUiState())
  val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

  /**
   * 获取验证码
   */
  fun getCaptcha() {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(
        isLoadingCaptcha = true,
        errorMessage = null
      )
      
      when (val result = authApi.captcha()) {
        is Result.Success -> {
          _uiState.value = _uiState.value.copy(
            isLoadingCaptcha = false,
            captchaSign = result.data,
            errorMessage = null
          )
        }
        is Result.Error -> {
          _uiState.value = _uiState.value.copy(
            isLoadingCaptcha = false,
            errorMessage = "获取验证码失败: ${result.errorMessage}"
          )
        }
        is Result.Loading -> {
          _uiState.value = _uiState.value.copy(
            isLoadingCaptcha = true,
            errorMessage = null
          )
        }
      }
    }
  }

  /**
   * 登录
   *
   * @param account 账号
   * @param password 密码
   * @param onSuccess 登录成功回调
   */
  fun login(account: String, password: String, captchaCode: String, captchaSign: String, onSuccess: () -> Unit) {
    viewModelScope.launch {
      // 输入验证
      if (account.isBlank()) {
        _uiState.value = _uiState.value.copy(errorMessage = "账号不能为空")
        return@launch
      }

      if (password.isBlank()) {
        _uiState.value = _uiState.value.copy(errorMessage = "密码不能为空")
        return@launch
      }

      if (captchaCode.isBlank()) {
        _uiState.value = _uiState.value.copy(errorMessage = "验证码不能为空")
        return@launch
      }

      // 设置加载状态
      _uiState.value = _uiState.value.copy(
        isLoading = true,
        errorMessage = ""
      )

      // 登录
      val signToUse = captchaSign.ifBlank { _uiState.value.captchaSign }
      val loginRequest = PhonePwLoginRequest(account, password, captchaCode, signToUse)
      when (val result = authApi.login(loginRequest)) {
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

  /**
   * 注册
   *
   * @param account 账号
   * @param password 密码
   * @param onSuccess 注册成功回调
   */
  fun register(account: String, password: String, onSuccess: () -> Unit) {
    viewModelScope.launch {
      // 输入验证
      if (account.isBlank() || password.isBlank()) {
        _uiState.value = _uiState.value.copy(errorMessage = "所有字段不能为空")
        return@launch
      }

      // 设置加载状态
      _uiState.value = _uiState.value.copy(
        isLoading = true,
        errorMessage = ""
      )

      // 注册
      when (val result = userRepository.register(account, password)) {
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
 * @param isLoadingCaptcha 是否加载验证码中
 * @param captchaSign 验证码签名
 * @param errorMessage 错误信息
 */
data class LoginUiState(
  val isLoading: Boolean = false,
  val isLoadingCaptcha: Boolean = false,
  val captchaSign: String = "",
  val errorMessage: String? = null
) 