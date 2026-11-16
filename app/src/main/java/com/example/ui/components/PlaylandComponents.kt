package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CloudBevelShadow
import com.example.ui.theme.CloudBorderBlue
import com.example.ui.theme.CloudWhite
import com.example.ui.theme.DeepSlateNavy
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldMintLip
import com.example.ui.theme.InputSurfaceBlue
import com.example.ui.theme.LavenderDream
import com.example.ui.theme.SkyCanvasBottom
import com.example.ui.theme.SkyCanvasTop
import com.example.ui.theme.SkyPastelBlue
import com.example.ui.theme.SkyPrimary
import com.example.ui.theme.SkyPrimaryLip
import com.example.ui.theme.SlateVariant
import com.example.ui.theme.SoftOutlineVariant
import com.example.ui.theme.StrawberryLip
import com.example.ui.theme.StrawberryPink
import com.example.ui.theme.SunnyHoney
import com.example.ui.theme.SunnyLip
import com.example.ui.theme.SunnyOrange
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Sky pastel gradient background with soft floating vector cloud silhouettes.
 */
@Composable
fun PlaylandSkyBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cloud_drift")
    val drift by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SkyCanvasTop, SkyCanvasBottom)
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            fun drawFluffyCloud(centerX: Float, centerY: Float, scale: Float, alpha: Float) {
                val color = Color.White.copy(alpha = alpha)
                drawCircle(color, radius = 38f * scale, center = Offset(centerX, centerY))
                drawCircle(color, radius = 28f * scale, center = Offset(centerX - 32f * scale, centerY + 8f * scale))
                drawCircle(color, radius = 30f * scale, center = Offset(centerX + 34f * scale, centerY + 6f * scale))
                drawRoundRect(
                    color = color,
                    topLeft = Offset(centerX - 46f * scale, centerY + 4f * scale),
                    size = Size(92f * scale, 30f * scale),
                    cornerRadius = CornerRadius(24f * scale, 24f * scale)
                )
            }

            drawFluffyCloud(w * 0.16f + drift, h * 0.11f, 1.3f, 0.72f)
            drawFluffyCloud(w * 0.84f - drift, h * 0.18f, 1.5f, 0.65f)
            drawFluffyCloud(w * 0.22f - drift * 0.7f, h * 0.56f, 1.1f, 0.45f)
            drawFluffyCloud(w * 0.82f + drift * 0.8f, h * 0.78f, 1.4f, 0.55f)
        }
        content()
    }
}

/**
 * Tactile 3D squishy bubble button with glossy top highlight and 5dp bottom lip
 * that physically depresses by 4dp when pressed.
 */
@Composable
fun ChunkyBubbleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = StrawberryPink,
    lipColor: Color = StrawberryLip,
    contentColor: Color = Color.White,
    leadingIcon: ImageVector? = null,
    height: Dp = 62.dp,
    enabled: Boolean = true,
    testTag: String = "chunky_button"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val lipHeight by animateDpAsState(
        targetValue = if (isPressed || !enabled) 1.dp else 5.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "lip"
    )
    val pressOffset by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "press_offset"
    )

    val effectiveBg = if (enabled) containerColor else SoftOutlineVariant
    val effectiveLip = if (enabled) lipColor else Color(0xFF94A3B8)

    Box(
        modifier = modifier
            .height(height + 5.dp)
            .testTag(testTag)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
    ) {
        // Bottom 3D lip shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(28.dp))
                .background(effectiveLip)
        )
        // Top glossy face
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset { IntOffset(0, pressOffset.roundToPx()) }
                .clip(RoundedCornerShape(28.dp))
                .background(effectiveBg)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.28f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        // Keep lipHeight referenced to satisfy lint/compose tracking
        if (lipHeight < 0.dp) Spacer(Modifier.size(0.dp))
    }
}

/**
 * Pure white pillowy cloud container card with soft sky border and 8dp bottom bevel shelf.
 */
