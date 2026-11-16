package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.auth.authStateFlow
import com.example.auth.signOutFromPlayland
import com.example.data.repository.PlaylandRepository
import com.example.ui.components.ChunkyBubbleButton
import com.example.ui.components.CloudPanelCard
import com.example.ui.components.PlaylandSkyBackground
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.GamePlayScreen
import com.example.ui.screens.ParentZoneScreen
import com.example.ui.screens.PlaylandHubScreen
import com.example.ui.screens.StickersScreen
import com.example.ui.theme.CloudWhite
import com.example.ui.theme.DeepSlateNavy
import com.example.ui.theme.PastelPlaylandTheme
import com.example.ui.theme.SkyFixed
import com.example.ui.theme.SkyPastelBlue
import com.example.ui.theme.SkyPrimary
import com.example.ui.theme.SkyPrimaryLip
import com.example.ui.theme.SlateVariant
import com.example.ui.theme.StrawberryPink
import com.example.ui.viewmodel.PlaylandUiState
import com.example.ui.viewmodel.PlaylandViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

private const val ROUTE_HUB = "playland_hub"
private const val ROUTE_STICKERS = "stickers"
private const val ROUTE_PARENTS = "parent_zone"
private const val ROUTE_GAME = "game/{gameId}"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PastelPlaylandTheme {
                PlaylandRootNavigation()
            }
        }
    }
}

@Composable
fun PlaylandRootNavigation() {
    val auth = Firebase.auth
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val user = currentUser

    if (user == null) {
        AuthScreen(onAuthSuccess = {})
    } else {
        key(user.uid) {
            PlaylandAuthenticatedShell(
                currentUserId = user.uid,
                displayName = user.displayName ?: "Little Explorer"
            )
        }
    }
}

