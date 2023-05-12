package com.rama.dengerinaja.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rama.dengerinaja.models.QueueEntity
import com.rama.dengerinaja.models.QueuedSongsEntity

@Dao
interface QueueDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueue(queue: QueueEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSongs(songs: List<QueuedSongsEntity>)

    @Query("DELETE from queued_songs")
    suspend fun clearQueueSongs()

    @Delete
    suspend fun deleteSongFromQueue(song: QueuedSongsEntity)

    @Query("SELECT * FROM queue_table where id = 0")
    fun getQueue(): LiveData<QueueEntity?>

    @Query("SELECT * FROM queued_songs")
    fun getQueuedSongs(): LiveData<List<QueuedSongsEntity>>

    @Query("UPDATE queue_table SET currentId  = :currentId where id = 0")
    suspend fun setCurrentId(currentId: Long)
}