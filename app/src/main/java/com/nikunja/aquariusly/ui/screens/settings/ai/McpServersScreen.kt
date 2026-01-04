package com.nikunja.aquariusly.ui.screens.settings.ai

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
import androidx.compose.ui.unit.dp
import com.nikunja.aquariusly.ui.theme.UnifiedAIColors
import com.nikunja.aquariusly.ui.theme.UnifiedTheme

data class McpServer(
    val id: String,
    val name: String,
    val description: String,
    val status: ServerStatus,
    val tools: List<String>,
    val isEnabled: Boolean
)

enum class ServerStatus { CONNECTED, DISCONNECTED, ERROR }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun McpServersScreen(
    onBackClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    
    var servers by remember {
        mutableStateOf(
            listOf(
                McpServer(
                    id = "filesystem",
                    name = "Filesystem",
                    description = "Read and write files on your device",
                    status = ServerStatus.CONNECTED,
                    tools = listOf("read_file", "write_file", "list_directory"),
                    isEnabled = true
                ),
                McpServer(
                    id = "web-search",
                    name = "Web Search",
                    description = "Search the web for information",
                    status = ServerStatus.CONNECTED,
                    tools = listOf("search", "fetch_url"),
                    isEnabled = true
                ),
                McpServer(
                    id = "database",
                    name = "Database",
                    description = "Query and manage databases",
                    status = ServerStatus.DISCONNECTED,
                    tools = listOf("query", "insert", "update"),
                    isEnabled = false
                ),
                McpServer(
                    id = "github",
                    name = "GitHub",
                    description = "Interact with GitHub repositories",
                    status = ServerStatus.ERROR,
                    tools = listOf("create_issue", "list_repos", "create_pr"),
                    isEnabled = true
                )
            )
        )
    }

    Scaffold(
        containerColor = colors.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "MCP Servers",
                        color = colors.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.textPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Add server */ }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Server",
                            tint = colors.accentBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.backgroundPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = colors.infoBg
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = colors.info,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "MCP servers extend AI capabilities with external tools and data sources.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.info
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            servers.forEach { server ->
                McpServerCard(
                    server = server,
                    onToggle = { enabled ->
                        servers = servers.map {
                            if (it.id == server.id) it.copy(isEnabled = enabled) else it
                        }
                    },
                    colors = colors
                )
            }
        }
    }
}

@Composable
private fun McpServerCard(
    server: McpServer,
    onToggle: (Boolean) -> Unit,
    colors: UnifiedAIColors
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colors.surfaceElevated
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusIndicator(status = server.status, colors = colors)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = server.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = server.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted
                        )
                    }
                }
                
                Switch(
                    checked = server.isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colors.textPrimary,
                        checkedTrackColor = colors.accentBlue,
                        uncheckedThumbColor = colors.textMuted,
                        uncheckedTrackColor = colors.borderDefault
                    )
                )
            }
            
            if (server.isEnabled) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = colors.borderSubtle)
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Available Tools",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textMuted
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    server.tools.forEach { tool ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = colors.borderDefault
                        ) {
                            Text(
                                text = tool,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusIndicator(status: ServerStatus, colors: UnifiedAIColors) {
    val (color, icon) = when (status) {
        ServerStatus.CONNECTED -> colors.success to Icons.Default.CheckCircle
        ServerStatus.DISCONNECTED -> colors.textMuted to Icons.Default.Close
        ServerStatus.ERROR -> colors.error to Icons.Default.Warning
    }
    
    Icon(
        imageVector = icon,
        contentDescription = status.name,
        tint = color,
        modifier = Modifier.size(24.dp)
    )
}
