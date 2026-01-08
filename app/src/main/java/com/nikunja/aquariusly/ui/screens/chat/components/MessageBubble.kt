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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nikunja.aquariusly.domain.model.AIModel
import com.nikunja.aquariusly.domain.model.AttachmentType
import com.nikunja.aquariusly.domain.model.Message
import com.nikunja.aquariusly.domain.model.MessageAttachment
import com.nikunja.aquariusly.ui.components.markdown.Markdown
import com.nikunja.aquariusly.ui.theme.UnifiedTheme

@Composable
fun MessageBubble(
    message: Message,
    model: AIModel
) {
    val colors = UnifiedTheme.colors
    val isUser = message.isFromUser
    var isReasoningExpanded by remember { mutableStateOf(false) }
    
    if (isUser) {
        // User message - right aligned with max width
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Column(
                modifier = Modifier.widthIn(max = 300.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Attachments
                message.attachments.forEach { attachment ->
                    AttachmentPreview(
                        attachment = attachment,
                        isUser = true,
                        accentColor = model.brandColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                // Message content
                if (message.content.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 4.dp
                        ),
                        color = model.brandColor
                    ) {
                        Text(
                            text = message.content,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    } else {
        // AI message - full width layout
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header row: Icon + Thinking (if has reasoning)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Model icon
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = model.iconRes),
                        contentDescription = model.name,
                        modifier = Modifier.size(28.dp),
                        tint = Color.Unspecified
                    )
                }
                
                // Thinking section inline with icon
                if (message.hasReasoning) {
                    Spacer(modifier = Modifier.width(8.dp))
                    ReasoningHeader(
                        reasoning = message.reasoning!!,
                        isExpanded = isReasoningExpanded,
                        onToggle = { isReasoningExpanded = !isReasoningExpanded },
                        accentColor = model.brandColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Attachments
            message.attachments.forEach { attachment ->
                AttachmentPreview(
                    attachment = attachment,
                    isUser = false,
                    accentColor = model.brandColor
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            // Message content - full width
            if (message.content.isNotBlank() || message.isStreaming) {
                if (message.isStreaming) {
                    StreamingContent(
                        text = message.content,
                        textColor = colors.textPrimary,
                        cursorColor = model.brandColor
                    )
                } else {
                    Markdown(
                        content = message.content,
                        textColor = colors.textPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun ReasoningHeader(
    reasoning: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val colors = UnifiedTheme.colors
    
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = accentColor.copy(alpha = 0.08f),
        modifier = modifier
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
                    Text(text = "💭", style = MaterialTheme.typography.labelMedium)
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
private fun StreamingContent(
    text: String,
    textColor: Color,
    cursorColor: Color,
    modifier: Modifier = Modifier
) {
    var displayedLength by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(text) {
        while (displayedLength < text.length) {
            displayedLength++
            kotlinx.coroutines.delay(0L)
        }
    }
    
    val displayedText = text.take(displayedLength.coerceAtMost(text.length))
    
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
    
    Column(modifier = modifier) {
        if (displayedText.isNotEmpty()) {
            Markdown(
                content = displayedText,
                textColor = textColor
            )
        }
        
        Text(
            text = "▌",
            color = cursorColor.copy(alpha = cursorAlpha),
            style = MaterialTheme.typography.bodyMedium
        )
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
                    Text(text = "📄", style = MaterialTheme.typography.titleMedium)
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
                    Text(text = "📎", style = MaterialTheme.typography.titleMedium)
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
