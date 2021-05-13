package com.example.karaoke1

import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.karaoke1.BluetoothUtils.Companion.onClickWrite
import com.example.karaoke1.MyApplication.Companion.mGatt
import com.example.karaoke1.item.ItemSong
import kotlinx.android.synthetic.main.activity_remote.*
import kotlinx.android.synthetic.main.activity_remote.view.*
import kotlinx.android.synthetic.main.payment_list_dialog.view.*
import kr.co.bootpay.Bootpay
import kr.co.bootpay.BootpayAnalytics
import kr.co.bootpay.enums.Method
import kr.co.bootpay.enums.PG
import kr.co.bootpay.enums.UX
import kr.co.bootpay.model.BootExtra
import kr.co.bootpay.model.BootUser
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import kotlin.reflect.typeOf
import android.content.Intent as Intent

class RemoteActivity : AppCompatActivity() {

    val spinItem= arrayOf("제목","가수")
    val listItem=arrayOf("가시","celebrity")
    lateinit var searchType:String

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote)

        val spinAdapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,spinItem)
        spinner.adapter=spinAdapter
        spinner.setSelection(0)
        spinner.onItemSelectedListener=object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                searchType=spinItem[position]
                //mGatt?.let { it1 -> onClickWrite(it1,"10") }
                Log.d("searchType","${searchType}")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        btnsearch.setOnClickListener {
            val builder = AlertDialog.Builder(this@RemoteActivity)
            val dialogView = layoutInflater.inflate(R.layout.search_result_dialog, null)
            try {
                //var jsonresult = JsonExecute().execute("search","http://175.118.28.138/music/search", searchType, editSearch.text.toString()).get()
                var jsonresult = JsonExecute().execute("search","http://192.168.122.228/music/search", searchType, editSearch.text.toString()).get()
                Log.d("JsonResult","${jsonresult}")
                var jsonArray:JSONArray=JSONArray(jsonresult)
                var resultSongs= arrayListOf<ItemSong>()
                Log.d("JsonArray","${jsonArray}")

                for(i in 0 until jsonArray.length()){
                    var jsonList: JSONArray? =jsonArray.getJSONArray(i)
                    //jsonObject?.let { it1 -> jsonArrayList.add(it1) }
                    for(j in 0 until jsonList!!.length()){
                        var jsonObject:JSONObject=jsonList.getJSONObject(j)
                        Log.d("jsonObject","${jsonObject}")
                        var resultsong:ItemSong=ItemSong(jsonObject.getString("name"),jsonObject.getString("singer"),jsonObject.getInt("id"))
                        resultSongs.add(resultsong)
                        Log.d("resultsong","${resultsong}")
                    }
                }
                var intent= Intent(this,PopupResultActivity::class.java)
                intent.putExtra("resultSong",resultSongs)
                Log.d("resultSong","${resultSongs.toString()} ")
                //mGatt?.let { it1 -> onClickWrite(it1,"17,") }
                startActivityForResult(intent,1)
            }catch(e:Exception){
                e.printStackTrace()
            }
        }

        /* 상단 3개*/
        btnMenu.setOnClickListener{
            Toast.makeText(this, "11 / 메뉴화면", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"11") }
        }
        btnChkResv.setOnClickListener {
            Toast.makeText(this, "20 / 예약목록", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"20") }
        }

        btnCancelResv.setOnClickListener {
            Toast.makeText(this, "24 / 예약취소", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"24") }
        }

        /*템포, 볼륨, 피치*/
        btnTempoUp.setOnClickListener{
            Toast.makeText(this, "Tempo Up / 23,1", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"23,1") }
        }
        btnTempoDown.setOnClickListener{
            Toast.makeText(this, "Tempo Down / 23,0", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"23,0") }
        }
        btnVolumeUp.setOnClickListener{
            Toast.makeText(this, "Volume Up / 21,1", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"21,1") }
        }
        btnVolumeDown.setOnClickListener{
            Toast.makeText(this, "Volume Down / 21,0", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"21,0") }
        }
        btnPitchUp.setOnClickListener{
            Toast.makeText(this, "Pitch Up / 22,1", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"22,1") }
        }
        btnPitchDown.setOnClickListener{
            Toast.makeText(this, "Pitch Down / 22,0", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"22,0") }
        }

        /*중앙 3개*/
        btnCancel.setOnClickListener {
            Toast.makeText(this, "12 / 취소", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"12") }
        }
        btnResvPrior.setOnClickListener {
            Toast.makeText(this, "25 / 우선예약", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"25") }
        }
        btnReserve.setOnClickListener {
            Toast.makeText(this, "18 / 예약하기", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"18") }
        }

        /* 방향키 및 재생*/
        btnPlay.setOnClickListener {
            Toast.makeText(this, "19 / 시작", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"19") }
        }

        btnUp.setOnClickListener{
            Toast.makeText(this, "Up / 14", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"14") }
        }
        btnDown.setOnClickListener{
            Toast.makeText(this, "Down / 13", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"13") }
        }
        btnLeft.setOnClickListener{
            Toast.makeText(this, "Left / 16", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"16") }
        }
        btnRight.setOnClickListener{
            Toast.makeText(this, "Right / 15", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"15") }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_payment -> {
                var price: Int = 0
                var count: Int = 0
                val builder = AlertDialog.Builder(this)
                val dialogView = layoutInflater.inflate(R.layout.payment_list_dialog, null)
                builder!!.setView(dialogView)!!.show()
                dialogView.paymentRg.setOnCheckedChangeListener { radioGroup, checked ->
                    when (checked) {
                        R.id.rb2 -> {
                            price = 500
                            count = 2
                            dialogView.btnbootpay.text = "${price}원 결제"
                        }
                        R.id.rb4 -> {
                            price = 1000
                            count = 4
                            dialogView.btnbootpay.text = "${price}원 결제"
                        }
                        R.id.rb10 -> {
                            price = 2000
                            count = 10
                            dialogView.btnbootpay.text = "${price}원 결제"
                        }
                        R.id.rb20 -> {
                            price = 2500
                            count = 20
                            dialogView.btnbootpay.text = "${price}원 결제"
                        }
                    }
                }
                dialogView.btnbootpay.setOnClickListener {
                    if (price != 0) {
                        Log.d("Price", "${price}")
                        BootpayAnalytics.init(this, "6048ae135b29480027521bb3")
                        BootpayRequest(price, count)
                    } else
                        Toast.makeText(
                                MyApplication.getGlobalApplicationContext(),
                                "곡 수를 선택해주세요.",
                                Toast.LENGTH_SHORT
                        )
                }
                return true
            }
            R.id.action_mypage->{
                val intent = Intent(this, MypageActivity::class.java)
                startActivity(intent)
                return true
            }
            else->{
                return super.onOptionsItemSelected(item)
            }
        }
    }

    @Suppress("DEPRECATION")
    fun BootpayRequest(price:Int,count:Int){
        val bootUser= BootUser()
        val bootExtra = BootExtra().setQuotas(intArrayOf(0, 2, 3))
        Bootpay.init(this)
                .setApplicationId("6048ae135b29480027521bb3")
                .setContext(this)
                .setBootUser(bootUser)
                .setBootExtra(bootExtra)
                .setUX(UX.PG_DIALOG)
                .setMethod(Method.CARD)
                .setPG(PG.INICIS)
                .setName("곡 예약")
                .setOrderId("${MyApplication.userEmail}")
                .setPrice(price)
                .onConfirm{ message->
                    Bootpay.confirm(message)
                    Log.d("Bootpay", message)
                    //서버로 관련 데이터를 전송할 라이프사이클
                   // JsonSearch().execute("http://175.118.28.138/payment/increase",MyApplication.userEmail,count.toString())
                }
                .onDone{message->
                    Log.d("Bootpay", "onDone")
                    Toast.makeText(MyApplication.getGlobalApplicationContext(),"결제가 정상적으로 완료되었습니다.",Toast.LENGTH_LONG)
                    //JsonExecute().execute("payment","http://175.118.28.138/payment/increase",MyApplication.userEmail,count.toString())
                    JsonExecute().execute("payment","http://192.168.122.228/payment/increase",MyApplication.userEmail,count.toString())
                }
                .onReady { message-> Log.d("Bootpay", "onReady")}
                .onCancel{ message->
                    Log.d("Bootpay", "onCancel")
                    Toast.makeText(MyApplication.getGlobalApplicationContext(),"결제가 취소되었습니다.",Toast.LENGTH_LONG)
                }
                .onError{ message->
                    Log.d("Bootpay", "onError")
                    Toast.makeText(MyApplication.getGlobalApplicationContext(),"결제 Error",Toast.LENGTH_LONG)
                }
                .onClose{message->Log.d("close", "close")}
                .request()
    }

}