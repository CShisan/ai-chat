package com.cshisan.feature.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cshisan.core.model.AiModel
import com.cshisan.core.model.ChatMessage
import com.cshisan.feature.chat.components.ModelSelector
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * 增强版聊天界面
 *
 * @param conversationId 对话ID
 * @param onBackPressed 返回按钮点击回调
 * @param isDarkTheme 是否为深色主题
 * @param viewModel 聊天视图模型
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedChatScreen(
    conversationId: String,
    onBackPressed: () -> Unit,
    isDarkTheme: Boolean,
    viewModel: ChatViewModel = hiltViewModel()
) {
    LaunchedEffect(conversationId) {
        viewModel.loadConversation(conversationId)
    }
    
    val conversationState by viewModel.conversationState.collectAsState()
    val messagesState by viewModel.messagesState.collectAsState()
    
    var showMenu by remember { mutableStateOf(false) }
    var showModelSelector by remember { mutableStateOf(false) }
    var selectedModel by remember { 
        mutableStateOf(
            AiModel(
                id = "claude-3.7", 
                name = "Claude-3.7",
                description = "",
                apiEndpoint = ""
            )
        ) 
    }
    
    val availableModels = remember {
        listOf(
            AiModel(
                id = "claude-3.7", 
                name = "Claude-3.7",
                description = "",
                apiEndpoint = ""
            ),
            AiModel(
                id = "gpt-4o", 
                name = "GPT-4o",
                description = "",
                apiEndpoint = ""
            ),
            AiModel(
                id = "claude-3.5", 
                name = "Claude-3.5",
                description = "",
                apiEndpoint = ""
            ),
            AiModel(
                id = "gemini-1.5", 
                name = "Gemini-1.5",
                description = "",
                apiEndpoint = ""
            )
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = conversationState.conversation?.title ?: "AI助手",
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Text(
                            text = selectedModel.name,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "更多选项",
                            tint = Color.White
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("切换模型") },
                            onClick = { 
                                showMenu = false
                                showModelSelector = true
                            }
                        )
                        
                        DropdownMenuItem(
                            text = { Text("清空对话") },
                            onClick = { 
                                // 清空对话
                                showMenu = false
                            }
                        )
                        
                        DropdownMenuItem(
                            text = { Text("删除对话") },
                            onClick = { 
                                // 删除对话
                                showMenu = false
                                onBackPressed()
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 模型选择器
            AnimatedVisibility(
                visible = showModelSelector,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "选择模型",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            
                            IconButton(onClick = { showModelSelector = false }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "关闭"
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        ModelSelector(
                            selectedModel = selectedModel,
                            availableModels = availableModels,
                            onModelSelected = { model ->
                                selectedModel = model
                                showModelSelector = false
                            }
                        )
                    }
                }
            }
            
            // 消息列表
            Box(modifier = Modifier.weight(1f)) {
                if (messagesState.messages.isEmpty() && !messagesState.isLoading) {
                    EmptyChatState(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                } else {
                    EnhancedChatMessageList(
                        messages = messagesState.messages,
                        isLoading = messagesState.isLoading,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            // 输入区域
            EnhancedChatInputArea(
                onSendMessage = { message -> 
                    viewModel.sendMessage(message, conversationId)
                },
                isLoading = messagesState.isLoading
            )
        }
    }
}

/**
 * 增强版消息列表
 *
 * @param messages 消息列表
 * @param isLoading 是否加载中
 * @param modifier 修改器
 */
@Composable
private fun EnhancedChatMessageList(
    messages: List<ChatMessage>,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    
    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(messages) { message ->
                EnhancedChatMessageItem(message = message)
            }
            
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }
    }
}

/**
 * 增强版消息项
 *
 * @param message 消息
 * @param modifier 修改器
 */
@Composable
private fun EnhancedChatMessageItem(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val isFromUser = message.isFromUser
    val isError = message.isError
    val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    var expanded by remember { mutableStateOf(true) }
    
    // 长消息处理（超过500字符的消息默认折叠）
    val isLongMessage = message.content.length > 500
    val displayContent = if (isLongMessage && !expanded) {
        message.content.take(500) + "..."
    } else {
        message.content
    }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = if (isFromUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            if (!isFromUser) {
                // AI头像
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (isError) Color(0xFFE53935) else Color(0xFF4285F4)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isError) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "错误",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = "AI",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            // 消息气泡
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isFromUser) 16.dp else 4.dp,
                    bottomEnd = if (isFromUser) 4.dp else 16.dp
                ),
                color = when {
                    isError -> Color(0xFFFDEDED)
                    isFromUser -> MaterialTheme.colorScheme.primary
                    else -> if (MaterialTheme.colorScheme.isLight()) Color(0xFFF1F1F1) else Color(0xFF2A2A2A)
                },
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = displayContent,
                        color = when {
                            isError -> Color(0xFFE53935)
                            isFromUser -> Color.White
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                    
                    // 显示展开/折叠按钮（如果是长消息）
                    if (isLongMessage) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (expanded) "收起" else "展开查看更多",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { expanded = !expanded }
                        )
                    }
                }
            }
            
            if (isFromUser) {
                Spacer(modifier = Modifier.width(8.dp))
                // 用户头像
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF9E9E9E)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "U",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // 时间戳
        Text(
            text = message.timestamp.format(timeFormatter),
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(
                start = if (isFromUser) 0.dp else 40.dp,
                end = if (isFromUser) 40.dp else 0.dp
            )
        )
    }
}

/**
 * 增强版输入区域
 *
 * @param onSendMessage 发送消息回调
 * @param isLoading 是否加载中
 * @param modifier 修改器
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnhancedChatInputArea(
    onSendMessage: (String) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(24.dp)
                    ),
                placeholder = { Text("输入消息...") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp)
            )
            
            // 发送按钮
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText)
                        inputText = ""
                    }
                },
                enabled = !isLoading && inputText.isNotBlank(),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (!isLoading && inputText.isNotBlank()) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                Color.Gray
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "发送",
                    tint = Color.White
                )
            }
        }
    }
}

/**
 * 空聊天状态
 *
 * @param modifier 修改器
 */
@Composable
private fun EmptyChatState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 空状态图标
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "开始聊天",
                    modifier = Modifier.size(50.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "开始新对话",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "在下方输入框发送消息，开始与AI助手对话",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

/**
 * 错误状态
 *
 * @param errorMessage 错误信息
 * @param onRetry 重试回调
 * @param modifier 修改器
 */
@Composable
private fun ErrorState(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = Color(0xFFFDEDED),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "错误",
                tint = Color(0xFFE53935)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "网络错误",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE53935)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = errorMessage,
                    fontSize = 14.sp,
                    color = Color(0xFFE53935).copy(alpha = 0.8f)
                )
            }
            
            IconButton(onClick = onRetry) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "重试",
                    tint = Color(0xFFE53935)
                )
            }
        }
    }
}

/**
 * 检查主题是否为浅色
 */
@Composable
private fun androidx.compose.material3.ColorScheme.isLight(): Boolean {
    return MaterialTheme.colorScheme.background.luminance() > 0.5f
} 