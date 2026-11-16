package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.audio.PlaylandSoundPlayer
import com.example.ui.components.CelebrationParticles
import com.example.ui.components.ChunkyBubbleButton
import com.example.ui.components.CloudPanelCard
import com.example.ui.components.PastelShapeCanvas
import com.example.ui.components.PlaylandSkyBackground
import com.example.ui.components.RewardChip
import com.example.ui.components.StarRatingTracker
import com.example.ui.theme.CloudWhite
import com.example.ui.theme.DeepSlateNavy
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldMintLip
import com.example.ui.theme.EmeraldMintSoft
import com.example.ui.theme.LavenderDream
import com.example.ui.theme.LavenderDreamLip
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
import com.example.ui.theme.SunnyLip
import com.example.ui.theme.SunnyOrange
import com.example.ui.theme.SunnyOrangeLip
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GamePlayScreen(
    gameId: String,
    soundEnabled: Boolean,
    onGameComplete: (stars: Int, score: Int, xpReward: Int) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var sessionSeed by remember { mutableIntStateOf(0) }
    var currentRound by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var mistakes by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    var feedbackBanner by remember { mutableStateOf<String?>(null) }
    var celebrationTick by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    val totalRounds = 5
    val gameTitle = when (gameId) {
        "phonics" -> "ABC Cloud Hop"
        "counting" -> "Sunny Berry Count"
        "shapes" -> "Bubble Shape Match"
        else -> "Dreamy Note Garden"
    }

    fun handleCorrectAnswer(points: Int) {
        score += points
        celebrationTick++
        feedbackBanner = "Yay! Great job!"
        PlaylandSoundPlayer.playSuccessChime(soundEnabled)
        scope.launch {
            delay(750)
            feedbackBanner = null
            if (currentRound + 1 >= totalRounds) {
                isFinished = true
                val earnedStars = when {
                    mistakes == 0 -> 3
                    mistakes <= 2 -> 2
                    else -> 1
                }
                val xpReward = earnedStars * 25 + 15
                onGameComplete(earnedStars, score, xpReward)
            } else {
                currentRound++
            }
        }
    }

    fun handleWrongAnswer() {
        mistakes++
        feedbackBanner = "Almost! Try another bubble!"
        PlaylandSoundPlayer.playGentleBoop(soundEnabled)
    }

    PlaylandSkyBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 820.dp)
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = CircleShape,
                        color = CloudWhite,
                        border = BorderStroke(2.5.dp, SkyPastelBlue),
                        shadowElevation = 3.dp
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("game_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back to Playland",
                                tint = DeepSlateNavy
                            )
                        }
                    }

                    Text(
                        text = gameTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        color = DeepSlateNavy
                    )

                    RewardChip(
                        label = "$score pts",
                        icon = Icons.Rounded.Star,
                        tint = SunnyHoney,
                        backgroundColor = SunnyFixed
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 5-Step Round Indicator Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in 0 until totalRounds) {
                        val activeColor = when {
                            isFinished || i < currentRound -> EmeraldMint
                            i == currentRound -> SunnyHoney
                            else -> Color.White.copy(alpha = 0.7f)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(12.dp)
                                .clip(CircleShape)
                                .background(activeColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (isFinished) {
                    val earnedStars = when {
                        mistakes == 0 -> 3
                        mistakes <= 2 -> 2
                        else -> 1
                    }
                    val xpReward = earnedStars * 25 + 15
                    GameResultCard(
                        gameTitle = gameTitle,
                        earnedStars = earnedStars,
                        score = score,
                        xpReward = xpReward,
                        onPlayAgain = {
                            sessionSeed++
                            currentRound = 0
                            score = 0
                            mistakes = 0
                            isFinished = false
                            feedbackBanner = null
                        },
                        onBackHome = onBack
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedContent(
                            targetState = currentRound to sessionSeed,
                            label = "round_transition"
                        ) { (round, seed) ->
                            when (gameId) {
                                "phonics" -> PhonicsRoundContent(
                                    round = round,
                                    seed = seed,
                                    onCorrect = { handleCorrectAnswer(30) },
                                    onWrong = { handleWrongAnswer() }
                                )
                                "counting" -> CountingRoundContent(
                                    round = round,
                                    seed = seed,
                                    onCorrect = { handleCorrectAnswer(30) },
                                    onWrong = { handleWrongAnswer() }
                                )
                                "shapes" -> ShapesRoundContent(
                                    round = round,
                                    seed = seed,
                                    onCorrect = { handleCorrectAnswer(30) },
                                    onWrong = { handleWrongAnswer() }
                                )
                                else -> MusicRoundContent(
                                    round = round,
                                    seed = seed,
                                    soundEnabled = soundEnabled,
                                    onCorrect = { handleCorrectAnswer(35) },
                                    onWrong = { handleWrongAnswer() }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        feedbackBanner?.let { msg ->
                            val isGood = msg.startsWith("Yay")
                            Surface(
                                shape = CircleShape,
                                color = if (isGood) EmeraldMintSoft else SunnyFixed,
                                border = BorderStroke(2.dp, if (isGood) EmeraldMint else SunnyHoney)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isGood) Icons.Rounded.CheckCircle else Icons.Rounded.AutoAwesome,
                                        contentDescription = null,
                                        tint = if (isGood) EmeraldMintLip else SunnyLip
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = msg,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = DeepSlateNavy
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (celebrationTick > 0) {
                CelebrationParticles(triggerKey = celebrationTick)
            }
        }
    }
}

private data class PhonicsPrompt(
    val letter: String,
    val word: String,
    val clue: String,
    val options: List<String>
)

@Composable
private fun PhonicsRoundContent(
    round: Int,
    seed: Int,
    onCorrect: () -> Unit,
    onWrong: () -> Unit
) {
    val prompts = remember(seed) {
        listOf(
            PhonicsPrompt("A", "Apple", "Crunchy red fruit", listOf("A", "B", "M")),
            PhonicsPrompt("B", "Bear", "Cuddly forest friend", listOf("D", "B", "S")),
            PhonicsPrompt("C", "Cloud", "Fluffy white sky pillow", listOf("C", "P", "T")),
            PhonicsPrompt("S", "Star", "Shines bright at night", listOf("R", "S", "L")),
            PhonicsPrompt("M", "Moon", "Glowing bedtime crescent", listOf("N", "K", "M"))
        )
    }
    val prompt = prompts[round % prompts.size]
    val colors = listOf(
        SkyPastelBlue to SkyPrimaryLip,
        StrawberryPink to StrawberryLip,
        SunnyOrange to SunnyOrangeLip
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        CloudPanelCard(cornerRadius = 32.dp) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Which letter starts this word?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = SlateVariant
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "_ ${prompt.word.drop(1)}",
                    style = MaterialTheme.typography.displayLarge,
                    color = DeepSlateNavy
                )
                Spacer(modifier = Modifier.height(8.dp))
                RewardChip(
                    label = prompt.clue,
                    icon = Icons.Rounded.AutoAwesome,
                    tint = SkyPastelBlue,
                    backgroundColor = SkyFixed
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            prompt.options.forEachIndexed { idx, letter ->
                val (bg, lip) = colors[idx % colors.size]
                ChunkyBubbleButton(
                    text = letter,
                    onClick = {
                        if (letter == prompt.letter) onCorrect() else onWrong()
                    },
                    containerColor = bg,
                    lipColor = lip,
                    height = 76.dp,
                    modifier = Modifier.weight(1f),
                    testTag = "phonics_option_$letter"
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CountingRoundContent(
    round: Int,
    seed: Int,
    onCorrect: () -> Unit,
    onWrong: () -> Unit
) {
    val targets = remember(seed) { listOf(3, 5, 2, 6, 4) }
    val target = targets[round % targets.size]
    val options = remember(target) {
        listOf(
            target,
            (target + 1).coerceAtMost(8),
            (target - 1).coerceAtLeast(1)
        ).distinct().let { base ->
            if (base.size < 3) (base + listOf(7, 2)).distinct().take(3) else base
        }.sorted()
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        CloudPanelCard(cornerRadius = 32.dp) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "How many sweet berries do you see?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = DeepSlateNavy,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(18.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    maxItemsInEachRow = 4
                ) {
                    repeat(target) {
                        Surface(
                            shape = CircleShape,
                            color = StrawberryFixed,
                            border = BorderStroke(2.5.dp, StrawberryPink),
                            modifier = Modifier.size(60.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.Favorite,
                                    contentDescription = "Berry",
                                    tint = StrawberryPink,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            val colors = listOf(
                EmeraldMint to EmeraldMintLip,
                SkyPastelBlue to SkyPrimaryLip,
                StrawberryPink to StrawberryLip
            )
            options.forEachIndexed { idx, num ->
                val (bg, lip) = colors[idx % colors.size]
                ChunkyBubbleButton(
                    text = num.toString(),
                    onClick = {
                        if (num == target) onCorrect() else onWrong()
                    },
                    containerColor = bg,
                    lipColor = lip,
                    height = 76.dp,
                    modifier = Modifier.weight(1f),
                    testTag = "counting_option_$num"
                )
            }
        }
    }
}

private data class ShapePrompt(
    val shapeId: String,
    val label: String,
    val color: Color,
    val choices: List<String>
)

@Composable
private fun ShapesRoundContent(
    round: Int,
    seed: Int,
    onCorrect: () -> Unit,
    onWrong: () -> Unit
) {
    val prompts = remember(seed) {
        listOf(
            ShapePrompt("star", "Star", SunnyHoney, listOf("circle", "star", "triangle")),
            ShapePrompt("heart", "Heart", StrawberryPink, listOf("heart", "square", "circle")),
            ShapePrompt("triangle", "Triangle", EmeraldMint, listOf("diamond", "triangle", "star")),
            ShapePrompt("circle", "Circle", SunnyOrange, listOf("circle", "heart", "square")),
            ShapePrompt("diamond", "Diamond", LavenderDream, listOf("star", "diamond", "triangle"))
        )
    }
    val prompt = prompts[round % prompts.size]

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        CloudPanelCard(cornerRadius = 32.dp) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tap the matching ${prompt.label}!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = DeepSlateNavy
                )
                Spacer(modifier = Modifier.height(14.dp))
                PastelShapeCanvas(
                    shapeType = prompt.shapeId,
                    color = prompt.color,
                    modifier = Modifier.size(120.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            prompt.choices.forEach { choice ->
                val choiceColor = when (choice) {
                    "star" -> SunnyHoney
                    "heart" -> StrawberryPink
                    "triangle" -> EmeraldMint
                    "circle" -> SunnyOrange
                    "diamond" -> LavenderDream
                    else -> SkyPastelBlue
                }
                CloudPanelCard(
                    modifier = Modifier.weight(1f),
                    cornerRadius = 28.dp,
                    onClick = {
                        if (choice == prompt.shapeId) onCorrect() else onWrong()
                    },
                    testTag = "shape_option_$choice"
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PastelShapeCanvas(
                            shapeType = choice,
                            color = choiceColor,
                            modifier = Modifier.size(62.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = choice.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelMedium,
                            color = DeepSlateNavy
                        )
                    }
                }
            }
        }
    }
}

private data class XylophoneBar(
    val label: String,
    val frequencyHz: Double,
    val color: Color
)

@Composable
private fun MusicRoundContent(
    round: Int,
    seed: Int,
    soundEnabled: Boolean,
    onCorrect: () -> Unit,
    onWrong: () -> Unit
) {
    val bars = remember {
        listOf(
            XylophoneBar("Do", 523.25, StrawberryPink),
            XylophoneBar("Re", 587.33, SunnyOrange),
            XylophoneBar("Mi", 659.25, SunnyHoney),
            XylophoneBar("Fa", 698.46, EmeraldMint),
            XylophoneBar("So", 783.99, SkyPastelBlue)
        )
    }

    val patterns = remember(seed) {
        listOf(
            listOf(0, 2),
            listOf(1, 3),
            listOf(0, 2, 4),
            listOf(4, 2, 0),
            listOf(1, 2, 4)
        )
    }
    val targetPattern = patterns[round % patterns.size]
    val userTaps = remember(round, seed) { mutableStateListOf<Int>() }
    var highlightedBarIndex by remember { mutableIntStateOf(-1) }

    fun replayPattern() {
        PlaylandSoundPlayer.playNoteSequence(
            frequencies = targetPattern.map { bars[it].frequencyHz },
            enabled = soundEnabled,
            onStepHighlight = { step ->
                highlightedBarIndex = if (step in targetPattern.indices) targetPattern[step] else -1
            }
        )
    }

    LaunchedEffect(round, seed) {
        delay(250)
        replayPattern()
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        CloudPanelCard(cornerRadius = 32.dp) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tap the glowing notes in order!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = DeepSlateNavy,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    targetPattern.forEachIndexed { idx, barIdx ->
                        val done = idx < userTaps.size
                        Surface(
                            shape = CircleShape,
                            color = if (done) bars[barIdx].color else Color(0xFFE2E8F0),
                            border = BorderStroke(2.dp, bars[barIdx].color)
                        ) {
                            Text(
                                text = bars[barIdx].label,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (done) Color.White else DeepSlateNavy,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                ChunkyBubbleButton(
                    text = "Listen Again",
                    onClick = { replayPattern() },
                    containerColor = LavenderDream,
                    lipColor = LavenderDreamLip,
                    leadingIcon = Icons.AutoMirrored.Rounded.VolumeUp,
                    height = 48.dp,
                    modifier = Modifier.width(190.dp),
                    testTag = "replay_melody_button"
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            bars.forEachIndexed { index, bar ->
                val isLit = highlightedBarIndex == index
                val scale by animateFloatAsState(
                    targetValue = if (isLit) 1.09f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "bar_scale"
                )
                val barHeight = (150 - index * 10).dp

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(barHeight)
                        .scale(scale)
                        .testTag("xylophone_bar_${bar.label}")
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (isLit) SunnyHoney else bar.color)
                        .clickable {
                            PlaylandSoundPlayer.playTone(bar.frequencyHz, 220, soundEnabled)
                            val expectedIndex = targetPattern[userTaps.size]
                            if (index == expectedIndex) {
                                userTaps.add(index)
                                if (userTaps.size == targetPattern.size) {
                                    onCorrect()
                                }
                            } else {
                                userTaps.clear()
                                onWrong()
                            }
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.MusicNote,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = bar.label,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GameResultCard(
    gameTitle: String,
    earnedStars: Int,
    score: Int,
    xpReward: Int,
    onPlayAgain: () -> Unit,
    onBackHome: () -> Unit
) {
    CloudPanelCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 36.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Awesome Job!",
                style = MaterialTheme.typography.displayMedium,
                color = DeepSlateNavy
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "You completed $gameTitle",
                style = MaterialTheme.typography.bodyLarge,
                color = SlateVariant
            )

            Spacer(modifier = Modifier.height(18.dp))
            StarRatingTracker(stars = earnedStars, starSize = 48.dp)
            Spacer(modifier = Modifier.height(18.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RewardChip(
                    label = "$score pts",
                    icon = Icons.Rounded.Star,
                    tint = SunnyHoney,
                    backgroundColor = SunnyFixed
                )
                RewardChip(
                    label = "+$xpReward XP",
                    icon = Icons.Rounded.AutoAwesome,
                    tint = EmeraldMint,
                    backgroundColor = EmeraldMintSoft
                )
            }

            if (earnedStars >= 2) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SunnyFixed,
                    border = BorderStroke(2.dp, SunnyHoney)
                ) {
                    Text(
                        text = "Sticker Unlocked in your Sticker Book!",
                        style = MaterialTheme.typography.labelMedium,
                        color = DeepSlateNavy,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ChunkyBubbleButton(
                    text = "Play Again",
                    onClick = onPlayAgain,
                    containerColor = EmeraldMint,
                    lipColor = EmeraldMintLip,
                    leadingIcon = Icons.Rounded.Replay,
                    modifier = Modifier.weight(1f),
                    testTag = "play_again_button"
                )
                ChunkyBubbleButton(
                    text = "Home",
                    onClick = onBackHome,
                    containerColor = SkyPastelBlue,
                    lipColor = SkyPrimaryLip,
                    leadingIcon = Icons.Rounded.Refresh,
                    modifier = Modifier.weight(1f),
                    testTag = "back_home_button"
                )
            }
        }
    }
}
