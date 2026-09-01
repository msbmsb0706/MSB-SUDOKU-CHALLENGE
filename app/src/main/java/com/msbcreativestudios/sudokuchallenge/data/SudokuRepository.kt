package com.msbcreativestudios.sudokuchallenge.data

import kotlinx.coroutines.flow.Flow

class SudokuRepository(private val sudokuDao: SudokuDao) {

    val activeGame: Flow<ActiveGameEntity?> = sudokuDao.getActiveGame()
    val userProfile: Flow<UserProfileEntity?> = sudokuDao.getUserProfile()
    val allTransactions: Flow<List<RewardTransactionEntity>> = sudokuDao.getAllTransactions()
    val allLeaderboard: Flow<List<LeaderboardPlayerEntity>> = sudokuDao.getAllLeaderboardPlayers()

    fun getGameHistory(userId: String): Flow<List<GameHistoryEntity>> {
        return sudokuDao.getGameHistory(userId)
    }

    suspend fun getUserProfileByEmail(email: String): UserProfileEntity? {
        return sudokuDao.getUserProfileByEmail(email)
    }

    suspend fun logOutAll() {
        sudokuDao.logOutAll()
    }

    suspend fun insertGameHistory(history: GameHistoryEntity) {
        sudokuDao.insertGameHistory(history)
    }

    fun getLeaderboardByRegion(region: String): Flow<List<LeaderboardPlayerEntity>> {
        return if (region == "Global" || region.isBlank()) {
            sudokuDao.getAllLeaderboardPlayers()
        } else {
            sudokuDao.getLeaderboardPlayersByRegion(region)
        }
    }

    suspend fun saveActiveGame(game: ActiveGameEntity) {
        sudokuDao.insertActiveGame(game)
    }

    suspend fun clearActiveGame() {
        sudokuDao.deleteActiveGame()
    }

    suspend fun saveUserProfile(profile: UserProfileEntity) {
        sudokuDao.insertUserProfile(profile)
    }

    suspend fun addRewardTransaction(tx: RewardTransactionEntity) {
        sudokuDao.insertTransaction(tx)
    }

    suspend fun setupLeaderboardPlayers(players: List<LeaderboardPlayerEntity>) {
        sudokuDao.insertLeaderboardPlayers(players)
    }

    suspend fun replaceLeaderboardCache(players: List<LeaderboardPlayerEntity>) {
        sudokuDao.clearLeaderboardPlayers()
        sudokuDao.insertLeaderboardPlayers(players)
    }
}
