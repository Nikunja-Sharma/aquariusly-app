package com.nikunja.aquariusly.data.repository

import com.nikunja.aquariusly.data.remote.api.AuthApiService
import com.nikunja.aquariusly.data.remote.dto.GoogleSignInRequest
import com.nikunja.aquariusly.data.remote.dto.UserDto
import com.nikunja.aquariusly.data.remote.interceptor.CookieInterceptor
import com.nikunja.aquariusly.domain.model.User
import com.nikunja.aquariusly.domain.repository.AuthRepository
import com.nikunja.aquariusly.domain.util.Resource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val cookieInterceptor: CookieInterceptor
) : AuthRepository {

    private var cachedUser: User? = null

    override suspend fun signInWithGoogle(idToken: String): Resource<User> {
        return try {
            val response = authApiService.signInWithGoogle(GoogleSignInRequest(idToken))
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val user = body.user.toDomain()
                    cachedUser = user
                    Resource.Success(user)
                } else {
                    Resource.Error("Empty response")
                }
            } else {
                Resource.Error(response.message() ?: "Sign in failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    override suspend fun getCurrentUser(forceRefresh: Boolean): Resource<User> {
        // Return cached user if available and not forcing refresh
        if (!forceRefresh) {
            cachedUser?.let { return Resource.Success(it) }
        }

        return try {
            val response = authApiService.getCurrentUser()
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val user = body.user.toDomain()
                    cachedUser = user
                    Resource.Success(user)
                } else {
                    // Return cached user as fallback if API returns empty
                    cachedUser?.let { return Resource.Success(it) }
                    Resource.Error("Empty response")
                }
            } else {
                // Return cached user as fallback on API error
                cachedUser?.let { return Resource.Success(it) }
                Resource.Error(response.message() ?: "Failed to get user")
            }
        } catch (e: Exception) {
            // Return cached user as fallback on network error
            cachedUser?.let { return Resource.Success(it) }
            Resource.Error(e.message ?: "Network error")
        }
    }

    override suspend fun refreshSession(): Resource<User> {
        return try {
            val response = authApiService.refreshToken()
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val user = body.user.toDomain()
                    cachedUser = user
                    Resource.Success(user)
                } else {
                    Resource.Error("Empty response")
                }
            } else {
                clearSession()
                Resource.Error("Session expired")
            }
        } catch (e: Exception) {
            clearSession()
            Resource.Error(e.message ?: "Network error")
        }
    }

    override suspend fun signOut(): Resource<Unit> {
        return try {
            val response = authApiService.logout()
            clearSession()
            
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Success(Unit) // Still clear local session
            }
        } catch (e: Exception) {
            clearSession()
            Resource.Success(Unit) // Still clear local session
        }
    }

    override suspend fun signOutAll(): Resource<Unit> {
        return try {
            val response = authApiService.logoutAll()
            clearSession()
            
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Failed to logout from all devices")
            }
        } catch (e: Exception) {
            clearSession()
            Resource.Error(e.message ?: "Network error")
        }
    }

    override fun isUserLoggedIn(): Boolean {
        return cookieInterceptor.hasValidSession()
    }

    override fun clearSession() {
        cachedUser = null
        cookieInterceptor.clearCookies()
    }

    private fun UserDto.toDomain(): User {
        return User(
            id = id,
            email = email,
            displayName = name,
            photoUrl = picture
        )
    }
}
