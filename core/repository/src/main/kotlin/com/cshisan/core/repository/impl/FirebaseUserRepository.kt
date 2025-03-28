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
 *
 * @param auth Firebase认证实例
 * @param firestore Firestore实例
 */
@Singleton
class FirebaseUserRepository @Inject constructor(
  private val auth: FirebaseAuth,
  private val firestore: FirebaseFirestore
) : UserRepository {

  private val usersCollection = firestore.collection("users")

  override fun getCurrentUser(): Flow<User?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
      val firebaseUser = auth.currentUser
      if (firebaseUser != null) {
        // 从Firestore获取用户详细信息
        usersCollection.document(firebaseUser.uid).get()
          .addOnSuccessListener { document ->
            if (document.exists()) {
              val id = document.id
              val username = document.getString("username") ?: ""
              val email = document.getString("email") ?: ""
              val profilePictureUrl = document.getString("profilePictureUrl") ?: ""
              val createdAtTimestamp = document.getTimestamp("createdAt")

              val createdAt = createdAtTimestamp?.toDate()?.toInstant()
                ?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
                ?: LocalDateTime.now()

              val user = User(id, username, email, profilePictureUrl, createdAt)
              trySend(user)
            } else {
              // 用户在Auth中存在但在Firestore中不存在
              trySend(null)
            }
          }
          .addOnFailureListener {
            trySend(null)
          }
      } else {
        trySend(null)
      }
    }

    auth.addAuthStateListener(listener)
    awaitClose { auth.removeAuthStateListener(listener) }
  }

  override suspend fun getUser(userId: String): Result<User> {
    return try {
      val document = usersCollection.document(userId).get().await()

      if (document.exists()) {
        val id = document.id
        val username = document.getString("username") ?: ""
        val email = document.getString("email") ?: ""
        val profilePictureUrl = document.getString("profilePictureUrl") ?: ""
        val createdAtTimestamp = document.getTimestamp("createdAt")

        val createdAt = createdAtTimestamp?.toDate()?.toInstant()
          ?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
          ?: LocalDateTime.now()

        Result.Success(User(id, username, email, profilePictureUrl, createdAt))
      } else {
        Result.Error("用户不存在")
      }
    } catch (e: Exception) {
      Result.Error(e.message ?: "获取用户失败")
    }
  }

  override suspend fun register(email: String, username: String, password: String): Result<User> {
    return try {
      // 创建Firebase Auth用户
      val authResult = auth.createUserWithEmailAndPassword(email, password).await()
      val firebaseUser = authResult.user

      if (firebaseUser != null) {
        val userId = firebaseUser.uid
        val createdAt = LocalDateTime.now()

        // 将用户信息保存到Firestore
        val userData = hashMapOf(
          "username" to username,
          "email" to email,
          "profilePictureUrl" to "",
          "createdAt" to Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant())
        )

        usersCollection.document(userId).set(userData).await()

        Result.Success(User(userId, username, email, "", createdAt))
      } else {
        Result.Error("用户注册失败")
      }
    } catch (e: Exception) {
      Result.Error(e.message ?: "注册失败")
    }
  }

  override suspend fun login(email: String, password: String): Result<User> {
    return try {
      val authResult = auth.signInWithEmailAndPassword(email, password).await()
      val firebaseUser = authResult.user

      if (firebaseUser != null) {
        val userId = firebaseUser.uid
        val document = usersCollection.document(userId).get().await()

        if (document.exists()) {
          val username = document.getString("username") ?: ""
          val userEmail = document.getString("email") ?: ""
          val profilePictureUrl = document.getString("profilePictureUrl") ?: ""
          val createdAtTimestamp = document.getTimestamp("createdAt")

          val createdAt = createdAtTimestamp?.toDate()?.toInstant()
            ?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
            ?: LocalDateTime.now()

          Result.Success(User(userId, username, userEmail, profilePictureUrl, createdAt))
        } else {
          Result.Error("用户信息不存在")
        }
      } else {
        Result.Error("登录失败")
      }
    } catch (e: Exception) {
      Result.Error(e.message ?: "登录失败")
    }
  }

  override suspend fun logout(): Result<Unit> {
    return try {
      auth.signOut()
      Result.Success(Unit)
    } catch (e: Exception) {
      Result.Error(e.message ?: "登出失败")
    }
  }

  override suspend fun updateUserProfile(user: User): Result<User> {
    return try {
      val currentUser = auth.currentUser

      if (currentUser != null && currentUser.uid == user.id) {
        val data = hashMapOf<String, Any>()

        // 只更新允许的字段
        data["username"] = user.username
        data["profilePictureUrl"] = user.profilePictureUrl

        usersCollection.document(user.id).update(data).await()

        // 如果邮箱变更，需要更新Auth中的邮箱
        if (currentUser.email != user.email) {
          currentUser.verifyBeforeUpdateEmail(user.email).await()
          // 同时更新Firestore中的邮箱
          usersCollection.document(user.id).update("email", user.email).await()
        }

        Result.Success(user)
      } else {
        Result.Error("没有权限更新用户信息")
      }
    } catch (e: Exception) {
      Result.Error(e.message ?: "更新用户信息失败")
    }
  }
} 