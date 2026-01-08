package com.nikunja.aquariusly.ui.components.markdown.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nikunja.aquariusly.ui.components.markdown.LocalMarkdownTheme

@Composable
fun MdHorizontalRule(
    modifier: Modifier = Modifier
) {
    val theme = LocalMarkdownTheme.current
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(1.dp)
            .background(theme.colors.horizontalRule)
    )
}
