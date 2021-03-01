package com.example.bletest

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanFilter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION_CODES.M
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.bletest.Constants.Companion.PERMISSIONS
import com.example.bletest.Constants.Companion.REQUEST_ALL_PERMISSION
import com.example.bletest.Constants.Companion.REQUEST_ENABLE_BT

class MainActivity : AppCompatActivity() {
    var scanResults:ArrayList<BluetoothDevice>?=ArrayList()

    private var bleAdapter:BluetoothAdapter?=MyApplication.getRepository().bleAdapter

    private var bleGatt: BluetoothGatt?=null

    @RequiresApi(M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!hasPermissions(this,PERMISSIONS)){
            requestPermissions(PERMISSIONS, REQUEST_ALL_PERMISSION)
        }

        MyApplication.getRepository().setBLEAdapter()
    }

    override fun onResume() {
        super.onResume()
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            finish()
        }
    }

    /**
     * Request BLE enable
     */
    private fun requestEnableBLE(){
        val bleEnableIntent= Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(bleEnableIntent, REQUEST_ENABLE_BT)
    }
    private fun hasPermissions(context: Context?,permissions:Array<String>):Boolean{
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M&&context!=null&&permissions!=null){
            for(permission in permissions){
                if(ActivityCompat.checkSelfPermission(context,permission)!=PackageManager.PERMISSION_GRANTED){
                    return false
                }
            }
        }
        return true
    }

    @RequiresApi(M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            REQUEST_ALL_PERMISSION->{
                if(grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Permissions granted!", Toast.LENGTH_SHORT).show()
                }else{
                    requestPermissions(permissions, REQUEST_ALL_PERMISSION)
                    Toast.makeText(this,"Permissions must be granted!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun onClickScan(){
        if(bleAdapter==null||!bleAdapter?.isEnabled!!){
            Toast.makeText(this,"Bluetooth ON",Toast.LENGTH_LONG).show()
            return
        }
        val filters:MutableList<ScanFilter>?=ArrayList()
        val scanFilter:ScanFilter=ScanFilter.Builder().setDeviceAddress(MAC)
    }

}