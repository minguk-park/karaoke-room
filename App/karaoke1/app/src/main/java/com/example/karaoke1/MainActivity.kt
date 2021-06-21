package com.example.karaoke1


import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.kakao.auth.ApiErrorCode
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import com.kakao.util.helper.Utility
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {

    private var sessionCallback: SessionCallback?=null
    //private var sessionCallback: SessionCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sessionCallback=SessionCallback()
        Log.d("start activity", "YES")
        Session.getCurrentSession().addCallback(sessionCallback)
        //val intent=Intent(this,ConnectActivity::class.java)
        supportActionBar?.title = "Login"
    }

    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(sessionCallback)
    }

    private class SessionCallback : ISessionCallback{
        override fun onSessionOpened() {
            requestMe()
        }
        private fun requestMe(){
            UserManagement.getInstance()
                    .me(object : MeV2ResponseCallback() {
                        override fun onSessionClosed(errorResult: ErrorResult) {
                            Toast.makeText(MyApplication.applicationContext(), "세션이 닫혔습니다. 다시 시도해 주세요: " + errorResult!!.errorMessage, Toast.LENGTH_LONG).show()
                        }

                        override fun onFailure(errorResult: ErrorResult) {
                            if (errorResult!!.errorCode == ApiErrorCode.CLIENT_ERROR_CODE) {
                                Toast.makeText(MyApplication.applicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(MyApplication.applicationContext(), "로그인 도중 오류가 발생했습니다: " + errorResult.errorMessage, Toast.LENGTH_LONG).show()
                            }
                        }

                        @Suppress("DEPRECATION")
                        override fun onSuccess(result: MeV2Response) {
                            JsonExecute().execute("signin","http://175.118.28.138/auth/register",result.nickname,result.kakaoAccount.email)            //서버에 정보 저장
                            //JsonExecute().execute("signin","http://192.168.122.228/auth/register",result.nickname,result.kakaoAccount.email)
                            Toast.makeText(MyApplication.applicationContext(), "Login success", Toast.LENGTH_LONG).show()
                            MyApplication.userEmail=result.kakaoAccount.email
                            MyApplication.userImg=result.thumbnailImagePath
                            val intent=Intent(MyApplication.applicationContext(),HomeActivity::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(MyApplication.applicationContext(),intent,null)
                        }
                    })
        }
        override fun onSessionOpenFailed(exception: KakaoException?) {
            Toast.makeText(MyApplication.applicationContext(), "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요: " + exception.toString(), Toast.LENGTH_SHORT).show();
        }

    }
}