package com.immersiads.app

import android.app.Application
import com.immersiads.app.data.local.AppDatabase
import com.immersiads.app.data.repository.AdRepository
import com.immersiads.app.data.repository.VocabularyRepository
import com.immersiads.app.domain.UserPreferences

class ImmersiAdsApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val userPreferences: UserPreferences by lazy { UserPreferences(this) }
    val vocabularyRepository: VocabularyRepository by lazy {
        VocabularyRepository(database.vocabularyDao())
    }
    val adRepository: AdRepository by lazy { AdRepository() }
}
