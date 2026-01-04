package com.nikunja.aquariusly.ui.screens.settings.chat

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nikunja.aquariusly.ui.theme.UnifiedAIColors
import com.nikunja.aquariusly.ui.theme.UnifiedTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClearChatsScreen(
    onBackClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    var showConfirmDialog by remember { mutableStateOf(false) }
    var clearOption by remember { mutableStateOf<ClearOption?>(null) }
    
    val stats = remember {
        mapOf(
            "Total Conversations" to "156",
            "Total Messages" to "4,892",
            "Storage Used" to "24.5 MB",
            "Oldest Chat" to "Oct 15, 2025"
        )
    }

    if (showConfirmDialog && clearOption != null) {
        ConfirmClearDialog(
            option = clearOption!!,
            onDismiss = { 
                showConfirmDialog = false
                clearOption = null
            },
            onConfirm = {
                showConfirmDialog = false
                clearOption = null
                // Handle clear
            },
            colors = colors
        )
    }

    Scaffold(
        containerColor = colors.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Clear Chats",
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
            // Warning Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = colors.warningBg
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = colors.warning,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Warning",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.warning
                        )
                        Text(
                            text = "Deleted chats cannot be recovered. Make sure to export any important conversations before clearing.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.warning
                        )
                    }
                }
            }
            
            // Stats Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = colors.surfaceElevated
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Current Data",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    stats.forEach { (label, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textSecondary
                            )
                            Text(
                                text = value,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = colors.textPrimary
                            )
                        }
                    }
                }
            }
            
            // Clear Options
            Text(
                text = "Clear Options",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            ClearOptionCard(
                icon = Icons.Default.DateRange,
                title = "Clear Old Chats",
                description = "Delete conversations older than 30 days",
                buttonText = "Clear Old",
                onClick = {
                    clearOption = ClearOption.OLD
                    showConfirmDialog = true
                },
                colors = colors
            )
            
            ClearOptionCard(
                icon = Icons.Default.Delete,
                title = "Clear All Chats",
                description = "Delete all conversations and messages",
                buttonText = "Clear All",
                isDestructive = true,
                onClick = {
                    clearOption = ClearOption.ALL
                    showConfirmDialog = true
                },
                colors = colors
            )
            
            // Export Option
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = { /* Export */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colors.accentBlue
                )
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export All Chats")
            }
        }
    }
}

enum class ClearOption { OLD, ALL }

@Composable
private fun ClearOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    buttonText: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit,
    colors: UnifiedAIColors
) {
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
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) colors.error else colors.textSecondary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
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
            
            Button(
                onClick = onClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDestructive) colors.error else colors.accentBlue
                )
            ) {
                Text(buttonText, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun ConfirmClearDialog(
    option: ClearOption,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    colors: UnifiedAIColors
) {
    val (title, message) = when (option) {
        ClearOption.OLD -> "Clear Old Chats?" to "This will delete all conversations older than 30 days. This action cannot be undone."
        ClearOption.ALL -> "Clear All Chats?" to "This will permanently delete ALL conversations and messages. This action cannot be undone."
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceElevated,
        icon = {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = colors.error,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = title,
                color = colors.textPrimary,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = message,
                color = colors.textSecondary,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = colors.textSecondary)
            }
        }
    )
}
