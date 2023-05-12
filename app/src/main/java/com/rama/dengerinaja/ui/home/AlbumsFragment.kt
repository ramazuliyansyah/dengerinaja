package com.rama.dengerinaja.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.rama.dengerinaja.adapters.AlbumsRVAdapter
import com.rama.dengerinaja.adapters.IAlbumsRVAdapter
import com.rama.dengerinaja.databinding.FragmentAlbumsBinding
import com.rama.dengerinaja.extensions.filter
import com.rama.dengerinaja.models.Album
import com.rama.dengerinaja.ui.BaseFragment

class AlbumsFragment : BaseFragment(), IAlbumsRVAdapter {

    private var _binding: FragmentAlbumsBinding? = null
    private val binding: FragmentAlbumsBinding
        get() = _binding!!
    private var albumsAdapter = AlbumsRVAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAlbumsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        mainViewModel.loadAlbums()

        mainViewModel.albums.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                binding.tvNoAlbumsText.visibility = View.VISIBLE
            } else {
                binding.tvNoAlbumsText.visibility = View.GONE
                albumsAdapter.submitList(it)
            }
        }

        playbackSessionViewModel?.mediaItems
            ?.filter { it.isNotEmpty() }
            ?.observe(this) {
                albumsAdapter.submitList(it as List<Album>)
            }
    }

    private fun setUpRecyclerView() {
        binding.apply {
            rvAllAlbums.adapter = albumsAdapter
            rvAllAlbums.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onAlbumClick(album: Album) {
        mainViewModel.mediaItemClicked(album, null)
    }
}