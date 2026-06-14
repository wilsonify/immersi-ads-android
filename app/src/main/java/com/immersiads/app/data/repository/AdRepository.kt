package com.immersiads.app.data.repository

import com.immersiads.app.data.model.Advertisement
import com.immersiads.app.data.sample.SampleData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdRepository {

    private val _advertisements = MutableStateFlow(SampleData.sampleAdvertisements)

    fun getAdvertisements(): Flow<List<Advertisement>> = _advertisements.asStateFlow()

    fun getAdvertisementById(id: String): Advertisement? =
        _advertisements.value.find { it.id == id }

}
