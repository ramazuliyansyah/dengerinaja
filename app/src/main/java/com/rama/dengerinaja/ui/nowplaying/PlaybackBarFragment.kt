package com.rama.dengerinaja.ui.nowplaying

import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.rama.dengerinaja.R
import com.rama.dengerinaja.databinding.FragmentPlaybackBarBinding
import com.rama.dengerinaja.models.MainNavigationAction
import com.rama.dengerinaja.ui.BaseFragment
import com.rama.dengerinaja.utils.SongUtils


class PlaybackBarFragment : BaseFragment() {

    private var _binding: FragmentPlaybackBarBinding? = null
    private val binding: FragmentPlaybackBarBinding
        get() = _binding!!
    private var gradientDrawable: GradientDrawable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPlaybackBarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nowPlayingViewModel.currentData.observe(this) {
            binding.tvSongNameBar.text = it.title
            binding.tvSongArtistBar.text = it.artist
            Glide.with(requireContext()).load(it.artwork).transform(RoundedCorners(12)).into(binding.ivArtworkBar)

            if(it.artwork != null) {
                changeBackground(it.artwork!!)
            } else {
                changeBackground((0xFF616261).toInt())
            }

            if (it.state == PlaybackStateCompat.STATE_PLAYING) {
                binding.ivPlayPauseBar.setImageResource(R.drawable.ic_pause)
            } else {
                binding.ivPlayPauseBar.setImageResource(R.drawable.ic_play)
            }
        }

        attachClickListeners()
    }

    override fun onResume() {
        super.onResume()
        //passing mediaController to seekBar and current time text view to update it
        mainViewModel.mediaController.observe(this) { mediaController ->
            binding.playbackProgressBar.setMediaController(mediaController)
        }
    }

    override fun onStop() {
        binding.playbackProgressBar.disconnectMediaController()
        super.onStop()
    }
    private fun attachClickListeners() {

        binding.ivPlayPauseBar.setOnClickListener {
            nowPlayingViewModel.currentData.value?.let { metaData ->
                mainViewModel.mediaItemClicked(metaData.toDummySong(), null)
            }
        }

        binding.ivNextBar.setOnClickListener {
            mainViewModel.transportControls().skipToNext()
        }

        binding.ivPreviousBar.setOnClickListener {
            mainViewModel.transportControls().skipToPrevious()
        }

        binding.clPlaybackBarRoot.setOnClickListener {
            navigationViewModel.mainNavigateTo(MainNavigationAction.EXPAND)
        }
    }

    private fun changeBackground(bitmap: Bitmap) {
        val getColorPaletteFromSongImage = Palette.from(bitmap).generate()
        changeBackground(SongUtils.getBackgroundColorFromPalette(getColorPaletteFromSongImage))
    }

    private fun changeBackground(color: Int) {
        gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(color, ContextCompat.getColor(requireContext(), R.color.bottom_gradient)))
        binding.clPlaybackBarRoot.background = gradientDrawable
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}