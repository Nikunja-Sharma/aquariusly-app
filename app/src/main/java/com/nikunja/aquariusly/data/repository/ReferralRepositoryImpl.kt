package com.nikunja.aquariusly.data.repository

import android.content.SharedPreferences
import com.nikunja.aquariusly.data.remote.api.ReferralApiService
import com.nikunja.aquariusly.data.remote.api.TrackInstallRequest
import com.nikunja.aquariusly.data.remote.dto.ApplyReferralRequest
import com.nikunja.aquariusly.data.remote.dto.ClaimReferralRewardRequest
import com.nikunja.aquariusly.domain.model.*
import com.nikunja.aquariusly.domain.repository.ApplyReferralResult
import com.nikunja.aquariusly.domain.repository.ReferralRepository
import com.nikunja.aquariusly.domain.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReferralRepositoryImpl @Inject constructor(
    private val apiService: ReferralApiService,
    private val prefs: SharedPreferences
) : ReferralRepository {

    companion object {
        private const val KEY_REFERRAL_APPLIED = "referral_code_applied"
        private const val KEY_PENDING_REFERRAL = "pending_referral_code"
    }

    override suspend fun getReferralInfo(): Resource<ReferralInfo> {
        return try {
            val response = apiService.getReferralInfo()
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                Resource.Success(
                    ReferralInfo(
                        referralCode = dto.referralCode,
                        referralLink = dto.referralLink,
                        totalReferrals = dto.totalReferrals,
                        successfulReferrals = dto.successfulReferrals,
                        pendingRewards = dto.pendingRewards,
                        claimedRewards = dto.claimedRewards
                    )
                )
            } else {
                Resource.Error(response.message() ?: "Failed to get referral info")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun getReferralHistory(page: Int): Resource<List<Referral>> {
        return try {
            val response = apiService.getReferralHistory(page)
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                Resource.Success(
                    dto.referrals.map { item ->
                        Referral(
                            id = item.id,
                            referredUserName = item.referredUserName,
                            referredUserAvatar = item.referredUserAvatar,
                            status = ReferralStatus.fromString(item.status),
                            reward = item.reward?.let { r ->
                                Reward(
                                    id = r.id,
                                    type = RewardType.fromString(r.type),
                                    amount = r.amount,
                                    description = r.description,
                                    modelId = r.modelId,
                                    expiresAt = r.expiresAt,
                                    claimed = r.claimed
                                )
                            },
                            createdAt = item.createdAt,
                            completedAt = item.completedAt
                        )
                    }
                )
            } else {
                Resource.Error(response.message() ?: "Failed to get referral history")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun getReferralStats(): Resource<ReferralStats> {
        return try {
            val response = apiService.getReferralStats()
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                Resource.Success(
                    ReferralStats(
                        leaderboardRank = dto.leaderboardRank,
                        thisMonthReferrals = dto.thisMonthReferrals,
                        streakDays = dto.streakDays,
                        nextMilestone = dto.nextMilestone?.let { m ->
                            Milestone(
                                target = m.target,
                                current = m.current,
                                reward = Reward(
                                    id = m.reward.id,
                                    type = RewardType.fromString(m.reward.type),
                                    amount = m.reward.amount,
                                    description = m.reward.description,
                                    modelId = m.reward.modelId,
                                    expiresAt = m.reward.expiresAt,
                                    claimed = m.reward.claimed
                                )
                            )
                        },
                        achievements = dto.achievements.map { a ->
                            Achievement(
                                id = a.id,
                                title = a.title,
                                description = a.description,
                                icon = a.icon,
                                unlocked = a.unlocked,
                                unlockedAt = a.unlockedAt
                            )
                        }
                    )
                )
            } else {
                Resource.Error(response.message() ?: "Failed to get referral stats")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun applyReferralCode(code: String): Resource<ApplyReferralResult> {
        return try {
            val response = apiService.applyReferralCode(ApplyReferralRequest(code))
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                if (dto.success) {
                    setReferralCodeApplied(true)
                    setPendingReferralCode(null)
                }
                Resource.Success(
                    ApplyReferralResult(
                        success = dto.success,
                        message = dto.message,
                        reward = dto.reward?.let { r ->
                            Reward(
                                id = r.id,
                                type = RewardType.fromString(r.type),
                                amount = r.amount,
                                description = r.description,
                                modelId = r.modelId,
                                expiresAt = r.expiresAt,
                                claimed = r.claimed
                            )
                        },
                        referrerName = dto.referrerName
                    )
                )
            } else {
                Resource.Error(response.message() ?: "Failed to apply referral code")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun claimReward(referralId: String): Resource<Reward> {
        return try {
            val response = apiService.claimReward(ClaimReferralRewardRequest(referralId))
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                if (dto.success && dto.reward != null) {
                    Resource.Success(
                        Reward(
                            id = dto.reward.id,
                            type = RewardType.fromString(dto.reward.type),
                            amount = dto.reward.amount,
                            description = dto.reward.description,
                            modelId = dto.reward.modelId,
                            expiresAt = dto.reward.expiresAt,
                            claimed = true
                        )
                    )
                } else {
                    Resource.Error(dto.message)
                }
            } else {
                Resource.Error(response.message() ?: "Failed to claim reward")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun trackInstallReferrer(
        referrerUrl: String?,
        referrerClickTime: Long?,
        installBeginTime: Long?,
        installVersion: String?,
        googlePlayInstant: Boolean?
    ): Resource<ApplyReferralResult> {
        return try {
            val response = apiService.trackInstallReferrer(
                TrackInstallRequest(
                    referrerUrl = referrerUrl,
                    referrerClickTime = referrerClickTime,
                    installBeginTime = installBeginTime,
                    installVersion = installVersion,
                    googlePlayInstant = googlePlayInstant
                )
            )
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                if (dto.success) {
                    setReferralCodeApplied(true)
                }
                Resource.Success(
                    ApplyReferralResult(
                        success = dto.success,
                        message = dto.message,
                        reward = dto.reward?.let { r ->
                            Reward(
                                id = r.id,
                                type = RewardType.fromString(r.type),
                                amount = r.amount,
                                description = r.description,
                                modelId = r.modelId,
                                expiresAt = r.expiresAt,
                                claimed = r.claimed
                            )
                        },
                        referrerName = dto.referrerName
                    )
                )
            } else {
                Resource.Error(response.message() ?: "Failed to track install referrer")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override fun hasAppliedReferralCode(): Boolean {
        return prefs.getBoolean(KEY_REFERRAL_APPLIED, false)
    }

    override fun setReferralCodeApplied(applied: Boolean) {
        prefs.edit().putBoolean(KEY_REFERRAL_APPLIED, applied).apply()
    }

    override fun getPendingReferralCode(): String? {
        return prefs.getString(KEY_PENDING_REFERRAL, null)
    }

    override fun setPendingReferralCode(code: String?) {
        if (code != null) {
            prefs.edit().putString(KEY_PENDING_REFERRAL, code).apply()
        } else {
            prefs.edit().remove(KEY_PENDING_REFERRAL).apply()
        }
    }
}
