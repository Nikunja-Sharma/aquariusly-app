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

data class KnowledgeBase(
    val id: String,
    val name: String,
    val documentCount: Int,
    val lastUpdated: String,
    val size: String,
    val isActive: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RagSettingsScreen(
    onBackClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    var ragEnabled by remember { mutableStateOf(true) }
    var chunkSize by remember { mutableFloatStateOf(512f) }
    var topK by remember { mutableFloatStateOf(5f) }
    var similarityThreshold by remember { mutableFloatStateOf(0.7f) }
    
    val knowledgeBases = remember {
        listOf(
            KnowledgeBase("kb1", "Company Docs", 156, "Jan 28, 2026", "24.5 MB", true),
            KnowledgeBase("kb2", "Product Manual", 42, "Jan 15, 2026", "8.2 MB", true),
            KnowledgeBase("kb3", "Research Papers", 89, "Dec 20, 2025", "45.1 MB", false)
        )
    }

    Scaffold(
        containerColor = colors.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "RAG Settings",
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
            // RAG Toggle
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = colors.surfaceElevated
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enable RAG",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Use knowledge bases to enhance AI responses",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted
                        )
                    }
                    Switch(
                        checked = ragEnabled,
                        onCheckedChange = { ragEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colors.textPrimary,
                            checkedTrackColor = colors.accentBlue
                        )
                    )
                }
            }
            
            // Configuration
            if (ragEnabled) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = colors.surfaceElevated
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Configuration",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Chunk Size
                        SliderSetting(
                            label = "Chunk Size",
                            value = chunkSize,
                            onValueChange = { chunkSize = it },
                            valueRange = 128f..2048f,
                            displayValue = "${chunkSize.toInt()} tokens",
                            colors = colors
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Top K
                        SliderSetting(
                            label = "Top K Results",
                            value = topK,
                            onValueChange = { topK = it },
                            valueRange = 1f..20f,
                            displayValue = topK.toInt().toString(),
                            colors = colors
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Similarity Threshold
                        SliderSetting(
                            label = "Similarity Threshold",
                            value = similarityThreshold,
                            onValueChange = { similarityThreshold = it },
                            valueRange = 0f..1f,
                            displayValue = String.format("%.2f", similarityThreshold),
                            colors = colors
                        )
                    }
                }
                
                // Knowledge Bases
                Text(
                    text = "Knowledge Bases",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                knowledgeBases.forEach { kb ->
                    KnowledgeBaseCard(knowledgeBase = kb, colors = colors)
                }
                
                // Add Knowledge Base Button
                OutlinedButton(
                    onClick = { /* Add KB */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colors.accentBlue
                    )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Knowledge Base")
                }
            }
        }
    }
}

@Composable
private fun SliderSetting(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    displayValue: String,
    colors: UnifiedAIColors
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
            Text(
                text = displayValue,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = colors.accentBlue
            )
        }
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
    }
}

@Composable
private fun KnowledgeBaseCard(knowledgeBase: KnowledgeBase, colors: UnifiedAIColors) {
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
                Icons.Default.Search,
                contentDescription = null,
                tint = if (knowledgeBase.isActive) colors.accentBlue else colors.textMuted,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = knowledgeBase.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = colors.textPrimary
                    )
                    if (knowledgeBase.isActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = colors.success.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Active",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.success
                            )
                        }
                    }
                }
                Text(
                    text = "${knowledgeBase.documentCount} docs • ${knowledgeBase.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }
            
            IconButton(onClick = { /* Edit */ }) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = colors.textMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
