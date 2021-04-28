package com.example.karaoke1

import android.app.Activity
import android.os.AsyncTask
import android.os.AsyncTask.execute
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.karaoke1.BluetoothUtils.Companion.onClickWrite
import com.example.karaoke1.MyApplication.Companion.mGatt
import com.example.karaoke1.adapter.RvSongAdapter
import com.example.karaoke1.item.ItemSong
import kotlinx.android.synthetic.main.activity_popup_result.*
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class PopupResultActivity : AppCompatActivity() {
    lateinit var songList:ArrayList<ItemSong>
        //ItemSong("가시","버즈",0),
        //ItemSong("Celebrity","아이유",0))

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_popup_result)

        songList=intent.getSerializableExtra("ListSong") as ArrayList<ItemSong>

        val adapter=RvSongAdapter(this,songList){songinfo->
            //mGatt?.let { it1 -> onClickWrite(it1, "r/${songinfo.title},${songinfo.singer}") }
            //JsonCount().execute("http://175.118.28.138/payment/d",MyApplication.userEmail)
            //JsonExecute().execute("countdown","http://175.118.28.138/payment/d",MyApplication.userEmail)
            finish()
        }
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


