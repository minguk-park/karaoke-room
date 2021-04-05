package com.example.karaoke1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.karaoke1.adapter.RvSongAdapter
import com.example.karaoke1.item.ItemSong
import kotlinx.android.synthetic.main.activity_popup_result.*

class PopupResultActivity : AppCompatActivity() {
    var songList=arrayListOf<ItemSong>(
        ItemSong("가시","버즈",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0),
        ItemSong("Celebrity","아이유",0)
    )

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_popup_result)

        val adapter=RvSongAdapter(this,songList)
        resultRv.adapter=adapter

        val lm=LinearLayoutManager(this)
        resultRv.layoutManager=lm
        resultRv.setHasFixedSize(true)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event?.action==MotionEvent.ACTION_OUTSIDE){
            return false
        }
        return true
    }

}