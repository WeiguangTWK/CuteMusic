package com.sosauce.cutemusic.ui.shared_components

import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.domain.repository.MediaStoreHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.filter


class PostViewModel(
    private val mediaStoreHelper: MediaStoreHelper
) : ViewModel() {



    var musics = mediaStoreHelper.fetchLatestMusics().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        mediaStoreHelper.musics
    )

    var albums = mediaStoreHelper.fetchLatestAlbums().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    var artists by mutableStateOf(
        mediaStoreHelper.artists
    )

    var folders by mutableStateOf(
        mediaStoreHelper.folders
    )



    private companion object {
        const val CUTE_ERROR = "CuteError"
    }

    var albumSongs by mutableStateOf(listOf<MediaItem>())
    var artistSongs by mutableStateOf(listOf<MediaItem>())
    var artistAlbums by mutableStateOf(listOf<Album>())

    fun albumSongs(album: String) {
        try {
            viewModelScope.launch {
                musics.collectLatest {
                    albumSongs = it.filter { it.mediaMetadata.albumTitle.toString() == album }
                }
            }
        } catch (e: Exception) {
            Log.e(CUTE_ERROR, e.message, e)
        }
    }

    fun artistSongs(artistName: String) {
        try {
            viewModelScope.launch {
                musics.collectLatest {
                    artistSongs = it.filter { it.mediaMetadata.artist == artistName }
                }
            }
        } catch (e: Exception) {
            Log.e(CUTE_ERROR, e.message, e)
        }

    }

    fun artistAlbums(artistName: String) {
        try {
            artistAlbums = albums.value.filter { it.artist == artistName }
        } catch (e: Exception) {
            Log.e(CUTE_ERROR, e.message, e)
        }
    }

    fun deleteMusic(
        uris: List<Uri>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        viewModelScope.launch {
            mediaStoreHelper.deleteMusics(
                uris,
                intentSenderLauncher
            )
        }
    }


    fun editMusic(
        uris: List<Uri>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        viewModelScope.launch {
            mediaStoreHelper.editMusic(
                uris,
                intentSenderLauncher
            )
        }
    }


}

