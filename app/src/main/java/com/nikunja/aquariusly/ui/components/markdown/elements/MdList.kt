package com.nikunja.aquariusly.ui.components.markdown.elements

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nikunja.aquariusly.ui.components.markdown.LocalMarkdownTheme
import com.nikunja.aquariusly.ui.components.markdown.MarkdownElement

@Composable
fun MdList(
    items: List<MarkdownElement.ListItemElement>,
    isOrdered: Boolean,
    startNumber: Int,
    modifier: Modifier = Modifier,
    renderElement: @Composable (MarkdownElement) -> Unit
) {
    val theme = LocalMarkdownTheme.current
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(theme.spacing.listItemSpacing)
    ) {
        items.forEachIndexed { index, item ->
            Row(modifier = Modifier.fillMaxWidth()) {
                // Bullet or number
                Text(
                    text = if (isOrdered) "${startNumber + index}." else "•",
                    style = theme.typography.body,
                    modifier = Modifier.width(24.dp)
                )
                
                // Item content
                Column(modifier = Modifier.weight(1f)) {
                    item.content.forEach { element ->
                        renderElement(element)
                    }
                }
            }
        }
    }
}
