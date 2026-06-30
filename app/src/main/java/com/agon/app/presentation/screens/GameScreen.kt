package com.agon.app.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.domain.model.*
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

// ═══════════════════════════════════════════════════════════════════════════
//  LUXURY THEME — Domino Royale
// ═══════════════════════════════════════════════════════════════════════════
private val DeepForest    = Color(0xFF06180A)
private val DarkGreen     = Color(0xFF0D2E14)
private val FeltGreen     = Color(0xFF1A4D28)
private val MidGreen      = Color(0xFF2D6E3E)
private val LightGreen    = Color(0xFF4CAF50)
private val RoyalGold     = Color(0xFFD4AF37)
private val BrightGold    = Color(0xFFFFD700)
private val PaleGold      = Color(0xFFFFF8DC)
private val BurnishedGold = Color(0xFFB8860B)
private val WarmWood      = Color(0xFF3D2817)
private val RichWood      = Color(0xFF5C3A1E)
private val DarkWood      = Color(0xFF2A1A0E)
private val Ivory         = Color(0xFFFFF8F0)
private val Cream         = Color(0xFFF5F0E6)
private val Charcoal      = Color(0xFF1A1A1A)
private val SoftRed       = Color(0xFFE53935)
private val SoftGreen     = Color(0xFF43A047)

private val GoldGradient = Brush.linearGradient(
    colors = listOf(BurnishedGold, RoyalGold, BrightGold, RoyalGold, BurnishedGold),
    start = Offset(0f, 0f),
    end = Offset(400f, 400f)
)

private val CardGradient = Brush.linearGradient(
    colors = listOf(Ivory, Cream, Ivory),
    start = Offset(0f, 0f),
    end = Offset(0f, 200f)
)

// ═══════════════════════════════════════════════════════════════════════════
//  PARTICLE SYSTEM — Confetti on win
// ═══════════════════════════════════════════════════════════════════════════
private data class Particle(
    val id: Int,
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val size: Float,
    val color: Color,
    val rotation: Float,
    val rotationSpeed: Float,
    val life: Float = 1f
)

