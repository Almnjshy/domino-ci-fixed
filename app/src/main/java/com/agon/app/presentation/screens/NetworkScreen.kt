package com.agon.app.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agon.app.domain.model.NetworkRoom
import com.agon.app.domain.model.NetworkState
import com.agon.app.domain.model.NetworkStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkScreen(
    networkState: NetworkState,
    discoveredRooms: List<NetworkRoom>,
    isLoading: Boolean,
    error: String?,
    showCreateDialog: Boolean,
    onCreateRoom: (String) -> Unit,
    onDiscover: () -> Unit,
    onJoinRoom: (NetworkRoom, String) -> Unit,
    onLeaveRoom: () -> Unit,
    onShowCreateDialog: () -> Unit,
    onDismissCreateDialog: () -> Unit,
    onBack: () -> Unit,
    onClearError: () -> Unit,
    statusMessage: String = ""
) {
    var playerName by remember { mutableStateOf("") }
    var roomToJoin by remember { mutableStateOf<NetworkRoom?>(null) }
    var createRoomName by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {

        // Top bar
        TopAppBar(
            title = { Text("丕賱賱毓亘 毓亘乇 丕賱卮亘賰丞") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "乇噩賵毓")
                }
            }
        )

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            // How it works
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("賰賷賮 賷毓賲賱責", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("馃摱 鬲兀賰丿 兀賳 噩賲賷毓 丕賱兀噩賴夭丞 毓賱賶 賳賮爻 丕賱賭 WiFi 兀賵 Hotspot")
                    Text("馃彔 卮禺氐 賵丕丨丿 賷賳卮卅 睾乇賮丞")
                    Text("馃懃 丕賱亘丕賯賵賳 賷亘丨孬賵賳 毓賳 丕賱睾乇賮丞 賵賷賳囟賲賵賳")
                    Text("馃幃 丕賱賲囟賷賮 賷亘丿兀 丕賱賱毓亘丞")
                }
            }

            Spacer(Modifier.height(12.dp))

            // Status
            val statusColor = when (networkState.status) {
                NetworkStatus.CONNECTED -> MaterialTheme.colorScheme.primary
                NetworkStatus.ERROR -> MaterialTheme.colorScheme.error
                NetworkStatus.CONNECTING, NetworkStatus.SYNCING, NetworkStatus.RECONNECTING ->
                    MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            Text(
                text = statusMessage.ifBlank {
                    when (networkState.status) {
                        NetworkStatus.CONNECTED -> "鉁� 賲鬲氐賱: ${networkState.roomName}"
                        NetworkStatus.CONNECTING -> "鈴� 噩丕乇賷 丕賱丕鬲氐丕賱..."
                        NetworkStatus.SYNCING -> "馃攳 噩丕乇賷 丕賱亘丨孬..."
                        NetworkStatus.ERROR -> "鉂� 禺胤兀"
                        else -> "猸� 睾賷乇 賲鬲氐賱"
                    }
                },
                color = statusColor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(12.dp))

            if (!networkState.isConnected) {
                // Player name input
                OutlinedTextField(
                    value = playerName,
                    onValueChange = { playerName = it },
                    label = { Text("丕爻賲賰 賮賷 丕賱賱毓亘丞") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onShowCreateDialog,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) { Text("馃彔 兀賳卮卅 睾乇賮丞") }

                    OutlinedButton(
                        onClick = onDiscover,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("馃攳 丕亘丨孬")
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (isLoading) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(8.dp))
                            Text("噩丕乇賷 丕賱亘丨孬... (3 孬賵丕賳賷)", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                // Rooms list
                if (discoveredRooms.isNotEmpty()) {
                    Text("丕賱睾乇賮 丕賱賲鬲丕丨丞:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(discoveredRooms) { room ->
                            RoomCard(
                                room = room,
                                onJoin = {
                                    if (playerName.isBlank()) roomToJoin = room
                                    else onJoinRoom(room, playerName)
                                }
                            )
                        }
                    }
                } else if (!isLoading) {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("馃摗", style = MaterialTheme.typography.displaySmall)
                            Text("賱丕 鬲賵噩丿 睾乇賮 鈥� 丕囟睾胤 亘丨孬",
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

            } else {
                // Connected view
                ConnectedView(
                    networkState = networkState,
                    onLeave = onLeaveRoom
                )
            }

            // Error
            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(error, Modifier.weight(1f), color = MaterialTheme.colorScheme.onErrorContainer)
                        TextButton(onClick = onClearError) { Text("鉁�") }
                    }
                }
            }

            // Missing name warning
            if (roomToJoin != null) {
                AlertDialog(
                    onDismissRequest = { roomToJoin = null },
                    title = { Text("兀丿禺賱 丕爻賲賰") },
                    text = {
                        OutlinedTextField(
                            value = playerName,
                            onValueChange = { playerName = it },
                            label = { Text("丕爻賲賰") },
                            singleLine = true
                        )
                    },
                    confirmButton = {
                        Button(onClick = {
                            onJoinRoom(roomToJoin!!, playerName)
                            roomToJoin = null
                        }, enabled = playerName.isNotBlank()) { Text("丕賳囟賲丕賲") }
                    },
                    dismissButton = { TextButton(onClick = { roomToJoin = null }) { Text("廿賱睾丕亍") } }
                )
            }
        }
    }

    // Create room dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = onDismissCreateDialog,
            title = { Text("馃彔 廿賳卮丕亍 睾乇賮丞 噩丿賷丿丞") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("爻賷鬲賲賰賳 丕賱兀氐丿賯丕亍 毓賱賶 賳賮爻 丕賱賭 WiFi 賲賳 乇丐賷丞 睾乇賮鬲賰 賵丕賱丕賳囟賲丕賲 廿賱賷賴丕.")
                    OutlinedTextField(
                        value = createRoomName,
                        onValueChange = { createRoomName = it },
                        label = { Text("丕爻賲 丕賱睾乇賮丞") },
                        placeholder = { Text("賲孬丕賱: 睾乇賮丞 賮賷氐賱") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { onCreateRoom(createRoomName); createRoomName = "" },
                    enabled = createRoomName.isNotBlank()
                ) { Text("廿賳卮丕亍") }
            },
            dismissButton = { TextButton(onClick = onDismissCreateDialog) { Text("廿賱睾丕亍") } }
        )
    }
}

