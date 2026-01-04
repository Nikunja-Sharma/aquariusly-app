package com.nikunja.aquariusly.ui.screens.settings.ai.configuremodel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nikunja.aquariusly.domain.model.ConfigurableModel
import com.nikunja.aquariusly.domain.model.ModelSortBy
import com.nikunja.aquariusly.domain.model.SortOrder
import com.nikunja.aquariusly.ui.theme.UnifiedTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigureModelScreen(
    onBackClick: () -> Unit,
    onChatWithModel: (String) -> Unit = {},
    viewModel: ConfigureModelViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = UnifiedTheme.colors
    val listState = rememberLazyListState()
    var showSortMenu by remember { mutableStateOf(false) }

    // Load more when reaching end
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastIndex ->
                if (lastIndex != null && lastIndex >= state.models.size - 3) {
                    viewModel.onLoadMore()
                }
            }
    }

    Scaffold(
        containerColor = colors.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Configure Models",
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
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Sort",
                            tint = colors.textPrimary
                        )
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        SortMenuItem("Name", ModelSortBy.NAME, state.filters.sortBy, viewModel) { showSortMenu = false }
                        SortMenuItem("Provider", ModelSortBy.PROVIDER, state.filters.sortBy, viewModel) { showSortMenu = false }
                        SortMenuItem("Context", ModelSortBy.CONTEXT_LENGTH, state.filters.sortBy, viewModel) { showSortMenu = false }
                        SortMenuItem("Cost", ModelSortBy.PROMPT_COST, state.filters.sortBy, viewModel) { showSortMenu = false }
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { 
                                Text(if (state.filters.sortOrder == SortOrder.ASC) "↑ Ascending" else "↓ Descending") 
                            },
                            onClick = { 
                                viewModel.onSortOrderToggle()
                                showSortMenu = false
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.backgroundPrimary)
            )
        },
        bottomBar = {
            if (state.selectedModelIds.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = colors.surfaceElevated,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${state.selectedModelIds.size} models selected",
                            color = colors.textSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(
                            onClick = viewModel::onSaveSelection,
                            enabled = !state.isSaving,
                            colors = ButtonDefaults.buttonColors(containerColor = colors.accentBlue)
                        ) {
                            if (state.isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = colors.textPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else if (state.saveSuccess) {
                                Icon(Icons.Default.Check, contentDescription = null)
                            } else {
                                Text("Save")
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            SearchBar(
                query = state.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Provider Filter Chips
            if (state.providers.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = state.selectedProvider == null,
                            onClick = { viewModel.onProviderSelected(null) },
                            label = { Text("All") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colors.accentBlue,
                                selectedLabelColor = colors.textPrimary
                            )
                        )
                    }
                    items(state.providers) { provider ->
                        FilterChip(
                            selected = state.selectedProvider == provider,
                            onClick = { viewModel.onProviderSelected(provider) },
                            label = { Text(provider) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colors.accentBlue,
                                selectedLabelColor = colors.textPrimary
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Stats
            state.pagination?.let { pagination ->
                Text(
                    text = "${pagination.total} models available",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // Models List
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = state.models, key = { it.id }) { model ->
                    ModelCard(
                        model = model,
                        isSelected = state.selectedModelIds.contains(model.id),
                        onClick = { viewModel.onModelToggle(model) },
                        onChatClick = { onChatWithModel(model.id) }
                    )
                }

                if (state.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = colors.accentBlue)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SortMenuItem(
    label: String,
    sortBy: ModelSortBy,
    currentSortBy: ModelSortBy?,
    viewModel: ConfigureModelViewModel,
    onDismiss: () -> Unit
) {
    DropdownMenuItem(
        text = { Text(label) },
        onClick = {
            viewModel.onSortByChange(sortBy)
            onDismiss()
        },
        trailingIcon = {
            if (currentSortBy == sortBy) {
                Icon(Icons.Default.Check, contentDescription = null)
            }
        }
    )
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = UnifiedTheme.colors

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colors.inputBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.inputBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                modifier = Modifier.size(20.dp),
                tint = colors.textMuted
            )
            Spacer(modifier = Modifier.width(12.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = colors.textPrimary),
                cursorBrush = SolidColor(colors.accentBlue),
                decorationBox = { innerTextField ->
                    Box {
                        if (query.isEmpty()) {
                            Text(
                                text = "Search models...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textMuted
                            )
                        }
                        innerTextField()
                    }
                }
            )
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = colors.textMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun ModelCard(
    model: ConfigurableModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    onChatClick: () -> Unit
) {
    val colors = UnifiedTheme.colors

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) colors.accentBlue else colors.borderDefault,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        color = if (isSelected) colors.accentBlue.copy(alpha = 0.08f) else colors.surfaceElevated
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onClick() },
                colors = CheckboxDefaults.colors(
                    checkedColor = colors.accentBlue,
                    uncheckedColor = colors.textMuted
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = model.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProviderBadge(provider = model.provider)
                    Text(
                        text = formatContextLength(model.contextLength),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatPricing(model.pricing.prompt, model.pricing.completion),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.accentAmber
                )
            }
            IconButton(
                onClick = onChatClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Chat with model",
                    tint = colors.accentBlue
                )
            }
        }
    }
}

@Composable
private fun ProviderBadge(provider: String) {
    val colors = UnifiedTheme.colors
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = colors.borderDefault
    ) {
        Text(
            text = provider,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary
        )
    }
}

private fun formatContextLength(length: Int): String {
    return when {
        length >= 1_000_000 -> "${length / 1_000_000}M ctx"
        length >= 1_000 -> "${length / 1_000}K ctx"
        else -> "$length ctx"
    }
}

private fun formatPricing(prompt: Double, completion: Double): String {
    return "$${"%.8f".format(prompt).trimEnd('0').trimEnd('.')}/in • $${"%.8f".format(completion).trimEnd('0').trimEnd('.')}/out"
}
