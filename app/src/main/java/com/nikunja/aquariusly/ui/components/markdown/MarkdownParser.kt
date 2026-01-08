package com.nikunja.aquariusly.ui.components.markdown

import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.node.*
import org.commonmark.parser.Parser

/**
 * Sealed class representing parsed markdown elements
 */
sealed class MarkdownElement {
    data class TextElement(
        val text: String,
        val isBold: Boolean = false,
        val isItalic: Boolean = false,
        val isStrikethrough: Boolean = false,
        val isCode: Boolean = false
    ) : MarkdownElement()
    
    data class LinkElement(
        val text: String,
        val url: String
    ) : MarkdownElement()
    
    data class HeadingElement(
        val level: Int,
        val content: List<MarkdownElement>
    ) : MarkdownElement()
    
    data class ParagraphElement(
        val content: List<MarkdownElement>
    ) : MarkdownElement()
    
    data class CodeBlockElement(
        val code: String,
        val language: String?
    ) : MarkdownElement()
    
    data class ListElement(
        val items: List<ListItemElement>,
        val isOrdered: Boolean,
        val startNumber: Int = 1
    ) : MarkdownElement()
    
    data class ListItemElement(
        val content: List<MarkdownElement>
    ) : MarkdownElement()
    
    data class BlockQuoteElement(
        val content: List<MarkdownElement>
    ) : MarkdownElement()
    
    data class TableElement(
        val headers: List<TableCellElement>,
        val rows: List<List<TableCellElement>>,
        val alignments: List<TableAlignment>
    ) : MarkdownElement()
    
    data class TableCellElement(
        val content: List<MarkdownElement>
    ) : MarkdownElement()
    
    data class HorizontalRuleElement(val id: Int = 0) : MarkdownElement()
    
    data class ImageElement(
        val url: String,
        val altText: String
    ) : MarkdownElement()
}

enum class TableAlignment { LEFT, CENTER, RIGHT }

/**
 * Parser that converts markdown string to list of MarkdownElements
 */
object MarkdownParser {
    private val parser: Parser by lazy {
        Parser.builder()
            .extensions(listOf(
                TablesExtension.create(),
                StrikethroughExtension.create(),
                AutolinkExtension.create()
            ))
            .build()
    }
    
    fun parse(markdown: String): List<MarkdownElement> {
        val normalized = markdown
            .replace("\\n", "\n")
            .replace("\\t", "\t")
        val document = parser.parse(normalized)
        return parseNode(document)
    }
    
    private fun parseNode(node: Node): List<MarkdownElement> {
        val elements = mutableListOf<MarkdownElement>()
        var child = node.firstChild
        
        while (child != null) {
            parseElement(child)?.let { elements.add(it) }
            child = child.next
        }
        
        return elements
    }
    
    private fun parseElement(node: Node): MarkdownElement? {
        return when (node) {
            is Heading -> MarkdownElement.HeadingElement(
                level = node.level,
                content = parseInlineContent(node)
            )
            is Paragraph -> MarkdownElement.ParagraphElement(
                content = parseInlineContent(node)
            )
            is FencedCodeBlock -> MarkdownElement.CodeBlockElement(
                code = node.literal.trimEnd(),
                language = node.info.takeIf { it.isNotBlank() }
            )
            is IndentedCodeBlock -> MarkdownElement.CodeBlockElement(
                code = node.literal.trimEnd(),
                language = null
            )
            is BulletList -> MarkdownElement.ListElement(
                items = parseListItems(node),
                isOrdered = false
            )
            is OrderedList -> MarkdownElement.ListElement(
                items = parseListItems(node),
                isOrdered = true,
                startNumber = node.startNumber
            )
            is BlockQuote -> MarkdownElement.BlockQuoteElement(
                content = parseNode(node)
            )
            is ThematicBreak -> MarkdownElement.HorizontalRuleElement()
            is org.commonmark.ext.gfm.tables.TableBlock -> parseTable(node)
            else -> null
        }
    }
    
    private fun parseListItems(list: Node): List<MarkdownElement.ListItemElement> {
        val items = mutableListOf<MarkdownElement.ListItemElement>()
        var child = list.firstChild
        
        while (child != null) {
            if (child is ListItem) {
                items.add(MarkdownElement.ListItemElement(content = parseNode(child)))
            }
            child = child.next
        }
        
        return items
    }
    
