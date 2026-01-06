package com.nikunja.aquariusly.data.remote.api

import com.nikunja.aquariusly.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {

    @POST("auth/google")
    suspend fun signInWithGoogle(
        @Body request: GoogleSignInRequest
    ): Response<AuthResponse>

    @POST("auth/email")
    suspend fun signInWithEmail(
        @Body request: EmailSignInRequest
    ): Response<AuthResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<MessageResponse>

    @POST("auth/logout-all")
    suspend fun logoutAll(): Response<MessageResponse>

    @GET("auth/me")
    suspend fun getCurrentUser(): Response<AuthResponse>
}
