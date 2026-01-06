package com.nikunja.aquariusly

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.nikunja.aquariusly.data.referral.InstallReferrerManager
import com.nikunja.aquariusly.domain.repository.ReferralRepository
import com.nikunja.aquariusly.ui.navigation.NavGraph
import com.nikunja.aquariusly.ui.theme.AquariuslyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
        private const val PREFS_NAME = "aquariusly_prefs"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_INSTALL_REFERRER_CHECKED = "install_referrer_checked"
    }

    @Inject
    lateinit var installReferrerManager: InstallReferrerManager
    
    @Inject
    lateinit var referralRepository: ReferralRepository

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Permission result handled silently */ }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        
        // Keep splash screen visible while loading initial data
        var keepSplashVisible = true
        splashScreen.setKeepOnScreenCondition { keepSplashVisible }
        
        enableEdgeToEdge()
        requestNotificationPermission()
        
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val hasCompletedOnboarding = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        
        // Handle deep link
        val deepLinkChatId = handleDeepLink(intent)
        val referralCode = handleReferralDeepLink(intent)
        
        // Check install referrer (only once per install)
        checkInstallReferrer(prefs)
        
        // Store referral code if present
        referralCode?.let { code ->
            referralRepository.setPendingReferralCode(code)
        }
        
        setContent {
            var onboardingCompleted by remember { mutableStateOf(hasCompletedOnboarding) }
            var initialChatId by remember { mutableStateOf(deepLinkChatId) }
            var pendingReferralCode by remember { mutableStateOf(referralCode) }
            
            // Dismiss splash after 5 seconds (for debugging - remove later)
            LaunchedEffect(Unit) {
                // kotlinx.coroutines.delay(5000L)
                keepSplashVisible = false
            }
            
            AquariuslyTheme {
                val navController = rememberNavController()
                
                NavGraph(
                    navController = navController,
                    hasCompletedOnboarding = onboardingCompleted,
                    initialChatId = initialChatId,
                    pendingReferralCode = pendingReferralCode,
                    onReferralCodeConsumed = { pendingReferralCode = null }
                )
                
                // Clear initial chat id after first composition
                LaunchedEffect(Unit) {
                    initialChatId = null
                }
                
                // Save onboarding state when navigation changes
                LaunchedEffect(navController) {
                    navController.addOnDestinationChangedListener { _, destination, _ ->
                        if (destination.route != "onboarding" && !onboardingCompleted) {
                            prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply()
                            onboardingCompleted = true
                        }
                    }
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    
    private fun checkInstallReferrer(prefs: android.content.SharedPreferences) {
        val alreadyChecked = prefs.getBoolean(KEY_INSTALL_REFERRER_CHECKED, false)
        if (alreadyChecked) return
        
        lifecycleScope.launch {
            try {
                val referrerInfo = installReferrerManager.getInstallReferrer()
                if (referrerInfo != null) {
                    Log.d(TAG, "Install referrer URL: ${referrerInfo.referrerUrl}")
                    
                    // Parse referral code from the referrer URL
                    val referralCode = installReferrerManager.parseReferralCode(referrerInfo.referrerUrl)
                    if (referralCode != null) {
                        Log.d(TAG, "Found referral code: $referralCode")
                        referralRepository.setPendingReferralCode(referralCode)
                        
                        // Track the install with backend
                        referralRepository.trackInstallReferrer(
                            referrerUrl = referrerInfo.referrerUrl,
                            referrerClickTime = referrerInfo.referrerClickTimestamp,
                            installBeginTime = referrerInfo.installBeginTimestamp,
                            installVersion = referrerInfo.installVersion,
                            googlePlayInstant = referrerInfo.googlePlayInstant
                        )
                    }
                }
                
                // Mark as checked
                prefs.edit().putBoolean(KEY_INSTALL_REFERRER_CHECKED, true).apply()
            } catch (e: Exception) {
                Log.e(TAG, "Error checking install referrer", e)
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle deep link when app is already running
        handleDeepLink(intent)?.let { chatId ->
            // You can use a shared ViewModel or event bus to notify the chat screen
            // For now, we'll recreate the activity
            setIntent(intent)
            recreate()
        }
        
        // Handle referral deep link
        handleReferralDeepLink(intent)?.let { code ->
            referralRepository.setPendingReferralCode(code)
        }
    }
    
    private fun handleDeepLink(intent: Intent?): String? {
        intent?.data?.let { uri ->
            // Handle aquariusly://chat/{chatId}
            if (uri.scheme == "aquariusly" && uri.host == "chat") {
                return uri.lastPathSegment
            }
            // Handle https://ai.isdev.in/chat/{chatId}
            if (uri.scheme == "https" && uri.host == "ai.isdev.in" && uri.path?.startsWith("/chat/") == true) {
                return uri.lastPathSegment
            }
        }
        return null
    }
    
    private fun handleReferralDeepLink(intent: Intent?): String? {
        intent?.data?.let { uri ->
            // Handle aquariusly://referral/{code}
            if (uri.scheme == "aquariusly" && uri.host == "referral") {
                return uri.lastPathSegment
            }
            // Handle https://ai.isdev.in/referral/{code}
            if (uri.scheme == "https" && uri.host == "ai.isdev.in" && uri.path?.startsWith("/referral/") == true) {
                return uri.lastPathSegment
            }
            // Handle https://ai.isdev.in/invite/{code}
            if (uri.scheme == "https" && uri.host == "ai.isdev.in" && uri.path?.startsWith("/invite/") == true) {
                return uri.lastPathSegment
            }
            // Handle query parameter: ?ref=CODE
            uri.getQueryParameter("ref")?.let { return it }
        }
        return null
    }
}
