package com.example.data.firebase

import android.content.Context
import com.example.data.db.AppDatabase
import android.util.Log
import kotlinx.coroutines.flow.first
import com.example.R
import com.example.data.model.NflGame
import com.example.data.model.SpreadPick
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await

class FirestoreSyncService(context: Context) {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(
        context.applicationContext.getString(R.string.firestore_database_id)
    )

    private val auth = Firebase.auth
    private val database = AppDatabase.getDatabase(context.applicationContext)

    fun isUserSignedIn(): Boolean = auth.currentUser != null

    suspend fun saveUserProfile(displayName: String) {
        val user = auth.currentUser ?: return
        val userDoc = db.collection("users").document(user.uid)
        val data = mapOf(
            "userId" to user.uid,
            "displayName" to displayName.ifBlank { user.displayName ?: "Rival Player" },
            "email" to (user.email ?: ""),
            "createdAt" to FieldValue.serverTimestamp()
        )
        userDoc.set(data, SetOptions.merge()).await()
    }

    suspend fun addEmailToWhitelist(email: String, role: String = "player") {
        if (!isUserSignedIn() || email.isBlank()) return
        val cleanEmail = email.trim().lowercase()
        val doc = db.collection("whitelist").document(cleanEmail)
        val data = mapOf(
            "email" to cleanEmail,
            "role" to role,
            "addedAt" to FieldValue.serverTimestamp()
        )
        doc.set(data, SetOptions.merge()).await()
    }

    suspend fun uploadGame(game: NflGame) {
        if (!isUserSignedIn()) return
        val doc = db.collection("games").document(game.id.toString())
        val data = mapOf(
            "id" to game.id.toString(),
            "weekNumber" to game.weekNumber,
            "awayTeamShort" to game.awayTeamShort,
            "awayTeam" to game.awayTeam,
            "homeTeamShort" to game.homeTeamShort,
            "homeTeam" to game.homeTeam,
            "homeSpread" to game.homeSpread,
            "sportsbookProvider" to game.sportsbookProvider,
            "gameDate" to game.gameDate,
            "kickoffTime" to game.kickoffTime,
            "tvNetwork" to game.tvNetwork,
            "awayScore" to (game.awayScore ?: -1),
            "homeScore" to (game.homeScore ?: -1),
            "isFinal" to game.isFinal
        )
        doc.set(data, SetOptions.merge()).await()
    }

    suspend fun updateGameDocument(gameId: Long, updates: Map<String, Any>) {
        Log.d("FIRESTORE", "updateGameDocument: $gameId")
        try {
            db.collection("games").document(gameId.toString())
                .set(updates, SetOptions.merge())
                .await()
            Log.d("FIRESTORE", "SUCCESS: $gameId")
        } catch (e: Exception) {
            Log.e("FIRESTORE", "FAILED: $gameId", e)
        }
    }

