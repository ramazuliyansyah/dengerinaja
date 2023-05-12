package com.rama.dengerinaja.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rama.dengerinaja.ui.home.AlbumsFragment
import com.rama.dengerinaja.ui.home.ArtistsFragment
import com.rama.dengerinaja.ui.home.PlaylistsFragment
import com.rama.dengerinaja.ui.home.SongsFragment
import com.rama.dengerinaja.utils.Constants

class MainViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return Constants.MAIN_VIEW_PAGER_SIZE
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return SongsFragment()
            1 -> return AlbumsFragment()
            2 -> return ArtistsFragment()
        }
        return PlaylistsFragment()
    }

}