@Composable
private fun ConfettiOverlay(
    active: Boolean,
    modifier: Modifier = Modifier
) {
    var particles by remember { mutableStateOf<List<Particle>>(emptyList()) }
    var tick by remember { mutableIntStateOf(0) }

    LaunchedEffect(active) {
        if (active) {
            particles = List(80) {
                Particle(
                    id = it,
                    x = Random.nextFloat() * 1000f,
                    y = -Random.nextFloat() * 200f,
                    vx = (Random.nextFloat() - 0.5f) * 8f,
                    vy = Random.nextFloat() * 6f + 2f,
                    size = Random.nextFloat() * 8f + 4f,
                    color = listOf(RoyalGold, BrightGold, SoftRed, SoftGreen, Ivory)[Random.nextInt(5)],
                    rotation = Random.nextFloat() * 360f,
                    rotationSpeed = (Random.nextFloat() - 0.5f) * 20f
                )
            }
            while (active) {
                delay(16)
                tick++
                particles = particles.mapNotNull { p ->
                    val newLife = p.life - 0.008f
                    if (newLife <= 0) return@mapNotNull null
                    p.copy(
                        x = p.x + p.vx,
                        y = p.y + p.vy,
                        vy = p.vy + 0.15f,
                        rotation = p.rotation + p.rotationSpeed,
                        life = newLife
                    )
                }
            }
        } else {
            particles = emptyList()
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { p ->
            drawCircle(
                color = p.color.copy(alpha = p.life),
                radius = p.size,
                center = Offset(p.x, p.y)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  AMBIENT BACKGROUND — Animated felt with light rays
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun LuxuryBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(60000, easing = LinearEasing)),
        label = "rotate"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(tween(4000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Base gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(FeltGreen, DarkGreen, DeepForest),
                        center = Offset(0.5f, 0.4f),
                        radius = 1.2f
                    )
                )
        )

        // Animated light rays
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val centerY = size.height / 3f
            for (i in 0..5) {
                val rayAngle = angle + i * 60f
                val rad = Math.toRadians(rayAngle.toDouble())
                val endX = centerX + cos(rad).toFloat() * size.width * 1.5f
                val endY = centerY + sin(rad).toFloat() * size.height * 1.5f
                drawLine(
                    color = RoyalGold.copy(alpha = pulse * 0.03f),
                    start = Offset(centerX, centerY),
                    end = Offset(endX, endY),
                    strokeWidth = 80f
                )
            }
        }

        // Subtle vignette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.Transparent, DeepForest.copy(alpha = 0.6f)),
                        radius = 1.3f
                    )
                )
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  GLASS CARD — Glassmorphism container
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.White.copy(alpha = 0.02f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            RoyalGold.copy(alpha = 0.3f),
                            Color.White.copy(alpha = 0.1f),
                            RoyalGold.copy(alpha = 0.3f)
                        )
                    ),
                    shape = shape
                )
                .padding(16.dp)
        ) {
            Column(content = content)
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  3D DOMINO TILE — With tilt, shadow, and premium feel
// ═══════════════════════════════════════════════════════════════════════════
@Composable
fun LuxuryDominoTile(
    tile: DominoTile,
    modifier: Modifier = Modifier,
    isMini: Boolean = false,
    isPlaceholder: Boolean = false,
    isSelected: Boolean = false,
    isLegal: Boolean = false,
    isHidden: Boolean = false,
    horizontal: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    var isHovered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = when {
            isSelected -> 1.12f
            isHovered -> 1.06f
            isLegal -> 1.03f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = 400f),
        label = "scale"
    )
    val elevation by animateDpAsState(
        targetValue = when {
            isSelected -> 16.dp
            isHovered -> 12.dp
            isLegal -> 6.dp
            else -> 3.dp
        },
        animationSpec = tween(200),
        label = "elevation"
    )
    val glowAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.6f else if (isLegal) 0.3f else 0f,
        animationSpec = tween(300),
        label = "glow"
    )
    val yOffset by animateFloatAsState(
        targetValue = if (isSelected) -12f else if (isHovered) -6f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "yOffset"
    )

    val tileWidth = if (isMini) 36.dp else if (horizontal) 52.dp else 56.dp
    val tileHeight = if (isMini) 60.dp else if (horizontal) 36.dp else 96.dp

    Box(
        modifier = modifier
            .offset(y = yOffset.dp)
            .scale(scale)
            .size(width = tileWidth, height = tileHeight)
            .then(
                if (onClick != null && !isPlaceholder && !isHidden)
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isHovered = true
                                tryAwaitRelease()
                                isHovered = false
                            },
                            onTap = { onClick() }
                        )
                    }
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        // Glow effect
        if (glowAlpha > 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding((-4).dp)
                    .background(
                        if (isSelected) BrightGold.copy(alpha = glowAlpha * 0.4f)
                        else SoftGreen.copy(alpha = glowAlpha * 0.3f),
                        RoundedCornerShape(12.dp)
                    )
                    .blur(12.dp)
            )
        }

        // Main tile
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(elevation, RoundedCornerShape(10.dp), spotColor = Color.Black.copy(alpha = 0.4f))
                .background(
                    when {
                        isHidden -> Charcoal
                        isPlaceholder -> Color.White.copy(alpha = 0.05f)
                        else -> Ivory
                    },
                    RoundedCornerShape(10.dp)
                )
                .border(
                    width = when {
                        isSelected -> 2.5.dp
                        isPlaceholder -> 1.dp
                        else -> 1.5.dp
                    },
                    brush = when {
                        isSelected -> GoldGradient
                        isPlaceholder -> Brush.linearGradient(
                            colors = listOf(Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.1f))
                        )
                        else -> Brush.linearGradient(colors = listOf(WarmWood, RichWood))
                    },
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isHidden) {
                HiddenTilePattern()
            } else if (!isPlaceholder) {
                if (horizontal) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DotFace(value = tile.top, isMini = isMini, modifier = Modifier.weight(1f))
                        VerticalDivider(color = WarmWood, thickness = if (isMini) 1.dp else 2.dp)
                        DotFace(value = tile.bottom, isMini = isMini, modifier = Modifier.weight(1f))
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DotFace(value = tile.top, isMini = isMini, modifier = Modifier.weight(1f))
                        HorizontalDivider(color = WarmWood, thickness = if (isMini) 1.dp else 2.dp)
                        DotFace(value = tile.bottom, isMini = isMini, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun HiddenTilePattern() {
    Canvas(modifier = Modifier.fillMaxSize().padding(4.dp)) {
        val step = 6f
        var x = 0f
        while (x < size.width + size.height) {
            drawLine(
                color = Color(0xFF3A3A4A),
                start = Offset(x, 0f),
                end = Offset(x - size.height, size.height),
                strokeWidth = 1f
            )
            x += step
        }
    }
}

@Composable
private fun DotFace(value: Int, isMini: Boolean, modifier: Modifier = Modifier) {
    val dotSize = if (isMini) 3.5.dp else 6.5.dp
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (value) {
            0 -> {}
            1 -> CenterDot(dotSize)
            2 -> DiagonalDots(dotSize, tl = true, br = true)
            3 -> DiagonalDots(dotSize, tl = true, c = true, br = true)
            4 -> DiagonalDots(dotSize, tl = true, tr = true, bl = true, br = true)
            5 -> DiagonalDots(dotSize, tl = true, tr = true, c = true, bl = true, br = true)
            6 -> SixDots(dotSize)
        }
    }
}

@Composable
private fun Dot(size: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .background(
                Brush.radialGradient(
                    colors = listOf(Charcoal, Charcoal.copy(alpha = 0.8f)),
                    radius = 0.7f
                ),
                CircleShape
            )
            .shadow(1.dp, CircleShape)
    )
}

