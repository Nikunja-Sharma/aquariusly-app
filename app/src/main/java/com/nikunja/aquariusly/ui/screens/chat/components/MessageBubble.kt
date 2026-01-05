package com.nikunja.aquariusly.ui.screens.chat.components

import android.graphics.Typeface
import android.webkit.WebView
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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import com.nikunja.aquariusly.domain.model.AIModel
import com.nikunja.aquariusly.domain.model.AttachmentType
import com.nikunja.aquariusly.domain.model.Message
import com.nikunja.aquariusly.domain.model.MessageAttachment
import com.nikunja.aquariusly.ui.theme.UnifiedTheme
import kotlinx.coroutines.delay
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor

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
                        StreamingMarkdownText(
                            text = message.content,
                            textColor = if (isUser) Color.White else colors.textPrimary,
                            cursorColor = model.brandColor,
                            modifier = Modifier.padding(12.dp)
                        )
                    } else if (isUser) {
                        // Plain text for user messages
                        Text(
                            text = message.content,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    } else {
                        // Markdown for AI messages
                        MarkdownContent(
                            content = message.content,
                            textColor = colors.textPrimary,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MarkdownContent(
    content: String,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val colors = UnifiedTheme.colors
    
    // Normalize content: convert escaped newlines to real newlines for proper markdown parsing
    val normalizedContent = remember(content) { normalizeMarkdownContent(content) }
    
    // Check if content has tables
    val hasTable = normalizedContent.contains(Regex("\\|.*\\|.*\\n\\|[-:| ]+\\|"))
    
    // Check if content has LaTeX equations
    val hasLatex = normalizedContent.contains("\\$\\$".toRegex()) || 
                   normalizedContent.contains("\\\\\\[".toRegex()) ||
                   normalizedContent.contains("\\\\\\(".toRegex())
    
    if (hasTable) {
        // Render with WebView for proper table support
        MarkdownWebView(
            content = normalizedContent,
            textColor = textColor,
            backgroundColor = colors.surfaceElevated,
            modifier = modifier
        )
    } else if (hasLatex) {
        // Render with LaTeX support
        MarkdownWithLatex(
            content = normalizedContent,
            textColor = textColor,
            modifier = modifier
        )
    } else {
        // Standard markdown with GFM
        val mdColors = markdownColor(
            text = textColor,
            codeText = colors.textPrimary,
            codeBackground = colors.backgroundSecondary,
            dividerColor = colors.borderDefault,
            linkText = colors.accentBlue
        )
        
        val mdTypography = markdownTypography(
            h1 = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = textColor),
            h2 = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = textColor),
            h3 = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = textColor),
            h4 = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, color = textColor),
            h5 = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium, color = textColor),
            h6 = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium, color = textColor),
            text = MaterialTheme.typography.bodyMedium.copy(color = textColor),
            code = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp, color = colors.textPrimary, background = colors.backgroundSecondary),
            paragraph = MaterialTheme.typography.bodyMedium.copy(color = textColor),
            ordered = MaterialTheme.typography.bodyMedium.copy(color = textColor),
            bullet = MaterialTheme.typography.bodyMedium.copy(color = textColor),
            list = MaterialTheme.typography.bodyMedium.copy(color = textColor)
        )
        
        Markdown(
            content = normalizedContent,
            colors = mdColors,
            typography = mdTypography,
            flavour = GFMFlavourDescriptor(),
            modifier = modifier
        )
    }
}

