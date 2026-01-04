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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.nikunja.aquariusly.ui.theme.UnifiedAIColors
import com.nikunja.aquariusly.ui.theme.UnifiedTheme

data class CreditPackage(
    val credits: Int,
    val price: String,
    val originalPrice: String? = null,
    val savings: String? = null,
    val isPopular: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyCreditsScreen(
    onBackClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    var selectedPackage by remember { mutableIntStateOf(1) }
    
    val packages = listOf(
        CreditPackage(100, "$4.99"),
        CreditPackage(500, "$19.99", "$24.95", "20% off", true),
        CreditPackage(1000, "$34.99", "$49.90", "30% off"),
        CreditPackage(2500, "$74.99", "$124.75", "40% off")
    )

    Scaffold(
        containerColor = colors.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Buy Credits",
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
            // Current Balance
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
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = colors.accentAmber,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Current Balance",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted
                        )
                        Text(
                            text = "1,250 credits",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                    }
                }
            }
            
            // Package Selection
            Text(
                text = "Select a Package",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            packages.forEachIndexed { index, pkg ->
                CreditPackageCard(
                    package_ = pkg,
                    isSelected = selectedPackage == index,
                    onClick = { selectedPackage = index },
                    colors = colors
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Purchase Button
            Button(
                onClick = { /* Purchase logic */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accentBlue
                )
            ) {
                Text(
                    "Purchase ${packages[selectedPackage].credits} Credits",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            // Info Text
            Text(
                text = "Credits never expire. Secure payment via Google Play.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun CreditPackageCard(
    package_: CreditPackage,
    isSelected: Boolean,
    onClick: () -> Unit,
    colors: UnifiedAIColors
) {
    val borderColor = if (isSelected) colors.accentBlue else colors.borderDefault
    val backgroundColor = if (isSelected) colors.accentBlue.copy(alpha = 0.1f) else colors.surfaceElevated
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = colors.accentBlue,
                    unselectedColor = colors.textMuted
                )
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${package_.credits} Credits",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    if (package_.isPopular) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = colors.accentBlue
                        ) {
                            Text(
                                text = "POPULAR",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                if (package_.savings != null) {
                    Text(
                        text = package_.savings,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.success
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = package_.price,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                if (package_.originalPrice != null) {
                    Text(
                        text = package_.originalPrice,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }
        }
    }
}
