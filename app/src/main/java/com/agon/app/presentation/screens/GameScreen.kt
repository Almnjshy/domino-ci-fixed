package com.agon.app.presentation.screens

import androidx.compose.ui.unit.Dp
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.domain.model.*
import com.agon.app.presentation.viewmodel.GameViewModel

// ── Colors ───────────────────────────────────────
private val DominoBg = Color(0xFFFFF8E1)      // Ivory
private val DominoBorder = Color(0xFF2E3B28)   // Dark green
private val DominoDot = Color(0xFF1A1A1A)      // Black
private val DominoDivider = Color(0xFF2E3B28)  // Dark green
private val FeltGreen = Color(0xFF1B5E20)      // Casino felt
private val FeltDark = Color(0xFF0D3B10)       // Darker felt
private val GoldAccent = Color(0xFFFFD700)     // Gold
private val WoodBrown = Color(0xFF5D4037)      // Wood table

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FeltDark)
    ) {
        // Background pattern
        CasinoBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            // ── Top Bar ──────────────────────────────
            TopBar(
                message = gameState.message,
                isAiThinking = isAiThinking,
                onBack = onBackToMenu
            )

            // ── Match Score Banner ───────────────────
            MatchScoreBanner(matchScore = gameState.matchScore, players = gameState.players)

            // ── Players Info ─────────────────────────
            PlayersRow(
                players = gameState.players,
                currentPlayerIndex = gameState.currentPlayerIndex,
                matchScore = gameState.matchScore
            )

            Spacer(Modifier.height(8.dp))

            // ── Board ────────────────────────────────
            BoardArea(
                boardState = gameState.board,
                stockCount = gameState.stockCount,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 12.dp)
            )

            // ── Side Selection ───────────────────────
            AnimatedVisibility(visible = selectedTile != null) {
                selectedTile?.let { tile ->
                    SideSelector(
                        tile = tile,
                        onSelectLeft = { onTileClick(tile, BoardSide.LEFT); selectedTile = null },
                        onSelectRight = { onTileClick(tile, BoardSide.RIGHT); selectedTile = null },
                        onCancel = { selectedTile = null }
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // ── Error ────────────────────────────────
            if (error != null) {
                ErrorBanner(error = error, onDismiss = onClearError)
            }

            // ── Player Hand ──────────────────────────
            val currentPlayer = gameState.currentPlayer
            if (currentPlayer != null && !currentPlayer.isAi && !gameState.isGameOver) {
                PlayerHandSection(
                    player = currentPlayer,
                    legalSides = legalSides,
                    onTileClick = { tile ->
                        val sides = legalSides(tile)
                        when {
                            sides.isEmpty() -> Unit
                            sides.size == 1 -> onTileClick(tile, sides.first())
                            else -> selectedTile = tile
                        }
                    },
                    onDrawOrPass = onDrawOrPass,
                    canDraw = gameState.canDraw
                )
            } else if (isAiThinking) {
                AiThinkingIndicator()
            }

            Spacer(Modifier.height(8.dp))
        }
    }

    // ── Round Result Dialog ──────────────────────
    if (showResult && gameState.isGameOver && !gameState.isMatchOver) {
        RoundResultDialog(
            gameState = gameState,
            onNextRound = onDismissResult,
            onQuit = onBackToMenu
        )
    }

    // ── Match Over Dialog ────────────────────────
    if (gameState.isMatchOver) {
        MatchResultDialog(
            gameState = gameState,
            onRematch = onNewGame,
            onQuit = onBackToMenu
        )
    }
}

// ─────────────────────────────────────────────────────
// Background
// ─────────────────────────────────────────────────────

@Composable
private fun CasinoBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(FeltDark, FeltGreen, FeltDark)
                )
            )
    )
}

// ─────────────────────────────────────────────────────
// Top Bar
// ─────────────────────────────────────────────────────

@Composable
private fun TopBar(message: String, isAiThinking: Boolean, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "رجوع",
                tint = Color.White
            )
        }
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
        if (isAiThinking) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp).padding(end = 8.dp),
                color = GoldAccent
            )
        } else {
            Spacer(Modifier.size(36.dp))
        }
    }
}

// ─────────────────────────────────────────────────────
// Match Score Banner
// ─────────────────────────────────────────────────────

