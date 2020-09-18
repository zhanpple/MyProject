package com.zhanpple.appc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.annotation.MyRouter
import com.example.basemoudle.RouterTools
import kotlinx.android.synthetic.main.activity_c_main.*

@MyRouter("appc/CMainActivity")
class CMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_c_main)
        tv.setOnClickListener {
            RouterTools.getInstance().navigate(this,"MainActivity")
        }
    }
}
