package com.rama.dengerinaja.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.rama.dengerinaja.adapters.ArtistsRVAdapter
import com.rama.dengerinaja.adapters.IArtistsRVAdapter
import com.rama.dengerinaja.databinding.FragmentArtistsBinding
import com.rama.dengerinaja.extensions.filter
import com.rama.dengerinaja.models.Artist
import com.rama.dengerinaja.ui.BaseFragment

class ArtistsFragment : BaseFragment(), IArtistsRVAdapter {

    private var _binding: FragmentArtistsBinding? = null
    private val binding: FragmentArtistsBinding
        get() = _binding!!
    private val artistsAdapter = ArtistsRVAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentArtistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        mainViewModel.loadArtists()

        mainViewModel.artists.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                binding.tvNoArtistsText.visibility = View.VISIBLE
            } else {
                binding.tvNoArtistsText.visibility = View.GONE
                artistsAdapter.submitList(it)
            }
        }

        playbackSessionViewModel?.mediaItems
            ?.filter { it.isNotEmpty() }
            ?.observe(this) {
                artistsAdapter.submitList(it as List<Artist>)
            }
    }

    private fun setUpRecyclerView() {
        binding.apply {
            rvAllArtists.adapter = artistsAdapter
            rvAllArtists.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onArtistClick(artist: Artist) {
        mainViewModel.mediaItemClicked(artist, null)
    }
}