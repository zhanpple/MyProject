package com.example.basemoudle;

import android.app.Activity;

import java.util.HashMap;

public interface IRouter {
    void addRouter(HashMap<String, Class<? extends Activity>> hashMap);
}
