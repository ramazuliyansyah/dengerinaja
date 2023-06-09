package com.rama.dengerinaja.extensions

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import com.rama.dengerinaja.models.QueuedSongsEntity
import com.rama.dengerinaja.models.Song
import com.rama.dengerinaja.repository.SongsRepository
import com.rama.dengerinaja.utils.SongUtils

fun List<Song?>.toQueue(): List<MediaSessionCompat.QueueItem> {
    return filterNotNull().map {
        MediaSessionCompat.QueueItem(it.toDescription(), it.id)
    }
}

fun Song.toDescription(): MediaDescriptionCompat {
    return MediaDescriptionCompat.Builder()
        .setTitle(title)
        .setMediaId(id.toString())
        .setSubtitle(artist)
        .setDescription(album)
        .setIconUri(SongUtils.getAlbumArtUri(albumId)).build()
}

fun List<QueuedSongsEntity>.toSongIDs() = map { it.songId }.toLongArray()

fun List<Song>.toSongIds() = map { it.id }.toLongArray() ?: LongArray(0)

fun List<MediaSessionCompat.QueueItem>?.toIDList(): LongArray {
    return this?.map { it.queueId }?.toLongArray() ?: LongArray(0)
}

fun Song.toSongEntity() = QueuedSongsEntity(null, this.id)

fun List<Song>.toQueuedSongsList() = map { it.toSongEntity() }

fun LongArray.toQueuedSongsList(songsRepository: SongsRepository): List<QueuedSongsEntity> {
    return songsRepository.getSongsForIds(this).toQueuedSongsList()
}
