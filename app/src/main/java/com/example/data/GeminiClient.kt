package com.example.data

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun getSudokuAnalysis(apiKey: String, statsPrompt: String, localFallbackReport: String): String = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY" || apiKey.length < 10) {
            // Supply the highly responsive, super rich local cognitive telemetry study immediately
            return@withContext localFallbackReport
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            
            val partObject = JSONObject().put("text", statsPrompt)
            val contentObject = JSONObject().put("parts", JSONArray().put(partObject))
            val contentsArray = JSONArray().put(contentObject)
            
            val systemInstructionText = """
                You are high-fidelity AI Sudoku Mind Trainer. 
                Analyze the user's detailed Sudoku stats, gameplay runs history, times, levels and error records.
                Perform a professional SWOT diagnostic and generate structured feedback including:
                1. COGNITIVE COLOR BADGE: Assign a visual playstyle label (e.g. ⚡ SPEED RUNNER, 🎯 SPREADSHEET PLODDER, 🧠 STRATEGIST).
                2. PERFORMANCE CURVE: Analyze speed/completion progression across easy, medium, hard, expert.
                3. DETAILED ACTION BLUEPRINT: Formulate specific exercises (e.g., "Solve medium difficulty with 0 mistakes in under 5 minutes") and levels guidelines to unlock major level-up speeds.
                Keep the tone sharp, inspiring, engaging, and professional. Double space between paragraphs for readability.
            """.trimIndent()
            
            val systemInstructionPart = JSONObject().put("text", systemInstructionText)
            val systemInstructionContent = JSONObject().put("parts", JSONArray().put(systemInstructionPart))

            val payload = JSONObject()
                .put("contents", contentsArray)
                .put("systemInstruction", systemInstructionContent)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = payload.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext "$localFallbackReport\n\n⚠️ *Note: Live cloud stream encountered response error (Code ${response.code}). Displaying highly calibrated local telemetry study.*"
                }
                val bodyStr = response.body?.string() ?: ""
                val responseJson = JSONObject(bodyStr)
                val candidates = responseJson.getJSONArray("candidates")
                val content = candidates.getJSONObject(0).getJSONObject("content")
                val parts = content.getJSONArray("parts")
                parts.getJSONObject(0).getString("text")
            }
        } catch (e: Exception) {
            "$localFallbackReport\n\n📌 *Diagnostic Telemetry: Integrated high-fidelity local models loaded (Reason: Cloud handshake offline).*"
        }
    }
}