@Composable
private fun CenterDot(size: Dp) {
    Box(Modifier.size(size * 3), contentAlignment = Alignment.Center) {
        Dot(size)
    }
}

@Composable
private fun DiagonalDots(
    size: Dp,
    tl: Boolean = false, tr: Boolean = false,
    c: Boolean = false,
    bl: Boolean = false, br: Boolean = false
) {
    Box(Modifier.size(size * 3), contentAlignment = Alignment.Center) {
        if (tl) Dot(size, Modifier.align(Alignment.TopStart))
        if (tr) Dot(size, Modifier.align(Alignment.TopEnd))
        if (c) Dot(size, Modifier.align(Alignment.Center))
        if (bl) Dot(size, Modifier.align(Alignment.BottomStart))
        if (br) Dot(size, Modifier.align(Alignment.BottomEnd))
    }
}

@Composable
private fun SixDots(size: Dp) {
    Column(
        modifier = Modifier.size(size * 3),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Dot(size); Dot(size)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Dot(size); Dot(size)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Dot(size); Dot(size)
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  MAIN GAME SCREEN — Luxury Edition
// ═══════════════════════════════════════════════════════════════════════════
@Composable
fun GameScreen(
    gameState: GameState,
    isAiThinking: Boolean,
    showResult: Boolean,
    error: String?,
    onTileClick: (DominoTile, BoardSide?) -> Unit,
    onDrawOrPass: () -> Unit,
    legalSides: (DominoTile) -> Set<BoardSide>,
    onNewGame: () -> Unit,
    onBackToMenu: () -> Unit,
    onDismissResult: () -> Unit,
    onClearError: () -> Unit
) {
    var selectedTile by remember { mutableStateOf<DominoTile?>(null) }
    val showConfetti = gameState.isMatchOver

    Box(modifier = Modifier.fillMaxSize()) {
        // Background
        LuxuryBackground()

        // Confetti on match win
        ConfettiOverlay(active = showConfetti)

        Column(modifier = Modifier.fillMaxSize()) {
            // ── Top Bar ──
            LuxuryTopBar(
                message = gameState.message,
                isAiThinking = isAiThinking,
                onBack = onBackToMenu
            )

            // ── Score Crown ──
            ScoreCrown(
                matchScore = gameState.matchScore,
                players = gameState.players,
                currentPlayerIndex = gameState.currentPlayerIndex
            )

            // ── Player Avatars ──
            PlayerAvatars(
                players = gameState.players,
                currentPlayerIndex = gameState.currentPlayerIndex,
                matchScore = gameState.matchScore
            )

            Spacer(Modifier.height(8.dp))

            // ── The Board ──
            LuxuryBoard(
                boardState = gameState.board,
                stockCount = gameState.stockCount,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            )

            // ── Side Selector ──
            AnimatedVisibility(
                visible = selectedTile != null,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                selectedTile?.let { tile ->
                    LuxurySideSelector(
                        tile = tile,
                        onLeft = { onTileClick(tile, BoardSide.LEFT); selectedTile = null },
                        onRight = { onTileClick(tile, BoardSide.RIGHT); selectedTile = null },
                        onCancel = { selectedTile = null }
                    )
                }
            }

            // ── Error ──
            if (error != null) {
                LuxuryErrorBanner(error = error, onDismiss = onClearError)
            }

            // ── Player Hand ──
            val currentPlayer = gameState.currentPlayer
            if (currentPlayer != null && !currentPlayer.isAi && !gameState.isGameOver) {
                LuxuryPlayerHand(
                    player = currentPlayer,
                    legalSides = legalSides,
                    selectedTile = selectedTile,
                    onTileClick = { tile ->
                        val sides = legalSides(tile)
                        when {
                            sides.isEmpty() -> Unit
                            sides.size == 1 -> onTileClick(tile, sides.first())
                            else -> selectedTile = if (selectedTile?.id == tile.id) null else tile
                        }
                    },
                    onDrawOrPass = onDrawOrPass,
                    canDraw = gameState.canDraw
                )
            } else if (isAiThinking) {
                LuxuryAiThinking()
            }

            Spacer(Modifier.height(8.dp))
        }

        // ── Dialogs ──
        if (showResult && gameState.isGameOver && !gameState.isMatchOver) {
            LuxuryRoundDialog(
                gameState = gameState,
                onNextRound = onDismissResult,
                onQuit = onBackToMenu
            )
        }

        if (gameState.isMatchOver) {
            LuxuryMatchDialog(
                gameState = gameState,
                onRematch = onNewGame,
                onQuit = onBackToMenu
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  TOP BAR
// ═══════════════════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LuxuryTopBar(message: String, isAiThinking: Boolean, onBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = PaleGold,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "رجوع",
                    tint = RoyalGold,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        actions = {
            if (isAiThinking) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(RoyalGold.copy(alpha = 0.15f), CircleShape)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = BrightGold,
                        strokeWidth = 2.5.dp
                    )
                }
            } else {
                Spacer(Modifier.width(48.dp))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.background(
            Brush.verticalGradient(
                colors = listOf(DeepForest.copy(alpha = 0.9f), Color.Transparent)
            )
        )
    )
}

// ═══════════════════════════════════════════════════════════════════════════
//  SCORE CROWN — Premium score display
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun ScoreCrown(
    matchScore: MatchScore,
    players: List<Player>,
    currentPlayerIndex: Int
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            players.forEachIndexed { index, player ->
                val isCurrent = index == currentPlayerIndex
                val score = matchScore.playerScore(player.id)
                val progress = matchScore.progressPercent(player.id)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Crown for leader
                    if (score == players.maxOf { matchScore.playerScore(it.id) } && score > 0) {
                        Text(
                            "👑",
                            fontSize = 16.sp,
                            modifier = Modifier.offset(y = (-4).dp)
                        )
                    }
                    Text(
                        player.displayName(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isCurrent) BrightGold else PaleGold.copy(alpha = 0.8f),
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(contentAlignment = Alignment.Center) {
                        // Background ring
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.size(52.dp),
                            color = Color.White.copy(alpha = 0.1f),
                            strokeWidth = 4.dp
                        )
                        // Progress ring
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(52.dp),
                            color = if (isCurrent) BrightGold else RoyalGold,
                            strokeWidth = 4.dp
                        )
                        Text(
                            "$score",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isCurrent) BrightGold else PaleGold
                        )
                    }
                    Text(
                        "${matchScore.playerRoundsWon(player.id)} جولة",
                        style = MaterialTheme.typography.labelSmall,
                        color = PaleGold.copy(alpha = 0.6f)
                    )
                }
                if (index < players.size - 1) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(RoyalGold, BurnishedGold)
                                ),
                                CircleShape
                            )
                            .border(2.dp, BrightGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "VS",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = DeepForest
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "الهدف: ${matchScore.targetScore} نقطة  •  الجولة ${matchScore.currentRound}",
            style = MaterialTheme.typography.labelSmall,
            color = PaleGold.copy(alpha = 0.7f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  PLAYER AVATARS
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun PlayerAvatars(
    players: List<Player>,
    currentPlayerIndex: Int,
    matchScore: MatchScore
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        players.forEachIndexed { i, player ->
            val isCurrent = i == currentPlayerIndex
            val infiniteTransition = rememberInfiniteTransition(label = "pulse$i")
            val pulse by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = if (isCurrent) 1.08f else 1f,
                animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
                label = "pulse"
            )
            val glowAlpha by animateFloatAsState(
                targetValue = if (isCurrent) 0.8f else 0f,
                animationSpec = tween(500),
                label = "glow"
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.Center) {
                    // Glow ring
                    if (glowAlpha > 0) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .background(BrightGold.copy(alpha = glowAlpha * 0.3f), CircleShape)
                                .blur(8.dp)
                        )
                    }
                    Card(
                        modifier = Modifier.scale(pulse),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) BrightGold else Color.White.copy(alpha = 0.1f)
                        ),
                        border = if (isCurrent) {
                            BorderStroke(3.dp, BrightGold)
                        } else {
                            BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                        }
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (player.isAi) "🤖" else "👤",
                                fontSize = 24.sp
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    player.displayName(),
                    fontSize = 11.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrent) BrightGold else PaleGold.copy(alpha = 0.8f)
                )
                Text(
                    "${player.hand.size} قطع",
                    fontSize = 10.sp,
                    color = PaleGold.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  LUXURY BOARD
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun LuxuryBoard(
    boardState: BoardState,
    stockCount: Int,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (boardState.isEmpty) {
                EmptyBoard()
            } else {
                BoardWithTiles(boardState = boardState, stockCount = stockCount)
            }
        }
    }
}

