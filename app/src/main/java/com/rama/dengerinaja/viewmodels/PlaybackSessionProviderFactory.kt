package com.rama.dengerinaja.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import com.rama.dengerinaja.models.MediaID
import com.rama.dengerinaja.playback.player.PlaybackSessionConnector

class PlaybackSessionProviderFactory(private val mediaID: MediaID, private val playbackSessionConnector: PlaybackSessionConnector): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlaybackSessionViewModel(mediaID, playbackSessionConnector) as T
    }
}