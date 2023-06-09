package com.rama.dengerinaja.ui.details

import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.rama.dengerinaja.R
import com.rama.dengerinaja.adapters.ISongsRVAdapter
import com.rama.dengerinaja.adapters.SongsRVAdapter
import com.rama.dengerinaja.databinding.FragmentPlaylistDetailsBinding
import com.rama.dengerinaja.extensions.filter
import com.rama.dengerinaja.extensions.getExtraBundle
import com.rama.dengerinaja.extensions.toSongIds
import com.rama.dengerinaja.models.Playlist
import com.rama.dengerinaja.models.Song
import com.rama.dengerinaja.ui.BaseFragment
import com.rama.dengerinaja.ui.SongBottomSheetFragment
import com.rama.dengerinaja.utils.Constants
import com.rama.dengerinaja.utils.SongUtils

class PlaylistDetailsFragment : BaseFragment(), ISongsRVAdapter {

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding: FragmentPlaylistDetailsBinding
        get() = _binding!!
    private var playlist: Playlist? = null
    private var caller: String? = null
    private var songs = listOf<Song>()
    private var songsAdapter: SongsRVAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlist = arguments?.getParcelable(Constants.PLAYLIST)
        caller = arguments?.getString(Constants.MEDIA_CALLER)

        if (playlist != null && caller != null) {
            binding.tvPlaylistName.text = playlist?.name

            var songText = "song"
            if (playlist?.noOfSongs!! > 1) {
                songText = "songs"
            }

            playlist?.albumIds?.let { addArtwork(it) }

            songsAdapter = SongsRVAdapter(this, this, nowPlayingViewModel)

            setUpRecyclerView()

            //getting songs for playlist
            mainViewModel.getPlaylistSongs(caller!!, playlist?.id!!)

            mainViewModel.playlistSongs.observe(viewLifecycleOwner) {
                songs = it
                songsAdapter?.submitList(it)
                val totalTime = SongUtils.getTotalDuration(songs)
                binding.tvPlaylistInfo.text = context?.getString(
                    R.string.format_two,
                    "${playlist?.noOfSongs} $songText",
                    totalTime
                )
            }

            playbackSessionViewModel?.mediaItems
                ?.filter { it.isNotEmpty() }
                ?.observe(this) {
                    songsAdapter?.submitList(it as List<Song>)
                }

            binding.ivBackButton.setOnClickListener {
                findNavController().popBackStack()
            }

            binding.ivDeletePlaylist.setOnClickListener {
                showDeleteDialog()
            }
        }
    }

    private fun showDeleteDialog() {
        val alertBuilder = AlertDialog.Builder(requireContext())
        val dialogLayout = layoutInflater.inflate(R.layout.delete_playlist_dialog_layout, null)

        val tvDeletePlaylist: TextView = dialogLayout.findViewById(R.id.tvConfirmDeleteText)
        val btnDelete: TextView = dialogLayout.findViewById(R.id.tvDeleteDialog)
        val btnCancel: TextView = dialogLayout.findViewById(R.id.tvCancelDialogDelete)

        tvDeletePlaylist.text = "Delete ${playlist?.name}?"

        alertBuilder.setView(dialogLayout)
        val deleteDialog = alertBuilder.create()
        deleteDialog.setCanceledOnTouchOutside(false)

        btnDelete.setOnClickListener {
            mainViewModel.deletePlaylist(playlist?.id!!)
            deleteDialog.dismiss()
            findNavController().popBackStack()
        }

        btnCancel.setOnClickListener {
            deleteDialog.dismiss()
        }

        deleteDialog.show()
    }

    private fun addArtwork(albumIds: List<Long>) {
        val images = mutableListOf<Bitmap>()
        for (i in albumIds) {
            val bitmap = SongUtils.getAlbumArtBitmap(requireContext(), i)
            if (bitmap != null) {
                images.add(bitmap)
            }
        }

        when (images.size) {
            0 -> {
                binding.llArtworkBottomLayer.visibility = View.GONE
                binding.ivAlbumsArtwork2.visibility = View.GONE
                Glide.with(requireContext()).load(R.drawable.ic_album_disk).into(binding.ivAlbumsArtwork1)
                binding.ivAlbumsArtwork1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorAccent))
            }
            1 -> {
                binding.llArtworkBottomLayer.visibility = View.GONE
                binding.ivAlbumsArtwork2.visibility = View.GONE
                Glide.with(requireContext()).load(images[0]).into(binding.ivAlbumsArtwork1)
            }
            2 -> {
                binding.llArtworkBottomLayer.visibility = View.GONE
                Glide.with(requireContext()).load(images[0]).into(binding.ivAlbumsArtwork1)
                Glide.with(requireContext()).load(images[1]).into(binding.ivAlbumsArtwork2)
            }
            3 -> {
                binding.ivAlbumsArtwork4.visibility = View.GONE
                Glide.with(requireContext()).load(images[0]).into(binding.ivAlbumsArtwork1)
                Glide.with(requireContext()).load(images[1]).into(binding.ivAlbumsArtwork2)
                Glide.with(requireContext()).load(images[2]).into(binding.ivAlbumsArtwork3)
            }
            4 -> {
                Glide.with(requireContext()).load(images[0]).into(binding.ivAlbumsArtwork1)
                Glide.with(requireContext()).load(images[1]).into(binding.ivAlbumsArtwork2)
                Glide.with(requireContext()).load(images[2]).into(binding.ivAlbumsArtwork3)
                Glide.with(requireContext()).load(images[3]).into(binding.ivAlbumsArtwork4)
            }
            else -> {
                Glide.with(requireContext()).load(images[0]).into(binding.ivAlbumsArtwork1)
                Glide.with(requireContext()).load(images[1]).into(binding.ivAlbumsArtwork2)
                Glide.with(requireContext()).load(images[2]).into(binding.ivAlbumsArtwork3)
                Glide.with(requireContext()).load(images[3]).into(binding.ivAlbumsArtwork4)
            }
        }
    }

    private fun setUpRecyclerView() {
        binding.apply {
            rvPlaylistSongs.adapter = songsAdapter
            rvPlaylistSongs.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onSongClicked(song: Song) {
        val extras = getExtraBundle(songs.toSongIds(), playlist?.name!!)
        mainViewModel.mediaItemClicked(song, extras)
    }

    override fun onMoreMenuClicked(song: Song) {
        val songBottomSheetFragment = SongBottomSheetFragment(mainViewModel, viewLifecycleOwner, findNavController(), Constants.CLICK_FROM_PLAYLIST_DETAILS)
        val bundle = Bundle().apply {
            putParcelable(Constants.SONG_BOTTOM_SHEET_ARG, song)
            putLong(Constants.PLAYLIST_ID_BOTTOM_SHEET_ARG, playlist?.id!!)
        }
        songBottomSheetFragment.arguments = bundle
        songBottomSheetFragment.show(requireActivity().supportFragmentManager, "SongBottomSheetFragment")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}