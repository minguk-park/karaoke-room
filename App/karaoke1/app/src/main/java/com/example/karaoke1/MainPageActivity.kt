package com.example.karaoke1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main_page.*

class MainPageActivity : AppCompatActivity() {

    enum class FragmentType {
        mark, home, mypage
    }

    val manager=supportFragmentManager
    var selected=2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        initView()
    }

    private fun initView(){
        //bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        changeFragmentTo(FragmentType.home)
        selected = 2
    }

    /*private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.hometab -> {
                if(selected==1){
                    return@OnNavigationItemSelectedListener true
                }
                title = "홈"
                selected=1
                changeFragmentTo(FragmentType.home)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }*/

    private fun changeFragmentTo(type: FragmentType) {
        val transaction = manager.beginTransaction()
        when(type) {
            FragmentType.home -> {
                title = "홈"
                val homeFragment = HomeActivity()
                transaction.replace(R.id.main_layout, homeFragment)
            }

        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        //transaction.addToBackStack(null)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }
}