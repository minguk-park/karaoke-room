package com.example.bletest

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context.BLUETOOTH_SERVICE

class Repository {
    var bleAdapter : BluetoothAdapter? = null

    companion object{
        private var sInstance: Repository? = null
        fun getInstance(): Repository {
            return sInstance
                ?: synchronized(this){
                    val instance = Repository()
                    sInstance = instance
                    instance
                }
        }
    }
    fun setBLEAdapter(){
        // ble manager
        val bleManager: BluetoothManager? = MyApplication.applicationContext().getSystemService( BLUETOOTH_SERVICE ) as BluetoothManager
        // set ble adapter
        bleAdapter = bleManager?.adapter
    }

}