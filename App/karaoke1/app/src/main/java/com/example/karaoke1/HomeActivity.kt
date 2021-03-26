package com.example.karaoke1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
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

class HomeActivity : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{

        val view: View = inflater.inflate(R.layout.activity_home, container, false)

        BootpayAnalytics.init(MyApplication.getGlobalApplicationContext(),"6048ae135b29480027521bb3")

        view.btnRemote.setOnClickListener {
            activity?.let {
                val intent = Intent(context, RemoteActivity::class.java)
                startActivity(intent)
            }
        }
        view.btnPayment.setOnClickListener {
            activity?.let {
                var price: Int = 0
                val builder = context?.let { it1 -> AlertDialog.Builder(it1) }
                val dialogView = layoutInflater.inflate(R.layout.payment_list_dialog, null)
                builder!!.setView(dialogView)!!.show()
                dialogView.paymentRg.setOnCheckedChangeListener { radioGroup, checked ->
                    when (checked) {
                        R.id.rb2 -> {
                            price = 500
                            dialogView.btnbootpay.text = "${price}원 결제"
                        }
                        R.id.rb4 -> {
                            price = 1000
                            dialogView.btnbootpay.text = "${price}원 결제"
                        }
                        R.id.rb10 -> {
                            price = 2000
                            dialogView.btnbootpay.text = "${price}원 결제"
                        }
                        R.id.rb20 -> {
                            price = 2500
                            dialogView.btnbootpay.text = "${price}원 결제"
                        }
                    }
                }
                dialogView.btnbootpay.setOnClickListener {
                    if (price != 0)
                        BootpayRequest(price)
                    else
                        Toast.makeText(
                            MyApplication.getGlobalApplicationContext(),
                            "곡 수를 선택해주세요.",
                            Toast.LENGTH_SHORT
                        )
                }
            }
        }
        return view
    }

    private fun BootpayRequest(price:Int){
        val bootUser= BootUser()
        Bootpay.init(MyApplication.getGlobalApplicationContext())
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
            }
            .onReady { message-> }
            .onCancel{ message->
                Toast.makeText(MyApplication.getGlobalApplicationContext(),"결제가 취소되었습니다.",Toast.LENGTH_LONG)
            }
            .onError{ message->
                Toast.makeText(MyApplication.getGlobalApplicationContext(),"결제 Error",Toast.LENGTH_LONG)
            }
            .onClose{message->}
            .request()
    }
}