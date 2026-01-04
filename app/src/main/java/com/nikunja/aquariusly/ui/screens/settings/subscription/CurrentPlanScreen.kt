package com.nikunja.aquariusly.ui.screens.settings.subscription

import androidx.compose.foundation.background
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

data class CurrentPlanState(
    val planName: String = "Pro Plan",
    val status: String = "Active",
    val price: String = "$19.99/month",
    val renewalDate: String = "February 15, 2026",
    val features: List<String> = listOf(
        "Unlimited conversations",
        "Priority response time",
        "Advanced AI models access",
        "Custom system prompts",
        "API access",
        "Priority support"
    ),
    val usageStats: UsageStats = UsageStats()
)

data class UsageStats(
    val messagesThisMonth: Int = 1247,
    val tokensUsed: Long = 2_450_000,
    val tokensLimit: Long = 5_000_000,
    val apiCallsThisMonth: Int = 89
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentPlanScreen(
    onBackClick: () -> Unit,
    onUpgradeClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    val state = remember { CurrentPlanState() }

    Scaffold(
        containerColor = colors.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Current Plan",
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
            // Plan Card
            PlanCard(state = state, colors = colors)
            
            // Usage Stats
            UsageStatsCard(stats = state.usageStats, colors = colors)
            
            // Features
            FeaturesCard(features = state.features, colors = colors)
            
            // Upgrade Button
            Button(
                onClick = onUpgradeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accentBlue
                )
            ) {
                Text(
                    "Upgrade Plan",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun PlanCard(state: CurrentPlanState, colors: UnifiedAIColors) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colors.surfaceElevated
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = state.planName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = state.price,
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.accentBlue
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colors.success.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = state.status,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.success,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = colors.borderSubtle)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = colors.textMuted,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Renews on ${state.renewalDate}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun UsageStatsCard(stats: UsageStats, colors: UnifiedAIColors) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colors.surfaceElevated
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Usage This Month",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Token Usage Progress
            val tokenProgress = stats.tokensUsed.toFloat() / stats.tokensLimit.toFloat()
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tokens",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "${formatNumber(stats.tokensUsed)} / ${formatNumber(stats.tokensLimit)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { tokenProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = colors.accentBlue,
                    trackColor = colors.borderDefault,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Messages",
                    value = stats.messagesThisMonth.toString(),
                    colors = colors
                )
                StatItem(
                    label = "API Calls",
                    value = stats.apiCallsThisMonth.toString(),
                    colors = colors
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, colors: UnifiedAIColors) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted
        )
    }
}

@Composable
private fun FeaturesCard(features: List<String>, colors: UnifiedAIColors) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colors.surfaceElevated
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Plan Features",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            features.forEach { feature ->
                Row(
                    modifier = Modifier.padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = colors.success,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
            }
        }
    }
}

private fun formatNumber(num: Long): String {
    return when {
        num >= 1_000_000 -> String.format("%.1fM", num / 1_000_000.0)
        num >= 1_000 -> String.format("%.1fK", num / 1_000.0)
        else -> num.toString()
    }
}
