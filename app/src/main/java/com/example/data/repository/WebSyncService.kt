package com.example.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object WebSyncService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun fetchCsvFromUrl(url: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            var targetUrl = url.trim()
            if (!targetUrl.startsWith("http://") && !targetUrl.startsWith("https://")) {
                targetUrl = "https://$targetUrl"
            }

            // Convert standard Google Docs edit link into published export link if user pasted normal sheet link
            // e.g. https://docs.google.com/spreadsheets/d/SHEET_ID/edit#gid=0 -> https://docs.google.com/spreadsheets/d/SHEET_ID/export?format=csv
            if (targetUrl.contains("docs.google.com/spreadsheets/d/") && !targetUrl.contains("export?format=csv")) {
                val sheetId = targetUrl.substringAfter("docs.google.com/spreadsheets/d/").substringBefore("/")
                targetUrl = "https://docs.google.com/spreadsheets/d/$sheetId/export?format=csv"
            }

            val request = Request.Builder()
                .url(targetUrl)
                .header("User-Agent", "Mozilla/5.0 (Android; SpreadRivals/1.0)")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP error ${response.code}: Make sure Google Sheet is shared with 'Anyone with the link can view' or published to web"))
            }

            val body = response.body?.string()
            if (body.isNullOrBlank()) {
                return@withContext Result.failure(Exception("Empty response from spreadsheet URL"))
            }

            Result.success(body)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
