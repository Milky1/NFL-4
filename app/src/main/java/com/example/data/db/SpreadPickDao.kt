package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SpreadPick
import kotlinx.coroutines.flow.Flow

@Dao
interface SpreadPickDao {
    @Query("SELECT * FROM spread_picks WHERE weekNumber = :weekNumber")
    fun getPicksForWeek(weekNumber: Int): Flow<List<SpreadPick>>

    @Query("SELECT * FROM spread_picks")
    fun getAllPicks(): Flow<List<SpreadPick>>

    @Query("SELECT * FROM spread_picks WHERE playerId = :playerId")
    fun getPicksForPlayer(playerId: Long): Flow<List<SpreadPick>>

    @Query("SELECT * FROM spread_picks WHERE gameId = :gameId AND playerId = :playerId")
    suspend fun getPick(gameId: Long, playerId: Long): SpreadPick?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(pick: SpreadPick): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(picks: List<SpreadPick>)

    @Query("DELETE FROM spread_picks WHERE gameId = :gameId AND playerId = :playerId")
    suspend fun deletePick(gameId: Long, playerId: Long)

    @Query("DELETE FROM spread_picks WHERE gameId = :gameId")
    suspend fun deletePicksForGame(gameId: Long)

    @Query("DELETE FROM spread_picks")
    suspend fun deleteAllPicks()
}
