package com.example.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.MatchEntity
import com.example.data.UserEntity
import com.example.engine.AntiCheatMonitor
import com.example.engine.AntiCheatStatus
import com.example.engine.BotEngine
import com.example.engine.RummyEvaluator
import com.example.model.CardMeld
import com.example.model.CosmeticType
import com.example.model.Friend
import com.example.model.GameMode
import com.example.model.HouseRules
import com.example.model.LeaderboardUser
import com.example.model.MatchHistoryEntry
import com.example.model.MeldType
import com.example.model.NotificationItem
import com.example.model.Player
import com.example.model.PlayerStatus
import com.example.model.PlayingCard
import com.example.model.Rank
import com.example.model.StoreItem
import com.example.model.Suit
import com.example.model.UserStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.random.Random

enum class AppScreen {
    HOME,
    LOBBY,
    GAME_TABLE,
    STORE,
    LEADERBOARD,
    STATS_DASHBOARD,
    SOCIAL_HUB
}

class RummyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val dao = db.rummyDao()

    // Navigation State
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // User State
    private val _userProfile = MutableStateFlow(UserEntity())
    val userProfile: StateFlow<UserEntity> = _userProfile.asStateFlow()

    // House Rules & Lobby State
    private val _houseRules = MutableStateFlow(HouseRules(maxPlayers = 4))
    val houseRules: StateFlow<HouseRules> = _houseRules.asStateFlow()

    // Match Players (2 to 8 players)
    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players.asStateFlow()

    // Game Table Decks
    private val _stockDeck = MutableStateFlow<List<PlayingCard>>(emptyList())
    val stockDeck: StateFlow<List<PlayingCard>> = _stockDeck.asStateFlow()

    private val _discardDeck = MutableStateFlow<List<PlayingCard>>(emptyList())
    val discardDeck: StateFlow<List<PlayingCard>> = _discardDeck.asStateFlow()

    private val _wildJoker = MutableStateFlow<PlayingCard?>(null)
    val wildJoker: StateFlow<PlayingCard?> = _wildJoker.asStateFlow()

    // Active Turn State
    private val _currentTurnSeatIndex = MutableStateFlow(0)
    val currentTurnSeatIndex: StateFlow<Int> = _currentTurnSeatIndex.asStateFlow()

    private val _turnTimeRemaining = MutableStateFlow(30)
    val turnTimeRemaining: StateFlow<Int> = _turnTimeRemaining.asStateFlow()

    private val _hasDrawnCardThisTurn = MutableStateFlow(false)
    val hasDrawnCardThisTurn: StateFlow<Boolean> = _hasDrawnCardThisTurn.asStateFlow()

    // Human Player Hand & Melds
    private val _humanHand = MutableStateFlow<List<PlayingCard>>(emptyList())
    val humanHand: StateFlow<List<PlayingCard>> = _humanHand.asStateFlow()

    private val _humanMelds = MutableStateFlow<List<CardMeld>>(emptyList())
    val humanMelds: StateFlow<List<CardMeld>> = _humanMelds.asStateFlow()

    private val _selectedCards = MutableStateFlow<Set<String>>(emptySet())
    val selectedCards: StateFlow<Set<String>> = _selectedCards.asStateFlow()

    // Voice Chat Simulation
    private val _isMicMuted = MutableStateFlow(false)
    val isMicMuted: StateFlow<Boolean> = _isMicMuted.asStateFlow()

    private val _isSpeakerMuted = MutableStateFlow(false)
    val isSpeakerMuted: StateFlow<Boolean> = _isSpeakerMuted.asStateFlow()

    private val _voiceWaveLevels = MutableStateFlow(listOf(0.2f, 0.5f, 0.8f, 0.3f, 0.6f))
    val voiceWaveLevels: StateFlow<List<Float>> = _voiceWaveLevels.asStateFlow()

    // Anti-Cheat & Low Latency Sync
    private val _antiCheatStatus = MutableStateFlow(AntiCheatStatus())
    val antiCheatStatus: StateFlow<AntiCheatStatus> = _antiCheatStatus.asStateFlow()

    // Store Items
    private val _storeItems = MutableStateFlow<List<StoreItem>>(emptyList())
    val storeItems: StateFlow<List<StoreItem>> = _storeItems.asStateFlow()

    // Social & Friends
    private val _friends = MutableStateFlow<List<Friend>>(emptyList())
    val friends: StateFlow<List<Friend>> = _friends.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _leaderboard = MutableStateFlow<List<LeaderboardUser>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardUser>> = _leaderboard.asStateFlow()

    // Dialogs & Modals
    private val _showDailyRewardModal = MutableStateFlow(false)
    val showDailyRewardModal: StateFlow<Boolean> = _showDailyRewardModal.asStateFlow()

    private val _isDailyRewardClaimedToday = MutableStateFlow(false)
    val isDailyRewardClaimedToday: StateFlow<Boolean> = _isDailyRewardClaimedToday.asStateFlow()

    private val _showDeclarationModal = MutableStateFlow(false)
    val showDeclarationModal: StateFlow<Boolean> = _showDeclarationModal.asStateFlow()

    private val _declarationResult = MutableStateFlow<Pair<Boolean, String>?>(null)
    val declarationResult: StateFlow<Pair<Boolean, String>?> = _declarationResult.asStateFlow()

    private val _showReportModal = MutableStateFlow(false)
    val showReportModal: StateFlow<Boolean> = _showReportModal.asStateFlow()

    private val _showShareModal = MutableStateFlow(false)
    val showShareModal: StateFlow<Boolean> = _showShareModal.asStateFlow()

    // AdMob Post Game State
    private val _showPostGameAd = MutableStateFlow(false)
    val showPostGameAd: StateFlow<Boolean> = _showPostGameAd.asStateFlow()

    private val _isGameWin = MutableStateFlow(false)
    val isGameWin: StateFlow<Boolean> = _isGameWin.asStateFlow()

    private val _earnedChips = MutableStateFlow(0L)
    val earnedChips: StateFlow<Long> = _earnedChips.asStateFlow()

    private val _matchHistoryList = MutableStateFlow<List<MatchHistoryEntry>>(emptyList())
    val matchHistoryList: StateFlow<List<MatchHistoryEntry>> = _matchHistoryList.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private var turnTimerJob: Job? = null
    private var voiceWaveJob: Job? = null

    init {
        loadInitialData()
        startVoiceWaveformAnimation()
        startAntiCheatMonitoring()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            dao.getUserProfile().collect { entity ->
                if (entity != null) {
                    _userProfile.value = entity
                    _isDailyRewardClaimedToday.value = checkIfClaimedToday(entity.lastRewardClaimTime)
                } else {
                    val defaultUser = UserEntity()
                    dao.saveUserProfile(defaultUser)
                    _userProfile.value = defaultUser
                    _isDailyRewardClaimedToday.value = false
                }
            }
        }

        viewModelScope.launch {
            dao.getMatchHistory().collect { historyEntities ->
                _matchHistoryList.value = historyEntities.map { entity ->
                    MatchHistoryEntry(
                        id = entity.id,
                        dateString = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(entity.timestamp)),
                        modeName = entity.modeName,
                        playerCount = entity.playerCount,
                        rankChange = if (entity.isWin) "+25 MMR" else "-12 MMR",
                        chipsDelta = entity.chipsDelta,
                        isWin = entity.isWin
                    )
                }
            }
        }

        initializeStoreItems()
        initializeFriends()
        initializeNotifications()
        initializeLeaderboard()
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun updateHouseRules(newRules: HouseRules) {
        _houseRules.value = newRules
        showToast("House rules updated for ${newRules.maxPlayers} players!")
    }

    fun updateUsername(newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            val updatedProfile = _userProfile.value.copy(username = newName)
            dao.saveUserProfile(updatedProfile)
            _userProfile.value = updatedProfile
            showToast("Profile name updated to $newName!")
        }
    }

    fun setPlayerCount(count: Int) {
        _houseRules.value = _houseRules.value.copy(maxPlayers = count.coerceIn(2, 8))
    }

    fun setGameMode(mode: GameMode) {
        _houseRules.value = _houseRules.value.copy(gameMode = mode)
    }

    /**
     * Prepares lobby seats for 2 to 8 players and enters LOBBY screen
     */
    fun createLobby(isPrivate: Boolean = true) {
        val rules = _houseRules.value.copy(
            isPrivateRoom = isPrivate,
            roomCode = if (isPrivate) generateRoomCode() else "PUBLIC"
        )
        _houseRules.value = rules

        val botNames = listOf("Astra_Bot", "Viper_AI", "Cyber_Rex", "Queen_Val", "Shadow_Joker", "Ace_Master", "Titan_AI")
        val botAvatars = listOf("avatar_bot1", "avatar_bot2", "avatar_bot3", "avatar_bot4", "avatar_bot5", "avatar_bot6", "avatar_bot7")

        val seatList = mutableListOf<Player>()
        // Seat 0: Human
        seatList.add(
            Player(
                id = "human_user",
                name = _userProfile.value.username,
                avatarRes = _userProfile.value.avatarRes,
                seatIndex = 0,
                chips = _userProfile.value.chips,
                isHuman = true,
                isHost = true,
                isReady = true,
                isBot = false,
                isVoiceConnected = true
            )
        )

        // Seats 1 to maxPlayers - 1: Bots / Simulated Players
        for (i in 1 until rules.maxPlayers) {
            seatList.add(
                Player(
                    id = "bot_$i",
                    name = botNames[(i - 1) % botNames.size],
                    avatarRes = botAvatars[(i - 1) % botAvatars.size],
                    seatIndex = i,
                    chips = Random.nextLong(20000L, 250000L),
                    isHuman = false,
                    isHost = false,
                    isReady = true,
                    isBot = true,
                    isVoiceConnected = Random.nextBoolean()
                )
            )
        }

        _players.value = seatList
        _currentScreen.value = AppScreen.LOBBY
    }

    /**
     * Join Room by Code
     */
    fun joinRoom(code: String) {
        val trimmedCode = code.trim().uppercase()
        if (trimmedCode.isEmpty()) {
            showToast("Please enter a valid Room Code!")
            return
        }

        val rules = _houseRules.value.copy(
            isPrivateRoom = true,
            roomCode = trimmedCode
        )
        _houseRules.value = rules

        val botNames = listOf("RoomHost_Ace", "Viper_AI", "Cyber_Rex", "Queen_Val")
        val botAvatars = listOf("avatar_bot1", "avatar_bot2", "avatar_bot3", "avatar_bot4")

        val seatList = mutableListOf<Player>()
        // Seat 0: Host
        seatList.add(
            Player(
                id = "bot_host",
                name = botNames[0],
                avatarRes = botAvatars[0],
                seatIndex = 0,
                chips = 150000L,
                isHuman = false,
                isHost = true,
                isReady = true,
                isBot = true,
                isOnline = true
            )
        )
        // Seat 1: Human User
        seatList.add(
            Player(
                id = "human_user",
                name = _userProfile.value.username,
                avatarRes = _userProfile.value.avatarRes,
                seatIndex = 1,
                chips = _userProfile.value.chips,
                isHuman = true,
                isHost = false,
                isReady = true,
                isBot = false,
                isOnline = true
            )
        )
        // Seats 2 to maxPlayers - 1
        for (i in 2 until rules.maxPlayers) {
            seatList.add(
                Player(
                    id = "bot_$i",
                    name = botNames[i % botNames.size],
                    avatarRes = botAvatars[i % botAvatars.size],
                    seatIndex = i,
                    chips = Random.nextLong(20000L, 250000L),
                    isHuman = false,
                    isHost = false,
                    isReady = true,
                    isBot = true,
                    isOnline = true
                )
            )
        }

        _players.value = seatList
        _currentScreen.value = AppScreen.LOBBY
        showToast("Successfully joined Room $trimmedCode!")
    }

    /**
     * Simulate Player Online / Offline Status Toggle
     */
    fun togglePlayerOnlineStatus(playerId: String) {
        val updated = _players.value.map { p ->
            if (p.id == playerId) {
                val newOnline = !p.isOnline
                if (!newOnline) {
                    showToast("⚠️ ${p.name} disconnected / went OFFLINE!")
                } else {
                    showToast("🟢 ${p.name} reconnected!")
                }
                p.copy(isOnline = newOnline)
            } else p
        }
        _players.value = updated
    }

    /**
     * Start actual card game from Lobby
     */
    fun startGame() {
        if (_userProfile.value.chips < _houseRules.value.entryFeeChips) {
            showToast("Insufficient chips for entry fee (${_houseRules.value.entryFeeChips} chips required)!")
            return
        }

        // Deduct entry fee
        viewModelScope.launch {
            dao.updateChips(-_houseRules.value.entryFeeChips)
        }

        // Deal Cards
        dealNewHand()
        _currentScreen.value = AppScreen.GAME_TABLE
        startTurnTimer()
    }

    private fun dealNewHand() {
        val rules = _houseRules.value
        val fullDeck = mutableListOf<PlayingCard>()

        // Generate 2 decks of 52 cards + 4 printed jokers if enabled
        val deckCount = if (rules.maxPlayers > 4) 3 else 2
        for (d in 0 until deckCount) {
            for (s in Suit.values()) {
                for (r in Rank.values()) {
                    fullDeck.add(PlayingCard(suit = s, rank = r))
                }
            }
            if (rules.allowPrintedJokers) {
                fullDeck.add(PlayingCard(suit = Suit.SPADES, rank = Rank.ACE, isPrintedJoker = true))
                fullDeck.add(PlayingCard(suit = Suit.HEARTS, rank = Rank.ACE, isPrintedJoker = true))
            }
        }

        fullDeck.shuffle()

        // Pick Wild Joker
        val wild = fullDeck.removeAt(0).copy(isWildJoker = true)
        _wildJoker.value = wild

        // Deal cardsPerPlayer to each player
        val updatedPlayers = _players.value.map { player ->
            val playerCards = mutableListOf<PlayingCard>()
            for (c in 0 until rules.cardsPerPlayer) {
                if (fullDeck.isNotEmpty()) {
                    val card = fullDeck.removeAt(0)
                    if (rules.allowWildJokers && card.rank == wild.rank) {
                        playerCards.add(card.copy(isWildJoker = true))
                    } else {
                        playerCards.add(card)
                    }
                }
            }
            player.copy(
                handCards = playerCards,
                status = PlayerStatus.THINKING,
                isWinner = false,
                pointsScore = 0
            )
        }

        _players.value = updatedPlayers

        // Open Discard Card
        val topDiscardCard = fullDeck.removeAt(0)
        _discardDeck.value = listOf(topDiscardCard)
        _stockDeck.value = fullDeck

        // Set human hand state & initial suit groups
        val human = updatedPlayers.firstOrNull { it.isHuman }
        if (human != null) {
            _humanHand.value = human.handCards
            val sorted = RummyEvaluator.autoSortBySuit(human.handCards)
            val initialGroups = sorted.groupBy { it.suit }.values.map { RummyEvaluator.evaluateMeld(it) }
            _humanMelds.value = initialGroups
        }

        _currentTurnSeatIndex.value = 0
        _hasDrawnCardThisTurn.value = false
        _selectedCards.value = emptySet()
    }

    /**
     * Player Actions: Draw Card from Stock Pile
     */
    fun drawFromStock() {
        if (_currentTurnSeatIndex.value != 0) {
            showToast("Wait for your turn!")
            return
        }
        if (_hasDrawnCardThisTurn.value) {
            showToast("You already drew a card this turn! Choose a card to discard.")
            return
        }
        val stock = _stockDeck.value.toMutableList()
        if (stock.isEmpty()) {
            val discards = _discardDeck.value.toMutableList()
            if (discards.size > 1) {
                val topDiscard = discards.removeAt(discards.size - 1)
                discards.shuffle()
                _stockDeck.value = discards
                _discardDeck.value = listOf(topDiscard)
                showToast("Stock deck reshuffled from discard pile!")
            } else {
                showToast("Stock pile empty!")
                return
            }
        }

        val currentStock = _stockDeck.value.toMutableList()
        if (currentStock.isNotEmpty()) {
            val drawnCard = currentStock.removeAt(0)
            _stockDeck.value = currentStock

            val newHand = _humanHand.value.toMutableList()
            newHand.add(drawnCard)
            _humanHand.value = newHand
            _hasDrawnCardThisTurn.value = true

            // Add drawn card to matching suit group in _humanMelds
            val currentMelds = _humanMelds.value.toMutableList()
            var added = false
            val updatedMelds = currentMelds.map { meld ->
                if (!added && meld.cards.isNotEmpty() && meld.cards.first().suit == drawnCard.suit) {
                    added = true
                    RummyEvaluator.evaluateMeld(meld.cards + drawnCard)
                } else meld
            }.toMutableList()

            if (!added) {
                updatedMelds.add(RummyEvaluator.evaluateMeld(listOf(drawnCard)))
            }
            _humanMelds.value = updatedMelds

            // Automatically select the newly drawn card so the user can discard it quickly
            _selectedCards.value = setOf(drawnCard.id)

            val updatedPlayers = _players.value.map { p ->
                if (p.isHuman) p.copy(handCards = newHand) else p
            }
            _players.value = updatedPlayers

            showToast("Drew ${drawnCard.displayValue}")
        }
    }

    /**
     * Player Actions: Draw Card from Discard Pile
     */
    fun drawFromDiscard() {
        if (_currentTurnSeatIndex.value != 0) {
            showToast("Wait for your turn!")
            return
        }
        if (_hasDrawnCardThisTurn.value) {
            showToast("You already drew a card this turn!")
            return
        }
        val discards = _discardDeck.value.toMutableList()
        if (discards.isEmpty()) {
            showToast("Discard pile is empty!")
            return
        }

        val drawnCard = discards.removeAt(discards.size - 1)
        _discardDeck.value = discards

        val newHand = _humanHand.value.toMutableList()
        newHand.add(drawnCard)
        _humanHand.value = newHand
        _hasDrawnCardThisTurn.value = true

        // Add drawn card to matching suit group in _humanMelds
        val currentMelds = _humanMelds.value.toMutableList()
        var added = false
        val updatedMelds = currentMelds.map { meld ->
            if (!added && meld.cards.isNotEmpty() && meld.cards.first().suit == drawnCard.suit) {
                added = true
                RummyEvaluator.evaluateMeld(meld.cards + drawnCard)
            } else meld
        }.toMutableList()

        if (!added) {
            updatedMelds.add(RummyEvaluator.evaluateMeld(listOf(drawnCard)))
        }
        _humanMelds.value = updatedMelds

        // Automatically select the drawn card for quick discard
        _selectedCards.value = setOf(drawnCard.id)

        val updatedPlayers = _players.value.map { p ->
            if (p.isHuman) p.copy(handCards = newHand) else p
        }
        _players.value = updatedPlayers

        showToast("Drew ${drawnCard.displayValue} from discard pile")
    }

    /**
     * Select / Deselect Card
     */
    fun toggleCardSelection(cardId: String) {
        val current = _selectedCards.value.toMutableSet()
        if (current.contains(cardId)) {
            current.remove(cardId)
        } else {
            current.add(cardId)
        }
        _selectedCards.value = current
    }

    /**
     * Player Action: Discard Selected Card
     */
    fun discardSelectedCard(card: PlayingCard) {
        if (_currentTurnSeatIndex.value != 0) {
            showToast("Not your turn!")
            return
        }
        if (!_hasDrawnCardThisTurn.value) {
            showToast("Draw a card first before discarding!")
            return
        }

        val newHand = _humanHand.value.filter { it.id != card.id }
        _humanHand.value = newHand

        // Remove card from _humanMelds
        val updatedMelds = _humanMelds.value.mapNotNull { meld ->
            val remaining = meld.cards.filter { it.id != card.id }
            if (remaining.isNotEmpty()) RummyEvaluator.evaluateMeld(remaining) else null
        }
        _humanMelds.value = updatedMelds

        val discards = _discardDeck.value.toMutableList()
        discards.add(card)
        _discardDeck.value = discards

        _selectedCards.value = emptySet()
        _hasDrawnCardThisTurn.value = false

        val updatedPlayers = _players.value.map { p ->
            if (p.isHuman) p.copy(handCards = newHand) else p
        }
        _players.value = updatedPlayers

        showToast("Discarded ${card.displayValue}")

        // Pass turn to next seat
        advanceTurn()
    }

    /**
     * Group selected cards in single hand row
     */
    fun groupSelectedCards() {
        val selectedIds = _selectedCards.value
        if (selectedIds.size < 2) {
            showToast("Select at least 2 cards to form a group!")
            return
        }

        val currentMelds = _humanMelds.value.toMutableList()
        val selectedCardsList = _humanHand.value.filter { selectedIds.contains(it.id) }

        // Remove selected cards from all existing groups
        val newGroups = mutableListOf<CardMeld>()
        for (meld in currentMelds) {
            val remainingCards = meld.cards.filter { !selectedIds.contains(it.id) }
            if (remainingCards.isNotEmpty()) {
                newGroups.add(RummyEvaluator.evaluateMeld(remainingCards))
            }
        }

        // Add selected cards as a brand new group
        val newMeld = RummyEvaluator.evaluateMeld(selectedCardsList)
        newGroups.add(newMeld)

        _humanMelds.value = newGroups
        _selectedCards.value = emptySet()

        showToast("Grouped ${selectedCardsList.size} cards (${newMeld.type.label})!")
    }

    /**
     * Ungroup selected cards
     */
    fun ungroupSelectedCards() {
        val selectedIds = _selectedCards.value
        if (selectedIds.isEmpty()) {
            autoSortHandBySuit()
            return
        }

        val currentMelds = _humanMelds.value.toMutableList()
        val newGroups = mutableListOf<CardMeld>()
        val ungroupedPool = mutableListOf<PlayingCard>()

        for (meld in currentMelds) {
            val keepCards = meld.cards.filter { !selectedIds.contains(it.id) }
            val removeCards = meld.cards.filter { selectedIds.contains(it.id) }
            if (keepCards.isNotEmpty()) {
                newGroups.add(RummyEvaluator.evaluateMeld(keepCards))
            }
            ungroupedPool.addAll(removeCards)
        }

        if (ungroupedPool.isNotEmpty()) {
            newGroups.add(RummyEvaluator.evaluateMeld(ungroupedPool))
        }

        _humanMelds.value = newGroups
        _selectedCards.value = emptySet()
        showToast("Ungrouped selected cards!")
    }

    /**
     * Auto Sort Hand by Suit & group in same row
     */
    fun autoSortHandBySuit() {
        val sortedHand = RummyEvaluator.autoSortBySuit(_humanHand.value)
        _humanHand.value = sortedHand

        val suitGroups = sortedHand
            .groupBy { it.suit }
            .values
            .map { cardsInSuit -> RummyEvaluator.evaluateMeld(cardsInSuit) }

        _humanMelds.value = suitGroups
        showToast("Grouped hand by Suit (♣ ♦ ♥ ♠)")
    }

    fun autoSortHandByRank() {
        val sortedHand = RummyEvaluator.autoSortByRank(_humanHand.value)
        _humanHand.value = sortedHand

        val rankGroups = sortedHand
            .groupBy { it.rank }
            .values
            .map { cardsInRank -> RummyEvaluator.evaluateMeld(cardsInRank) }

        _humanMelds.value = rankGroups
        showToast("Grouped hand by Rank (Ace - King)")
    }

    /**
     * Player Action: Declare / Finish Hand
     */
    fun declareHand() {
        val hand = _humanHand.value
        // Auto group hand if unmelded
        val melds = if (_humanMelds.value.isNotEmpty()) _humanMelds.value else {
            hand.chunked(3).map { RummyEvaluator.evaluateMeld(it) }
        }

        val result = RummyEvaluator.validateDeclaration(melds)
        _declarationResult.value = result
        _showDeclarationModal.value = true

        if (result.first) {
            // Player won match!
            val winChips = _houseRules.value.entryFeeChips * _players.value.size
            _isGameWin.value = true
            _earnedChips.value = winChips

            viewModelScope.launch {
                dao.updateChips(winChips)
                dao.insertMatchHistory(
                    MatchEntity(
                        id = UUID.randomUUID().toString(),
                        timestamp = System.currentTimeMillis(),
                        modeName = _houseRules.value.gameMode.displayName,
                        playerCount = _players.value.size,
                        pointsScore = 0,
                        chipsDelta = winChips,
                        isWin = true
                    )
                )
            }
        } else {
            _isGameWin.value = false
            _earnedChips.value = 0L
        }
    }

    fun dismissDeclarationModal() {
        val isWin = _declarationResult.value?.first == true
        _showDeclarationModal.value = false
        if (isWin) {
            // Trigger AdMob post game overlay after winning declaration
            _showPostGameAd.value = true
        }
    }

    fun dismissPostGameAd() {
        _showPostGameAd.value = false
        _currentScreen.value = AppScreen.HOME
    }

    fun triggerTestAdMob() {
        _isGameWin.value = true
        _earnedChips.value = 50000L
        _showPostGameAd.value = true
    }

    /**
     * Advance turn to next seat (handles 2 to 8 player turns & bot turns)
     */
    private fun advanceTurn() {
        val nextSeat = (_currentTurnSeatIndex.value + 1) % _players.value.size
        _currentTurnSeatIndex.value = nextSeat
        _hasDrawnCardThisTurn.value = false
        _turnTimeRemaining.value = _houseRules.value.turnTimeSeconds

        if (nextSeat != 0) {
            // Bot turn simulation
            simulateBotTurn(nextSeat)
        }
    }

    private fun simulateBotTurn(botSeatIndex: Int) {
        viewModelScope.launch {
            delay(1500) // Realistic thinking pause
            val bot = _players.value.firstOrNull { it.seatIndex == botSeatIndex } ?: return@launch
            val topDiscard = _discardDeck.value.lastOrNull()
            
            val drawFromDiscard = BotEngine.shouldDrawFromDiscard(bot.handCards, topDiscard)
            val botHand = bot.handCards.toMutableList()

            if (drawFromDiscard && topDiscard != null) {
                _discardDeck.value = _discardDeck.value.dropLast(1)
                botHand.add(topDiscard)
            } else {
                val stock = _stockDeck.value.toMutableList()
                if (stock.isNotEmpty()) {
                    val drawn = stock.removeAt(0)
                    _stockDeck.value = stock
                    botHand.add(drawn)
                }
            }

            delay(1200)
            val cardToDiscard = BotEngine.chooseCardToDiscard(botHand)
            botHand.remove(cardToDiscard)

            val discards = _discardDeck.value.toMutableList()
            discards.add(cardToDiscard)
            _discardDeck.value = discards

            // Update bot hand
            val updated = _players.value.map {
                if (it.seatIndex == botSeatIndex) it.copy(handCards = botHand) else it
            }
            _players.value = updated

            // Check if bot can declare
            val botMelds = BotEngine.canBotDeclare(botHand)
            if (botMelds != null && Random.nextFloat() < 0.15f) {
                // Bot declares
                _isGameWin.value = false
                _earnedChips.value = 0L
                _showPostGameAd.value = true
                showToast("${bot.name} declared the hand! Match finished.")
            } else {
                advanceTurn()
            }
        }
    }

    private fun startTurnTimer() {
        turnTimerJob?.cancel()
        turnTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_turnTimeRemaining.value > 0) {
                    _turnTimeRemaining.value -= 1
                } else {
                    // Turn expired -> auto drop or auto advance
                    showToast("Turn time expired!")
                    advanceTurn()
                }
            }
        }
    }

    /**
     * Voice Chat Controls
     */
    fun toggleMic() {
        _isMicMuted.value = !_isMicMuted.value
        showToast(if (_isMicMuted.value) "Microphone Muted" else "Microphone Unmuted (Low Latency RTC Active)")
    }

    fun toggleSpeaker() {
        _isSpeakerMuted.value = !_isSpeakerMuted.value
        showToast(if (_isSpeakerMuted.value) "Voice Audio Muted" else "Voice Audio Unmuted")
    }

    private var audioRecord: AudioRecord? = null

    private fun startVoiceWaveformAnimation() {
        voiceWaveJob?.cancel()
        voiceWaveJob = viewModelScope.launch(Dispatchers.IO) {
            val sampleRate = 16000
            val channelConfig = AudioFormat.CHANNEL_IN_MONO
            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
            val bufferSize = minBufferSize.coerceAtLeast(1024)
            val buffer = ShortArray(bufferSize)

            while (true) {
                if (!_isMicMuted.value) {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        getApplication(),
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermission) {
                        try {
                            if (audioRecord == null || audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                                audioRecord = AudioRecord(
                                    MediaRecorder.AudioSource.MIC,
                                    sampleRate,
                                    channelConfig,
                                    audioFormat,
                                    bufferSize
                                )
                            }
                            if (audioRecord?.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
                                audioRecord?.startRecording()
                            }

                            val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                            if (read > 0) {
                                var sum = 0.0
                                for (i in 0 until read) {
                                    sum += buffer[i] * buffer[i]
                                }
                                val rms = Math.sqrt(sum / read)
                                val normalized = (rms / 3000.0).toFloat().coerceIn(0.15f, 1.0f)
                                _voiceWaveLevels.value = listOf(
                                    normalized * 0.8f + Random.nextFloat() * 0.2f,
                                    normalized,
                                    normalized * 0.9f,
                                    normalized * 0.7f + Random.nextFloat() * 0.3f,
                                    normalized * 0.85f
                                )
                            } else {
                                generateSimulatedWaveLevels()
                            }
                        } catch (e: Exception) {
                            stopAudioRecording()
                            generateSimulatedWaveLevels()
                        }
                    } else {
                        stopAudioRecording()
                        generateSimulatedWaveLevels()
                    }
                } else {
                    stopAudioRecording()
                    _voiceWaveLevels.value = listOf(0.1f, 0.1f, 0.1f, 0.1f, 0.1f)
                }
                delay(100)
            }
        }
    }

    private fun generateSimulatedWaveLevels() {
        _voiceWaveLevels.value = listOf(
            Random.nextFloat().coerceIn(0.15f, 0.85f),
            Random.nextFloat().coerceIn(0.2f, 1.0f),
            Random.nextFloat().coerceIn(0.15f, 0.9f),
            Random.nextFloat().coerceIn(0.1f, 0.8f),
            Random.nextFloat().coerceIn(0.2f, 0.95f)
        )
    }

    private fun stopAudioRecording() {
        try {
            if (audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                audioRecord?.stop()
            }
            audioRecord?.release()
            audioRecord = null
        } catch (_: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        stopAudioRecording()
    }

    private fun startAntiCheatMonitoring() {
        viewModelScope.launch {
            while (true) {
                delay(3000)
                _antiCheatStatus.value = AntiCheatMonitor.inspectMatchState(_houseRules.value.roomCode)
            }
        }
    }

    /**
     * Store Buy & Equip
     */
    fun buyOrEquipItem(item: StoreItem) {
        val currentItems = _storeItems.value.toMutableList()
        val index = currentItems.indexOfFirst { it.id == item.id }
        if (index == -1) return

        val target = currentItems[index]
        if (!target.isOwned) {
            if (_userProfile.value.chips < target.priceChips) {
                showToast("Not enough chips! Requires ${target.priceChips} chips.")
                return
            }
            viewModelScope.launch {
                dao.updateChips(-target.priceChips)
            }
            currentItems[index] = target.copy(isOwned = true, isEquipped = true)
            showToast("Purchased and equipped ${target.title}!")
        } else {
            // Unequip others of same type, equip target
            currentItems.indices.forEach { i ->
                if (currentItems[i].type == target.type) {
                    currentItems[i] = currentItems[i].copy(isEquipped = (currentItems[i].id == target.id))
                }
            }
            showToast("Equipped ${target.title}!")
        }
        _storeItems.value = currentItems
    }

    /**
     * Claim Daily Reward
     */
    fun claimDailyReward() {
        if (_isDailyRewardClaimedToday.value) {
            showToast("You have already claimed your reward for today!")
            return
        }
        val rewardChips = 15000L
        viewModelScope.launch {
            dao.updateChips(rewardChips)
            val now = System.currentTimeMillis()
            dao.saveUserProfile(_userProfile.value.copy(lastRewardClaimTime = now))
            _isDailyRewardClaimedToday.value = true
        }
        _showDailyRewardModal.value = false
        showToast("Claimed Daily Reward: +15,000 Chips!")
    }

    fun openDailyRewardModal() {
        _showDailyRewardModal.value = true
    }

    fun closeDailyRewardModal() {
        _showDailyRewardModal.value = false
    }

    fun openReportModal() {
        _showReportModal.value = true
    }

    fun closeReportModal() {
        _showReportModal.value = false
    }

    fun submitPlayerReport(reason: String) {
        _showReportModal.value = false
        showToast("Report submitted to Server Integrity Team. Log #${Random.nextInt(10000, 99999)} filed.")
    }

    fun openShareModal() {
        _showShareModal.value = true
    }

    fun closeShareModal() {
        _showShareModal.value = false
    }

    fun showToast(msg: String) {
        viewModelScope.launch {
            _toastMessage.value = msg
            delay(2500)
            if (_toastMessage.value == msg) {
                _toastMessage.value = null
            }
        }
    }

    private fun generateRoomCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..6).map { chars.random() }.joinToString("")
    }

    private fun checkIfClaimedToday(lastClaimTime: Long): Boolean {
        if (lastClaimTime == 0L) return false
        val now = Calendar.getInstance()
        val last = Calendar.getInstance().apply { timeInMillis = lastClaimTime }
        return now.get(Calendar.YEAR) == last.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == last.get(Calendar.DAY_OF_YEAR)
    }

    private fun initializeStoreItems() {
        _storeItems.value = listOf(
            StoreItem("card_gold", "Royal Gold Deck", "3D Gold foil back design with embossed crown", 25000L, CosmeticType.CARD_BACK, "ic_card", "#FFD700", isOwned = true, isEquipped = true),
            StoreItem("card_cyber", "Cyber Neon Deck", "Glowing cyan grid lines with matrix aesthetic", 50000L, CosmeticType.CARD_BACK, "ic_card", "#00E5FF"),
            StoreItem("card_velvet", "Velvet Crimson Deck", "Deep velvet texture with ruby flourishes", 35000L, CosmeticType.CARD_BACK, "ic_card", "#D32F2F"),
            StoreItem("felt_emerald", "Monte Carlo Emerald", "Classic luxury casino felt table theme", 0L, CosmeticType.TABLE_FELT, "ic_felt", "#144D30", isOwned = true, isEquipped = true),
            StoreItem("felt_sapphire", "Sapphire Night", "Deep navy felt with silver starbursts", 40000L, CosmeticType.TABLE_FELT, "ic_felt", "#1B2A4A"),
            StoreItem("felt_velvet", "Royal Purple Velvet", "Majestic purple felt table with gold embroidery", 60000L, CosmeticType.TABLE_FELT, "ic_felt", "#4A1B4E"),
            StoreItem("frame_crown", "Golden Crown Frame", "Shimmering animated crown avatar frame", 30000L, CosmeticType.AVATAR_FRAME, "ic_frame", "#FFD700", isOwned = true, isEquipped = true),
            StoreItem("frame_diamond", "Diamond Shark Frame", "Ice diamond glow frame with shark badge", 75000L, CosmeticType.AVATAR_FRAME, "ic_frame", "#B9F2FF"),
            StoreItem("chips_starter", "Stack of 50K Chips", "Instant chip refill bundle", 10000L, CosmeticType.CHIP_BUNDLE, "ic_chip", "#FFD700")
        )
    }

    private fun initializeFriends() {
        _friends.value = listOf(
            Friend("f1", "Alex_Spades", "avatar_f1", "In Lobby (3/4)", "Grandmaster", 450000L, isOnline = true),
            Friend("f2", "Elena_Rummy", "avatar_f2", "Playing Points Rummy", "Diamond I", 210000L, isOnline = true),
            Friend("f3", "Vikram_Ace", "avatar_f3", "Online - Idle", "Master", 120000L, isOnline = true),
            Friend("f4", "Sarah_Joker", "avatar_f4", "Offline - 2h ago", "Gold III", 45000L, isOnline = false)
        )
    }

    private fun initializeNotifications() {
        _notifications.value = listOf(
            NotificationItem("n1", "Weekend Rummy Major", "100,000 Chip Pool Tournament starts in 30 minutes!", "10m ago"),
            NotificationItem("n2", "Friend Invite", "Alex_Spades invited you to Private Lobby #RUMMY8", "1h ago"),
            NotificationItem("n3", "Daily Streak", "Log in today to claim your 15,000 Free Chips bonus!", "5h ago")
        )
    }

    private fun initializeLeaderboard() {
        _leaderboard.value = listOf(
            LeaderboardUser(1, "King_Rummy_99", "avatar_top1", 4850, "Grandmaster", 78),
            LeaderboardUser(2, "Queen_Of_Spades", "avatar_top2", 4210, "Grandmaster", 74),
            LeaderboardUser(3, "Viper_Cards", "avatar_top3", 3980, "Master I", 71),
            LeaderboardUser(4, "RoyalPlayer (You)", "avatar_gold", 2450, "Diamond II", 67, isCurrentUser = true),
            LeaderboardUser(5, "Astra_Rummy", "avatar_top5", 2310, "Diamond III", 64)
        )
    }
}
