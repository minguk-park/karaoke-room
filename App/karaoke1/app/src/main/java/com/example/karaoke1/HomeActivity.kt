package com.example.karaoke1

import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.dd.processbutton.iml.ActionProcessButton
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_home.*
import java.util.ArrayList
import com.example.karaoke1.MyApplication.Companion.mGatt

@Suppress("DEPRECATION")
class HomeActivity : AppCompatActivity(){
    private val TAG: String = "Central"

    lateinit var MAC_ADDR: String

    private var ble_adapter: BluetoothAdapter? = null
    private var is_scanning: Boolean = false
    private var is_connected: Boolean = false
    //private var connected: Boolean = false
    private var scan_results: HashMap<String, BluetoothDevice>? =HashMap()
    private var scanDevice: BluetoothDevice?=null
    private var ble_scanner: BluetoothLeScanner? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportActionBar?.title = "MainPage"

        if (!hasPermissions(this, Constants.PERMISSIONS)) {
            requestPermissions(Constants.PERMISSIONS, Constants.REQUEST_ALL_PERMISSION)
        }

        var ble_manager: BluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        //set ble adapter
        ble_adapter = ble_manager.adapter


        btnconnect.setOnClickListener {
            if(!is_connected) {
                val integrator = IntentIntegrator(this)
                integrator.setBeepEnabled(true)
                integrator.captureActivity = MyBarcodeReaderActivity::class.java
                integrator.initiateScan()
            }else{

            }
        }
        btnRemote.setOnClickListener {
            val intent = Intent(this, RemoteActivity::class.java)
            startActivity(intent)
        }

