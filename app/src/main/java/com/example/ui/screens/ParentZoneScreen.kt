package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.model.GameProgress
import com.example.data.model.UserProfile
import com.example.ui.components.ChunkyBubbleButton
import com.example.ui.components.CloudPanelCard
import com.example.ui.components.PlaylandInputField
import com.example.ui.components.PlaylandToggleSwitch
import com.example.ui.components.StarRatingTracker
import com.example.ui.theme.CloudWhite
import com.example.ui.theme.DeepSlateNavy
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldMintLip
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SkyFixed
import com.example.ui.theme.SkyPastelBlue
import com.example.ui.theme.SkyPrimary
import com.example.ui.theme.SkyPrimaryLip
import com.example.ui.theme.SlateVariant
import com.example.ui.theme.StrawberryLip
import com.example.ui.theme.StrawberryPink
import com.example.ui.theme.SunnyOrange
import com.example.ui.theme.SunnyOrangeLip
import com.example.ui.viewmodel.PlaylandGameCatalogItem

@Composable
fun ParentZoneScreen(
    profile: UserProfile,
    gamesMap: Map<String, GameProgress>,
    gameCatalog: List<PlaylandGameCatalogItem>,
    isUnlocked: Boolean,
    onAttemptUnlock: (String) -> Boolean,
    onLock: () -> Unit,
    onSaveSettings: (Boolean, Boolean, Int, String) -> Unit,
    onResetProgress: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isUnlocked) {
        ParentPinGate(
            expectedPin = profile.parentPin,
            onAttemptUnlock = onAttemptUnlock,
            modifier = modifier
        )
    } else {
        ParentDashboardContent(
            profile = profile,
            gamesMap = gamesMap,
            gameCatalog = gameCatalog,
            onLock = onLock,
            onSaveSettings = onSaveSettings,
            onResetProgress = onResetProgress,
            onSignOut = onSignOut,
            modifier = modifier
        )
    }
}

@Composable
private fun ParentPinGate(
    expectedPin: String,
    onAttemptUnlock: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        CloudPanelCard(
            modifier = Modifier.widthIn(max = 460.dp),
            cornerRadius = 32.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = SkyFixed,
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Shield,
                            contentDescription = "Parent gate",
                            tint = SkyPrimary,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Parent & Educator Corner",
                    style = MaterialTheme.typography.headlineMedium,
                    color = DeepSlateNavy,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Enter your 4-digit PIN to view learning progress and safety controls.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (expectedPin == "1234") "First-time default PIN: 1234" else "Enter your custom 4-digit PIN",
                    style = MaterialTheme.typography.labelSmall,
                    color = SkyPrimary
                )

                Spacer(modifier = Modifier.height(18.dp))

                PlaylandInputField(
                    value = pinInput,
                    onValueChange = { raw ->
                        val digits = raw.filter { it.isDigit() }.take(4)
                        pinInput = digits
                        pinError = false
                    },
                    placeholder = "4-digit PIN",
                    leadingIcon = Icons.Rounded.Lock,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    testTag = "parent_pin_input"
                )

                if (pinError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Incorrect PIN. Please try again.",
                        style = MaterialTheme.typography.labelMedium,
                        color = ErrorRed
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                ChunkyBubbleButton(
                    text = "Unlock Parent Corner",
                    onClick = {
                        if (!onAttemptUnlock(pinInput)) {
                            pinError = true
                            pinInput = ""
                        }
                    },
                    containerColor = SkyPastelBlue,
                    lipColor = SkyPrimaryLip,
                    leadingIcon = Icons.Rounded.LockOpen,
                    enabled = pinInput.length == 4,
                    testTag = "unlock_parent_button"
                )
            }
        }
    }
}

