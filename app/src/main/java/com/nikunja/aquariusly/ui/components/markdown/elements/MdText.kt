package com.nikunja.aquariusly.ui.components.markdown.elements

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.nikunja.aquariusly.ui.components.markdown.LocalMarkdownTheme
import com.nikunja.aquariusly.ui.components.markdown.MarkdownElement

/**
 * Renders inline markdown content (text, bold, italic, code, links)
 */
@Composable
fun MdInlineContent(
    elements: List<MarkdownElement>,
    modifier: Modifier = Modifier
) {
    val theme = LocalMarkdownTheme.current
    val uriHandler = LocalUriHandler.current
    
    val annotatedString = buildAnnotatedString {
        elements.forEach { element ->
            when (element) {
                is MarkdownElement.TextElement -> {
                    val style = SpanStyle(
                        fontWeight = if (element.isBold) FontWeight.Bold else null,
                        fontStyle = if (element.isItalic) FontStyle.Italic else null,
                        textDecoration = if (element.isStrikethrough) TextDecoration.LineThrough else null,
                        fontFamily = if (element.isCode) FontFamily.Monospace else null,
                        background = if (element.isCode) theme.colors.codeBackground else androidx.compose.ui.graphics.Color.Unspecified
                    )
                    withStyle(style) {
                        append(element.text)
                    }
                }
                is MarkdownElement.LinkElement -> {
                    pushStringAnnotation(tag = "URL", annotation = element.url)
                    withStyle(SpanStyle(
                        color = theme.colors.link,
                        textDecoration = TextDecoration.Underline
                    )) {
                        append(element.text)
                    }
                    pop()
                }
                else -> {}
            }
        }
    }
    
    ClickableText(
        text = annotatedString,
        style = theme.typography.body,
        modifier = modifier,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    uriHandler.openUri(annotation.item)
                }
        }
    )
}