@Composable
private fun EmptyBoard() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LuxuryDominoTile(
            tile = DominoTile(0, 0, 0),
            isPlaceholder = true,
            modifier = Modifier.size(width = 48.dp, height = 80.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "اللوحة جاهزة",
            color = PaleGold.copy(alpha = 0.7f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            "اختر قطعة للبدء",
            color = PaleGold.copy(alpha = 0.4f),
            fontSize = 13.sp
        )
    }
}

@Composable
private fun BoardWithTiles(boardState: BoardState, stockCount: Int) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Board ends
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EndBadge(value = boardState.leftEnd, label = "← يسار")
            StockBadge(count = stockCount, totalTiles = boardState.tiles.size)
            EndBadge(value = boardState.rightEnd, label = "يمين →")
        }

        Spacer(Modifier.height(8.dp))

        // Tiles
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            contentPadding = PaddingValues(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val displayTiles = if (boardState.tiles.size > 8)
                boardState.tiles.takeLast(8) else boardState.tiles
            items(displayTiles) { placed ->
                LuxuryDominoTile(
                    tile = placed.tile,
                    isMini = true,
                    horizontal = true,
                    modifier = Modifier.size(width = 40.dp, height = 28.dp)
                )
            }
        }
    }
}

@Composable
private fun EndBadge(value: Int?, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            fontSize = 10.sp,
            color = PaleGold.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(BrightGold, RoyalGold)
                    ),
                    RoundedCornerShape(10.dp)
                )
                .border(2.dp, PaleGold, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "${value ?: "—"}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = DeepForest
            )
        }
    }
}

