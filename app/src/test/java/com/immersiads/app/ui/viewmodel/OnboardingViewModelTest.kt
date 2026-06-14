package com.immersiads.app.ui.viewmodel

import com.immersiads.app.data.model.Language
import com.immersiads.app.domain.UserPreferences
import com.immersiads.app.ui.onboarding.OnboardingViewModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var userPreferences: UserPreferences
    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userPreferences = mockk(relaxed = true)
        every { userPreferences.nativeLanguage } returns flowOf("en")
        every { userPreferences.targetLanguage } returns flowOf("es")
        every { userPreferences.isOnboardingComplete } returns flowOf(false)
        viewModel = OnboardingViewModel(userPreferences)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has step 0`() {
        assertEquals(0, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `nextStep increments step`() {
        viewModel.nextStep()
        assertEquals(1, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `previousStep from step 1 returns to step 0`() {
        viewModel.nextStep()
        viewModel.previousStep()
        assertEquals(0, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `previousStep does not go below 0`() {
        viewModel.previousStep()
        assertEquals(0, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `nextStep does not go beyond total steps`() {
        repeat(10) { viewModel.nextStep() }
        assertTrue(viewModel.uiState.value.currentStep < viewModel.uiState.value.totalSteps)
    }

    @Test
    fun `selectNativeLanguage updates state`() {
        val language = Language("en", "English", "English", "🇺🇸")
        viewModel.selectNativeLanguage(language)
        assertEquals(language, viewModel.uiState.value.nativeLanguage)
    }

    @Test
    fun `selectTargetLanguage updates state`() {
        val language = Language("es", "Spanish", "Español", "🇪🇸")
        viewModel.selectTargetLanguage(language)
        assertEquals(language, viewModel.uiState.value.targetLanguage)
    }

    @Test
    fun `completeOnboarding requires both languages selected`() = runTest {
        // No languages selected - should not complete
        viewModel.completeOnboarding()
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isComplete)
    }

    @Test
    fun `completeOnboarding saves preferences when both languages selected`() = runTest {
        val native = Language("en", "English", "English", "🇺🇸")
        val target = Language("es", "Spanish", "Español", "🇪🇸")
        viewModel.selectNativeLanguage(native)
        viewModel.selectTargetLanguage(target)
        viewModel.completeOnboarding()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { userPreferences.setNativeLanguage("en") }
        coVerify { userPreferences.setTargetLanguage("es") }
        coVerify { userPreferences.setOnboardingComplete(true) }
        assertTrue(viewModel.uiState.value.isComplete)
    }
}