@Composable
private fun RoomCard(room: NetworkRoom, onJoin: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(room.name, fontWeight = FontWeight.Bold)
                Text("丕賱賲囟賷賮: ${room.hostName}", style = MaterialTheme.typography.bodySmall)
                Text(
                    "${room.currentPlayers}/${room.maxPlayers} 賱丕毓亘",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (room.currentPlayers < room.maxPlayers)
                        MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            Button(
                onClick = onJoin,
                enabled = room.currentPlayers < room.maxPlayers
            ) { Text("丕賳囟賲丕賲") }
        }
    }
}

@Composable
private fun ConnectedView(networkState: NetworkState, onLeave: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("丕賱睾乇賮丞: ${networkState.roomName}", fontWeight = FontWeight.Bold)
            Text(if (networkState.isHost) "兀賳鬲 丕賱賲囟賷賮" else "賱丕毓亘",
                style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Text("丕賱賱丕毓亘賵賳 丕賱賲鬲氐賱賵賳:", fontWeight = FontWeight.Medium)
            networkState.connectedPlayers.forEach { player ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (player.isHost) "馃憫" else "馃懁")
                    Spacer(Modifier.width(4.dp))
                    Text(player.name)
                    if (player.isReady) {
                        Spacer(Modifier.width(4.dp))
                        Text("鉁�", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            if (networkState.isHost) {
                Text(
                    "賮賷 丕賳鬲馗丕乇 丕賱賱丕毓亘賷賳... (${networkState.playerCount}/${4})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
    Spacer(Modifier.height(12.dp))
    OutlinedButton(onClick = onLeave, modifier = Modifier.fillMaxWidth()) { Text("賲睾丕丿乇丞 丕賱睾乇賮丞") }
}
