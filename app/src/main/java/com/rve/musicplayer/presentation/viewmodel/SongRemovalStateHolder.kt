package com.rve.musicplayer.presentation.viewmodel

import android.app.Activity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rve.musicplayer.data.model.Song
import com.rve.musicplayer.data.preferences.PlaylistPreferencesRepository
import com.rve.musicplayer.data.repository.MusicRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@ViewModelScoped
class SongRemovalStateHolder @Inject constructor(
    private val musicRepository: MusicRepository,
    private val metadataEditStateHolder: MetadataEditStateHolder,
    private val playlistPreferencesRepository: PlaylistPreferencesRepository,
    private val libraryStateHolder: LibraryStateHolder
) {
    suspend fun showDeleteConfirmation(activity: Activity, song: Song): Boolean {
        return withContext(Dispatchers.Main) {
            try {
                if (activity.isFinishing || activity.isDestroyed) {
                    return@withContext false
                }

                val userChoice = CompletableDeferred<Boolean>()
                val dialog = MaterialAlertDialogBuilder(activity)
                    .setTitle("Delete song?")
                    .setMessage(
                        """
                    "${song.title}" by ${song.displayArtist}

                    This song will be permanently deleted from your device and cannot be recovered.
                """
                            .trimIndent()
                    )
                    .setPositiveButton("Delete") { _, _ ->
                        userChoice.complete(true)
                    }
                    .setNegativeButton("Cancel") { _, _ ->
                        userChoice.complete(false)
                    }
                    .setOnCancelListener {
                        userChoice.complete(false)
                    }
                    .setCancelable(true)
                    .create()

                dialog.show()
                userChoice.await()
            } catch (_: Exception) {
                false
            }
        }
    }

    suspend fun deleteSongFile(song: Song): Boolean {
        return metadataEditStateHolder.deleteSong(song)
    }

    suspend fun removeSongFromLibrary(song: Song) {
        libraryStateHolder.removeSong(song.id)
        musicRepository.deleteById(song.id.toLong())
        playlistPreferencesRepository.removeSongFromAllPlaylists(song.id)
    }
}
