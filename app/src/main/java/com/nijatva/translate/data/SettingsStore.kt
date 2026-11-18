package com.nijatva.translate.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/** Where the translation engine lives and how to authenticate against it. */
data class EngineSettings(
    val baseUrl: String = SettingsStore.DEFAULT_BASE_URL,
    val apiKey: String = "",
)

private val Context.settingsDataStore by preferencesDataStore(name = "nijatva_settings")

class SettingsStore(private val context: Context) {

    private val baseUrlKey = stringPreferencesKey("engine_base_url")
    private val apiKeyKey = stringPreferencesKey("engine_api_key")

    val settings: Flow<EngineSettings> = context.settingsDataStore.data
        .catch { cause -> if (cause is IOException) emit(emptyPreferences()) else throw cause }
        .map { prefs ->
            EngineSettings(
                baseUrl = prefs[baseUrlKey] ?: SettingsStore.DEFAULT_BASE_URL,
                apiKey = prefs[apiKeyKey] ?: "",
            )
        }

    suspend fun save(baseUrl: String, apiKey: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[baseUrlKey] = baseUrl.trim().ifBlank { SettingsStore.DEFAULT_BASE_URL }
            prefs[apiKeyKey] = apiKey.trim()
        }
    }

    companion object {
        const val DEFAULT_BASE_URL = "https://libretranslate.com"
    }
}
