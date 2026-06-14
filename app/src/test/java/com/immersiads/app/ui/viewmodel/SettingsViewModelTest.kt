package com.immersiads.app.ui.viewmodel

import com.immersiads.app.domain.UserPreferences
import com.immersiads.app.ui.settings.SettingsViewModel
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
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var userPreferences: UserPreferences
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userPreferences = mockk(relaxed = true)
        every { userPreferences.nativeLanguage } returns flowOf("en")
        every { userPreferences.targetLanguage } returns flowOf("de")
        every { userPreferences.subtitlesEnabled } returns flowOf(true)
        every { userPreferences.playbackSpeed } returns flowOf(1.5f)
        every { userPreferences.isDarkMode } returns flowOf(true)
        viewModel = SettingsViewModel(userPreferences)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads native language from preferences`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("en", viewModel.uiState.value.nativeLanguage)
    }

    @Test
    fun `loads target language from preferences`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("de", viewModel.uiState.value.targetLanguage)
    }

    @Test
    fun `loads subtitles enabled from preferences`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.subtitlesEnabled)
    }

    @Test
    fun `loads playback speed from preferences`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1.5f, viewModel.uiState.value.playbackSpeed, 0.01f)
    }

    @Test
    fun `loads dark mode from preferences`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isDarkMode)
    }

    @Test
    fun `setSubtitlesEnabled saves to preferences`() = runTest {
        viewModel.setSubtitlesEnabled(false)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { userPreferences.setSubtitlesEnabled(false) }
    }

    @Test
    fun `setDarkMode saves to preferences`() = runTest {
        viewModel.setDarkMode(false)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { userPreferences.setDarkMode(false) }
    }

    @Test
    fun `setPlaybackSpeed saves to preferences`() = runTest {
        viewModel.setPlaybackSpeed(2.0f)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { userPreferences.setPlaybackSpeed(2.0f) }
    }
}
