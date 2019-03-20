package com.app.JMS;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.app.JMS.bean.ChatListBean;
import com.app.JMS.bean.MsgSendStatus;
import com.app.JMS.bean.SqlMessage;
import com.app.JMS.util.LogUtil;
import com.app.JMS.util.MessageUtil;
import com.app.MainApplication;
import com.app.Util.MyUrl;
import com.app.Util.StringUtil;
import com.app.modle.WebSocketUtil;
import com.dhh.websocket.WebSocketSubscriber;
import com.app.JMS.bean.Message;


import io.reactivex.annotations.NonNull;
import okhttp3.WebSocket;
import okio.ByteString;

public class ChatService extends Service {

    // Binder given to clients
    private final IBinder myBinder = new MyBinder();

//    private IBinder myBinder;

    private WebSocket mwebSocket;
    private WebSocketSubscriber webSocketSubscriber;

    public class MyBinder extends Binder {
        public ChatService getService() {
            return ChatService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        Log.e("ee", "e null");

        super.onCreate();
    }

    //开启服务被调用
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    //    结束服务的时候被调用
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void sendMessage(ByteString byteString) {
        Log.e("send","发送消息" + byteString.toString());
        if (mwebSocket != null)
            mwebSocket.send(byteString);
    }

    public void setWebSocket(String fromUserId, Activity activity) {

        webSocketSubscriber = new WebSocketSubscriber() {
            @Override
            protected void onOpen(@NonNull WebSocket webSocket) {
                Log.d("MainActivity", " on WebSocket open");
                mwebSocket = webSocket;
//                MessageUtil.setMwebSocket(webSocket);
            }

            /**
             * 接收到消息
             * @param text
             */
            @Override
            protected void onMessage(@NonNull String text) {
                Log.d("MainActivity", text);
            }

            /**
             * 二进制消息
             * @param byteString
             */
            @Override
            protected void onMessage(@NonNull ByteString byteString) {
                Log.e("recevieMessage", byteString.toString());
//                MessageUtil.savaMessage(byteString, getApplicationContext().getFilesDir().getAbsolutePath(),"1");
            }

            @Override
            protected void onReconnect() {
            }

            @Override
            protected void onClose() {
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        };

        String url = MyUrl.add_Wsurl(fromUserId);

//
//        WebSocketUtil.getInstance()
//                .connect(url, webSocketSubscriber);
    }

}
