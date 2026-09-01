package com.msbcreativestudios.sudokuchallenge.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ActiveGameEntity::class,
        UserProfileEntity::class,
        RewardTransactionEntity::class,
        LeaderboardPlayerEntity::class,
        GameHistoryEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class SudokuDatabase : RoomDatabase() {

    abstract fun sudokuDao(): SudokuDao

    companion object {
        @Volatile
        private var INSTANCE: SudokuDatabase? = null

        fun getDatabase(context: Context): SudokuDatabase {
            return INSTANCE ?: synchronized(this) {
                val appContext = context.applicationContext
                val instance = Room.databaseBuilder(
                    appContext,
                    SudokuDatabase::class.java,
                    "sudoku_global_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
