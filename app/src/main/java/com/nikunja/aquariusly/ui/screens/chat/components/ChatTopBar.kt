package com.nikunja.aquariusly.ui.screens.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nikunja.aquariusly.domain.model.AIModel
import com.nikunja.aquariusly.ui.theme.UnifiedTheme

@Composable
fun ChatTopBar(
    selectedModel: AIModel,
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.surfaceElevated)
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier.size(22.dp),
                tint = colors.textSecondary
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.surfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderDefault)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = selectedModel.iconRes),
                        contentDescription = selectedModel.name,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                }
                
                Text(
                    text = selectedModel.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        IconButton(onClick = onProfileClick) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceElevated)
                    .border(1.dp, colors.borderDefault, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(20.dp),
                    tint = colors.textSecondary
                )
            }
        }
    }
}