@Composable
private fun ParentDashboardContent(
    profile: UserProfile,
    gamesMap: Map<String, GameProgress>,
    gameCatalog: List<PlaylandGameCatalogItem>,
    onLock: () -> Unit,
    onSaveSettings: (Boolean, Boolean, Int, String) -> Unit,
    onResetProgress: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var soundEnabled by remember(profile.soundEnabled) { mutableStateOf(profile.soundEnabled) }
    var dailyTimerEnabled by remember(profile.dailyTimerEnabled) { mutableStateOf(profile.dailyTimerEnabled) }
    var dailyLimit by remember(profile.dailyLimitMinutes) { mutableIntStateOf(profile.dailyLimitMinutes.toInt()) }
    var newPin by remember(profile.parentPin) { mutableStateOf(profile.parentPin) }
    var showResetConfirm by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 900.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Parent Dashboard",
                            style = MaterialTheme.typography.displayMedium,
                            color = DeepSlateNavy
                        )
                        Text(
                            text = "${profile.learnerName}'s learning snapshot & controls",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    ChunkyBubbleButton(
                        text = "Lock",
                        onClick = onLock,
                        containerColor = SunnyOrange,
                        lipColor = SunnyOrangeLip,
                        leadingIcon = Icons.Rounded.Lock,
                        height = 50.dp,
                        modifier = Modifier.width(126.dp),
                        testTag = "lock_parent_button"
                    )
                }
            }

            // Learning Progress Report Card
            item {
                CloudPanelCard(cornerRadius = 32.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Learning Progress",
                            style = MaterialTheme.typography.headlineMedium,
                            color = DeepSlateNavy
                        )

                        gameCatalog.forEach { game ->
                            val prog = gamesMap[game.id]
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = game.title,
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = DeepSlateNavy
                                    )
                                    Text(
                                        text = "${prog?.playsCount ?: 0} sessions • Best score: ${prog?.bestScore ?: 0}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SlateVariant
                                    )
                                }
                                StarRatingTracker(stars = (prog?.stars ?: 0L).toInt())
                            }
                        }
                    }
                }
            }

            // Preferences & Timer Controls Card
            item {
                CloudPanelCard(cornerRadius = 32.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Text(
                            text = "Playland Settings",
                            style = MaterialTheme.typography.headlineMedium,
                            color = DeepSlateNavy
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                                    contentDescription = null,
                                    tint = SkyPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Playful Sound Chimes",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = DeepSlateNavy
                                    )
                                    Text(
                                        text = "Gentle xylophone & celebration audio",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SlateVariant
                                    )
                                }
                            }
                            PlaylandToggleSwitch(
                                checked = soundEnabled,
                                onCheckedChange = { soundEnabled = it },
                                testTag = "sound_toggle_switch"
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Timer,
                                    contentDescription = null,
                                    tint = SkyPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Daily Play Reminder",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = DeepSlateNavy
                                    )
                                    Text(
                                        text = "Healthy screen-time pacing",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SlateVariant
                                    )
                                }
                            }
                            PlaylandToggleSwitch(
                                checked = dailyTimerEnabled,
                                onCheckedChange = { dailyTimerEnabled = it },
                                testTag = "timer_toggle_switch"
                            )
                        }

                        if (dailyTimerEnabled) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Daily Limit: $dailyLimit min",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = DeepSlateNavy
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Surface(
                                        shape = CircleShape,
                                        color = SkyFixed,
                                        border = BorderStroke(2.dp, SkyPastelBlue)
                                    ) {
                                        IconButton(
                                            onClick = { dailyLimit = (dailyLimit - 5).coerceAtLeast(10) }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Remove,
                                                contentDescription = "Decrease limit",
                                                tint = SkyPrimary
                                            )
                                        }
                                    }
                                    Surface(
                                        shape = CircleShape,
                                        color = SkyFixed,
                                        border = BorderStroke(2.dp, SkyPastelBlue)
                                    ) {
                                        IconButton(
                                            onClick = { dailyLimit = (dailyLimit + 5).coerceAtMost(120) }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Add,
                                                contentDescription = "Increase limit",
                                                tint = SkyPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "4-Digit Parent PIN",
                                style = MaterialTheme.typography.labelLarge,
                                color = DeepSlateNavy
                            )
                            PlaylandInputField(
                                value = newPin,
                                onValueChange = { raw ->
                                    newPin = raw.filter { it.isDigit() }.take(4)
                                },
                                placeholder = "4-digit PIN",
                                leadingIcon = Icons.Rounded.Lock,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                testTag = "update_pin_input"
                            )
                        }

                        ChunkyBubbleButton(
                            text = "Save Preferences",
                            onClick = {
                                val validPin = if (newPin.length == 4) newPin else profile.parentPin
                                onSaveSettings(soundEnabled, dailyTimerEnabled, dailyLimit, validPin)
                            },
                            containerColor = EmeraldMint,
                            lipColor = EmeraldMintLip,
                            testTag = "save_parent_settings_button"
                        )
                    }
                }
            }

            // Account & Reset Actions
            item {
                CloudPanelCard(cornerRadius = 32.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        ChunkyBubbleButton(
                            text = "Reset Explorer Progress",
                            onClick = { showResetConfirm = true },
                            containerColor = SunnyOrange,
                            lipColor = SunnyOrangeLip,
                            leadingIcon = Icons.Rounded.DeleteSweep,
                            testTag = "reset_progress_button"
                        )
                        ChunkyBubbleButton(
                            text = "Sign Out",
                            onClick = onSignOut,
                            containerColor = StrawberryPink,
                            lipColor = StrawberryLip,
                            leadingIcon = Icons.AutoMirrored.Rounded.Logout,
                            testTag = "sign_out_button"
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            containerColor = CloudWhite,
            shape = RoundedCornerShape(32.dp),
            title = {
                Text(
                    text = "Reset Stars & Stickers?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = DeepSlateNavy
                )
            },
            text = {
                Text(
                    text = "This clears all earned stars, XP, and collected stickers for ${profile.learnerName}.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SlateVariant
                )
            },
            confirmButton = {
                ChunkyBubbleButton(
                    text = "Yes, Reset",
                    onClick = {
                        showResetConfirm = false
                        onResetProgress()
                    },
                    containerColor = StrawberryPink,
                    lipColor = StrawberryLip,
                    height = 50.dp
                )
            },
            dismissButton = {
                ChunkyBubbleButton(
                    text = "Keep Stars",
                    onClick = { showResetConfirm = false },
                    containerColor = SkyPastelBlue,
                    lipColor = SkyPrimaryLip,
                    height = 50.dp
                )
            }
        )
    }
}
