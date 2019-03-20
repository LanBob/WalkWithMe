package com.app.modle;

import android.content.Context;

import com.dhh.rxlifecycle.RxLifecycle;
import com.dhh.websocket.Config;
import com.dhh.websocket.RxWebSocket;
import com.dhh.websocket.WebSocketInfo;
import com.dhh.websocket.WebSocketSubscriber;

import java.util.concurrent.TimeUnit;

import okhttp3.WebSocket;
import okio.ByteString;

public class WebSocketUtil {
    private WebSocket mWebSocket;
    private Config config;

    private WebSocketUtil(){
        initConfig();
        RxWebSocket.setConfig(config);
    }

    public static WebSocketUtil getInstance(){
        return SingletonHolder.INSTANCE;
    }
    public void connect(String url,Context context,WebSocketSubscriber webSocketSubscriber) {
        RxWebSocket.get(url)
                .compose(RxLifecycle.with(context).<WebSocketInfo>bindOnDestroy())
                .subscribe(webSocketSubscriber);
    }

    /**
     * 异步发送数据
     * @param url url
     * @param msg 文字消息
     */
    public static void send(String url,String msg){
        //异步发送,若WebSocket已经打开,直接发送,若没有打开,打开一个WebSocket发送完数据,直接关闭.
        RxWebSocket.asyncSend(url, msg);
    }

    /**
     * 异步发送数据
     * @param url 路径
     * @param byteString 文字消息
     */
    public static void sendByte(String url,ByteString byteString){
        RxWebSocket.asyncSend(url, byteString);
    }


//==================================================对内操作
    //在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final WebSocketUtil INSTANCE = new WebSocketUtil();
    }

    /**
     * 初始化
     */
    private void initConfig() {
        config = new Config.Builder()
                .setReconnectInterval(4, TimeUnit.SECONDS)//设置重连间隔
                .build();
    }
}
