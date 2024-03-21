package com.example.sabritest

import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.DrmConfiguration
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlaybackException
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.drm.DefaultDrmSessionManager
import androidx.media3.exoplayer.drm.DrmSessionManager
import androidx.media3.exoplayer.drm.FrameworkMediaDrm
import androidx.media3.exoplayer.drm.LocalMediaDrmCallback
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import java.nio.charset.StandardCharsets


class MainActivity : AppCompatActivity() {

    lateinit var playerView: PlayerView;
    lateinit var exoPlayer: ExoPlayer;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        playerView  = findViewById(R.id.playerView);

        exoPlayer = ExoPlayer.Builder(this).build();
        playerView.player = exoPlayer;


//        widevinePlayer();
//        normalPlayer();
        clearkeyPlayer();

        exoPlayer.prepare();
        exoPlayer.play();

    }

    fun normalPlayer(){
        val mediaItem = MediaItem.fromUri("https://dash.akamaized.net/akamai/bbb_30fps/bbb_30fps.mpd")
        exoPlayer.setMediaItem(mediaItem);
    }

    fun widevinePlayer(){
        val licenseUri = "https://drm-widevine-licensing.axtest.net/AcquireLicense"
        val  mpdUrl = "https://media.axprod.net/TestVectors/v7-MultiDRM-SingleKey/Manifest_1080p.mpd"
        val httpRequestHeaders : Map<String, String> = mapOf("X-AxDRM-Message" to "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ2ZXJzaW9uIjoxLCJjb21fa2V5X2lkIjoiYjMzNjRlYjUtNTFmNi00YWUzLThjOTgtMzNjZWQ1ZTMxYzc4IiwibWVzc2FnZSI6eyJ0eXBlIjoiZW50aXRsZW1lbnRfbWVzc2FnZSIsImtleXMiOlt7ImlkIjoiOWViNDA1MGQtZTQ0Yi00ODAyLTkzMmUtMjdkNzUwODNlMjY2IiwiZW5jcnlwdGVkX2tleSI6ImxLM09qSExZVzI0Y3Iya3RSNzRmbnc9PSJ9XX19.4lWwW46k-oWcah8oN18LPj5OLS5ZU-_AQv7fe0JhNjA")

        val playerItem =  MediaItem.Builder()
            .setUri(mpdUrl)
            .setDrmConfiguration(
                MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                    .setLicenseUri(licenseUri)
                    .setMultiSession(true)
                    .setLicenseRequestHeaders(httpRequestHeaders)
                    .build()
            )
            .build()
        exoPlayer.setMediaItem(playerItem);
    }

    fun clearkeyPlayer(){
        val drmKey = "b537b1141cef8d580f7e8a4efba57b65"
        val drmKeyId = "000babeda3f2592ce71f7a976288ef9b"
        val mpdUrl = "https://linear210-de-dash1-prd-ak.cdn12.skycdp.com/345431/index_stereo.mpd"

        val drmKeyBytes = drmKey.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val encodedDrmKey = Base64.encodeToString(drmKeyBytes, Base64.DEFAULT)

        val drmKeyIdBytes = drmKeyId.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val encodedDrmKeyId = Base64.encodeToString(drmKeyIdBytes, Base64.DEFAULT)

        val drmBody = "{\"keys\":[{\"kty\":\"oct\",\"k\":\"${drmKey}\",\"kid\":\"${drmKeyId}\"}],\"type\":\"temporary\"}"
        val playerItem = MediaItem.Builder()
            .setUri(mpdUrl)
            .setDrmConfiguration(
                MediaItem.DrmConfiguration.Builder(C.CLEARKEY_UUID)
                    .setMultiSession(true)
                    .setKeySetId(drmBody.toByteArray())
//                    .setKeySetId(byteArrayOf(*drmKeyBytes, *drmKeyIdBytes))
                    .build()
            )
            .build()
        exoPlayer.setMediaItem(playerItem);
    }
}