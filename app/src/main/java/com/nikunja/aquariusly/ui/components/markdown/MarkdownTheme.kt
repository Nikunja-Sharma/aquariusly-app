package com.nikunja.aquariusly.ui.components.markdown

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Immutable
data class MarkdownColors(
    val text: Color,
    val link: Color,
    val codeText: Color,
    val codeBackground: Color,
    val blockQuoteBorder: Color,
    val blockQuoteBackground: Color,
    val tableBorder: Color,
    val tableHeaderBackground: Color,
    val tableRowEvenBackground: Color,
    val horizontalRule: Color
)

@Immutable
data class MarkdownTypography(
    val body: TextStyle,
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle,
    val h4: TextStyle,
    val h5: TextStyle,
    val h6: TextStyle,
    val code: TextStyle,
    val codeBlock: TextStyle
)

@Immutable
data class MarkdownSpacing(
    val paragraphSpacing: Dp = 8.dp,
    val headingSpacing: Dp = 12.dp,
    val listItemSpacing: Dp = 4.dp,
    val codeBlockPadding: Dp = 12.dp,
    val blockQuotePadding: Dp = 12.dp,
    val tableCellPadding: Dp = 8.dp
)

@Immutable
data class MarkdownTheme(
    val colors: MarkdownColors,
    val typography: MarkdownTypography,
    val spacing: MarkdownSpacing = MarkdownSpacing()
)

val LocalMarkdownTheme = staticCompositionLocalOf<MarkdownTheme> {
    error("No MarkdownTheme provided")
}

fun defaultMarkdownTheme(
    textColor: Color,
    linkColor: Color = Color(0xFF4A90D9),
    codeBackground: Color,
    isDark: Boolean
): MarkdownTheme {
    return MarkdownTheme(
        colors = MarkdownColors(
            text = textColor,
            link = linkColor,
            codeText = textColor,
            codeBackground = codeBackground,
            blockQuoteBorder = linkColor,
            blockQuoteBackground = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF5F5F5),
            tableBorder = textColor.copy(alpha = 0.2f),
            tableHeaderBackground = textColor.copy(alpha = 0.08f),
            tableRowEvenBackground = textColor.copy(alpha = 0.03f),
            horizontalRule = textColor.copy(alpha = 0.2f)
        ),
        typography = MarkdownTypography(
            body = TextStyle(fontSize = 14.sp, color = textColor, lineHeight = 20.sp),
            h1 = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textColor),
            h2 = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor),
            h3 = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = textColor),
            h4 = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = textColor),
            h5 = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textColor),
            h6 = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textColor),
            code = TextStyle(fontSize = 13.sp, fontFamily = FontFamily.Monospace, color = textColor),
            codeBlock = TextStyle(fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = textColor, lineHeight = 18.sp)
        )
    )
}
