package com.cshisan.core.api.di

import com.cshisan.core.api.KtorClient
import com.cshisan.core.api.service.ChatService
import com.cshisan.core.api.service.ChatServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 网络相关的依赖注入模块
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
  /**
   * 提供聊天服务
   */
  @Provides
  @Singleton
  fun provideChatService(
    client: KtorClient
  ): ChatService {
    return ChatServiceImpl(client)
  }
} 