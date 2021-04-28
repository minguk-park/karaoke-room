package com.example.bootpaytest

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import kr.co.bootpay.Bootpay
import kr.co.bootpay.BootpayAnalytics
import kr.co.bootpay.enums.Method
import kr.co.bootpay.enums.PG
import kr.co.bootpay.enums.UX
import kr.co.bootpay.model.BootExtra
import kr.co.bootpay.model.BootUser

class MainActivity : AppCompatActivity() {

    val application_id = "6048ae135b29480027521bb3"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BootpayAnalytics.init(this, application_id)

        findViewById<Button>(R.id.button_first).setOnClickListener {
            goBootpayRequest()
        }
    }


    fun goBootpayRequest() {
        //val bootUser = BootUser().setPhone("010-1234-5678")
        val bootUser=BootUser()
        val bootExtra = BootExtra().setQuotas(intArrayOf(0, 2, 3))
        Log.d("CHECK","boot Extra")

        val stuck = 1 //재고 있음

        Bootpay.init(this)
                .setApplicationId(application_id) // 해당 프로젝트(안드로이드)의 application id 값
                .setContext(this)
                .setBootUser(bootUser)
                .setBootExtra(bootExtra)
                .setUX(UX.PG_DIALOG)
                .setPG(PG.INICIS)
                .setMethod(Method.CARD)
                //.setIsShowAgree(true)         // 정보 수집에 관련된 동의창 여부
//                .setUserPhone("010-1234-5678") // 구매자 전화번호
                .setName("맥북프로's 임다") // 결제할 상품명
                .setOrderId("1234") // 결제 고유번호expire_month
                .setPrice(100) // 결제할 금액
                .addItem("마우's 스", 1, "ITEM_CODE_MOUSE", 100) // 주문정보에 담길 상품정보, 통계를 위해 사용
                .addItem("키보드", 1, "ITEM_CODE_KEYBOARD", 200, "패션", "여성상의", "블라우스") // 주문정보에 담길 상품정보, 통계를 위해 사용
                .onConfirm { message ->
                    if (0 < stuck) Bootpay.confirm(message); // 재고가 있을 경우.
                    else Bootpay.removePaymentWindow(); // 재고가 없어 중간에 결제창을 닫고 싶을 경우
                    Log.d("confirm", message);
                }
                .onDone { message ->
                    Log.d("done", message)
                }
                .onReady { message ->
                    Log.d("ready", message)
                }
                .onCancel { message ->
                    Log.d("cancel", message)
                }
                .onError{ message ->
                    Log.d("error", message)
                }
                .onClose { message ->
                    Log.d("close", "close")
                }
                .request()
    }
}