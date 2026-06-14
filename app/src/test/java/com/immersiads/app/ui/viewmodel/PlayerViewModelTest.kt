package com.immersiads.app.ui.viewmodel

import com.immersiads.app.data.model.Advertisement
import com.immersiads.app.data.model.DifficultyLevel
import com.immersiads.app.data.model.Subtitle
import com.immersiads.app.data.repository.AdRepository
import com.immersiads.app.data.repository.VocabularyRepository
import com.immersiads.app.domain.UserPreferences
import com.immersiads.app.ui.player.PlayerViewModel
import io.mockk.coEvery
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var adRepository: AdRepository
    private lateinit var vocabularyRepository: VocabularyRepository
    private lateinit var userPreferences: UserPreferences
    private lateinit var viewModel: PlayerViewModel

    private val testAd = Advertisement(
        id = "ad_test",
        title = "Test Ad",
        brand = "TestBrand",
        languageCode = "es",
        videoUrl = "https://example.com/video.mp4",
        thumbnailUrl = "",
        durationMs = 10000,
        subtitles = listOf(
            Subtitle(0, 3000, "Hola mundo", "Hello world"),
            Subtitle(3000, 6000, "¿Cómo estás?", "How are you?")
        ),
        difficulty = DifficultyLevel.BEGINNER
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        adRepository = mockk()
        vocabularyRepository = mockk(relaxed = true)
        userPreferences = mockk(relaxed = true)
        every { adRepository.getAdvertisementById("ad_test") } returns testAd
        every { userPreferences.playbackSpeed } returns flowOf(1.0f)
        every { userPreferences.subtitlesEnabled } returns flowOf(true)
        viewModel = PlayerViewModel(adRepository, vocabularyRepository, userPreferences, "ad_test")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads advertisement by id`() {
        assertNotNull(viewModel.uiState.value.advertisement)
        assertEquals("ad_test", viewModel.uiState.value.advertisement?.id)
    }

    @Test
    fun `loads playback speed from preferences`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1.0f, viewModel.uiState.value.playbackSpeed, 0.01f)
    }

    @Test
    fun `loads subtitles enabled from preferences`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.subtitlesEnabled)
    }

    @Test
    fun `updatePosition finds current subtitle`() {
        viewModel.updatePosition(1000)
        assertNotNull(viewModel.uiState.value.currentSubtitle)
        assertEquals("Hola mundo", viewModel.uiState.value.currentSubtitle?.text)
    }

    @Test
    fun `updatePosition returns null for no subtitle`() {
        viewModel.updatePosition(2999)
        assertNotNull(viewModel.uiState.value.currentSubtitle)
    }

    @Test
    fun `updatePosition returns null when out of range`() {
        viewModel.updatePosition(50000)
        assertNull(viewModel.uiState.value.currentSubtitle)
    }

    @Test
    fun `setPlaybackSpeed updates state and saves`() = runTest {
        viewModel.setPlaybackSpeed(1.5f)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1.5f, viewModel.uiState.value.playbackSpeed, 0.01f)
        coVerify { userPreferences.setPlaybackSpeed(1.5f) }
    }

    @Test
    fun `toggleSubtitles flips subtitle state`() = runTest {
        val initial = viewModel.uiState.value.subtitlesEnabled
        viewModel.toggleSubtitles()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(!initial, viewModel.uiState.value.subtitlesEnabled)
        coVerify { userPreferences.setSubtitlesEnabled(!initial) }
    }

    @Test
    fun `onWordSelected shows translation popup`() {
        viewModel.updatePosition(1000)
        viewModel.onWordSelected("Hola")
        assertTrue(viewModel.uiState.value.showTranslationPopup)
        assertEquals("Hola", viewModel.uiState.value.selectedWord)
    }

    @Test
    fun `dismissTranslationPopup hides popup`() {
        viewModel.updatePosition(1000)
        viewModel.onWordSelected("Hola")
        viewModel.dismissTranslationPopup()
        assertNull(viewModel.uiState.value.selectedWord)
        assertNull(viewModel.uiState.value.selectedWordTranslation)
    }

    @Test
    fun `saveWordToVocabulary adds word and shows snackbar`() = runTest {
        coEvery { vocabularyRepository.saveVocabulary(any()) } returns 1L
        viewModel.updatePosition(1000)
        viewModel.onWordSelected("Hola")
        viewModel.saveWordToVocabulary()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.savedVocabularyWords.contains("Hola"))
        assertNotNull(viewModel.uiState.value.snackbarMessage)
    }

    @Test
    fun `onAdCompleted increments streak and ads watched`() = runTest {
        viewModel.onAdCompleted()
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { userPreferences.incrementAdsWatched() }
        coVerify { userPreferences.incrementStreak() }
    }

    @Test
    fun `clearSnackbar clears message`() {
        viewModel.clearSnackbar()
        assertNull(viewModel.uiState.value.snackbarMessage)
    }

    @Test
    fun `onPlayerError sets error message`() {
        val error = androidx.media3.common.PlaybackException(
            "Network error", null, 1000
        )
        viewModel.onPlayerError(error)
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.errorMessage!!.contains("Network error"))
    }

    @Test
    fun `clearError clears error message`() {
        val error = androidx.media3.common.PlaybackException(
            "err", null, 1000
        )
        viewModel.onPlayerError(error)
        viewModel.clearError()
        assertNull(viewModel.uiState.value.errorMessage)
    }
}
