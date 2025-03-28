package com.cshisan.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 浅色主题配色
private val LightColors = lightColorScheme(
    primary = Color(0xFF4285F4),
    onPrimary = Color.White,
    secondary = Color(0xFF34A853),
    onSecondary = Color.White,
    tertiary = Color(0xFFEA4335),
    onTertiary = Color.White,
    background = Color.White,
    onBackground = Color(0xFF202124),
    surface = Color(0xFFF8F9FA),
    onSurface = Color(0xFF202124),
    error = Color(0xFFF44336),
    onError = Color.White
)

// 深色主题配色
private val DarkColors = darkColorScheme(
    primary = Color(0xFF669DF6),
    onPrimary = Color.Black,
    secondary = Color(0xFF46BF62),
    onSecondary = Color.Black,
    tertiary = Color(0xFFEF6A5A),
    onTertiary = Color.Black,
    background = Color(0xFF202124),
    onBackground = Color(0xFFE8EAED),
    surface = Color(0xFF303134),
    onSurface = Color(0xFFE8EAED),
    error = Color(0xFFFF7769),
    onError = Color.Black
)

/**
 * AI聊天应用的主题
 * 
 * @param darkTheme 是否为深色主题
 * @param dynamicColor 是否使用动态主题（Android 12+特性）
 * @param content 内容
 */
@Composable
fun AiChatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            
            // 使UI内容延伸到系统栏区域
            WindowCompat.setDecorFitsSystemWindows(window, false)
            
            // 使用WindowCompat设置系统栏外观，避免直接设置颜色
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
} 