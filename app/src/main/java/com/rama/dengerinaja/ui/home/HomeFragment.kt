package com.rama.dengerinaja.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import com.rama.dengerinaja.R
import com.rama.dengerinaja.adapters.MainViewPagerAdapter
import com.rama.dengerinaja.databinding.FragmentHomeBinding
import com.rama.dengerinaja.extensions.filter
import com.rama.dengerinaja.extensions.map
import com.rama.dengerinaja.models.MediaID
import com.rama.dengerinaja.models.SortType
import com.rama.dengerinaja.repository.SongsRepository
import com.rama.dengerinaja.ui.BaseFragment
import com.rama.dengerinaja.utils.Constants
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding!!
    @Inject lateinit var songsRepository: SongsRepository
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    private var tabs = arrayOf("Songs", "Albums", "Artists", "Playlists")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewPagerAdapter = MainViewPagerAdapter(childFragmentManager, lifecycle)

        binding.vpMain.adapter = mainViewPagerAdapter

        //integrating tabLayout
        TabLayoutMediator(binding.tlMain, binding.vpMain) { tab, position ->
            tab.text = tabs[position]
        }.attach()

        binding.ivSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

        mainViewModel.currentMediaItem
            .map { it.getContentIfNotHandled() }
            .filter { it != null }
            .observe(viewLifecycleOwner) {
                val mediaID = it
                if (mediaID != null) {
                    navigateToMediaItem(mediaID)
                }
            }

        binding.ivSort.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), binding.ivSort)
            popupMenu.menuInflater.inflate(R.menu.sort_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.sort_a_z -> {
                        mainViewModel.updateSortType(SortType.A_Z)
                    }
                    R.id.sort_z_a -> {
                        mainViewModel.updateSortType(SortType.Z_A)
                    }
                    R.id.sort_year -> {
                        mainViewModel.updateSortType(SortType.YEAR)
                    }
                    R.id.sort_duration -> {
                        mainViewModel.updateSortType(SortType.DURATION)
                    }
                }
                true
            }
            popupMenu.show()
        }
    }

    private fun navigateToMediaItem(mediaId: MediaID) {
        when (mediaId.type?.toInt()) {
            Constants.ARTIST_MODE -> {
                val bundle = Bundle().apply {
                    putString(Constants.MEDIA_TYPE, mediaId.type)
                    putString(Constants.MEDIA_ID, mediaId.mediaId)
                    putString(Constants.MEDIA_CALLER, mediaId.caller)
                    putParcelable(Constants.ARTIST, mediaId.mediaItem)
                }
                findNavController().navigate(R.id.action_homeFragment_to_artistDetailsFragment, bundle)
            }
            Constants.ALBUM_MODE -> {
                val bundle = Bundle().apply {
                    putString(Constants.MEDIA_TYPE, mediaId.type)
                    putString(Constants.MEDIA_ID, mediaId.mediaId)
                    putString(Constants.MEDIA_CALLER, mediaId.caller)
                    putParcelable(Constants.ALBUM, mediaId.mediaItem)
                }
                findNavController().navigate(R.id.action_homeFragment_to_albumDetailsFragment, bundle)
            }
            Constants.PLAYLIST_MODE -> {
                val bundle = Bundle().apply {
                    putString(Constants.MEDIA_TYPE, mediaId.type)
                    putString(Constants.MEDIA_ID, mediaId.mediaId)
                    putString(Constants.MEDIA_CALLER, mediaId.caller)
                    putParcelable(Constants.PLAYLIST, mediaId.mediaItem)
                }
                findNavController().navigate(R.id.action_homeFragment_to_playlistDetailsFragment, bundle)
            }
            else -> {}
        }
    }
}