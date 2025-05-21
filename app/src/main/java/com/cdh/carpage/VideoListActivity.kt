package com.cdh.carpage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class VideoListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VideoItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_list)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        // 获取视频文件列表（示例路径，需要根据实际情况修改）
        val videoDir = File("/storage/emulated/0/Movies")
        val videoFiles = if (videoDir.exists() && videoDir.isDirectory) {
            videoDir.listFiles()?.filter { it.isFile && it.name.endsWith(".mp4") } ?: emptyList()
        } else {
            emptyList()
        }

        adapter = VideoItemAdapter(videoFiles)
        recyclerView.adapter = adapter
    }

    companion object {
        // 创建用于启动该 Activity 的 Intent
        fun newIntent(context: android.content.Context): Intent {
            return Intent(context, VideoListActivity::class.java)
        }
    }
}