package com.zmp.javaproject

import android.graphics.BitmapRegionDecoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.MessageQueue
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.example.annotation.MyRouter
import com.example.basemoudle.RouterTools
import com.example.router.appc.`MyRouter$$APPC`


@MyRouter("MainActivity")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        RouterTools.getInstance().init(this)
        RouterTools.getInstance().navigate(this,"appb/BMainActivity")


    }


}