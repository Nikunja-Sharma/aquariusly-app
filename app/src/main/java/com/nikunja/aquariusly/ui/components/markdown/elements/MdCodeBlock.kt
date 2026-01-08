package com.nikunja.aquariusly.ui.components.markdown.elements

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.res.painterResource
import com.nikunja.aquariusly.R
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nikunja.aquariusly.ui.components.markdown.LocalMarkdownTheme
import kotlinx.coroutines.delay

@Composable
fun MdCodeBlock(
    code: String,
    language: String?,
    modifier: Modifier = Modifier
) {
    val theme = LocalMarkdownTheme.current
    val context = LocalContext.current
    var isCopied by remember { mutableStateOf(false) }
    
    // Reset copied state after delay
    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(2000)
            isCopied = false
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        // Header with language and copy button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2D2D2D))
                .padding(horizontal = 12.dp, vertical = 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Language label
            Text(
                text = language?.lowercase() ?: "code",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                ),
                color = Color(0xFF9CA3AF)
            )
            
            // Copy button
            TextButton(
                onClick = {
                    copyToClipboard(context, code)
                    isCopied = true
                },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF9CA3AF)
                )
            ) {
                if (isCopied) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Copied",
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_copy),
                        contentDescription = "Copy code",
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFF9CA3AF)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isCopied) "Copied!" else "Copy",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        
        // Code content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E))
                .horizontalScroll(rememberScrollState())
                .padding(12.dp)
        ) {
            Text(
                text = code,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                ),
                color = Color(0xFFE5E7EB)
            )
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("code", text)
    clipboard.setPrimaryClip(clip)
}
