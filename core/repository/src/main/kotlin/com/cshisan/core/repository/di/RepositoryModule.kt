package com.cshisan.core.repository.di

import com.cshisan.core.repository.ChatRepository
import com.cshisan.core.repository.UserRepository
import com.cshisan.core.repository.impl.FirebaseChatRepository
import com.cshisan.core.repository.impl.FirebaseUserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.cshisan.core.repository.BuildConfig

/**
 * 提供仓库相关的依赖注入
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    /**
     * 提供Firebase认证服务
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    /**
     * 提供Firebase Firestore服务
     */
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore(BuildConfig.REALTIME_DATABASE_URL)
    }
    
    /**
     * 提供聊天仓库实现
     */
    @Provides
    @Singleton
    fun provideChatRepository(firestore: FirebaseFirestore): ChatRepository {
        return FirebaseChatRepository(firestore)
    }
    
    /**
     * 提供用户仓库实现
     */
    @Provides
    @Singleton
    fun provideUserRepository(auth: FirebaseAuth, firestore: FirebaseFirestore): UserRepository {
        return FirebaseUserRepository(auth, firestore)
    }
} 