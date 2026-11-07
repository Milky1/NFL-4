package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.NflGame
import kotlinx.coroutines.flow.Flow

@Dao
interface NflGameDao {
    @Query("SELECT * FROM nfl_games WHERE weekNumber = :weekNumber ORDER BY id ASC")
    fun getGamesForWeek(weekNumber: Int): Flow<List<NflGame>>

    @Query("SELECT * FROM nfl_games ORDER BY weekNumber ASC, id ASC")
    fun getAllGames(): Flow<List<NflGame>>

    @Query("SELECT DISTINCT weekNumber FROM nfl_games ORDER BY weekNumber ASC")
    fun getAllWeekNumbers(): Flow<List<Int>>

    @Query("SELECT * FROM nfl_games WHERE id = :id")
    suspend fun getGameById(id: Long): NflGame?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(game: NflGame): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(games: List<NflGame>): List<Long>

    @Update
    suspend fun update(game: NflGame)

    @Query("UPDATE nfl_games SET awayScore = :awayScore, homeScore = :homeScore, isFinal = :isFinal WHERE id = :gameId")
    suspend fun updateScore(gameId: Long, awayScore: Int?, homeScore: Int?, isFinal: Boolean)

    @Query("UPDATE nfl_games SET homeSpread = :newSpread WHERE id = :gameId")
    suspend fun updateSpread(gameId: Long, newSpread: Double)

    @Query("UPDATE nfl_games SET homeSpread = :newSpread, sportsbookProvider = :sportsbook WHERE id = :gameId")
    suspend fun updateSpreadWithSportsbook(gameId: Long, newSpread: Double, sportsbook: String)

    @Query("UPDATE nfl_games SET gameDate = :gameDate, kickoffTime = :kickoffTime, tvNetwork = :tvNetwork, venue = :venue WHERE id = :gameId")
    suspend fun updateFixtureDetails(gameId: Long, gameDate: String, kickoffTime: String, tvNetwork: String, venue: String)

    @Query("DELETE FROM nfl_games WHERE id = :id")
    suspend fun deleteGame(id: Long)

    @Query("SELECT COUNT(*) FROM nfl_games")
    suspend fun getGameCount(): Int

    @Query("DELETE FROM nfl_games")
    suspend fun deleteAllGames()
}
