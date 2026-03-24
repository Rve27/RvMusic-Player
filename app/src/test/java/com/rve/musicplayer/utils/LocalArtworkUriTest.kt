package com.rve.musicplayer.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LocalArtworkUriTest {

    @Test
    fun resolveSongArtworkUri_convertsLegacyLocalCacheUriToStableUri() {
        val resolved = LocalArtworkUri.resolveSongArtworkUri(
            storedUri = "content://com.rve.musicplayer.provider/cache/song_art_42.jpg",
            songId = 42L,
            contentUriString = "content://media/external/audio/media/42"
        )

        assertThat(resolved).isEqualTo(LocalArtworkUri.buildSongUri(42L))
    }

    @Test
    fun resolveSongArtworkUri_keepsRemoteArtworkUriUntouched() {
        val resolved = LocalArtworkUri.resolveSongArtworkUri(
            storedUri = "https://example.com/cover.jpg",
            songId = 42L,
            contentUriString = "content://media/external/audio/media/42"
        )

        assertThat(resolved).isEqualTo("https://example.com/cover.jpg")
    }

    @Test
    fun resolveSongArtworkUri_keepsCloudSourceArtworkUntouched() {
        val resolved = LocalArtworkUri.resolveSongArtworkUri(
            storedUri = "telegram_art://123/456",
            songId = 42L,
            contentUriString = "telegram://123/456"
        )

        assertThat(resolved).isEqualTo("telegram_art://123/456")
    }

    @Test
    fun parseSongId_readsStableSongUri() {
        val songId = LocalArtworkUri.parseSongId(LocalArtworkUri.buildSongUri(99L))

        assertThat(songId).isEqualTo(99L)
    }
}
