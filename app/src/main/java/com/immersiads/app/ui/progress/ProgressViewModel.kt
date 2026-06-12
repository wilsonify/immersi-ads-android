package com.immersiads.app.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.immersiads.app.data.repository.VocabularyRepository
import com.immersiads.app.domain.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class ProgressUiState(
    val streakCount: Int = 0,
    val totalAdsWatched: Int = 0,
    val vocabularyCount: Int = 0,
    val targetLanguage: String = "es",
    val isLoading: Boolean = true
)

class ProgressViewModel(
    private val userPreferences: UserPreferences,
    private val vocabularyRepository: VocabularyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                userPreferences.streakCount,
                userPreferences.totalAdsWatched,
                userPreferences.targetLanguage,
                vocabularyRepository.getAllVocabulary()
            ) { streak, adsWatched, targetLang, vocabularyItems ->
                ProgressUiState(
                    streakCount = streak,
                    totalAdsWatched = adsWatched,
                    vocabularyCount = vocabularyItems.size,
                    targetLanguage = targetLang,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