@Composable
fun CloudPanelCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 32.dp,
    borderColor: Color = CloudBorderBlue,
    onClick: (() -> Unit)? = null,
    testTag: String? = null,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressOffset by animateDpAsState(
        targetValue = if (isPressed && onClick != null) 4.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "card_press"
    )

    val taggedModifier = if (testTag != null) modifier.testTag(testTag) else modifier

    Box(
        modifier = taggedModifier.then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = onClick
                )
            } else {
                Modifier
            }
        )
    ) {
        // 8dp bottom foam shelf bevel
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = 6.dp)
                .clip(RoundedCornerShape(cornerRadius))
                .background(CloudBevelShadow)
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
                .offset { IntOffset(0, pressOffset.roundToPx()) },
            shape = RoundedCornerShape(cornerRadius),
            color = CloudWhite,
            border = BorderStroke(3.dp, borderColor),
            shadowElevation = 2.dp
        ) {
            content()
        }
    }
}

/**
 * Cloud Game Card with illustrative banner, category pill, title, and 3-star rating tracker.
 */
@Composable
fun CloudGameCard(
    title: String,
    subtitle: String,
    ageTag: String,
    bannerResId: Int,
    starsEarned: Int,
    accentColor: Color,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "game_card"
) {
    CloudPanelCard(
        modifier = modifier,
        cornerRadius = 32.dp,
        onClick = onPlayClick,
        testTag = testTag
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(156.dp)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                Image(
                    painter = painterResource(id = bannerResId),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Age pill pinned top-left
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    shape = CircleShape,
                    color = CloudWhite.copy(alpha = 0.94f),
                    border = BorderStroke(2.dp, accentColor.copy(alpha = 0.45f))
                ) {
                    Text(
                        text = ageTag,
                        style = MaterialTheme.typography.labelSmall,
                        color = DeepSlateNavy,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 18.dp, top = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = DeepSlateNavy,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StarRatingTracker(stars = starsEarned)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(14.dp))

                ChunkyBubbleButton(
                    text = if (starsEarned > 0) "Play Again" else "Play Now",
                    onClick = onPlayClick,
                    containerColor = accentColor,
                    lipColor = darkenForLip(accentColor),
                    height = 54.dp,
                    testTag = "${testTag}_play_button"
                )
            }
        }
    }
}

@Composable
fun StarRatingTracker(
    stars: Int,
    maxStars: Int = 3,
    starSize: Dp = 24.dp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        for (i in 1..maxStars) {
            val filled = i <= stars
            Icon(
                imageVector = if (filled) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                contentDescription = if (filled) "Earned star" else "Empty star",
                tint = if (filled) SunnyHoney else SoftOutlineVariant,
                modifier = Modifier.size(starSize)
            )
        }
    }
}

/**
 * Capsule-shaped Reward Chip with pastel fill, saturated border, and left icon badge.
 */
@Composable
fun RewardChip(
    label: String,
    icon: ImageVector,
    tint: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
        ),
        shape = CircleShape,
        color = backgroundColor,
        border = BorderStroke(2.dp, tint.copy(alpha = 0.55f))
    ) {
        Row(
            modifier = Modifier.padding(start = 6.dp, end = 14.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(tint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = DeepSlateNavy,
                maxLines = 1
            )
        }
    }
}

/**
 * Thick, rounded capsule Star Progress Meter with segmented candy bars and a bouncing star token.
 */
