package com.zmp.javaproject.cc;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lib.FindViewByID;
import com.example.lib.MyRouter;
import com.zmp.javaproject.R;


/**
 * Created at 17:42 2020/9/14
 *
 * @author zmp
 * <p>
 * des:
 */
@MyRouter("AAA")
public class AAA{
    @FindViewByID(R.id.myView)
    View myVie;
    @FindViewByID(R.id.myView)
    View myVie2;
    @FindViewByID(R.id.myView)
    View myVie3;

}
