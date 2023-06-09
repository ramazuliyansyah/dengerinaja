package com.rama.dengerinaja.utils

object Constants {

    const val APP_PACKAGE_NAME = "com.rama.dengerinaja"
    const val EMPTY_ARTWORK_URI = "android.resource://com.rama.dengerinaja/drawable/ic_album_disk"
    const val SONG = "song"
    const val ALBUM = "album"
    const val ARTIST = "artist"
    const val PLAYLIST = "playlist"
    const val MAIN_VIEW_PAGER_SIZE = 4
    const val YIELD_FREQUENCY = 165
    const val MINIMUM_INITIAL_DRAG_VELOCITY = 10
    const val MAXIMUM_INITIAL_DRAG_VELOCITY = 25
    const val MEDIA_ID = "MEDIA_ID"
    const val MEDIA_TYPE = "MEDIA_TYPE"
    const val MEDIA_CALLER = "MEDIA_CALLER"
    const val SONG_BOTTOM_SHEET_ARG = "song_bottom_sheet_arg"
    const val PLAYLIST_ID_BOTTOM_SHEET_ARG = "playlist_id_bottom_sheet_arg"
    const val ADD_TO_PLAYLIST_SONG_ID_ARG = "add_to_playlist_song_id_arg"
    const val ADD_TO_PLAYLIST_SONG_TITLE_ARG = "add_to_playlist_song_title_arg"

    //media session callback
    const val QUEUED_SONGS_LIST = "queued_songs_list"
    const val SEEK_TO_POSITION = "seek_to_position"
    const val QUEUE_TITLE = "queue_title"
    const val ACTION_PLAY_NEXT = "action_play_next"
    const val ACTION_SONG_DELETED = "action_song_deleted"
    const val ACTION_REPEAT_SONG = "action_repeat_song"
    const val ACTION_QUEUE_REORDER = "action_queue_reorder"
    const val ACTION_REPEAT_QUEUE = "action_repeat_queue"
    const val ACTION_SET_MEDIA_STATE = "action_set_media_state"
    const val ACTION_RESTORE_MEDIA_SESSION = "action_restore_media_session"
    const val QUEUE_FROM = "queue_from"
    const val QUEUE_TO = "queue_to"

    //shared preferences
    const val PREFERENCES_HELPER = "preferences_helper"
    const val SONG_SORT_ORDER = "song_sort_order"
    const val QUEUE_IDS = "queue_ids"
    const val IS_REPEAT_ON = "is_repeat_on"

    //permissions
    const val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 100
    const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 101

    //services
    const val SONG_LOADED = "song_loaded"
    const val SONG_STARTED = "song_started"
    const val SONG_PLAYED = "song_played"
    const val SONG_PAUSED = "song_paused"
    const val SONG_ENDED = "song_ended"
    const val MEDIA_ID_ROOT = -1

    //notification
    const val NOTIFICATION_SONG_ID = 1
    const val NOTIFICATION_SONG_CHANNEL_ID = "notification_song_channel_id"
    const val FROM_NOTIFICATION = "from_notification"
    const val CURRENT_PLAYING_SONG_POSITION = "current_playing_song_position"
    const val ACTION_NOTIFICATION_NEXT = "action_next"
    const val ACTION_NOTIFICATION_PREVIOUS= "action_previous"
    const val ACTION_NOTIFICATION_PLAY_PAUSE = "action_play_pause"

    //player
    const val NO_SONG_ID =  -1L
    const val MAX_SHUFFLE__BUFFER_SIZE = 16

    //playback modes
    const val ALL_SONGS_MODE = 10
    const val ALL_ALBUMS_MODE = 11
    const val ALL_ARTISTS_MODE = 12
    const val ALL_PLAYLISTS_MODE = 13
    const val SONG_MODE = "song_mode"
    const val ARTIST_MODE = 15
    const val ALBUM_MODE = 16
    const val PLAYLIST_MODE = 17
    const val REPEAT_MODE = "repeat_mode"
    const val SHUFFLE_MODE = "shuffle_mode"

    //more menu click from
    const val CLICK_FROM_ALL_SONG_DETAILS = "click_from_all_song_details"
    const val CLICK_FROM_ALBUM_DETAILS = "click_from_album_details"
    const val CLICK_FROM_ARTIST_DETAILS = "click_from_artist_details"
    const val CLICK_FROM_PLAYLIST_DETAILS = "click_from_playlist_details"
    const val CLICK_FROM_SEARCH = "click_from_search"
}