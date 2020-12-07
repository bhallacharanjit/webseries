package com.aprosoft.webseries

import android.graphics.PixelFormat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class VideoActivity : AppCompatActivity() {

    val path = "https://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_video)


//        val videoPlayer:AndExoPlayerView = findViewById(R.id.andExoPlayerView)
//        videoPlayer.setSource("https://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4")

//        val youtubeVidePlayer:YouTubePlayerView = findViewById(R.id.youtube_player_view)
//
//        lifecycle.addObserver(youtubeVidePlayer)
//
//        youtubeVidePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
//            override fun onReady(youTubePlayer: YouTubePlayer) {
//                val videoId = "S0Q4gqBUs7c"
//                youTubePlayer.loadVideo(videoId, 0f)
//            }
//        })


//        val videoView:VideoView = findViewById(R.id.video)
//        val mediaController:MediaController = MediaController(this)
//
////        val uri:Uri = Uri.parse(path)
////
//        val uri =
//            Uri.parse("rtsp://r4---sn-npoe7n7r.googlevideo.com/Cj0LENy73wIaNAljdjwUslC1zBMYESARFC2Os5pfMOCoAUIASARgraD3tfawos1figELR25FRkJtX0YzNkUM/799C0095A69635CC0EC932AD22F3D65487053371.91F036D73E9DC8AF3380ABE310FAD0938C9DA4E0/yt8/1/video.3gp")
//
//
//
//
//        mediaController.setAnchorView(videoView)
//        videoView.setVideoURI(uri)
////        videoView.setVideoPath("https://www.youtube.com/watch?v=UQ4Yg_HUBFY")
//        videoView.start()
//        videoView.requestFocus()
//        videoView.setMediaController(mediaController)
//

    }
}