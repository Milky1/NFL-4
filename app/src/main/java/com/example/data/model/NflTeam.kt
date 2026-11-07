package com.example.data.model

import androidx.compose.ui.graphics.Color

data class NflTeam(
    val code: String,
    val name: String,
    val city: String,
    val primaryColor: Color,
    val secondaryColor: Color
) {
    val fullName: String get() = "$city $name"
}

object NflTeams {
    val teams = listOf(
        NflTeam("ARI", "Cardinals", "Arizona", Color(0xFF97233F), Color(0xFFFFB612)),
        NflTeam("ATL", "Falcons", "Atlanta", Color(0xFFA71930), Color(0xFF000000)),
        NflTeam("BAL", "Ravens", "Baltimore", Color(0xFF241773), Color(0xFF9E7C0C)),
        NflTeam("BUF", "Bills", "Buffalo", Color(0xFF00338D), Color(0xFFC60C30)),
        NflTeam("CAR", "Panthers", "Carolina", Color(0xFF0085CA), Color(0xFF101820)),
        NflTeam("CHI", "Bears", "Chicago", Color(0xFF0B162A), Color(0xFFC83803)),
        NflTeam("CIN", "Bengals", "Cincinnati", Color(0xFFFB4F14), Color(0xFF000000)),
        NflTeam("CLE", "Browns", "Cleveland", Color(0xFF311D00), Color(0xFFFF3C00)),
        NflTeam("DAL", "Cowboys", "Dallas", Color(0xFF003594), Color(0xFF041E42)),
        NflTeam("DEN", "Broncos", "Denver", Color(0xFFFB4F14), Color(0xFF002244)),
        NflTeam("DET", "Lions", "Detroit", Color(0xFF0076B6), Color(0xFFB0B7BC)),
        NflTeam("GB", "Packers", "Green Bay", Color(0xFF203731), Color(0xFFFFB612)),
        NflTeam("HOU", "Texans", "Houston", Color(0xFF03202F), Color(0xFFA71930)),
        NflTeam("IND", "Colts", "Indianapolis", Color(0xFF002C5F), Color(0xFFA2AAAD)),
        NflTeam("JAX", "Jaguars", "Jacksonville", Color(0xFF006778), Color(0xFFD7A22A)),
        NflTeam("KC", "Chiefs", "Kansas City", Color(0xFFE31837), Color(0xFFFFB81C)),
        NflTeam("LV", "Raiders", "Las Vegas", Color(0xFF000000), Color(0xFFA5ACAF)),
        NflTeam("LAC", "Chargers", "Los Angeles", Color(0xFF0080C6), Color(0xFFFFC20E)),
        NflTeam("LAR", "Rams", "Los Angeles", Color(0xFF003594), Color(0xFFFFA300)),
        NflTeam("MIA", "Dolphins", "Miami", Color(0xFF008E97), Color(0xFFFC4C02)),
        NflTeam("MIN", "Vikings", "Minnesota", Color(0xFF4F2683), Color(0xFFFFC62F)),
        NflTeam("NE", "Patriots", "New England", Color(0xFF002244), Color(0xFFC60C30)),
        NflTeam("NO", "Saints", "New Orleans", Color(0xFFD3BC8D), Color(0xFF101820)),
        NflTeam("NYG", "Giants", "New York", Color(0xFF0B2265), Color(0xFFA71930)),
        NflTeam("NYJ", "Jets", "New York", Color(0xFF125740), Color(0xFF000000)),
        NflTeam("PHI", "Eagles", "Philadelphia", Color(0xFF004C54), Color(0xFFA5ACAF)),
        NflTeam("PIT", "Steelers", "Pittsburgh", Color(0xFFFFB612), Color(0xFF101820)),
        NflTeam("SF", "49ers", "San Francisco", Color(0xFFAA0000), Color(0xFFB3995D)),
        NflTeam("SEA", "Seahawks", "Seattle", Color(0xFF002244), Color(0xFF69BE28)),
        NflTeam("TB", "Buccaneers", "Tampa Bay", Color(0xFFD50A0A), Color(0xFF0A0A08)),
        NflTeam("TEN", "Titans", "Tennessee", Color(0xFF0C2340), Color(0xFF4B92DB)),
        NflTeam("WAS", "Commanders", "Washington", Color(0xFF5A1414), Color(0xFFFFB612))
    )

    private val teamsMap = teams.associateBy { it.code }

