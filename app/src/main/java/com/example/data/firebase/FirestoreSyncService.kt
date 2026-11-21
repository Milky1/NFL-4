package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.db.AppDatabase
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
        try {
            // 1. Sync games safely
            val gamesSnapshot = db.collection("games").get().await()
            Log.d("SYNC", "Firestore games: ${gamesSnapshot.size()}")
            val games = gamesSnapshot.documents.mapNotNull { doc ->
                val gameId = doc.id.toLongOrNull() ?: doc.getString("id")?.toLongOrNull() ?: return@mapNotNull null
                NflGame(
                    id = gameId,
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
                    awayScore = doc.getLong("awayScore")?.toInt()?.takeIf { it >= 0 },
                    homeScore = doc.getLong("homeScore")?.toInt()?.takeIf { it >= 0 },
                    isFinal = doc.getBoolean("isFinal") ?: false
                )
            }
            database.nflGameDao().insertAll(games)

            // 2. Sync picks with deterministic IDs to prevent duplicates on restart
            val picksSnapshot = db.collection("picks").get().await()
            Log.d("SYNC", "Firestore picks: ${picksSnapshot.size()}")
            val picks = picksSnapshot.documents.mapNotNull { doc ->
                val gameId = (doc.get("gameId") as? Number)?.toLong()
                    ?: doc.getString("gameId")?.toLongOrNull()
                    ?: return@mapNotNull null
                val playerId = (doc.get("playerId") as? Number)?.toLong()
                    ?: doc.getString("playerId")?.toLongOrNull()
                    ?: return@mapNotNull null
                val weekNumber = (doc.get("weekNumber") as? Number)?.toInt() ?: 1
                val pickedTeamShort = doc.getString("pickedTeamShort") ?: ""
                val canonicalDocId = "w${weekNumber}_g${gameId}_p${playerId}"

                SpreadPick(
                    id = canonicalDocId.hashCode().toLong(),
                    gameId = gameId,
                    playerId = playerId,
                    weekNumber = weekNumber,
                    pickedTeamShort = pickedTeamShort,
                    timestamp = doc.getLong("timestamp") ?: 0L,
                    firstPickTeamShort = doc.getString("firstPickTeamShort") ?: pickedTeamShort
                )
            }
            database.spreadPickDao().insertAll(picks)
            Log.d("SYNC", "syncFromFirestore DONE")
        } catch (e: Exception) {
            Log.e("SYNC", "syncFromFirestore FAILED: ${e.message}", e)
        }
    }

    suspend fun uploadPick(pick: SpreadPick, playerName: String) {
        Log.d("FIRESTORE", "uploadPick: $playerName")
        val uid = auth.currentUser?.uid ?: "anonymous"
        val pickDocId = "w${pick.weekNumber}_g${pick.gameId}_p${pick.playerId}"
        val doc = db.collection("picks").document(pickDocId)
        val data = mapOf(
            "id" to pickDocId,
            "gameId" to pick.gameId.toString(),
            "playerId" to pick.playerId.toString(),
            "playerName" to playerName,
            "weekNumber" to pick.weekNumber,
            "pickedTeamShort" to pick.pickedTeamShort,
            "firstPickTeamShort" to pick.firstPickTeamShort.ifBlank { pick.pickedTeamShort },
            "timestamp" to pick.timestamp,
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

                if (snapshot != null && !snapshot.metadata.hasPendingWrites()) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            val gameIdRaw = doc.get("gameId")
                            val gameId = when (gameIdRaw) {
                                is String -> gameIdRaw.toLongOrNull()
                                is Number -> gameIdRaw.toLong()
                                else -> null
                            } ?: return@mapNotNull null

                            val playerIdRaw = doc.get("playerId")
                            val playerId = when (playerIdRaw) {
                                is String -> playerIdRaw.toLongOrNull()
                                is Number -> playerIdRaw.toLong()
                                else -> null
                            } ?: return@mapNotNull null

                            val week = (doc.get("weekNumber") as? Number)?.toInt()
                                ?: (doc.getString("weekNumber"))?.toIntOrNull() ?: 1
                            val team = doc.getString("pickedTeamShort") ?: return@mapNotNull null

                            val canonicalDocId = "w${week}_g${gameId}_p${playerId}"
                            val uniqueId = canonicalDocId.hashCode().toLong()

                            SpreadPick(
                                id = uniqueId,
                                gameId = gameId,
                                playerId = playerId,
                                weekNumber = week,
                                pickedTeamShort = team,
                                timestamp = doc.getLong("timestamp") ?: 0L,
                                firstPickTeamShort = doc.getString("firstPickTeamShort") ?: team
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

                if (snapshot != null && !snapshot.metadata.hasPendingWrites()) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            val id = (doc.get("id") as? Number)?.toLong()
                                ?: doc.getString("id")?.toLongOrNull()
                                ?: doc.id.toLongOrNull()
                                ?: return@mapNotNull null
                            val weekNumber = (doc.get("weekNumber") as? Number)?.toInt() ?: 1
                            val seasonYear = (doc.get("seasonYear") as? Number)?.toInt() ?: 2026
                            val awayTeamShort = doc.getString("awayTeamShort") ?: ""
                            val awayTeam = doc.getString("awayTeam") ?: ""
                            val homeTeamShort = doc.getString("homeTeamShort") ?: ""
                            val homeTeam = doc.getString("homeTeam") ?: ""
                            val homeSpread = doc.getDouble("homeSpread") ?: 0.0
                            val sportsbookProvider = doc.getString("sportsbookProvider") ?: ""
                            val gameDate = doc.getString("gameDate") ?: ""
                            val kickoffTime = doc.getString("kickoffTime") ?: ""
                            val tvNetwork = doc.getString("tvNetwork") ?: ""
                            val venue = doc.getString("venue") ?: ""
                            val awayScore = (doc.get("awayScore") as? Number)?.toInt()?.takeIf { it >= 0 }
                            val homeScore = (doc.get("homeScore") as? Number)?.toInt()?.takeIf { it >= 0 }
                            val isFinal = doc.getBoolean("isFinal") ?: false

                            NflGame(
                                id = id,
                                weekNumber = weekNumber,
                                seasonYear = seasonYear,
                                awayTeamShort = awayTeamShort,
                                awayTeam = awayTeam,
                                homeTeamShort = homeTeamShort,
                                homeTeam = homeTeam,
                                homeSpread = homeSpread,
                                sportsbookProvider = sportsbookProvider,
                                gameDate = gameDate,
                                kickoffTime = kickoffTime,
                                tvNetwork = tvNetwork,
                                venue = venue,
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
