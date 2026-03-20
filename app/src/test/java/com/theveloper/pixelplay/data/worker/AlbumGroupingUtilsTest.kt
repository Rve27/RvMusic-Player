package com.theveloper.pixelplay.data.worker

import com.google.common.truth.Truth.assertThat
import com.theveloper.pixelplay.data.database.SongEntity
import org.junit.Test

class AlbumGroupingUtilsTest {

    @Test
    fun `resolveAlbumArtist prefers embedded album artist`() {
        val resolved = resolveAlbumArtist(
            rawAlbumArtist = null,
            metadataAlbumArtist = "The Weeknd"
        )

        assertThat(resolved).isEqualTo("The Weeknd")
    }

    @Test
    fun `buildAlbumGroupingKey ignores per-track artist when title and art match`() {
        val soloTrack = testSong(
            artistName = "The Weeknd",
            albumArtist = null,
            albumArtUriString = "content://art/hurry-up",
            parentDirectoryPath = "/music/The Weeknd & Justice - Hurry Up Tomorrow"
        )
        val collabTrack = testSong(
            artistName = "The Weeknd, Justice",
            albumArtist = null,
            albumArtUriString = "content://art/hurry-up",
            parentDirectoryPath = "/music/The Weeknd & Justice - Hurry Up Tomorrow"
        )

        assertThat(buildAlbumGroupingKey(soloTrack)).isEqualTo(buildAlbumGroupingKey(collabTrack))
    }

    @Test
    fun `buildAlbumGroupingKey keeps same-titled albums apart when directories differ`() {
        val firstAlbum = testSong(
            artistName = "Artist A",
            albumArtist = null,
            albumArtUriString = null,
            parentDirectoryPath = "/music/Artist A/Greatest Hits"
        )
        val secondAlbum = testSong(
            artistName = "Artist B",
            albumArtist = null,
            albumArtUriString = null,
            parentDirectoryPath = "/music/Artist B/Greatest Hits"
        )

        assertThat(buildAlbumGroupingKey(firstAlbum)).isNotEqualTo(buildAlbumGroupingKey(secondAlbum))
    }

    @Test
    fun `chooseAlbumDisplayArtist prefers dominant track artist when grouping is off`() {
        val songs = listOf(
            testSong(artistName = "The Weeknd", albumArtist = "The Weeknd & Justice"),
            testSong(artistName = "The Weeknd", albumArtist = "The Weeknd & Justice"),
            testSong(artistName = "The Weeknd, Justice", albumArtist = "The Weeknd & Justice")
        )

        val displayArtist = chooseAlbumDisplayArtist(
            songs = songs,
            preferAlbumArtist = false
        )

        assertThat(displayArtist).isEqualTo("The Weeknd")
    }

    @Test
    fun `chooseAlbumDisplayArtist prefers album artist when grouping is on`() {
        val songs = listOf(
            testSong(artistName = "The Weeknd", albumArtist = "The Weeknd & Justice"),
            testSong(artistName = "The Weeknd, Justice", albumArtist = "The Weeknd & Justice")
        )

        val displayArtist = chooseAlbumDisplayArtist(
            songs = songs,
            preferAlbumArtist = true
        )

        assertThat(displayArtist).isEqualTo("The Weeknd & Justice")
    }

    private fun testSong(
        artistName: String,
        albumArtist: String?,
        albumArtUriString: String? = null,
        parentDirectoryPath: String = "/music/default",
        albumName: String = "Hurry Up Tomorrow",
        albumId: Long = 42L
    ): SongEntity {
        return SongEntity(
            id = albumId,
            title = "Track",
            artistName = artistName,
            artistId = 1L,
            albumArtist = albumArtist,
            albumName = albumName,
            albumId = albumId,
            contentUriString = "content://media/$albumId",
            albumArtUriString = albumArtUriString,
            duration = 180_000L,
            genre = null,
            filePath = "$parentDirectoryPath/track.flac",
            parentDirectoryPath = parentDirectoryPath
        )
    }
}
