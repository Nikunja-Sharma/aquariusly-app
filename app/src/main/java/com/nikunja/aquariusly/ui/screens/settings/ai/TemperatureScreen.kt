package com.nikunja.aquariusly.ui.screens.settings.ai

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
fun TemperatureScreen(
    onBackClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    var temperature by remember { mutableFloatStateOf(0.7f) }
    var topP by remember { mutableFloatStateOf(0.9f) }
    var frequencyPenalty by remember { mutableFloatStateOf(0f) }
    var presencePenalty by remember { mutableFloatStateOf(0f) }

    Scaffold(
        containerColor = colors.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Temperature & Sampling",
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Temperature
            ParameterCard(
                title = "Temperature",
                description = "Controls randomness. Lower values make responses more focused and deterministic. Higher values make output more creative and varied.",
                value = temperature,
                onValueChange = { temperature = it },
                valueRange = 0f..2f,
                displayValue = String.format("%.1f", temperature),
                lowLabel = "Precise",
                highLabel = "Creative",
                colors = colors
            )
            
            // Top P
            ParameterCard(
                title = "Top P (Nucleus Sampling)",
                description = "Controls diversity via nucleus sampling. 0.9 means only tokens comprising the top 90% probability mass are considered.",
                value = topP,
                onValueChange = { topP = it },
                valueRange = 0f..1f,
                displayValue = String.format("%.2f", topP),
                lowLabel = "Narrow",
                highLabel = "Diverse",
                colors = colors
            )
            
            // Frequency Penalty
            ParameterCard(
                title = "Frequency Penalty",
                description = "Reduces repetition by penalizing tokens based on how often they appear in the text so far.",
                value = frequencyPenalty,
                onValueChange = { frequencyPenalty = it },
                valueRange = 0f..2f,
                displayValue = String.format("%.1f", frequencyPenalty),
                lowLabel = "Allow Repetition",
                highLabel = "Avoid Repetition",
                colors = colors
            )
            
            // Presence Penalty
            ParameterCard(
                title = "Presence Penalty",
                description = "Encourages the model to talk about new topics by penalizing tokens that have already appeared.",
                value = presencePenalty,
                onValueChange = { presencePenalty = it },
                valueRange = 0f..2f,
                displayValue = String.format("%.1f", presencePenalty),
                lowLabel = "Stay on Topic",
                highLabel = "Explore Topics",
                colors = colors
            )
            
            // Presets
            Text(
                text = "Quick Presets",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PresetButton(
                    label = "Precise",
                    onClick = {
                        temperature = 0.2f
                        topP = 0.8f
                        frequencyPenalty = 0f
                        presencePenalty = 0f
                    },
                    colors = colors,
                    modifier = Modifier.weight(1f)
                )
                PresetButton(
                    label = "Balanced",
                    onClick = {
                        temperature = 0.7f
                        topP = 0.9f
                        frequencyPenalty = 0f
                        presencePenalty = 0f
                    },
                    colors = colors,
                    modifier = Modifier.weight(1f)
                )
                PresetButton(
                    label = "Creative",
                    onClick = {
                        temperature = 1.2f
                        topP = 0.95f
                        frequencyPenalty = 0.5f
                        presencePenalty = 0.5f
                    },
                    colors = colors,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ParameterCard(
    title: String,
    description: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    displayValue: String,
    lowLabel: String,
    highLabel: String,
    colors: UnifiedAIColors
) {
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
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colors.accentBlue.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = displayValue,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.accentBlue
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
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
                    text = lowLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted
                )
                Text(
                    text = highLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted
                )
            }
        }
    }
}

@Composable
private fun PresetButton(
    label: String,
    onClick: () -> Unit,
    colors: UnifiedAIColors,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = colors.textPrimary
        )
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}
