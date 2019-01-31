package com.app;


import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.dhh.websocket.Config;

import java.util.concurrent.TimeUnit;


public class MainApplication extends MultiDexApplication {

    /**
     * 全局的上下文.
     */
    private static Context mContext;
    public Config config;
    public static Application	mApplication;



    @Override
    public void onCreate() {

        super.onCreate();
        mApplication = this;
        mContext = getApplicationContext();
        initConfig();

    }

    /**
     * 初始化WebSocket配置
     */
    private void initConfig() {
        config = new Config.Builder()
                .setReconnectInterval(4, TimeUnit.SECONDS)//设置重连间隔
                .build();

        /*
         config = new Config.Builder()
                .setReconnectInterval(4, TimeUnit.SECONDS)//设置重连间隔
                .setClient(new OkHttpClient.Builder()
                        .pingInterval(7, TimeUnit.SECONDS)
                        .build()//设置心跳包
                ).build();
         */

    }

    @Override
    public void onTerminate() {
        Log.e("tm","time");
        super.onTerminate();
        Log.e("tm","time");
    }

    /**
     * 获取Context.
     *
     * @return
     */
    public static Context getContext() {
        return mContext;
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

}