@Composable
fun StarProgressMeter(
    xp: Long,
    level: Long,
    modifier: Modifier = Modifier
) {
    val progressInLevel = ((xp % 100L).toFloat() / 100f).coerceIn(0.08f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progressInLevel,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "xp_progress"
    )

    val bounceTransition = rememberInfiniteTransition(label = "star_bounce")
    val bounceScale by bounceTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_scale"
    )

    CloudPanelCard(
        modifier = modifier,
        cornerRadius = 28.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Level $level Explorer",
                    style = MaterialTheme.typography.headlineSmall,
                    color = DeepSlateNavy
                )
                Text(
                    text = "${xp % 100}/100 XP",
                    style = MaterialTheme.typography.labelMedium,
                    color = SkyPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                // Track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(22.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0F2FE))
                        .border(2.dp, Color(0xFFBAE6FD), CircleShape)
                ) {
                    // Candy fill bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(22.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(SunnyHoney, SunnyOrange)
                                )
                            )
                    )
                    // Candy segment dividers
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        repeat(4) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(22.dp)
                                    .background(Color.White.copy(alpha = 0.45f))
                            )
                        }
                    }
                }

                // Bouncing leading star badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(30.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size((30 * bounceScale).dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(SunnyHoney)
                            .border(2.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = "Progress star",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tall 58dp rounded input field with pale blue interior fill and thick friendly Sky Pastel focus border.
 */
@Composable
fun PlaylandInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    testTag: String = "playland_input"
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor = if (isFocused) SkyPastelBlue else Color(0xFFBAE6FD)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(24.dp),
        color = InputSurfaceBlue,
        border = BorderStroke(if (isFocused) 3.dp else 2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = SkyPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateVariant.copy(alpha = 0.65f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = DeepSlateNavy,
                        fontWeight = FontWeight.Bold
                    ),
                    cursorBrush = SolidColor(SkyPrimary),
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    visualTransformation = visualTransformation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocused = it.isFocused }
                )
            }
        }
    }
}

/**
 * Bouncy pill toggle switch with oversized candy handle.
 */