@Composable
private fun MarkdownWebView(
    content: String,
    textColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    val textColorHex = String.format("#%06X", 0xFFFFFF and textColor.toArgb())
    
    // Escape content for JavaScript
    val escapedContent = content
        .replace("\\", "\\\\")
        .replace("`", "\\`")
        .replace("$", "\\$")
        .replace("\n", "\\n")
    
    val html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <script src="https://cdn.jsdelivr.net/npm/marked/marked.min.js"></script>
            <style>
                * { box-sizing: border-box; margin: 0; padding: 0; }
                body { 
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    font-size: 14px;
                    line-height: 1.5;
                    color: $textColorHex;
                    background: transparent;
                    padding: 0;
                }
                .table-wrapper {
                    overflow-x: auto;
                    margin: 8px 0;
                    -webkit-overflow-scrolling: touch;
                }
                table { 
                    border-collapse: collapse; 
                    min-width: 100%;
                    font-size: 12px;
                    white-space: nowrap;
                }
                th, td { 
                    border: 1px solid ${textColorHex}40;
                    padding: 6px 10px;
                    text-align: left;
                }
                th { 
                    background: ${textColorHex}10;
                    font-weight: 600;
                }
                tr:nth-child(even) { background: ${textColorHex}05; }
                code { 
                    background: ${textColorHex}15;
                    padding: 2px 4px;
                    border-radius: 3px;
                    font-family: monospace;
                    font-size: 12px;
                }
                pre { 
                    background: ${textColorHex}10;
                    padding: 8px;
                    border-radius: 4px;
                    overflow-x: auto;
                }
                pre code { background: none; padding: 0; }
                p { margin: 4px 0; }
                h1, h2, h3, h4, h5, h6 { margin: 8px 0 4px 0; }
                ul, ol { padding-left: 20px; margin: 4px 0; }
                blockquote { 
                    border-left: 3px solid ${textColorHex}40;
                    padding-left: 12px;
                    margin: 8px 0;
                    color: ${textColorHex}90;
                }
                a { color: #4A90D9; }
                strong { font-weight: 600; }
            </style>
        </head>
        <body>
            <div id="content"></div>
            <script>
                marked.setOptions({ gfm: true, breaks: true });
                
                // Parse and render
                var html = marked.parse(`$escapedContent`);
                
                // Wrap tables in scrollable div
                html = html.replace(/<table>/g, '<div class="table-wrapper"><table>');
                html = html.replace(/<\/table>/g, '</table></div>');
                
                document.getElementById('content').innerHTML = html;
            </script>
        </body>
        </html>
    """.trimIndent()
    
    var webViewHeight by remember { mutableStateOf(300.dp) }
    val density = LocalContext.current.resources.displayMetrics.density
    
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = false
                settings.domStorageEnabled = true
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                
                webViewClient = object : android.webkit.WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        view?.evaluateJavascript(
                            "(function() { return document.body.scrollHeight; })();"
                        ) { height ->
                            height?.replace("\"", "")?.toFloatOrNull()?.let { h ->
                                webViewHeight = (h / density + 16).dp
                            }
                        }
                    }
                }
                
                loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(webViewHeight.coerceIn(50.dp, 800.dp))
    )
}

/**
 * Normalize markdown content by converting escaped sequences to actual characters.
 * This handles cases where AI returns literal \n instead of actual newlines.
 */
private fun normalizeMarkdownContent(content: String): String {
    return content
        .replace("\\n", "\n")  // Escaped newline to real newline
        .replace("\\t", "\t")  // Escaped tab to real tab
        .replace("\\r", "\r")  // Escaped carriage return
}

@Composable
private fun MarkdownWithLatex(
    content: String,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val colors = UnifiedTheme.colors
    val isDark = colors.isDark
    val bgColor = colors.surfaceElevated
    
    // Normalize content first
    val normalizedContent = remember(content) { normalizeMarkdownContent(content) }
    
    // Parse content into segments (text vs latex)
    val segments = remember(normalizedContent) { parseLatexSegments(normalizedContent) }
    
    Column(modifier = modifier) {
        segments.forEach { segment ->
            when (segment) {
                is ContentSegment.Text -> {
                    if (segment.content.isNotBlank()) {
                        val mdColors = markdownColor(
                            text = textColor,
                            codeText = colors.textPrimary,
                            codeBackground = colors.backgroundSecondary,
                            dividerColor = colors.borderDefault,
                            linkText = colors.accentBlue
                        )
                        
                        val mdTypography = markdownTypography(
                            h1 = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = textColor),
                            h2 = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = textColor),
                            h3 = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = textColor),
                            h4 = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, color = textColor),
                            h5 = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium, color = textColor),
                            h6 = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium, color = textColor),
                            text = MaterialTheme.typography.bodyMedium.copy(color = textColor),
                            code = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp, color = colors.textPrimary),
                            paragraph = MaterialTheme.typography.bodyMedium.copy(color = textColor),
                            ordered = MaterialTheme.typography.bodyMedium.copy(color = textColor),
                            bullet = MaterialTheme.typography.bodyMedium.copy(color = textColor),
                            list = MaterialTheme.typography.bodyMedium.copy(color = textColor)
                        )
                        
                        Markdown(
                            content = segment.content,
                            colors = mdColors,
                            typography = mdTypography,
                            flavour = GFMFlavourDescriptor()
                        )
                    }
                }
                is ContentSegment.Latex -> {
                    LatexView(
                        latex = segment.content,
                        isBlock = segment.isBlock,
                        textColor = textColor,
                        bgColor = bgColor,
                        isDark = isDark
                    )
                }
            }
        }
    }
}

@Composable
private fun LatexView(
    latex: String,
    isBlock: Boolean,
    textColor: Color,
    bgColor: Color,
    isDark: Boolean
) {
    val textColorHex = String.format("#%06X", 0xFFFFFF and textColor.toArgb())
    val bgColorHex = String.format("#%06X", 0xFFFFFF and bgColor.toArgb())
    
    val html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
            <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/katex.min.css">
            <script src="https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/katex.min.js"></script>
            <style>
                body { 
                    margin: 0; 
                    padding: ${if (isBlock) "8px" else "0"}; 
                    background: transparent;
                    color: $textColorHex;
                    display: flex;
                    justify-content: ${if (isBlock) "center" else "flex-start"};
                    align-items: center;
                }
                .katex { font-size: ${if (isBlock) "1.1em" else "1em"}; color: $textColorHex; }
                .katex-display { margin: 0; }
            </style>
        </head>
        <body>
            <div id="math"></div>
            <script>
                katex.render("${latex.replace("\\", "\\\\").replace("\"", "\\\"")}", document.getElementById("math"), {
                    throwOnError: false,
                    displayMode: $isBlock
                });
            </script>
        </body>
        </html>
    """.trimIndent()
    
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
            }
        },
        modifier = Modifier
            .then(if (isBlock) Modifier.fillMaxWidth() else Modifier.wrapContentWidth())
            .height(if (isBlock) 60.dp else 24.dp)
    )
}

