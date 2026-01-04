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

data class LoginSession(
    val device: String,
    val location: String,
    val lastActive: String,
    val isCurrent: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(
    onBackClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    var appLockEnabled by remember { mutableStateOf(false) }
    var biometricEnabled by remember { mutableStateOf(true) }
    var twoFactorEnabled by remember { mutableStateOf(false) }
    
    val sessions = remember {
        listOf(
            LoginSession("iPhone 15 Pro", "San Francisco, CA", "Now", true),
            LoginSession("MacBook Pro", "San Francisco, CA", "2 hours ago", false),
            LoginSession("Chrome on Windows", "New York, NY", "Yesterday", false)
        )
    }

    Scaffold(
        containerColor = colors.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Security",
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
            // App Lock
            SecuritySection(title = "App Lock", colors = colors) {
                SecurityToggleItem(
                    icon = Icons.Default.Lock,
                    title = "Enable App Lock",
                    description = "Require authentication to open the app",
                    isChecked = appLockEnabled,
                    onToggle = { appLockEnabled = it },
                    colors = colors
                )
                if (appLockEnabled) {
                    HorizontalDivider(color = colors.borderSubtle)
                    SecurityToggleItem(
                        icon = Icons.Default.Face,
                        title = "Biometric Unlock",
                        description = "Use Face ID or fingerprint",
                        isChecked = biometricEnabled,
                        onToggle = { biometricEnabled = it },
                        colors = colors
                    )
                }
            }
            
            // Two-Factor Authentication
            SecuritySection(title = "Two-Factor Authentication", colors = colors) {
                SecurityToggleItem(
                    icon = Icons.Default.Check,
                    title = "Enable 2FA",
                    description = "Add an extra layer of security to your account",
                    isChecked = twoFactorEnabled,
                    onToggle = { twoFactorEnabled = it },
                    colors = colors
                )
                if (twoFactorEnabled) {
                    HorizontalDivider(color = colors.borderSubtle)
                    SecurityActionItem(
                        icon = Icons.Default.Phone,
                        title = "Authenticator App",
                        description = "Set up with Google Authenticator or similar",
                        onClick = { /* Setup */ },
                        colors = colors
                    )
                }
            }
            
            // Password
            SecuritySection(title = "Password", colors = colors) {
                SecurityActionItem(
                    icon = Icons.Default.Lock,
                    title = "Change Password",
                    description = "Last changed 45 days ago",
                    onClick = { /* Change */ },
                    colors = colors
                )
            }
            
            // Active Sessions
            Text(
                text = "Active Sessions",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.textSecondary,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp)
            )
            
            sessions.forEach { session ->
                SessionCard(session = session, colors = colors)
            }
            
            // Sign Out All
            TextButton(
                onClick = { /* Sign out all */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Sign Out All Other Devices",
                    color = colors.error
                )
            }
        }
    }
}

@Composable
private fun SecuritySection(
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
private fun SecurityToggleItem(
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
private fun SecurityActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
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
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = colors.textMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SessionCard(session: LoginSession, colors: UnifiedAIColors) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colors.surfaceElevated
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Phone,
                contentDescription = null,
                tint = if (session.isCurrent) colors.accentBlue else colors.textMuted,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = session.device,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = colors.textPrimary
                    )
                    if (session.isCurrent) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = colors.success.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Current",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.success
                            )
                        }
                    }
                }
                Text(
                    text = "${session.location} • ${session.lastActive}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }
            if (!session.isCurrent) {
                IconButton(onClick = { /* Revoke */ }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Revoke",
                        tint = colors.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
