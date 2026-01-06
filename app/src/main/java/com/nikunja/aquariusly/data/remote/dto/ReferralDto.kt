package com.nikunja.aquariusly.data.remote.dto

import com.google.gson.annotations.SerializedName

// Request DTOs
data class ApplyReferralRequest(
    @SerializedName("referral_code") val referralCode: String
)

data class ClaimReferralRewardRequest(
    @SerializedName("referral_id") val referralId: String
)

// Response DTOs
data class ReferralInfoResponse(
    @SerializedName("referralCode") val referralCode: String,
    @SerializedName("referralLink") val referralLink: String,
    @SerializedName("totalReferrals") val totalReferrals: Int,
    @SerializedName("successfulReferrals") val successfulReferrals: Int,
    @SerializedName("pendingRewards") val pendingRewards: Int,
    @SerializedName("claimedRewards") val claimedRewards: Int
)

data class ReferralReward(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String, // "free_tokens", "premium_model_access", "credits"
    @SerializedName("amount") val amount: Int,
    @SerializedName("description") val description: String,
    @SerializedName("model_id") val modelId: String? = null,
    @SerializedName("expires_at") val expiresAt: Long? = null,
    @SerializedName("claimed") val claimed: Boolean = false
)

data class ReferralHistoryItem(
    @SerializedName("id") val id: String,
    @SerializedName("referred_user_name") val referredUserName: String,
    @SerializedName("referred_user_avatar") val referredUserAvatar: String? = null,
    @SerializedName("status") val status: String, // "pending", "completed", "expired"
    @SerializedName("reward") val reward: ReferralReward? = null,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("completed_at") val completedAt: Long? = null
)

data class ReferralHistoryResponse(
    @SerializedName("referrals") val referrals: List<ReferralHistoryItem>,
    @SerializedName("total_count") val totalCount: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("has_more") val hasMore: Boolean
)

data class ApplyReferralResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("reward") val reward: ReferralReward? = null,
    @SerializedName("referrer_name") val referrerName: String? = null
)

data class ClaimRewardResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("reward") val reward: ReferralReward? = null
)

data class ReferralStatsResponse(
    @SerializedName("leaderboard_rank") val leaderboardRank: Int? = null,
    @SerializedName("this_month_referrals") val thisMonthReferrals: Int,
    @SerializedName("streak_days") val streakDays: Int,
    @SerializedName("next_milestone") val nextMilestone: ReferralMilestone? = null,
    @SerializedName("achievements") val achievements: List<ReferralAchievement>
)

data class ReferralMilestone(
    @SerializedName("target") val target: Int,
    @SerializedName("current") val current: Int,
    @SerializedName("reward") val reward: ReferralReward
)

data class ReferralAchievement(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("unlocked") val unlocked: Boolean,
    @SerializedName("unlocked_at") val unlockedAt: Long? = null
)