@Composable
private fun StockBadge(count: Int, totalTiles: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .border(1.dp, RoyalGold.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = null,
            tint = RoyalGold,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            "$count",
            color = PaleGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            " / $totalTiles",
            color = PaleGold.copy(alpha = 0.5f),
            fontSize = 11.sp
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  LUXURY SIDE SELECTOR
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun LuxurySideSelector(
    tile: DominoTile,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onCancel: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "اختر الجانب",
                style = MaterialTheme.typography.titleMedium,
                color = BrightGold,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            LuxuryDominoTile(
                tile = tile,
                isSelected = true,
                modifier = Modifier.size(width = 60.dp, height = 100.dp)
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GoldButton(
                    onClick = onLeft,
                    text = "← يسار",
                    modifier = Modifier.weight(1f)
                )
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PaleGold),
                    border = BorderStroke(1.dp, PaleGold.copy(alpha = 0.5f))
                ) {
                    Text("إلغاء")
                }
                GoldButton(
                    onClick = onRight,
                    text = "يمين →",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GoldButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GoldGradient, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text,
                color = DeepForest,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  LUXURY PLAYER HAND
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun LuxuryPlayerHand(
    player: Player,
    legalSides: (DominoTile) -> Set<BoardSide>,
    selectedTile: DominoTile?,
    onTileClick: (DominoTile) -> Unit,
    onDrawOrPass: () -> Unit,
    canDraw: Boolean
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "🎴",
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "يدك",
                        style = MaterialTheme.typography.titleSmall,
                        color = PaleGold,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(RoyalGold.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "${player.hand.size}",
                            color = BrightGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                Text(
                    "القيمة: ${player.handValue}",
                    style = MaterialTheme.typography.labelSmall,
                    color = PaleGold.copy(alpha = 0.6f)
                )
            }

            Spacer(Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(player.hand) { tile ->
                    val sides = legalSides(tile)
                    val isSelected = selectedTile?.id == tile.id
                    LuxuryDominoTile(
                        tile = tile,
                        isLegal = sides.isNotEmpty(),
                        isSelected = isSelected,
                        onClick = if (sides.isNotEmpty()) { { onTileClick(tile) } } else null,
                        modifier = Modifier.size(width = 48.dp, height = 84.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            GoldButton(
                onClick = onDrawOrPass,
                text = if (canDraw) "🎲  سحب قطعة" else "⏭️  تخطي الدور",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  AI THINKING
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun LuxuryAiThinking() {
    var dotCount by remember { mutableIntStateOf(1) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(400)
            dotCount = if (dotCount >= 3) 1 else dotCount + 1
        }
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = BrightGold,
                strokeWidth = 2.5.dp
            )
            Spacer(Modifier.width(12.dp))
            Text(
                "🤖  AI يحسب أفضل حركة${".".repeat(dotCount)}",
                style = MaterialTheme.typography.bodyMedium,
                color = PaleGold
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  ERROR BANNER
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun LuxuryErrorBanner(error: String, onDismiss: () -> Unit) {
    val shake by rememberInfiniteTransition(label = "shake").animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(100), RepeatMode.Reverse),
        label = "shake"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset(x = shake.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFB71C1C).copy(alpha = 0.9f))
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "⚠️  $error",
                Modifier.weight(1f),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            TextButton(onClick = onDismiss) {
                Text("✕", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  DIALOGS
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun LuxuryRoundDialog(
    gameState: GameState,
    onNextRound: () -> Unit,
    onQuit: () -> Unit
) {
    val lastRound = gameState.matchScore.roundHistory.lastOrNull()

    AlertDialog(
        onDismissRequest = {},
        containerColor = WarmWood,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎉", fontSize = 40.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "انتهت الجولة!",
                    color = BrightGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    gameState.message,
                    color = PaleGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                if (lastRound != null) {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .background(RoyalGold.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .border(1.dp, RoyalGold, RoundedCornerShape(12.dp))
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "+${lastRound.pointsEarned} نقطة",
                            color = BrightGold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "النتيجة الحالية:",
                    color = RoyalGold,
                    fontWeight = FontWeight.Bold
                )
                gameState.players.forEach { player ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(player.displayName(), color = PaleGold)
                        Text(
                            "${gameState.matchScore.playerScore(player.id)} نقطة",
                            color = BrightGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {
            GoldButton(onClick = onNextRound, text = "▶  جولة تالية", modifier = Modifier.fillMaxWidth())
        },
        dismissButton = {
            TextButton(onClick = onQuit) {
                Text("خروج", color = PaleGold.copy(alpha = 0.8f))
            }
        }
    )
}

@Composable
private fun LuxuryMatchDialog(
    gameState: GameState,
    onRematch: () -> Unit,
    onQuit: () -> Unit
) {
    val winnerId = gameState.matchScore.matchWinnerId
    val winner = winnerId?.let { gameState.players.getOrNull(it) }

    AlertDialog(
        onDismissRequest = {},
        containerColor = WarmWood,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🏆", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "انتهت المباراة!",
                    color = BrightGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.radialGradient(
                                colors = listOf(RoyalGold.copy(alpha = 0.3f), Color.Transparent)
                            ),
                            CircleShape
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        winner?.displayName() ?: "تعادل!",
                        color = BrightGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp
                    )
                }
                Text(
                    if (winner != null) "فاز بالمباراة!" else "لا يوجد فائز",
                    color = PaleGold.copy(alpha = 0.8f),
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "الترتيب النهائي:",
                    color = RoyalGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                gameState.matchScore.leaderboard.forEachIndexed { i, (playerId, score) ->
                    val player = gameState.players.getOrNull(playerId)
                    val medal = when (i) {
                        0 -> "🥇"
                        1 -> "🥈"
                        else -> "🥉"
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(
                                if (i == 0) RoyalGold.copy(alpha = 0.15f) else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "$medal ${player?.displayName() ?: "لاعب"}",
                            color = if (i == 0) BrightGold else PaleGold
                        )
                        Text(
                            "$score نقطة",
                            color = if (i == 0) BrightGold else PaleGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {
            GoldButton(
                onClick = onRematch,
                text = "🔄  مباراة جديدة",
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            TextButton(onClick = onQuit) {
                Text("القائمة الرئيسية", color = PaleGold.copy(alpha = 0.8f))
            }
        }
    )
}
