package com.rama.dengerinaja.viewmodels

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import com.rama.dengerinaja.extensions.isPlayEnabled
import com.rama.dengerinaja.extensions.isPlaying
import com.rama.dengerinaja.extensions.isPrepared
import com.rama.dengerinaja.extensions.map
import com.rama.dengerinaja.models.*
import com.rama.dengerinaja.playback.player.PlaybackSessionConnector
import com.rama.dengerinaja.playback.player.SongPlayer
import com.rama.dengerinaja.repository.AlbumRepository
import com.rama.dengerinaja.repository.ArtistRepository
import com.rama.dengerinaja.repository.PlaylistRepository
import com.rama.dengerinaja.repository.SongsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
    private val albumsRepository: AlbumRepository,
    private val artistsRepository: ArtistRepository,
    private val playlistRepository: PlaylistRepository,
    private val playbackSessionConnector: PlaybackSessionConnector,
    private val songPlayer: SongPlayer
): ViewModel() {

    val rootMediaId: LiveData<MediaID> = playbackSessionConnector.isConnected.map { isConnected ->
        if (isConnected) {
            MediaID().fromString(playbackSessionConnector.rootMediaId)
        } else {
            null
        }
    }

    val mediaController: LiveData<MediaControllerCompat> = playbackSessionConnector.isConnected.map { isConnected ->
        if (isConnected) {
            playbackSessionConnector.mediaController
        } else {
            null
        }
    }

    var isBeingReordered = false

    private val _sortType: MutableLiveData<SortType> = MutableLiveData()
    val sortType: LiveData<SortType> = _sortType

    private val _songs: MutableLiveData<List<Song>> =  MutableLiveData()
    val songs: LiveData<List<Song>> = _songs

    private val _albumSongs: MutableLiveData<List<Song>> =  MutableLiveData()
    val albumSongs: LiveData<List<Song>> = _albumSongs

    private val _artistSongs: MutableLiveData<List<Song>> =  MutableLiveData()
    val artistSongs: LiveData<List<Song>> = _artistSongs

    private val _albums: MutableLiveData<List<Album>> = MutableLiveData()
    val albums: LiveData<List<Album>> = _albums

    private val _artistAlbums: MutableLiveData<List<Album>> = MutableLiveData()
    val artistAlbums: LiveData<List<Album>> = _artistAlbums

    private val _artists: MutableLiveData<List<Artist>> = MutableLiveData()
    val artists: LiveData<List<Artist>> = _artists

    private val _songsForIds: MutableLiveData<List<Song>> =  MutableLiveData()
    val songsForIds: LiveData<List<Song>> = _songsForIds

    private val _songForId: MutableLiveData<Song> = MutableLiveData()
    val songForId: LiveData<Song> = _songForId

    private val _playlists: MutableLiveData<List<Playlist>> = MutableLiveData()
    val playlists: LiveData<List<Playlist>> = _playlists

    private val _playlistSongs: MutableLiveData<List<Song>> =  MutableLiveData()
    val playlistSongs: LiveData<List<Song>> = _playlistSongs

    private val _navigateToMediaItem = MutableLiveData<Event<MediaID>>()
    val navigateToMediaItem: LiveData<Event<MediaID>> = _navigateToMediaItem

    private val _currentMediaItem = MutableLiveData<Event<MediaID>>()
    val currentMediaItem: LiveData<Event<MediaID>> = _currentMediaItem

    private val _customAction = MutableLiveData<Event<String>>()
    val customAction: LiveData<Event<String>> = _customAction

    fun transportControls() = playbackSessionConnector.transportControls

    fun mediaItemClicked(clickedItem: MediaBrowserCompat.MediaItem, extras: Bundle?) {
        if (clickedItem.isBrowsable) {
            browseToItem(clickedItem)
        } else {
            playMedia(clickedItem, extras)
        }
    }

    private fun browseToItem(clickedItem: MediaBrowserCompat.MediaItem) {
        _navigateToMediaItem.value = Event(MediaID().fromString(clickedItem.mediaId!!).apply {
            this.mediaItem = clickedItem
        })
        _currentMediaItem.postValue(Event(MediaID().fromString(clickedItem.mediaId!!).apply {
            this.mediaItem = clickedItem
        }))
    }

    private fun playMedia(mediaItem: MediaBrowserCompat.MediaItem, extras: Bundle?) {
        val nowPlaying = playbackSessionConnector.nowPlaying.value
        val transportControls = playbackSessionConnector.transportControls
        val isPrepared = playbackSessionConnector.playbackState.value?.isPrepared ?: false

        if (isPrepared && MediaID().fromString(mediaItem.mediaId!!).mediaId == nowPlaying?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)) {
            playbackSessionConnector.playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> transportControls.pause()
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> {
                        Log.d("TAGYOYO", "Media item clicked can be neither played nor paused is enabled: mediaId=${mediaItem.mediaId}")
                    }
                }
            }
        } else {
            transportControls.playFromMediaId(mediaItem.mediaId, extras)
        }
    }

    fun updateSortType(sortType: SortType) {
        _sortType.postValue(sortType)
    }

    fun addSongToQueue(songId: Long) {
        val queueIds = songPlayer.getQueueIds()
        val resultList = queueIds.toMutableList()
        resultList.add(songId)

        val resultLongArray = resultList.toLongArray()
        songPlayer.setQueue(resultLongArray, "Custom Queue")
    }

    //song
    fun loadSongs(sortType: SortType) = viewModelScope.launch(Dispatchers.IO) {
        _songs.postValue(songsRepository.getSongs(MediaID.CALLER_SELF, sortType))
    }

    fun getSongForIds(ids: LongArray) = viewModelScope.launch {
        _songsForIds.postValue(songsRepository.getSongsForIds(ids))
    }

    fun getSongForId(id: Long) = viewModelScope.launch {
        _songForId.postValue(songsRepository.getSongForId(id))
    }

    //album
    fun loadAlbums() = viewModelScope.launch(Dispatchers.IO) {
        _albums.postValue(albumsRepository.getAllAlbums(MediaID.CALLER_SELF))
    }

    fun getAlbumSongs(caller: String, albumId: Long) {
        _albumSongs.postValue(albumsRepository.getSongsForAlbum(caller, albumId))
    }

    //playlist
    fun loadPlaylists() = viewModelScope.launch(Dispatchers.IO) {
        _playlists.postValue(playlistRepository.getAllPlaylists(MediaID.CALLER_SELF))
    }

    fun createPlaylist(name: String?): Long {
        return playlistRepository.createPlaylist(name)
    }

    fun getPlaylistSongs(caller: String?, playlistId: Long) {
        _playlistSongs.postValue(playlistRepository.getSongsInPlaylist(caller, playlistId))
    }

    fun addToPlaylist(playlistId: Long, ids: LongArray) {
        playlistRepository.addToPlaylist(playlistId, ids)
    }

    fun deletePlaylist(playlistId: Long) {
        playlistRepository.deletePlaylist(playlistId)
    }

    fun deleteSongFromPlaylist(songId: Long, playlistId: Long) {
        playlistRepository.deleteTrackFromPlaylist(songId, playlistId)
    }

    //artist
    fun loadArtists() = viewModelScope.launch(Dispatchers.IO) {
        _artists.postValue(artistsRepository.getAllArtists(MediaID.CALLER_SELF))
    }

    fun getArtistSongs(caller: String, artistId: Long) {
        _artistSongs.postValue(artistsRepository.getSongsForArtist(caller, artistId))
    }

    fun getArtistAlbums(artistId: Long) {
        _artistAlbums.postValue(albumsRepository.getAlbumsForArtist(artistId))
    }

    //bottom sheet
    fun goToAlbum(song: Song) {
        browseToItem(albumsRepository.getAlbum(song.albumId))
    }

    fun goToArtist(song: Song) {
        browseToItem(artistsRepository.getArtist(song.artistId))
    }
}