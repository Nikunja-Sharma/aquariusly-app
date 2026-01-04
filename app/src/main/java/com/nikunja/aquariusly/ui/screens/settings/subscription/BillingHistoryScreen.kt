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

data class Invoice(
    val id: String,
    val date: String,
    val description: String,
    val amount: String,
    val status: InvoiceStatus
)

enum class InvoiceStatus { PAID, PENDING, FAILED }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingHistoryScreen(
    onBackClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    
    val invoices = remember {
        listOf(
            Invoice("INV-2026-001", "Jan 15, 2026", "Pro Plan - Monthly", "$19.99", InvoiceStatus.PAID),
            Invoice("INV-2025-012", "Dec 15, 2025", "Pro Plan - Monthly", "$19.99", InvoiceStatus.PAID),
            Invoice("INV-2025-011", "Dec 10, 2025", "Credit Purchase - 500", "$19.99", InvoiceStatus.PAID),
            Invoice("INV-2025-010", "Nov 15, 2025", "Pro Plan - Monthly", "$19.99", InvoiceStatus.PAID),
            Invoice("INV-2025-009", "Oct 15, 2025", "Pro Plan - Monthly", "$19.99", InvoiceStatus.PAID),
            Invoice("INV-2025-008", "Sep 15, 2025", "Pro Plan - Monthly", "$19.99", InvoiceStatus.PAID)
        )
    }

    Scaffold(
        containerColor = colors.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Billing History",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = colors.surfaceElevated
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$119.94",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Total Spent",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted
                        )
                    }
                    VerticalDivider(
                        modifier = Modifier.height(40.dp),
                        color = colors.borderSubtle
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "6",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Invoices",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "All Invoices",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            
            invoices.forEach { invoice ->
                InvoiceCard(invoice = invoice, colors = colors)
            }
        }
    }
}

@Composable
private fun InvoiceCard(invoice: Invoice, colors: UnifiedAIColors) {
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
                Icons.Default.Info,
                contentDescription = null,
                tint = colors.textMuted,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = invoice.description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = invoice.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted
                    )
                    Text(
                        text = invoice.id,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = invoice.amount,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                StatusBadge(status = invoice.status, colors = colors)
            }
        }
    }
}

@Composable
private fun StatusBadge(status: InvoiceStatus, colors: UnifiedAIColors) {
    val (text, color) = when (status) {
        InvoiceStatus.PAID -> "Paid" to colors.success
        InvoiceStatus.PENDING -> "Pending" to colors.warning
        InvoiceStatus.FAILED -> "Failed" to colors.error
    }
    
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}
