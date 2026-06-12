package com.immersiads.app.ui.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.immersiads.app.data.model.VocabularyItem
import com.immersiads.app.data.repository.VocabularyRepository
import com.immersiads.app.domain.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class VocabularyUiState(
    val vocabularyItems: List<VocabularyItem> = emptyList(),
    val targetLanguage: String = "es",
    val isLoading: Boolean = true,
    val searchQuery: String = ""
)

class VocabularyViewModel(
    private val vocabularyRepository: VocabularyRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(VocabularyUiState())
    val uiState: StateFlow<VocabularyUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            combine(
                vocabularyRepository.getAllVocabulary(),
                userPreferences.targetLanguage,
                _searchQuery
            ) { items, targetLang, query ->
                Triple(items, targetLang, query)
            }.collect { (items, targetLang, query) ->
                val filtered = if (query.isBlank()) items
                else items.filter {
                    it.word.contains(query, ignoreCase = true) ||
                            it.translation.contains(query, ignoreCase = true)
                }
                _uiState.value = _uiState.value.copy(
                    vocabularyItems = filtered,
                    targetLanguage = targetLang,
                    isLoading = false,
                    searchQuery = query
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun deleteItem(item: VocabularyItem) {
        viewModelScope.launch {
            vocabularyRepository.deleteVocabulary(item)
        }
    }
}
