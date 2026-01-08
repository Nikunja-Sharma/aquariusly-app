package com.nikunja.aquariusly.ui.components.markdown.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nikunja.aquariusly.ui.components.markdown.LocalMarkdownTheme
import com.nikunja.aquariusly.ui.components.markdown.MarkdownElement
import com.nikunja.aquariusly.ui.components.markdown.TableAlignment

@Composable
fun MdTable(
    headers: List<MarkdownElement.TableCellElement>,
    rows: List<List<MarkdownElement.TableCellElement>>,
    alignments: List<TableAlignment>,
    modifier: Modifier = Modifier
) {
    val theme = LocalMarkdownTheme.current
    val borderColor = theme.colors.tableBorder
    val columnCount = headers.size
    
    // Calculate column widths based on content
    val columnWidths = remember(headers, rows) {
        List(columnCount) { 140.dp } // Fixed width per column
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .border(1.dp, borderColor)
    ) {
        Column {
            // Header row
            TableRowWithDividers(
                cells = headers,
                columnWidths = columnWidths,
                borderColor = borderColor,
                backgroundColor = theme.colors.tableHeaderBackground
            )
            
            // Data rows
            rows.forEachIndexed { index, row ->
                TableRowWithDividers(
                    cells = row,
                    columnWidths = columnWidths,
                    borderColor = borderColor,
                    backgroundColor = if (index % 2 == 0) 
                        theme.colors.tableRowEvenBackground 
                    else 
                        Color.Transparent
                )
            }
        }
    }
}

@Composable
private fun TableRowWithDividers(
    cells: List<MarkdownElement.TableCellElement>,
    columnWidths: List<Dp>,
    borderColor: Color,
    backgroundColor: Color
) {
    val theme = LocalMarkdownTheme.current
    
    Row(
        modifier = Modifier
            .background(backgroundColor)
            .drawBehind {
                // Draw bottom border
                drawLine(
                    color = borderColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
                
                // Draw vertical dividers
                var xOffset = 0f
                columnWidths.forEachIndexed { index, width ->
                    if (index > 0) {
                        drawLine(
                            color = borderColor,
                            start = Offset(xOffset, 0f),
                            end = Offset(xOffset, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    xOffset += width.toPx()
                }
            }
    ) {
        cells.forEachIndexed { index, cell ->
            Box(
                modifier = Modifier
                    .width(columnWidths.getOrElse(index) { 100.dp })
                    .padding(theme.spacing.tableCellPadding)
            ) {
                MdInlineContent(elements = cell.content)
            }
        }
    }
}
