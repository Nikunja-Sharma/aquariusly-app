package com.nikunja.aquariusly.ui.screens.chat.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nikunja.aquariusly.domain.model.AIModel
import com.nikunja.aquariusly.ui.theme.UnifiedTheme

@Composable
fun TypingIndicator(
    model: AIModel,
    isThinking: Boolean = false,
    thinkingContent: String = ""
) {
    val colors = UnifiedTheme.colors

}
