package com.nikunja.aquariusly.ui.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object Chat : Screen("chat")
    data object Home : Screen("home")
    data object Profile : Screen("profile")
    data object Settings : Screen("settings")
    data object ProfileEdit : Screen("profile_edit")
    
    // Subscription & Billing
    data object CurrentPlan : Screen("current_plan")
    data object Credits : Screen("credits")
    data object BuyCredits : Screen("buy_credits")
    data object BillingHistory : Screen("billing_history")
    data object ManageSubscription : Screen("manage_subscription")
    
    // AI Configuration
    data object ModelSelection : Screen("model_selection")
    data object ConfigureModel : Screen("configure_model")
    data object McpServers : Screen("mcp_servers")
    data object RagSettings : Screen("rag_settings")
    data object SystemPrompt : Screen("system_prompt")
    data object Temperature : Screen("temperature")
    data object MaxTokens : Screen("max_tokens")
    
    // Chat Settings
    data object ChatHistory : Screen("chat_history")
    data object ClearChats : Screen("clear_chats")
    
    // Privacy & Security
    data object Privacy : Screen("privacy")
    data object Security : Screen("security")
    
    // Support
    data object HelpCenter : Screen("help_center")
    data object SendFeedback : Screen("send_feedback")
    data object About : Screen("about")
    
    // Chat with specific model
    data class ChatWithModel(val modelId: String) : Screen("chat?modelId=$modelId") {
        companion object {
            const val ROUTE = "chat?modelId={modelId}"
            const val ARG_MODEL_ID = "modelId"
        }
    }
}
