package com.cshisan.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.cshisan.app.ui.navigation.NavGraph
import com.cshisan.app.ui.theme.AiChatTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * 应用程序主Activity
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val darkTheme = isSystemInDarkTheme()
      var isDarkTheme by remember { mutableStateOf(darkTheme) }

      AiChatApp(
        isDarkTheme = isDarkTheme,
        onThemeChanged = { isDarkTheme = it }
      )
    }
  }
}

@Composable
fun AiChatApp(
  isDarkTheme: Boolean,
  onThemeChanged: (Boolean) -> Unit
) {
  AiChatTheme(darkTheme = isDarkTheme) {
    Surface(
      modifier = Modifier.fillMaxSize(),
      color = MaterialTheme.colorScheme.background
    ) {
      val navController = rememberNavController()
      NavGraph(
        navController = navController,
        darkTheme = isDarkTheme
      )
    }
  }
} 