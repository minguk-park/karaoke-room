@file:Suppress("DEPRECATION")

package com.example.karaoke1

import android.os.AsyncTask
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

public class JsonExecute: AsyncTask<String, String, String>() {
    override fun doInBackground(vararg strings: String?): String? {
        val arr = arrayOfNulls<String>(strings.size)
        for (i in strings.indices) {
            arr[i] = strings[i]
        }
        val ob = JSONObject()
        var con: HttpURLConnection? = null
        var reader: BufferedReader? = null
        var commend:String= arr[0].toString()
        lateinit var url:URL
        try {
            when(commend){
                "signin"->{
                    url= URL(arr[1].toString())
                    ob.accumulate("name", arr[2])
                    ob.accumulate("email", arr[3])
                }
                "search"->{
                    url= URL(arr[1].toString())
                    ob.accumulate("type", arr[2])
                    ob.accumulate("text", arr[3])
                }
                "countdown"->{
                    url= URL(arr[1].toString())
                    ob.accumulate("email", arr[2])
                }
                "payment"->{
                    url= URL(arr[1].toString())
                    ob.accumulate("email", arr[2])
                    ob.accumulate("point", arr[3])
                }
            }
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

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
    }
}