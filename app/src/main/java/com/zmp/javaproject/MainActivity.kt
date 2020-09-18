package com.zmp.javaproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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