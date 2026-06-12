package com.immersiads.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.immersiads.app.data.model.Language
import com.immersiads.app.data.model.SUPPORTED_LANGUAGES
import com.immersiads.app.domain.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val currentStep: Int = 0,
    val totalSteps: Int = 3,
    val nativeLanguage: Language? = null,
    val targetLanguage: Language? = null,
    val availableLanguages: List<Language> = SUPPORTED_LANGUAGES,
    val isComplete: Boolean = false
)

class OnboardingViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun selectNativeLanguage(language: Language) {
        _uiState.value = _uiState.value.copy(nativeLanguage = language)
    }

    fun selectTargetLanguage(language: Language) {
        _uiState.value = _uiState.value.copy(targetLanguage = language)
    }

    fun nextStep() {
        val current = _uiState.value
        if (current.currentStep < current.totalSteps - 1) {
            _uiState.value = current.copy(currentStep = current.currentStep + 1)
        }
    }

    fun previousStep() {
        val current = _uiState.value
        if (current.currentStep > 0) {
            _uiState.value = current.copy(currentStep = current.currentStep - 1)
        }
    }

    fun completeOnboarding() {
        val current = _uiState.value
        val nativeLang = current.nativeLanguage ?: return
        val targetLang = current.targetLanguage ?: return

        viewModelScope.launch {
            userPreferences.setNativeLanguage(nativeLang.code)
            userPreferences.setTargetLanguage(targetLang.code)
            userPreferences.setOnboardingComplete(true)
            _uiState.value = _uiState.value.copy(isComplete = true)
        }
    }
}
