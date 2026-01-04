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

data class AIModel(
    val id: String,
    val name: String,
    val provider: String,
    val description: String,
    val contextWindow: String,
    val speed: String,
    val costPerToken: String,
    val capabilities: List<String>,
    val isSelected: Boolean = false,
    val isPro: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSelectionScreen(
    onBackClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    var selectedModelId by remember { mutableStateOf("gpt-4-turbo") }
    
    val models = listOf(
        AIModel(
            id = "gpt-4-turbo",
            name = "GPT-4 Turbo",
            provider = "OpenAI",
            description = "Most capable model for complex tasks",
            contextWindow = "128K tokens",
            speed = "Fast",
            costPerToken = "2 credits/1K tokens",
            capabilities = listOf("Reasoning", "Coding", "Analysis", "Creative"),
            isSelected = true
        ),
        AIModel(
            id = "claude-3-opus",
            name = "Claude 3 Opus",
            provider = "Anthropic",
            description = "Excellent for nuanced conversations",
            contextWindow = "200K tokens",
            speed = "Medium",
            costPerToken = "3 credits/1K tokens",
            capabilities = listOf("Writing", "Analysis", "Research"),
            isPro = true
        ),
        AIModel(
            id = "gemini-pro",
            name = "Gemini Pro",
            provider = "Google",
            description = "Balanced performance and speed",
            contextWindow = "32K tokens",
            speed = "Very Fast",
            costPerToken = "1 credit/1K tokens",
            capabilities = listOf("General", "Coding", "Math")
        ),
        AIModel(
            id = "llama-3-70b",
            name = "Llama 3 70B",
            provider = "Meta",
            description = "Open source powerhouse",
            contextWindow = "8K tokens",
            speed = "Fast",
            costPerToken = "0.5 credits/1K tokens",
            capabilities = listOf("General", "Coding")
        )
    )

    Scaffold(
        containerColor = colors.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Model Selection",
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
            Text(
                text = "Choose your default AI model",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
            
            models.forEach { model ->
                ModelCard(
                    model = model,
                    isSelected = model.id == selectedModelId,
                    onClick = { selectedModelId = model.id },
                    colors = colors
                )
            }
        }
    }
}

@Composable
private fun ModelCard(
    model: AIModel,
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
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) colors.accentBlue.copy(alpha = 0.08f) else colors.surfaceElevated
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isSelected,
                        onClick = onClick,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = colors.accentBlue,
                            unselectedColor = colors.textMuted
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = model.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary
                            )
                            if (model.isPro) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = colors.accentViolet.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "PRO",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = colors.accentViolet,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text(
                            text = model.provider,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = model.description,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ModelStat(label = "Context", value = model.contextWindow, colors = colors)
                ModelStat(label = "Speed", value = model.speed, colors = colors)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = model.costPerToken,
                style = MaterialTheme.typography.bodySmall,
                color = colors.accentAmber
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                model.capabilities.forEach { capability ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = colors.borderDefault
                    ) {
                        Text(
                            text = capability,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModelStat(label: String, value: String, colors: UnifiedAIColors) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = colors.textPrimary
        )
    }
}
