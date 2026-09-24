# Walkthrough - Modernized Lobby Seat Grid

I have updated the Match Lobby to use the circular "Avatar and Name" design and made the player grid flexible in height.

## Changes Made

### [LobbyScreen.kt](file:///G:/app/New folder/A/rummy-club (4)/app/src/main/java/com/example/ui/screens/LobbyScreen.kt)

1.  **Switched to Simple Mode**: Player seats in the lobby now use circular avatars and name labels, matching the updated game screen style.
2.  **Flexible Height Layout**: Replaced the fixed-height `LazyVerticalGrid` with a dynamic `Column` + `Row` structure using `chunked(4)`.
3.  **Automatic Resizing**: The green "Casino Felt" section now automatically adjusts its height based on the number of players (1 row for up to 4 players, 2 rows for up to 8 players).
4.  **Optimized Spacing**: Used `Arrangement.SpaceEvenly` for player rows and added `16.dp` vertical spacing between rows to maintain a professional look.

```diff
-                // Grid of Seats (2 to 8 seats)
-                LazyVerticalGrid(
-                    columns = GridCells.Fixed(4),
-                    modifier = Modifier
-                        .fillMaxWidth()
-                        .height(180.dp),
-                    horizontalArrangement = Arrangement.spacedBy(8.dp),
-                    verticalArrangement = Arrangement.spacedBy(8.dp)
-                ) {
-                    items(players) { player ->
-                        Box(
-                            modifier = Modifier
-                                .clip(RoundedCornerShape(12.dp))
-                                .clickable { viewModel.togglePlayerOnlineStatus(player.id) }
-                        ) {
-                            PlayerSeatView(
-                                player = player,
-                                isCurrentTurn = false,
-                                turnSecondsLeft = houseRules.turnTimeSeconds
-                            )
-                        }
-                    }
-                }
+                // Flexible Grid of Seats (2 to 8 seats)
+                Column(
+                    modifier = Modifier.fillMaxWidth(),
+                    verticalArrangement = Arrangement.spacedBy(16.dp)
+                ) {
+                    players.chunked(4).forEach { rowPlayers ->
+                        Row(
+                            modifier = Modifier.fillMaxWidth(),
+                            horizontalArrangement = Arrangement.SpaceEvenly
+                        ) {
+                            rowPlayers.forEach { player ->
+                                Box(
+                                    modifier = Modifier
+                                        .clip(RoundedCornerShape(12.dp))
+                                        .clickable { viewModel.togglePlayerOnlineStatus(player.id) }
+                                ) {
+                                    PlayerSeatView(
+                                        player = player,
+                                        isCurrentTurn = false,
+                                        turnSecondsLeft = houseRules.turnTimeSeconds,
+                                        isSimpleMode = true
+                                    )
+                                }
+                            }
+                        }
+                    }
+                }
```

## Verification Results

- **Build**: Successful.
- **Lobby UI**:
    - Circular avatars are displayed correctly.
    - The green container height changes dynamically when toggling between player counts (e.g., 4P vs 8P).
    - The layout remains centered and responsive.
