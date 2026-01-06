package com.nikunja.aquariusly.data.referral

import android.content.Context
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class InstallReferrerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "InstallReferrerManager"
    }

    data class ReferrerInfo(
        val referrerUrl: String?,
        val referrerClickTimestamp: Long,
        val installBeginTimestamp: Long,
        val installVersion: String?,
        val googlePlayInstant: Boolean
    )

    suspend fun getInstallReferrer(): ReferrerInfo? = suspendCancellableCoroutine { continuation ->
        val referrerClient = InstallReferrerClient.newBuilder(context).build()
        
        referrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        try {
                            val response: ReferrerDetails = referrerClient.installReferrer
                            val referrerInfo = ReferrerInfo(
                                referrerUrl = response.installReferrer,
                                referrerClickTimestamp = response.referrerClickTimestampSeconds,
                                installBeginTimestamp = response.installBeginTimestampSeconds,
                                installVersion = response.installVersion,
                                googlePlayInstant = response.googlePlayInstantParam
                            )
                            Log.d(TAG, "Install referrer: ${referrerInfo.referrerUrl}")
                            continuation.resume(referrerInfo)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error getting referrer details", e)
                            continuation.resume(null)
                        } finally {
                            referrerClient.endConnection()
                        }
                    }
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                        Log.w(TAG, "Install referrer not supported")
                        continuation.resume(null)
                        referrerClient.endConnection()
                    }
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                        Log.w(TAG, "Install referrer service unavailable")
                        continuation.resume(null)
                        referrerClient.endConnection()
                    }
                    else -> {
                        Log.w(TAG, "Unknown response code: $responseCode")
                        continuation.resume(null)
                        referrerClient.endConnection()
                    }
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                Log.w(TAG, "Install referrer service disconnected")
            }
        })

        continuation.invokeOnCancellation {
            referrerClient.endConnection()
        }
    }

    /**
     * Parse referral code from the referrer URL
     * Expected format: utm_source=aquariusly&utm_medium=referral&utm_campaign=REF123ABC
     */
    fun parseReferralCode(referrerUrl: String?): String? {
        if (referrerUrl.isNullOrBlank()) return null
        
        return try {
            val params = referrerUrl.split("&").associate { param ->
                val parts = param.split("=")
                if (parts.size == 2) parts[0] to parts[1] else "" to ""
            }
            
            // Check if it's a referral from our app
            if (params["utm_source"] == "aquariusly" && params["utm_medium"] == "referral") {
                params["utm_campaign"]
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing referral code", e)
            null
        }
    }
}