@Composable
private fun MatchScoreBanner(matchScore: MatchScore, players: List<Player>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WoodBrown.copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                players.forEach { player ->
                    ScoreColumn(player = player, matchScore = matchScore)
                    if (player.id < players.size - 1) {
                        VSIndicator()
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = "هدف: ${matchScore.targetScore} نقطة • الجولة ${matchScore.currentRound}",
                style = MaterialTheme.typography.labelSmall,
                color = GoldAccent,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ScoreColumn(player: Player, matchScore: MatchScore) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = player.displayName(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = "${matchScore.playerScore(player.id)}",
            style = MaterialTheme.typography.headlineMedium,
            color = GoldAccent,
            fontWeight = FontWeight.ExtraBold
        )
        LinearProgressIndicator(
            progress = { matchScore.progressPercent(player.id) },
            modifier = Modifier.width(70.dp).height(5.dp).clip(RoundedCornerShape(3.dp)),
            color = GoldAccent,
            trackColor = Color.Black.copy(alpha = 0.3f)
        )
        Text(
            text = "${matchScore.playerRoundsWon(player.id)} جولات",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun VSIndicator() {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(GoldAccent, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "VS",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
            color = FeltDark
        )
    }
}

// ─────────────────────────────────────────────────────
// Players Row
// ─────────────────────────────────────────────────────

@Composable
private fun PlayersRow(
    players: List<Player>,
    currentPlayerIndex: Int,
    matchScore: MatchScore
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        players.forEach { player ->
            val isCurrent = player.id == currentPlayerIndex
            val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
                initialValue = 1f, targetValue = if (isCurrent) 1.08f else 1f,
                animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
                label = "scale"
            )
            Card(
                modifier = Modifier.scale(pulse),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent)
                        GoldAccent.copy(alpha = 0.9f)
                    else Color.Black.copy(alpha = 0.4f)
                ),
                border = if (isCurrent) {
                    androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                } else null
            ) {
                Column(
                    Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        player.displayName(),
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = if (isCurrent) FeltDark else Color.White,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DominoMiniIcon()
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${player.hand.size}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) FeltDark else Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DominoMiniIcon() {
    Box(
        modifier = Modifier
            .size(width = 10.dp, height = 16.dp)
            .background(DominoBg, RoundedCornerShape(2.dp))
            .border(1.dp, DominoBorder, RoundedCornerShape(2.dp))
    ) {
        Divider(
            modifier = Modifier.align(Alignment.Center),
            color = DominoBorder,
            thickness = 0.5.dp
        )
    }
}

// ─────────────────────────────────────────────────────
// Board Area
// ─────────────────────────────────────────────────────

@Composable
private fun BoardArea(boardState: BoardState, stockCount: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.shadow(12.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = FeltGreen.copy(alpha = 0.7f)),
        border = androidx.compose.foundation.BorderStroke(3.dp, WoodBrown)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (boardState.isEmpty) {
                EmptyBoardMessage()
            } else {
                BoardContent(boardState = boardState, stockCount = stockCount)
            }
        }
    }
}

@Composable
private fun EmptyBoardMessage() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        DominoTileView(
            tile = DominoTile(0, 0, 0),
            modifier = Modifier.size(width = 50.dp, height = 80.dp),
            isPlaceholder = true
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "اللوحة فارغة — ابدأ اللعب",
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun BoardContent(boardState: BoardState, stockCount: Int) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Board ends display
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EndTile(value = boardState.leftEnd, label = "← يسار")
            // Middle tiles
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                val displayTiles = boardState.tiles.dropLast(1).takeLast(6)
                items(displayTiles) { placed ->
                    DominoTileView(
                        tile = placed.tile,
                        modifier = Modifier.size(width = 36.dp, height = 60.dp),
                        isMini = true
                    )
                }
            }
            EndTile(value = boardState.rightEnd, label = "يمين →")
        }
        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StockIndicator(count = stockCount)
            Text(
                "${boardState.tiles.size} قطعة على اللوحة",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun EndTile(value: Int?, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(4.dp)) {
        Text(
            label,
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(2.dp))
        Box(
            Modifier
                .size(44.dp)
                .background(GoldAccent.copy(alpha = 0.9f), RoundedCornerShape(10.dp))
                .border(2.dp, Color.White, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "${value ?: "-"}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = FeltDark
            )
        }
    }
}

