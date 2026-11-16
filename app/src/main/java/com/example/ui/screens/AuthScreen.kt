package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.auth.attemptAutoSignIn
import com.example.auth.onGoogleSignInClicked
import com.example.ui.components.ChunkyBubbleButton
import com.example.ui.components.CloudPanelCard
import com.example.ui.components.PlaylandSkyBackground
import com.example.ui.components.RewardChip
import com.example.ui.theme.DeepSlateNavy
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldMintSoft
import com.example.ui.theme.ErrorContainerLight
import com.example.ui.theme.OnErrorContainerLight
import com.example.ui.theme.SkyFixed
import com.example.ui.theme.SkyPastelBlue
import com.example.ui.theme.SlateVariant
import com.example.ui.theme.StrawberryLip
import com.example.ui.theme.StrawberryPink
import com.example.ui.theme.SunnyFixed
import com.example.ui.theme.SunnyHoney

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    var isCheckingSession by remember { mutableStateOf(true) }
    var isSigningIn by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = {
                isCheckingSession = false
                onAuthSuccess()
            },
            onUnauthenticated = {
                isCheckingSession = false
            },
            scope = scope
        )
    }

    PlaylandSkyBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            CloudPanelCard(
                modifier = Modifier.widthIn(max = 480.dp),
                cornerRadius = 36.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_playland_star_1794833799094),
                        contentDescription = "Pastel Playland Star Mascot",
                        modifier = Modifier
                            .size(118.dp)
                            .clip(CircleShape)
                            .border(4.dp, SkyPastelBlue.copy(alpha = 0.45f), CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Pastel Playland",
                        style = MaterialTheme.typography.displayMedium,
                        color = DeepSlateNavy,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "A cheerful play-and-learn world for early explorers ages 3–8!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RewardChip(
                            label = "4 Mini-Games",
                            icon = Icons.Rounded.AutoAwesome,
                            tint = SkyPastelBlue,
                            backgroundColor = SkyFixed
                        )
                        RewardChip(
                            label = "Cloud Stars",
                            icon = Icons.Rounded.CloudDone,
                            tint = EmeraldMint,
                            backgroundColor = EmeraldMintSoft
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn()
                    ) {
                        errorMessage?.let { msg ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                shape = MaterialTheme.shapes.medium,
                                color = ErrorContainerLight
                            ) {
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnErrorContainerLight,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }

                    if (isCheckingSession || isSigningIn) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            CircularProgressIndicator(
                                color = StrawberryPink,
                                strokeWidth = 4.dp,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (isCheckingSession) "Opening the clouds..." else "Signing in with Google...",
                                style = MaterialTheme.typography.labelMedium,
                                color = SlateVariant
                            )
                        }
                    } else {
                        ChunkyBubbleButton(
                            text = "Sign in with Google",
                            onClick = {
                                errorMessage = null
                                isSigningIn = true
                                onGoogleSignInClicked(
                                    context = context,
                                    credentialManager = credentialManager,
                                    onAuthSuccess = {
                                        isSigningIn = false
                                        onAuthSuccess()
                                    },
                                    onAuthError = { msg ->
                                        isSigningIn = false
                                        errorMessage = msg
                                    },
                                    scope = scope,
                                    onAuthCancelled = {
                                        isSigningIn = false
                                    }
                                )
                            },
                            containerColor = StrawberryPink,
                            lipColor = StrawberryLip,
                            leadingIcon = Icons.AutoMirrored.Rounded.Login,
                            height = 66.dp,
                            testTag = "google_sign_in_button"
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    RewardChip(
                        label = "Kid-safe & Parent-controlled",
                        icon = Icons.Rounded.Star,
                        tint = SunnyHoney,
                        backgroundColor = SunnyFixed
                    )
                }
            }
        }
    }
}
