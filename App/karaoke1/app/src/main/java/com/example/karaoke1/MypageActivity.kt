package com.example.karaoke1

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_mypage.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

@Suppress("DEPRECATION")
class MypageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)
        supportActionBar?.title = "마이페이지"

        /*Log.d("Mypage",MyApplication.userEmail)
        var count=JsonExecute().execute("count","http://175.118.28.138/payment/invest",MyApplication.userEmail).get()

        Log.d("Mypage", count.toString())
        txtCount.text="남은 곡 : ${count.toString()}"*/
        Glide.with(this).load(MyApplication.userImg).apply(RequestOptions.bitmapTransform(MultiTransformation(CenterCrop(), RoundedCorners(200)))).into(imguser)
        coroutine()
    }

    fun coroutine(){
        CoroutineScope(Dispatchers.Main).launch{

            val value=CoroutineScope(Dispatchers.Default).async{
                JsonExecute().getValue("count","http://175.118.28.138/payment/invest",MyApplication.userEmail)
            }.await()

            Log.d("Mypage",MyApplication.userEmail)
            //var count=JsonExecute().execute("count","http://175.118.28.138/payment/invest",MyApplication.userEmail).get()
            Log.d("Mypage", value.toString())
            txtCount.text="남은 곡 : ${value.toString()}"
        }
    }

    fun getValue():String?{

        val ob = JSONObject()
        var con: HttpURLConnection? = null
        var reader: BufferedReader? = null
        //var commend:String= arr[0].toString()
        lateinit var url:URL
        try {
            url= URL("http://175.118.28.138/payment/invest")
            ob.accumulate("email", MyApplication.userEmail)
            try {
                //val url = URL("http://175.118.28.138/payment/increase ")        // url 수정
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

                val stream = con.inputStream
                reader = BufferedReader(InputStreamReader(stream))

                val buffer = StringBuffer()
                var line: String? = ""
                while (reader.readLine().also { line = it } != null) {
                    buffer.append(line)
                }
                return buffer.toString()
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
}
