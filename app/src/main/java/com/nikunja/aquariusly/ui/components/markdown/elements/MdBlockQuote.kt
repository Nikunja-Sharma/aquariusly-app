package com.nikunja.aquariusly.ui.components.markdown.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nikunja.aquariusly.ui.components.markdown.LocalMarkdownTheme
import com.nikunja.aquariusly.ui.components.markdown.MarkdownElement

@Composable
fun MdBlockQuote(
    content: List<MarkdownElement>,
    modifier: Modifier = Modifier,
    renderElement: @Composable (MarkdownElement) -> Unit
) {
    val theme = LocalMarkdownTheme.current
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(theme.colors.blockQuoteBackground)
    ) {
        // Left border
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(theme.colors.blockQuoteBorder)
        )
        
        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(theme.spacing.blockQuotePadding)
        ) {
            content.forEach { element ->
                renderElement(element)
            }
        }
    }
}
