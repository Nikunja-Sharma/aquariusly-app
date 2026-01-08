package com.nikunja.aquariusly.ui.components.markdown.elements

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.nikunja.aquariusly.ui.components.markdown.LocalMarkdownTheme
import com.nikunja.aquariusly.ui.components.markdown.MarkdownElement

@Composable
fun MdHeading(
    level: Int,
    content: List<MarkdownElement>,
    modifier: Modifier = Modifier
) {
    val theme = LocalMarkdownTheme.current
    
    val style = when (level) {
        1 -> theme.typography.h1
        2 -> theme.typography.h2
        3 -> theme.typography.h3
        4 -> theme.typography.h4
        5 -> theme.typography.h5
        else -> theme.typography.h6
    }
    
    val annotatedString = buildAnnotatedString {
        content.forEach { element ->
            when (element) {
                is MarkdownElement.TextElement -> {
                    withStyle(
                        style.toSpanStyle().copy(
                            fontWeight = if (element.isBold) FontWeight.Bold else style.fontWeight,
                            fontStyle = if (element.isItalic) FontStyle.Italic else null,
                            textDecoration = if (element.isStrikethrough) TextDecoration.LineThrough else null,
                            fontFamily = if (element.isCode) FontFamily.Monospace else null
                        )
                    ) {
                        append(element.text)
                    }
                }
                is MarkdownElement.LinkElement -> {
                    withStyle(style.toSpanStyle().copy(
                        color = theme.colors.link,
                        textDecoration = TextDecoration.Underline
                    )) {
                        append(element.text)
                    }
                }
                else -> {}
            }
        }
    }
    
    Text(
        text = annotatedString,
        style = style,
        modifier = modifier.padding(vertical = 4.dp)
    )
}
