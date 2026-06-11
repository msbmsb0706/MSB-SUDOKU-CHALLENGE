package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "active_game")
data class ActiveGameEntity(
    @PrimaryKey val id: Int = 1, // Only 1 active draft puzzle at a time
    val puzzleStr: String,       // 81 characters of numbers 0-9
    val solutionStr: String,     // 81 characters of solution numbers 1-9
    val userDraftStr: String,    // 81 characters of user-entered numbers
    val pencilNotesStr: String,  // 81 elements of pipe-separated active notes, e.g. "12|3"
    val secondsElapsed: Long,
    val mistakes: Int,
    val completed: Boolean,
    val difficulty: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val userId: String = "unique_current_player",
    val username: String = "MSB_Player_One",
    val region: String = "Americas", // Americas, Europe, Asia-Pacific, Africa
    val xp: Int = 240,
    val level: Int = 1,
    val playGoldPoints: Int = 2750, // Point bank (redeemable for Google Play Gifts)
    val gems: Int = 45,             // Custom gems currency (e.g., standard premium rewards)
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val bestTimeEasy: Long = 0L,
    val bestTimeMedium: Long = 0L,
    val bestTimeHard: Long = 0L,
    val bestTimeExpert: Long = 0L,
    val passwordHash: String = "",
    val securityQuestion: String = "",
    val securityAnswer: String = "",
    val isLoggedIn: Boolean = false,
    val linkedInUrl: String = "",
    val facebookUrl: String = "",
    val instagramUrl: String = "",
    val countryName: String = "United States",
    val countryFlag: String = "🇺🇸",
    val phoneNumber: String = "",
    val certificatePassword: String = "",
    val profilePhotoPath: String = ""
)

@Entity(tableName = "reward_transaction")
data class RewardTransactionEntity(
    @PrimaryKey(autoGenerate = true) val transactionId: Int = 0,
    val giftCardTitle: String,       // e.g. "$10 Google Play Gift Card"
    val pointsCost: Int,             // e.g. 10000
    val timestamp: Long = System.currentTimeMillis(),
    val status: String,              // e.g. "Pending Verification", "Active", "Completed", "Processing"
    val secureCode: String,          // e.g. "GPLA-YMSB-STUD-9182" or encrypted
    val orderNumber: String          // e.g. "MSB-PLAY-4917-23A"
)

@Entity(tableName = "leaderboard_player")
data class LeaderboardPlayerEntity(
    @PrimaryKey val username: String,
    val rank: Int,
    val points: Int,                // Competitive rating points
    val region: String,
    val avatarColorSeed: Int = 0,   // Customize avatars in Compose
    val activeMultiplier: Double = 1.0,
    val isCurrentUser: Boolean = false
)

@Entity(tableName = "game_history")
data class GameHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val difficulty: String,
    val timeElapsedSeconds: Long,
    val mistakeCount: Int,
    val xpGained: Int,
    val pgpGained: Int,
    val status: String,             // "WON", "LOST"
    val timestamp: Long = System.currentTimeMillis()
)
