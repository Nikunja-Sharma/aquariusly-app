package com.nikunja.aquariusly.domain.model

data class ReferralInfo(
    val referralCode: String,
    val referralLink: String,
    val totalReferrals: Int,
    val successfulReferrals: Int,
    val pendingRewards: Int,
    val claimedRewards: Int
) {
    val totalRewardsEarned: Int get() = pendingRewards + claimedRewards
}

data class Referral(
    val id: String,
    val referredUserName: String,
    val referredUserAvatar: String?,
    val status: ReferralStatus,
    val reward: Reward?,
    val createdAt: Long,
    val completedAt: Long?
)

enum class ReferralStatus {
    PENDING,
    COMPLETED,
    EXPIRED;
    
    companion object {
        fun fromString(value: String): ReferralStatus {
            return when (value.lowercase()) {
                "pending" -> PENDING
                "completed" -> COMPLETED
                "expired" -> EXPIRED
                else -> PENDING
            }
        }
    }
}

data class Reward(
    val id: String,
    val type: RewardType,
    val amount: Int,
    val description: String,
    val modelId: String?,
    val expiresAt: Long?,
    val claimed: Boolean
)

enum class RewardType {
    FREE_TOKENS,
    PREMIUM_MODEL_ACCESS,
    CREDITS;
    
    companion object {
        fun fromString(value: String): RewardType {
            return when (value.lowercase()) {
                "free_tokens" -> FREE_TOKENS
                "premium_model_access" -> PREMIUM_MODEL_ACCESS
                "credits" -> CREDITS
                else -> FREE_TOKENS
            }
        }
    }
}

data class ReferralStats(
    val leaderboardRank: Int?,
    val thisMonthReferrals: Int,
    val streakDays: Int,
    val nextMilestone: Milestone?,
    val achievements: List<Achievement>
)

data class Milestone(
    val target: Int,
    val current: Int,
    val reward: Reward
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val unlocked: Boolean,
    val unlockedAt: Long?
)
