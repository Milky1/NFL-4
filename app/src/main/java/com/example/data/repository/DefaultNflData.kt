package com.example.data.repository

import com.example.data.model.NflGame
import com.example.data.model.Player
import com.example.data.model.SpreadPick

object DefaultNflData {
    val initialPlayers = listOf(
        Player(id = 1L, name = "Clive", avatarColorHex = 0xFF00E676, avatarEmoji = "C", orderIndex = 0),
        Player(id = 2L, name = "Mark", avatarColorHex = 0xFFFFB300, avatarEmoji = "M", orderIndex = 1),
        Player(id = 3L, name = "Conor", avatarColorHex = 0xFF38BDF8, avatarEmoji = "CO", orderIndex = 2)
    )

    fun getSampleGames(): List<NflGame> {
        val games = mutableListOf<NflGame>()
        var idCounter = 1L

        // Helper to add game
        fun addGame(
            week: Int,
            awayShort: String,
            awayName: String,
            homeShort: String,
            homeName: String,
            homeSpread: Double,
            kickoff: String,
            date: String,
            network: String,
            venue: String,
            awayScore: Int? = null,
            homeScore: Int? = null,
            isFinal: Boolean = false
        ) {
            games.add(
                NflGame(
                    id = idCounter++,
                    weekNumber = week,
                    seasonYear = 2026,
                    awayTeam = awayName,
                    awayTeamShort = awayShort,
                    homeTeam = homeName,
                    homeTeamShort = homeShort,
                    homeSpread = homeSpread,
                    kickoffTime = kickoff,
                    gameDate = date,
                    tvNetwork = network,
                    venue = venue,
                    sportsbookProvider = "DraftKings",
                    awayScore = awayScore,
                    homeScore = homeScore,
                    isFinal = isFinal
                )
            )
        }

        // ==================== WEEK 1 (16 Games - All Final) ====================
        // Spread target: Clive 8, Mark 9, Conor 9 (Overall tie)
        // 1. New England Patriots @ Seattle Seahawks -3.5
        addGame(1, "NE", "New England Patriots", "SEA", "Seattle Seahawks", -3.5, "1:00 PM GMT", "Sun, Sep 13", "CBS", "Lumen Field, Seattle", 10, 13, true)
        // 2. San Francisco 49ers @ Los Angeles Rams -3.5
        addGame(1, "SF", "San Francisco 49ers", "LAR", "Los Angeles Rams", -3.5, "4:25 PM GMT", "Sun, Sep 13", "FOX", "SoFi Stadium, Inglewood", 27, 7, true)
        // 3. Atlanta Falcons @ Pittsburgh Steelers -5.5
        addGame(1, "ATL", "Atlanta Falcons", "PIT", "Pittsburgh Steelers", -5.5, "1:00 PM GMT", "Sun, Sep 13", "FOX", "Acrisure Stadium, Pittsburgh", 13, 20, true)
        // 4. Baltimore Ravens -3.5 @ Indianapolis Colts
        addGame(1, "BAL", "Baltimore Ravens", "IND", "Indianapolis Colts", 3.5, "1:00 PM GMT", "Sun, Sep 13", "CBS", "Lucas Oil Stadium, Indianapolis", 24, 20, true)
        // 5. Buffalo Bills -1.5 @ Houston Texans
        addGame(1, "BUF", "Buffalo Bills", "HOU", "Houston Texans", 1.5, "1:00 PM GMT", "Sun, Sep 13", "CBS", "NRG Stadium, Houston", 23, 20, true)
        // 6. Chicago Bears -3 @ Carolina Panthers
        addGame(1, "CHI", "Chicago Bears", "CAR", "Carolina Panthers", 3.0, "1:00 PM GMT", "Sun, Sep 13", "FOX", "Bank of America Stadium, Charlotte", 24, 17, true)
        // 7. Cleveland Browns @ Jacksonville Jaguars -8.5
        addGame(1, "CLE", "Cleveland Browns", "JAX", "Jacksonville Jaguars", -8.5, "1:00 PM GMT", "Sun, Sep 13", "CBS", "EverBank Stadium, Jacksonville", 17, 27, true)
        // 8. New Orleans Saints @ Detroit Lions -6.5
        addGame(1, "NO", "New Orleans Saints", "DGMT", "Detroit Lions", -6.5, "1:00 PM GMT", "Sun, Sep 13", "FOX", "Ford Field, Detroit", 20, 31, true)
        // 9. New York Jets @ Tennessee Titans -1.5
        addGame(1, "NYJ", "New York Jets", "TEN", "Tennessee Titans", -1.5, "1:00 PM GMT", "Sun, Sep 13", "CBS", "Nissan Stadium, Nashville", 24, 17, true)
        // 10. Tampa Bay Buccaneers @ Cincinnati Bengals -3.5
        addGame(1, "TB", "Tampa Bay Buccaneers", "CIN", "Cincinnati Bengals", -3.5, "1:00 PM GMT", "Sun, Sep 13", "FOX", "Paycor Stadium, Cincinnati", 20, 27, true)
        // 11. Arizona Cardinals @ Los Angeles Chargers -9.5
        addGame(1, "ARI", "Arizona Cardinals", "LAC", "Los Angeles Chargers", -9.5, "4:05 PM GMT", "Sun, Sep 13", "CBS", "SoFi Stadium, Inglewood", 14, 28, true)
        // 12. Green Bay Packers @ Minnesota Vikings -1.5
        addGame(1, "GB", "Green Bay Packers", "MIN", "Minnesota Vikings", -1.5, "4:25 PM GMT", "Sun, Sep 13", "FOX", "U.S. Bank Stadium, Minneapolis", 27, 23, true)
        // 13. Miami Dolphins @ Las Vegas Raiders -3
        addGame(1, "MIA", "Miami Dolphins", "LV", "Las Vegas Raiders", -3.0, "4:05 PM GMT", "Sun, Sep 13", "CBS", "Allegiant Stadium, Las Vegas", 21, 20, true)
        // 14. Washington Commanders @ Philadelphia Eagles -5.5
        addGame(1, "WAS", "Washington Commanders", "PHI", "Philadelphia Eagles", -5.5, "1:00 PM GMT", "Sun, Sep 13", "FOX", "Lincoln Financial Field, Philadelphia", 17, 30, true)
        // 15. Dallas Cowboys -3 @ New York Giants
        addGame(1, "DAL", "Dallas Cowboys", "NYG", "New York Giants", 3.0, "8:20 PM GMT", "Sun, Sep 13", "NBC", "MetLife Stadium, East Rutherford", 28, 14, true)
        // 16. Denver Broncos @ Kansas City Chiefs -2.5
        addGame(1, "DEN", "Denver Broncos", "KC", "Kansas City Chiefs", -2.5, "8:15 PM GMT", "Mon, Sep 14", "ESPN", "GEHA Field at Arrowhead Stadium, Kansas City", 16, 23, true)

        // ==================== WEEK 2 (16 Games - All Final) ====================
        // Spread target: Clive 9, Mark 8, Conor 5 (+€20 to Clive)
        // 1. Detroit Lions @ Buffalo Bills -4.5 (Picks: Clive=Bills, Mark=Bills, Conor=Lions)
        addGame(2, "DGMT", "Detroit Lions", "BUF", "Buffalo Bills", -4.5, "8:15 PM GMT", "Thu, Sep 17", "Prime Video", "Highmark Stadium, Orchard Park", 20, 24, true)
        // 2. Carolina Panthers -2.5 @ Atlanta Falcons (Picks: Clive=falcons, Mark=Panthers, Conor=Panthers)
        addGame(2, "CAR", "Carolina Panthers", "ATL", "Atlanta Falcons", 2.5, "1:00 PM GMT", "Sun, Sep 20", "FOX", "Mercedes-Benz Stadium, Atlanta", 23, 20, true)
        // 3. Cincinnati Bengals @ Houston Texans -2.5 (Picks: Clive=bengals, Mark=Texans, Conor=Bengals)
        addGame(2, "CIN", "Cincinnati Bengals", "HOU", "Houston Texans", -2.5, "1:00 PM GMT", "Sun, Sep 20", "CBS", "NRG Stadium, Houston", 24, 20, true)
        // 4. Cleveland Browns @ Tampa Bay Buccaneers -8.5 (Picks: Clive=browns, Mark=Bucs, Conor=Bucs)
        addGame(2, "CLE", "Cleveland Browns", "TB", "Tampa Bay Buccaneers", -8.5, "1:00 PM GMT", "Sun, Sep 20", "FOX", "Raymond James Stadium, Tampa", 14, 20, true)
        // 5. Green Bay Packers -3.5 @ New York Jets (Picks: Clive=jets, Mark=Jets, Conor=Packers)
        addGame(2, "GB", "Green Bay Packers", "NYJ", "New York Jets", 3.5, "1:00 PM GMT", "Sun, Sep 20", "FOX", "MetLife Stadium, East Rutherford", 20, 21, true)
        // 6. Minnesota Vikings @ Chicago Bears -4.5 (Picks: Clive=bears, Mark=Bears, Conor=Bears)
        addGame(2, "MIN", "Minnesota Vikings", "CHI", "Chicago Bears", -4.5, "1:00 PM GMT", "Sun, Sep 20", "FOX", "Soldier Field, Chicago", 27, 20, true)
        // 7. New Orleans Saints @ Baltimore Ravens -7.5 (Picks: Clive=ravens, Mark=Ravens, Conor=Ravens)
        addGame(2, "NO", "New Orleans Saints", "BAL", "Baltimore Ravens", -7.5, "1:00 PM GMT", "Sun, Sep 20", "CBS", "M&T Bank Stadium, Baltimore", 21, 24, true)
        // 8. Philadelphia Eagles -7 @ Tennessee Titans (Picks: Clive=eagles, Mark=Titans, Conor=Eagles)
        addGame(2, "PHI", "Philadelphia Eagles", "TEN", "Tennessee Titans", 7.0, "1:00 PM GMT", "Sun, Sep 20", "FOX", "Nissan Stadium, Nashville", 28, 17, true)
        // 9. Pittsburgh Steelers @ New England Patriots -5.5 (Picks: Clive=steelers, Mark=Steelers, Conor=Patriots)
        addGame(2, "PIT", "Pittsburgh Steelers", "NE", "New England Patriots", -5.5, "1:00 PM GMT", "Sun, Sep 20", "CBS", "Gillette Stadium, Foxborough", 20, 17, true)
        // 10. Jacksonville Jaguars @ Denver Broncos -2.5 (Picks: Clive=broncos, Mark=Broncos, Conor=Jags)
        addGame(2, "JAX", "Jacksonville Jaguars", "DEN", "Denver Broncos", -2.5, "4:05 PM GMT", "Sun, Sep 20", "CBS", "Empower Field at Mile High, Denver", 17, 23, true)
        // 11. Las Vegas Raiders @ Los Angeles Chargers -6.5 (Picks: Clive=chargers, Mark=Raiders, Conor=Chargers)
        addGame(2, "LV", "Las Vegas Raiders", "LAC", "Los Angeles Chargers", -6.5, "4:05 PM GMT", "Sun, Sep 20", "CBS", "SoFi Stadium, Inglewood", 20, 24, true)
        // 12. Miami Dolphins @ San Francisco 49ers -12.5 (Picks: Clive=49ers, Mark=Dolphins, Conor=49ers)
        addGame(2, "MIA", "Miami Dolphins", "SF", "San Francisco 49ers", -12.5, "4:25 PM GMT", "Sun, Sep 20", "FOX", "Levi's Stadium, Santa Clara", 20, 30, true)
        // 13. Seattle Seahawks -3.5 @ Arizona Cardinals (Picks: Clive=seahawks, Mark=Seahawks, Conor=Seahawks)
        addGame(2, "SEA", "Seattle Seahawks", "ARI", "Arizona Cardinals", 3.5, "4:25 PM GMT", "Sun, Sep 20", "FOX", "State Farm Stadium, Glendale", 27, 20, true)
        // 14. Washington Commanders @ Dallas Cowboys -3.5 (Picks: Clive=cowboys, Mark=Team, Conor=Football Team)
        addGame(2, "WAS", "Washington Commanders", "DAL", "Dallas Cowboys", -3.5, "4:25 PM GMT", "Sun, Sep 20", "CBS", "AT&T Stadium, Arlington", 17, 24, true)
        // 15. Indianapolis Colts @ Kansas City Chiefs -6.5 (Picks: Clive=chiefs, Mark=Chiefs, Conor=Chiefs)
        addGame(2, "IND", "Indianapolis Colts", "KC", "Kansas City Chiefs", -6.5, "8:20 PM GMT", "Sun, Sep 20", "NBC", "GEHA Field at Arrowhead Stadium, Kansas City", 20, 23, true)
        // 16. New York Giants @ Los Angeles Rams -7 (Picks: Clive=rams, Mark=Rams, Conor=Giants)
        addGame(2, "NYG", "New York Giants", "LAR", "Los Angeles Rams", -7.0, "8:15 PM GMT", "Mon, Sep 21", "ESPN", "SoFi Stadium, Inglewood", 14, 24, true)

        // ==================== WEEK 3 (16 Games - All Final) ====================
        // Spread target: Clive 7, Mark 11, Conor 4 (Mark +€10, Clive -€5, Conor -€5)
        // 1. Atlanta Falcons @ Green Bay Packers -5.5 (Picks: Clive=Falcons, Mark=Falcons, Conor=Packers)
        addGame(3, "ATL", "Atlanta Falcons", "GB", "Green Bay Packers", -5.5, "1:00 PM GMT", "Sun, Sep 27", "FOX", "Lambeau Field, Green Bay", 21, 24, true)
        // 2. Carolina Panthers -2.5 @ Cleveland Browns (Picks: Clive=panthers, Mark=Panthers, Conor=Panthers)
        addGame(3, "CAR", "Carolina Panthers", "CLE", "Cleveland Browns", 2.5, "1:00 PM GMT", "Sun, Sep 27", "CBS", "Huntington Bank Field, Cleveland", 17, 20, true)
        // 3. Cincinnati Bengals -3.5 @ Pittsburgh Steelers (Picks: Clive=steelers, Mark=Bengals, Conor=Bengals)
        addGame(3, "CIN", "Cincinnati Bengals", "PIT", "Pittsburgh Steelers", 3.5, "1:00 PM GMT", "Sun, Sep 27", "CBS", "Acrisure Stadium, Pittsburgh", 20, 23, true)
        // 4. Houston Texans -1.5 @ Indianapolis Colts (Picks: Clive=texans, Mark=Colts, Conor=Texans)
        addGame(3, "HOU", "Houston Texans", "IND", "Indianapolis Colts", 1.5, "1:00 PM GMT", "Sun, Sep 27", "CBS", "Lucas Oil Stadium, Indianapolis", 20, 21, true)
        // 5. Kansas City Chiefs -10.5 @ Miami Dolphins (Picks: Clive=dolphins, Mark=Chiefs, Conor=Chiefs)
        addGame(3, "KC", "Kansas City Chiefs", "MIA", "Miami Dolphins", 10.5, "1:00 PM GMT", "Sun, Sep 27", "CBS", "Hard Rock Stadium, Miami Gardens", 27, 20, true)
        // 6. Los Angeles Chargers @ Buffalo Bills -7.5 (Picks: Clive=bills, Mark=Bills, Conor=Bills)
        addGame(3, "LAC", "Los Angeles Chargers", "BUF", "Buffalo Bills", -7.5, "1:00 PM GMT", "Sun, Sep 27", "CBS", "Highmark Stadium, Orchard Park", 13, 27, true)
        // 7. New England Patriots @ Jacksonville Jaguars -2.5 (Picks: Clive=jags, Mark=Pats, Conor=Pats)
        addGame(3, "NE", "New England Patriots", "JAX", "Jacksonville Jaguars", -2.5, "1:00 PM GMT", "Sun, Sep 27", "CBS", "EverBank Stadium, Jacksonville", 24, 20, true)
        // 8. New York Jets @ Detroit Lions -6.5 (Picks: Clive=lions, Mark=Jets, Conor=Lions)
        addGame(3, "NYJ", "New York Jets", "DGMT", "Detroit Lions", -6.5, "1:00 PM GMT", "Sun, Sep 27", "FOX", "Ford Field, Detroit", 20, 24, true)
        // 9. Seattle Seahawks -7.5 @ Washington Commanders (Picks: Clive=seahawks, Mark=Team, Conor=Seahawks)
        addGame(3, "SEA", "Seattle Seahawks", "WAS", "Washington Commanders", 7.5, "4:05 PM GMT", "Sun, Sep 27", "FOX", "Northwest Stadium, Landover", 24, 20, true)
        // 10. Tennessee Titans @ New York Giants -2.5 (Picks: Clive=titans, Mark=Giants, Conor=Titans)
        addGame(3, "TEN", "Tennessee Titans", "NYG", "New York Giants", -2.5, "4:05 PM GMT", "Sun, Sep 27", "CBS", "MetLife Stadium, East Rutherford", 16, 23, true)
        // 11. Arizona Cardinals @ San Francisco 49ers -8.5 (Picks: Clive=49ers, Mark=Cardinals, Conor=49ers)
        addGame(3, "ARI", "Arizona Cardinals", "SF", "San Francisco 49ers", -8.5, "4:25 PM GMT", "Sun, Sep 27", "FOX", "Levi's Stadium, Santa Clara", 20, 24, true)
        // 12. Minnesota Vikings -1.5 @ Tampa Bay Buccaneers (Picks: Clive=bucs, Mark=Vikings, Conor=Vikings)
        addGame(3, "MIN", "Minnesota Vikings", "TB", "Tampa Bay Buccaneers", 1.5, "4:25 PM GMT", "Sun, Sep 27", "FOX", "Raymond James Stadium, Tampa", 20, 23, true)
        // 13. Baltimore Ravens -3.5 @ Dallas Cowboys (Picks: Clive=cowboys, Mark=Cowboys, Conor=Ravens)
        addGame(3, "BAL", "Baltimore Ravens", "DAL", "Dallas Cowboys", 3.5, "4:25 PM GMT", "Sun, Sep 27", "FOX", "AT&T Stadium, Arlington", 24, 23, true)
        // 14. Las Vegas Raiders @ New Orleans Saints -3 (Picks: Clive=saints, Mark=Raiders, Conor=Saints)
        addGame(3, "LV", "Las Vegas Raiders", "NO", "New Orleans Saints", -3.0, "8:20 PM GMT", "Sun, Sep 27", "NBC", "Caesars Superdome, New Orleans", 20, 21, true)
        // 15. Los Angeles Rams -2.5 @ Denver Broncos (Picks: Clive=broncos, Mark=Rams, Conor=Rams)
        addGame(3, "LAR", "Los Angeles Rams", "DEN", "Denver Broncos", 2.5, "8:15 PM GMT", "Mon, Sep 28", "ESPN", "Empower Field at Mile High, Denver", 24, 17, true)
        // 16. Philadelphia Eagles -3.5 @ Chicago Bears (Picks: Clive=eagles, Mark=Bears, Conor=Eagles)
        addGame(3, "PHI", "Philadelphia Eagles", "CHI", "Chicago Bears", 3.5, "8:15 PM GMT", "Mon, Sep 28", "ABC", "Soldier Field, Chicago", 27, 20, true)

        // ==================== WEEK 4 (16 Games - All Final) ====================
        // Spread target: Clive 8, Mark 10, Conor 7 (Mark +€10, Clive -€5, Conor -€5)
        // 1. Pittsburgh Steelers -2.5 @ Cleveland Browns (Picks: Clive=Browns, Mark=Steelers, Conor=Browns)
        addGame(4, "PIT", "Pittsburgh Steelers", "CLE", "Cleveland Browns", 2.5, "1:00 PM GMT", "Sun, Oct 4", "CBS", "Huntington Bank Field, Cleveland", 24, 17, true)
        // 2. Indianapolis Colts -3.5 @ Washington Commanders (Picks: Clive=Colts, Mark=Team, Conor=Football Team)
        addGame(4, "IND", "Indianapolis Colts", "WAS", "Washington Commanders", 3.5, "1:00 PM GMT", "Sun, Oct 4", "FOX", "Northwest Stadium, Landover", 21, 20, true)
        // 3. Arizona Cardinals -2.5 @ New York Giants (Picks: Clive=Giants, Mark=Giants, Conor=Cardinals)
        addGame(4, "ARI", "Arizona Cardinals", "NYG", "New York Giants", 2.5, "1:00 PM GMT", "Sun, Oct 4", "FOX", "MetLife Stadium, East Rutherford", 20, 21, true)
        // 4. Dallas Cowboys @ Houston Texans -3 (Picks: Clive=Texans, Mark=Cowboys, Conor=Cowboys)
        addGame(4, "DAL", "Dallas Cowboys", "HOU", "Houston Texans", -3.0, "1:00 PM GMT", "Sun, Oct 4", "CBS", "NRG Stadium, Houston", 17, 20, true)
        // 5. Green Bay Packers -3.5 @ Tampa Bay Buccaneers (Picks: Clive=Bucs, Mark=Bucs, Conor=Packers)
        addGame(4, "GB", "Green Bay Packers", "TB", "Tampa Bay Buccaneers", 3.5, "1:00 PM GMT", "Sun, Oct 4", "FOX", "Raymond James Stadium, Tampa", 20, 24, true)
        // 6. Jacksonville Jaguars @ Cincinnati Bengals -2.5 (Picks: Clive=Jags, Mark=Jags, Conor=Jags)
        addGame(4, "JAX", "Jacksonville Jaguars", "CIN", "Cincinnati Bengals", -2.5, "1:00 PM GMT", "Sun, Oct 4", "CBS", "Paycor Stadium, Cincinnati", 17, 24, true)
        // 7. Los Angeles Rams -3.5 @ Philadelphia Eagles (Picks: Clive=Rams, Mark=Rams, Conor=Eagles)
        addGame(4, "LAR", "Los Angeles Rams", "PHI", "Philadelphia Eagles", 3.5, "1:00 PM GMT", "Sun, Oct 4", "FOX", "Lincoln Financial Field, Philadelphia", 28, 21, true)
        // 8. New England Patriots @ Buffalo Bills -7 (Picks: Clive=Bills, Mark=Bills, Conor=Bills)
        addGame(4, "NE", "New England Patriots", "BUF", "Buffalo Bills", -7.0, "1:00 PM GMT", "Sun, Oct 4", "CBS", "Highmark Stadium, Orchard Park", 10, 24, true)
        // 9. New York Jets @ Chicago Bears -3.5 (Picks: Clive=Bears, Mark=Bears, Conor=Bears)
        addGame(4, "NYJ", "New York Jets", "CHI", "Chicago Bears", -3.5, "1:00 PM GMT", "Sun, Oct 4", "CBS", "Soldier Field, Chicago", 17, 24, true)
        // 10. Tennessee Titans @ Baltimore Ravens -11.5 (Picks: Clive=Titans, Mark=Ravens, Conor=Ravens)
        addGame(4, "TEN", "Tennessee Titans", "BAL", "Baltimore Ravens", -11.5, "1:00 PM GMT", "Sun, Oct 4", "CBS", "M&T Bank Stadium, Baltimore", 13, 28, true)
        // 11. Miami Dolphins @ Minnesota Vikings -10.5 (Picks: Clive=Vikings, Mark=Dolphins, Conor=Dolphins)
        addGame(4, "MIA", "Miami Dolphins", "MIN", "Minnesota Vikings", -10.5, "1:00 PM GMT", "Sun, Oct 4", "FOX", "U.S. Bank Stadium, Minneapolis", 14, 31, true)
        // 12. Denver Broncos @ San Francisco 49ers -3 (Picks: Clive=Broncos, Mark=49ers, Conor=Broncos)
        addGame(4, "DEN", "Denver Broncos", "SF", "San Francisco 49ers", -3.0, "4:05 PM GMT", "Sun, Oct 4", "FOX", "Levi's Stadium, Santa Clara", 17, 24, true)
        // 13. Kansas City Chiefs -4.5 @ Las Vegas Raiders (Picks: Clive=Chiefs, Mark=Chiefs, Conor=Raiders)
        addGame(4, "KC", "Kansas City Chiefs", "LV", "Las Vegas Raiders", 4.5, "4:25 PM GMT", "Sun, Oct 4", "CBS", "Allegiant Stadium, Las Vegas", 24, 23, true)
        // 14. Los Angeles Chargers @ Seattle Seahawks -7 (Picks: Clive=Seahawks, Mark=Chargers, Conor=Chargers)
        addGame(4, "LAC", "Los Angeles Chargers", "SEA", "Seattle Seahawks", -7.0, "4:25 PM GMT", "Sun, Oct 4", "CBS", "Lumen Field, Seattle", 20, 24, true)
        // 15. Detroit Lions -3.5 @ Carolina Panthers (Picks: Clive=Lions, Mark=Panthers, Conor=Lions)
        addGame(4, "DGMT", "Detroit Lions", "CAR", "Carolina Panthers", 3.5, "8:20 PM GMT", "Sun, Oct 4", "NBC", "Bank of America Stadium, Charlotte", 27, 20, true)
        // 16. Atlanta Falcons @ New Orleans Saints -2.5 (Picks: Clive=Saints, Mark=Falcons, Conor=Falcons)
        addGame(4, "ATL", "Atlanta Falcons", "NO", "New Orleans Saints", -2.5, "8:15 PM GMT", "Mon, Oct 5", "ESPN", "Caesars Superdome, New Orleans", 20, 24, true)

        // ==================== WEEK 5 (15 Games - All Final) ====================
        // Spread target: Clive 3, Mark 7, Conor 8 (Conor +€10, Clive -€5, Mark -€5)
        // 1. Tampa Bay Buccaneers @ Dallas Cowboys -8.5 (Picks: Clive=Cowboys, Mark=Bucs, Conor=Cowboys)
        addGame(5, "TB", "Tampa Bay Buccaneers", "DAL", "Dallas Cowboys", -8.5, "8:20 PM GMT", "Sun, Oct 11", "NBC", "AT&T Stadium, Arlington", 14, 27, true)
        // 2. Philadelphia Eagles @ Jacksonville Jaguars -7.5 (Picks: Clive=Jags, Mark=Eagles, Conor=Eagles)
        addGame(5, "PHI", "Philadelphia Eagles", "JAX", "Jacksonville Jaguars", -7.5, "9:30 AM GMT", "Sun, Oct 11", "NFL Network", "EverBank Stadium, Jacksonville", 17, 27, true)
        // 3. Cincinnati Bengals -6.5 @ Miami Dolphins (Picks: Clive=Dolphins, Mark=Bengals, Conor=Bengals)
        addGame(5, "CIN", "Cincinnati Bengals", "MIA", "Miami Dolphins", 6.5, "1:00 PM GMT", "Sun, Oct 11", "CBS", "Hard Rock Stadium, Miami Gardens", 30, 17, true)
        // 4. Cleveland Browns @ New York Jets -2.5 (Picks: Clive=Browns, Mark=Jets, Conor=Browns)
        addGame(5, "CLE", "Cleveland Browns", "NYJ", "New York Jets", -2.5, "1:00 PM GMT", "Sun, Oct 11", "CBS", "MetLife Stadium, East Rutherford", 20, 17, true)
        // 5. Houston Texans -7.5 @ Tennessee Titans (Picks: Clive=Texans, Mark=Titans, Conor=Texans)
        addGame(5, "HOU", "Houston Texans", "TEN", "Tennessee Titans", 7.5, "1:00 PM GMT", "Sun, Oct 11", "CBS", "Nissan Stadium, Nashville", 24, 20, true)
        // 6. Indianapolis Colts @ Pittsburgh Steelers -2.5 (Picks: Clive=Colts, Mark=Steelers, Conor=Colts)
        addGame(5, "IND", "Indianapolis Colts", "PIT", "Pittsburgh Steelers", -2.5, "1:00 PM GMT", "Sun, Oct 11", "CBS", "Acrisure Stadium, Pittsburgh", 17, 24, true)
        // 7. Las Vegas Raiders @ New England Patriots -3.5 (Picks: Clive=Pats, Mark=Raiders, Conor=Raiders)
        addGame(5, "LV", "Las Vegas Raiders", "NE", "New England Patriots", -3.5, "1:00 PM GMT", "Sun, Oct 11", "FOX", "Gillette Stadium, Foxborough", 24, 21, true)
        // 8. Minnesota Vikings -2.5 @ New Orleans Saints (Picks: Clive=Saints, Mark=Saints, Conor=Vikings)
        addGame(5, "MIN", "Minnesota Vikings", "NO", "New Orleans Saints", 2.5, "1:00 PM GMT", "Sun, Oct 11", "FOX", "Caesars Superdome, New Orleans", 27, 20, true)
        // 9. New York Giants @ Washington Commanders -4.5 (Picks: Clive=Team, Mark=Team, Conor=Team)
        addGame(5, "NYG", "New York Giants", "WAS", "Washington Commanders", -4.5, "1:00 PM GMT", "Sun, Oct 11", "FOX", "Northwest Stadium, Landover", 20, 23, true)
        // 10. Denver Broncos -3.5 @ Los Angeles Chargers (Picks: Clive=Broncos, Mark=Chargers, Conor=Broncos)
        addGame(5, "DEN", "Denver Broncos", "LAC", "Los Angeles Chargers", 3.5, "4:05 PM GMT", "Sun, Oct 11", "CBS", "SoFi Stadium, Inglewood", 21, 20, true)
        // 11. Chicago Bears -1.5 @ Green Bay Packers (Picks: Clive=Bears, Mark=Bears, Conor=Bears)
        addGame(5, "CHI", "Chicago Bears", "GB", "Green Bay Packers", 1.5, "4:25 PM GMT", "Sun, Oct 11", "FOX", "Lambeau Field, Green Bay", 20, 24, true)
        // 12. Detroit Lions -5.5 @ Arizona Cardinals (Picks: Clive=Cardinals, Mark=Lions, Conor=Lions)
        addGame(5, "DGMT", "Detroit Lions", "ARI", "Arizona Cardinals", 5.5, "4:25 PM GMT", "Sun, Oct 11", "FOX", "State Farm Stadium, Glendale", 31, 20, true)
        // 13. San Francisco 49ers @ Seattle Seahawks -3 (Picks: Clive=Seahawks, Mark=Seahawks, Conor=49ers)
        addGame(5, "SF", "San Francisco 49ers", "SEA", "Seattle Seahawks", -3.0, "4:25 PM GMT", "Sun, Oct 11", "FOX", "Lumen Field, Seattle", 24, 20, true)
        // 14. Baltimore Ravens @ Atlanta Falcons -3 (Picks: Clive=Falcons, Mark=Falcons, Conor=Falcons)
        addGame(5, "BAL", "Baltimore Ravens", "ATL", "Atlanta Falcons", -3.0, "1:00 PM GMT", "Sun, Oct 11", "CBS", "Mercedes-Benz Stadium, Atlanta", 24, 20, true)
        // 15. Buffalo Bills @ Los Angeles Rams -3 (Picks: Clive=Rams, Mark=Bills, Conor=Bills)
        addGame(5, "BUF", "Buffalo Bills", "LAR", "Los Angeles Rams", -3.0, "8:15 PM GMT", "Mon, Oct 12", "ESPN", "SoFi Stadium, Inglewood", 27, 21, true)

        // ==================== WEEK 6 (14 Games - All Final) ====================
        // Spread target: Clive 6, Mark 8, Conor 8 (Tie for first: Mark & Conor +€0, Clive +€0)
        // 1. Seattle Seahawks -2.5 @ Denver Broncos (Picks: Clive=Seahawks, Mark=Broncos, Conor=Seahawks)
        addGame(6, "SEA", "Seattle Seahawks", "DEN", "Denver Broncos", 2.5, "1:00 PM GMT", "Sun, Oct 18", "CBS", "Empower Field at Mile High, Denver", 24, 17, true)
        // 2. Houston Texans @ Jacksonville Jaguars -2.5 (Picks: Clive=Jags, Mark=Texans, Conor=Texans)
        addGame(6, "HOU", "Houston Texans", "JAX", "Jacksonville Jaguars", -2.5, "1:00 PM GMT", "Sun, Oct 18", "CBS", "EverBank Stadium, Jacksonville", 17, 24, true)
        // 3. Baltimore Ravens -3 @ Cleveland Browns (Picks: Clive=browns, Mark=Ravens, Conor=Browns)
        addGame(6, "BAL", "Baltimore Ravens", "CLE", "Cleveland Browns", 3.0, "1:00 PM GMT", "Sun, Oct 18", "CBS", "Huntington Bank Field, Cleveland", 23, 20, true)
        // 4. Carolina Panthers -3 @ Philadelphia Eagles (Picks: Clive=eagles, Mark=Eagles, Conor=Panthers)
        addGame(6, "CAR", "Carolina Panthers", "PHI", "Philadelphia Eagles", 3.0, "1:00 PM GMT", "Sun, Oct 18", "FOX", "Lincoln Financial Field, Philadelphia", 23, 20, true)
        // 5. Chicago Bears -1.5 @ Atlanta Falcons (Picks: Clive=bears, Mark=Bears, Conor=Falcons)
        addGame(6, "CHI", "Chicago Bears", "ATL", "Atlanta Falcons", 1.5, "1:00 PM GMT", "Sun, Oct 18", "FOX", "Mercedes-Benz Stadium, Atlanta", 20, 21, true)
        // 6. New Orleans Saints -1.5 @ New York Giants (Picks: Clive=giants, Mark=Saints, Conor=Giants)
        addGame(6, "NO", "New Orleans Saints", "NYG", "New York Giants", 1.5, "1:00 PM GMT", "Sun, Oct 18", "FOX", "MetLife Stadium, East Rutherford", 24, 20, true)
        // 7. New York Jets @ New England Patriots -7.5 (Picks: Clive=jets, Mark=Pats, Conor=Patriots)
        addGame(6, "NYJ", "New York Jets", "NE", "New England Patriots", -7.5, "1:00 PM GMT", "Sun, Oct 18", "CBS", "Gillette Stadium, Foxborough", 13, 24, true)
        // 8. Pittsburgh Steelers -2.5 @ Tampa Bay Buccaneers (Picks: Clive=bucs, Mark=Steelers, Conor=Steelers)
        addGame(6, "PIT", "Pittsburgh Steelers", "TB", "Tampa Bay Buccaneers", 2.5, "1:00 PM GMT", "Sun, Oct 18", "CBS", "Raymond James Stadium, Tampa", 24, 20, true)
        // 9. Tennessee Titans @ Indianapolis Colts -6.5 (Picks: Clive=colts, Mark=Titans, Conor=Titans)
        addGame(6, "TEN", "Tennessee Titans", "IND", "Indianapolis Colts", -6.5, "1:00 PM GMT", "Sun, Oct 18", "CBS", "Lucas Oil Stadium, Indianapolis", 14, 24, true)
        // 10. Arizona Cardinals @ Los Angeles Rams -12.5 (Picks: Clive=cardinals, Mark=Rams, Conor=Cardinals)
        addGame(6, "ARI", "Arizona Cardinals", "LAR", "Los Angeles Rams", -12.5, "4:05 PM GMT", "Sun, Oct 18", "FOX", "SoFi Stadium, Inglewood", 10, 27, true)
        // 11. Buffalo Bills -5.5 @ Las Vegas Raiders (Picks: Clive=bills, Mark=Bills, Conor=Bills)
        addGame(6, "BUF", "Buffalo Bills", "LV", "Las Vegas Raiders", 5.5, "4:25 PM GMT", "Sun, Oct 18", "CBS", "Allegiant Stadium, Las Vegas", 27, 20, true)
        // 12. Los Angeles Chargers @ Kansas City Chiefs -7.5 (Picks: Clive=chiefs, Mark=Chiefs, Conor=Chiefs)
        addGame(6, "LAC", "Los Angeles Chargers", "KC", "Kansas City Chiefs", -7.5, "4:25 PM GMT", "Sun, Oct 18", "CBS", "GEHA Field at Arrowhead Stadium, Kansas City", 17, 28, true)
        // 13. Dallas Cowboys @ Green Bay Packers -1.5 (Picks: Clive=cowboys, Mark=Packers, Conor=Packers)
        addGame(6, "DAL", "Dallas Cowboys", "GB", "Green Bay Packers", -1.5, "8:20 PM GMT", "Sun, Oct 18", "NBC", "Lambeau Field, Green Bay", 20, 24, true)
        // 14. Washington Commanders @ San Francisco 49ers -7.5 (Picks: Clive=49ers, Mark=49ers, Conor=49ers)
        addGame(6, "WAS", "Washington Commanders", "SF", "San Francisco 49ers", -7.5, "8:15 PM GMT", "Mon, Oct 19", "ESPN", "Levi's Stadium, Santa Clara", 14, 24, true)

        // ==================== WEEK 7 (14 Games - All Final) ====================
        // Spread target: Clive 7, Mark 7, Conor 9 (Conor +€20, Clive -€10, Mark -€10)
        // 1. New England Patriots @ Chicago Bears -3 (Picks: Clive=Bears, Mark=Bears, Conor=Bears)
        addGame(7, "NE", "New England Patriots", "CHI", "Chicago Bears", -3.0, "1:00 PM GMT", "Sun, Oct 25", "FOX", "Soldier Field, Chicago", 13, 24, true)
        // 2. Pittsburgh Steelers -3 @ New Orleans Saints (Picks: Clive=steelers, Mark=Saints, Conor=steelers)
        addGame(7, "PIT", "Pittsburgh Steelers", "NO", "New Orleans Saints", 3.0, "1:00 PM GMT", "Sun, Oct 25", "CBS", "Caesars Superdome, New Orleans", 24, 17, true)
        // 3. Cincinnati Bengals -2.5 @ Baltimore Ravens (Picks: Clive=bengals, Mark=Ravens, Conor=Bengals)
        addGame(7, "CIN", "Cincinnati Bengals", "BAL", "Baltimore Ravens", 2.5, "1:00 PM GMT", "Sun, Oct 25", "CBS", "M&T Bank Stadium, Baltimore", 27, 23, true)
        // 4. Cleveland Browns -1.5 @ Tennessee Titans (Picks: Clive=browns, Mark=Browns, Conor=Browns)
        addGame(7, "CLE", "Cleveland Browns", "TEN", "Tennessee Titans", 1.5, "1:00 PM GMT", "Sun, Oct 25", "CBS", "Nissan Stadium, Nashville", 24, 20, true)
        // 5. Indianapolis Colts @ Minnesota Vikings -6.5 (Picks: Clive=colts, Mark=Vikings, Conor=Vikings)
        addGame(7, "IND", "Indianapolis Colts", "MIN", "Minnesota Vikings", -6.5, "1:00 PM GMT", "Sun, Oct 25", "FOX", "U.S. Bank Stadium, Minneapolis", 21, 24, true)
        // 6. Miami Dolphins @ New York Jets -3.5 (Picks: Clive=jets, Mark=Jets, Conor=Jets)
        addGame(7, "MIA", "Miami Dolphins", "NYJ", "New York Jets", -3.5, "1:00 PM GMT", "Sun, Oct 25", "CBS", "MetLife Stadium, East Rutherford", 16, 24, true)
        // 7. New York Giants @ Houston Texans -10.5 (Picks: Clive=texans, Mark=Giants, Conor=Giants)
        addGame(7, "NYG", "New York Giants", "HOU", "Houston Texans", -10.5, "1:00 PM GMT", "Sun, Oct 25", "FOX", "NRG Stadium, Houston", 20, 27, true)
        // 8. San Francisco 49ers -2.5 @ Atlanta Falcons (Picks: Clive=49ers, Mark=Falcons, Conor=Falcons)
        addGame(7, "SF", "San Francisco 49ers", "ATL", "Atlanta Falcons", 2.5, "1:00 PM GMT", "Sun, Oct 25", "FOX", "Mercedes-Benz Stadium, Atlanta", 27, 20, true)
        // 9. Tampa Bay Buccaneers @ Carolina Panthers -2.5 (Picks: Clive=panthers, Mark=Panthers, Conor=Panthers)
        addGame(7, "TB", "Tampa Bay Buccaneers", "CAR", "Carolina Panthers", -2.5, "1:00 PM GMT", "Sun, Oct 25", "FOX", "Bank of America Stadium, Charlotte", 23, 20, true)
        // 10. Denver Broncos -4.5 @ Arizona Cardinals (Picks: Clive=cardinals, Mark=Broncos, Conor=Broncos)
        addGame(7, "DEN", "Denver Broncos", "ARI", "Arizona Cardinals", 4.5, "4:05 PM GMT", "Sun, Oct 25", "CBS", "State Farm Stadium, Glendale", 27, 17, true)
        // 11. Green Bay Packers @ Detroit Lions -3.5 (Picks: Clive=lions, Mark=Lions, Conor=Packers)
        addGame(7, "GB", "Green Bay Packers", "DGMT", "Detroit Lions", -3.5, "4:25 PM GMT", "Sun, Oct 25", "FOX", "Ford Field, Detroit", 24, 21, true)
        // 12. Los Angeles Rams -3.5 @ Las Vegas Raiders (Picks: Clive=raiders, Mark=Rams, Conor=Raiders)
        addGame(7, "LAR", "Los Angeles Rams", "LV", "Las Vegas Raiders", 3.5, "4:25 PM GMT", "Sun, Oct 25", "CBS", "Allegiant Stadium, Las Vegas", 28, 20, true)
        // 13. Kansas City Chiefs @ Seattle Seahawks -3 (Picks: Clive=seahawks, Mark=Chiefs, Conor=Chiefs)
        addGame(7, "KC", "Kansas City Chiefs", "SEA", "Seattle Seahawks", -3.0, "8:20 PM GMT", "Sun, Oct 25", "NBC", "Lumen Field, Seattle", 24, 20, true)
        // 14. Dallas Cowboys -4.5 @ Philadelphia Eagles (Picks: Clive=cowboys, Mark=Cowboys, Conor=Cowboys)
        addGame(7, "DAL", "Dallas Cowboys", "PHI", "Philadelphia Eagles", 4.5, "8:15 PM GMT", "Mon, Oct 26", "ESPN", "Lincoln Financial Field, Philadelphia", 24, 23, true)

        // ==================== WEEK 8 (14 Games - All Final) ====================
        // 1. Carolina Panthers @ Green Bay Packers -3.5 (Picks: Clive=Panthers, Mark=Packers, Conor=Panthers) -> CAR covers
        addGame(8, "CAR", "Carolina Panthers", "GB", "Green Bay Packers", -3.5, "1:00 PM GMT", "Sun, Nov 1", "FOX", "Lambeau Field, Green Bay", 21, 24, true)
        // 2. Arizona Cardinals @ Dallas Cowboys -7 (Picks: Clive=cardinals, Mark=Cowboys, Conor=Cowboys) -> ARI covers
        addGame(8, "ARI", "Arizona Cardinals", "DAL", "Dallas Cowboys", -7.0, "1:00 PM GMT", "Sun, Nov 1", "FOX", "AT&T Stadium, Arlington", 20, 24, true)
        // 3. Atlanta Falcons @ Tampa Bay Buccaneers -2.5 (Picks: Clive=Bucs, Mark=Falcons, Conor=Falcons) -> TB covers
        addGame(8, "ATL", "Atlanta Falcons", "TB", "Tampa Bay Buccaneers", -2.5, "1:00 PM GMT", "Sun, Nov 1", "FOX", "Raymond James Stadium, Tampa", 20, 27, true)
        // 4. Baltimore Ravens @ Buffalo Bills -3 (Picks: Clive=Bills, Mark=Bills, Conor=Bills) -> BUF covers
        addGame(8, "BAL", "Baltimore Ravens", "BUF", "Buffalo Bills", -3.0, "1:00 PM GMT", "Sun, Nov 1", "CBS", "Highmark Stadium, Orchard Park", 20, 27, true)
        // 5. Cleveland Browns @ Pittsburgh Steelers -3.5 (Picks: Clive=Steelers, Mark=Browns, Conor=Browns) -> PIT covers
        addGame(8, "CLE", "Cleveland Browns", "PIT", "Pittsburgh Steelers", -3.5, "1:00 PM GMT", "Sun, Nov 1", "CBS", "Acrisure Stadium, Pittsburgh", 17, 24, true)
        // 6. Indianapolis Colts @ Jacksonville Jaguars -10.5 (Picks: Clive=jags, Mark=Colts, Conor=Jags) -> JAX covers
        addGame(8, "IND", "Indianapolis Colts", "JAX", "Jacksonville Jaguars", -10.5, "1:00 PM GMT", "Sun, Nov 1", "CBS", "EverBank Stadium, Jacksonville", 14, 28, true)
        // 7. Las Vegas Raiders @ New York Jets -1.5 (Picks: Clive=jets, Mark=Raiders, Conor=Raiders) -> NYJ covers
        addGame(8, "LV", "Las Vegas Raiders", "NYJ", "New York Jets", -1.5, "1:00 PM GMT", "Sun, Nov 1", "CBS", "MetLife Stadium, East Rutherford", 20, 23, true)
        // 8. Minnesota Vikings @ Detroit Lions -3.5 (Picks: Clive=lions, Mark=Vikings, Conor=Vikings) -> DGMT covers
        addGame(8, "MIN", "Minnesota Vikings", "DGMT", "Detroit Lions", -3.5, "1:00 PM GMT", "Sun, Nov 1", "FOX", "Ford Field, Detroit", 20, 27, true)
        // 9. Tennessee Titans @ Cincinnati Bengals -12.5 (Picks: Clive=bengals, Mark=Bengals, Conor=Bengals) -> TEN covers
        addGame(8, "TEN", "Tennessee Titans", "CIN", "Cincinnati Bengals", -12.5, "1:00 PM GMT", "Sun, Nov 1", "CBS", "Paycor Stadium, Cincinnati", 20, 27, true)
        // 10. Los Angeles Chargers @ Los Angeles Rams -6.5 (Picks: Clive=chargers, Mark=Rams, Conor=Rams) -> LAR covers
        addGame(8, "LAC", "Los Angeles Chargers", "LAR", "Los Angeles Rams", -6.5, "4:05 PM GMT", "Sun, Nov 1", "CBS", "SoFi Stadium, Inglewood", 17, 27, true)
        // 11. Kansas City Chiefs -1.5 @ Denver Broncos (Picks: Clive=chiefs, Mark=Chiefs, Conor=Broncos) -> KC covers
        addGame(8, "KC", "Kansas City Chiefs", "DEN", "Denver Broncos", 1.5, "4:25 PM GMT", "Sun, Nov 1", "CBS", "Empower Field at Mile High, Denver", 24, 20, true)
        // 12. New England Patriots -7 @ Miami Dolphins (Picks: Clive=dolphins, Mark=Pats, Conor=Patriots) -> NE covers
        addGame(8, "NE", "New England Patriots", "MIA", "Miami Dolphins", 7.0, "4:25 PM GMT", "Sun, Nov 1", "CBS", "Hard Rock Stadium, Miami Gardens", 30, 17, true)
        // 13. Philadelphia Eagles @ Washington Commanders -5.5 (Picks: Clive=team, Mark=Team, Conor=Eagles) -> PHI covers
        addGame(8, "PHI", "Philadelphia Eagles", "WAS", "Washington Commanders", -5.5, "8:20 PM GMT", "Sun, Nov 1", "NBC", "Northwest Stadium, Landover", 24, 27, true)
        // 14. Chicago Bears @ Seattle Seahawks -3 (Picks: Clive=bears, Mark=Seahawks, Conor=Bears) -> SEA covers
        addGame(8, "CHI", "Chicago Bears", "SEA", "Seattle Seahawks", -3.0, "8:15 PM GMT", "Mon, Nov 2", "ESPN", "Lumen Field, Seattle", 17, 24, true)

        // ==================== WEEK 9 (14 Games - Active Week, Starts Tonight!) ====================
        // Tonight: Jaguars at Ravens! DraftKings spread: Jax -10.0. Nobody has made their pick yet.
        addGame(9, "JAX", "Jacksonville Jaguars", "BAL", "Baltimore Ravens", 10.0, "8:15 PM GMT", "Thu, Nov 5", "Prime Video", "M&T Bank Stadium, Baltimore", null, null, false)
        addGame(9, "CIN", "Cincinnati Bengals", "ATL", "Atlanta Falcons", 3.0, "9:30 AM GMT", "Sun, Nov 8", "NFL Network", "Santiago Bernabéu Stadium, Madrid", null, null, false)
        addGame(9, "DEN", "Denver Broncos", "CAR", "Carolina Panthers", 2.5, "1:00 PM GMT", "Sun, Nov 8", "CBS", "Bank of America Stadium, Charlotte", null, null, false)
        addGame(9, "DAL", "Dallas Cowboys", "IND", "Indianapolis Colts", 5.5, "1:00 PM GMT", "Sun, Nov 8", "FOX", "Lucas Oil Stadium, Indianapolis", null, null, false)
        addGame(9, "NYJ", "New York Jets", "KC", "Kansas City Chiefs", -9.5, "1:00 PM GMT", "Sun, Nov 8", "CBS", "GEHA Field at Arrowhead Stadium, Kansas City", null, null, false)
        addGame(9, "DGMT", "Detroit Lions", "MIA", "Miami Dolphins", 7.5, "1:00 PM GMT", "Sun, Nov 8", "FOX", "Hard Rock Stadium, Miami Gardens", null, null, false)
        addGame(9, "CLE", "Cleveland Browns", "NO", "New Orleans Saints", -2.5, "1:00 PM GMT", "Sun, Nov 8", "CBS", "Caesars Superdome, New Orleans", null, null, false)
        addGame(9, "NYG", "New York Giants", "PHI", "Philadelphia Eagles", -3.5, "1:00 PM GMT", "Sun, Nov 8", "FOX", "Lincoln Financial Field, Philadelphia", null, null, false)
        addGame(9, "HOU", "Houston Texans", "LAC", "Los Angeles Chargers", 4.5, "4:05 PM GMT", "Sun, Nov 8", "CBS", "SoFi Stadium, Inglewood", null, null, false)
        addGame(9, "ARI", "Arizona Cardinals", "SEA", "Seattle Seahawks", -8.5, "4:25 PM GMT", "Sun, Nov 8", "FOX", "Lumen Field, Seattle", null, null, false)
        addGame(9, "GB", "Green Bay Packers", "NE", "New England Patriots", -3.5, "4:25 PM GMT", "Sun, Nov 8", "FOX", "Gillette Stadium, Foxborough", null, null, false)
        addGame(9, "LV", "Las Vegas Raiders", "SF", "San Francisco 49ers", -9.5, "4:25 PM GMT", "Sun, Nov 8", "CBS", "Levi's Stadium, Santa Clara", null, null, false)
        addGame(9, "TB", "Tampa Bay Buccaneers", "CHI", "Chicago Bears", -6.5, "8:20 PM GMT", "Sun, Nov 8", "NBC", "Soldier Field, Chicago", null, null, false)
        addGame(9, "BUF", "Buffalo Bills", "MIN", "Minnesota Vikings", 2.5, "8:15 PM GMT", "Mon, Nov 9", "ESPN", "U.S. Bank Stadium, Minneapolis", null, null, false)

        // ==================== WEEK 10 (14 Games) ====================
        addGame(10, "CIN", "Cincinnati Bengals", "BAL", "Baltimore Ravens", -6.0, "8:15 PM GMT", "Thu, Nov 12", "Prime Video", "M&T Bank Stadium, Baltimore", null, null, false)
        addGame(10, "NYG", "New York Giants", "CAR", "Carolina Panthers", 4.5, "9:30 AM GMT", "Sun, Nov 15", "NFL Network", "Allianz Arena, Munich", null, null, false)
        addGame(10, "NE", "New England Patriots", "CHI", "Chicago Bears", -6.5, "1:00 PM GMT", "Sun, Nov 15", "FOX", "Soldier Field, Chicago", null, null, false)
        addGame(10, "BUF", "Buffalo Bills", "IND", "Indianapolis Colts", 4.5, "1:00 PM GMT", "Sun, Nov 15", "CBS", "Lucas Oil Stadium, Indianapolis", null, null, false)
        addGame(10, "MIN", "Minnesota Vikings", "JAX", "Jacksonville Jaguars", 4.5, "1:00 PM GMT", "Sun, Nov 15", "FOX", "EverBank Stadium, Jacksonville", null, null, false)
        addGame(10, "DEN", "Denver Broncos", "KC", "Kansas City Chiefs", -8.5, "1:00 PM GMT", "Sun, Nov 15", "CBS", "GEHA Field at Arrowhead Stadium, Kansas City", null, null, false)
        addGame(10, "ATL", "Atlanta Falcons", "NO", "New Orleans Saints", 3.5, "1:00 PM GMT", "Sun, Nov 15", "FOX", "Caesars Superdome, New Orleans", null, null, false)
        addGame(10, "SF", "San Francisco 49ers", "TB", "Tampa Bay Buccaneers", 6.0, "1:00 PM GMT", "Sun, Nov 15", "FOX", "Raymond James Stadium, Tampa", null, null, false)
        addGame(10, "PIT", "Pittsburgh Steelers", "WAS", "Washington Commanders", -3.0, "1:00 PM GMT", "Sun, Nov 15", "CBS", "Northwest Stadium, Landover", null, null, false)
        addGame(10, "TEN", "Tennessee Titans", "LAC", "Los Angeles Chargers", -7.5, "4:05 PM GMT", "Sun, Nov 15", "FOX", "SoFi Stadium, Inglewood", null, null, false)
        addGame(10, "NYJ", "New York Jets", "ARI", "Arizona Cardinals", -1.0, "4:25 PM GMT", "Sun, Nov 15", "CBS", "State Farm Stadium, Glendale", null, null, false)
        addGame(10, "PHI", "Philadelphia Eagles", "DAL", "Dallas Cowboys", 7.0, "4:25 PM GMT", "Sun, Nov 15", "CBS", "AT&T Stadium, Arlington", null, null, false)
        addGame(10, "DGMT", "Detroit Lions", "HOU", "Houston Texans", 3.5, "8:20 PM GMT", "Sun, Nov 15", "NBC", "NRG Stadium, Houston", null, null, false)
        addGame(10, "MIA", "Miami Dolphins", "LAR", "Los Angeles Rams", -1.0, "8:15 PM GMT", "Mon, Nov 16", "ESPN", "SoFi Stadium, Inglewood", null, null, false)

        // ==================== WEEK 11 (14 Games) ====================
        addGame(11, "WAS", "Washington Commanders", "PHI", "Philadelphia Eagles", -3.5, "8:15 PM GMT", "Thu, Nov 19", "Prime Video", "Lincoln Financial Field, Philadelphia", null, null, false)
        addGame(11, "GB", "Green Bay Packers", "CHI", "Chicago Bears", 6.0, "1:00 PM GMT", "Sun, Nov 22", "FOX", "Soldier Field, Chicago", null, null, false)
        addGame(11, "JAX", "Jacksonville Jaguars", "DGMT", "Detroit Lions", -13.5, "1:00 PM GMT", "Sun, Nov 22", "CBS", "Ford Field, Detroit", null, null, false)
        addGame(11, "LV", "Las Vegas Raiders", "MIA", "Miami Dolphins", -7.5, "1:00 PM GMT", "Sun, Nov 22", "CBS", "Hard Rock Stadium, Miami Gardens", null, null, false)
        addGame(11, "LAR", "Los Angeles Rams", "NE", "New England Patriots", 5.0, "1:00 PM GMT", "Sun, Nov 22", "FOX", "Gillette Stadium, Foxborough", null, null, false)
        addGame(11, "CLE", "Cleveland Browns", "NO", "New Orleans Saints", -1.0, "1:00 PM GMT", "Sun, Nov 22", "FOX", "Caesars Superdome, New Orleans", null, null, false)
        addGame(11, "IND", "Indianapolis Colts", "NYJ", "New York Jets", -3.5, "1:00 PM GMT", "Sun, Nov 22", "CBS", "MetLife Stadium, East Rutherford", null, null, false)
        addGame(11, "BAL", "Baltimore Ravens", "PIT", "Pittsburgh Steelers", 3.0, "1:00 PM GMT", "Sun, Nov 22", "CBS", "Acrisure Stadium, Pittsburgh", null, null, false)
        addGame(11, "MIN", "Minnesota Vikings", "TEN", "Tennessee Titans", 6.0, "1:00 PM GMT", "Sun, Nov 22", "CBS", "Nissan Stadium, Nashville", null, null, false)
        addGame(11, "ATL", "Atlanta Falcons", "DEN", "Denver Broncos", -2.5, "4:05 PM GMT", "Sun, Nov 22", "FOX", "Empower Field at Mile High, Denver", null, null, false)
        addGame(11, "SEA", "Seattle Seahawks", "SF", "San Francisco 49ers", -6.5, "4:05 PM GMT", "Sun, Nov 22", "FOX", "Levi's Stadium, Santa Clara", null, null, false)
        addGame(11, "KC", "Kansas City Chiefs", "BUF", "Buffalo Bills", -2.5, "4:25 PM GMT", "Sun, Nov 22", "CBS", "Highmark Stadium, Orchard Park", null, null, false)
        addGame(11, "CIN", "Cincinnati Bengals", "LAC", "Los Angeles Chargers", -1.5, "8:20 PM GMT", "Sun, Nov 22", "NBC", "SoFi Stadium, Inglewood", null, null, false)
        addGame(11, "HOU", "Houston Texans", "DAL", "Dallas Cowboys", 7.0, "8:15 PM GMT", "Mon, Nov 23", "ESPN", "AT&T Stadium, Arlington", null, null, false)

        // ==================== WEEK 12 (13 Games) ====================
        addGame(12, "PIT", "Pittsburgh Steelers", "CLE", "Cleveland Browns", 3.5, "8:15 PM GMT", "Thu, Nov 26", "Prime Video", "Huntington Bank Field, Cleveland", null, null, false)
        addGame(12, "KC", "Kansas City Chiefs", "CAR", "Carolina Panthers", 11.0, "1:00 PM GMT", "Sun, Nov 29", "CBS", "Bank of America Stadium, Charlotte", null, null, false)
        addGame(12, "MIN", "Minnesota Vikings", "CHI", "Chicago Bears", 3.5, "1:00 PM GMT", "Sun, Nov 29", "FOX", "Soldier Field, Chicago", null, null, false)
        addGame(12, "TEN", "Tennessee Titans", "HOU", "Houston Texans", -8.5, "1:00 PM GMT", "Sun, Nov 29", "CBS", "NRG Stadium, Houston", null, null, false)
        addGame(12, "DGMT", "Detroit Lions", "IND", "Indianapolis Colts", 7.5, "1:00 PM GMT", "Sun, Nov 29", "FOX", "Lucas Oil Stadium, Indianapolis", null, null, false)
        addGame(12, "NE", "New England Patriots", "MIA", "Miami Dolphins", -7.0, "1:00 PM GMT", "Sun, Nov 29", "CBS", "Hard Rock Stadium, Miami Gardens", null, null, false)
        addGame(12, "TB", "Tampa Bay Buccaneers", "NYG", "New York Giants", 5.5, "1:00 PM GMT", "Sun, Nov 29", "CBS", "MetLife Stadium, East Rutherford", null, null, false)
        addGame(12, "DAL", "Dallas Cowboys", "WAS", "Washington Commanders", -10.5, "1:00 PM GMT", "Sun, Nov 29", "FOX", "Northwest Stadium, Landover", null, null, false)
        addGame(12, "DEN", "Denver Broncos", "LV", "Las Vegas Raiders", 5.5, "4:05 PM GMT", "Sun, Nov 29", "CBS", "Allegiant Stadium, Las Vegas", null, null, false)
        addGame(12, "SF", "San Francisco 49ers", "GB", "Green Bay Packers", -2.5, "4:25 PM GMT", "Sun, Nov 29", "FOX", "Lambeau Field, Green Bay", null, null, false)
        addGame(12, "ARI", "Arizona Cardinals", "SEA", "Seattle Seahawks", -1.0, "4:25 PM GMT", "Sun, Nov 29", "FOX", "Lumen Field, Seattle", null, null, false)
        addGame(12, "PHI", "Philadelphia Eagles", "LAR", "Los Angeles Rams", 3.0, "8:20 PM GMT", "Sun, Nov 29", "NBC", "SoFi Stadium, Inglewood", null, null, false)
        addGame(12, "BAL", "Baltimore Ravens", "LAC", "Los Angeles Chargers", 3.0, "8:15 PM GMT", "Mon, Nov 30", "ESPN", "SoFi Stadium, Inglewood", null, null, false)

        // ==================== WEEK 13 (16 Games - Thanksgiving Slate) ====================
        addGame(13, "CHI", "Chicago Bears", "DGMT", "Detroit Lions", -10.5, "12:30 PM GMT", "Thu, Dec 3", "CBS", "Ford Field, Detroit", null, null, false)
        addGame(13, "NYG", "New York Giants", "DAL", "Dallas Cowboys", -3.5, "4:30 PM GMT", "Thu, Dec 3", "FOX", "AT&T Stadium, Arlington", null, null, false)
        addGame(13, "MIA", "Miami Dolphins", "GB", "Green Bay Packers", -3.5, "8:20 PM GMT", "Thu, Dec 3", "NBC", "Lambeau Field, Green Bay", null, null, false)
        addGame(13, "LV", "Las Vegas Raiders", "KC", "Kansas City Chiefs", -13.0, "3:00 PM GMT", "Fri, Dec 4", "Prime Video", "GEHA Field at Arrowhead Stadium, Kansas City", null, null, false)
        addGame(13, "LAC", "Los Angeles Chargers", "ATL", "Atlanta Falcons", 1.5, "1:00 PM GMT", "Sun, Dec 6", "CBS", "Mercedes-Benz Stadium, Atlanta", null, null, false)
        addGame(13, "PIT", "Pittsburgh Steelers", "CIN", "Cincinnati Bengals", -2.5, "1:00 PM GMT", "Sun, Dec 6", "CBS", "Paycor Stadium, Cincinnati", null, null, false)
        addGame(13, "HOU", "Houston Texans", "JAX", "Jacksonville Jaguars", 4.0, "1:00 PM GMT", "Sun, Dec 6", "FOX", "EverBank Stadium, Jacksonville", null, null, false)
        addGame(13, "ARI", "Arizona Cardinals", "MIN", "Minnesota Vikings", -3.5, "1:00 PM GMT", "Sun, Dec 6", "FOX", "U.S. Bank Stadium, Minneapolis", null, null, false)
        addGame(13, "IND", "Indianapolis Colts", "NE", "New England Patriots", 2.5, "1:00 PM GMT", "Sun, Dec 6", "CBS", "Gillette Stadium, Foxborough", null, null, false)
        addGame(13, "SEA", "Seattle Seahawks", "NYJ", "New York Jets", -1.5, "1:00 PM GMT", "Sun, Dec 6", "FOX", "MetLife Stadium, East Rutherford", null, null, false)
        addGame(13, "TEN", "Tennessee Titans", "WAS", "Washington Commanders", -6.0, "1:00 PM GMT", "Sun, Dec 6", "CBS", "Northwest Stadium, Landover", null, null, false)
        addGame(13, "TB", "Tampa Bay Buccaneers", "CAR", "Carolina Panthers", 6.0, "4:05 PM GMT", "Sun, Dec 6", "FOX", "Bank of America Stadium, Charlotte", null, null, false)
        addGame(13, "LAR", "Los Angeles Rams", "NO", "New Orleans Saints", 2.5, "4:05 PM GMT", "Sun, Dec 6", "FOX", "Caesars Superdome, New Orleans", null, null, false)
        addGame(13, "PHI", "Philadelphia Eagles", "BAL", "Baltimore Ravens", -3.0, "4:25 PM GMT", "Sun, Dec 6", "CBS", "M&T Bank Stadium, Baltimore", null, null, false)
        addGame(13, "SF", "San Francisco 49ers", "BUF", "Buffalo Bills", -6.5, "8:20 PM GMT", "Sun, Dec 6", "NBC", "Highmark Stadium, Orchard Park", null, null, false)
        addGame(13, "CLE", "Cleveland Browns", "DEN", "Denver Broncos", -5.5, "8:15 PM GMT", "Mon, Dec 7", "ESPN", "Empower Field at Mile High, Denver", null, null, false)

        // ==================== WEEK 14 (13 Games) ====================
        addGame(14, "GB", "Green Bay Packers", "DGMT", "Detroit Lions", -3.5, "8:15 PM GMT", "Thu, Dec 10", "Prime Video", "Ford Field, Detroit", null, null, false)
        addGame(14, "NYJ", "New York Jets", "MIA", "Miami Dolphins", -6.5, "1:00 PM GMT", "Sun, Dec 13", "CBS", "Hard Rock Stadium, Miami Gardens", null, null, false)
        addGame(14, "ATL", "Atlanta Falcons", "MIN", "Minnesota Vikings", -4.5, "1:00 PM GMT", "Sun, Dec 13", "FOX", "U.S. Bank Stadium, Minneapolis", null, null, false)
        addGame(14, "NO", "New Orleans Saints", "NYG", "New York Giants", -1.0, "1:00 PM GMT", "Sun, Dec 13", "FOX", "MetLife Stadium, East Rutherford", null, null, false)
        addGame(14, "CAR", "Carolina Panthers", "PHI", "Philadelphia Eagles", -13.0, "1:00 PM GMT", "Sun, Dec 13", "FOX", "Lincoln Financial Field, Philadelphia", null, null, false)
        addGame(14, "CLE", "Cleveland Browns", "PIT", "Pittsburgh Steelers", -6.5, "1:00 PM GMT", "Sun, Dec 13", "CBS", "Acrisure Stadium, Pittsburgh", null, null, false)
        addGame(14, "LV", "Las Vegas Raiders", "TB", "Tampa Bay Buccaneers", -6.5, "1:00 PM GMT", "Sun, Dec 13", "CBS", "Raymond James Stadium, Tampa", null, null, false)
        addGame(14, "JAX", "Jacksonville Jaguars", "TEN", "Tennessee Titans", -3.5, "1:00 PM GMT", "Sun, Dec 13", "CBS", "Nissan Stadium, Nashville", null, null, false)
        addGame(14, "SEA", "Seattle Seahawks", "ARI", "Arizona Cardinals", 2.5, "4:05 PM GMT", "Sun, Dec 13", "CBS", "State Farm Stadium, Glendale", null, null, false)
        addGame(14, "BUF", "Buffalo Bills", "LAR", "Los Angeles Rams", 4.5, "4:25 PM GMT", "Sun, Dec 13", "FOX", "SoFi Stadium, Inglewood", null, null, false)
        addGame(14, "CHI", "Chicago Bears", "SF", "San Francisco 49ers", -4.5, "4:25 PM GMT", "Sun, Dec 13", "FOX", "Levi's Stadium, Santa Clara", null, null, false)
        addGame(14, "LAC", "Los Angeles Chargers", "KC", "Kansas City Chiefs", -4.0, "8:20 PM GMT", "Sun, Dec 13", "NBC", "GEHA Field at Arrowhead Stadium, Kansas City", null, null, false)
        addGame(14, "CIN", "Cincinnati Bengals", "DAL", "Dallas Cowboys", 5.5, "8:15 PM GMT", "Mon, Dec 14", "ESPN", "AT&T Stadium, Arlington", null, null, false)

        // ==================== WEEK 15 (16 Games) ====================
        addGame(15, "LAR", "Los Angeles Rams", "SF", "San Francisco 49ers", -2.5, "8:15 PM GMT", "Thu, Dec 17", "Prime Video", "Levi's Stadium, Santa Clara", null, null, false)
        addGame(15, "DAL", "Dallas Cowboys", "CAR", "Carolina Panthers", 3.0, "1:00 PM GMT", "Sun, Dec 20", "FOX", "Bank of America Stadium, Charlotte", null, null, false)
        addGame(15, "KC", "Kansas City Chiefs", "CLE", "Cleveland Browns", 4.5, "1:00 PM GMT", "Sun, Dec 20", "CBS", "Huntington Bank Field, Cleveland", null, null, false)
        addGame(15, "MIA", "Miami Dolphins", "HOU", "Houston Texans", -2.5, "1:00 PM GMT", "Sun, Dec 20", "CBS", "NRG Stadium, Houston", null, null, false)
        addGame(15, "NYJ", "New York Jets", "JAX", "Jacksonville Jaguars", 3.5, "1:00 PM GMT", "Sun, Dec 20", "CBS", "EverBank Stadium, Jacksonville", null, null, false)
        addGame(15, "WAS", "Washington Commanders", "NO", "New Orleans Saints", 3.5, "1:00 PM GMT", "Sun, Dec 20", "FOX", "Caesars Superdome, New Orleans", null, null, false)
        addGame(15, "BAL", "Baltimore Ravens", "NYG", "New York Giants", 16.0, "1:00 PM GMT", "Sun, Dec 20", "CBS", "MetLife Stadium, East Rutherford", null, null, false)
        addGame(15, "CIN", "Cincinnati Bengals", "TEN", "Tennessee Titans", 5.5, "1:00 PM GMT", "Sun, Dec 20", "FOX", "Nissan Stadium, Nashville", null, null, false)
        addGame(15, "NE", "New England Patriots", "ARI", "Arizona Cardinals", -4.5, "4:25 PM GMT", "Sun, Dec 20", "CBS", "State Farm Stadium, Glendale", null, null, false)
        addGame(15, "IND", "Indianapolis Colts", "DEN", "Denver Broncos", -4.5, "4:25 PM GMT", "Sun, Dec 20", "CBS", "Empower Field at Mile High, Denver", null, null, false)
        addGame(15, "BUF", "Buffalo Bills", "DGMT", "Detroit Lions", 1.5, "4:25 PM GMT", "Sun, Dec 20", "CBS", "Ford Field, Detroit", null, null, false)
        addGame(15, "TB", "Tampa Bay Buccaneers", "LAC", "Los Angeles Chargers", -3.5, "4:25 PM GMT", "Sun, Dec 20", "FOX", "SoFi Stadium, Inglewood", null, null, false)
        addGame(15, "PIT", "Pittsburgh Steelers", "PHI", "Philadelphia Eagles", -5.5, "4:25 PM GMT", "Sun, Dec 20", "FOX", "Lincoln Financial Field, Philadelphia", null, null, false)
        addGame(15, "GB", "Green Bay Packers", "SEA", "Seattle Seahawks", 3.5, "8:20 PM GMT", "Sun, Dec 20", "NBC", "Lumen Field, Seattle", null, null, false)
        addGame(15, "CHI", "Chicago Bears", "MIN", "Minnesota Vikings", -7.0, "8:15 PM GMT", "Mon, Dec 21", "ESPN", "U.S. Bank Stadium, Minneapolis", null, null, false)
        addGame(15, "ATL", "Atlanta Falcons", "LV", "Las Vegas Raiders", 4.5, "8:30 PM GMT", "Mon, Dec 21", "ESPN", "Allegiant Stadium, Las Vegas", null, null, false)

        // ==================== WEEK 16 (16 Games) ====================
        addGame(16, "CLE", "Cleveland Browns", "CIN", "Cincinnati Bengals", -7.0, "8:15 PM GMT", "Thu, Dec 24", "Prime Video", "Paycor Stadium, Cincinnati", null, null, false)
        addGame(16, "HOU", "Houston Texans", "KC", "Kansas City Chiefs", -3.5, "1:00 PM GMT", "Sat, Dec 26", "NBC", "GEHA Field at Arrowhead Stadium, Kansas City", null, null, false)
        addGame(16, "PIT", "Pittsburgh Steelers", "BAL", "Baltimore Ravens", -6.5, "4:30 PM GMT", "Sat, Dec 26", "FOX", "M&T Bank Stadium, Baltimore", null, null, false)
        addGame(16, "NYG", "New York Giants", "ATL", "Atlanta Falcons", -9.0, "1:00 PM GMT", "Sun, Dec 27", "FOX", "Mercedes-Benz Stadium, Atlanta", null, null, false)
        addGame(16, "NE", "New England Patriots", "BUF", "Buffalo Bills", -14.0, "1:00 PM GMT", "Sun, Dec 27", "CBS", "Highmark Stadium, Orchard Park", null, null, false)
        addGame(16, "ARI", "Arizona Cardinals", "CAR", "Carolina Panthers", 4.5, "1:00 PM GMT", "Sun, Dec 27", "FOX", "Bank of America Stadium, Charlotte", null, null, false)
        addGame(16, "DGMT", "Detroit Lions", "CHI", "Chicago Bears", 7.0, "1:00 PM GMT", "Sun, Dec 27", "FOX", "Soldier Field, Chicago", null, null, false)
        addGame(16, "TEN", "Tennessee Titans", "IND", "Indianapolis Colts", -3.5, "1:00 PM GMT", "Sun, Dec 27", "CBS", "Lucas Oil Stadium, Indianapolis", null, null, false)
        addGame(16, "LAR", "Los Angeles Rams", "NYJ", "New York Jets", 3.0, "1:00 PM GMT", "Sun, Dec 27", "CBS", "MetLife Stadium, East Rutherford", null, null, false)
        addGame(16, "PHI", "Philadelphia Eagles", "WAS", "Washington Commanders", 3.5, "1:00 PM GMT", "Sun, Dec 27", "FOX", "Northwest Stadium, Landover", null, null, false)
        addGame(16, "DEN", "Denver Broncos", "LAC", "Los Angeles Chargers", -2.5, "4:05 PM GMT", "Sun, Dec 27", "CBS", "SoFi Stadium, Inglewood", null, null, false)
        addGame(16, "MIN", "Minnesota Vikings", "SEA", "Seattle Seahawks", 1.5, "4:05 PM GMT", "Sun, Dec 27", "FOX", "Lumen Field, Seattle", null, null, false)
        addGame(16, "JAX", "Jacksonville Jaguars", "LV", "Las Vegas Raiders", 1.5, "4:25 PM GMT", "Sun, Dec 27", "CBS", "Allegiant Stadium, Las Vegas", null, null, false)
        addGame(16, "SF", "San Francisco 49ers", "MIA", "Miami Dolphins", 1.5, "4:25 PM GMT", "Sun, Dec 27", "CBS", "Hard Rock Stadium, Miami Gardens", null, null, false)
        addGame(16, "TB", "Tampa Bay Buccaneers", "DAL", "Dallas Cowboys", 3.5, "8:20 PM GMT", "Sun, Dec 27", "NBC", "AT&T Stadium, Arlington", null, null, false)
        addGame(16, "NO", "New Orleans Saints", "GB", "Green Bay Packers", -13.5, "8:15 PM GMT", "Mon, Dec 28", "ESPN", "Lambeau Field, Green Bay", null, null, false)

        // ==================== WEEK 17 (16 Games) ====================
        addGame(17, "KC", "Kansas City Chiefs", "PIT", "Pittsburgh Steelers", 2.5, "1:00 PM GMT", "Fri, Jan 1", "Netflix", "Acrisure Stadium, Pittsburgh", null, null, false)
        addGame(17, "BAL", "Baltimore Ravens", "HOU", "Houston Texans", 2.0, "4:30 PM GMT", "Fri, Jan 1", "Netflix", "NRG Stadium, Houston", null, null, false)
        addGame(17, "LAC", "Los Angeles Chargers", "NE", "New England Patriots", 4.0, "1:00 PM GMT", "Sun, Jan 3", "CBS", "Gillette Stadium, Foxborough", null, null, false)
        addGame(17, "IND", "Indianapolis Colts", "NYG", "New York Giants", 6.5, "1:00 PM GMT", "Sun, Jan 3", "FOX", "MetLife Stadium, East Rutherford", null, null, false)
        addGame(17, "NYJ", "New York Jets", "BUF", "Buffalo Bills", -9.5, "1:00 PM GMT", "Sun, Jan 3", "CBS", "Highmark Stadium, Orchard Park", null, null, false)
        addGame(17, "CAR", "Carolina Panthers", "TB", "Tampa Bay Buccaneers", -8.5, "1:00 PM GMT", "Sun, Jan 3", "CBS", "Raymond James Stadium, Tampa", null, null, false)
        addGame(17, "GB", "Green Bay Packers", "MIN", "Minnesota Vikings", -1.5, "1:00 PM GMT", "Sun, Jan 3", "FOX", "U.S. Bank Stadium, Minneapolis", null, null, false)
        addGame(17, "LV", "Las Vegas Raiders", "NO", "New Orleans Saints", -1.5, "1:00 PM GMT", "Sun, Jan 3", "FOX", "Caesars Superdome, New Orleans", null, null, false)
        addGame(17, "TEN", "Tennessee Titans", "JAX", "Jacksonville Jaguars", -1.5, "1:00 PM GMT", "Sun, Jan 3", "CBS", "EverBank Stadium, Jacksonville", null, null, false)
        addGame(17, "DAL", "Dallas Cowboys", "PHI", "Philadelphia Eagles", -13.5, "1:00 PM GMT", "Sun, Jan 3", "FOX", "Lincoln Financial Field, Philadelphia", null, null, false)
        addGame(17, "DEN", "Denver Broncos", "CIN", "Cincinnati Bengals", -3.5, "4:05 PM GMT", "Sun, Jan 3", "CBS", "Paycor Stadium, Cincinnati", null, null, false)
        addGame(17, "ARI", "Arizona Cardinals", "LAR", "Los Angeles Rams", -5.5, "4:05 PM GMT", "Sun, Jan 3", "FOX", "SoFi Stadium, Inglewood", null, null, false)
        addGame(17, "MIA", "Miami Dolphins", "CLE", "Cleveland Browns", 3.0, "8:20 PM GMT", "Sun, Jan 3", "NBC", "Huntington Bank Field, Cleveland", null, null, false)
        addGame(17, "ATL", "Atlanta Falcons", "WAS", "Washington Commanders", -3.5, "8:15 PM GMT", "Sun, Jan 3", "FOX", "Northwest Stadium, Landover", null, null, false)
        addGame(17, "CHI", "Chicago Bears", "SEA", "Seattle Seahawks", -4.5, "8:15 PM GMT", "Sun, Jan 3", "CBS", "Lumen Field, Seattle", null, null, false)
        addGame(17, "DGMT", "Detroit Lions", "SF", "San Francisco 49ers", 3.5, "8:15 PM GMT", "Mon, Jan 4", "ESPN", "Levi's Stadium, Santa Clara", null, null, false)

        return games
    }

