package com.rama.dengerinaja.repository

import androidx.lifecycle.LifecycleCoroutineScope
import com.rama.dengerinaja.db.QueueDAO
import com.rama.dengerinaja.extensions.equalsBy
import com.rama.dengerinaja.extensions.toQueuedSongsList
import com.rama.dengerinaja.models.QueueEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QueueRepository(private val queueDAO: QueueDAO, private val songsRepository: SongsRepository) {

    suspend fun updateQueue(queue: QueueEntity) = queueDAO.insertQueue(queue)

    private suspend fun setCurrentSongId(id: Long) = queueDAO.setCurrentId(id)

    fun updateQueuedSongs(
        queueIds: LongArray?,
        currentSongId: Long?,
        lifecycleScope: LifecycleCoroutineScope
    ) {
        if (queueIds == null || currentSongId == null)
            return

        val currentList = queueDAO.getQueuedSongs().value
        val songsList = queueIds.toQueuedSongsList(songsRepository)
        if (currentList != null) {
            val isListEqual = currentList.equalsBy(songsList) { left, right ->
                left.id == right.id
            }
            lifecycleScope.launch(Dispatchers.IO) {
                if (queueIds.isNotEmpty() && !isListEqual) {
                    queueDAO.clearQueueSongs()
                    queueDAO.insertAllSongs(songsList)
                    setCurrentSongId(currentSongId)
                } else {
                    setCurrentSongId(currentSongId)
                }
            }
        }
    }
}