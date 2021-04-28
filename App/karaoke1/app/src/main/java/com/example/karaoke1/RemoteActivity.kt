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
import android.content.Intent as Intent

class RemoteActivity : AppCompatActivity() {

    //val spinItem=resources.getStringArray(R.array.optionspin)
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
                Log.d("searchType","${searchType}")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        btnsearch.setOnClickListener {
            val builder = AlertDialog.Builder(this@RemoteActivity)
            val dialogView = layoutInflater.inflate(R.layout.search_result_dialog, null)
            try {
                /*var jsonresult = JsonExecute().execute("search","http://175.118.28.138/music/search", searchType, editSearch.text.toString()).get()
                Log.d("JsonResult","${jsonresult}")
                var jsonArray:JSONArray=JSONArray(jsonresult)
                var resultSongs= arrayListOf<ItemSong>()
                for(i in 0 until jsonArray.length()){
                    var jsonObject:JSONObject=jsonArray.getJSONObject(i)
                    var resultsong=ItemSong(jsonObject.getString("id"),jsonObject.getString("name"),0)
                    resultSongs.add(resultsong)
                    Log.d("JsonResult","${resultsong.title} / ${resultsong.singer}")
                }*/
                var intent= Intent(this,PopupResultActivity::class.java)
                //intent.putExtra("ListSong",resultSongs)
                //intent.putExtra("resultSong",jsonresult)
                startActivityForResult(intent,1)
            }catch(e:Exception){
                e.printStackTrace()
            }
        }

        btnTempoUp.setOnClickListener{
            Toast.makeText(this, "Tempo Up / 1", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"1") }
        }
        btnTempoDown.setOnClickListener{
            Toast.makeText(this, "Tempo Down / 2", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"2") }
        }
        btnVolumeUp.setOnClickListener{
            Toast.makeText(this, "Volume Up / 3", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"3") }
        }
        btnVolumeDown.setOnClickListener{
            Toast.makeText(this, "Volume Down / 4", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"4") }
        }
        btnPitchUp.setOnClickListener{
            Toast.makeText(this, "Pitch Up / 5", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"5") }
        }
        btnPitchDown.setOnClickListener{
            Toast.makeText(this, "Pitch Down / 6", Toast.LENGTH_SHORT).show()
            mGatt?.let { it1 -> onClickWrite(it1,"6") }
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
                    JsonExecute().execute("payment","http://175.118.28.138/payment/increase",MyApplication.userEmail,count.toString())
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