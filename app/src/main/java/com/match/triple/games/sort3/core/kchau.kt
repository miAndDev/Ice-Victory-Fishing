package com.match.triple.games.sort3.core

import android.util.Log
import com.anor.security.StringShield
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

@StringShield
class Kch() {
    suspend fun kchau(request: PostRequest): Result<Unit> = withContext(IO) {
        Log.d("LINK_DATA", "chau started $request")
        try {

            val url = URL(request.url)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                connectTimeout = 60_000
                readTimeout = 60_000
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
            }

            val body = request.fields.toJsonObject()

            Log.d("LINK_DATA", "request url: ${request.url}")
            request.fields.forEach { (key, value) ->
                Log.d("LINK_DATA", "request field: $key=$value")
            }
            Log.d("LINK_DATA", "request body: $body")

            BufferedWriter(OutputStreamWriter(connection.outputStream, Charsets.UTF_8))
                .use { it.write(body) }

            val code = connection.responseCode
            val response = if (code in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
            }
            Log.d("LINK_DATA", "chau response" +response)

            if (code in 200..299) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("${"http://"} $code"))
            }
        } catch (e: Exception) {
            Log.d("LINK_DATA", "chau failed $e")
            Result.failure(e)
        }
    }

    private fun Map<String, String>.toJsonObject(): String {
        val entries = entries.joinToString(",") { (key, value) ->
            """"${key.escapeJson()}":"${value.escapeJson()}""""
        }
        return "{$entries}"
    }

    private fun String.escapeJson(): String = buildString(length) {
        for (char in this@escapeJson) {
            when (char) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(char)
            }
        }
    }
}