@Composable
fun PlaylandToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = EmeraldMint,
    testTag: String = "playland_switch"
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 28.dp else 4.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "switch_thumb"
    )

    Box(
        modifier = modifier
            .size(width = 64.dp, height = 38.dp)
            .testTag(testTag)
            .clip(CircleShape)
            .background(if (checked) activeColor else Color(0xFFCBD5E1))
            .border(
                width = 2.5.dp,
                color = if (checked) EmeraldMintLip else Color(0xFF94A3B8),
                shape = CircleShape
            )
            .clickable(role = Role.Switch) { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(thumbOffset.roundToPx(), 0) }
                .size(30.dp)
                .shadow(3.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

/**
 * Bouncy rounded bubble checkbox.
 */
@Composable
fun PlaylandCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "playland_checkbox"
) {
    Box(
        modifier = modifier
            .size(36.dp)
            .testTag(testTag)
            .clip(RoundedCornerShape(12.dp))
            .background(if (checked) EmeraldMint else InputSurfaceBlue)
            .border(
                width = 2.5.dp,
                color = if (checked) EmeraldMintLip else SkyPastelBlue,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(role = Role.Checkbox) { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = checked,
            enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
            exit = scaleOut()
        ) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = "Checked",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

/**
 * Celebratory popping pastel particle overlay shown on round completion.
 */
@Composable
fun CelebrationParticles(
    triggerKey: Int,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(triggerKey) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing)
        )
    }

    val particles = remember {
        listOf(
            ParticleSpec(0.15f, -0.35f, StrawberryPink, 14f),
            ParticleSpec(0.32f, -0.52f, SunnyHoney, 18f),
            ParticleSpec(0.48f, -0.42f, SkyPastelBlue, 15f),
            ParticleSpec(0.68f, -0.50f, EmeraldMint, 17f),
            ParticleSpec(0.84f, -0.36f, LavenderDream, 14f),
            ParticleSpec(0.24f, -0.22f, SunnyOrange, 12f),
            ParticleSpec(0.75f, -0.24f, StrawberryPink, 13f)
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val p = progress.value
        if (p <= 0f || p >= 1f) return@Canvas
        val alpha = (1f - p).coerceIn(0f, 1f)
        particles.forEachIndexed { idx, spec ->
            val startX = size.width * spec.xFraction
            val startY = size.height * 0.55f
            val angle = (idx * 0.6f)
            val cx = startX + cos(angle) * 60f * p
            val cy = startY + size.height * spec.yTravel * p + (p * p * 120f)
            drawCircle(
                color = spec.color.copy(alpha = alpha),
                radius = spec.radius * (1f - p * 0.3f),
                center = Offset(cx, cy)
            )
        }
    }
}

private data class ParticleSpec(
    val xFraction: Float,
    val yTravel: Float,
    val color: Color,
    val radius: Float
)

/**
 * Draws crisp, glossy pastel geometric shapes for Bubble Shape Match.
 */
@Composable
fun PastelShapeCanvas(
    shapeType: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val radius = minOf(w, h) * 0.38f

        when (shapeType) {
            "circle" -> {
                drawCircle(color = color, radius = radius, center = Offset(cx, cy))
                drawCircle(
                    color = Color.White.copy(alpha = 0.35f),
                    radius = radius * 0.28f,
                    center = Offset(cx - radius * 0.35f, cy - radius * 0.35f)
                )
            }
            "square" -> {
                val side = radius * 1.65f
                drawRoundRect(
                    color = color,
                    topLeft = Offset(cx - side / 2f, cy - side / 2f),
                    size = Size(side, side),
                    cornerRadius = CornerRadius(24f, 24f)
                )
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.30f),
                    topLeft = Offset(cx - side * 0.32f, cy - side * 0.36f),
                    size = Size(side * 0.55f, side * 0.14f),
                    cornerRadius = CornerRadius(12f, 12f)
                )
            }
            "triangle" -> {
                val path = Path().apply {
                    moveTo(cx, cy - radius)
                    lineTo(cx + radius * 0.95f, cy + radius * 0.8f)
                    lineTo(cx - radius * 0.95f, cy + radius * 0.8f)
                    close()
                }
                drawPath(path, color = color)
            }
            "star" -> {
                val path = Path()
                val inner = radius * 0.48f
                for (i in 0 until 10) {
                    val r = if (i % 2 == 0) radius else inner
                    val angle = (-PI / 2.0) + (i * PI / 5.0)
                    val x = cx + (r * cos(angle)).toFloat()
                    val y = cy + (r * sin(angle)).toFloat()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                drawPath(path, color = color)
            }
            "heart" -> {
                val path = Path().apply {
                    moveTo(cx, cy + radius * 0.85f)
                    cubicTo(
                        cx - radius * 1.25f, cy + radius * 0.15f,
                        cx - radius * 0.85f, cy - radius * 0.95f,
                        cx, cy - radius * 0.3f
                    )
                    cubicTo(
                        cx + radius * 0.85f, cy - radius * 0.95f,
                        cx + radius * 1.25f, cy + radius * 0.15f,
                        cx, cy + radius * 0.85f
                    )
                    close()
                }
                drawPath(path, color = color)
            }
            else -> {
                // Diamond
                val path = Path().apply {
                    moveTo(cx, cy - radius)
                    lineTo(cx + radius * 0.82f, cy)
                    lineTo(cx, cy + radius)
                    lineTo(cx - radius * 0.82f, cy)
                    close()
                }
                drawPath(path, color = color)
            }
        }
    }
}

fun darkenForLip(color: Color): Color {
    return when (color) {
        SkyPastelBlue -> SkyPrimaryLip
        StrawberryPink -> StrawberryLip
        EmeraldMint -> EmeraldMintLip
        SunnyHoney -> SunnyLip
        SunnyOrange -> Color(0xFFEA580C)
        LavenderDream -> Color(0xFF7C3AED)
        else -> color.copy(
            red = (color.red * 0.78f).coerceIn(0f, 1f),
            green = (color.green * 0.78f).coerceIn(0f, 1f),
            blue = (color.blue * 0.78f).coerceIn(0f, 1f)
        )
    }
}
