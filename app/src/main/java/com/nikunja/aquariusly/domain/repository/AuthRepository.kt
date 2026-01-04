package com.nikunja.aquariusly.domain.repository

import com.nikunja.aquariusly.domain.model.User
import com.nikunja.aquariusly.domain.util.Resource

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): Resource<User>
    suspend fun getCurrentUser(forceRefresh: Boolean = false): Resource<User>
    suspend fun refreshSession(): Resource<User>
    suspend fun signOut(): Resource<Unit>
    suspend fun signOutAll(): Resource<Unit>
    fun isUserLoggedIn(): Boolean
    fun clearSession()
}
