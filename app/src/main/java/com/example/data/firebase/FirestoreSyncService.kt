package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.NflGame
import com.example.data.model.SpreadPick
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreSyncService(context: Context) {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(
        context.applicationContext.getString(R.string.firestore_database_id)
    )

    private val auth = Firebase.auth

    fun isUserSignedIn(): Boolean = auth.currentUser != null

    fun getCurrentUser() = auth.currentUser

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

    suspend fun getWhitelistedEmails(): List<String> {
        return try {
            val snap = db.collection("whitelist").get().await()
            snap.documents.mapNotNull { it.getString("email") }
        } catch (e: Exception) {
            emptyList()
        }
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
        if (!isUserSignedIn()) return
        try {
            val doc = db.collection("games").document(gameId.toString())
            doc.update(updates).await()
        } catch (e: Exception) {
            val doc = db.collection("games").document(gameId.toString())
            doc.set(updates, SetOptions.merge()).await()
        }
    }

    suspend fun uploadPick(pick: SpreadPick, playerName: String) {
        val uid = auth.currentUser?.uid ?: return
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
        if (auth.currentUser == null) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }

        val registration = db.collection("picks")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("FirestoreSync", "Listen error: ${error.message}", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            val gameId = (doc.get("gameId") as? String)?.toLongOrNull() ?: (doc.get("gameId") as? Number)?.toLong() ?: return@mapNotNull null
                            val playerId = (doc.get("playerId") as? String)?.toLongOrNull() ?: (doc.get("playerId") as? Number)?.toLong() ?: return@mapNotNull null
                            val week = (doc.get("weekNumber") as? Number)?.toInt() ?: 10
                            val team = doc.getString("pickedTeamShort") ?: return@mapNotNull null
                            SpreadPick(
                                id = doc.id.hashCode().toLong(),
                                gameId = gameId,
                                playerId = playerId,
                                weekNumber = week,
                                pickedTeamShort = team
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(list)
                }
            }
        awaitClose { registration.remove() }
    }
}
