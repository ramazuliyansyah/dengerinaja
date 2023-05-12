package com.rama.dengerinaja.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rama.dengerinaja.models.QueueEntity
import com.rama.dengerinaja.models.QueuedSongsEntity

@Database(entities = [QueueEntity::class, QueuedSongsEntity::class], version = 1, exportSchema = false)
abstract class GeetDatabase: RoomDatabase() {

    abstract fun queueDao(): QueueDAO
}