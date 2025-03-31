package com.cshisan.core.repository.impl

import com.cshisan.core.model.Result
import com.cshisan.core.model.User
import com.cshisan.core.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 基于Firebase实现的用户仓库
 * @param firestore Firestore实例
 */
@Singleton
class FirebaseUserRepository @Inject constructor(
  private val firestore: FirebaseFirestore
) : UserRepository {

  private val usersCollection = firestore.collection("users")

  override fun getCurrentUser(): Flow<User?> = callbackFlow {
    trySend(User("9527", "9527", "", "", LocalDateTime.now()))
  }

  override suspend fun getUser(userId: String): Result<User> {
    return Result.Success(User("9527", "9527", "", "", LocalDateTime.now()))
  }

  override suspend fun register(email: String, password: String): Result<User> {
    return Result.Success(User("9527", "9527", email, "", LocalDateTime.now()))
  }

  override suspend fun login(email: String, password: String): Result<User> {
    return Result.Success(User("9527", "9527", email, "", LocalDateTime.now()))
  }

  override suspend fun logout(): Result<Unit> {
    return Result.Success(Unit)
  }

  override suspend fun updateUserProfile(user: User): Result<User> {
    return Result.Success(User("9527", "9527", "", "", LocalDateTime.now()))
  }
} 