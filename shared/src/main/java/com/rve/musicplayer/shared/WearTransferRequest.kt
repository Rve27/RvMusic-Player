package com.rve.musicplayer.shared

import kotlinx.serialization.Serializable

/**
 * Request sent from the watch to the phone to initiate a song transfer.
 * The phone will validate the song, send metadata, and stream the audio file.
 */
@Serializable
data class WearTransferRequest(
    val requestId: String,
    val songId: String,
)
