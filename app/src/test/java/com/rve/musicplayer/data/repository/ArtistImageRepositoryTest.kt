package com.rve.musicplayer.data.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ArtistImageRepositoryTest {

    @Test
    fun `calculateCustomImageSampleSize keeps small bitmaps at full resolution`() {
        assertEquals(1, ArtistImageRepository.calculateCustomImageSampleSize(1024, 1024))
    }

    @Test
    fun `calculateCustomImageSampleSize aggressively downsamples oversized inputs`() {
        val sampleSize = ArtistImageRepository.calculateCustomImageSampleSize(12000, 8000)

        assertTrue(sampleSize >= 4)
        assertEquals(8, sampleSize)
    }
}
