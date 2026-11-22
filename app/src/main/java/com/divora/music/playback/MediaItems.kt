package com.divora.music.playback

import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.divora.music.data.innertube.InnerTube
import com.divora.music.data.model.Song
import com.divora.music.data.model.hiRes

const val EXTRA_SONG = "divora.song"
const val EXTRA_VIDEO = "divora.video"

/**
 * divora://audio/<id>[?video=<videoId>] – resolved to a real stream URL at load time.
 * Live streams are divora://live/<id>, resolved to their HLS manifest, which carries the picture too: their video mode
 * keeps the same URI (so switching doesn't reload the stream) and only turns the video track on.
 */
fun Song.toMediaItem(videoId: String? = null): MediaItem {
    val uri = if (isLive) Uri.Builder().scheme("divora").authority("live").appendPath(id).build()
    else Uri.Builder().scheme("divora").authority("audio").appendPath(id).apply {
        if (videoId != null) appendQueryParameter("video", videoId)
        // The listener's own upload (#101): asked for as their account from the start.
        if (isUpload) appendQueryParameter("upload", "1")
    }.build()
    val extras = Bundle().apply {
        putString(EXTRA_SONG, InnerTube.json.encodeToString(Song.serializer(), this@toMediaItem))
        if (videoId != null) putString(EXTRA_VIDEO, videoId)
    }
    // #328: shown as the version picked to play in its place (the item itself stays the song, for history and likes).
    val shown = com.divora.music.data.SongVersions.shownOf(this)
    return MediaItem.Builder()
        .setMediaId(id)
        .setUri(uri)
        .setCustomCacheKey(id)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(shown.title)
                .setArtist(shown.artistText)
                .setAlbumTitle(shown.album?.title)
                // The cover picked for the song or its album (#305), or a file's own (#350), through the artwork provider:
                // a car or a watch can't open Divora's files, nor Android's album art without its own permission.
                .setArtworkUri(com.divora.music.data.PlaylistCovers.artOf(this@toMediaItem).let { art ->
                    if (art != null && (art.startsWith("file:") || art.startsWith(ArtworkProvider.LOCAL_ALBUM_ART))) ArtworkProvider.contentUri(art)
                    else hiRes(art, 544)?.let(Uri::parse)
                })
                .setIsPlayable(true)
                .setIsBrowsable(false)
                .setMediaType(if (isEpisode) MediaMetadata.MEDIA_TYPE_PODCAST_EPISODE else MediaMetadata.MEDIA_TYPE_MUSIC)
                .setExtras(extras)
                .build()
        )
        .build()
}

fun MediaItem.toSong(): Song? = mediaMetadata.extras?.getString(EXTRA_SONG)?.let {
    runCatching { InnerTube.json.decodeFromString(Song.serializer(), it) }.getOrNull()
}

val MediaItem.videoModeId: String? get() = mediaMetadata.extras?.getString(EXTRA_VIDEO)
