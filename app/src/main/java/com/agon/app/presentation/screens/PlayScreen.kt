package com.agon.app.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 鈹€鈹€ Colors 鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€
private val FeltDark = Color(0xFF0D3B10)
private val FeltGreen = Color(0xFF1B5E20)
private val FeltLight = Color(0xFF2E7D32)
private val WoodBrown = Color(0xFF5D4037)
private val WoodLight = Color(0xFF8D6E63)
private val GoldAccent = Color(0xFFFFD700)
private val GoldDark = Color(0xFFB8860B)
private val Ivory = Color(0xFFFFF8E1)
private val DominoBg = Color(0xFFFFF8E1)
private val DominoBorder = Color(0xFF2E3B28)

@Composable
fun PlayScreen(
    onVsAi: () -> Unit,
    onVsPlayer: () -> Unit,
    onNetwork: () -> Unit,
    onTournament: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(FeltDark, FeltGreen, FeltLight)
                )
            )
    ) {
        // Decorative background elements
        BackgroundDecorations()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 鈹€鈹€ Top Bar 鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "乇噩賵毓",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(Modifier.weight(1f))
            }

            Spacer(Modifier.height(24.dp))

            // 鈹€鈹€ Title 鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€
            GameTitle()

            Spacer(Modifier.height(40.dp))

            // 鈹€鈹€ Game Modes 鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€
            Text(
                "丕禺鬲乇 賵囟毓 丕賱賱毓亘",
                style = MaterialTheme.typography.headlineSmall,
                color = GoldAccent,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            // Mode Cards
            GameModeCard(
                title = "囟丿 丕賱匕賰丕亍 丕賱丕氐胤賳丕毓賷",
                subtitle = "鬲丨丿賶 丕賱賰賲亘賷賵鬲乇 賮賷 賲亘丕乇丕丞 賰賱丕爻賷賰賷丞",
                icon = Icons.Default.Computer,
                iconEmoji = "馃",
                colors = listOf(Color(0xFF1565C0), Color(0xFF0D47A1)),
                onClick = onVsAi
            )

            Spacer(Modifier.height(12.dp))

            GameModeCard(
                title = "賱丕毓亘 囟丿 賱丕毓亘",
                subtitle = "鬲丨丿賶 氐丿賷賯賰 毓賱賶 賳賮爻 丕賱噩賴丕夭",
                icon = Icons.Default.Person,
                iconEmoji = "馃懃",
                colors = listOf(Color(0xFF6A1B9A), Color(0xFF4A148C)),
                onClick = onVsPlayer
            )

            Spacer(Modifier.height(12.dp))

            GameModeCard(
                title = "丕賱賱毓亘 毓亘乇 丕賱卮亘賰丞",
                subtitle = "鬲賵丕氐賱 賲毓 丕賱兀氐丿賯丕亍 毓亘乇 WiFi",
                icon = Icons.Default.Wifi,
                iconEmoji = "馃摱",
                colors = listOf(Color(0xFF2E7D32), Color(0xFF1B5E20)),
                onClick = onNetwork,
                isNew = true
            )

            Spacer(Modifier.height(12.dp))

            GameModeCard(
                title = "亘胤賵賱丞",
                subtitle = "鬲賳丕賮爻 賮賷 亘胤賵賱丞 賲鬲毓丿丿丞 丕賱噩賵賱丕鬲",
                icon = Icons.Default.Group,
                iconEmoji = "馃弳",
                colors = listOf(Color(0xFFE65100), Color(0xFFBF360C)),
                onClick = onTournament
            )

            Spacer(Modifier.weight(1f))

            // 鈹€鈹€ Bottom Decoration 鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€
            DominoRowDecoration()
        }
    }
}

// 鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€
// Game Title
// 鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€

@Composable
private fun GameTitle() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Domino icon
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DominoIconTile(top = 6, bottom = 6, size = 50)
            DominoIconTile(top = 0, bottom = 0, size = 50)
            DominoIconTile(top = 6, bottom = 6, size = 50)
        }

        Spacer(Modifier.height(12.dp))

        Text(
            "DOMINO",
            style = MaterialTheme.typography.displayLarge,
            color = GoldAccent,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 48.sp,
            letterSpacing = 4.sp
        )

        Text(
            "丿賵賲賷賳賵",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )

        Box(
            modifier = Modifier
                .width(120.dp)
                .height(3.dp)
                .background(GoldAccent, RoundedCornerShape(2.dp))
                .padding(vertical = 8.dp)
        )
    }
}

