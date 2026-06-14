package com.immersiads.app.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.immersiads.app.data.model.Advertisement
import com.immersiads.app.data.model.Subtitle
import com.immersiads.app.data.model.VocabularyItem
import com.immersiads.app.data.repository.AdRepository
import com.immersiads.app.data.repository.VocabularyRepository
import com.immersiads.app.domain.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class PlayerUiState(
    val advertisement: Advertisement? = null,
    val currentSubtitle: Subtitle? = null,
    val isPlaying: Boolean = false,
    val playbackSpeed: Float = 1.0f,
    val currentPositionMs: Long = 0L,
    val subtitlesEnabled: Boolean = true,
    val selectedWord: String? = null,
    val selectedWordTranslation: String? = null,
    val showTranslationPopup: Boolean = false,
    val savedVocabularyWords: Set<String> = emptySet(),
    val snackbarMessage: String? = null,
    val errorMessage: String? = null
)

class PlayerViewModel(
    private val adRepository: AdRepository,
    private val vocabularyRepository: VocabularyRepository,
    private val userPreferences: UserPreferences,
    private val adId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        loadAd()
        loadPreferences()
    }

    private fun loadAd() {
        val ad = adRepository.getAdvertisementById(adId)
        _uiState.value = _uiState.value.copy(advertisement = ad)
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            val speed = userPreferences.playbackSpeed.first()
            val subtitles = userPreferences.subtitlesEnabled.first()
            _uiState.value = _uiState.value.copy(
                playbackSpeed = speed,
                subtitlesEnabled = subtitles
            )
        }
    }

    fun updatePosition(positionMs: Long) {
        val ad = _uiState.value.advertisement ?: return
        val currentSubtitle = ad.subtitles.firstOrNull { subtitle ->
            positionMs >= subtitle.startTimeMs && positionMs < subtitle.endTimeMs
        }
        _uiState.value = _uiState.value.copy(
            currentPositionMs = positionMs,
            currentSubtitle = currentSubtitle
        )
    }

    fun setPlaybackSpeed(speed: Float) {
        viewModelScope.launch {
            userPreferences.setPlaybackSpeed(speed)
            _uiState.value = _uiState.value.copy(playbackSpeed = speed)
        }
    }

    fun toggleSubtitles() {
        val newValue = !_uiState.value.subtitlesEnabled
        viewModelScope.launch {
            userPreferences.setSubtitlesEnabled(newValue)
            _uiState.value = _uiState.value.copy(subtitlesEnabled = newValue)
        }
    }

    fun onWordSelected(word: String) {
        val subtitle = _uiState.value.currentSubtitle
        val translation = if (subtitle != null && subtitle.translationText.isNotEmpty()) {
            "→ ${subtitle.translationText}"
        } else {
            "(translation not available)"
        }
        _uiState.value = _uiState.value.copy(
            selectedWord = word,
            selectedWordTranslation = translation,
            showTranslationPopup = true
        )
    }

    fun dismissTranslationPopup() {
        _uiState.value = _uiState.value.copy(
            showTranslationPopup = false,
            selectedWord = null,
            selectedWordTranslation = null
        )
    }

    fun saveWordToVocabulary() {
        val state = _uiState.value
        val word = state.selectedWord ?: return
        val ad = state.advertisement ?: return

        viewModelScope.launch {
            val item = VocabularyItem(
                word = word,
                translation = state.selectedWordTranslation ?: "",
                languageCode = ad.languageCode,
                context = state.currentSubtitle?.text ?: "",
                adId = ad.id
            )
            vocabularyRepository.saveVocabulary(item)
            _uiState.value = _uiState.value.copy(
                savedVocabularyWords = _uiState.value.savedVocabularyWords + word,
                showTranslationPopup = false,
                snackbarMessage = "\"$word\" saved to vocabulary"
            )
        }
    }

    fun onAdCompleted() {
        viewModelScope.launch {
            userPreferences.incrementAdsWatched()
            userPreferences.incrementStreak()
        }
    }

    fun onPlayerError(error: androidx.media3.common.PlaybackException) {
        _uiState.value = _uiState.value.copy(
            errorMessage = "Playback error: ${error.localizedMessage ?: "Unknown error"}"
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }
}
