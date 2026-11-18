package com.nijatva.translate.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Single entry point for translation. Reads the configured engine on every call so a
 * change in settings takes effect immediately, without an app restart.
 */
class TranslationRepository(context: Context) {

    private val store = SettingsStore(context.applicationContext)

    val settings: Flow<EngineSettings> = store.settings

    suspend fun translate(text: String, source: String, target: String): String {
        val current = settings.first()
        return engineFor(current).translate(text.trim(), source, target)
    }

    suspend fun saveSettings(baseUrl: String, apiKey: String) = store.save(baseUrl, apiKey)

    private fun engineFor(current: EngineSettings): TranslationEngine =
        LibreTranslateEngine(baseUrl = current.baseUrl, apiKey = current.apiKey)
}