    private fun parseTable(table: Node): MarkdownElement.TableElement {
        val headers = mutableListOf<MarkdownElement.TableCellElement>()
        val rows = mutableListOf<List<MarkdownElement.TableCellElement>>()
        val alignments = mutableListOf<TableAlignment>()
        
        var child = table.firstChild
        while (child != null) {
            when (child) {
                is org.commonmark.ext.gfm.tables.TableHead -> {
                    var row = child.firstChild
                    while (row != null) {
                        if (row is org.commonmark.ext.gfm.tables.TableRow) {
                            var cell = row.firstChild
                            while (cell != null) {
                                if (cell is org.commonmark.ext.gfm.tables.TableCell) {
                                    headers.add(MarkdownElement.TableCellElement(parseInlineContent(cell)))
                                    alignments.add(when (cell.alignment) {
                                        org.commonmark.ext.gfm.tables.TableCell.Alignment.CENTER -> TableAlignment.CENTER
                                        org.commonmark.ext.gfm.tables.TableCell.Alignment.RIGHT -> TableAlignment.RIGHT
                                        else -> TableAlignment.LEFT
                                    })
                                }
                                cell = cell.next
                            }
                        }
                        row = row.next
                    }
                }
                is org.commonmark.ext.gfm.tables.TableBody -> {
                    var row = child.firstChild
                    while (row != null) {
                        if (row is org.commonmark.ext.gfm.tables.TableRow) {
                            val rowCells = mutableListOf<MarkdownElement.TableCellElement>()
                            var cell = row.firstChild
                            while (cell != null) {
                                if (cell is org.commonmark.ext.gfm.tables.TableCell) {
                                    rowCells.add(MarkdownElement.TableCellElement(parseInlineContent(cell)))
                                }
                                cell = cell.next
                            }
                            rows.add(rowCells)
                        }
                        row = row.next
                    }
                }
            }
            child = child.next
        }
        
        return MarkdownElement.TableElement(headers, rows, alignments)
    }
    
    private fun parseInlineContent(node: Node): List<MarkdownElement> {
        val elements = mutableListOf<MarkdownElement>()
        var child = node.firstChild
        
        while (child != null) {
            elements.addAll(parseInlineNode(child, InlineStyle()))
            child = child.next
        }
        
        return elements
    }
    
    private data class InlineStyle(
        val bold: Boolean = false,
        val italic: Boolean = false,
        val strikethrough: Boolean = false,
        val code: Boolean = false
    )
    
    private fun parseInlineNode(node: Node, style: InlineStyle): List<MarkdownElement> {
        return when (node) {
            is Text -> listOf(MarkdownElement.TextElement(
                text = node.literal,
                isBold = style.bold,
                isItalic = style.italic,
                isStrikethrough = style.strikethrough,
                isCode = style.code
            ))
            is Code -> listOf(MarkdownElement.TextElement(
                text = node.literal,
                isCode = true
            ))
            is StrongEmphasis -> {
                val newStyle = style.copy(bold = true)
                collectChildInlines(node, newStyle)
            }
            is Emphasis -> {
                val newStyle = style.copy(italic = true)
                collectChildInlines(node, newStyle)
            }
            is org.commonmark.ext.gfm.strikethrough.Strikethrough -> {
                val newStyle = style.copy(strikethrough = true)
                collectChildInlines(node, newStyle)
            }
            is Link -> listOf(MarkdownElement.LinkElement(
                text = getTextContent(node),
                url = node.destination
            ))
            is Image -> listOf(MarkdownElement.ImageElement(
                url = node.destination,
                altText = getTextContent(node)
            ))
            is SoftLineBreak -> listOf(MarkdownElement.TextElement(" "))
            is HardLineBreak -> listOf(MarkdownElement.TextElement("\n"))
            else -> collectChildInlines(node, style)
        }
    }
    
    private fun collectChildInlines(node: Node, style: InlineStyle): List<MarkdownElement> {
        val elements = mutableListOf<MarkdownElement>()
        var child = node.firstChild
        while (child != null) {
            elements.addAll(parseInlineNode(child, style))
            child = child.next
        }
        return elements
    }
    
    private fun getTextContent(node: Node): String {
        val sb = StringBuilder()
        var child = node.firstChild
        while (child != null) {
            if (child is Text) sb.append(child.literal)
            else sb.append(getTextContent(child))
            child = child.next
        }
        return sb.toString()
    }
}
