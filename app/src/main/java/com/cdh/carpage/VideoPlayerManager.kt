package com.cdh.carpage

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class VideoPlayerManager(private val context: Context) {
    private val player: ExoPlayer = ExoPlayer.Builder(context).build()

    private var playlist = mutableListOf<MediaItem>()
    private var currentIndex = 0

    fun getPlayer() = player

    fun setPlaylist(mediaItems: List<MediaItem>) {
        playlist.clear()
        playlist.addAll(mediaItems)
        currentIndex = 0
        playCurrent()
    }

    private fun playCurrent() {
        if (playlist.isEmpty()) return
        player.setMediaItem(playlist[currentIndex])
        player.prepare()
        player.play()
    }

    fun playNext() {
        if (playlist.isEmpty()) return
        currentIndex = (currentIndex + 1) % playlist.size
        playCurrent()
    }

    fun playPrevious() {
        if (playlist.isEmpty()) return
        currentIndex = if (currentIndex - 1 < 0) playlist.size - 1 else currentIndex - 1
        playCurrent()
    }

    fun playPauseToggle() {
        if (player.isPlaying) player.pause() else player.play()
    }

    fun release() {
        player.release()
    }
}
