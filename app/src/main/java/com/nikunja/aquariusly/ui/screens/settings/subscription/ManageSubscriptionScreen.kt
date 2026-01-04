package com.nikunja.aquariusly.ui.screens.settings.subscription

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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

data class SubscriptionPlan(
    val name: String,
    val price: String,
    val period: String,
    val features: List<String>,
    val isCurrent: Boolean = false,
    val isRecommended: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageSubscriptionScreen(
    onBackClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    var showCancelDialog by remember { mutableStateOf(false) }
    
    val plans = listOf(
        SubscriptionPlan(
            name = "Free",
            price = "$0",
            period = "forever",
            features = listOf("50 messages/day", "Basic AI models", "Standard response time")
        ),
        SubscriptionPlan(
            name = "Pro",
            price = "$19.99",
            period = "/month",
            features = listOf("Unlimited messages", "All AI models", "Priority response", "Custom prompts"),
            isCurrent = true
        ),
        SubscriptionPlan(
            name = "Enterprise",
            price = "$49.99",
            period = "/month",
            features = listOf("Everything in Pro", "API access", "Team features", "Dedicated support", "Custom integrations"),
            isRecommended = true
        )
    )

    if (showCancelDialog) {
        CancelSubscriptionDialog(
            onDismiss = { showCancelDialog = false },
            onConfirm = { showCancelDialog = false },
            colors = colors
        )
    }

    Scaffold(
        containerColor = colors.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Manage Subscription",
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
            Text(
                text = "Choose Your Plan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            
            plans.forEach { plan ->
                PlanCard(plan = plan, colors = colors)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Cancel Subscription
            TextButton(
                onClick = { showCancelDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Cancel Subscription",
                    color = colors.error
                )
            }
        }
    }
}

@Composable
private fun PlanCard(plan: SubscriptionPlan, colors: UnifiedAIColors) {
    val borderColor = when {
        plan.isCurrent -> colors.accentBlue
        plan.isRecommended -> colors.accentViolet
        else -> colors.borderDefault
    }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (plan.isCurrent || plan.isRecommended) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            ),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = plan.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        if (plan.isCurrent) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = colors.accentBlue.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "CURRENT",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.accentBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        if (plan.isRecommended) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = colors.accentViolet.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "RECOMMENDED",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.accentViolet,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = plan.price,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = plan.period,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textMuted,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            plan.features.forEach { feature ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = colors.success,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
            }
            
            if (!plan.isCurrent) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* Switch plan */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (plan.isRecommended) colors.accentViolet else colors.accentBlue
                    )
                ) {
                    Text(
                        text = if (plan.price == "$0") "Downgrade" else "Upgrade",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun CancelSubscriptionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    colors: UnifiedAIColors
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceElevated,
        title = {
            Text(
                "Cancel Subscription?",
                color = colors.textPrimary
            )
        },
        text = {
            Text(
                "You'll lose access to Pro features at the end of your billing period. Your data will be preserved.",
                color = colors.textSecondary
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Cancel Subscription", color = colors.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Keep Subscription", color = colors.accentBlue)
            }
        }
    )
}
