package com.cshisan.core.api.di

import com.cshisan.core.api.KtorClient
import com.cshisan.core.api.auth.AuthApi
import com.cshisan.core.api.auth.AuthApiImpl
import com.cshisan.core.api.chat.ChatApi
import com.cshisan.core.api.chat.ChatApiImpl
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
  @Provides
  @Singleton
  fun provideAuthApi(
    client: KtorClient
  ): AuthApi {
    return AuthApiImpl(client)
  }

  @Provides
  @Singleton
  fun provideChatApi(
    client: KtorClient
  ): ChatApi {
    return ChatApiImpl(client)
  }
} 