// 鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€
// Game Mode Card
// 鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€

@Composable
private fun GameModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconEmoji: String,
    colors: List<Color>,
    onClick: () -> Unit,
    isNew: Boolean = false
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(150),
        label = "card_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable {
                isPressed = true
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(colors = colors),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon circle
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            CircleShape
                        )
                        .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        iconEmoji,
                        fontSize = 28.sp
                    )
                }

                // Text content
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        if (isNew) {
                            Spacer(Modifier.width(8.dp))
                            NewBadge()
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        subtitle,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                }

                // Arrow
                Text(
                    "鈻�",
                    color = GoldAccent,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun NewBadge() {
    Box(
        modifier = Modifier
            .background(GoldAccent, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            "噩丿賷丿!",
            color = FeltDark,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// 鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€
// Domino Icon Tile (for title)
// 鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€

@Composable
private fun DominoIconTile(top: Int, bottom: Int, size: Int) {
    Box(
        modifier = Modifier
            .size(width = size.dp, height = (size * 1.6).dp)
            .background(DominoBg, RoundedCornerShape(8.dp))
            .border(2.dp, DominoBorder, RoundedCornerShape(8.dp))
            .shadow(4.dp, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.padding(4.dp)
        ) {
            DotPatternMini(dots = top, size = size / 5)
            Divider(
                color = DominoBorder,
                thickness = 1.5.dp,
                modifier = Modifier.width((size * 0.6).dp)
            )
            DotPatternMini(dots = bottom, size = size / 5)
        }
    }
}

@Composable
private fun DotPatternMini(dots: Int, size: Int) {
    val dotSize = size.dp
    when (dots) {
        0 -> Box(modifier = Modifier.size(dotSize * 3))
        6 -> {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    MiniDot(dotSize); MiniDot(dotSize)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    MiniDot(dotSize); MiniDot(dotSize)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    MiniDot(dotSize); MiniDot(dotSize)
                }
            }
        }
        else -> {
            Box(modifier = Modifier.size(dotSize * 3), contentAlignment = Alignment.Center) {
                MiniDot(dotSize * 1.5f)
            }
        }
    }
}

@Composable
private fun MiniDot(size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(DominoBorder, CircleShape)
    )
}

// 鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€
// Background Decorations
// 鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€

@Composable
private fun BackgroundDecorations() {
    // Floating domino pieces in background
    Box(modifier = Modifier.fillMaxSize()) {
        // Top right decoration
        DominoDecoration(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
                .size(80.dp)
                .alpha(0.1f)
        )

        // Bottom left decoration
        DominoDecoration(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
                .size(60.dp)
                .alpha(0.08f)
        )
    }
}

@Composable
private fun DominoDecoration(modifier: Modifier) {
    Box(modifier = modifier) {
        DominoIconTile(top = 3, bottom = 4, size = 40)
    }
}

// 鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€
// Bottom Domino Row
// 鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€

@Composable
private fun DominoRowDecoration() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val tiles = listOf(
            Pair(0, 0), Pair(1, 2), Pair(3, 3), Pair(4, 5), Pair(6, 6)
        )
        tiles.forEachIndexed { index, (top, bottom) ->
            val offset by rememberInfiniteTransition(label = "float_$index").animateFloat(
                initialValue = 0f,
                targetValue = -8f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, delayStart = index * 200),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "offset"
            )
            Box(modifier = Modifier.offset(y = offset.dp)) {
                DominoIconTile(top = top, bottom = bottom, size = 36)
            }
            if (index < tiles.size - 1) {
                Spacer(Modifier.width(4.dp))
            }
        }
    }
}

// Helper extension
private fun Modifier.alpha(alpha: Float): Modifier = this.then(
    androidx.compose.ui.draw.alpha(alpha)
)