package com.cdh.carpage

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.cdh.carpage.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // 支持viewbinding
    private lateinit var binding : ActivityMainBinding
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化播放器
        player = ExoPlayer.Builder(this).build()
        binding.playerView.player = player

        // 添加媒体资源
        val mediaItem = MediaItem.fromUri("https://www.w3schools.com/html/mov_bbb.mp4".toUri())
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}