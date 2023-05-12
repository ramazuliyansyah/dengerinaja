package com.rama.dengerinaja.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.rama.dengerinaja.R
import com.rama.dengerinaja.adapters.*
import com.rama.dengerinaja.databinding.FragmentSearchBinding
import com.rama.dengerinaja.extensions.getExtraBundle
import com.rama.dengerinaja.extensions.toSongIds
import com.rama.dengerinaja.models.Album
import com.rama.dengerinaja.models.Artist
import com.rama.dengerinaja.models.Song
import com.rama.dengerinaja.utils.Constants
import com.rama.dengerinaja.viewmodels.MainViewModel
import com.rama.dengerinaja.viewmodels.SearchViewModel

class SearchFragment : BaseFragment(), IAlbumsRVAdapter, IArtistsRVAdapter, ISongsRVAdapter {

    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding
        get() = _binding!!
    private val searchViewModel by activityViewModels<SearchViewModel>()
    private var songsAdapter: SongsRVAdapter? = null
    private var albumAdapter = AlbumsRVAdapter(this)
    private var artistAdapter = ArtistsRVAdapter(this)
    private var songs = listOf<Song>()
    private var albums = listOf<Album>()
    private var artists = listOf<Artist>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songsAdapter = SongsRVAdapter(this, this, nowPlayingViewModel)

        setUpRecyclerView()

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(cs: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun onTextChanged(cs: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchViewModel.search(cs.toString())
                if (cs?.length == 0) {
                    searchViewModel.clearResults()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                songsAdapter?.submitList(emptyList())
            }
        })

        searchViewModel.searchResults.observe(viewLifecycleOwner) {
            songs = it.songs
            albums = it.albums
            artists = it.artists

            songsAdapter?.submitList(it.songs)
            albumAdapter.submitList(it.albums)
            artistAdapter.submitList(it.artists)

            if (it.songs.isNullOrEmpty() && it.albums.isNullOrEmpty() && it.artists.isNullOrEmpty()) {
                binding.tvNoResultsText.visibility = View.VISIBLE
                changeTitlesAndDividerVisibility(false)
            } else {
                binding.tvNoResultsText.visibility = View.GONE
                changeTitlesAndDividerVisibility(true)
            }
        }

        binding.ivBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun changeTitlesAndDividerVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.tvSongText.visibility = View.VISIBLE
            binding.dividerLine.visibility = View.VISIBLE
            binding.tvAlbumText.visibility = View.VISIBLE
            binding.dividerLine1.visibility = View.VISIBLE
            binding.tvArtistText.visibility = View.VISIBLE
            binding.dividerLine2.visibility = View.VISIBLE
        } else {
            binding.tvSongText.visibility = View.GONE
            binding.dividerLine.visibility = View.GONE
            binding.tvAlbumText.visibility = View.GONE
            binding.dividerLine1.visibility = View.GONE
            binding.tvArtistText.visibility = View.GONE
            binding.dividerLine2.visibility = View.GONE
        }
    }

    private fun setUpRecyclerView() {
        binding.apply {
            rvSongsSearch.adapter = songsAdapter
            rvSongsSearch.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvAlbumsSearch.adapter = albumAdapter
            rvAlbumsSearch.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            rvArtistSearch.adapter = artistAdapter
            rvArtistSearch.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onSongClicked(song: Song) {
        val extras = getExtraBundle(songs.toSongIds(), getString(R.string.all_songs))
        mainViewModel.mediaItemClicked(song, extras)
    }

    override fun onMoreMenuClicked(song: Song) {
        val songBottomSheetFragment = SongBottomSheetFragment(mainViewModel, viewLifecycleOwner, findNavController(), Constants.CLICK_FROM_SEARCH)
        val bundle = Bundle().apply {
            putParcelable(Constants.SONG_BOTTOM_SHEET_ARG, song)
        }
        songBottomSheetFragment.arguments = bundle
        songBottomSheetFragment.show(requireActivity().supportFragmentManager, "SongBottomSheetFragment")
    }

    override fun onAlbumClick(album: Album) {
        mainViewModel.mediaItemClicked(album, null)
        findNavController().popBackStack()
    }

    override fun onArtistClick(artist: Artist) {
        mainViewModel.mediaItemClicked(artist, null)
        findNavController().popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        searchViewModel.clearResults()
        _binding = null
    }
}