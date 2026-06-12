package com.immersiads.app.domain

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    companion object {
        val KEY_NATIVE_LANGUAGE = stringPreferencesKey("native_language")
        val KEY_TARGET_LANGUAGE = stringPreferencesKey("target_language")
        val KEY_ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val KEY_PLAYBACK_SPEED = floatPreferencesKey("playback_speed")
        val KEY_SUBTITLES_ENABLED = booleanPreferencesKey("subtitles_enabled")
        val KEY_STREAK_COUNT = intPreferencesKey("streak_count")
        val KEY_LAST_ACTIVE_DATE = longPreferencesKey("last_active_date")
        val KEY_TOTAL_ADS_WATCHED = intPreferencesKey("total_ads_watched")
        val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val nativeLanguage: Flow<String> = context.dataStore.data.map { it[KEY_NATIVE_LANGUAGE] ?: "en" }
    val targetLanguage: Flow<String> = context.dataStore.data.map { it[KEY_TARGET_LANGUAGE] ?: "es" }
    val isOnboardingComplete: Flow<Boolean> = context.dataStore.data.map { it[KEY_ONBOARDING_COMPLETE] ?: false }
    val playbackSpeed: Flow<Float> = context.dataStore.data.map { it[KEY_PLAYBACK_SPEED] ?: 1.0f }
    val subtitlesEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_SUBTITLES_ENABLED] ?: true }
    val streakCount: Flow<Int> = context.dataStore.data.map { it[KEY_STREAK_COUNT] ?: 0 }
    val lastActiveDate: Flow<Long> = context.dataStore.data.map { it[KEY_LAST_ACTIVE_DATE] ?: 0L }
    val totalAdsWatched: Flow<Int> = context.dataStore.data.map { it[KEY_TOTAL_ADS_WATCHED] ?: 0 }
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[KEY_DARK_MODE] ?: false }

    suspend fun setNativeLanguage(code: String) {
        context.dataStore.edit { it[KEY_NATIVE_LANGUAGE] = code }
    }

    suspend fun setTargetLanguage(code: String) {
        context.dataStore.edit { it[KEY_TARGET_LANGUAGE] = code }
    }

    suspend fun setOnboardingComplete(complete: Boolean) {
        context.dataStore.edit { it[KEY_ONBOARDING_COMPLETE] = complete }
    }

    suspend fun setPlaybackSpeed(speed: Float) {
        context.dataStore.edit { it[KEY_PLAYBACK_SPEED] = speed }
    }

    suspend fun setSubtitlesEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SUBTITLES_ENABLED] = enabled }
    }

    suspend fun incrementStreak() {
        context.dataStore.edit { prefs ->
            prefs[KEY_STREAK_COUNT] = (prefs[KEY_STREAK_COUNT] ?: 0) + 1
            prefs[KEY_LAST_ACTIVE_DATE] = System.currentTimeMillis()
        }
    }

    suspend fun incrementAdsWatched() {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOTAL_ADS_WATCHED] = (prefs[KEY_TOTAL_ADS_WATCHED] ?: 0) + 1
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[KEY_DARK_MODE] = enabled }
    }
}
