package com.rama.dengerinaja.repository

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.rama.dengerinaja.extensions.*
import com.rama.dengerinaja.models.Artist
import com.rama.dengerinaja.models.MediaID
import com.rama.dengerinaja.models.Song

class ArtistRepository(private val context: Context) {

    fun getAllArtists(caller: String?): List<Artist> {
        if (caller != null)
            MediaID.currentCaller = caller

        val cursor = makeArtistCursor(null, null)
        val artists = arrayListOf<Artist>()

        if (cursor!= null && cursor.moveToFirst()) {
            do {
                artists.add(getArtistFromCursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor?.close()

        return artists
    }

    fun getArtist(id: Long): Artist {
        val cursor = makeArtistCursor("_id=?", arrayOf(id.toString()))
        var artist = Artist()
        if (cursor!= null && cursor.moveToFirst()) {
            artist = getArtistFromCursor(cursor)
        }
        cursor?.close()
        return artist
    }

    fun searchArtist(searchQuery: String, limit: Int): List<Artist> {
        val cursor = makeArtistCursor("artist LIKE ?", arrayOf("$searchQuery%"))
        val artists = arrayListOf<Artist>()
        if (cursor!= null && cursor.moveToFirst()) {
            do {
                artists.add(getArtistFromCursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor?.close()

        if (artists.size < limit) {
            val moreCursor = makeArtistCursor("artist LIKE ?", arrayOf("%_$searchQuery%"))
            val moreArtists = arrayListOf<Artist>()
            if (moreCursor!= null && moreCursor.moveToFirst()) {
                do {
                    moreArtists.add(getArtistFromCursor(moreCursor))
                } while (moreCursor.moveToNext())
            }
            cursor?.close()
            artists += moreArtists
        }
        return if (artists.size < limit) {
            artists
        }  else {
            artists.subList(0, limit)
        }
    }

    fun getSongsForArtist(caller: String?, artistId: Long): List<Song> {
        MediaID.currentCaller = caller
        val cursor = makeArtistSongCursor(artistId)
        val songs = arrayListOf<Song>()

        if (cursor!= null && cursor.moveToFirst()) {
            do {
                songs.add(getSongFromCursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return songs
    }

    private fun makeArtistCursor(selection: String?, paramArrayOfString: Array<String>?): Cursor? {
        return context.contentResolver.query(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
            arrayOf("_id", "artist", "number_of_albums", "number_of_tracks"),
            selection,
            paramArrayOfString,
            MediaStore.Audio.Artists.DEFAULT_SORT_ORDER
        )
    }

    private fun makeArtistSongCursor(artistId: Long): Cursor? {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "is_music=1 AND title != '' AND artist_id=$artistId"
        return context.contentResolver.query(
            uri,
            arrayOf("_id", "title", "artist", "album", "duration", "track", "album_id", "artist_id"),
            selection,
            null,
            MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        )
    }

    private fun getArtistFromCursor(cursor: Cursor): Artist {
        val id = cursor.getLong(MediaStore.Audio.AudioColumns._ID)
        val artistName = cursor.getStringOrNull(MediaStore.Audio.AlbumColumns.ARTIST)
        val noOfSongs = cursor.getInt(MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS)
        val noOfAlbums = cursor.getInt(MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS)

        val songs = getSongsForArtist(MediaID.CALLER_SELF, id)
        val albumsIds = mutableListOf<Long>()
        for (i in songs) {
            albumsIds.add(i.albumId)
        }

        return Artist(
            id,
            artistName ?: "",
            noOfSongs,
            noOfAlbums,
            albumsIds
        )
    }

    private fun getSongFromCursor(cursor: Cursor): Song {
        val id = cursor.getLong(MediaStore.Audio.AudioColumns._ID)
        val title = cursor.getString(MediaStore.Audio.AudioColumns.TITLE)
        val artistName = cursor.getStringOrNull(MediaStore.Audio.AudioColumns.ARTIST)
        val albumName = cursor.getStringOrNull(MediaStore.Audio.AudioColumns.ALBUM)
        val duration = cursor.getLong(MediaStore.Audio.AudioColumns.DURATION)
        val albumId = cursor.getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)
        val artistId = cursor.getLong(MediaStore.Audio.AudioColumns.ARTIST_ID)
        val trackNumber = cursor.getInt(MediaStore.Audio.AudioColumns.TRACK).normalizeTrackNumber()

        return Song(
            id,
            title,
            artistName ?: "",
            albumName ?: "",
            duration,
            albumId,
            artistId,
            trackNumber
        )
    }
}