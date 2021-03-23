package com.example.karaoke1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.payment_list_dialog.*
import kotlinx.android.synthetic.main.payment_list_dialog.view.*
import kr.co.bootpay.Bootpay
import kr.co.bootpay.BootpayAnalytics
import kr.co.bootpay.enums.Method
import kr.co.bootpay.enums.PG
import kr.co.bootpay.enums.UX
import kr.co.bootpay.model.BootUser

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        BootpayAnalytics.init(this,"6048ae135b29480027521bb3")

        btnRemote.setOnClickListener {
            val intent= Intent(this,RemoteActivity::class.java)
            startActivity(intent)
        }
        btnPayment.setOnClickListener {
            var price:Int=0
            val builder= AlertDialog.Builder(this)
            val dialogView=layoutInflater.inflate(R.layout.payment_list_dialog,null)
            builder.setView(dialogView).show()
            dialogView.paymentRg.setOnCheckedChangeListener { radioGroup, checked ->
                when(checked){
                    R.id.rb2->{
                        price=500
                        dialogView.btnbootpay.text="${price}원 결제"
                    }
                    R.id.rb4->{
                        price=1000
                        dialogView.btnbootpay.text="${price}원 결제"
                    }
                    R.id.rb10->{
                        price=2000
                        dialogView.btnbootpay.text="${price}원 결제"
                    }
                    R.id.rb20->{
                        price=2500
                        dialogView.btnbootpay.text="${price}원 결제"
                    }
                }
            }
            dialogView.btnbootpay.setOnClickListener {
                if(price!=0)
                    BootpayRequest(price)
                else
                    Toast.makeText(this,"곡 수를 선택해주세요.",Toast.LENGTH_SHORT)
            }
        }
    }

    private fun BootpayRequest(price:Int){
        val bootUser= BootUser()
        Bootpay.init(this)
            .setApplicationId("6048ae135b29480027521bb3")
            .setContext(this)
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
                Toast.makeText(this,"결제가 정상적으로 완료되었습니다.",Toast.LENGTH_LONG)
            }
            .onReady { message-> }
            .onCancel{ message->
                Toast.makeText(this,"결제가 취소되었습니다.",Toast.LENGTH_LONG)
            }
            .onError{ message->
                Toast.makeText(this,"결제 Error",Toast.LENGTH_LONG)
            }
            .onClose{message->}
            .request()
    }
}