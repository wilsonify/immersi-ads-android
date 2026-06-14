package com.immersiads.app.domain

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UserPreferencesTest {

    private class InMemoryDataStore : DataStore<Preferences> {
        private val _data = MutableStateFlow(emptyPreferences())
        override val data: Flow<Preferences> = _data.asStateFlow()

        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
            _data.value = transform(_data.value)
            return _data.value
        }
    }

    private lateinit var dataStore: InMemoryDataStore
    private lateinit var userPreferences: UserPreferences

    @Before
    fun setUp() {
        dataStore = InMemoryDataStore()
        userPreferences = UserPreferences(dataStore)
    }

    @Test
    fun `nativeLanguage defaults to en`() = runTest {
        val result = userPreferences.nativeLanguage.first()
        assertEquals("en", result)
    }

    @Test
    fun `nativeLanguage returns stored value`() = runTest {
        dataStore.edit { it[UserPreferences.KEY_NATIVE_LANGUAGE] = "fr" }
        val result = userPreferences.nativeLanguage.first()
        assertEquals("fr", result)
    }

    @Test
    fun `targetLanguage defaults to es`() = runTest {
        val result = userPreferences.targetLanguage.first()
        assertEquals("es", result)
    }

    @Test
    fun `targetLanguage returns stored value`() = runTest {
        dataStore.edit { it[UserPreferences.KEY_TARGET_LANGUAGE] = "de" }
        val result = userPreferences.targetLanguage.first()
        assertEquals("de", result)
    }

    @Test
    fun `isOnboardingComplete defaults to false`() = runTest {
        val result = userPreferences.isOnboardingComplete.first()
        assertFalse(result)
    }

    @Test
    fun `isOnboardingComplete returns stored value`() = runTest {
        dataStore.edit { it[UserPreferences.KEY_ONBOARDING_COMPLETE] = true }
        val result = userPreferences.isOnboardingComplete.first()
        assertTrue(result)
    }

    @Test
    fun `playbackSpeed defaults to 1_0f`() = runTest {
        val result = userPreferences.playbackSpeed.first()
        assertEquals(1.0f, result, 0.001f)
    }

    @Test
    fun `playbackSpeed returns stored value`() = runTest {
        dataStore.edit { it[UserPreferences.KEY_PLAYBACK_SPEED] = 1.5f }
        val result = userPreferences.playbackSpeed.first()
        assertEquals(1.5f, result, 0.001f)
    }

    @Test
    fun `subtitlesEnabled defaults to true`() = runTest {
        val result = userPreferences.subtitlesEnabled.first()
        assertTrue(result)
    }

    @Test
    fun `subtitlesEnabled returns stored value`() = runTest {
        dataStore.edit { it[UserPreferences.KEY_SUBTITLES_ENABLED] = false }
        val result = userPreferences.subtitlesEnabled.first()
        assertFalse(result)
    }

    @Test
    fun `streakCount defaults to 0`() = runTest {
        val result = userPreferences.streakCount.first()
        assertEquals(0, result)
    }

    @Test
    fun `streakCount returns stored value`() = runTest {
        dataStore.edit { it[UserPreferences.KEY_STREAK_COUNT] = 7 }
        val result = userPreferences.streakCount.first()
        assertEquals(7, result)
    }

    @Test
    fun `totalAdsWatched defaults to 0`() = runTest {
        val result = userPreferences.totalAdsWatched.first()
        assertEquals(0, result)
    }

    @Test
    fun `totalAdsWatched returns stored value`() = runTest {
        dataStore.edit { it[UserPreferences.KEY_TOTAL_ADS_WATCHED] = 42 }
        val result = userPreferences.totalAdsWatched.first()
        assertEquals(42, result)
    }

    @Test
    fun `isDarkMode defaults to false`() = runTest {
        val result = userPreferences.isDarkMode.first()
        assertFalse(result)
    }

    @Test
    fun `isDarkMode returns stored value`() = runTest {
        dataStore.edit { it[UserPreferences.KEY_DARK_MODE] = true }
        val result = userPreferences.isDarkMode.first()
        assertTrue(result)
    }

    @Test
    fun `setNativeLanguage persists value`() = runTest {
        userPreferences.setNativeLanguage("ja")
        val result = userPreferences.nativeLanguage.first()
        assertEquals("ja", result)
    }

    @Test
    fun `setTargetLanguage persists value`() = runTest {
        userPreferences.setTargetLanguage("ko")
        val result = userPreferences.targetLanguage.first()
        assertEquals("ko", result)
    }

    @Test
    fun `setOnboardingComplete persists value`() = runTest {
        userPreferences.setOnboardingComplete(true)
        val result = userPreferences.isOnboardingComplete.first()
        assertTrue(result)
    }

    @Test
    fun `setPlaybackSpeed persists value`() = runTest {
        userPreferences.setPlaybackSpeed(2.0f)
        val result = userPreferences.playbackSpeed.first()
        assertEquals(2.0f, result, 0.001f)
    }

    @Test
    fun `setSubtitlesEnabled persists value`() = runTest {
        userPreferences.setSubtitlesEnabled(false)
        val result = userPreferences.subtitlesEnabled.first()
        assertFalse(result)
    }

    @Test
    fun `incrementStreak increments from 0`() = runTest {
        userPreferences.incrementStreak()
        val result = userPreferences.streakCount.first()
        assertEquals(1, result)
    }

    @Test
    fun `incrementStreak increments existing value`() = runTest {
        dataStore.edit { it[UserPreferences.KEY_STREAK_COUNT] = 5 }
        userPreferences.incrementStreak()
        val result = userPreferences.streakCount.first()
        assertEquals(6, result)
    }

    @Test
    fun `incrementStreak updates lastActiveDate`() = runTest {
        userPreferences.incrementStreak()
        dataStore.edit { prefs ->
            val lastActive = prefs[UserPreferences.KEY_LAST_ACTIVE_DATE]
            assert(lastActive != null && lastActive > 0)
        }
    }

    @Test
    fun `incrementAdsWatched increments from 0`() = runTest {
        userPreferences.incrementAdsWatched()
        val result = userPreferences.totalAdsWatched.first()
        assertEquals(1, result)
    }

    @Test
    fun `incrementAdsWatched increments existing value`() = runTest {
        dataStore.edit { it[UserPreferences.KEY_TOTAL_ADS_WATCHED] = 10 }
        userPreferences.incrementAdsWatched()
        val result = userPreferences.totalAdsWatched.first()
        assertEquals(11, result)
    }

    @Test
    fun `setDarkMode persists value`() = runTest {
        userPreferences.setDarkMode(true)
        val result = userPreferences.isDarkMode.first()
        assertTrue(result)
    }
}
