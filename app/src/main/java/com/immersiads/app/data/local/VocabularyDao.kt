package com.immersiads.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.immersiads.app.data.local.entities.VocabularyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {

    @Query("SELECT * FROM vocabulary ORDER BY savedAtMs DESC")
    fun getAllVocabulary(): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary WHERE languageCode = :languageCode ORDER BY savedAtMs DESC")
    fun getVocabularyByLanguage(languageCode: String): Flow<List<VocabularyEntity>>

    @Query("SELECT COUNT(*) FROM vocabulary")
    suspend fun getVocabularyCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabulary(item: VocabularyEntity): Long

    @Delete
    suspend fun deleteVocabulary(item: VocabularyEntity)

}
