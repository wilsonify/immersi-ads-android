package com.immersiads.app.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.immersiads.app.data.model.Advertisement
import com.immersiads.app.data.repository.AdRepository
import com.immersiads.app.domain.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class FeedUiState(
    val advertisements: List<Advertisement> = emptyList(),
    val targetLanguage: String = "es",
    val streakCount: Int = 0,
    val isLoading: Boolean = true,
    val filterByLanguage: Boolean = true
)

class FeedViewModel(
    private val adRepository: AdRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                adRepository.getAdvertisements(),
                userPreferences.targetLanguage,
                userPreferences.streakCount
            ) { ads, targetLang, streak ->
                Triple(ads, targetLang, streak)
            }.collect { (ads, targetLang, streak) ->
                val filtered = if (_uiState.value.filterByLanguage) {
                    ads.filter { it.languageCode == targetLang }
                } else {
                    ads
                }
                _uiState.value = _uiState.value.copy(
                    advertisements = filtered,
                    targetLanguage = targetLang,
                    streakCount = streak,
                    isLoading = false
                )
            }
        }
    }

    fun toggleLanguageFilter() {
        val current = _uiState.value
        val allAds = adRepository.getAllAdvertisements()
        val filtered = if (!current.filterByLanguage) {
            allAds.filter { it.languageCode == current.targetLanguage }
        } else {
            allAds
        }
        _uiState.value = current.copy(
            filterByLanguage = !current.filterByLanguage,
            advertisements = filtered
        )
    }
}
