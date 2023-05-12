package com.rama.dengerinaja.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.rama.dengerinaja.R
import com.rama.dengerinaja.adapters.IPlaylistsRVAdapter
import com.rama.dengerinaja.adapters.PlaylistsRVAdapter
import com.rama.dengerinaja.databinding.FragmentPlaylistsBinding
import com.rama.dengerinaja.extensions.filter
import com.rama.dengerinaja.models.Playlist
import com.rama.dengerinaja.ui.BaseFragment


class PlaylistsFragment : BaseFragment(), IPlaylistsRVAdapter {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding: FragmentPlaylistsBinding
        get() = _binding!!
    private val playlistAdapter = PlaylistsRVAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        mainViewModel.loadPlaylists()

        mainViewModel.playlists.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.tvNoPlaylistAdded.visibility = View.GONE
            }
            playlistAdapter.submitList(it)
        }

        playbackSessionViewModel?.mediaItems
            ?.filter { it.isNotEmpty() }
            ?.observe(this) { list ->
                @Suppress("UNCHECKED_CAST")
                playlistAdapter.submitList(list as List<Playlist>)
            }
    }

    private fun setUpRecyclerView() {
        binding.apply {
            rvAllPlaylists.adapter = playlistAdapter
            rvAllPlaylists.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        }
    }

    override fun onPlaylistClicked(playlist: Playlist) {
        mainViewModel.mediaItemClicked(playlist, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}