@Composable
private fun StockIndicator(count: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        DominoMiniIcon()
        Spacer(Modifier.width(4.dp))
        Text(
            "$count",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ─────────────────────────────────────────────────────
// Domino Tile View (The Star!)
// ─────────────────────────────────────────────────────

@Composable
fun DominoTileView(
    tile: DominoTile,
    modifier: Modifier = Modifier,
    isMini: Boolean = false,
    isPlaceholder: Boolean = false,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = tween(200), label = "tile_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(if (isSelected) 8.dp else 3.dp, RoundedCornerShape(10.dp))
            .background(
                if (isPlaceholder) Color.White.copy(alpha = 0.1f) else DominoBg,
                RoundedCornerShape(10.dp)
            )
            .border(
                width = if (isSelected) 3.dp else if (isPlaceholder) 1.dp else 2.dp,
                color = if (isSelected) GoldAccent else if (isPlaceholder) Color.White.copy(alpha = 0.3f) else DominoBorder,
                shape = RoundedCornerShape(10.dp)
            )
            .then(if (onClick != null && !isPlaceholder) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (!isPlaceholder) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // Top half
                DotPattern(dots = tile.top, isMini = isMini, modifier = Modifier.weight(1f))

                // Divider line
                Divider(
                    color = DominoDivider,
                    thickness = if (isMini) 1.dp else 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (isMini) 2.dp else 6.dp)
                )

                // Bottom half
                DotPattern(dots = tile.bottom, isMini = isMini, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DotPattern(dots: Int, isMini: Boolean, modifier: Modifier = Modifier) {
    val dotSize = if (isMini) 4.dp else 7.dp
    val spacing = if (isMini) 1.dp else 2.dp

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        when (dots) {
            0 -> {} // Empty
            1 -> CenterDot(dotSize)
            2 -> DiagonalDots(dotSize, topLeft = true, bottomRight = true)
            3 -> DiagonalDots(dotSize, topLeft = true, center = true, bottomRight = true)
            4 -> FourCornersDots(dotSize)
            5 -> FiveDots(dotSize)
            6 -> SixDots(dotSize)
            else -> Text(
                dots.toString(),
                fontSize = if (isMini) 10.sp else 16.sp,
                fontWeight = FontWeight.Bold,
                color = DominoDot
            )
        }
    }
}

@Composable
private fun Dot(size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(DominoDot, CircleShape)
            .shadow(1.dp, CircleShape)
    )
}

@Composable
private fun CenterDot(size: Dp) {
    Box(modifier = Modifier.size(size * 3), contentAlignment = Alignment.Center) {
        Dot(size)
    }
}

@Composable
private fun DiagonalDots(
    size: Dp,
    topLeft: Boolean = false,
    topRight: Boolean = false,
    center: Boolean = false,
    bottomLeft: Boolean = false,
    bottomRight: Boolean = false
) {
    Box(modifier = Modifier.size(size * 3), contentAlignment = Alignment.Center) {
        if (topLeft) Dot(Modifier.align(Alignment.TopStart), size)
        if (topRight) Dot(Modifier.align(Alignment.TopEnd), size)
        if (center) Dot(Modifier.align(Alignment.Center), size)
        if (bottomLeft) Dot(Modifier.align(Alignment.BottomStart), size)
        if (bottomRight) Dot(Modifier.align(Alignment.BottomEnd), size)
    }
}

@Composable
private fun FourCornersDots(size: Dp) {
    DiagonalDots(size, topLeft = true, topRight = true, bottomLeft = true, bottomRight = true)
}

@Composable
private fun FiveDots(size: Dp) {
    DiagonalDots(size, topLeft = true, topRight = true, center = true, bottomLeft = true, bottomRight = true)
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

@Composable
private fun Dot(modifier: Modifier, size: Dp) {
    Box(
        modifier = modifier
            .size(size)
            .background(DominoDot, CircleShape)
            .shadow(1.dp, CircleShape)
    )
}

// ─────────────────────────────────────────────────────
// Side Selector
// ─────────────────────────────────────────────────────

@Composable
private fun SideSelector(
    tile: DominoTile,
    onSelectLeft: () -> Unit,
    onSelectRight: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WoodBrown.copy(alpha = 0.95f))
    ) {
        Column(
            Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "اختر الجانب",
                style = MaterialTheme.typography.titleMedium,
                color = GoldAccent,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            DominoTileView(
                tile = tile,
                modifier = Modifier.size(width = 60.dp, height = 100.dp),
                isSelected = true
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onSelectLeft,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Text("← يسار", color = FeltDark, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("إلغاء")
                }
                Button(
                    onClick = onSelectRight,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Text("يمين →", color = FeltDark, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────
// Player Hand Section
// ─────────────────────────────────────────────────────

@Composable
private fun PlayerHandSection(
    player: Player,
    legalSides: (DominoTile) -> Set<BoardSide>,
    onTileClick: (DominoTile) -> Unit,
    onDrawOrPass: () -> Unit,
    canDraw: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "🎴 يدك (${player.hand.size} قطع)",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.weight(1f))
            Text(
                "قيمة: ${player.handValue}",
                style = MaterialTheme.typography.labelSmall,
                color = GoldAccent
            )
        }
        Spacer(Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(player.hand) { tile ->
                val sides = legalSides(tile)
                HandTile(
                    tile = tile,
                    isLegal = sides.isNotEmpty(),
                    onClick = if (sides.isNotEmpty()) { { onTileClick(tile) } } else null
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onDrawOrPass,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (canDraw) GoldAccent else Color.Gray
            )
        ) {
            Text(
                if (canDraw) "🎲 سحب قطعة" else "⏭️ تخطي الدور",
                color = FeltDark,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun HandTile(tile: DominoTile, isLegal: Boolean, onClick: (() -> Unit)?) {
    val scale by animateFloatAsState(
        targetValue = if (isLegal) 1f else 0.88f,
        animationSpec = tween(200), label = "tile_scale"
    )
    val elevation by animateDpAsState(
        targetValue = if (isLegal) 6.dp else 1.dp,
        animationSpec = tween(200), label = "tile_elevation"
    )

    DominoTileView(
        tile = tile,
        modifier = Modifier
            .scale(scale)
            .size(width = 52.dp, height = 88.dp),
        isSelected = isLegal,
        onClick = onClick
    )
}

// ─────────────────────────────────────────────────────
// AI Thinking
// ─────────────────────────────────────────────────────

@Composable
private fun AiThinkingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = GoldAccent
                )
                Text(
                    "🤖 AI يفكر...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────
// Error Banner
// ─────────────────────────────────────────────────────

@Composable
private fun ErrorBanner(error: String, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFB71C1C).copy(alpha = 0.9f))
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "⚠️ $error",
                Modifier.weight(1f),
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            TextButton(onClick = onDismiss) {
                Text("✕", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─────────────────────────────────────────────────────
// Dialogs
// ─────────────────────────────────────────────────────

@Composable
private fun RoundResultDialog(
    gameState: GameState,
    onNextRound: () -> Unit,
    onQuit: () -> Unit
) {
    val lastRound = gameState.matchScore.roundHistory.lastOrNull()
    AlertDialog(
        onDismissRequest = {},
        containerColor = WoodBrown,
        title = {
            Text(
                "🎉 انتهت الجولة ${lastRound?.roundNumber ?: ""}!",
                color = GoldAccent,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    gameState.message,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                if (lastRound != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "+${lastRound.pointsEarned} نقطة",
                        color = GoldAccent,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text("النتيجة:", color = GoldAccent, fontWeight = FontWeight.Bold)
                gameState.players.forEach { player ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(player.displayName(), color = Color.White)
                        Text(
                            "${gameState.matchScore.playerScore(player.id)} نقطة",
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onNextRound,
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text("▶ جولة تالية", color = FeltDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onQuit) {
                Text("خروج", color = Color.White.copy(alpha = 0.8f))
            }
        }
    )
}

@Composable
private fun MatchResultDialog(
    gameState: GameState,
    onRematch: () -> Unit,
    onQuit: () -> Unit
) {
    val winnerId = gameState.matchScore.matchWinnerId
    val winner = winnerId?.let { gameState.players.getOrNull(it) }
    AlertDialog(
        onDismissRequest = {},
        containerColor = WoodBrown,
        title = {
            Text(
                "🏆 انتهت المباراة!",
                color = GoldAccent,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "${winner?.displayName() ?: "تعادل"} فاز!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(Modifier.height(8.dp))
                Text("الترتيب النهائي:", color = GoldAccent, fontWeight = FontWeight.Bold)
                gameState.matchScore.leaderboard.forEachIndexed { i, (playerId, score) ->
                    val player = gameState.players.getOrNull(playerId)
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "${i + 1}. ${player?.displayName() ?: "لاعب"}",
                            color = Color.White
                        )
                        Text(
                            "$score نقطة",
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onRematch,
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text("🔄 مباراة جديدة", color = FeltDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onQuit) {
                Text("القائمة", color = Color.White.copy(alpha = 0.8f))
            }
        }
    )
}