    suspend fun saveGamesToCloud(games: List<NflGame>) {
        try {
            games.forEach { game ->
                val data = mapOf(
                    "id" to game.id.toString(),
                    "weekNumber" to game.weekNumber,
                    "seasonYear" to game.seasonYear,
                    "awayTeamShort" to game.awayTeamShort,
                    "awayTeam" to game.awayTeam,
                    "homeTeamShort" to game.homeTeamShort,
                    "homeTeam" to game.homeTeam,
                    "homeSpread" to game.homeSpread,
                    "sportsbookProvider" to game.sportsbookProvider,
                    "gameDate" to game.gameDate,
                    "kickoffTime" to game.kickoffTime,
                    "tvNetwork" to game.tvNetwork,
                    "venue" to game.venue,
                    "awayScore" to (game.awayScore ?: -1),
                    "homeScore" to (game.homeScore ?: -1),
                    "isFinal" to game.isFinal
                )
                db.collection("games").document(game.id.toString())
                    .set(data, SetOptions.merge())
                    .await()
            }
            Log.d("FIRESTORE", "Successfully synced ${games.size} games to cloud")
        } catch (e: Exception) {
            Log.e("FIRESTORE", "Failed to save games to cloud: ${e.message}", e)
        }
    }
    suspend fun syncFromFirestore() {
        Log.d("SYNC", "syncFromFirestore START")
        // Sync games
        val gamesSnapshot = db.collection("games").get().await()
        Log.d("SYNC", "Firestore games: ${gamesSnapshot.size()}")
        val games = gamesSnapshot.documents.map { doc ->
            NflGame(
                id = doc.id.toLong(),
                weekNumber = doc.getLong("weekNumber")?.toInt() ?: 1,
                seasonYear = doc.getLong("seasonYear")?.toInt() ?: 2026,
                awayTeam = doc.getString("awayTeam") ?: "",
                awayTeamShort = doc.getString("awayTeamShort") ?: "",
                homeTeam = doc.getString("homeTeam") ?: "",
                homeTeamShort = doc.getString("homeTeamShort") ?: "",
                homeSpread = doc.getDouble("homeSpread") ?: 0.0,
                kickoffTime = doc.getString("kickoffTime") ?: "Sun 1:00 PM",
                gameDate = doc.getString("gameDate") ?: "",
                tvNetwork = doc.getString("tvNetwork") ?: "CBS",
                venue = doc.getString("venue") ?: "",
                sportsbookProvider = doc.getString("sportsbookProvider") ?: "DraftKings",
                awayScore = doc.getLong("awayScore")?.toInt(),
                homeScore = doc.getLong("homeScore")?.toInt(),
                isFinal = doc.getBoolean("isFinal") ?: false
            )
        }
        database.nflGameDao().insertAll(games)
        val verifyGames = database.nflGameDao().getAllGames().first()
        Log.d("SYNC", "Room games after sync: ${verifyGames.size}")
        Log.d("SYNC", "Room game scores (week 9): ${verifyGames.filter { it.weekNumber == 9 }.map { "${it.homeScore}-${it.awayScore}" }}")
        // Write into Room (adjust DAO name/method to match yours)
        // database.nflGameDao().insertAll(games)

// Sync picks
        val picksSnapshot = db.collection("picks").get().await()
        Log.d("SYNC", "Firestore picks: ${picksSnapshot.size()}")
        val picks = picksSnapshot.documents.map { doc ->
            SpreadPick(
                id = 0,
                gameId = doc.getString("gameId")?.toLong() ?: 0L,
                playerId = doc.getString("playerId")?.toLong() ?: 0L,
                weekNumber = doc.getLong("weekNumber")?.toInt() ?: 1,
                pickedTeamShort = doc.getString("pickedTeamShort") ?: "",
                timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                firstPickTeamShort = doc.getString("firstPickTeamShort") ?: doc.getString("pickedTeamShort") ?: ""
            )
        }
        database.spreadPickDao().insertAll(picks)
        val verify = database.spreadPickDao().getAllPicks().first()
        Log.d("SYNC", "Room picks after sync: ${verify.size}")
        Log.d("SYNC", "Room pick weeks: ${verify.map { it.weekNumber }}")
        Log.d("SYNC", "Room pick playerIds: ${verify.map { it.playerId }}")
        Log.d("SYNC", "Room pick gameIds: ${verify.map { it.gameId }}")
        Log.d("SYNC", "Picks written to Room: ${picks.size}")

        Log.d("SYNC", "syncFromFirestore DONE")
    }
    suspend fun uploadPick(pick: SpreadPick, playerName: String) {
        Log.d("FIRESTORE", "uploadPick: $playerName")
        val uid = auth.currentUser?.uid ?: "anonymous"  // ← use a fallback instead of returning
        val pickDocId = "w${pick.weekNumber}_g${pick.gameId}_p${pick.playerId}"
        val doc = db.collection("picks").document(pickDocId)
        val data = mapOf(
            "id" to pickDocId,
            "gameId" to pick.gameId.toString(),
            "playerId" to pick.playerId.toString(),
            "playerName" to playerName,
            "weekNumber" to pick.weekNumber,
            "pickedTeamShort" to pick.pickedTeamShort,
            "userId" to uid,
            "createdAt" to FieldValue.serverTimestamp()
        )
        doc.set(data, SetOptions.merge()).await()
    }

