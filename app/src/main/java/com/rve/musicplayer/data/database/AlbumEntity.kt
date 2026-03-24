package com.rve.musicplayer.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rve.musicplayer.data.model.Album
import com.rve.musicplayer.utils.LocalArtworkUri
import com.rve.musicplayer.utils.normalizeMetadataTextOrEmpty

@Entity(
    tableName = "albums",
    indices = [
        Index(value = ["title"], unique = false),
        Index(value = ["artist_id"], unique = false), // Para buscar álbumes por artista
        Index(value = ["artist_name"], unique = false) // Nuevo índice para búsquedas por nombre de artista del álbum
    ]
)
data class AlbumEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "artist_name") val artistName: String, // Nombre del artista del álbum
    @ColumnInfo(name = "artist_id") val artistId: Long, // ID del artista principal del álbum (si aplica)
    @ColumnInfo(name = "album_art_uri_string") val albumArtUriString: String?,
    @ColumnInfo(name = "song_count") val songCount: Int,
    @ColumnInfo(name = "year") val year: Int
)

private fun mediaStoreAlbumArtUri(albumId: Long): String {
    return android.content.ContentUris.withAppendedId(
        android.net.Uri.parse("content://media/external/audio/albumart"),
        albumId
    ).toString()
}

fun AlbumEntity.toAlbum(): Album {
    val effectiveAlbumArtUri = when {
        this.albumArtUriString.isNullOrBlank() -> mediaStoreAlbumArtUri(this.id)
        LocalArtworkUri.looksLikeVolatileArtworkUri(this.albumArtUriString) -> mediaStoreAlbumArtUri(this.id)
        else -> this.albumArtUriString
    }

    return Album(
        id = this.id,
        title = this.title.normalizeMetadataTextOrEmpty(),
        artist = this.artistName.normalizeMetadataTextOrEmpty(),
        albumArtUriString = effectiveAlbumArtUri,
        songCount = this.songCount,
        year = this.year
    )
}

fun List<AlbumEntity>.toAlbums(): List<Album> {
    return this.map { it.toAlbum() }
}

fun Album.toEntity(artistIdForAlbum: Long): AlbumEntity { // Necesitamos pasar el artistId si el modelo Album no lo tiene directamente
    return AlbumEntity(
        id = this.id,
        title = this.title,
        artistName = this.artist,
        artistId = artistIdForAlbum, // Asignar el ID del artista
        albumArtUriString = this.albumArtUriString,
        songCount = this.songCount,
        year = this.year
    )
}
