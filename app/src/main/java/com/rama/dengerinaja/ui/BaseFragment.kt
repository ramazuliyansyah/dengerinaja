package com.rama.dengerinaja.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.rama.dengerinaja.R
import com.rama.dengerinaja.extensions.filter
import com.rama.dengerinaja.extensions.map
import com.rama.dengerinaja.extensions.safeActivity
import com.rama.dengerinaja.models.MediaID
import com.rama.dengerinaja.playback.player.PlaybackSessionConnector
import com.rama.dengerinaja.utils.Constants
import com.rama.dengerinaja.viewmodels.*
import javax.inject.Inject

open class BaseFragment: Fragment() {

    protected val mainViewModel by activityViewModels<MainViewModel>()
    protected val navigationViewModel by activityViewModels<NavigationViewModel>()
    protected val nowPlayingViewModel by activityViewModels<NowPlayingViewModel>()
    @Inject lateinit var playbackSessionConnector: PlaybackSessionConnector
    protected var playbackSessionViewModel: PlaybackSessionViewModel? = null
    private var mediaID: MediaID? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.navigateToMediaItem
            .map { it.getContentIfNotHandled() }
            .filter { it != null }
            .observe(this) {
                mediaID = it
                if (mediaID != null) {
                    initializePlaybackSession(mediaID!!)
                }
            }
    }

    private fun initializePlaybackSession(mediaId: MediaID) {
        playbackSessionViewModel = ViewModelProvider(this, PlaybackSessionProviderFactory(mediaId, playbackSessionConnector)).get(PlaybackSessionViewModel::class.java)
    }
}