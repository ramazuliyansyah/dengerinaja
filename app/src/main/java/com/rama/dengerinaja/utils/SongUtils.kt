package com.rama.dengerinaja.utils

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.palette.graphics.Palette
import com.rama.dengerinaja.R
import com.rama.dengerinaja.models.Song
import java.io.FileNotFoundException

object SongUtils {

    fun getSongUri(id: Long): Uri {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
    }

    fun getAlbumArtUri(albumId: Long): Uri {
        val artworkUri = Uri.parse("content://media/external/audio/albumart")
        return ContentUris.withAppendedId(artworkUri, albumId)
    }

    fun getAlbumArtBitmap(context: Context, albumId: Long?): Bitmap? {
        if (albumId == null)
            return null

        return try {
            if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, getAlbumArtUri(albumId))
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, getAlbumArtUri(albumId))
                ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.RGBA_F16, true)
            }
        } catch (e: FileNotFoundException) {
            BitmapFactory.decodeResource(context.resources, R.drawable.ic_album_disk)
        }
    }

    fun formatTimeStringShort(secs: Long): String {
        val s = secs / 1000
        return String.format("%d:%02d", s/60, s % 60)
    }

    fun getSongWithId(songs: List<Song>, id: Long): Song? {
        for (i in songs) {
            if (i.id == id)
                return i
        }
        return null
    }

    fun getTotalDuration(songs: List<Song>): String {
        var totalTime: Long = 0
        for (i in songs) {
            totalTime += i.duration
        }
        return formatTimeStringShort(totalTime)
    }

    fun getBackgroundColorFromPalette(palette: Palette): Int {
        var background = palette.getDarkVibrantColor(0xFF616261.toInt())

        if (background == (0xFF616261).toInt())
            background = palette.getDarkMutedColor(0xFF616261.toInt())

        if (background == (0xFF616261).toInt())
            background = palette.getVibrantColor(0xFF616261.toInt())

        if (background == (0xFF616261).toInt())
            background = palette.getMutedColor(0xFF616261.toInt())

        return background
    }
}