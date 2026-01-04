package com.nikunja.aquariusly.ui.screens.settings.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nikunja.aquariusly.ui.theme.UnifiedAIColors
import com.nikunja.aquariusly.ui.theme.UnifiedTheme

data class ChatHistoryItem(
    val id: String,
    val title: String,
    val preview: String,
    val date: String,
    val messageCount: Int,
    val model: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHistoryScreen(
    onBackClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf<ChatHistoryItem?>(null) }
    
    val chatHistory = remember {
        listOf(
            ChatHistoryItem("1", "Kotlin Coroutines Help", "Can you explain how to use Flow in Kotlin...", "Today", 24, "GPT-4"),
            ChatHistoryItem("2", "Recipe Ideas", "I need some healthy dinner recipes for...", "Today", 12, "Claude 3"),
            ChatHistoryItem("3", "Code Review", "Please review this function and suggest...", "Yesterday", 18, "GPT-4"),
            ChatHistoryItem("4", "Travel Planning", "I'm planning a trip to Japan in spring...", "Yesterday", 32, "GPT-4"),
            ChatHistoryItem("5", "Debug Session", "I'm getting a NullPointerException when...", "Jan 26", 45, "Claude 3"),
            ChatHistoryItem("6", "Writing Assistance", "Help me write a professional email to...", "Jan 25", 8, "GPT-4"),
            ChatHistoryItem("7", "Math Problem", "Can you solve this calculus problem...", "Jan 24", 15, "Gemini"),
            ChatHistoryItem("8", "API Design", "What's the best way to design a REST API...", "Jan 23", 28, "GPT-4")
        )
    }
    
    val filteredHistory = chatHistory.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
        it.preview.contains(searchQuery, ignoreCase = true)
    }

    showDeleteDialog?.let { chat ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            containerColor = colors.surfaceElevated,
            title = { Text("Delete Chat?", color = colors.textPrimary) },
            text = { 
                Text(
                    "This will permanently delete \"${chat.title}\" and all its messages.",
                    color = colors.textSecondary
                ) 
            },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Delete", color = colors.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            }
        )
    }

    Scaffold(
        containerColor = colors.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Chat History",
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
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search conversations...", color = colors.textMuted) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = colors.textMuted
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = colors.textMuted
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accentBlue,
                    unfocusedBorderColor = colors.borderDefault,
                    focusedContainerColor = colors.surfaceElevated,
                    unfocusedContainerColor = colors.surfaceElevated,
                    cursorColor = colors.accentBlue
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            // Stats
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                color = colors.surfaceElevated
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatColumn(
                        value = chatHistory.size.toString(),
                        label = "Conversations",
                        colors = colors
                    )
                    StatColumn(
                        value = chatHistory.sumOf { it.messageCount }.toString(),
                        label = "Messages",
                        colors = colors
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Chat List
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredHistory, key = { it.id }) { chat ->
                    ChatHistoryCard(
                        chat = chat,
                        onDelete = { showDeleteDialog = chat },
                        colors = colors
                    )
                }
            }
        }
    }
}

@Composable
private fun StatColumn(value: String, label: String, colors: UnifiedAIColors) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted
        )
    }
}

@Composable
private fun ChatHistoryCard(
    chat: ChatHistoryItem,
    onDelete: () -> Unit,
    colors: UnifiedAIColors
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colors.surfaceElevated
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chat.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = chat.preview,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chat.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted
                    )
                    Text(
                        text = "•",
                        color = colors.textMuted
                    )
                    Text(
                        text = "${chat.messageCount} messages",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted
                    )
                    Text(
                        text = "•",
                        color = colors.textMuted
                    )
                    Text(
                        text = chat.model,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.accentBlue
                    )
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = colors.textMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
