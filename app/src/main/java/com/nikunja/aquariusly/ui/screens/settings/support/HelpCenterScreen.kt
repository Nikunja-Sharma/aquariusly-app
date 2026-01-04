package com.nikunja.aquariusly.ui.screens.settings.support

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import com.nikunja.aquariusly.ui.theme.UnifiedAIColors
import com.nikunja.aquariusly.ui.theme.UnifiedTheme

data class FaqCategory(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val faqs: List<FaqItem>
)

data class FaqItem(
    val question: String,
    val answer: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(
    onBackClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    var searchQuery by remember { mutableStateOf("") }
    
    val categories = remember {
        listOf(
            FaqCategory(
                title = "Getting Started",
                icon = Icons.Default.PlayArrow,
                faqs = listOf(
                    FaqItem(
                        "How do I start a conversation?",
                        "Simply type your message in the chat input at the bottom of the screen and press send. The AI will respond to your query."
                    ),
                    FaqItem(
                        "What AI models are available?",
                        "We offer GPT-4 Turbo, Claude 3 Opus, Gemini Pro, and Llama 3. You can switch models in Settings > AI Configuration > Model Selection."
                    ),
                    FaqItem(
                        "How do credits work?",
                        "Credits are used for AI responses. Different models use different amounts of credits per token. You can view your balance in Settings > Credits."
                    )
                )
            ),
            FaqCategory(
                title = "Account & Billing",
                icon = Icons.Default.Person,
                faqs = listOf(
                    FaqItem(
                        "How do I upgrade my plan?",
                        "Go to Settings > Manage Subscription to view available plans and upgrade. Changes take effect immediately."
                    ),
                    FaqItem(
                        "Can I get a refund?",
                        "Refunds are available within 7 days of purchase for unused credits. Contact support for assistance."
                    ),
                    FaqItem(
                        "How do I cancel my subscription?",
                        "Go to Settings > Manage Subscription and tap 'Cancel Subscription'. You'll retain access until the end of your billing period."
                    )
                )
            ),
            FaqCategory(
                title = "Features",
                icon = Icons.Default.Star,
                faqs = listOf(
                    FaqItem(
                        "What is RAG?",
                        "RAG (Retrieval Augmented Generation) allows the AI to search through your uploaded documents to provide more accurate, contextual responses."
                    ),
                    FaqItem(
                        "What are MCP Servers?",
                        "MCP (Model Context Protocol) servers extend AI capabilities with external tools like web search, file access, and database queries."
                    ),
                    FaqItem(
                        "How do I customize the AI's behavior?",
                        "Use the System Prompt setting to define how the AI should respond. You can also adjust Temperature for creativity and Max Tokens for response length."
                    )
                )
            ),
            FaqCategory(
                title = "Privacy & Security",
                icon = Icons.Default.Lock,
                faqs = listOf(
                    FaqItem(
                        "Is my data secure?",
                        "Yes, all data is encrypted in transit and at rest. We use industry-standard security practices to protect your information."
                    ),
                    FaqItem(
                        "Can I delete my data?",
                        "Yes, go to Settings > Privacy > Delete My Account to permanently remove all your data from our servers."
                    ),
                    FaqItem(
                        "Are my conversations private?",
                        "Your conversations are private by default. You can opt out of AI training in Settings > Privacy."
                    )
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
                        "Help Center",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search help articles...", color = colors.textMuted) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = colors.textMuted
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.accentBlue,
                        unfocusedBorderColor = colors.borderDefault,
                        focusedContainerColor = colors.surfaceElevated,
                        unfocusedContainerColor = colors.surfaceElevated
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
            
            // Quick Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        icon = Icons.Default.Email,
                        title = "Contact Us",
                        onClick = { /* Contact */ },
                        colors = colors,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        icon = Icons.Default.Call,
                        title = "Live Chat",
                        onClick = { /* Chat */ },
                        colors = colors,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // FAQ Categories
            items(categories) { category ->
                FaqCategoryCard(category = category, colors = colors)
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    colors: UnifiedAIColors,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = colors.accentBlue.copy(alpha = 0.1f),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.accentBlue,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = colors.accentBlue
            )
        }
    }
}

@Composable
private fun FaqCategoryCard(category: FaqCategory, colors: UnifiedAIColors) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colors.surfaceElevated
    ) {
        Column(
            modifier = Modifier.animateContentSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    tint = colors.accentBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = colors.textMuted
                )
            }
            
            // FAQs
            if (isExpanded) {
                HorizontalDivider(color = colors.borderSubtle)
                category.faqs.forEachIndexed { index, faq ->
                    FaqItemCard(faq = faq, colors = colors)
                    if (index < category.faqs.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = colors.borderSubtle
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FaqItemCard(faq: FaqItem, colors: UnifiedAIColors) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .padding(16.dp)
            .animateContentSize()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = faq.question,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = colors.textMuted,
                modifier = Modifier.size(20.dp)
            )
        }
        
        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = faq.answer,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        }
    }
}
