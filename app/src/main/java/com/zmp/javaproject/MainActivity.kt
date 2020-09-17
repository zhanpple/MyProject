package com.zmp.javaproject

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.lib.FindViewByID
import com.example.lib.MyRouter

@MyRouter("MainActivity")
class MainActivity : AppCompatActivity() {


    @FindViewByID(R.id.myView)
    lateinit var myView :View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }



}