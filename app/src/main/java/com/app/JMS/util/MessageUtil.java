package com.app.JMS.util;

import android.app.Activity;
import android.util.Log;

import com.app.JMS.bean.ChatListBean;
import com.app.JMS.bean.Message;
import com.app.JMS.bean.SqlMessage;
import com.app.Util.MyUrl;
import com.app.Util.StringUtil;
import com.app.modle.WebSocketUtil;
import com.dhh.websocket.WebSocketSubscriber;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;

import io.reactivex.annotations.NonNull;
import okhttp3.WebSocket;
import okio.ByteString;

public class MessageUtil {

//    private static WebSocket mwebSocket;
//    private static WebSocketSubscriber webSocketSubscriber;
//
//    public static WebSocket getMwebSocket() {
//        return mwebSocket;
//    }
//
//    public static void setMwebSocket(WebSocket webSocket) {
//        mwebSocket = webSocket;
//    }

    public static void savaMessage(ByteString byteString, String fileRoute){
        String whoLogin = StringUtil.getValue("username");

//        byte[] bytes = byteString.toByteArray();
        ByteBuffer byteBuffer = byteString.asByteBuffer();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes, 0, bytes.length);

//        Message message = (Message) StringUtil.byteToObject(bytes);
        Message message = (Message) toObject(bytes);

//        待改善，现在消息记录里只能有他人的聊天记录
        if(!message.getSenderId().equals(whoLogin)){
            ChatListBean chatListBean = new ChatListBean();
            chatListBean.setTime(StringUtil.getToMinute(message.getSentTime()));

//        里面的userId是从谁发来的,get获取都是数字，null的话就是0
            int count = StringUtil.getUserIdCount(message.getSenderId());

            chatListBean.setCount(count + 1);
            chatListBean.setUserId(message.getSenderId());

//                只要有消息来，就会触发进行存储，插入聊天列表之中
            StringUtil.insertChatListBean(chatListBean);
            Log.e("user" + message.getSenderId() + " 发来的消息","插入到数据库之中,数据库现在有多少人？：" + StringUtil.query().size());
        }


        String absolutePath  = fileRoute;
        String fileName = String.valueOf(System.currentTimeMillis());
//                ByteString进行存储
        StringUtil.createFile(
                absolutePath,fileName);
        String path = absolutePath + File.pathSeparator + fileName;
//      保存消息到本地
        StringUtil.writeFile(bytes,path,false);

//                将这个消息，存储到数据库
        SqlMessage sqlMessage = new SqlMessage();
        sqlMessage.setTime(String.valueOf(message.getSentTime()));
        sqlMessage.setPath(path);


//        此情况是，本登录的人发的消息
        if(message.getSenderId().equals(whoLogin)){
            //        消息是从谁发来的,满足本人先，对方后面
            sqlMessage.setUserId(message.getSenderId() + message.getTargetId());

        }else {
            sqlMessage.setUserId(message.getTargetId()+message.getSenderId());
        }
//      会存储S在qlMesage之中
        StringUtil.insertSqlMessage(sqlMessage);
        Log.e("user" + message.getSenderId() + " 发来的消息","插入到数据库之中,数据库中这个人现在有条消息" + StringUtil.getSqlMessage(message.getSenderId()).size());
    }

    public static void clearCount(String userId){
        StringUtil.clearCount(userId);
    }


    /**
     * 数组转对象
     * @param bytes
     * @return
     */
    public static Object toObject (byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
            ObjectInputStream ois = new ObjectInputStream (bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return obj;
    }

}
