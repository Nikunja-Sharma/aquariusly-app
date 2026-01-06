package com.nikunja.aquariusly.ui.screens.referral

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nikunja.aquariusly.domain.model.*
import com.nikunja.aquariusly.ui.theme.UnifiedTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferralScreen(
    onBackClick: () -> Unit,
    viewModel: ReferralViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = UnifiedTheme.colors
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showCopiedSnackbar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invite Friends", color = colors.textPrimary) },
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
        },
        snackbarHost = {
            if (showCopiedSnackbar) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = colors.success,
                    contentColor = Color.White
                ) {
                    Text("Referral code copied!")
                }
                LaunchedEffect(showCopiedSnackbar) {
                    kotlinx.coroutines.delay(2000)
                    showCopiedSnackbar = false
                }
            }
        },
        containerColor = colors.backgroundPrimary
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Section
            item {
                ReferralHeroCard(
                    referralInfo = state.referralInfo,
                    onCopyCode = {
                        state.referralInfo?.referralCode?.let { code ->
                            clipboardManager.setText(AnnotatedString(code))
                            showCopiedSnackbar = true
                        }
                    },
                    onShare = {
                        state.referralInfo?.let { info ->
                            val shareText = """
                                🚀 Join me on Aquariusly - the ultimate AI chat app!
                                
                                Use my referral code: ${info.referralCode}
                                
                                Or click this link: ${info.referralLink}
                                
                                We both get free AI credits when you sign up! 🎁
                            """.trimIndent()
                            
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Join Aquariusly with my referral!")
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                        }
                    }
                )
            }

            // Stats Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Total Referrals",
                        value = "${state.referralInfo?.totalReferrals ?: 0}",
                        icon = Icons.Default.Person,
                        color = colors.accentBlue
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Rewards Earned",
                        value = "${state.referralInfo?.totalRewardsEarned ?: 0}",
                        icon = Icons.Default.Star,
                        color = colors.accentViolet
                    )
                }
            }

            // Apply Referral Code Section (only if not applied yet)
            if (!state.hasAppliedReferralCode) {
                item {
                    ApplyReferralCodeCard(
                        onApplyClick = { viewModel.onEvent(ReferralEvent.ShowApplyCodeDialog) }
                    )
                }
            }

            // How it Works
            item {
                HowItWorksSection()
            }

            // Referral History
            if (state.referralHistory.isNotEmpty()) {
                item {
                    Text(
                        text = "Your Referrals",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }
                
                items(state.referralHistory) { referral ->
                    ReferralHistoryItem(
                        referral = referral,
                        onClaimReward = { 
                            referral.reward?.let { reward ->
                                if (!reward.claimed) {
                                    viewModel.onEvent(ReferralEvent.ClaimReward(referral.id))
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    // Apply Code Dialog
    if (state.applyCodeDialogVisible) {
        ApplyCodeDialog(
            code = state.applyCodeInput,
            onCodeChange = { viewModel.onEvent(ReferralEvent.UpdateApplyCodeInput(it)) },
            onApply = { viewModel.onEvent(ReferralEvent.ApplyReferralCode) },
            onDismiss = { viewModel.onEvent(ReferralEvent.HideApplyCodeDialog) },
            isLoading = state.applyCodeLoading,
            result = state.applyCodeResult
        )
    }

    // Reward Claimed Dialog
    if (state.showRewardDialog && state.claimedReward != null) {
        RewardClaimedDialog(
            reward = state.claimedReward!!,
            onDismiss = { viewModel.onEvent(ReferralEvent.DismissRewardDialog) }
        )
    }
}

@Composable
private fun ReferralHeroCard(
    referralInfo: ReferralInfo?,
    onCopyCode: () -> Unit,
    onShare: () -> Unit
) {
    val colors = UnifiedTheme.colors
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceElevated)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(colors.accentBlue.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = colors.accentBlue
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Invite Friends, Earn Rewards!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Share your code and both you and your friend get free AI credits",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Referral Code Box
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = colors.backgroundTertiary
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Your Code",
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.textMuted
                            )
                            Text(
                                text = referralInfo?.referralCode ?: "Loading...",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                letterSpacing = 2.sp
                            )
                        }
                        
                        IconButton(
                            onClick = onCopyCode,
                            modifier = Modifier
                                .size(40.dp)
                                .background(colors.accentBlue.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Copy",
                                tint = colors.accentBlue
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Share Button
                Button(
                    onClick = onShare,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accentBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share Invite Link",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    val colors = UnifiedTheme.colors
    
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceElevated)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        }
    }
}

@Composable
private fun ApplyReferralCodeCard(
    onApplyClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onApplyClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.success.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(colors.success.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = colors.success,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Have a referral code?",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Text(
                    text = "Enter it to get free credits",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = colors.textSecondary
            )
        }
    }
}

@Composable
private fun HowItWorksSection() {
    val colors = UnifiedTheme.colors
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceElevated)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "How it Works",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            HowItWorksStep(
                number = "1",
                title = "Share your code",
                description = "Send your unique referral code to friends"
            )
            
            HowItWorksStep(
                number = "2",
                title = "Friend signs up",
                description = "They create an account using your code"
            )
            
            HowItWorksStep(
                number = "3",
                title = "Both get rewards!",
                description = "You both receive free AI credits instantly"
            )
        }
    }
}