    /**
     * Official authentic scores for refreshable weeks.
     * Tapping [⚡ Refresh Match Scores] populates these scores and flags isFinal = true.
     */
    fun getOfficialWeekScores(weekNumber: Int): Map<Pair<String, String>, Pair<Int, Int>> {
        // Weeks 9 and onwards: Scores must be entered manually as requested
        if (weekNumber >= 9) return emptyMap()

        val sampleGamesForWeek = getSampleGames().filter { it.weekNumber == weekNumber && it.isFinal && it.awayScore != null && it.homeScore != null }
        if (sampleGamesForWeek.isNotEmpty()) {
            return sampleGamesForWeek.associate { (it.awayTeamShort to it.homeTeamShort) to (it.awayScore!! to it.homeScore!!) }
        }

        return emptyMap()
    }

    /**
     * Fresh fixtures for Week 9 and subsequent weeks
     */
    fun getFreshWeekFixtures(weekNumber: Int): List<NflGame> {
        val all = getSampleGames()
        return all.filter { it.weekNumber == weekNumber }
    }

    val AVAILABLE_SPORTSBOOKS = listOf("DraftKings")

    /**
     * Spreads by sportsbook provider
     */
    fun getSpreadsForProvider(weekNumber: Int, provider: String = "DraftKings"): Map<Pair<String, String>, Double> {
        return when (weekNumber) {
            9 -> when (provider.lowercase()) {
                "fanduel" -> mapOf(
                    ("JAX" to "BAL") to 9.5,   // Jax -9.5
                    ("CIN" to "ATL") to 3.5,   // Cin -3.5
                    ("DEN" to "CAR") to 3.0,   // Den -3.0
                    ("DAL" to "IND") to 6.0,   // Dal -6.0
                    ("NYJ" to "KC") to -10.0,  // KC -10.0
                    ("DGMT" to "MIA") to 7.0,   // Det -7.0
                    ("CLE" to "NO") to -3.0,   // NO -3.0
                    ("NYG" to "PHI") to -3.5,  // Phi -3.5
                    ("HOU" to "LAC") to 4.0,   // Hou -4.0
                    ("ARI" to "SEA") to -9.0,  // Sea -9.0
                    ("GB" to "NE") to -3.0,    // NE -3.0
                    ("LV" to "SF") to -10.0,   // SF -10.0
                    ("TB" to "CHI") to -7.0,   // Chi -7.0
                    ("BUF" to "MIN") to 2.5    // Buf -2.5
                )
                "betmgm" -> mapOf(
                    ("JAX" to "BAL") to 10.0,  // Jax -10.0
                    ("CIN" to "ATL") to 3.0,   // Cin -3.0
                    ("DEN" to "CAR") to 2.5,   // Den -2.5
                    ("DAL" to "IND") to 5.5,   // Dal -5.5
                    ("NYJ" to "KC") to -9.5,   // KC -9.5
                    ("DGMT" to "MIA") to 7.5,   // Det -7.5
                    ("CLE" to "NO") to -2.5,   // NO -2.5
                    ("NYG" to "PHI") to -4.0,  // Phi -4.0
                    ("HOU" to "LAC") to 4.5,   // Hou -4.5
                    ("ARI" to "SEA") to -8.5,  // Sea -8.5
                    ("GB" to "NE") to -3.5,    // NE -3.5
                    ("LV" to "SF") to -9.5,    // SF -9.5
                    ("TB" to "CHI") to -6.5,   // Chi -6.5
                    ("BUF" to "MIN") to 2.0    // Buf -2.0
                )
                "caesars" -> mapOf(
                    ("JAX" to "BAL") to 10.5,  // Jax -10.5
                    ("CIN" to "ATL") to 3.0,   // Cin -3.0
                    ("DEN" to "CAR") to 2.5,   // Den -2.5
                    ("DAL" to "IND") to 5.5,   // Dal -5.5
                    ("NYJ" to "KC") to -9.5,   // KC -9.5
                    ("DGMT" to "MIA") to 7.5,   // Det -7.5
                    ("CLE" to "NO") to -2.5,   // NO -2.5
                    ("NYG" to "PHI") to -3.5,  // Phi -3.5
                    ("HOU" to "LAC") to 4.5,   // Hou -4.5
                    ("ARI" to "SEA") to -8.0,  // Sea -8.0
                    ("GB" to "NE") to -3.5,    // NE -3.5
                    ("LV" to "SF") to -9.0,    // SF -9.0
                    ("TB" to "CHI") to -6.5,   // Chi -6.5
                    ("BUF" to "MIN") to 2.5    // Buf -2.5
                )
                "espn bet" -> mapOf(
                    ("JAX" to "BAL") to 10.0,  // Jax -10.0
                    ("CIN" to "ATL") to 3.0,   // Cin -3.0
                    ("DEN" to "CAR") to 2.5,   // Den -2.5
                    ("DAL" to "IND") to 5.5,   // Dal -5.5
                    ("NYJ" to "KC") to -9.5,   // KC -9.5
                    ("DGMT" to "MIA") to 7.5,   // Det -7.5
                    ("CLE" to "NO") to -2.5,   // NO -2.5
                    ("NYG" to "PHI") to -3.5,  // Phi -3.5
                    ("HOU" to "LAC") to 4.5,   // Hou -4.5
                    ("ARI" to "SEA") to -8.5,  // Sea -8.5
                    ("GB" to "NE") to -3.5,    // NE -3.5
                    ("LV" to "SF") to -9.5,    // SF -9.5
                    ("TB" to "CHI") to -6.5,   // Chi -6.5
                    ("BUF" to "MIN") to 2.5    // Buf -2.5
                )
                else -> mapOf( // DraftKings & default
                    ("JAX" to "BAL") to 10.0,  // Jax -10.0
                    ("CIN" to "ATL") to 3.0,   // Cin -3.0
                    ("DEN" to "CAR") to 2.5,   // Den -2.5
                    ("DAL" to "IND") to 5.5,   // Dal -5.5
                    ("NYJ" to "KC") to -9.5,   // KC -9.5
                    ("DGMT" to "MIA") to 7.5,   // Det -7.5
                    ("CLE" to "NO") to -2.5,   // NO -2.5
                    ("NYG" to "PHI") to -3.5,  // Phi -3.5
                    ("HOU" to "LAC") to 4.5,   // Hou -4.5
                    ("ARI" to "SEA") to -8.5,  // Sea -8.5
                    ("GB" to "NE") to -3.5,    // NE -3.5
                    ("LV" to "SF") to -9.5,    // SF -9.5
                    ("TB" to "CHI") to -6.5,   // Chi -6.5
                    ("BUF" to "MIN") to 2.5    // Buf -2.5
                )
            }
            else -> emptyMap()
        }
    }

