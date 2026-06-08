package com.example.data

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
    version = 4,
    exportSchema = false
)
abstract class SudokuDatabase : RoomDatabase() {

    abstract fun sudokuDao(): SudokuDao

    companion object {
        @Volatile
        private var INSTANCE: SudokuDatabase? = null

        fun getDatabase(context: Context): SudokuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
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
