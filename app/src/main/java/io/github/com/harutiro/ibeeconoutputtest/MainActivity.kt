package io.github.com.harutiro.ibeeconoutputtest

import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import io.github.com.harutiro.ibeeconoutputtest.databinding.ActivityMainBinding
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BeaconTransmitter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val PERMISSION_REQUEST_COARSE_LOCATION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //パーミッション確認
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }

        //bindingの作成
        binding = ActivityMainBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        //ビーコンのパーサーの作成、ibeaconの取得をするときに必要
        val beaconParser = BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
        val beaconTransmitter = BeaconTransmitter(applicationContext, beaconParser)

        //ibeaconが出力されているか判断する部分
        binding.stateCheckButton.setOnClickListener {
            binding.stateTextbox.text = beaconTransmitter.isStarted.toString()
        }

        //ibeaconの出力を止める。
        binding.ibeaconStopButton.setOnClickListener {
            beaconTransmitter.stopAdvertising()
            binding.stateTextbox.text = beaconTransmitter.isStarted.toString()

        }

        //ibeaconの出力を開始する。
        binding.ibeeconStartButton.setOnClickListener {
            //テキストボックスの情報を取得
            val uuid = binding.uuidEditTextbox.text.toString()
            val major = binding.majarEditTextbox.text.toString()
            val minor = binding.minorEditTextbox.text.toString()

            //beaconのビルダーで、どんなデータを送信するか作成する。
            val beacon = Beacon.Builder()
                .setId1(uuid)
                .setId2(major)
                .setId3(minor)
                .setManufacturer(0x004C)
                .build()

            //実際にbeaconを開始する部分
            beaconTransmitter.startAdvertising(beacon, object : AdvertiseCallback() {

                //正しく動作したとき
                override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                    super.onStartSuccess(settingsInEffect)
                    Log.d("debag","OK")
                    binding.stateTextbox.text = beaconTransmitter.isStarted.toString()

                }

                //失敗したとき
                override fun onStartFailure(errorCode: Int) {
                    Log.d("debag","NO")
                }
            })


        }
    }
}