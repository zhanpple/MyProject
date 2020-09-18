package com.example.basemoudle;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexFile;


/**
 * Created at 14:49 2020/9/17
 *
 * @author zmp
 * <p>
 * des:
 */
public class RouterTools {
    private static volatile RouterTools mRouterTools;
    private static HashMap<String, Class<? extends Activity>> mRouterMap;

    private RouterTools() {
        mRouterMap = new HashMap<>();
    }

    public static RouterTools getInstance() {
        if (mRouterTools == null) {
            synchronized (RouterTools.class) {
                mRouterTools = new RouterTools();
            }
        }
        return mRouterTools;
    }

    public void addRouter(String router, Class<? extends Activity> clazz) {
        mRouterMap.put(router, clazz);
    }

    public void router(String router) {
        System.out.println(mRouterMap.get(router));
    }

    public void init(Context context) {
        String packageResourcePath = context.getApplicationContext().getPackageResourcePath();
        try {
            DexFile dexFile = new DexFile(packageResourcePath);

            Enumeration<String> entries = dexFile.entries();
            while (entries.hasMoreElements()) {
                String element = entries.nextElement();
                if (element.contains("com.example.router")) {
                    Log.e("RouterTools", "init: " + element);
                    Class<?> aClass = Class.forName(element);
                    if (IRouter.class.isAssignableFrom(aClass)) {
                        aClass.newInstance();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("RouterTools", "init: ", e);
        }

        for (Map.Entry<String, Class<? extends Activity>> stringClassEntry : mRouterMap.entrySet()) {
            Log.e("RouterTools", "stringClassEntry: "
                    + stringClassEntry.getKey() + "--" + stringClassEntry.getValue());

        }
    }


}
