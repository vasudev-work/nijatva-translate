package com.nijatva.translate.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nijatva.translate.data.Language
import com.nijatva.translate.data.Languages
import com.nijatva.translate.data.TranslationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TranslateUiState(
    val source: Language = Languages.auto,
    val target: Language = Languages.english,
    val input: String = "",
    val output: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val baseUrl: String = "",
    val apiKey: String = "",
) {
    val canTranslate: Boolean get() = input.isNotBlank() && !isLoading
    val canSwap: Boolean get() = source.code != Languages.AUTO
}

class TranslateViewModel(private val repository: TranslationRepository) : ViewModel() {

    private val _state = MutableStateFlow(TranslateUiState())
    val state: StateFlow<TranslateUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.settings.collect { settings ->
                _state.update { it.copy(baseUrl = settings.baseUrl, apiKey = settings.apiKey) }
            }
        }
    }

    fun onInputChange(value: String) {
        _state.update { it.copy(input = value, error = null) }
    }

    fun onClearInput() {
        _state.update { it.copy(input = "", output = "", error = null) }
    }

    fun onSourceSelected(language: Language) {
        _state.update { current ->
            val target = if (language.code == current.target.code) Languages.english else current.target
            current.copy(source = language, target = target, output = "")
        }
    }

    fun onTargetSelected(language: Language) {
        _state.update { current ->
            if (language.code == Languages.AUTO) return@update current
            val source = if (language.code == current.source.code) Languages.auto else current.source
            current.copy(target = language, source = source, output = "")
        }
    }

    fun onSwap() {
        _state.update { current ->
            if (!current.canSwap) return@update current
            current.copy(
                source = current.target,
                target = current.source,
                input = current.output.ifBlank { current.input },
                output = if (current.output.isBlank()) "" else current.input,
                error = null,
            )
        }
    }

    fun translate() {
        val snapshot = _state.value
        if (!snapshot.canTranslate) return

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            runCatching {
                repository.translate(snapshot.input, snapshot.source.code, snapshot.target.code)
            }.onSuccess { translated ->
                _state.update { it.copy(isLoading = false, output = translated) }
            }.onFailure { failure ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = failure.message ?: "Translation failed",
                    )
                }
            }
        }
    }

    fun saveSettings(baseUrl: String, apiKey: String) {
        viewModelScope.launch { repository.saveSettings(baseUrl, apiKey) }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TranslateViewModel(TranslationRepository(context.applicationContext))
            }
        }
    }
}