        btnmypage.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                Toast.makeText(MyApplication.getGlobalApplicationContext(), "scanned :  ${result.contents} format: ${result.formatName}", Toast.LENGTH_LONG).show()
                MAC_ADDR = result.contents.toString()
                Log.d("QR", "${MAC_ADDR}")
                //val intent = Intent(MyApplication.getGlobalApplicationContext(), ConnectActivity::class.java)
                //intent.putExtra("MAC", MAC_ADDR)
                btnconnect.progress=1
                btnconnect.setMode(ActionProcessButton.Mode.ENDLESS)
                startScan()
                //startActivity(intent)
            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    val scan_cb = object : ScanCallback() {
        private var cb_scan_result: MutableMap<String, BluetoothDevice>? = null

        init {
            cb_scan_result = scan_results as HashMap<String, BluetoothDevice>
            Log.d(TAG, "scan_cb")
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e(TAG, "BLE Scan Failed / $errorCode")
            Toast.makeText(applicationContext, "BLE Scan Failed : $errorCode", Toast.LENGTH_LONG).show()
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            if (result != null) {
                Log.d(TAG, "onScanResult")
                addScanResult(result)
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            if (results != null) {
                for (result in results) {
                    Log.d(TAG, "onBatchScanResults")
                    addScanResult(result)
                }
            }
        }
        private fun addScanResult(result: ScanResult) {
            Log.d(TAG, "add Scan Result")
            val device: BluetoothDevice = result.device
            val device_address: String = device.address
            scan_results?.put(device_address, device)
            scanDevice=device
            Log.d(TAG, "$device  $device_address  ${scan_results?.get(device)} ${scanDevice.toString()}")
        }
    }

    private val gattClientcallback: BluetoothGattCallback =object: BluetoothGattCallback(){
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if(status== BluetoothGatt.GATT_FAILURE){
                Log.d(TAG, "bluetoothGatt.GATT_FAILURE, ${is_connected}")
                disconnectGattServer()
                return
            }else if(status!= BluetoothGatt.GATT_SUCCESS){
                Log.d(TAG, "bluetoothGatt.GATT_FAILURE / ${status}")
                disconnectGattServer()
                return
            }
            if(newState== BluetoothProfile.STATE_CONNECTED){
                //setConnected(true)
                Log.d(TAG, "Connected to the GATT server")
                gatt!!.discoverServices()
            }else if(newState== BluetoothProfile.STATE_DISCONNECTED){
                Log.d(TAG, "BluetoothProfile.STATE_DISCONNECTED")
                disconnectGattServer()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt,status)
            val services: List<BluetoothGattService>?=gatt!!.services
            lateinit var characteristics: List<BluetoothGattCharacteristic>
            lateinit var descriptors: List<BluetoothGattDescriptor>

            /*discover failure*/
            if(status!= BluetoothGatt.GATT_SUCCESS){
                Log.e(TAG, "Device service discovery failed, status: $status")
                return
            }
            Log.d(TAG, "Services discovery is successful $status")

            // command characteristic을 GATT 서버로부터 찾음
            if(services!=null) {
                for (service in services) {
                    characteristics=service.characteristics
                    for(characteristic in characteristics){
                        descriptors=characteristic.descriptors
                        for(descriptor in descriptors){
                            //Log.d(TAG, "${descriptor.uuid.toString()}")
                            if(descriptor.uuid.toString().equals(Constants.CLIENT_CHARACTERISTIC_CONFIG)){
                                mGatt!!.setCharacteristicNotification(characteristic,true)
                                descriptor.value= BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                gatt.writeDescriptor(descriptor)
                            }
                        }
                    }
                }
                Log.d(TAG, "Services get ${mGatt}")
                is_connected=true
                btnconnect.progress=100
                txtble.text="블루투스 연결 완료"
                imgble.setImageResource(R.drawable.bluetooth_connected)
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if(status==BluetoothGatt.GATT_SUCCESS){
                Log.d(TAG, "Characteristic written successfully")
            }else{
                Log.e(TAG, "Characteristic write unsuccessful, status=$status")
                disconnectGattServer()
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicRead(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Characteristic read successfully")
                readCharacteristic(characteristic)
            } else {
                Log.e(TAG, "Characteristic read unsuccessful, status: $status")
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic) {
            super.onCharacteristicChanged(gatt, characteristic)
            readCharacteristic(characteristic)
        }

        private fun readCharacteristic(characteristic: BluetoothGattCharacteristic){
            val msg=characteristic.getStringValue(0)
            //_txtRead+=msg
            //txtRead.set(_txtRead)
        }
    }

    fun disconnectGattServer(){
        Log.d(TAG, "Closing Gatt connection")
        //mConnected=false
        if(mGatt !=null){
            mGatt!!.disconnect()
            mGatt!!.close()
            Log.d("mGatt","${mGatt}")
            is_connected=false
            MAC_ADDR=""
            scan_results =HashMap()
            scanDevice=null
        }
    }

    fun connectDevice(device: BluetoothDevice?){
        //txtState.setText("Connecting to $device?.address")
        Log.d(TAG,"Connecting to $device // $scanDevice")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mGatt =device!!.connectGatt(this, false, gattClientcallback,BluetoothDevice.TRANSPORT_LE)
        }
        else {
            mGatt =device!!.connectGatt(this, false, gattClientcallback)
        }
    }

    private fun requestEnableBLE() {
        val ble_enable_intent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(ble_enable_intent, Constants.REQUEST_ENABLE_BT)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startScan() {
        if (ble_adapter == null || !ble_adapter!!.isEnabled) {
            requestEnableBLE()
            //txtState.text = "Scanning Failed: ble not enabled"
            Log.d(TAG,"Scanning Failed: ble not enabled")
            return;
        }
        val filters: MutableList<ScanFilter> = ArrayList()
        val scan_filter = ScanFilter.Builder()
                .setDeviceAddress(MAC_ADDR)
                .build()
        filters.add(scan_filter)
        val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build()
        //scan_results = HashMap()

        ble_scanner = ble_adapter!!.bluetoothLeScanner
        ble_scanner?.startScan(filters, settings, scan_cb)
        is_scanning = true
        Handler(Looper.getMainLooper()).postDelayed({
            //txtState.setText("add Scanned Device : ${scanDevice}")
            Log.d("TAG", "Stop Scanning")
            stopScan()
        }, 7000L)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun stopScan(){
        Log.d("Stop Scan","stopScan")
        ble_adapter?.bluetoothLeScanner?.stopScan(scan_cb)
        is_scanning=false
        scan_results= HashMap()
        //txt.text="${scanDevice}"
        if(scanDevice!=null) {
            connectDevice(scanDevice)
            Log.d("mGatt", "${mGatt}")
        }
        else {
            Log.e(TAG, "Scan Failed. Retry, please")
            btnconnect.progress=-1
            Toast.makeText(applicationContext, "Scan Failed. Retry, please", Toast.LENGTH_LONG).show()
            //startScan()
        }
        scanDevice=null
        MAC_ADDR=""
    }
}




