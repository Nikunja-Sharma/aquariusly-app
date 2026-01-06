package com.nikunja.aquariusly.data.remote.api

import com.nikunja.aquariusly.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ReferralApiService {

    @GET("referral/info")
    suspend fun getReferralInfo(): Response<ReferralInfoResponse>

    @GET("referral/history")
    suspend fun getReferralHistory(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ReferralHistoryResponse>

    @GET("referral/stats")
    suspend fun getReferralStats(): Response<ReferralStatsResponse>

    @POST("referral/apply")
    suspend fun applyReferralCode(
        @Body request: ApplyReferralRequest
    ): Response<ApplyReferralResponse>

    @POST("referral/claim")
    suspend fun claimReward(
        @Body request: ClaimReferralRewardRequest
    ): Response<ClaimRewardResponse>

    @POST("referral/track-install")
    suspend fun trackInstallReferrer(
        @Body request: TrackInstallRequest
    ): Response<ApplyReferralResponse>
}

data class TrackInstallRequest(
    val referrerUrl: String?,
    val referrerClickTime: Long?,
    val installBeginTime: Long?,
    val installVersion: String?,
    val googlePlayInstant: Boolean?
)