    // Multi-name and alias dictionary
    private val aliasMap: Map<String, String> = mapOf(
        // Commanders / Washington Football Team / Team
        "TEAM" to "WAS",
        "FOOTBALL TEAM" to "WAS",
        "WASHINGTON FOOTBALL TEAM" to "WAS",
        "WASHINGTON" to "WAS",
        "COMMANDERS" to "WAS",
        "WFT" to "WAS",
        "WSH" to "WAS",
        "REDSKINS" to "WAS",

        // Buccaneers / Bucs / Bucaneers
        "BUCS" to "TB",
        "BUCANEERS" to "TB",
        "BUCCANEERS" to "TB",
        "TAMPA" to "TB",
        "TAMPA BAY" to "TB",
        "TBB" to "TB",

        // Jaguars / Jags
        "JAGS" to "JAX",
        "JAC" to "JAX",
        "JACKSONVILLE" to "JAX",
        "JAGUARS" to "JAX",

        // Patriots / Pats
        "PATS" to "NE",
        "PATRIOTS" to "NE",
        "NEW ENGLAND" to "NE",

        // 49ers / Niners
        "NINERS" to "SF",
        "49ERS" to "SF",
        "SAN FRANCISCO" to "SF",

        // Dolphins / Phins
        "PHINS" to "MIA",
        "DOLPHINS" to "MIA",
        "MIAMI" to "MIA",

        // Vikings / Vikes
        "VIKES" to "MIN",
        "VIKINGS" to "MIN",
        "MINNESOTA" to "MIN",

        // Cardinals / Cards
        "CARDS" to "ARI",
        "CARDINALS" to "ARI",
        "ARIZONA" to "ARI",

        // Chiefs
        "CHIEFS" to "KC",
        "KANSAS CITY" to "KC",
        "KAN" to "KC",

        // Packers
        "PACKERS" to "GB",
        "GREEN BAY" to "GB",

        // Raiders
        "RAIDERS" to "LV",
        "LAS VEGAS" to "LV",
        "OAKLAND" to "LV",
        "LVR" to "LV",
        "OAK" to "LV",

        // Chargers
        "CHARGERS" to "LAC",
        "SAN DIEGO" to "LAC",
        "LA CHARGERS" to "LAC",
        "LOS ANGELES CHARGERS" to "LAC",
        "SD" to "LAC",

        // Rams
        "RAMS" to "LAR",
        "LA RAMS" to "LAR",
        "LOS ANGELES RAMS" to "LAR",
        "ST. LOUIS" to "LAR",
        "LA" to "LAR",

        // Giants
        "GIANTS" to "NYG",
        "NY GIANTS" to "NYG",

        // Jets
        "JETS" to "NYJ",
        "NY JETS" to "NYJ",

        // Steelers
        "STEELERS" to "PIT",
        "PITTSBURGH" to "PIT",

        // Ravens
        "RAVENS" to "BAL",
        "BALTIMORE" to "BAL",

        // Browns
        "BROWNS" to "CLE",
        "CLEVELAND" to "CLE",

        // Bengals
        "BENGALS" to "CIN",
        "CINCINNATI" to "CIN",

        // Bills
        "BILLS" to "BUF",
        "BUFFALO" to "BUF",

        // Texans
        "TEXANS" to "HOU",
        "HOUSTON" to "HOU",

        // Colts
        "COLTS" to "IND",
        "INDIANAPOLIS" to "IND",

        // Titans
        "TITANS" to "TEN",
        "TENNESSEE" to "TEN",

        // Broncos
        "BRONCOS" to "DEN",
        "DENVER" to "DEN",

        // Cowboys
        "COWBOYS" to "DAL",
        "DALLAS" to "DAL",

        // Eagles
        "EAGLES" to "PHI",
        "PHILADELPHIA" to "PHI",

        // Falcons
        "FALCONS" to "ATL",
        "ATLANTA" to "ATL",

        // Panthers
        "PANTHERS" to "CAR",
        "CAROLINA" to "CAR",

        // Saints
        "SAINTS" to "NO",
        "NEW ORLEANS" to "NO",

        // Bears
        "BEARS" to "CHI",
        "CHICAGO" to "CHI",

        // Lions
        "LIONS" to "DET",
        "DETROIT" to "DET",

        // Seahawks
        "SEAHAWKS" to "SEA",
        "SEATTLE" to "SEA"
    )

    fun getByCode(code: String): NflTeam {
        val normalized = code.trim().uppercase()
        val direct = teamsMap[normalized]
        if (direct != null) return direct

        val aliasCode = aliasMap[normalized]
        if (aliasCode != null) {
            val aliasedTeam = teamsMap[aliasCode]
            if (aliasedTeam != null) return aliasedTeam
        }

        // Fuzzy match by name, city or full name
        val found = teams.firstOrNull {
            it.name.equals(normalized, ignoreCase = true) ||
            it.city.equals(normalized, ignoreCase = true) ||
            it.fullName.equals(normalized, ignoreCase = true)
        }
        if (found != null) return found

        return NflTeam(
            code = code,
            name = code,
            city = "",
            primaryColor = Color(0xFF334155),
            secondaryColor = Color(0xFF64748B)
        )
    }

    fun getByCodeOrName(input: String): NflTeam = getByCode(input)
}
