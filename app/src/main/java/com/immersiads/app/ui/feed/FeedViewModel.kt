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

    private val _filterByLanguage = MutableStateFlow(true)

    init {
        viewModelScope.launch {
            combine(
                adRepository.getAdvertisements(),
                userPreferences.targetLanguage,
                userPreferences.streakCount,
                _filterByLanguage
            ) { ads, targetLang, streak, filterEnabled ->
                val filtered = if (filterEnabled) {
                    ads.filter { it.languageCode == targetLang }
                } else {
                    ads
                }
                FeedUiState(
                    advertisements = filtered,
                    targetLanguage = targetLang,
                    streakCount = streak,
                    isLoading = false,
                    filterByLanguage = filterEnabled
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleLanguageFilter() {
        _filterByLanguage.value = !_filterByLanguage.value
    }
}
