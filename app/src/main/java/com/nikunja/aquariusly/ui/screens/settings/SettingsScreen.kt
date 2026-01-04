package com.nikunja.aquariusly.ui.screens.settings

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import coil.compose.AsyncImage
import com.nikunja.aquariusly.ui.theme.UnifiedAIColors
import com.nikunja.aquariusly.ui.theme.UnifiedTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onSignOut: () -> Unit,
    onCurrentPlanClick: () -> Unit = {},
    onCreditsClick: () -> Unit = {},
    onBuyCreditsClick: () -> Unit = {},
    onBillingHistoryClick: () -> Unit = {},
    onManageSubscriptionClick: () -> Unit = {},
    onModelSelectionClick: () -> Unit = {},
    onMcpServersClick: () -> Unit = {},
    onRagSettingsClick: () -> Unit = {},
    onSystemPromptClick: () -> Unit = {},
    onTemperatureClick: () -> Unit = {},
    onMaxTokensClick: () -> Unit = {},
    onChatHistoryClick: () -> Unit = {},
    onClearChatsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onSecurityClick: () -> Unit = {},
    onHelpCenterClick: () -> Unit = {},
    onSendFeedbackClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val colors = UnifiedTheme.colors
    val context = LocalContext.current

    // Check notification permission status
    var notificationsEnabled by remember {
        mutableStateOf(NotificationManagerCompat.from(context).areNotificationsEnabled())
    }
    
    // Track if permission was already requested
    var permissionRequested by remember { mutableStateOf(false) }

    // Permission launcher for Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        notificationsEnabled = isGranted
        permissionRequested = true
        // If denied, open settings
        if (!isGranted) {
            val intent = Intent().apply {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
            context.startActivity(intent)
        }
    }

    // Refresh notification status when screen resumes
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    LaunchedEffect(state.isSignedOut) {
        if (state.isSignedOut) {
            onSignOut()
        }
    }

    Scaffold(
        containerColor = colors.backgroundPrimary,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Settings",
                        color = colors.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.backgroundPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card
            ProfileCard(
                name = state.profile?.name ?: "",
                email = state.profile?.email ?: "",
                photoUrl = state.profile?.picture,
                onClick = onEditProfileClick,
                colors = colors
            )

            // Subscription & Billing
            SettingsSection(title = "Subscription & Billing", colors = colors) {
                SettingsNavigationItem(
                    icon = Icons.Default.Star,
                    title = "Current Plan",
                    subtitle = "Pro Plan • Active",
                    onClick = onCurrentPlanClick,
                    colors = colors
                )
                SettingsNavigationItem(
                    icon = Icons.Default.Add,
                    title = "Credits",
                    subtitle = "1,250 credits remaining",
                    onClick = onCreditsClick,
                    colors = colors
                )
                SettingsNavigationItem(
                    icon = Icons.Default.ShoppingCart,
                    title = "Buy Credits",
                    subtitle = "Purchase additional credits",
                    onClick = onBuyCreditsClick,
                    colors = colors
                )
                SettingsNavigationItem(
                    icon = Icons.Default.DateRange,
                    title = "Billing History",
                    subtitle = "View past invoices",
                    onClick = onBillingHistoryClick,
                    colors = colors
                )
                SettingsNavigationItem(
                    icon = Icons.Default.Refresh,
                    title = "Manage Subscription",
                    subtitle = "Upgrade, downgrade or cancel",
                    onClick = onManageSubscriptionClick,
                    colors = colors
                )
            }

            // General Settings
            SettingsSection(title = "General", colors = colors) {
                SettingsToggleItem(
                    icon = Icons.Default.Settings,
                    title = "Dark Mode",
                    subtitle = "Use dark theme",
                    isChecked = state.isDarkMode,
                    onToggle = viewModel::toggleDarkMode,
                    colors = colors
                )
                SettingsToggleItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = if (notificationsEnabled) "Push notifications enabled" else "Tap to enable notifications",
                    isChecked = notificationsEnabled,
                    onToggle = {
                        // Always open system settings (for both enable and disable)
                        val intent = Intent().apply {
                            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                        context.startActivity(intent)
                    },
                    colors = colors
                )
                SettingsToggleItem(
                    icon = Icons.Default.Phone,
                    title = "Haptic Feedback",
                    subtitle = "Vibrate on interactions",
                    isChecked = state.hapticFeedback,
                    onToggle = viewModel::toggleHapticFeedback,
                    colors = colors
                )
            }

            // AI Settings
            SettingsSection(title = "AI Configuration", colors = colors) {
                SettingsNavigationItem(
                    icon = Icons.Default.Star,
                    title = "Model Selection",
                    subtitle = "Choose your AI model",
                    onClick = onModelSelectionClick,
                    colors = colors
                )
                SettingsNavigationItem(
                    icon = Icons.Default.Build,
                    title = "MCP Servers",
                    subtitle = "Configure Model Context Protocol",
                    onClick = onMcpServersClick,
                    colors = colors
                )
                SettingsNavigationItem(
                    icon = Icons.Default.Search,
                    title = "RAG Settings",
                    subtitle = "Retrieval Augmented Generation",
                    onClick = onRagSettingsClick,
                    colors = colors
                )
                SettingsNavigationItem(
                    icon = Icons.Default.Create,
                    title = "System Prompt",
                    subtitle = "Customize AI behavior",
                    onClick = onSystemPromptClick,
                    colors = colors
                )
                SettingsNavigationItem(
                    icon = Icons.Default.Settings,
                    title = "Temperature",
                    subtitle = "Control response creativity",
                    onClick = onTemperatureClick,
                    colors = colors
                )
                SettingsNavigationItem(
                    icon = Icons.Default.List,
                    title = "Max Tokens",
                    subtitle = "Set response length limit",
                    onClick = onMaxTokensClick,
                    colors = colors
                )
            }

            // Chat Settings
            SettingsSection(title = "Chat", colors = colors) {
                SettingsNavigationItem(
                    icon = Icons.Default.Refresh,
                    title = "Chat History",
                    subtitle = "Manage your conversations",
                    onClick = onChatHistoryClick,
                    colors = colors
                )
                SettingsToggleItem(
                    icon = Icons.Default.Send,
                    title = "Stream Responses",
                    subtitle = "Show responses as they generate",
                    isChecked = true,
                    onToggle = { },
                    colors = colors
                )
                SettingsNavigationItem(
                    icon = Icons.Default.Delete,
                    title = "Clear All Chats",
                    subtitle = "Delete all conversation history",
                    onClick = onClearChatsClick,
                    colors = colors
                )
            }

            // Privacy & Security
            SettingsSection(title = "Privacy & Security", colors = colors) {
                SettingsNavigationItem(
                    icon = Icons.Default.Lock,
                    title = "Privacy",
                    subtitle = "Manage your data",
                    onClick = onPrivacyClick,
                    colors = colors
                )
                SettingsNavigationItem(
                    icon = Icons.Default.Check,
                    title = "Security",
                    subtitle = "App lock and authentication",
                    onClick = onSecurityClick,
                    colors = colors
                )
            }

            // Support
            SettingsSection(title = "Support", colors = colors) {
                SettingsNavigationItem(
                    icon = Icons.Default.Info,
                    title = "Help Center",
                    subtitle = "FAQs and guides",
                    onClick = onHelpCenterClick,
                    colors = colors
                )
                SettingsNavigationItem(
                    icon = Icons.Default.Email,
                    title = "Send Feedback",
                    subtitle = "Report bugs or suggest features",
                    onClick = onSendFeedbackClick,
                    colors = colors
                )
                SettingsNavigationItem(
                    icon = Icons.Default.Info,
                    title = "About",
                    subtitle = "Version 1.0.0",
                    onClick = onAboutClick,
                    colors = colors
                )
            }

            // Sign Out
            Spacer(modifier = Modifier.height(8.dp))
            
            SignOutButton(
                isLoading = state.isSigningOut,
                onClick = viewModel::signOut,
                colors = colors
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProfileCard(
    name: String,
    email: String,
    photoUrl: String?,
    onClick: () -> Unit,
    colors: UnifiedAIColors
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = colors.surfaceElevated
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(colors.cardBackground),
                contentAlignment = Alignment.Center
            ) {
                if (photoUrl != null) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Profile photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Outlined.Person,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = colors.textMuted
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name.ifEmpty { "Set your name" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }

            Icon(
                Icons.Outlined.Edit,
                contentDescription = "Edit profile",
                tint = colors.textMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    colors: UnifiedAIColors,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = colors.textSecondary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = colors.surfaceElevated
        ) {
            Column(content = content)
        }
    }
}

@Composable
private fun SettingsToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onToggle: () -> Unit,
    colors: UnifiedAIColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted
            )
        }
        
        Switch(
            checked = isChecked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.textPrimary,
                checkedTrackColor = colors.accentBlue,
                uncheckedThumbColor = colors.textMuted,
                uncheckedTrackColor = colors.borderDefault
            )
        )
    }
}

@Composable
private fun SettingsNavigationItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    colors: UnifiedAIColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted
            )
        }
        
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = colors.textMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SignOutButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    colors: UnifiedAIColors
) {
    OutlinedButton(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = colors.error
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.error.copy(alpha = 0.5f))
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = colors.error,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Sign Out",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
