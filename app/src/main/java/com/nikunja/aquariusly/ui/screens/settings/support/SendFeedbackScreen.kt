package com.nikunja.aquariusly.ui.screens.settings.support

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

enum class FeedbackType(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    BUG("Bug Report", Icons.Default.Warning),
    FEATURE("Feature Request", Icons.Default.Star),
    IMPROVEMENT("Improvement", Icons.Default.ThumbUp),
    OTHER("Other", Icons.Default.MoreVert)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendFeedbackScreen(
    onBackClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    var selectedType by remember { mutableStateOf(FeedbackType.BUG) }
    var subject by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var includeSystemInfo by remember { mutableStateOf(true) }
    var includeScreenshot by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = colors.backgroundPrimary,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Send Feedback",
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Feedback Type Selection
            Text(
                text = "What type of feedback?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FeedbackType.entries.forEach { type ->
                    FeedbackTypeChip(
                        type = type,
                        isSelected = selectedType == type,
                        onClick = { selectedType = type },
                        colors = colors,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Subject
            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Subject", color = colors.textMuted) },
                placeholder = { Text("Brief summary of your feedback", color = colors.textMuted) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accentBlue,
                    unfocusedBorderColor = colors.borderDefault,
                    focusedContainerColor = colors.surfaceElevated,
                    unfocusedContainerColor = colors.surfaceElevated,
                    cursorColor = colors.accentBlue,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary,
                    focusedLabelColor = colors.accentBlue
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                label = { Text("Description", color = colors.textMuted) },
                placeholder = { 
                    Text(
                        when (selectedType) {
                            FeedbackType.BUG -> "Please describe the bug, steps to reproduce, and expected behavior..."
                            FeedbackType.FEATURE -> "Describe the feature you'd like to see and how it would help you..."
                            FeedbackType.IMPROVEMENT -> "What would you like us to improve and why..."
                            FeedbackType.OTHER -> "Share your thoughts with us..."
                        },
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
                    unfocusedTextColor = colors.textPrimary,
                    focusedLabelColor = colors.accentBlue
                ),
                shape = RoundedCornerShape(12.dp)
            )
            
            // Options
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = colors.surfaceElevated
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { includeSystemInfo = !includeSystemInfo }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Include System Info",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "Device model, OS version, app version",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textMuted
                            )
                        }
                        Checkbox(
                            checked = includeSystemInfo,
                            onCheckedChange = { includeSystemInfo = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = colors.accentBlue,
                                uncheckedColor = colors.textMuted
                            )
                        )
                    }
                    
                    HorizontalDivider(color = colors.borderSubtle)
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { includeScreenshot = !includeScreenshot }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Attach Screenshot",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "Include a screenshot of the issue",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textMuted
                            )
                        }
                        Checkbox(
                            checked = includeScreenshot,
                            onCheckedChange = { includeScreenshot = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = colors.accentBlue,
                                uncheckedColor = colors.textMuted
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Submit Button
            Button(
                onClick = { /* Submit feedback */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = subject.isNotBlank() && description.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accentBlue,
                    disabledContainerColor = colors.borderDefault
                )
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Submit Feedback",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun FeedbackTypeChip(
    type: FeedbackType,
    isSelected: Boolean,
    onClick: () -> Unit,
    colors: UnifiedAIColors,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) colors.accentBlue else colors.borderDefault,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) colors.accentBlue.copy(alpha = 0.1f) else colors.surfaceElevated
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = type.icon,
                contentDescription = null,
                tint = if (isSelected) colors.accentBlue else colors.textMuted,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = type.label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) colors.accentBlue else colors.textSecondary
            )
        }
    }
}
