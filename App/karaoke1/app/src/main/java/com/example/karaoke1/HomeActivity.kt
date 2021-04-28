package com.example.karaoke1

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.payment_list_dialog.*
import kotlinx.android.synthetic.main.payment_list_dialog.view.*
import kr.co.bootpay.Bootpay
import kr.co.bootpay.BootpayAnalytics
import kr.co.bootpay.enums.Method
import kr.co.bootpay.enums.PG
import kr.co.bootpay.enums.UX
import kr.co.bootpay.model.BootUser
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class HomeActivity : AppCompatActivity(){
    lateinit var MAC_ADDR: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

       BootpayAnalytics.init(MyApplication.getGlobalApplicationContext(), "6048ae135b29480027521bb3")

        btnconnect2.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setBeepEnabled(true)
            integrator.captureActivity = MyBarcodeReaderActivity::class.java
            integrator.initiateScan()
        }
        btnRemote.setOnClickListener {
            val intent = Intent(this, RemoteActivity::class.java)
            startActivity(intent)
        }
        /*btnPayment.setOnClickListener {
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
                    BootpayRequest(price, count)
                } else
                    Toast.makeText(
                            MyApplication.getGlobalApplicationContext(),
                            "곡 수를 선택해주세요.",
                            Toast.LENGTH_SHORT
                    )
            }
        }*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                Toast.makeText(MyApplication.getGlobalApplicationContext(), "scanned :  ${result.contents} format: ${result.formatName}", Toast.LENGTH_LONG).show()
                MAC_ADDR = result.contents.toString()
                Log.d("QR", "${MAC_ADDR}")
                val intent = Intent(MyApplication.getGlobalApplicationContext(), ConnectActivity::class.java)
                intent.putExtra("MAC", MAC_ADDR)
                startActivity(intent)
            } else {
                Toast.makeText(MyApplication.getGlobalApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

   /* @Suppress("DEPRECATION")
    fun BootpayRequest(price:Int,count:Int){
        val bootUser= BootUser()
        Bootpay.init(this)
                .setApplicationId("6048ae135b29480027521bb3")
                .setContext(MyApplication.getGlobalApplicationContext())
                .setBootUser(bootUser)
                .setUX(UX.PG_DIALOG)
                .setMethod(Method.CARD)
                .setPG(PG.INICIS)
                .setName("곡 예약")
                .setOrderId("${MyApplication.userEmail}")
                .setPrice(price)
                .onConfirm{ message->
                    //서버로 관련 데이터를 전송할 라이프사이클
                }
                .onDone{message->
                    Toast.makeText(MyApplication.getGlobalApplicationContext(),"결제가 정상적으로 완료되었습니다.",Toast.LENGTH_LONG)
                    //JsonPayment().execute(MyApplication.userEmail,price.toString(),count.toString())
                }
                .onReady { message-> }
                .onCancel{ message->
                    Toast.makeText(MyApplication.getGlobalApplicationContext(),"결제가 취소되었습니다.",Toast.LENGTH_LONG)
                }
                .onError{ message->
                    Toast.makeText(MyApplication.getGlobalApplicationContext(),"결제 Error",Toast.LENGTH_LONG)
                }
                .onClose{message->Log.d("close", "close")}
                .request()
    }

    @Suppress("DEPRECATION")
    public class JsonPayment: AsyncTask<String, String, String>() {
        override fun doInBackground(vararg strings: String?): String? {
            val arr = arrayOfNulls<String>(strings.size)
            for (i in strings.indices) {
                arr[i] = strings[i]
            }
            val ob = JSONObject()
            var con: HttpURLConnection? = null
            var reader: BufferedReader? = null
            try {
                ob.accumulate("id", arr[0])
                ob.accumulate("price", arr[1])
                ob.accumulate("addcount",arr[2])
                try {
                    val url = URL("http://175.118.28.138/payment/increase ")        // url 수정
                    //URL url = new URL(urls[0]);
                    con = url.openConnection() as HttpURLConnection
                    con.requestMethod = "POST" //post 방식
                    con.setRequestProperty("Cache-Control", "no-cache") //캐시 설정
                    con.setRequestProperty("Content-Type", "application/json") // json형태로 전송
                    con.setRequestProperty("Accept", "text/html") //서버에 response 데이터를 html로 받음
                    con.doOutput = true //OutStream으로 post데이터를 넘겨주겠다는 의미
                    con.doInput = true //InputStream으로 서버의 응답을 받겠다는 의미
                    con.connect()

                    val outStream = con.outputStream //스트림 생성
                    val writer = BufferedWriter(OutputStreamWriter(outStream))
                    writer.write(ob.toString())
                    writer.flush()
                    writer.close()

                    /*val stream = con.inputStream
                    reader = BufferedReader(InputStreamReader(stream))

                    val buffer = StringBuffer()
                    var line: String? = ""
                    while (reader.readLine().also { line = it } != null) {
                        buffer.append(line)
                    }
                    return buffer.toString()*/
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                //종료가 되면 disconnect메소드를 호출한다.
                con?.disconnect()
                try {
                    //버퍼를 닫아준다.
                    reader?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }
    }*/

}




