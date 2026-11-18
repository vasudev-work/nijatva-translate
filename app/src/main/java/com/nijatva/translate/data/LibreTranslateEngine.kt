package com.nijatva.translate.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/** Thrown when an engine answers with an error or something we cannot read. */
class TranslationException(message: String) : IOException(message)

/** Anything that can turn text in one language into another. */
interface TranslationEngine {
    suspend fun translate(text: String, source: String, target: String): String
}

@Serializable
internal data class TranslateRequest(
    val q: String,
    val source: String,
    val target: String,
    val format: String = "text",
    @SerialName("api_key") val apiKey: String? = null,
)

@Serializable
internal data class TranslateResponse(
    val translatedText: String? = null,
    val error: String? = null,
)

/**
 * Talks to any LibreTranslate-compatible server (libretranslate.com, a local Docker
 * container, or a self-hosted gateway in front of IndicTrans2 / Bhashini).
 */
class LibreTranslateEngine(
    private val baseUrl: String,
    private val apiKey: String,
) : TranslationEngine {

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun translate(text: String, source: String, target: String): String =
        withContext(Dispatchers.IO) {
            val payload = json.encodeToString(
                TranslateRequest.serializer(),
                TranslateRequest(
                    q = text,
                    source = source,
                    target = target,
                    apiKey = apiKey.ifBlank { null },
                ),
            )

            val request = Request.Builder()
                .url(endpoint())
                .post(payload.toRequestBody(JSON))
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    throw TranslationException("Engine replied ${response.code}")
                }

                val parsed = runCatching {
                    json.decodeFromString(TranslateResponse.serializer(), body)
                }.getOrElse {
                    throw TranslationException("Engine sent an unexpected response")
                }

                parsed.error?.let { throw TranslationException(it) }

                parsed.translatedText
                    ?.takeIf { it.isNotBlank() }
                    ?: throw TranslationException("Engine returned an empty translation")
            }
        }

    private fun endpoint(): String {
        val base = baseUrl.trim().trimEnd('/')
        return when {
            base.isEmpty() -> throw TranslationException("No engine URL configured")
            base.endsWith("/translate") -> base
            else -> "$base/translate"
        }
    }

    private companion object {
        val JSON = "application/json; charset=utf-8".toMediaType()
    }
}
