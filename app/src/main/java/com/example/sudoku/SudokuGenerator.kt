package com.example.sudoku

import kotlin.random.Random

enum class SudokuDifficulty(val label: String, val cluesCount: Int) {
    EASY("Easy", 46),
    MEDIUM("Medium", 36),
    HARD("Hard", 28),
    EXPERT("Expert", 20)
}

object SudokuGenerator {

    /**
     * Generates a new Sudoku puzzle.
     * Returns a Pair of:
     * 1. The starting puzzle (9x9 or 4x4 grid, where 0 represents empty cell)
     * 2. The full correct solution (9x9 or 4x4 grid)
     */
    fun generate(difficulty: SudokuDifficulty, size: Int = 9): Pair<Array<IntArray>, Array<IntArray>> {
        if (size == 4) {
            val solution = Array(4) { IntArray(4) }
            fillGrid4x4(solution)

            val puzzle = Array(4) { r -> solution[r].clone() }

            val cluesCount = when (difficulty) {
                SudokuDifficulty.EASY -> 10
                SudokuDifficulty.MEDIUM -> 8
                SudokuDifficulty.HARD -> 6
                SudokuDifficulty.EXPERT -> 4
            }
            val cellsToRemove = 16 - cluesCount

            val random = Random.Default
            val cellPositions = (0..15).shuffled(random).toMutableList()

            var removedCount = 0
            for (pos in cellPositions) {
                if (removedCount >= cellsToRemove) break
                val r = pos / 4
                val c = pos % 4
                puzzle[r][c] = 0
                removedCount++
            }

            return Pair(puzzle, solution)
        }

        // Step 1: Create a fully valid solved 9x9 grid
        val solution = Array(9) { IntArray(9) }
        fillGrid(solution)

        // Step 2: Clone the solved grid to make the puzzle
        val puzzle = Array(9) { r -> solution[r].clone() }

        // Step 3: Remove cell digits to match the desired clue count
        val cellsToKeep = difficulty.cluesCount
        val cellsToRemove = 81 - cellsToKeep

        val random = Random.Default
        val cellPositions = (0..80).shuffled(random).toMutableList()

        var removedCount = 0
        for (pos in cellPositions) {
            if (removedCount >= cellsToRemove) break
            val r = pos / 9
            val c = pos % 9

            val backup = puzzle[r][c]
            puzzle[r][c] = 0

            // Optionally check if puzzle still has a unique solution.
            // For a mobile offline generator, a simple check or straight deletion is standard,
            // but let's verify if there is at least one solution (always yes, since we built it from one).
            removedCount++
        }

        return Pair(puzzle, solution)
    }

    private fun fillGrid4x4(grid: Array<IntArray>): Boolean {
        for (row in 0..3) {
            for (col in 0..3) {
                if (grid[row][col] == 0) {
                    val numbers = (1..4).shuffled()
                    for (num in numbers) {
                        if (isValidPlacement4x4(grid, row, col, num)) {
                            grid[row][col] = num
                            if (fillGrid4x4(grid)) {
                                return true
                            }
                            grid[row][col] = 0
                        }
                    }
                    return false // Backtrack
                }
            }
        }
        return true
    }

    private fun isValidPlacement4x4(grid: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        for (c in 0..3) {
            if (grid[row][c] == num) return false
        }
        for (r in 0..3) {
            if (grid[r][col] == num) return false
        }
        val boxRowStart = (row / 2) * 2
        val boxColStart = (col / 2) * 2
        for (r in boxRowStart until boxRowStart + 2) {
            for (c in boxColStart until boxColStart + 2) {
                if (grid[r][c] == num) return false
            }
        }
        return true
    }

    private fun fillGrid(grid: Array<IntArray>): Boolean {
        for (row in 0..8) {
            for (col in 0..8) {
                if (grid[row][col] == 0) {
                    val numbers = (1..9).shuffled()
                    for (num in numbers) {
                        if (isValidPlacement(grid, row, col, num)) {
                            grid[row][col] = num
                            if (fillGrid(grid)) {
                                return true
                            }
                            grid[row][col] = 0
                        }
                    }
                    return false // Backtrack
                }
            }
        }
        return true // Grid complete
    }

    private fun isValidPlacement(grid: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        // Check row
        for (c in 0..8) {
            if (grid[row][c] == num) return false
        }
        // Check col
        for (r in 0..8) {
            if (grid[r][col] == num) return false
        }
        // Check 3x3 square
        val boxRowStart = (row / 3) * 3
        val boxColStart = (col / 3) * 3
        for (r in boxRowStart until boxRowStart + 3) {
            for (c in boxColStart until boxColStart + 3) {
                if (grid[r][c] == num) return false
            }
        }
        return true
    }

    /**
     * Solves a given grid. Modifies grid in-place and returns true if solvable.
     */
    fun solve(grid: Array<IntArray>): Boolean {
        for (row in 0..8) {
            for (col in 0..8) {
                if (grid[row][col] == 0) {
                    for (num in 1..9) {
                        if (isValidPlacement(grid, row, col, num)) {
                            grid[row][col] = num
                            if (solve(grid)) return true
                            grid[row][col] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }
}
