package com.cshisan.feature.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cshisan.core.model.User

/**
 * 侧边导航抽屉
 *
 * @param drawerState 抽屉状态
 * @param currentUser 当前用户
 * @param onNavigateToChats 导航到聊天列表
 * @param onNavigateToSettings 导航到设置
 * @param onNavigateToHelp 导航到帮助
 * @param onLogout 登出回调
 * @param darkTheme 是否为深色主题
 * @param onThemeChanged 主题变更回调
 * @param content 内容
 */
@Composable
fun SideNavigationDrawer(
    drawerState: DrawerState,
    currentUser: User?,
    onNavigateToChats: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onLogout: () -> Unit,
    darkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(vertical = 24.dp)
            ) {
                // 用户信息
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 用户头像
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF6C63FF).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.username?.firstOrNull()?.toString() ?: "张",
                            color = Color(0xFF6C63FF),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = currentUser?.username ?: "张三",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                        Text(
                            text = currentUser?.email ?: "zhangsan@example.com",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
              HorizontalDivider()
                
                // 导航菜单项
                NavigationItem(
                    icon = Icons.Default.Info,
                    title = "聊天频道",
                    onClick = onNavigateToChats
                )
                
                NavigationItem(
                    icon = Icons.Default.Settings,
                    title = "设置",
                    onClick = onNavigateToSettings
                )
                
                NavigationItem(
                    icon = Icons.Default.Info,
                    title = "帮助与支持",
                    onClick = onNavigateToHelp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 深色模式开关
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "深色模式",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = "深色模式",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Switch(
                        checked = darkTheme,
                        onCheckedChange = onThemeChanged,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF6C63FF),
                            checkedTrackColor = Color(0xFF6C63FF).copy(alpha = 0.5f)
                        )
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))

              HorizontalDivider()
                
                // 登出按钮
                NavigationItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    title = "退出登录",
                    onClick = onLogout
                )
            }
        },
        content = content
    )
}

/**
 * 导航菜单项
 *
 * @param icon 图标
 * @param title 标题
 * @param onClick 点击回调
 */
@Composable
private fun NavigationItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = title,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
} 