    /**
     * Latest market spreads for [📊 Refresh Spreads] (defaults to DraftKings)
     */
    fun getFreshWeekSpreads(weekNumber: Int): Map<Pair<String, String>, Double> =
        getSpreadsForProvider(weekNumber, "DraftKings")

    fun getSamplePicks(games: List<NflGame>): List<SpreadPick> {
        val picks = mutableListOf<SpreadPick>()
        var pickIdCounter = 1L

        val cliveId = 1L
        val markId = 2L
        val conorId = 3L

        for (game in games) {
            val week = game.weekNumber
            val away = game.awayTeamShort
            val home = game.homeTeamShort

            // User requirement: For week 9 and subsequent weeks, nobody has made their pick yet
            if (week >= 9) continue

            // Assign picks for Clive, Mark, and Conor matching attached spreadsheet weekly ATS totals:
            // W1 (8,9,9), W2 (9,8,5), W3 (7,11,4), W4 (8,10,7), W5 (3,7,8), W6 (6,8,8), W7 (7,7,9), W8 (9,5,6)
            // Season total 2026: Clive 57, Mark 65, Conor 56
            val (cPick, mPick, coPick) = when (week) {
                1 -> when (home) {
                    "SEA" -> Triple("NE", "SEA", "SEA")  // Pats covered: Clive (W), Mark (L), Conor (L)
                    "LAR" -> Triple("LAR", "LAR", "LAR") // 49ers covered: all wrong
                    "PIT" -> Triple("ATL", "PIT", "PIT") // Steelers covered: Clive (L), Mark (W), Conor (W)
                    "IND" -> Triple("BAL", "BAL", "BAL") // Ravens covered: all 3 correct
                    "HOU" -> Triple("HOU", "BUF", "BUF") // Bills covered: Clive (L), Mark (W), Conor (W)
                    "CAR" -> Triple("CHI", "CHI", "CHI") // Bears covered: all 3 correct
                    "JAX" -> Triple("CLE", "JAX", "JAX") // Jags covered: Clive (L), Mark (W), Conor (W)
                    "DGMT" -> Triple("DGMT", "DGMT", "DGMT") // Lions covered: all 3 correct
                    "TEN" -> Triple("NYJ", "NYJ", "TEN") // Jets covered: Clive (W), Mark (W), Conor (L)
                    "CIN" -> Triple("TB", "CIN", "CIN")  // Bengals covered: Clive (L), Mark (W), Conor (W)
                    "LAC" -> Triple("LAC", "LAC", "LAC") // Chargers covered: all 3 correct
                    "MIN" -> Triple("GB", "MIN", "GB")   // Packers covered: Clive (W), Mark (L), Conor (W)
                    "LV"  -> Triple("LV", "LV", "LV")    // Dolphins covered: all wrong
                    "PHI" -> Triple("PHI", "PHI", "PHI") // Eagles covered: all 3 correct
                    "NYG" -> Triple("NYG", "DAL", "DAL") // Cowboys covered: Clive (L), Mark (W), Conor (W)
                    "KC"  -> Triple("DEN", "DEN", "DEN") // Chiefs covered: all wrong
                    else -> Triple(home, away, home)
                }
                2 -> when (home) {
                    "BUF" -> Triple("BUF", "BUF", "DGMT") // Bills, Bills, Lions (DGMT covers +4.5)
                    "ATL" -> Triple("ATL", "CAR", "CAR") // falcons, Panthers, Panthers (CAR covers -2.5)
                    "HOU" -> Triple("CIN", "HOU", "CIN") // bengals, Texans, Bengals (CIN covers +2.5)
                    "TB"  -> Triple("CLE", "TB", "TB")   // browns, Bucs, Bucs (CLE covers +8.5)
                    "NYJ" -> Triple("NYJ", "NYJ", "GB")  // jets, Jets, Packers (NYJ covers +3.5)
                    "CHI" -> Triple("CHI", "CHI", "CHI") // bears, Bears, Bears (MIN covers +4.5)
                    "BAL" -> Triple("BAL", "BAL", "BAL") // ravens, Ravens, Ravens (NO covers +7.5)
                    "TEN" -> Triple("PHI", "TEN", "PHI") // eagles, Titans, Eagles (PHI covers -7.0)
                    "NE"  -> Triple("PIT", "PIT", "NE")  // steelers, Steelers, Patriots (PIT covers +5.5)
                    "DEN" -> Triple("DEN", "DEN", "JAX") // broncos, Broncos, Jags (DEN covers -2.5)
                    "LAC" -> Triple("LAC", "LV", "LAC")  // chargers, Raiders, Chargers (LV covers +6.5)
                    "SF"  -> Triple("SF", "MIA", "SF")   // 49ers, Dolphins, 49ers (MIA covers +12.5)
                    "ARI" -> Triple("SEA", "SEA", "SEA") // seahawks, Seahawks, Seahawks (SEA covers -3.5)
                    "DAL" -> Triple("DAL", "WAS", "WAS") // cowboys, Team, Football Team (DAL covers -3.5)
                    "KC"  -> Triple("KC", "KC", "KC")    // chiefs, Chiefs, Chiefs (IND covers +6.5)
                    "LAR" -> Triple("LAR", "LAR", "NYG") // rams, Rams, Giants (LAR covers -7.0)
                    else -> Triple(home, away, home)
                }
                3 -> when (home) {
                    "GB"  -> Triple("ATL", "ATL", "GB")  // Falcons, Falcons, Packers (ATL covers)
                    "CLE" -> Triple("CAR", "CAR", "CAR") // panthers, Panthers, Panthers (CLE covers)
                    "PIT" -> Triple("PIT", "CIN", "CIN") // steelers, Bengals, Bengals (PIT covers)
                    "IND" -> Triple("HOU", "IND", "HOU") // texans, Colts, Texans (IND covers)
                    "MIA" -> Triple("MIA", "KC", "KC")   // dolphins, Chiefs, Chiefs (MIA covers)
                    "BUF" -> Triple("BUF", "BUF", "BUF") // bills, Bills, Bills (BUF covers)
                    "JAX" -> Triple("JAX", "NE", "NE")   // jags, Pats, Pats (NE covers)
                    "DGMT" -> Triple("DGMT", "NYJ", "DGMT") // lions, Jets, Lions (NYJ covers)
                    "WAS" -> Triple("SEA", "WAS", "SEA") // seahawks, Team, Seahawks (WAS covers)
                    "NYG" -> Triple("TEN", "NYG", "TEN") // titans, Giants, Titans (NYG covers)
                    "SF"  -> Triple("SF", "ARI", "SF")   // 49ers, Cardinals, 49ers (ARI covers)
                    "TB"  -> Triple("TB", "MIN", "MIN")  // bucs, Vikings, Vikings (TB covers)
                    "DAL" -> Triple("DAL", "DAL", "BAL") // cowboys, Cowboys, Ravens (DAL covers)
                    "NO"  -> Triple("NO", "LV", "NO")    // saints, Raiders, Saints (LV covers)
                    "DEN" -> Triple("DEN", "LAR", "LAR") // broncos, Rams, Rams (LAR covers)
                    "CHI" -> Triple("PHI", "CHI", "PHI") // eagles, Bears, Eagles (PHI covers)
                    else -> Triple(home, away, home)
                }
                4 -> when (home) {
                    "CLE" -> Triple("CLE", "PIT", "CLE") // Browns, Steelers, Browns (PIT covers)
                    "WAS" -> Triple("IND", "WAS", "WAS") // Colts, Team, Football Team (WAS covers)
                    "NYG" -> Triple("NYG", "NYG", "ARI") // Giants, Giants, Cardinals (NYG covers)
                    "HOU" -> Triple("HOU", "DAL", "DAL") // Texans, Cowboys, Cowboys (Push)
                    "TB"  -> Triple("TB", "TB", "GB")   // Bucs, Bucs, Packers (TB covers)
                    "CIN" -> Triple("JAX", "JAX", "JAX") // Jags, Jags, Jags (CIN covers)
                    "PHI" -> Triple("LAR", "LAR", "PHI") // Rams, Rams, Eagles (LAR covers)
                    "BUF" -> Triple("BUF", "BUF", "BUF") // Bills, Bills, Bills (BUF covers)
                    "CHI" -> Triple("CHI", "CHI", "CHI") // Bears, Bears, Bears (CHI covers)
                    "BAL" -> Triple("TEN", "BAL", "BAL") // Titans, Ravens, Ravens (BAL covers)
                    "MIN" -> Triple("MIN", "MIA", "MIA") // Vikings, Dolphins, Dolphins (MIN covers)
                    "SF"  -> Triple("DEN", "SF", "DEN")  // Broncos, 49ers, Broncos (SF covers)
                    "LV"  -> Triple("KC", "KC", "LV")    // Chiefs, Chiefs, Raiders (LV covers)
                    "SEA" -> Triple("SEA", "LAC", "LAC") // Seahawks, Chargers, Chargers (LAC covers)
                    "CAR" -> Triple("DGMT", "CAR", "DGMT") // Lions, Panthers, Lions (DGMT covers)
                    "NO"  -> Triple("NO", "ATL", "ATL")  // Saints, Falcons, Falcons (NO covers)
                    else -> Triple(home, away, home)
                }
                5 -> when (home) {
                    "DAL" -> Triple("DAL", "TB", "DAL")  // Cowboys, Bucs, Cowboys (DAL covers)
                    "JAX" -> Triple("JAX", "PHI", "PHI") // Jags, Eagles, Eagles (JAX covers)
                    "MIA" -> Triple("MIA", "CIN", "CIN") // Dolphins, Bengals, Bengals (CIN covers)
                    "NYJ" -> Triple("CLE", "NYJ", "CLE") // Browns, Jets, Browns (CLE covers)
                    "TEN" -> Triple("HOU", "TEN", "HOU") // Texans, Titans, Texans (TEN covers)
                    "PIT" -> Triple("IND", "PIT", "IND") // Colts, Steelers, Colts (PIT covers)
                    "NE"  -> Triple("NE", "LV", "LV")    // Pats, Raiders, Raiders (LV covers)
                    "NO"  -> Triple("NO", "NO", "MIN")   // Saints, Saints, Vikings (MIN covers)
                    "WAS" -> Triple("WAS", "WAS", "WAS") // Team, Team, Team (NYG covers)
                    "LAC" -> Triple("DEN", "LAC", "DEN") // Broncos, Chargers, Broncos (LAC covers)
                    "GB"  -> Triple("CHI", "CHI", "CHI") // Bears, Bears, Bears (GB covers)
                    "ARI" -> Triple("ARI", "DGMT", "DGMT") // Cardinals, Lions, Lions (DGMT covers)
                    "SEA" -> Triple("SEA", "SEA", "SF")  // Seahawks, Seahawks, 49ers (SF covers)
                    "ATL" -> Triple("ATL", "ATL", "ATL") // Falcons, Falcons, Falcons (BAL covers)
                    "LAR" -> Triple("LAR", "BUF", "BUF") // Rams, Bills, Bills (BUF covers)
                    else -> Triple(home, away, home)
                }
                6 -> when (home) {
                    "DEN" -> Triple("SEA", "DEN", "SEA") // Seahawks, Broncos, Seahawks (SEA covers)
                    "JAX" -> Triple("JAX", "HOU", "HOU") // Jags, Texans, Texans (JAX covers)
                    "CLE" -> Triple("CLE", "BAL", "CLE") // browns, Ravens, Browns (Push)
                    "PHI" -> Triple("PHI", "PHI", "CAR") // eagles, Eagles, Panthers (Push)
                    "ATL" -> Triple("CHI", "CHI", "ATL") // bears, Bears, Falcons (ATL covers)
                    "NYG" -> Triple("NYG", "NO", "NYG")  // giants, Saints, Giants (NO covers)
                    "NE"  -> Triple("NYJ", "NE", "NE")   // jets, Pats, Patriots (NE covers)
                    "TB"  -> Triple("TB", "PIT", "PIT")  // bucs, Steelers, Steelers (PIT covers)
                    "IND" -> Triple("IND", "TEN", "TEN") // colts, Titans, Titans (IND covers)
                    "LAR" -> Triple("ARI", "LAR", "ARI") // cardinals, Rams, Cardinals (LAR covers)
                    "LV"  -> Triple("BUF", "BUF", "BUF") // bills, Bills, Bills (BUF covers)
                    "KC"  -> Triple("KC", "KC", "KC")    // chiefs, Chiefs, Chiefs (KC covers)
                    "GB"  -> Triple("DAL", "GB", "GB")    // cowboys, Packers, Packers (GB covers)
                    "SF"  -> Triple("SF", "SF", "SF")    // 49ers, 49ers, 49ers (SF covers)
                    else -> Triple(home, away, home)
                }
                7 -> when (home) {
                    "CHI" -> Triple("CHI", "CHI", "CHI") // Bears, Bears, Bears (CHI covers)
                    "NO"  -> Triple("PIT", "NO", "PIT")  // steelers, Saints, steelers (PIT covers)
                    "BAL" -> Triple("CIN", "BAL", "CIN") // bengals, Ravens, Bengals (CIN covers)
                    "TEN" -> Triple("CLE", "CLE", "CLE") // browns, Browns, Browns (CLE covers)
                    "MIN" -> Triple("IND", "MIN", "MIN") // colts, Vikings, Vikings (IND covers)
                    "NYJ" -> Triple("NYJ", "NYJ", "NYJ") // jets, Jets, Jets (NYJ covers)
                    "HOU" -> Triple("HOU", "NYG", "NYG") // texans, Giants, Giants (NYG covers)
                    "ATL" -> Triple("SF", "ATL", "ATL")  // 49ers, Falcons, Falcons (SF covers)
                    "CAR" -> Triple("CAR", "CAR", "CAR") // panthers, Panthers, Panthers (TB covers)
                    "ARI" -> Triple("ARI", "DEN", "DEN") // cardinals, Broncos, Broncos (DEN covers)
                    "DGMT" -> Triple("DGMT", "DGMT", "GB")  // lions, Lions, Packers (GB covers)
                    "LV"  -> Triple("LV", "LAR", "LV")   // raiders, Rams, Raiders (LAR covers)
                    "SEA" -> Triple("SEA", "KC", "KC")   // seahawks, Chiefs, Chiefs (KC covers)
                    "PHI" -> Triple("DAL", "DAL", "DAL") // cowboys, Cowboys, Cowboys (PHI covers)
                    else -> Triple(home, away, home)
                }
                8 -> when (home) {
                    "GB"  -> Triple("CAR", "GB", "CAR")  // Panthers, Packers, Panthers (CAR covers)
                    "DAL" -> Triple("ARI", "DAL", "DAL") // cardinals, Cowboys, Cowboys (ARI covers)
                    "TB"  -> Triple("TB", "ATL", "ATL")  // Bucs, Falcons, Falcons (TB covers)
                    "BUF" -> Triple("BUF", "BUF", "BUF") // Bills, Bills, Bills (BUF covers)
                    "PIT" -> Triple("PIT", "CLE", "CLE") // Steelers, Browns, Browns (PIT covers)
                    "JAX" -> Triple("JAX", "IND", "JAX") // jags, Colts, Jags (JAX covers)
                    "NYJ" -> Triple("NYJ", "LV", "LV")   // jets, Raiders, Raiders (NYJ covers)
                    "DGMT" -> Triple("DGMT", "MIN", "MIN") // lions, Vikings, Vikings (DGMT covers)
                    "CIN" -> Triple("CIN", "CIN", "CIN") // bengals, Bengals, Bengals (TEN covers)
                    "LAR" -> Triple("LAC", "LAR", "LAR") // chargers, Rams, Rams (LAR covers)
                    "DEN" -> Triple("KC", "KC", "DEN")   // chiefs, Chiefs, Broncos (KC covers)
                    "MIA" -> Triple("MIA", "NE", "NE")   // dolphins, Pats, Patriots (NE covers)
                    "WAS" -> Triple("WAS", "WAS", "PHI") // team, Team, Eagles (PHI covers)
                    "SEA" -> Triple("CHI", "SEA", "CHI") // bears, Seahawks, Bears (SEA covers)
                    else -> Triple(home, away, home)
                }
                else -> Triple(away, home, away)
            }

            picks.add(SpreadPick(id = pickIdCounter++, gameId = game.id, playerId = cliveId, weekNumber = week, pickedTeamShort = cPick))
            picks.add(SpreadPick(id = pickIdCounter++, gameId = game.id, playerId = markId, weekNumber = week, pickedTeamShort = mPick))
            picks.add(SpreadPick(id = pickIdCounter++, gameId = game.id, playerId = conorId, weekNumber = week, pickedTeamShort = coPick))
        }

        return picks
    }
}
