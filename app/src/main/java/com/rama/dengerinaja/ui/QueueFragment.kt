package com.rama.dengerinaja.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rama.dengerinaja.R
import com.rama.dengerinaja.adapters.IQueueRVAdapter
import com.rama.dengerinaja.adapters.QueueRVAdapter
import com.rama.dengerinaja.databinding.FragmentQueueBinding
import com.rama.dengerinaja.extensions.*
import com.rama.dengerinaja.models.QueueData
import com.rama.dengerinaja.models.Song
import com.rama.dengerinaja.widgets.QueueDragCallback

class QueueFragment : BaseFragment(), IQueueRVAdapter {

    private var _binding: FragmentQueueBinding? = null
    private val binding: FragmentQueueBinding
        get() = _binding!!
    private var queueAdapter: QueueRVAdapter? = null
    private var touchHelper: ItemTouchHelper? = null
    private var callback: QueueDragCallback? = null
    private var songs = listOf<Song>()
    private var queueData: QueueData? = null
    private var queueIds: LongArray? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentQueueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        queueAdapter = QueueRVAdapter(this, this, nowPlayingViewModel)

        binding.ivBackButtonQueue.setOnClickListener {
            findNavController().navigateUp()
        }

        setUpRecyclerView()

        nowPlayingViewModel.queueData.observe(viewLifecycleOwner) {
            queueData = it
            queueIds = it.queue
            mainViewModel.getSongForIds(it.queue)
        }

        mainViewModel.songsForIds.observe(viewLifecycleOwner) {
            if (mainViewModel.isBeingReordered) {
                mainViewModel.isBeingReordered = false
            } else {
                songs = it
                val orderedSongs = queueIds?.let { it1 -> songs.keepInOrder(it1) }
                if (orderedSongs != null) {
                    queueAdapter?.updateData(orderedSongs)
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        binding.apply {
            rvQueue.adapter = queueAdapter
            getTouchHelper().attachToRecyclerView(rvQueue)
            rvQueue.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onSongClicked(song: Song) {
        val extras = queueData?.queueTitle?.let { getExtraBundle(songs.toSongIds(), it) }
        mainViewModel.mediaItemClicked(song, extras)
    }

    override fun onPickUp(viewHolder: RecyclerView.ViewHolder) {
        getTouchHelper().startDrag(viewHolder)
    }

    private fun getTouchHelper(): ItemTouchHelper {
        check(!isDetached)
        val instance = touchHelper
        if (instance != null) {
            return instance
        }
        val newCallback = QueueDragCallback(mainViewModel, queueAdapter!!)
        val newInstance = ItemTouchHelper(newCallback)
        callback = newCallback
        touchHelper = newInstance
        return newInstance
    }

    override fun onDestroy() {
        super.onDestroy()
        callback = null
        touchHelper = null
        _binding = null
    }
}