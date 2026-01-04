package com.nikunja.aquariusly.ui.screens.settings.privacy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nikunja.aquariusly.ui.theme.UnifiedAIColors
import com.nikunja.aquariusly.ui.theme.UnifiedTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(
    onBackClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    var saveHistory by remember { mutableStateOf(true) }
    var shareAnalytics by remember { mutableStateOf(false) }
    var personalizedAds by remember { mutableStateOf(false) }
    var improveAI by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = colors.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Privacy",
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
            // Data Collection
            PrivacySection(title = "Data Collection", colors = colors) {
                PrivacyToggleItem(
                    icon = Icons.Default.Refresh,
                    title = "Save Chat History",
                    description = "Store conversations on our servers for sync across devices",
                    isChecked = saveHistory,
                    onToggle = { saveHistory = it },
                    colors = colors
                )
                HorizontalDivider(color = colors.borderSubtle)
                PrivacyToggleItem(
                    icon = Icons.Default.Info,
                    title = "Share Analytics",
                    description = "Help us improve by sharing anonymous usage data",
                    isChecked = shareAnalytics,
                    onToggle = { shareAnalytics = it },
                    colors = colors
                )
                HorizontalDivider(color = colors.borderSubtle)
                PrivacyToggleItem(
                    icon = Icons.Default.Star,
                    title = "Improve AI Models",
                    description = "Allow your conversations to help train better AI",
                    isChecked = improveAI,
                    onToggle = { improveAI = it },
                    colors = colors
                )
            }
            
            // Advertising
            PrivacySection(title = "Advertising", colors = colors) {
                PrivacyToggleItem(
                    icon = Icons.Default.Notifications,
                    title = "Personalized Ads",
                    description = "Show ads based on your interests",
                    isChecked = personalizedAds,
                    onToggle = { personalizedAds = it },
                    colors = colors
                )
            }
            
            // Data Management
            PrivacySection(title = "Your Data", colors = colors) {
                PrivacyActionItem(
                    icon = Icons.Default.Share,
                    title = "Export My Data",
                    description = "Download a copy of all your data",
                    onClick = { /* Export */ },
                    colors = colors
                )
                HorizontalDivider(color = colors.borderSubtle)
                PrivacyActionItem(
                    icon = Icons.Default.Delete,
                    title = "Delete My Account",
                    description = "Permanently delete your account and all data",
                    onClick = { /* Delete */ },
                    isDestructive = true,
                    colors = colors
                )
            }
            
            // Legal Links
            PrivacySection(title = "Legal", colors = colors) {
                PrivacyLinkItem(
                    title = "Privacy Policy",
                    onClick = { /* Open */ },
                    colors = colors
                )
                HorizontalDivider(color = colors.borderSubtle)
                PrivacyLinkItem(
                    title = "Terms of Service",
                    onClick = { /* Open */ },
                    colors = colors
                )
                HorizontalDivider(color = colors.borderSubtle)
                PrivacyLinkItem(
                    title = "Cookie Policy",
                    onClick = { /* Open */ },
                    colors = colors
                )
            }
        }
    }
}

@Composable
private fun PrivacySection(
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
private fun PrivacyToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit,
    colors: UnifiedAIColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = colors.textPrimary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onToggle,
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
private fun PrivacyActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
    colors: UnifiedAIColors
) {
    Surface(
        onClick = onClick,
        color = colors.surfaceElevated
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) colors.error else colors.textSecondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (isDestructive) colors.error else colors.textPrimary
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }
        }
    }
}

@Composable
private fun PrivacyLinkItem(
    title: String,
    onClick: () -> Unit,
    colors: UnifiedAIColors
) {
    Surface(
        onClick = onClick,
        color = colors.surfaceElevated
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary
            )
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = colors.textMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
