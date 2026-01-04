package com.nikunja.aquariusly.ui.screens.settings.support

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nikunja.aquariusly.R
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Logo Section
            AppLogoSection(colors = colors)

            // App Info Section
            AboutSection(title = "App Info", colors = colors) {
                AboutInfoItem("Version", "1.0.0 (Build 42)", colors)
                AboutInfoItem("Platform", "Android", colors)
                AboutInfoItem("Last Updated", "January 28, 2026", colors)
            }

            // Links Section
            AboutSection(title = "Connect", colors = colors) {
                AboutNavigationItem(
                    icon = Icons.Default.Star,
                    title = "Rate Us",
                    subtitle = "Love the app? Leave a review!",
                    onClick = { },
                    colors = colors
                )
                AboutNavigationItem(
                    icon = Icons.Default.Share,
                    title = "Share App",
                    subtitle = "Tell your friends about us",
                    onClick = { },
                    colors = colors
                )
                AboutNavigationItem(
                    icon = Icons.Default.Favorite,
                    title = "Follow Us",
                    subtitle = "@aquariusly on Twitter",
                    onClick = { },
                    colors = colors
                )
            }

            // Legal Section
            AboutSection(title = "Legal", colors = colors) {
                AboutNavigationItem(
                    icon = Icons.Default.Lock,
                    title = "Privacy Policy",
                    subtitle = "How we handle your data",
                    onClick = { },
                    colors = colors
                )
                AboutNavigationItem(
                    icon = Icons.Default.Info,
                    title = "Terms of Service",
                    subtitle = "Usage terms and conditions",
                    onClick = { },
                    colors = colors
                )
                AboutNavigationItem(
                    icon = Icons.Default.Build,
                    title = "Open Source Licenses",
                    subtitle = "Third-party libraries",
                    onClick = { },
                    colors = colors
                )
            }

            // Footer
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Made with ❤️ in Assam", style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
                Spacer(modifier = Modifier.height(4.dp))
                Text("© 2026 Aquariusly. All rights reserved.", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AppLogoSection(colors: UnifiedAIColors) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colors.surfaceElevated
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_aquariusly_logo),
                contentDescription = "Aquariusly Logo",
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Aquariusly",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                "Your AI Companion",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
        }
    }
}

@Composable
private fun AboutSection(
    title: String,
    colors: UnifiedAIColors,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = colors.textSecondary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = colors.surfaceElevated
        ) {
            Column(content = content)
        }
    }
}

@Composable
private fun AboutInfoItem(label: String, value: String, colors: UnifiedAIColors) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = colors.textSecondary)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = colors.textPrimary)
    }
}

@Composable
private fun AboutNavigationItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    colors: UnifiedAIColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted
            )
        }

        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = colors.textMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}
