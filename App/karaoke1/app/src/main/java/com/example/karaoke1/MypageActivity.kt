package com.example.karaoke1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_mypage.*

@Suppress("DEPRECATION")
class MypageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        var count=JsonExecute().execute("count","http://192.168.122.228/payment/invest",MyApplication.userEmail)
        txtCount.text="남은 곡 : $count"
    }
}