package com.msbcreativestudios.sudokuchallenge.data

import android.util.Log
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
    private const val TAG = "GeminiClient"
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun isKeyInvalid(key: String): Boolean {
        return key.isBlank() || 
               key.contains("placeholder", ignoreCase = true) || 
               key.contains("your_", ignoreCase = true) || 
               key == "GEMINI_API_KEY_DEFAULT_VALUE"
    }

    suspend fun getSudokuAnalysis(apiKey: String, statsPrompt: String, localFallbackReport: String): String = withContext(Dispatchers.IO) {
        if (isKeyInvalid(apiKey)) {
            Log.d(TAG, "getSudokuAnalysis: Invalid/placeholder key. Returning offline fallback.")
            return@withContext localFallbackReport
        }
        
        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            
            val requestBodyJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", statsPrompt)
                            })
                        })
                    })
                })
            }
            
            val body = requestBodyJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()
                
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "getSudokuAnalysis call failed: HTTP code ${response.code}")
                    return@withContext localFallbackReport
                }
                
                val bodyStr = response.body?.string() ?: return@withContext localFallbackReport
                val jsonResponse = JSONObject(bodyStr)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val contentObj = candidate.optJSONObject("content")
                    if (contentObj != null) {
                        val parts = contentObj.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val textValue = parts.getJSONObject(0).optString("text")
                            if (textValue.isNotBlank()) {
                                return@withContext textValue
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in getSudokuAnalysis", e)
        }
        
        return@withContext localFallbackReport
    }

    suspend fun getCertificateEndorsement(
        apiKey: String,
        userName: String,
        gridSize: Int,
        difficulty: String,
        durationSeconds: Long,
        synapticSpeed: Double,
        focusRating: Double,
        globalPercentile: Double,
        localFallbackReport: String
    ): String = withContext(Dispatchers.IO) {
        if (isKeyInvalid(apiKey)) {
            Log.d(TAG, "getCertificateEndorsement: Invalid/placeholder key. Returning offline fallback.")
            return@withContext localFallbackReport
        }
        
        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val prompt = "Generate a concise 1-sentence high-prestige analytical endorsement certificate testimonial for $userName who solved a ${gridSize}x${gridSize} Sudoku matrix on $difficulty level in $durationSeconds seconds with a synaptic speed of ${String.format(java.util.Locale.US, "%.2f", synapticSpeed)}Hz (Focus: ${String.format(java.util.Locale.US, "%.1f", focusRating)}%, Percentile: ${String.format(java.util.Locale.US, "%.3f", globalPercentile)}%). Keep it elite, futuristic, and professional. Match the style of an official cognitive board audit."
            
            val requestBodyJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
            }
            
            val body = requestBodyJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()
                
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "getCertificateEndorsement failed: HTTP code ${response.code}")
                    return@withContext localFallbackReport
                }
                
                val bodyStr = response.body?.string() ?: return@withContext localFallbackReport
                val jsonResponse = JSONObject(bodyStr)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val contentObj = candidate.optJSONObject("content")
                    if (contentObj != null) {
                        val parts = contentObj.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val textValue = parts.getJSONObject(0).optString("text")
                            if (textValue.isNotBlank()) {
                                return@withContext textValue.trim()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in getCertificateEndorsement", e)
        }
        
        return@withContext localFallbackReport
    }
}
