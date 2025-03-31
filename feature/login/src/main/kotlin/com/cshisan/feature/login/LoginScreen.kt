package com.cshisan.feature.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.core.graphics.drawable.toBitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

/**
 * 登录界面
 *
 * @param onLoginSuccess 登录成功回调
 * @param viewModel 登录视图模型
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
  onLoginSuccess: () -> Unit,
  viewModel: LoginViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()

  var account by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var captchaCode by remember { mutableStateOf("") }
  val captchaImage = remember { mutableStateOf<ImageBitmap?>(null) }
  
  // 当captchaSign变化时尝试解码图片
  LaunchedEffect(uiState.captchaSign) {
    captchaImage.value = if (uiState.captchaSign.isNotEmpty()) {
      try {
        val imageBytes = Base64.decode(uiState.captchaSign, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        bitmap?.asImageBitmap()
      } catch (e: Exception) {
        null
      }
    } else {
      null
    }
  }

  // 组件首次加载时请求验证码
  LaunchedEffect(key1 = Unit) {
    viewModel.getCaptcha()
  }

  // 处理返回到该界面时重新加载验证码
  DisposableEffect(key1 = Unit) {
    // 界面离开时的清理工作
    onDispose {
      // 空实现，仅为了在回到页面时触发LaunchedEffect
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(60.dp))

      // AI聊天助手图标
      Surface(
        modifier = Modifier
          .size(80.dp)
          .clip(CircleShape),
        color = Color(0xFF6C63FF).copy(alpha = 0.2f)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icons.Default.Menu
//                    Icon(
//                        painter = painterResource(id = Icons.Default.Menu),
//                        contentDescription = "AI助手图标",
//                        modifier = Modifier.size(40.dp),
//                        tint = Color(0xFF6C63FF)
//                    )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // 欢迎文本
      Text(
        text = "欢迎使用AI聊天助手",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )

      Text(
        text = "智能对话，随时随地",
        fontSize = 16.sp,
        color = Color.Gray,
        modifier = Modifier.padding(top = 8.dp)
      )

      Spacer(modifier = Modifier.height(40.dp))

      // 账号输入框
      OutlinedTextField(
        value = account,
        onValueChange = { account = it },
        label = { Text("账号") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        isError = uiState.errorMessage?.contains("账号") == true
      )

      Spacer(modifier = Modifier.height(16.dp))

      // 密码输入框
      OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("密码") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        isError = uiState.errorMessage?.contains("密码") == true
      )

      Spacer(modifier = Modifier.height(16.dp))

      // 验证码输入框和图片
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = captchaCode,
          onValueChange = { captchaCode = it },
          label = { Text("验证码") },
          modifier = Modifier.weight(1f),
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          isError = uiState.errorMessage?.contains("验证码") == true
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // 验证码图片或刷新按钮
        Box(
          modifier = Modifier
            .size(width = 120.dp, height = 48.dp),
          contentAlignment = Alignment.Center
        ) {
          if (uiState.isLoadingCaptcha) {
            CircularProgressIndicator(
              modifier = Modifier.size(24.dp),
              color = Color(0xFF6C63FF)
            )
          } else {
            // 验证码图片
            if (captchaImage.value != null) {
              Image(
                bitmap = captchaImage.value!!,
                contentDescription = "验证码图片",
                modifier = Modifier.fillMaxSize()
              )
            } else {
              // 如果解码失败或没有验证码，显示刷新按钮
              IconButton(onClick = { viewModel.getCaptcha() }) {
                Icon(
                  imageVector = Icons.Default.Refresh,
                  contentDescription = "刷新验证码",
                  tint = Color(0xFF6C63FF)
                )
              }
            }
          }
        }
      }

      // 错误信息
      uiState.errorMessage?.let { errorMessage ->
        Text(
          text = errorMessage,
          color = MaterialTheme.colorScheme.error,
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
        )
      }

      Spacer(modifier = Modifier.height(32.dp))

      // 登录按钮
      Button(
        onClick = {
          viewModel.login(account, password, captchaCode, uiState.captchaSign, onSuccess = onLoginSuccess)
        },
        enabled = !uiState.isLoading && !uiState.isLoadingCaptcha && account.isNotBlank() && password.isNotBlank() && captchaCode.isNotBlank(),
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF6C63FF)
        )
      ) {
        if (uiState.isLoading) {
          Text("登录中...")
        } else {
          Text("登录")
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 忘记密码
      TextButton(
        onClick = { /* 忘记密码逻辑 */ },
        modifier = Modifier.align(Alignment.CenterHorizontally)
      ) {
        Text("忘记密码?", color = Color(0xFF6C63FF))
      }
    }

    // 底部注册提示
    Row(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("没有账号?", color = Color.Gray)
      TextButton(onClick = { /* 注册逻辑 */ }) {
        Text("立即注册", color = Color(0xFF6C63FF))
      }
    }
  }
} 