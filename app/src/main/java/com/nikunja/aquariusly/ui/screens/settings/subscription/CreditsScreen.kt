package com.nikunja.aquariusly.ui.screens.settings.subscription

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

data class CreditsState(
    val currentCredits: Int = 1250,
    val totalCreditsUsed: Int = 3750,
    val creditHistory: List<CreditTransaction> = listOf(
        CreditTransaction("Purchase", "+500", "Jan 28, 2026", true),
        CreditTransaction("GPT-4 Usage", "-120", "Jan 27, 2026", false),
        CreditTransaction("Claude Usage", "-85", "Jan 26, 2026", false),
        CreditTransaction("Monthly Bonus", "+100", "Jan 1, 2026", true),
        CreditTransaction("Image Generation", "-45", "Dec 30, 2025", false)
    )
)

data class CreditTransaction(
    val description: String,
    val amount: String,
    val date: String,
    val isCredit: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditsScreen(
    onBackClick: () -> Unit,
    onBuyCreditsClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    val state = remember { CreditsState() }

    Scaffold(
        containerColor = colors.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Credits",
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
            // Credits Balance Card
            CreditsBalanceCard(
                currentCredits = state.currentCredits,
                totalUsed = state.totalCreditsUsed,
                colors = colors
            )
            
            // Buy Credits Button
            Button(
                onClick = onBuyCreditsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accentBlue
                )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Buy More Credits",
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            // Transaction History
            TransactionHistoryCard(
                transactions = state.creditHistory,
                colors = colors
            )
        }
    }
}

@Composable
private fun CreditsBalanceCard(
    currentCredits: Int,
    totalUsed: Int,
    colors: UnifiedAIColors
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colors.surfaceElevated
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Available Credits",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = currentCredits.toString(),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = colors.accentBlue
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = colors.borderSubtle)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = totalUsed.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Total Used",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = (currentCredits + totalUsed).toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Total Purchased",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionHistoryCard(
    transactions: List<CreditTransaction>,
    colors: UnifiedAIColors
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colors.surfaceElevated
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            transactions.forEachIndexed { index, transaction ->
                TransactionItem(transaction = transaction, colors = colors)
                if (index < transactions.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = colors.borderSubtle
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: CreditTransaction,
    colors: UnifiedAIColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = transaction.description,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary
            )
            Text(
                text = transaction.date,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted
            )
        }
        Text(
            text = transaction.amount,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (transaction.isCredit) colors.success else colors.error
        )
    }
}
