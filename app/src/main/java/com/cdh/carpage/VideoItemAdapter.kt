package com.cdh.carpage

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class VideoItemAdapter(
    private val videoFiles: List<File>
) : RecyclerView.Adapter<VideoItemAdapter.VideoViewHolder>() {

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnailImageView: ImageView = itemView.findViewById(R.id.thumbnailImageView)
        val videoNameTextView: TextView = itemView.findViewById(R.id.videoNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoFile = videoFiles[position]
        
        // 使用Glide加载视频缩略图
        Glide.with(holder.itemView.context)
            .load(Uri.fromFile(videoFile))
            .into(holder.thumbnailImageView)
        
        holder.videoNameTextView.text = videoFile.name
    }

    override fun getItemCount(): Int = videoFiles.size
}