private data class PlaylandNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
private fun PlaylandAuthenticatedShell(
    currentUserId: String,
    displayName: String
) {
    val viewModel: PlaylandViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY])
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                PlaylandViewModel(
                    repository = PlaylandRepository(db),
                    currentUserId = currentUserId,
                    initialDisplayName = displayName
                )
            }
        }
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val isParentUnlocked by viewModel.isParentUnlocked.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    LaunchedEffect(actionMessage) {
        actionMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearActionMessage()
        }
    }

    val navItems = listOf(
        PlaylandNavItem(
            route = ROUTE_HUB,
            label = "Play",
            selectedIcon = Icons.Rounded.SportsEsports,
            unselectedIcon = Icons.Outlined.SportsEsports,
            testTag = "nav_play"
        ),
        PlaylandNavItem(
            route = ROUTE_STICKERS,
            label = "Stickers",
            selectedIcon = Icons.Rounded.EmojiEvents,
            unselectedIcon = Icons.Outlined.EmojiEvents,
            testTag = "nav_stickers"
        ),
        PlaylandNavItem(
            route = ROUTE_PARENTS,
            label = "Parents",
            selectedIcon = Icons.Rounded.Shield,
            unselectedIcon = Icons.Outlined.Shield,
            testTag = "nav_parents"
        )
    )

    val showChrome = currentRoute != ROUTE_GAME

    fun navigateTopLevel(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 840.dp

        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                if (showChrome && !useRail) {
                    PlaylandBottomBar(
                        items = navItems,
                        currentRoute = currentRoute,
                        onSelect = ::navigateTopLevel
                    )
                }
            }
        ) { innerPadding ->
            PlaylandSkyBackground {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding())
                ) {
                    if (showChrome && useRail) {
                        PlaylandNavigationRail(
                            items = navItems,
                            currentRoute = currentRoute,
                            onSelect = ::navigateTopLevel
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .then(
                                if (showChrome) Modifier.statusBarsPadding() else Modifier
                            )
                    ) {
                        when (val state = uiState) {
                            is PlaylandUiState.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CloudPanelCard(cornerRadius = 32.dp) {
                                        Column(
                                            modifier = Modifier.padding(28.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            CircularProgressIndicator(color = StrawberryPink)
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = "Loading Pastel Playland...",
                                                style = MaterialTheme.typography.headlineSmall,
                                                color = DeepSlateNavy
                                            )
                                        }
                                    }
                                }
                            }

                            is PlaylandUiState.Error -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CloudPanelCard(cornerRadius = 32.dp) {
                                        Column(
                                            modifier = Modifier.padding(24.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Cloud Sync Paused",
                                                style = MaterialTheme.typography.headlineMedium,
                                                color = DeepSlateNavy
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = state.message,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = SlateVariant,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            ChunkyBubbleButton(
                                                text = "Sign Out",
                                                onClick = {
                                                    signOutFromPlayland(credentialManager, scope)
                                                },
                                                containerColor = SkyPastelBlue,
                                                lipColor = SkyPrimaryLip
                                            )
                                        }
                                    }
                                }
                            }

                            is PlaylandUiState.Ready -> {
                                NavHost(
                                    navController = navController,
                                    startDestination = ROUTE_HUB
                                ) {
                                    composable(ROUTE_HUB) {
                                        PlaylandHubScreen(
                                            profile = state.profile,
                                            gamesMap = state.gamesMap,
                                            unlockedStickers = state.unlockedStickers,
                                            gameCatalog = viewModel.gameCatalog,
                                            searchQuery = searchQuery,
                                            selectedCategory = selectedCategory,
                                            onSearchChange = viewModel::updateSearchQuery,
                                            onCategorySelect = viewModel::selectCategory,
                                            onPlayGame = { gameId ->
                                                navController.navigate("game/$gameId")
                                            },
                                            onSaveProfile = viewModel::updateLearnerProfile,
                                            onNavigateStickers = { navigateTopLevel(ROUTE_STICKERS) }
                                        )
                                    }

                                    composable(ROUTE_STICKERS) {
                                        StickersScreen(
                                            stickerCatalog = viewModel.stickerCatalog,
                                            unlockedStickers = state.unlockedStickers,
                                            onPlayGame = { gameId ->
                                                navController.navigate("game/$gameId")
                                            }
                                        )
                                    }

                                    composable(ROUTE_PARENTS) {
                                        ParentZoneScreen(
                                            profile = state.profile,
                                            gamesMap = state.gamesMap,
                                            gameCatalog = viewModel.gameCatalog,
                                            isUnlocked = isParentUnlocked,
                                            onAttemptUnlock = { pin ->
                                                viewModel.unlockParentZone(pin, state.profile.parentPin)
                                            },
                                            onLock = viewModel::lockParentZone,
                                            onSaveSettings = viewModel::updateParentSettings,
                                            onResetProgress = viewModel::resetProgress,
                                            onSignOut = {
                                                scope.launch {
                                                    signOutFromPlayland(credentialManager, scope)
                                                }
                                            }
                                        )
                                    }

                                    composable(
                                        route = ROUTE_GAME,
                                        arguments = listOf(navArgument("gameId") { type = NavType.StringType })
                                    ) { entry ->
                                        val gameId = entry.arguments?.getString("gameId") ?: "phonics"
                                        GamePlayScreen(
                                            gameId = gameId,
                                            soundEnabled = state.profile.soundEnabled,
                                            onGameComplete = { stars, score, xpReward ->
                                                viewModel.recordGameResult(gameId, stars, score, xpReward)
                                            },
                                            onBack = { navController.popBackStack() }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaylandBottomBar(
    items: List<PlaylandNavItem>,
    currentRoute: String?,
    onSelect: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 10.dp),
        shape = RoundedCornerShape(32.dp),
        color = CloudWhite,
        border = BorderStroke(3.dp, Color(0xFFE0F2FE)),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                Row(
                    modifier = Modifier
                        .height(52.dp)
                        .clip(CircleShape)
                        .background(if (selected) SkyPastelBlue else Color.Transparent)
                        .testTag(item.testTag)
                        .clickable { onSelect(item.route) }
                        .padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        tint = if (selected) Color.White else SlateVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selected) Color.White else DeepSlateNavy
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaylandNavigationRail(
    items: List<PlaylandNavItem>,
    currentRoute: String?,
    onSelect: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(start = 14.dp, top = 14.dp, bottom = 14.dp)
            .width(96.dp),
        shape = RoundedCornerShape(32.dp),
        color = CloudWhite,
        border = BorderStroke(3.dp, Color(0xFFE0F2FE)),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (selected) SkyFixed else Color.Transparent)
                        .testTag(item.testTag)
                        .clickable { onSelect(item.route) }
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        tint = if (selected) SkyPrimary else SlateVariant,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) SkyPrimary else DeepSlateNavy
                    )
                }
            }
        }
    }
}
