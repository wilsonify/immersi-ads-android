package com.immersiads.app.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class AdRepositoryTest {

    private lateinit var repository: AdRepository

    @Before
    fun setUp() {
        repository = AdRepository()
    }

    @Test
    fun `getAdvertisements returns sample data`() = runBlocking {
        val result = repository.getAdvertisements().first()

        assertNotNull(result)
        assert(result.isNotEmpty())
    }

    @Test
    fun `getAdvertisementById returns matching ad`() {
        val result = repository.getAdvertisementById("ad_001")

        assertNotNull(result)
        assertEquals("ad_001", result?.id)
    }

    @Test
    fun `getAdvertisementById returns null for unknown id`() {
        val result = repository.getAdvertisementById("nonexistent")

        assertNull(result)
    }

    @Test
    fun `getAdvertisements flow emits the same list on repeated access`() = runBlocking {
        val first = repository.getAdvertisements().first()
        val second = repository.getAdvertisements().first()

        assertEquals(first.size, second.size)
        assertEquals(first, second)
    }
}
