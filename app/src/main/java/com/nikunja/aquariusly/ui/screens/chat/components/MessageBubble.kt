package com.nikunja.aquariusly.ui.screens.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nikunja.aquariusly.domain.model.AIModel
import com.nikunja.aquariusly.domain.model.AttachmentType
import com.nikunja.aquariusly.domain.model.Message
import com.nikunja.aquariusly.domain.model.MessageAttachment
import com.nikunja.aquariusly.ui.theme.UnifiedTheme
import kotlinx.coroutines.delay

@Composable
fun MessageBubble(
    message: Message,
    model: AIModel
) {
    val colors = UnifiedTheme.colors
    val isUser = message.isFromUser
    var isReasoningExpanded by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(model.brandColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = model.name.first().toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = model.brandColor
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Column(
            modifier = Modifier.widthIn(max = 300.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            // Show attachments if any
            message.attachments.forEach { attachment ->
                AttachmentPreview(
                    attachment = attachment,
                    isUser = isUser,
                    accentColor = model.brandColor
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            // Show reasoning/thinking section if available (for AI messages)
            if (!isUser && message.hasReasoning) {
                ReasoningSection(
                    reasoning = message.reasoning!!,
                    isExpanded = isReasoningExpanded,
                    onToggle = { isReasoningExpanded = !isReasoningExpanded },
                    accentColor = model.brandColor
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Message content
            if (message.content.isNotBlank() || message.isStreaming) {
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    ),
                    color = if (isUser) model.brandColor else colors.surfaceElevated
                ) {
                    if (message.isStreaming) {
                        StreamingText(
                            text = message.content,
                            textColor = if (isUser) Color.White else colors.textPrimary,
                            cursorColor = model.brandColor,
                            modifier = Modifier.padding(12.dp)
                        )
                    } else {
                        Text(
                            text = message.content,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isUser) Color.White else colors.textPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StreamingText(
    text: String,
    textColor: Color,
    cursorColor: Color,
    modifier: Modifier = Modifier
) {
    // Track the last known text length to detect new content
    var lastTextLength by remember { mutableIntStateOf(0) }
    var displayedLength by remember { mutableIntStateOf(0) }
    
    // When new text arrives, we need to animate from current displayed to new length
    LaunchedEffect(text) {
        // If text grew, animate the new characters
        while (displayedLength < text.length) {
            displayedLength++
            delay(0L) // 12ms per character = ~83 chars/sec typing speed
        }
        lastTextLength = text.length
    }
    
    // Blinking cursor animation
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(530),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )
    
    val displayedText = text.take(displayedLength.coerceAtMost(text.length))
    
    Text(
        text = buildAnnotatedString {
            append(displayedText)
            withStyle(SpanStyle(color = cursorColor.copy(alpha = cursorAlpha))) {
                append("▌")
            }
        },
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        color = textColor
    )
}

@Composable
private fun ReasoningSection(
    reasoning: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    accentColor: Color
) {
    val colors = UnifiedTheme.colors
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = accentColor.copy(alpha = 0.08f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "💭",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "Thinking",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = accentColor
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = reasoning,
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun AttachmentPreview(
    attachment: MessageAttachment,
    isUser: Boolean,
    accentColor: Color
) {
    val colors = UnifiedTheme.colors
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isUser) accentColor.copy(alpha = 0.8f) else colors.surfaceElevated
    ) {
        when (attachment.type) {
            AttachmentType.IMAGE -> {
                AsyncImage(
                    model = attachment.uri,
                    contentDescription = attachment.name,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
            AttachmentType.PDF -> {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📄",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Column {
                        Text(
                            text = attachment.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isUser) Color.White else colors.textPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = formatFileSize(attachment.size),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isUser) Color.White.copy(alpha = 0.7f) else colors.textMuted
                        )
                    }
                }
            }
            AttachmentType.FILE -> {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📎",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = attachment.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isUser) Color.White else colors.textPrimary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> "${bytes / (1024 * 1024)} MB"
    }
}
