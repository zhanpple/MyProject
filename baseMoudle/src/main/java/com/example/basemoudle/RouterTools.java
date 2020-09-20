package com.example.basemoudle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.Modifier;
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
                if (mRouterTools == null) {
                    mRouterTools = new RouterTools();
                }
            }
        }
        return mRouterTools;
    }

    public void addRouter(String router, Class<? extends Activity> clazz) {
        mRouterMap.put(router, clazz);
    }

    public void navigate(Context context, String router) {
        System.out.println(mRouterMap.get(router));
        context.startActivity(new Intent(context, mRouterMap.get(router)));
    }

    public void init(Context context) {
        String packageResourcePath = context.getApplicationContext().getPackageResourcePath();
        try {
            DexFile dexFile = new DexFile(packageResourcePath);
            Enumeration<String> entries = dexFile.entries();
            while (entries.hasMoreElements()) {
                String element = entries.nextElement();
                Log.e("RouterTools", "init: " + element);
                if (element.contains("com.example.router")) {
                    Log.e("RouterTools", "init: " + element);
                    Class<?> aClass = Class.forName(element);
                    int modifiers = aClass.getModifiers();
                    if (IRouter.class.isAssignableFrom(aClass) && !Modifier.isInterface(modifiers)) {
                        ((IRouter) aClass.newInstance()).addRouter(mRouterMap);
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
