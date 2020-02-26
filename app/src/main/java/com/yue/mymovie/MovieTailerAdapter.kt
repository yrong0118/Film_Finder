package com.yue.mymovie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.recyclerview.widget.RecyclerView

class MovieTailerAdapter (var mData:List<Video>) :RecyclerView.Adapter<MovieTailerAdapter.Companion.VideoViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.video_item, parent, false)

        return VideoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
       holder.videoWeb.loadData(mData.get(position).filmVedioKey, "text/html" , "utf-8" )
    }


    companion object{
        class VideoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val videoWeb: WebView

            init {
                videoWeb = itemView.findViewById(R.id.video_tailer) as WebView
                videoWeb.settings.javaScriptEnabled = true
                videoWeb.webChromeClient = WebChromeClient()
            }
        }
    }
}

