package com.nikunja.aquariusly.ui.screens.settings.ai

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

data class TokenPreset(
    val tokens: Int,
    val label: String,
    val description: String,
    val useCase: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaxTokensScreen(
    onBackClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    var maxTokens by remember { mutableFloatStateOf(2048f) }
    var selectedPreset by remember { mutableStateOf<Int?>(2048) }
    
    val presets = listOf(
        TokenPreset(512, "Short", "~400 words", "Quick answers, simple queries"),
        TokenPreset(1024, "Medium", "~800 words", "Standard conversations"),
        TokenPreset(2048, "Long", "~1,600 words", "Detailed explanations"),
        TokenPreset(4096, "Extended", "~3,200 words", "Complex analysis, long-form content"),
        TokenPreset(8192, "Maximum", "~6,400 words", "Full documents, comprehensive responses")
    )

    Scaffold(
        containerColor = colors.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Max Tokens",
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
                actions = {
                    TextButton(onClick = { /* Save */ }) {
                        Text("Save", color = colors.accentBlue)
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
            // Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = colors.infoBg
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = colors.info,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Max tokens limits the length of AI responses. Higher values allow longer responses but use more credits.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.info
                    )
                }
            }
            
            // Current Value Display
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
                        text = "Current Limit",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${maxTokens.toInt()}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.accentBlue
                    )
                    Text(
                        text = "tokens",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Slider(
                        value = maxTokens,
                        onValueChange = { 
                            maxTokens = it
                            selectedPreset = null
                        },
                        valueRange = 256f..8192f,
                        steps = 31,
                        colors = SliderDefaults.colors(
                            thumbColor = colors.accentBlue,
                            activeTrackColor = colors.accentBlue,
                            inactiveTrackColor = colors.borderDefault
                        )
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "256",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textMuted
                        )
                        Text(
                            text = "8192",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textMuted
                        )
                    }
                }
            }
            
            // Presets
            Text(
                text = "Quick Presets",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            presets.forEach { preset ->
                PresetCard(
                    preset = preset,
                    isSelected = selectedPreset == preset.tokens,
                    onClick = {
                        selectedPreset = preset.tokens
                        maxTokens = preset.tokens.toFloat()
                    },
                    colors = colors
                )
            }
            
            // Cost Estimation
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = colors.warningBg
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = colors.warning,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Estimated Cost",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.warning,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "~${(maxTokens / 1000 * 2).toInt()} credits per max-length response",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.warning
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetCard(
    preset: TokenPreset,
    isSelected: Boolean,
    onClick: () -> Unit,
    colors: UnifiedAIColors
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) colors.accentBlue else colors.borderDefault,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) colors.accentBlue.copy(alpha = 0.08f) else colors.surfaceElevated
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
                        text = preset.label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = preset.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted
                    )
                }
                Text(
                    text = preset.useCase,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
            
            Text(
                text = "${preset.tokens}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) colors.accentBlue else colors.textSecondary
            )
        }
    }
}
