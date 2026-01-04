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
import com.nikunja.aquariusly.ui.theme.UnifiedTheme

data class PromptTemplate(
    val id: String,
    val name: String,
    val description: String,
    val prompt: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemPromptScreen(
    onBackClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    var customPrompt by remember { 
        mutableStateOf("You are a helpful AI assistant. Be concise, accurate, and friendly in your responses. When providing code, include comments and explanations.") 
    }
    var selectedTemplateId by remember { mutableStateOf<String?>(null) }
    
    val templates = listOf(
        PromptTemplate(
            id = "default",
            name = "Default Assistant",
            description = "Balanced, helpful responses",
            prompt = "You are a helpful AI assistant. Be concise, accurate, and friendly."
        ),
        PromptTemplate(
            id = "coder",
            name = "Code Expert",
            description = "Focused on programming tasks",
            prompt = "You are an expert programmer. Provide clean, efficient code with explanations. Follow best practices and suggest improvements."
        ),
        PromptTemplate(
            id = "writer",
            name = "Creative Writer",
            description = "For creative and writing tasks",
            prompt = "You are a creative writing assistant. Help with storytelling, editing, and improving written content. Be imaginative and engaging."
        ),
        PromptTemplate(
            id = "analyst",
            name = "Data Analyst",
            description = "For data and analysis tasks",
            prompt = "You are a data analysis expert. Help interpret data, create visualizations, and provide insights. Be precise and methodical."
        )
    )

    Scaffold(
        containerColor = colors.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "System Prompt",
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
            // Info
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
                        text = "The system prompt defines how the AI behaves. It's sent with every message to guide responses.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.info
                    )
                }
            }
            
            // Templates
            Text(
                text = "Quick Templates",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            
            templates.forEach { template ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedTemplateId == template.id) 
                        colors.accentBlue.copy(alpha = 0.1f) 
                    else colors.surfaceElevated,
                    onClick = {
                        selectedTemplateId = template.id
                        customPrompt = template.prompt
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedTemplateId == template.id,
                            onClick = {
                                selectedTemplateId = template.id
                                customPrompt = template.prompt
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = colors.accentBlue
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = template.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = colors.textPrimary
                            )
                            Text(
                                text = template.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textMuted
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Custom Prompt Editor
            Text(
                text = "Custom Prompt",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            
            OutlinedTextField(
                value = customPrompt,
                onValueChange = { 
                    customPrompt = it
                    selectedTemplateId = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                placeholder = { 
                    Text(
                        "Enter your custom system prompt...",
                        color = colors.textMuted
                    ) 
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accentBlue,
                    unfocusedBorderColor = colors.borderDefault,
                    focusedContainerColor = colors.surfaceElevated,
                    unfocusedContainerColor = colors.surfaceElevated,
                    cursorColor = colors.accentBlue,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )
            
            // Character count
            Text(
                text = "${customPrompt.length} characters",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
                modifier = Modifier.align(Alignment.End)
            )
            
            // Reset Button
            TextButton(
                onClick = {
                    customPrompt = templates.first().prompt
                    selectedTemplateId = templates.first().id
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset to Default")
            }
        }
    }
}
