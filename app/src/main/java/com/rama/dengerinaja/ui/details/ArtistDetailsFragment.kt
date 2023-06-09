package com.rama.dengerinaja.ui.details

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.rama.dengerinaja.R
import com.rama.dengerinaja.adapters.ArtistAlbumsRVAdapter
import com.rama.dengerinaja.adapters.IArtistAlbumsRVAdapter
import com.rama.dengerinaja.adapters.ISongsRVAdapter
import com.rama.dengerinaja.adapters.SongsRVAdapter
import com.rama.dengerinaja.databinding.FragmentArtistDetailsBinding
import com.rama.dengerinaja.extensions.filter
import com.rama.dengerinaja.extensions.getExtraBundle
import com.rama.dengerinaja.extensions.toSongIds
import com.rama.dengerinaja.models.Album
import com.rama.dengerinaja.models.Artist
import com.rama.dengerinaja.models.Song
import com.rama.dengerinaja.ui.BaseFragment
import com.rama.dengerinaja.ui.SongBottomSheetFragment
import com.rama.dengerinaja.utils.Constants
import com.rama.dengerinaja.utils.SongUtils

class ArtistDetailsFragment : BaseFragment(), IArtistAlbumsRVAdapter, ISongsRVAdapter {

    private var _binding: FragmentArtistDetailsBinding? = null
    private val binding: FragmentArtistDetailsBinding
        get() = _binding!!
    private var artist: Artist? = null
    private var caller: String? = null
    private var songs = listOf<Song>()
    private var songsAdapter: SongsRVAdapter? = null
    private var artistAlbumAdapter: ArtistAlbumsRVAdapter = ArtistAlbumsRVAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentArtistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        artist = arguments?.getParcelable(Constants.ARTIST)
        caller = arguments?.getString(Constants.MEDIA_CALLER)

        if (artist != null && caller != null) {
            binding.tvArtistName.text = artist?.name

            var albumText = "album"
            var songText = "song"
            if (artist?.noOfAlbums!! > 1) {
                albumText = "albums"
            }
            if (artist?.noOfSongs!! > 1) {
                songText = "songs"
            }
            binding.tvArtistInfo.text = context?.getString(
                R.string.format_two,
                "${artist?.noOfAlbums} $albumText",
                "${artist?.noOfSongs} $songText"
            )

            artist?.albumIds?.let { addArtwork(it) }

            songsAdapter = SongsRVAdapter(this, this, nowPlayingViewModel)

            setUpRecyclerView()

            //getting songs for artist
            mainViewModel.getArtistSongs(caller!!, artist?.id!!)

            mainViewModel.artistSongs.observe(viewLifecycleOwner) {
                if (it.isNullOrEmpty()) {
                    binding.tvNoSongsText.visibility = View.VISIBLE
                } else {
                    binding.tvNoSongsText.visibility = View.GONE
                }
                songs = it
                songsAdapter?.submitList(it)
            }

            //getting albums for artist
            mainViewModel.getArtistAlbums(artist?.id!!)

            mainViewModel.artistAlbums.observe(viewLifecycleOwner) {
                if (it.isNullOrEmpty()) {
                    binding.tvNoAlbumsText.visibility = View.VISIBLE
                } else {
                    binding.tvNoAlbumsText.visibility = View.GONE
                }
                artistAlbumAdapter.submitList(it)
            }

            playbackSessionViewModel?.mediaItems
                ?.filter { it.isNotEmpty() }
                ?.observe(this) {
                    songsAdapter?.submitList(it as List<Song>)
                }

            binding.ivBackButton.setOnClickListener {
                findNavController().popBackStack()
            }

        } else {
            findNavController().popBackStack()
        }
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
            rvArtistSongs.adapter = songsAdapter
            rvArtistSongs.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvArtistAlbums.adapter = artistAlbumAdapter
            rvArtistAlbums.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onArtistAlbumClick(album: Album) {
        mainViewModel.mediaItemClicked(album, null)
        findNavController().popBackStack()
    }

    override fun onSongClicked(song: Song) {
        val extras = getExtraBundle(songs.toSongIds(), artist?.name!!)
        mainViewModel.mediaItemClicked(song, extras)
    }

    override fun onMoreMenuClicked(song: Song) {
        val songBottomSheetFragment = SongBottomSheetFragment(mainViewModel, viewLifecycleOwner, findNavController(), Constants.CLICK_FROM_ARTIST_DETAILS)
        val bundle = Bundle().apply {
            putParcelable(Constants.SONG_BOTTOM_SHEET_ARG, song)
        }
        songBottomSheetFragment.arguments = bundle
        songBottomSheetFragment.show(requireActivity().supportFragmentManager, "SongBottomSheetFragment")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}