@Composable
private fun HowItWorksStep(
    number: String,
    title: String,
    description: String
) {
    val colors = UnifiedTheme.colors
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(colors.accentBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        }
    }
}

@Composable
private fun ReferralHistoryItem(
    referral: Referral,
    onClaimReward: () -> Unit
) {
    val colors = UnifiedTheme.colors
    val statusColor = when (referral.status) {
        ReferralStatus.COMPLETED -> colors.success
        ReferralStatus.PENDING -> colors.warning
        ReferralStatus.EXPIRED -> colors.textMuted
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceElevated)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(statusColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = referral.referredUserName.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = referral.referredUserName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(statusColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = referral.status.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor
                    )
                }
            }
            
            // Claim button if reward available
            if (referral.status == ReferralStatus.COMPLETED && referral.reward != null && !referral.reward.claimed) {
                Button(
                    onClick = onClaimReward,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.success),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Claim", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun ApplyCodeDialog(
    code: String,
    onCodeChange: (String) -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean,
    result: com.nikunja.aquariusly.domain.repository.ApplyReferralResult?
) {
    val colors = UnifiedTheme.colors
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceElevated,
        title = {
            Text(
                text = "Enter Referral Code",
                color = colors.textPrimary
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = code,
                    onValueChange = { onCodeChange(it.uppercase()) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g., ABC123") },
                    singleLine = true,
                    enabled = !isLoading && result == null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.accentBlue,
                        unfocusedBorderColor = colors.borderDefault
                    )
                )
                
                if (result != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = result.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (result.success) colors.success else colors.error
                    )
                    if (result.success && result.referrerName != null) {
                        Text(
                            text = "Referred by: ${result.referrerName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (result?.success == true) {
                TextButton(onClick = onDismiss) {
                    Text("Done", color = colors.accentBlue)
                }
            } else {
                TextButton(
                    onClick = onApply,
                    enabled = code.isNotBlank() && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Apply", color = colors.accentBlue)
                    }
                }
            }
        },
        dismissButton = {
            if (result?.success != true) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = colors.textSecondary)
                }
            }
        }
    )
}

@Composable
private fun RewardClaimedDialog(
    reward: Reward,
    onDismiss: () -> Unit
) {
    val colors = UnifiedTheme.colors
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceElevated,
        icon = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(colors.success.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = colors.success,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = "Reward Claimed!",
                color = colors.textPrimary,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = reward.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "+${reward.amount} ${reward.type.name.replace("_", " ").lowercase()}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colors.success
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = colors.success)
            ) {
                Text("Awesome!")
            }
        }
    )
}
