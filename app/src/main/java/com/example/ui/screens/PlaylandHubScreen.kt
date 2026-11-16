package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.GameProgress
import com.example.data.model.StickerReward
import com.example.data.model.UserProfile
import com.example.ui.components.ChunkyBubbleButton
import com.example.ui.components.CloudGameCard
import com.example.ui.components.CloudPanelCard
import com.example.ui.components.PlaylandInputField
import com.example.ui.components.RewardChip
import com.example.ui.components.StarProgressMeter
import com.example.ui.theme.CloudWhite
import com.example.ui.theme.DeepSlateNavy
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldMintLip
import com.example.ui.theme.EmeraldMintSoft
import com.example.ui.theme.LavenderDream
import com.example.ui.theme.LavenderDreamSoft
import com.example.ui.theme.SkyFixed
import com.example.ui.theme.SkyPastelBlue
import com.example.ui.theme.SkyPrimary
import com.example.ui.theme.SkyPrimaryLip
import com.example.ui.theme.SlateVariant
import com.example.ui.theme.StrawberryFixed
import com.example.ui.theme.StrawberryLip
import com.example.ui.theme.StrawberryPink
import com.example.ui.theme.SunnyFixed
import com.example.ui.theme.SunnyHoney
import com.example.ui.theme.SunnyOrange
import com.example.ui.viewmodel.PlaylandGameCatalogItem

