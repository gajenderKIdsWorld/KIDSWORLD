package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Abc
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.model.StickerReward
import com.example.ui.components.ChunkyBubbleButton
import com.example.ui.components.CloudPanelCard
import com.example.ui.components.RewardChip
import com.example.ui.theme.CloudWhite
import com.example.ui.theme.DeepSlateNavy
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldMintSoft
import com.example.ui.theme.LavenderDream
import com.example.ui.theme.LavenderDreamSoft
import com.example.ui.theme.SkyFixed
import com.example.ui.theme.SkyPastelBlue
import com.example.ui.theme.SlateVariant
import com.example.ui.theme.SoftOutlineVariant
import com.example.ui.theme.StrawberryFixed
import com.example.ui.theme.StrawberryPink
import com.example.ui.theme.SunnyFixed
import com.example.ui.theme.SunnyHoney
import com.example.ui.components.darkenForLip
import com.example.ui.viewmodel.StickerCatalogItem

@Composable
fun StickersScreen(
    stickerCatalog: List<StickerCatalogItem>,
    unlockedStickers: List<StickerReward>,
    onPlayGame: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val unlockedIds = unlockedStickers.map { it.stickerId }.toSet()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 240.dp),
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1100.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                CloudPanelCard(cornerRadius = 32.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Sticker Book",
                                style = MaterialTheme.typography.displayMedium,
                                color = DeepSlateNavy
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Earn 2 or more stars in any mini-game to collect its shiny sticker!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SlateVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        RewardChip(
                            label = "${unlockedIds.size} / ${stickerCatalog.size}",
                            icon = Icons.Rounded.EmojiEvents,
                            tint = SunnyHoney,
                            backgroundColor = SunnyFixed
                        )
                    }
                }
            }

            items(stickerCatalog, key = { it.id }) { item ->
                val isUnlocked = unlockedIds.contains(item.id)
                val (icon, accent, softBg) = stickerVisuals(item.category)

                CloudPanelCard(
                    cornerRadius = 32.dp,
                    borderColor = if (isUnlocked) accent.copy(alpha = 0.55f) else Color(0xFFE0F2FE),
                    testTag = "sticker_card_${item.id}"
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(92.dp)
                                .clip(CircleShape)
                                .background(if (isUnlocked) softBg else Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isUnlocked) icon else Icons.Rounded.Lock,
                                contentDescription = item.title,
                                tint = if (isUnlocked) accent else SoftOutlineVariant,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = if (isUnlocked) DeepSlateNavy else SlateVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        if (isUnlocked) {
                            Surface(
                                shape = CircleShape,
                                color = EmeraldMintSoft,
                                border = BorderStroke(2.dp, EmeraldMint)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Star,
                                        contentDescription = null,
                                        tint = SunnyHoney,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Collected!",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = DeepSlateNavy
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = item.unlockHint,
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            ChunkyBubbleButton(
                                text = "Play to Unlock",
                                onClick = { onPlayGame(item.category) },
                                containerColor = accent,
                                lipColor = darkenForLip(accent),
                                height = 50.dp,
                                testTag = "unlock_sticker_${item.id}_btn"
                            )
                        }
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

private fun stickerVisuals(category: String): Triple<ImageVector, Color, Color> {
    return when (category) {
        "phonics" -> Triple(Icons.Rounded.Abc, SkyPastelBlue, SkyFixed)
        "counting" -> Triple(Icons.Rounded.Favorite, StrawberryPink, StrawberryFixed)
        "shapes" -> Triple(Icons.Rounded.Category, EmeraldMint, EmeraldMintSoft)
        "music" -> Triple(Icons.Rounded.MusicNote, LavenderDream, LavenderDreamSoft)
        else -> Triple(Icons.Rounded.Star, SunnyHoney, SunnyFixed)
    }
}
