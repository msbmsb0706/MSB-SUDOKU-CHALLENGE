package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SudokuDao {

    // --- Active Game ---
    @Query("SELECT * FROM active_game WHERE id = 1 LIMIT 1")
    fun getActiveGame(): Flow<ActiveGameEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActiveGame(game: ActiveGameEntity)

    @Query("DELETE FROM active_game WHERE id = 1")
    suspend fun deleteActiveGame()


    // --- User Profile ---
    @Query("SELECT * FROM user_profile WHERE isLoggedIn = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE userId = :email LIMIT 1")
    suspend fun getUserProfileByEmail(email: String): UserProfileEntity?

    @Query("UPDATE user_profile SET isLoggedIn = 0")
    suspend fun logOutAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)


    // --- Game History / Run records ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameHistory(history: GameHistoryEntity)

    @Query("SELECT * FROM game_history WHERE userId = :userId ORDER BY timestamp DESC")
    fun getGameHistory(userId: String): Flow<List<GameHistoryEntity>>


    // --- Reward Transactions ---
    @Query("SELECT * FROM reward_transaction ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<RewardTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: RewardTransactionEntity)


    // --- Leaderboard Players ---
    @Query("SELECT * FROM leaderboard_player ORDER BY points DESC, rank ASC")
    fun getAllLeaderboardPlayers(): Flow<List<LeaderboardPlayerEntity>>

    @Query("SELECT * FROM leaderboard_player WHERE region = :region ORDER BY points DESC, rank ASC")
    fun getLeaderboardPlayersByRegion(region: String): Flow<List<LeaderboardPlayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardPlayers(players: List<LeaderboardPlayerEntity>)

    @Query("DELETE FROM leaderboard_player")
    suspend fun clearLeaderboardPlayers()
}
