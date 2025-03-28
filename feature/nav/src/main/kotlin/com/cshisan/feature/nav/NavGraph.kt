package com.cshisan.feature.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cshisan.feature.chat.ChatScreen
import com.cshisan.feature.chat.ConversationListScreen

/**
 * 应用导航图
 * 
 * @param navController 导航控制器
 * @param startDestination 起始目的地
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = NavDestination.CONVERSATION_LIST
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 会话列表
        composable(NavDestination.CONVERSATION_LIST) {
            ConversationListScreen(
                onConversationSelected = { conversationId ->
                    navController.navigate("${NavDestination.CHAT}/$conversationId")
                }
            )
        }
        
        // 聊天界面
        composable(
            route = "${NavDestination.CHAT}/{conversationId}",
            arguments = listOf(
                navArgument("conversationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            ChatScreen(conversationId = conversationId)
        }
        
        // 登录界面
        composable(NavDestination.LOGIN) {
            // LoginScreen(onLoginSuccess = { navController.navigate(NavDestination.CONVERSATION_LIST) })
        }
        
        // 用户设置界面
        composable(NavDestination.SETTINGS) {
            // SettingsScreen()
        }
    }
}

/**
 * 导航目的地
 */
object NavDestination {
    const val CONVERSATION_LIST = "conversation_list"
    const val CHAT = "chat"
    const val LOGIN = "login"
    const val SETTINGS = "settings"
} 