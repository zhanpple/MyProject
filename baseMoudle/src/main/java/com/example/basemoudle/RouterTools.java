package com.example.basemoudle;

import android.content.Context;

import java.util.HashMap;


/**
 * Created at 14:49 2020/9/17
 *
 * @author zmp
 * <p>
 * des:
 */
public class RouterTools {
    private static volatile RouterTools mRouterTools;
    private static HashMap<String, Class<?>> mRouterMap;

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

    public void addRouter(String router, Class<?> clazz) {
        mRouterMap.put(router, clazz);
    }

    public void router(String router) {
        System.out.println(mRouterMap.get(router));
    }

    public void init(Context context){

        context.getPackageManager().

    }


}
