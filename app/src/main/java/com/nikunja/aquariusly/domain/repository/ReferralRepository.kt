package com.nikunja.aquariusly.domain.repository

import com.nikunja.aquariusly.domain.model.*
import com.nikunja.aquariusly.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ReferralRepository {
    suspend fun getReferralInfo(): Resource<ReferralInfo>
    suspend fun getReferralHistory(page: Int = 1): Resource<List<Referral>>
    suspend fun getReferralStats(): Resource<ReferralStats>
    suspend fun applyReferralCode(code: String): Resource<ApplyReferralResult>
    suspend fun claimReward(referralId: String): Resource<Reward>
    suspend fun trackInstallReferrer(
        referrerUrl: String?,
        referrerClickTime: Long?,
        installBeginTime: Long?,
        installVersion: String?,
        googlePlayInstant: Boolean?
    ): Resource<ApplyReferralResult>
    
    // Local storage
    fun hasAppliedReferralCode(): Boolean
    fun setReferralCodeApplied(applied: Boolean)
    fun getPendingReferralCode(): String?
    fun setPendingReferralCode(code: String?)
}

data class ApplyReferralResult(
    val success: Boolean,
    val message: String,
    val reward: Reward?,
    val referrerName: String?
)
