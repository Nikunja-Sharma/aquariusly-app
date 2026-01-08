package com.nikunja.aquariusly.ui.components.markdown

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.nikunja.aquariusly.ui.components.markdown.elements.*
import com.nikunja.aquariusly.ui.theme.UnifiedTheme

/**
 * Main Markdown composable that renders markdown content
 */
@Composable
fun Markdown(
    content: String,
    modifier: Modifier = Modifier,
    textColor: Color = UnifiedTheme.colors.textPrimary
) {
    val colors = UnifiedTheme.colors
    
    val theme = remember(textColor, colors.isDark) {
        defaultMarkdownTheme(
            textColor = textColor,
            linkColor = colors.accentBlue,
            codeBackground = colors.backgroundSecondary,
            isDark = colors.isDark
        )
    }
    
    val elements = remember(content) {
        MarkdownParser.parse(content)
    }
    
    CompositionLocalProvider(LocalMarkdownTheme provides theme) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(theme.spacing.paragraphSpacing)
        ) {
            elements.forEach { element ->
                MarkdownElementRenderer(element)
            }
        }
    }
}

@Composable
private fun MarkdownElementRenderer(element: MarkdownElement) {
    val theme = LocalMarkdownTheme.current
    
    when (element) {
        is MarkdownElement.HeadingElement -> {
            MdHeading(
                level = element.level,
                content = element.content
            )
        }
        
        is MarkdownElement.ParagraphElement -> {
            MdInlineContent(elements = element.content)
        }
        
        is MarkdownElement.CodeBlockElement -> {
            MdCodeBlock(
                code = element.code,
                language = element.language
            )
        }
        
        is MarkdownElement.ListElement -> {
            MdList(
                items = element.items,
                isOrdered = element.isOrdered,
                startNumber = element.startNumber,
                renderElement = { MarkdownElementRenderer(it) }
            )
        }
        
        is MarkdownElement.BlockQuoteElement -> {
            MdBlockQuote(
                content = element.content,
                renderElement = { MarkdownElementRenderer(it) }
            )
        }
        
        is MarkdownElement.TableElement -> {
            MdTable(
                headers = element.headers,
                rows = element.rows,
                alignments = element.alignments
            )
        }
        
        is MarkdownElement.HorizontalRuleElement -> {
            MdHorizontalRule()
        }
        
        is MarkdownElement.ImageElement -> {
            MdImage(
                url = element.url,
                altText = element.altText
            )
        }
        
        // Handle inline elements that might appear at block level
        is MarkdownElement.TextElement -> {
            MdInlineContent(elements = listOf(element))
        }
        
        is MarkdownElement.LinkElement -> {
            MdInlineContent(elements = listOf(element))
        }
        
        else -> {}
    }
}
