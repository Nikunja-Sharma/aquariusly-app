package com.nikunja.aquariusly.ui.screens.settings.support

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nikunja.aquariusly.ui.theme.UnifiedAIColors
import com.nikunja.aquariusly.ui.theme.UnifiedTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBackClick: () -> Unit) {
    val colors = UnifiedTheme.colors

    Scaffold(
        containerColor = colors.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = { Text("About", color = colors.textPrimary, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = colors.textPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.backgroundPrimary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            AppLogoSection(colors = colors)
            Spacer(modifier = Modifier.height(32.dp))
            AppInfoCard(colors = colors)
            Spacer(modifier = Modifier.height(16.dp))
            LinksCard(colors = colors)
            Spacer(modifier = Modifier.height(16.dp))
            LegalCard(colors = colors)
            Spacer(modifier = Modifier.height(24.dp))
            Text("Made with ❤️ in Nepal", style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
            Spacer(modifier = Modifier.height(8.dp))
            Text("© 2026 Aquariusly. All rights reserved.", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
        }
    }
}


@Composable
private fun AppLogoSection(colors: UnifiedAIColors) {
    Surface(
        modifier = Modifier.size(100.dp),
        shape = RoundedCornerShape(24.dp),
        color = colors.accentBlue.copy(alpha = 0.15f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = colors.accentBlue,
                modifier = Modifier.size(48.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text("Aquariusly", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = colors.textPrimary)
    Text("Your AI Companion", style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
}

@Composable
private fun AppInfoCard(colors: UnifiedAIColors) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = colors.surfaceElevated) {
        Column(modifier = Modifier.padding(20.dp)) {
            InfoRow("Version", "1.0.0 (Build 42)", colors)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = colors.borderSubtle)
            InfoRow("Platform", "Android", colors)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = colors.borderSubtle)
            InfoRow("Last Updated", "January 28, 2026", colors)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, colors: UnifiedAIColors) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = colors.textPrimary)
    }
}

@Composable
private fun LinksCard(colors: UnifiedAIColors) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = colors.surfaceElevated) {
        Column {
            LinkItem(Icons.Default.Star, "Rate Us", "Love the app? Leave a review!", colors) { }
            HorizontalDivider(color = colors.borderSubtle)
            LinkItem(Icons.Default.Share, "Share App", "Tell your friends about us", colors) { }
            HorizontalDivider(color = colors.borderSubtle)
            LinkItem(Icons.Default.Favorite, "Follow Us", "@aquariusly on Twitter", colors) { }
        }
    }
}

@Composable
private fun LinkItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, colors: UnifiedAIColors, onClick: () -> Unit) {
    Surface(onClick = onClick, color = colors.surfaceElevated) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = colors.accentBlue, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = colors.textPrimary)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
            }
            Icon(Icons.Default.KeyboardArrowRight, null, tint = colors.textMuted, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun LegalCard(colors: UnifiedAIColors) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = colors.surfaceElevated) {
        Column {
            LegalLink("Privacy Policy", colors) { }
            HorizontalDivider(color = colors.borderSubtle)
            LegalLink("Terms of Service", colors) { }
            HorizontalDivider(color = colors.borderSubtle)
            LegalLink("Open Source Licenses", colors) { }
        }
    }
}

@Composable
private fun LegalLink(title: String, colors: UnifiedAIColors, onClick: () -> Unit) {
    Surface(onClick = onClick, color = colors.surfaceElevated) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
            Icon(Icons.Default.KeyboardArrowRight, null, tint = colors.textMuted, modifier = Modifier.size(20.dp))
        }
    }
}
