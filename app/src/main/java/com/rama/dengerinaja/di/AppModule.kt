package com.rama.dengerinaja.di

import android.app.Application
import android.app.NotificationManager
import android.content.ComponentName
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.rama.dengerinaja.db.GeetDatabase
import com.rama.dengerinaja.playback.PlaybackService
import com.rama.dengerinaja.playback.player.MusicPlayer
import com.rama.dengerinaja.playback.player.PlaybackSessionConnector
import com.rama.dengerinaja.playback.player.Queue
import com.rama.dengerinaja.playback.player.SongPlayer
import com.rama.dengerinaja.repository.*
import com.rama.dengerinaja.utils.NotificationGenerator
import kotlinx.coroutines.DelicateCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application): GeetDatabase =
        Room.databaseBuilder(application, GeetDatabase::class.java, "geet_database").build()

    @DelicateCoroutinesApi
    @Provides
    @Singleton
    fun providePlaybackService(): PlaybackService =
        PlaybackService()

    @Provides
    @Singleton
    fun provideSongRepository(application: Application): SongsRepository =
        SongsRepository(application.applicationContext)

    @Provides
    @Singleton
    fun provideQueueRepository(geetDatabase: GeetDatabase, songsRepository: SongsRepository) =
        QueueRepository(geetDatabase.queueDao(), songsRepository)

    @Provides
    @Singleton
    fun provideArtistRepository(application: Application) =
        ArtistRepository(application)

    @Provides
    @Singleton
    fun providePlaylistRepository(application: Application) =
        PlaylistRepository(application)

    @Provides
    @Singleton
    fun provideAlbumRepository(application: Application) =
        AlbumRepository(application)

    @Provides
    @Singleton
    fun provideMusicPlayer(application: Application) =
        MusicPlayer(application)

    @Provides
    @Singleton
    fun provideQueue(application: Application, songsRepository: SongsRepository, geetDatabase: GeetDatabase) =
        Queue(application, songsRepository, geetDatabase.queueDao())

        @Provides
    @Singleton
    fun provideSongPlayer(application: Application, musicPlayer: MusicPlayer, songsRepository: SongsRepository, geetDatabase: GeetDatabase, queue: Queue) =
        SongPlayer(application, musicPlayer, songsRepository, geetDatabase.queueDao(), queue)

    @Provides
    @Singleton
    fun provideNotificationGenerator(application: Application) =
        NotificationGenerator(application)

    @DelicateCoroutinesApi
    @Provides
    @Singleton
    fun provideComponentName(application: Application, playbackService: PlaybackService) =
        ComponentName(application, playbackService::class.java)

    @Provides
    @Singleton
    fun providePlaybackSessionConnector(application: Application, componentName: ComponentName) =
        PlaybackSessionConnector(application, componentName)
}