@Composable
fun PlaylandHubScreen(
    profile: UserProfile,
    gamesMap: Map<String, GameProgress>,
    unlockedStickers: List<StickerReward>,
    gameCatalog: List<PlaylandGameCatalogItem>,
    searchQuery: String,
    selectedCategory: String,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onPlayGame: (String) -> Unit,
    onSaveProfile: (String, String) -> Unit,
    onNavigateStickers: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showProfileDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Letters", "Numbers", "Shapes", "Music")
    val filteredGames = remember(gameCatalog, searchQuery, selectedCategory) {
        gameCatalog.filter { game ->
            val matchesCategory = selectedCategory == "All" || game.category == selectedCategory
            val q = searchQuery.trim().lowercase()
            val matchesSearch = q.isEmpty() ||
                game.title.lowercase().contains(q) ||
                game.subtitle.lowercase().contains(q) ||
                game.category.lowercase().contains(q)
            matchesCategory && matchesSearch
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 290.dp),
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1200.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Greeting + Avatar Pill
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hi, ${profile.learnerName}!",
                            style = MaterialTheme.typography.displayMedium,
                            color = DeepSlateNavy,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Ready to bounce & learn today?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Tactile Avatar Pill
                    Surface(
                        modifier = Modifier
                            .testTag("edit_profile_button")
                            .clickable { showProfileDialog = true },
                        shape = CircleShape,
                        color = CloudWhite,
                        border = BorderStroke(3.dp, SkyPastelBlue),
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AvatarBadge(avatarId = profile.avatarId, size = 36.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = "Edit explorer profile",
                                tint = SkyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Star Progress Meter
            item(span = { GridItemSpan(maxLineSpan) }) {
                StarProgressMeter(
                    xp = profile.xp,
                    level = profile.level,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Reward Chips Strip
            item(span = { GridItemSpan(maxLineSpan) }) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        RewardChip(
                            label = "${profile.totalStars} Stars",
                            icon = Icons.Rounded.Star,
                            tint = SunnyHoney,
                            backgroundColor = SunnyFixed
                        )
                    }
                    item {
                        RewardChip(
                            label = "${unlockedStickers.size}/4 Stickers",
                            icon = Icons.Rounded.EmojiEvents,
                            tint = StrawberryPink,
                            backgroundColor = StrawberryFixed,
                            onClick = onNavigateStickers
                        )
                    }
                    item {
                        RewardChip(
                            label = "${profile.xp} XP",
                            icon = Icons.Rounded.Bolt,
                            tint = EmeraldMint,
                            backgroundColor = EmeraldMintSoft
                        )
                    }
                }
            }

            // Kid Search + Category Filter Pills
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    PlaylandInputField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = "Find a game (ABC, Berry, Shapes, Music)...",
                        leadingIcon = Icons.Rounded.Search,
                        testTag = "kid_search_input"
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { category ->
                            val selected = category == selectedCategory
                            Surface(
                                modifier = Modifier
                                    .testTag("category_chip_${category.lowercase()}")
                                    .clickable { onCategorySelect(category) },
                                shape = CircleShape,
                                color = if (selected) SkyPastelBlue else CloudWhite,
                                border = BorderStroke(
                                    width = 2.5.dp,
                                    color = if (selected) SkyPrimaryLip else Color(0xFFBAE6FD)
                                )
                            ) {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (selected) Color.White else DeepSlateNavy,
                                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Game Cards Grid or Empty State
            if (filteredGames.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    CloudPanelCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = SunnyHoney,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No matching games found",
                                style = MaterialTheme.typography.headlineMedium,
                                color = DeepSlateNavy
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Try clearing your search to see all 4 Pastel Playland worlds!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SlateVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            ChunkyBubbleButton(
                                text = "Show All Games",
                                onClick = {
                                    onSearchChange("")
                                    onCategorySelect("All")
                                },
                                containerColor = SkyPastelBlue,
                                lipColor = SkyPrimaryLip,
                                testTag = "clear_search_button"
                            )
                        }
                    }
                }
            } else {
                items(filteredGames, key = { it.id }) { game ->
                    val progress = gamesMap[game.id]
                    CloudGameCard(
                        title = game.title,
                        subtitle = game.subtitle,
                        ageTag = game.ageTag,
                        bannerResId = game.bannerResId,
                        starsEarned = (progress?.stars ?: 0L).toInt(),
                        accentColor = Color(game.accentHex),
                        onPlayClick = { onPlayGame(game.id) },
                        testTag = "game_card_${game.id}"
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showProfileDialog) {
        EditProfileDialog(
            currentName = profile.learnerName,
            currentAvatarId = profile.avatarId,
            onDismiss = { showProfileDialog = false },
            onSave = { newName, newAvatar ->
                onSaveProfile(newName, newAvatar)
                showProfileDialog = false
            }
        )
    }
}

@Composable
fun AvatarBadge(avatarId: String, size: androidx.compose.ui.unit.Dp = 44.dp) {
    val (icon, bg, tint) = avatarStyle(avatarId)
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "$avatarId avatar",
            tint = tint,
            modifier = Modifier.size(size * 0.6f)
        )
    }
}

private fun avatarStyle(avatarId: String): Triple<ImageVector, Color, Color> {
    return when (avatarId) {
        "berry" -> Triple(Icons.Rounded.Favorite, StrawberryFixed, StrawberryPink)
        "cloud" -> Triple(Icons.Rounded.Cloud, SkyFixed, SkyPastelBlue)
        "bunny" -> Triple(Icons.Rounded.Pets, LavenderDreamSoft, LavenderDream)
        else -> Triple(Icons.Rounded.Star, SunnyFixed, SunnyOrange)
    }
}

@Composable
private fun EditProfileDialog(
    currentName: String,
    currentAvatarId: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var nameInput by remember { mutableStateOf(currentName) }
    var selectedAvatar by remember { mutableStateOf(currentAvatarId) }
    val avatars = listOf("star" to "Starry", "berry" to "Berry", "cloud" to "Cloudy", "bunny" to "Bunny")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CloudWhite,
        shape = RoundedCornerShape(32.dp),
        title = {
            Text(
                text = "Explorer Profile",
                style = MaterialTheme.typography.headlineMedium,
                color = DeepSlateNavy
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PlaylandInputField(
                    value = nameInput,
                    onValueChange = { if (it.length <= 24) nameInput = it },
                    placeholder = "Explorer's nickname",
                    testTag = "profile_name_input"
                )
                Text(
                    text = "Pick your Playland buddy:",
                    style = MaterialTheme.typography.labelMedium,
                    color = SlateVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    avatars.forEach { (id, label) ->
                        val isSelected = selectedAvatar == id
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .clickable { selectedAvatar = id }
                                .padding(6.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                border = BorderStroke(
                                    width = if (isSelected) 3.dp else 1.5.dp,
                                    color = if (isSelected) StrawberryPink else Color.Transparent
                                ),
                                color = Color.Transparent
                            ) {
                                AvatarBadge(avatarId = id, size = 48.dp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) StrawberryLip else SlateVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            ChunkyBubbleButton(
                text = "Save Buddy",
                onClick = { onSave(nameInput, selectedAvatar) },
                containerColor = EmeraldMint,
                lipColor = EmeraldMintLip,
                height = 52.dp,
                testTag = "save_profile_button"
            )
        },
        dismissButton = {
            ChunkyBubbleButton(
                text = "Cancel",
                onClick = onDismiss,
                containerColor = SkyPastelBlue,
                lipColor = SkyPrimaryLip,
                height = 52.dp,
                testTag = "cancel_profile_button"
            )
        }
    )
}
