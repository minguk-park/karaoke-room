package com.example.karaoke1.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.karaoke1.R
import com.example.karaoke1.item.ItemSong
import kotlinx.android.synthetic.main.item_song_card.view.*
import java.nio.file.Files.size

class RvSongAdapter(val context: Context, val songList:ArrayList<ItemSong>,val itemClick:(ItemSong)->Unit) :
    RecyclerView.Adapter<RvSongAdapter.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_song_card,parent,false)
        return Holder(view,itemClick)
    }
    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: RvSongAdapter.Holder, position: Int) {
        holder.bind(songList[position],context)
    }

    inner class Holder(itemView: View?,itemClick:(ItemSong)->Unit):RecyclerView.ViewHolder(itemView!!){
        val song_title=itemView?.findViewById<TextView>(R.id.txttitle)
        val song_singer=itemView?.findViewById<TextView>(R.id.txtsinger)

        fun bind(songinfo:ItemSong,context: Context){
            song_title?.text=songinfo.title.toString()
            song_singer?.text=songinfo.singer.toString()

            itemView.setOnClickListener{itemClick(songinfo)}
        }
    }

}
