package com.cshisan.feature.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cshisan.feature.chat.ChatScreen
import com.cshisan.feature.chat.ConversationListScreen
import com.cshisan.feature.chat.EnhancedChatScreen
import com.cshisan.feature.login.LoginScreen

/**
 * 应用程序导航路径
 */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Chat : Screen("chat/{conversationId}")
    data object EnhancedChat : Screen("enhanced_chat/{conversationId}")
    data object Login : Screen("login")

    companion object {
        fun chatRoute(conversationId: String): String = "chat/$conversationId"
        fun enhancedChatRoute(conversationId: String): String = "enhanced_chat/$conversationId"
    }
}

/**
 * 应用程序导航图
 *
 * @param navController 导航控制器
 * @param startDestination 起始导航路径
 * @param darkTheme 是否为深色主题
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route,
    darkTheme: Boolean = false,
) {
    val isDarkTheme by remember { mutableStateOf(darkTheme) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            ConversationListScreen(
                onConversationSelected = { conversationId ->
                    navController.navigate(Screen.enhancedChatRoute(conversationId))
                },
//                onCreateNewConversation = { conversationId ->
//                    navController.navigate(Screen.enhancedChatRoute(conversationId))
//                }
            )
        }

        // 登录界面
        composable(route = Screen.Login.route) {
            LoginScreen(onLoginSuccess = { navController.navigate(Screen.Login.route) })
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("conversationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            ChatScreen(
                conversationId = conversationId,
//                onBackPressed = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EnhancedChat.route,
            arguments = listOf(
                navArgument("conversationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            EnhancedChatScreen(
                conversationId = conversationId,
                onBackPressed = { navController.popBackStack() },
                isDarkTheme = isDarkTheme
            )
        }
    }
}
 