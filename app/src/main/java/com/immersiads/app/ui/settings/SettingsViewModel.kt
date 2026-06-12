package com.immersiads.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.immersiads.app.domain.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class SettingsUiState(
    val nativeLanguage: String = "en",
    val targetLanguage: String = "es",
    val subtitlesEnabled: Boolean = true,
    val playbackSpeed: Float = 1.0f,
    val isDarkMode: Boolean = false
)

class SettingsViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                userPreferences.nativeLanguage,
                userPreferences.targetLanguage,
                userPreferences.subtitlesEnabled,
                userPreferences.playbackSpeed,
                userPreferences.isDarkMode
            ) { native, target, subtitles, speed, dark ->
                SettingsUiState(
                    nativeLanguage = native,
                    targetLanguage = target,
                    subtitlesEnabled = subtitles,
                    playbackSpeed = speed,
                    isDarkMode = dark
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun setSubtitlesEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setSubtitlesEnabled(enabled)
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setDarkMode(enabled)
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        viewModelScope.launch {
            userPreferences.setPlaybackSpeed(speed)
        }
    }
}
