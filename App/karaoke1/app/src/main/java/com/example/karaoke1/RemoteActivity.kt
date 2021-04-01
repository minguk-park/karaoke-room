package com.example.karaoke1

import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_remote.*
import kotlinx.android.synthetic.main.activity_remote.view.*
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
            //val builder = AlertDialog.Builder(this@RemoteActivity)
            //val dialogView = layoutInflater.inflate(R.layout.search_result_dialog, null)
            try {
                //var jsonresult = JsonSearch().execute(searchType, editSearch.text.toString()).get()
                //Log.d("JsonResult","${jsonresult}")
                /*val alertDialog:AlertDialog=builder.setView(dialogView)
                        .setItems(listItem,object: DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                               Toast.makeText(applicationContext,"${listItem[p1]}",Toast.LENGTH_LONG)
                                Log.d("dialog","${listItem[p1]}")
                            }
                        }).create()
                alertDialog.show()*/
                var intent= Intent(this,PopupResultActivity::class.java)
                startActivityForResult(intent,1)
            }catch(e:Exception){
                e.printStackTrace()
            }
        }

        btnTempoUp.setOnClickListener{
            Toast.makeText(this, "Tempo Up / 1", Toast.LENGTH_SHORT).show()
            //onClickWrite(mGatt,"1")
        }
        btnTempoDown.setOnClickListener{
            Toast.makeText(this, "Tempo Down / 2", Toast.LENGTH_SHORT).show()
            //onClickWrite(mGatt,"2")
        }
        btnVolumeUp.setOnClickListener{
            Toast.makeText(this, "Volume Up / 3", Toast.LENGTH_SHORT).show()
            //onClickWrite(mGatt,"3")
        }
        btnVolumeDown.setOnClickListener{
            Toast.makeText(this, "Volume Down / 4", Toast.LENGTH_SHORT).show()
            //builder.setView(dialogView)
            //onClickWrite(mGatt,"4")
        }
        btnPitchUp.setOnClickListener{
            Toast.makeText(this, "Pitch Up / 5", Toast.LENGTH_SHORT).show()
            //onClickWrite(mGatt,"5")
        }
        btnPitchDown.setOnClickListener{
            Toast.makeText(this, "Pitch Down / 6", Toast.LENGTH_SHORT).show()
            //onClickWrite(mGatt,"6")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)
        val mSearch : MenuItem= menu!!.findItem(R.id.search);

        //Log.d("TAG","${mSearch}")
        val sv:SearchView= mSearch.actionView as SearchView
        sv.isSubmitButtonEnabled

        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val builder = AlertDialog.Builder(this@RemoteActivity)
                val dialogView = layoutInflater.inflate(R.layout.search_result_dialog, null)
                builder.setView(dialogView).show()
                /*var result=ArrayList<String>()
                onClickWrite(mGatt,"search/${query}.toString()")
                */
                Toast.makeText(MyApplication.applicationContext(), "yes", Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })

        return true
    }

    @Suppress("DEPRECATION")
    public class JsonSearch: AsyncTask<String, String, String>() {
        override fun doInBackground(vararg strings: String?): String? {
            val arr = arrayOfNulls<String>(strings.size)
            for (i in strings.indices) {
                arr[i] = strings[i]
            }
            val ob = JSONObject()
            var con: HttpURLConnection? = null
            var reader: BufferedReader? = null
            try {
                ob.accumulate("type", arr[0])
                ob.accumulate("text", arr[1])
                try {
                    val url = URL("http://175.118.28.138/kakao")        // url 수정
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
}