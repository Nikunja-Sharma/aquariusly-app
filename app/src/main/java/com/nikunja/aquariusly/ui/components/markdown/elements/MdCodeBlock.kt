package com.nikunja.aquariusly.ui.components.markdown.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nikunja.aquariusly.ui.components.markdown.LocalMarkdownTheme

@Composable
fun MdCodeBlock(
    code: String,
    language: String?,
    modifier: Modifier = Modifier
) {
    val theme = LocalMarkdownTheme.current
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(theme.colors.codeBackground)
    ) {
        // Language label
        if (!language.isNullOrBlank()) {
            Text(
                text = language,
                style = theme.typography.code.copy(
                    color = theme.colors.text.copy(alpha = 0.6f)
                ),
                modifier = Modifier.padding(
                    start = theme.spacing.codeBlockPadding,
                    top = 8.dp,
                    end = theme.spacing.codeBlockPadding
                )
            )
        }
        
        // Code content with horizontal scroll
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(theme.spacing.codeBlockPadding)
        ) {
            Text(
                text = code,
                style = theme.typography.codeBlock
            )
        }
    }
}
