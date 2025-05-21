package com.cdh.carpage

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cdh.carpage.databinding.ActivityMainBinding
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var playerManager: VideoPlayerManager
    private lateinit var playlistAdapter: PlaylistAdapter

    // 记录当前播放器是否缩小状态
    private var isZoomedOut = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d("MainActivity", "onCreate called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化播放器管理器
        playerManager = VideoPlayerManager(this)
        binding.playerView.player = playerManager.getPlayer()

//        // 设置播放列表
//        val mediaItems = listOf(
//            MediaItem.fromUri("https://www.w3schools.com/html/mov_bbb.mp4".toUri()),
//            MediaItem.fromUri("https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4".toUri())
//        )
//        playerManager.setPlaylist(mediaItems)

        setupListeners()
        updateLeftInfo()
        initPlaylist()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    @OptIn(UnstableApi::class) private fun initPlaylist() {
        // 播放地址列表
        val mediaUrls = listOf(
            "https://www.w3schools.com/html/mov_bbb.mp4",
            "https://www.w3schools.com/html/movie.mp4"
        )

        // 构建 MediaItem 列表
        val mediaItems = mediaUrls.map { url -> MediaItem.fromUri(url) }

        playerManager.setPlaylist(mediaItems)

        // 设置播放队列并准备
        val player = playerManager.getPlayer()
        player.setMediaItems(mediaItems)
        player.prepare()

        // 设置 PlayerView 的 Player
        binding.playerView.player = player
        binding.playerView.useController = true

//        binding.playerView.controllerShowTimeoutMs = 3000 // 控制器3秒后隐藏

        binding.playerView.setControllerVisibilityListener(object : PlayerView.ControllerVisibilityListener {
            override fun onVisibilityChanged(visibility: Int) {
                println("visibility == View.VISIBLE"+(visibility == View.VISIBLE))
                binding.playlistRecycler.visibility =
                    if (visibility == View.VISIBLE) View.VISIBLE else View.GONE
            }
        })

        // 创建播放列表适配器（点击时切换播放索引）
        playlistAdapter = PlaylistAdapter(mediaUrls) { clickedUrl ->
            val index = mediaUrls.indexOf(clickedUrl)
            if (index != -1) {
                player.seekToDefaultPosition(index)
                player.play()
            }
        }

        binding.playlistRecycler.layoutManager = LinearLayoutManager(this)
        binding.playlistRecycler.adapter = playlistAdapter


    }

    private fun setupListeners() {
        binding.btnNext.setOnClickListener { playerManager.playNext() }
        binding.btnPrev.setOnClickListener { playerManager.playPrevious() }
        binding.btnPlayPause.setOnClickListener { playerManager.playPauseToggle() }

        binding.btnZoom.setOnClickListener {
            if (isZoomedOut) {
                zoomInPlayer()
            } else {
                zoomOutPlayer()
            }
        }

        binding.btnPip.setOnClickListener {
            enterPipMode()
        }

        binding.btnList.setOnClickListener {
            val intent = VideoListActivity.newIntent(this@MainActivity)
            startActivity(intent)
        }
    }

    // 缩小播放器，显示左侧天气和限行
    private fun zoomOutPlayer() {
        isZoomedOut = true
        // 调整播放器大小为 1/3 屏幕宽高，示范调整约束布局参数
        val params = binding.playerView.layoutParams
//        params.width = (resources.displayMetrics.widthPixels * 0.6).toInt()
//        params.height = (resources.displayMetrics.heightPixels * 0.3).toInt()
        params.width = 600
        params.height = 400
        binding.playerView.layoutParams = params

        binding.playerView.requestLayout()
        binding.playerView.invalidate()

        // 显示左侧信息栏
        binding.leftInfoLayout.visibility = View.VISIBLE
        binding.btnZoom.text = "放大"
    }

    // 放大播放器，隐藏左侧信息
    private fun zoomInPlayer() {
        isZoomedOut = false
        val params = binding.playerView.layoutParams
        params.width = -1 // match_parent
        params.height = -1
        binding.playerView.layoutParams = params

        binding.leftInfoLayout.visibility = View.GONE
        binding.btnZoom.text = "缩小"
    }

    private fun enterPipMode() {
        val aspectRatio = Rational(binding.playerView.width, binding.playerView.height)
        val params = PictureInPictureParams.Builder()
            .setAspectRatio(aspectRatio)
            .build()
        enterPictureInPictureMode(params)
    }

    override fun onUserLeaveHint() {
        // 用户按Home时自动进入悬浮播放
        enterPipMode()
    }

    // 监听 PiP 模式变化，恢复 UI
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            // 隐藏全部控件
            binding.controlPanel.visibility = View.GONE
            binding.leftInfoLayout.visibility = View.GONE
        } else {
            // 恢复控件
            binding.controlPanel.visibility = View.VISIBLE
            if (isZoomedOut) binding.leftInfoLayout.visibility = View.VISIBLE
        }
    }

    // 根据当前日期设置天气和限行信息
    private fun updateLeftInfo() {
        // 假设天气信息，这里你可以替换成实际API获取
        binding.tvWeather.text = "天气：晴 25℃"

        val todayWeekDay = SimpleDateFormat("EEEE", Locale.CHINA).format(Date())
        binding.tvRestriction.text = if (todayWeekDay == "星期二") {
            "今天是$todayWeekDay，限行"
        } else {
            "今天是$todayWeekDay，无限行"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerManager.release()
    }
}
