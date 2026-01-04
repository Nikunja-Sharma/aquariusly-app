package com.nikunja.aquariusly.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nikunja.aquariusly.ui.screens.chat.ChatScreen
import com.nikunja.aquariusly.ui.screens.home.HomeScreen
import com.nikunja.aquariusly.ui.screens.login.LoginScreen
import com.nikunja.aquariusly.ui.screens.onboarding.OnboardingScreen
import com.nikunja.aquariusly.ui.screens.profile.ProfileScreen
import com.nikunja.aquariusly.ui.screens.profileedit.ProfileEditScreen
import com.nikunja.aquariusly.ui.screens.settings.SettingsScreen
import com.nikunja.aquariusly.ui.screens.settings.subscription.*
import com.nikunja.aquariusly.ui.screens.settings.ai.*
import com.nikunja.aquariusly.ui.screens.settings.ai.configuremodel.ConfigureModelScreen
import com.nikunja.aquariusly.ui.screens.settings.chat.*
import com.nikunja.aquariusly.ui.screens.settings.privacy.*
import com.nikunja.aquariusly.ui.screens.settings.support.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun NavGraph(
    navController: NavHostController,
    hasCompletedOnboarding: Boolean,
    initialChatId: String? = null,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    
    val startDestination = when {
        !hasCompletedOnboarding -> Screen.Onboarding.route
        !isLoggedIn -> Screen.Login.route
        else -> Screen.Chat.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                initialOffsetX = { 100 },
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                targetOffsetX = { -100 },
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                initialOffsetX = { -100 },
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                targetOffsetX = { 100 },
                animationSpec = tween(300)
            )
        }
    ) {
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Chat.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(route = Screen.Chat.route) {
            ChatScreen(
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                initialChatId = initialChatId
            )
        }
        
        composable(
            route = Screen.ChatWithModel.ROUTE,
            arguments = listOf(
                navArgument(Screen.ChatWithModel.ARG_MODEL_ID) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val modelId = backStackEntry.arguments?.getString(Screen.ChatWithModel.ARG_MODEL_ID) ?: ""
            ChatScreen(
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                initialModelId = modelId
            )
        }
        
        composable(route = Screen.Home.route) {
            HomeScreen(
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        
        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onEditProfileClick = { navController.navigate(Screen.ProfileEdit.route) },
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onCurrentPlanClick = { navController.navigate(Screen.CurrentPlan.route) },
                onCreditsClick = { navController.navigate(Screen.Credits.route) },
                onBuyCreditsClick = { navController.navigate(Screen.BuyCredits.route) },
                onBillingHistoryClick = { navController.navigate(Screen.BillingHistory.route) },
                onManageSubscriptionClick = { navController.navigate(Screen.ManageSubscription.route) },
                onModelSelectionClick = { navController.navigate(Screen.ModelSelection.route) },
                onConfigureModelClick = { navController.navigate(Screen.ConfigureModel.route) },
                onMcpServersClick = { navController.navigate(Screen.McpServers.route) },
                onRagSettingsClick = { navController.navigate(Screen.RagSettings.route) },
                onSystemPromptClick = { navController.navigate(Screen.SystemPrompt.route) },
                onTemperatureClick = { navController.navigate(Screen.Temperature.route) },
                onMaxTokensClick = { navController.navigate(Screen.MaxTokens.route) },
                onChatHistoryClick = { navController.navigate(Screen.ChatHistory.route) },
                onClearChatsClick = { navController.navigate(Screen.ClearChats.route) },
                onPrivacyClick = { navController.navigate(Screen.Privacy.route) },
                onSecurityClick = { navController.navigate(Screen.Security.route) },
                onHelpCenterClick = { navController.navigate(Screen.HelpCenter.route) },
                onSendFeedbackClick = { navController.navigate(Screen.SendFeedback.route) },
                onAboutClick = { navController.navigate(Screen.About.route) }
            )
        }
        
        composable(route = Screen.ProfileEdit.route) {
            ProfileEditScreen(
                onBackClick = { navController.popBackStack() },
                onProfileUpdated = { }
            )
        }
        
        // Subscription & Billing Screens
        composable(route = Screen.CurrentPlan.route) {
            CurrentPlanScreen(
                onBackClick = { navController.popBackStack() },
                onUpgradeClick = { navController.navigate(Screen.ManageSubscription.route) }
            )
        }
        
        composable(route = Screen.Credits.route) {
            CreditsScreen(
                onBackClick = { navController.popBackStack() },
                onBuyCreditsClick = { navController.navigate(Screen.BuyCredits.route) }
            )
        }
        
        composable(route = Screen.BuyCredits.route) {
            BuyCreditsScreen(onBackClick = { navController.popBackStack() })
        }
        
        composable(route = Screen.BillingHistory.route) {
            BillingHistoryScreen(onBackClick = { navController.popBackStack() })
        }
        
        composable(route = Screen.ManageSubscription.route) {
            ManageSubscriptionScreen(onBackClick = { navController.popBackStack() })
        }
        
        // AI Configuration Screens
        composable(route = Screen.ModelSelection.route) {
            com.nikunja.aquariusly.ui.screens.settings.ai.ModelSelectionScreen(
                onBackClick = { navController.popBackStack() },
                onChatWithModel = { modelId ->
                    val encodedModelId = URLEncoder.encode(modelId, StandardCharsets.UTF_8.toString())
                    navController.navigate("chat?modelId=$encodedModelId") {
                        popUpTo(Screen.Chat.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(route = Screen.ConfigureModel.route) {
            ConfigureModelScreen(
                onBackClick = { navController.popBackStack() },
                onChatWithModel = { modelId ->
                    val encodedModelId = URLEncoder.encode(modelId, StandardCharsets.UTF_8.toString())
                    navController.navigate("chat?modelId=$encodedModelId") {
                        popUpTo(Screen.Chat.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(route = Screen.McpServers.route) {
            McpServersScreen(onBackClick = { navController.popBackStack() })
        }
        
        composable(route = Screen.RagSettings.route) {
            RagSettingsScreen(onBackClick = { navController.popBackStack() })
        }
        
        composable(route = Screen.SystemPrompt.route) {
            SystemPromptScreen(onBackClick = { navController.popBackStack() })
        }
        
        composable(route = Screen.Temperature.route) {
            TemperatureScreen(onBackClick = { navController.popBackStack() })
        }
        
        composable(route = Screen.MaxTokens.route) {
            MaxTokensScreen(onBackClick = { navController.popBackStack() })
        }
        
        // Chat Settings Screens
        composable(route = Screen.ChatHistory.route) {
            ChatHistoryScreen(onBackClick = { navController.popBackStack() })
        }
        
        composable(route = Screen.ClearChats.route) {
            ClearChatsScreen(onBackClick = { navController.popBackStack() })
        }
        
        // Privacy & Security Screens
        composable(route = Screen.Privacy.route) {
            PrivacyScreen(onBackClick = { navController.popBackStack() })
        }
        
        composable(route = Screen.Security.route) {
            SecurityScreen(onBackClick = { navController.popBackStack() })
        }
        
        // Support Screens
        composable(route = Screen.HelpCenter.route) {
            HelpCenterScreen(onBackClick = { navController.popBackStack() })
        }
        
        composable(route = Screen.SendFeedback.route) {
            SendFeedbackScreen(onBackClick = { navController.popBackStack() })
        }
        
        composable(route = Screen.About.route) {
            AboutScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
