package com.example

import com.example.data.model.AtsResult
import com.example.data.model.NflGame
import com.example.data.model.Player
import com.example.data.model.SpreadPick
import com.example.data.stats.StatsCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testAtsCalculation_homeCovers() {
        // Chiefs -3.5 vs Ravens. Chiefs win 27-20 (Margin = +7 > 3.5)
        val game = NflGame(
            id = 1L,
            weekNumber = 1,
            awayTeam = "Baltimore Ravens",
            awayTeamShort = "BAL",
            homeTeam = "Kansas City Chiefs",
            homeTeamShort = "KC",
            homeSpread = -3.5,
            awayScore = 20,
            homeScore = 27,
            isFinal = true
        )
        assertEquals(AtsResult.HOME_COVERED, game.getAtsResult())
        assertEquals("KC", game.getCoveringTeamShort())
    }

    @Test
    fun testAtsCalculation_awayCovers() {
        // Chiefs -3.5 vs Ravens. Chiefs win 23-20 (Margin = +3 < 3.5 -> Ravens cover +3.5)
        val game = NflGame(
            id = 2L,
            weekNumber = 1,
            awayTeam = "Baltimore Ravens",
            awayTeamShort = "BAL",
            homeTeam = "Kansas City Chiefs",
            homeTeamShort = "KC",
            homeSpread = -3.5,
            awayScore = 20,
            homeScore = 23,
            isFinal = true
        )
        assertEquals(AtsResult.AWAY_COVERED, game.getAtsResult())
        assertEquals("BAL", game.getCoveringTeamShort())
    }

    @Test
    fun testAtsCalculation_push() {
        // Chiefs -3.0 vs Ravens. Chiefs win 23-20 (Margin = +3 == 3.0 -> Push)
        val game = NflGame(
            id = 3L,
            weekNumber = 1,
            awayTeam = "Baltimore Ravens",
            awayTeamShort = "BAL",
            homeTeam = "Kansas City Chiefs",
            homeTeamShort = "KC",
            homeSpread = -3.0,
            awayScore = 20,
            homeScore = 23,
            isFinal = true
        )
        assertEquals(AtsResult.PUSH, game.getAtsResult())
        assertEquals("PUSH", game.getCoveringTeamShort())
    }

    @Test
    fun testStatsCalculator_weeklyWinner() {
        val p1 = Player(id = 1L, name = "Clive")
        val p2 = Player(id = 2L, name = "Alex")

        val game = NflGame(
            id = 1L,
            weekNumber = 1,
            awayTeam = "BAL",
            awayTeamShort = "BAL",
            homeTeam = "KC",
            homeTeamShort = "KC",
            homeSpread = -3.5,
            awayScore = 20,
            homeScore = 27,
            isFinal = true
        )

        val pick1 = SpreadPick(id = 1L, gameId = 1L, playerId = 1L, weekNumber = 1, pickedTeamShort = "KC")
        val pick2 = SpreadPick(id = 2L, gameId = 1L, playerId = 2L, weekNumber = 1, pickedTeamShort = "BAL")

        val summary = StatsCalculator.calculateWeeklySummary(
            weekNumber = 1,
            games = listOf(game),
            players = listOf(p1, p2),
            picks = listOf(pick1, pick2)
        )

        assertEquals(1, summary.winners.size)
        assertEquals("Clive", summary.winners.first().name)
    }

    @Test
    fun testAreAllPlayersWrong_whenAllThreeLose() {
        // Tampa Bay @ Kansas City (KC -9.0 line). Actual score: KC 30, TB 24 (+6 margin < 9.0) -> TB covered (+9.0)
        val game = NflGame(
            id = 90L,
            weekNumber = 9,
            awayTeam = "Tampa Bay Buccaneers",
            awayTeamShort = "TB",
            homeTeam = "Kansas City Chiefs",
            homeTeamShort = "KC",
            homeSpread = -9.0,
            awayScore = 24,
            homeScore = 30,
            isFinal = true
        )
        val clive = Player(id = 1L, name = "Clive")
        val mark = Player(id = 2L, name = "Mark")
        val conor = Player(id = 3L, name = "Conor")
        val players = listOf(clive, mark, conor)

        // All 3 players backed Chiefs -9.0
        val picks = listOf(
            SpreadPick(id = 1L, gameId = 90L, playerId = 1L, weekNumber = 9, pickedTeamShort = "KC"),
            SpreadPick(id = 2L, gameId = 90L, playerId = 2L, weekNumber = 9, pickedTeamShort = "KC"),
            SpreadPick(id = 3L, gameId = 90L, playerId = 3L, weekNumber = 9, pickedTeamShort = "KC")
        )

        // All 3 players are WRONG
        assertEquals(true, StatsCalculator.areAllPlayersWrong(game, players, picks))
        assertEquals(0, StatsCalculator.getWinningPlayers(game, players, picks).size)
    }

    @Test
    fun testAreAllPlayersWrong_whenSomeWin() {
        // Denver @ Baltimore (BAL -9.0 line). Actual score: BAL 41, DEN 10 (+31 margin > 9.0) -> BAL covered (-9.0)
        val game = NflGame(
            id = 91L,
            weekNumber = 9,
            awayTeam = "Denver Broncos",
            awayTeamShort = "DEN",
            homeTeam = "Baltimore Ravens",
            homeTeamShort = "BAL",
            homeSpread = -9.0,
            awayScore = 10,
            homeScore = 41,
            isFinal = true
        )
        val clive = Player(id = 1L, name = "Clive")
        val mark = Player(id = 2L, name = "Mark")
        val conor = Player(id = 3L, name = "Conor")
        val players = listOf(clive, mark, conor)

        // Clive and Mark backed BAL (covers!), Conor backed DEN (loses)
        val picks = listOf(
            SpreadPick(id = 1L, gameId = 91L, playerId = 1L, weekNumber = 9, pickedTeamShort = "BAL"),
            SpreadPick(id = 2L, gameId = 91L, playerId = 2L, weekNumber = 9, pickedTeamShort = "BAL"),
            SpreadPick(id = 3L, gameId = 91L, playerId = 3L, weekNumber = 9, pickedTeamShort = "DEN")
        )

        assertEquals(false, StatsCalculator.areAllPlayersWrong(game, players, picks))
        val winners = StatsCalculator.getWinningPlayers(game, players, picks)
        assertEquals(2, winners.size)
        assertEquals("Clive", winners[0].name)
        assertEquals("Mark", winners[1].name)
    }

    @Test
    fun testOnlyFavoriteSpreadShown_awayFavorite() {
        // Jax @ Baltimore, Jax favored by 10.0 (homeSpread = 10.0)
        val game = NflGame(
            id = 101L,
            weekNumber = 9,
            awayTeam = "Jacksonville Jaguars",
            awayTeamShort = "JAX",
            homeTeam = "Baltimore Ravens",
            homeTeamShort = "BAL",
            homeSpread = 10.0,
            awayScore = null,
            homeScore = null,
            isFinal = false
        )
        // Only JAX should display "-10", Baltimore should have empty string
        assertEquals("-10", game.formattedAwaySpread())
        assertEquals("", game.formattedHomeSpread())
        assertEquals("JAX -10", game.lineSummary)
    }

    @Test
    fun testOnlyFavoriteSpreadShown_homeFavorite() {
        // NE @ Seattle, Seattle favored by 3.5 (homeSpread = -3.5)
        val game = NflGame(
            id = 102L,
            weekNumber = 1,
            awayTeam = "New England Patriots",
            awayTeamShort = "NE",
            homeTeam = "Seattle Seahawks",
            homeTeamShort = "SEA",
            homeSpread = -3.5,
            awayScore = null,
            homeScore = null,
            isFinal = false
        )
        // Only Seattle should display "-3.5", NE should have empty string
        assertEquals("", game.formattedAwaySpread())
        assertEquals("-3.5", game.formattedHomeSpread())
        assertEquals("SEA -3.5", game.lineSummary)
    }
}