private sealed class ContentSegment {
    data class Text(val content: String) : ContentSegment()
    data class Latex(val content: String, val isBlock: Boolean) : ContentSegment()
}

private fun parseLatexSegments(content: String): List<ContentSegment> {
    val segments = mutableListOf<ContentSegment>()
    var remaining = content
    
    // Patterns for LaTeX: $$...$$ (block), $...$ (inline), \[...\] (block), \(...\) (inline)
    val blockPattern = "\\$\\$([^$]+)\\$\\$|\\\\\\[([^\\]]+)\\\\\\]".toRegex()
    val inlinePattern = "(?<!\\$)\\$(?!\\$)([^$]+)\\$(?!\\$)|\\\\\\(([^)]+)\\\\\\)".toRegex()
    
    while (remaining.isNotEmpty()) {
        val blockMatch = blockPattern.find(remaining)
        val inlineMatch = inlinePattern.find(remaining)
        
        val nextMatch = listOfNotNull(blockMatch, inlineMatch)
            .minByOrNull { it.range.first }
        
        if (nextMatch == null) {
            segments.add(ContentSegment.Text(remaining))
            break
        }
        
        // Add text before the match
        if (nextMatch.range.first > 0) {
            segments.add(ContentSegment.Text(remaining.substring(0, nextMatch.range.first)))
        }
        
        // Add latex segment
        val isBlock = nextMatch == blockMatch
        val latexContent = nextMatch.groupValues.drop(1).firstOrNull { it.isNotEmpty() } ?: ""
        segments.add(ContentSegment.Latex(latexContent, isBlock))
        
        remaining = remaining.substring(nextMatch.range.last + 1)
    }
    
    return segments
}

@Composable
private fun StreamingMarkdownText(
    text: String,
    textColor: Color,
    cursorColor: Color,
    modifier: Modifier = Modifier
) {
    val colors = UnifiedTheme.colors
    
    // Normalize content for proper markdown parsing
    val normalizedText = remember(text) { normalizeMarkdownContent(text) }
    
    // Track displayed length for typing animation
    var displayedLength by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(normalizedText) {
        while (displayedLength < normalizedText.length) {
            displayedLength++
            delay(0L)
        }
    }
    
    val displayedText = normalizedText.take(displayedLength.coerceAtMost(normalizedText.length))
    
    // Blinking cursor
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
        val mdColors = markdownColor(
            text = textColor,
            codeText = colors.textPrimary,
            codeBackground = colors.backgroundSecondary,
            dividerColor = colors.borderDefault,
            linkText = colors.accentBlue
        )
        
        val mdTypography = markdownTypography(
            h1 = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = textColor),
            h2 = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = textColor),
            h3 = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = textColor),
            h4 = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, color = textColor),
            h5 = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium, color = textColor),
            h6 = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium, color = textColor),
            text = MaterialTheme.typography.bodyMedium.copy(color = textColor),
            code = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp, color = colors.textPrimary),
            paragraph = MaterialTheme.typography.bodyMedium.copy(color = textColor),
            ordered = MaterialTheme.typography.bodyMedium.copy(color = textColor),
            bullet = MaterialTheme.typography.bodyMedium.copy(color = textColor),
            list = MaterialTheme.typography.bodyMedium.copy(color = textColor)
        )
        
        if (displayedText.isNotEmpty()) {
            Markdown(
                content = displayedText,
                colors = mdColors,
                typography = mdTypography,
                flavour = GFMFlavourDescriptor()
            )
        }
        
        // Cursor
        Text(
            text = "▌",
            color = cursorColor.copy(alpha = cursorAlpha),
            style = MaterialTheme.typography.bodyMedium
        )
    }
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
