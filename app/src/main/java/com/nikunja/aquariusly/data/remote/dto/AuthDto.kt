package com.nikunja.aquariusly.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GoogleSignInRequest(
    @SerializedName("idToken")
    val idToken: String
)

data class AuthResponse(
    @SerializedName("user")
    val user: UserDto
)

data class UserDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("picture")
    val picture: String?
)

data class UserProfileResponse(
    @SerializedName("profile")
    val profile: UserProfileDto
)

data class UserProfileDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("picture")
    val picture: String?,
    @SerializedName("bio")
    val bio: String?,
    @SerializedName("preferences")
    val preferences: UserPreferencesDto
)

data class UserPreferencesDto(
    @SerializedName("theme")
    val theme: String,
    @SerializedName("notifications")
    val notifications: Boolean,
    @SerializedName("language")
    val language: String,
    @SerializedName("preferredModels")
    val preferredModels: List<String> = emptyList(),
    @SerializedName("defaultModel")
    val defaultModel: String? = null
)

data class UpdateProfileRequest(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("bio")
    val bio: String? = null
)

data class UpdatePreferencesRequest(
    @SerializedName("theme")
    val theme: String? = null,
    @SerializedName("notifications")
    val notifications: Boolean? = null,
    @SerializedName("language")
    val language: String? = null,
    @SerializedName("preferredModels")
    val preferredModels: List<String>? = null,
    @SerializedName("defaultModel")
    val defaultModel: String? = null
)

data class PreferencesResponse(
    @SerializedName("preferences")
    val preferences: UserPreferencesDto
)

data class MessageResponse(
    @SerializedName("message")
    val message: String
)

data class ErrorResponse(
    @SerializedName("error")
    val error: String
)
