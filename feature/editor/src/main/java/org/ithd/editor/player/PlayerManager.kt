package org.ithd.editor.player

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import androidx.media3.cast.CastPlayer
import androidx.media3.cast.SessionAvailabilityListener
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MediaSourceEventListener
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerControlView
import com.google.android.gms.cast.framework.CastContext
import org.ithd.editor.ProgressTracker
import kotlin.math.roundToLong


@UnstableApi
class PlayerManager(
    val context: Context,
    val listener: Listener,
    val playerView: CustomPlayerView,
    val castContext: CastContext
) : Player.Listener, SessionAvailabilityListener {

    private var mediaQueue: ArrayList<MediaItem> = ArrayList()
    private val localPlayer: Player
    private val castPlayer: CastPlayer
    private val mediaSourceFactory: MediaSource.Factory = MediaSource.Factory.UNSUPPORTED
    private var currentItemIndex = C.INDEX_UNSET
    private var currentPlayer: Player? = null
    private var lastSeenTracks: Tracks? = Tracks.EMPTY
    private var progressTracker: ProgressTracker? = null
    var progressChangeListener: ((position: Long) -> Unit)? = null

    init {
        localPlayer = ExoPlayer.Builder(context).build()
        localPlayer.addListener(this)
        castPlayer = CastPlayer(castContext)
        castPlayer.addListener(this)
        castPlayer.setSessionAvailabilityListener(this)
        setCurrentPlayer(if (castPlayer.isCastSessionAvailable) castPlayer else localPlayer)
    }

//    override fun onIsPlayingChanged(isPlaying: Boolean) {
//        if (isPlaying) {
//            initializeProgressTracker()
//        } else {
//            progressTracker?.cancelHandler()
//        }
//    }

    private fun initializeProgressTracker() {
        progressTracker = ProgressTracker(currentPlayer) { position ->
            progressChangeListener?.invoke(position)
        }
    }

    fun play() {
        Log.i("wpfowpekf","in play function = ${currentPlayer?.currentPosition}")
        initializeProgressTracker()
        currentPlayer?.play()
    }

    fun pause() {
        Log.i("wpfowpekf","in pause function = ${currentPlayer?.currentPosition}")
        currentPlayer?.pause()
        progressTracker?.cancelHandler()
    }

    // Internal methods.
    private fun setCurrentPlayer(currentPlayer: Player) {
        if (this.currentPlayer === currentPlayer) {
            return
        }
        playerView.player = currentPlayer
        playerView.controllerHideOnTouch = currentPlayer === localPlayer
        playerView.controllerShowTimeoutMs = PlayerControlView.DEFAULT_SHOW_TIMEOUT_MS
        playerView.defaultArtwork = null

        // Player state management.
        var playbackPositionMs = C.TIME_UNSET
        var currentItemIndex = C.INDEX_UNSET
        var playWhenReady = false
        val previousPlayer: Player? = this.currentPlayer
        if (previousPlayer != null) {
            // Save state from the previous player.
            val playbackState = previousPlayer.playbackState
            if (playbackState != Player.STATE_ENDED) {
                playbackPositionMs = previousPlayer.currentPosition
                playWhenReady = previousPlayer.playWhenReady
                currentItemIndex = previousPlayer.currentMediaItemIndex
                if (currentItemIndex != this.currentItemIndex) {
                    playbackPositionMs = C.TIME_UNSET
                    currentItemIndex = this.currentItemIndex
                }
            }
            previousPlayer.stop()
            previousPlayer.clearMediaItems()
        }
        this.currentPlayer = currentPlayer

        // Media queue management.
        currentPlayer.setMediaItems(mediaQueue, currentItemIndex, playbackPositionMs)
        currentPlayer.playWhenReady = playWhenReady
        Log.i("dcdwcdcw----->>>", "${currentPlayer.playbackParameters.speed}")
//        currentPlayer.playbackParameters = PlaybackParameters(currentPlayer.playbackParameters.speed * 2)
        Log.i("dcdwcdcw----->>>", "${currentPlayer.playbackParameters.speed}")

        (currentPlayer as ExoPlayer).setSeekParameters(SeekParameters.CLOSEST_SYNC)
        currentPlayer.prepare()
    }

    override fun onCastSessionAvailable() {
        setCurrentPlayer(castPlayer)
    }

    override fun onCastSessionUnavailable() {
        setCurrentPlayer(localPlayer)
    }

    override fun onPlayerError(error: PlaybackException) {
        listener.onPlayerError(error.toBundle())
    }

    /**
     * Plays a specified queue item in the current player.
     *
     * @param itemIndex The index of the item to play.
     */
    fun selectQueueItem(itemIndex: Int) {
        setCurrentItem(itemIndex)
    }

    // Queue manipulation methods.

    /**
     * Returns the index of the currently played item.
     */
    fun getCurrentItemIndex(): Int = this.currentItemIndex

    /**
     * Appends {@code item} to the media queue.
     *
     * @param item The {@link MediaItem} to append.
     */
    fun addItem(item: MediaItem) {
        mediaQueue.add(item)
//        mediaSourceFactory.createMediaSource(item)
        currentPlayer?.addMediaItem(item)

        Log.i("wpeofkwpekf", "duration from manager = ${currentPlayer?.contentDuration}")

        // Create a data source factory.
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
// Create a progressive media source pointing to a stream uri.
        val mediaSource: MediaSource =
            ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(item)
        mediaSource.addEventListener(Handler(Looper.getMainLooper()),object :MediaSourceEventListener {

        })

    }

    /**
     * Returns the size of the media queue.
     */
    fun getMediaQueueSize(): Int = this.mediaQueue.size

    /**
     * Returns the item at the given index in the media queue.
     *
     * @param position The index of the item.
     * @return The item at the given index in the media queue.
     */
    fun getItem(position: Int): MediaItem = this.mediaQueue[position]

    /**
     * Removes the item at the given index from the media queue.
     *
     * @param item The item to remove.
     * @return Whether the removal was successful.
     */
    fun removeItem(item: MediaItem): Boolean {
        val itemIndex = mediaQueue.indexOf(item)
        if (itemIndex == -1) return false
        currentPlayer?.removeMediaItem(itemIndex)
        mediaQueue.removeAt(itemIndex)
        if (itemIndex == currentItemIndex && itemIndex == mediaQueue.size) {
            maybeSetCurrentItemAndNotify(C.INDEX_UNSET)
        } else if (itemIndex < currentItemIndex) {
            maybeSetCurrentItemAndNotify(currentItemIndex - 1)
        }
        return true
    }

    /**
     * Moves an item within the queue.
     *
     * @param item     The item to move.
     * @param newIndex The target index of the item in the queue.
     * @return Whether the item move was successful.
     */
    fun moveItem(item: MediaItem, newIndex: Int): Boolean {
        val fromIndex = mediaQueue.indexOf(item)
        if (fromIndex == -1) {
            return false
        }
        // Player update.
        currentPlayer?.moveMediaItem(fromIndex, newIndex)
        mediaQueue.add(newIndex, mediaQueue.removeAt(fromIndex))

        // Index update.
        if (fromIndex == currentItemIndex) {
            maybeSetCurrentItemAndNotify(newIndex)
        } else if (fromIndex < currentItemIndex && newIndex >= currentItemIndex) {
            maybeSetCurrentItemAndNotify(currentItemIndex - 1)
        } else if (fromIndex > currentItemIndex && newIndex <= currentItemIndex) {
            maybeSetCurrentItemAndNotify(currentItemIndex + 1)
        }
        return true
    }


    /**
     * Dispatches a given {@link KeyEvent} to the corresponding view of the current player.
     *
     * @param event The {@link KeyEvent}.
     * @return Whether the event was handled by the target view.
     */
    fun dispatchKeyEvent(event: KeyEvent): Boolean = playerView.dispatchKeyEvent(event)

    /**
     * Releases the manager and the players that it holds.
     */
    fun release() {
        currentItemIndex = C.INDEX_UNSET
        mediaQueue.clear()
        castPlayer.setSessionAvailabilityListener(null)
        castPlayer.release()
        playerView.player = null
        localPlayer.release()
        progressTracker?.cancelHandler()
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        val isLast = (currentItemIndex + 1) == currentPlayer?.mediaItemCount
        val isComplete =
            currentPlayer?.isPlaying == false && playbackState == Player.STATE_ENDED && isLast

        Log.i("wpoefwe", "isComplete = $isComplete")
        updateCurrentItemIndex()
    }

    // Player.Listener implementation.
    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        updateCurrentItemIndex()
    }

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
        updateCurrentItemIndex()
    }

    override fun onTracksChanged(tracks: Tracks) {
        if (currentPlayer != localPlayer || tracks == lastSeenTracks) {
            return
        }
        if (tracks.containsType(C.TRACK_TYPE_VIDEO)
            && !tracks.isTypeSupported(C.TRACK_TYPE_VIDEO,  /* allowExceedsCapabilities = */true)
        ) {
            listener.onUnsupportedTrack(C.TRACK_TYPE_VIDEO)
        }
        if (tracks.containsType(C.TRACK_TYPE_AUDIO)
            && !tracks.isTypeSupported(C.TRACK_TYPE_AUDIO,  /* allowExceedsCapabilities = */true)
        ) {
            listener.onUnsupportedTrack(C.TRACK_TYPE_AUDIO)
        }
        lastSeenTracks = tracks
    }

    private fun updateCurrentItemIndex() {
        val playbackState: Int? = currentPlayer?.playbackState
        maybeSetCurrentItemAndNotify(
            currentItemIndex =
            if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED)
                currentPlayer!!.currentMediaItemIndex
            else
                C.INDEX_UNSET
        )
    }

    /**
     * Starts playback of the item at the given index.
     *
     * @param itemIndex The index of the item to play.
     */
    private fun setCurrentItem(itemIndex: Int) {
        maybeSetCurrentItemAndNotify(itemIndex)
        if (currentPlayer?.currentTimeline?.windowCount == mediaQueue.size) {
            // This only happens with the cast player. The receiver app in the cast device clears the
            // timeline when the last item of the timeline has been played to end.
            currentPlayer?.setMediaItems(mediaQueue, itemIndex, C.TIME_UNSET)
        } else {
            currentPlayer?.seekTo(itemIndex, C.TIME_UNSET)
        }
        currentPlayer?.playWhenReady = true
    }

    private fun maybeSetCurrentItemAndNotify(currentItemIndex: Int) {
        if (this.currentItemIndex != currentItemIndex) {
            val oldIndex = this.currentItemIndex
            this.currentItemIndex = currentItemIndex
            listener.onQueuePositionChanged(oldIndex, currentItemIndex)
        }
    }

    override fun onVideoSizeChanged(videoSize: VideoSize) {
        Log.i("ewfkpwefk", "pixelWidthHeightRatio = ${videoSize.pixelWidthHeightRatio}")
        Log.i("ewfkpwefk", "unappliedRotationDegrees = ${videoSize.unappliedRotationDegrees}")
        Log.i("ewfkpwefk", "height = ${videoSize.height}")
        Log.i("ewfkpwefk", "width = ${videoSize.width}")
    }

    fun setCurrentPosition(updatedIndex: Int, currentPosition: Float) {
        Log.i("wpfowpekf","seekTo = ${currentPosition.roundToLong()}")
        currentPlayer?.seekTo(updatedIndex, currentPosition.roundToLong())
        Log.i("wpfowpekf","after seekTo = ${currentPlayer?.currentPosition}")
        Log.i("wpfowpekf","currentTimeline = ${currentPlayer?.currentTimeline}")
    }

    interface Listener {
        /**
         * Called when the currently played item of the media queue changes.
         */
        fun onQueuePositionChanged(previousIndex: Int, newIndex: Int)

        /**
         * Called when a track of type `trackType` is not supported by the player.
         *
         * @param trackType One of the [C]`.TRACK_TYPE_*` constants.
         */
        fun onUnsupportedTrack(trackType: Int)

        fun onPlayerError(errorBundle: Bundle?)
    }
}