    fun observeCloudPicks(): Flow<List<SpreadPick>> = callbackFlow {
        val registration = db.collection("picks")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("FirestoreSync", "Picks listen error: ${error.message}", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            // Extract gameId regardless of whether Firestore stored it as String or Number
                            val gameIdRaw = doc.get("gameId")
                            val gameId = when (gameIdRaw) {
                                is String -> gameIdRaw.toLongOrNull()
                                is Number -> gameIdRaw.toLong()
                                else -> null
                            } ?: return@mapNotNull null

                            // Extract playerId regardless of whether Firestore stored it as String or Number
                            val playerIdRaw = doc.get("playerId")
                            val playerId = when (playerIdRaw) {
                                is String -> playerIdRaw.toLongOrNull()
                                is Number -> playerIdRaw.toLong()
                                else -> null
                            } ?: return@mapNotNull null

                            val week = (doc.get("weekNumber") as? Number)?.toInt()
                                ?: (doc.getString("weekNumber"))?.toIntOrNull() ?: 1
                            val team = doc.getString("pickedTeamShort") ?: return@mapNotNull null

                            // Use document ID hash or construct a stable numeric ID
                            val uniqueId = doc.id.hashCode().toLong()

                            SpreadPick(
                                id = uniqueId,
                                gameId = gameId,
                                playerId = playerId,
                                weekNumber = week,
                                pickedTeamShort = team
                            )
                        } catch (e: Exception) {
                            Log.e("FirestoreSync", "Failed to parse pick doc: ${doc.id}", e)
                            null
                        }
                    }
                    trySend(list)
                }
            }
        awaitClose { registration.remove() }
    }.distinctUntilChanged()
    fun observeCloudGames(): Flow<List<NflGame>> = callbackFlow {
        val registration = db.collection("games")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("FirestoreSync", "Games listen error: ${error.message}", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            val id = (doc.get("id") as? Number)?.toLong()
                                ?: doc.getString("id")?.toLongOrNull()
                                ?: doc.id.toLongOrNull()
                                ?: return@mapNotNull null
                            val weekNumber = (doc.get("weekNumber") as? Number)?.toInt() ?: 1
                            val awayTeamShort = doc.getString("awayTeamShort") ?: ""
                            val awayTeam = doc.getString("awayTeam") ?: ""
                            val homeTeamShort = doc.getString("homeTeamShort") ?: ""
                            val homeTeam = doc.getString("homeTeam") ?: ""
                            val homeSpread = doc.getDouble("homeSpread") ?: 0.0
                            val sportsbookProvider = doc.getString("sportsbookProvider") ?: ""
                            val gameDate = doc.getString("gameDate") ?: ""
                            val kickoffTime = doc.getString("kickoffTime") ?: ""
                            val tvNetwork = doc.getString("tvNetwork") ?: ""
                            val awayScore = (doc.get("awayScore") as? Number)?.toInt()?.takeIf { it >= 0 }
                            val homeScore = (doc.get("homeScore") as? Number)?.toInt()?.takeIf { it >= 0 }
                            val isFinal = doc.getBoolean("isFinal") ?: false

                            NflGame(
                                id = id,
                                weekNumber = weekNumber,
                                awayTeamShort = awayTeamShort,
                                awayTeam = awayTeam,
                                homeTeamShort = homeTeamShort,
                                homeTeam = homeTeam,
                                homeSpread = homeSpread,
                                sportsbookProvider = sportsbookProvider,
                                gameDate = gameDate,
                                kickoffTime = kickoffTime,
                                tvNetwork = tvNetwork,
                                awayScore = awayScore,
                                homeScore = homeScore,
                                isFinal = isFinal
                            )
                        } catch (e: Exception) {
                            Log.e("FirestoreSync", "Failed to parse game doc: ${doc.id}", e)
                            null
                        }
                    }
                    trySend(list)
                }
            }
        awaitClose { registration.remove() }
    }.distinctUntilChanged()
}
