package com.app.JMS.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.app.JMS.ChatService;
import com.app.JMS.bean.ChatListBean;
import com.app.JMS.bean.Message;
import com.app.JMS.bean.SqlMessage;
import com.app.R;
import com.app.Util.LogOutUtil;
import com.app.Util.MyUrl;
import com.app.Util.StringUtil;
import com.app.commonAdapter.Com_Adapter;
import com.app.commonAdapter.Com_ViewHolder;
import com.app.modle.WebSocketUtil;
import com.app.view.BadgeView;
import com.dhh.websocket.WebSocketSubscriber;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.WebSocket;
import okio.ByteString;

/**
 * 这里要指定发送给谁
 */
public class ChatListActivity extends AppCompatActivity implements View.OnClickListener {

    ActionBar actionBar;
    private RecyclerView recyclerView;
    private List<ChatListBean> listBeans;
    private RecyclerView.Adapter adapter;

    private WebSocket mwebSocket;
    private WebSocketSubscriber webSocketSubscriber;
    private Disposable disposable;
    private String userId = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_message_list);
        actionBar = getSupportActionBar();
        actionBar.setTitle("我的旅行");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        userId = StringUtil.getValue("username");
        initView();
    }


    private void initView() {
        recyclerView = findViewById(R.id.message_list_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatListActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        listBeans = new ArrayList<>();

//        固定至少有一个Bean
//        listBeans = StringUtil.query();
        List<ChatListBean> ll  = StringUtil.query();
        LogOutUtil.d("listBean"+ll.size());
        if(ll.size() == 0){
            ChatListBean chatListBean = new ChatListBean();
            chatListBean.setCount(0);
            chatListBean.setUserId(MyUrl.getKefu());
            chatListBean.setTime(StringUtil.getToMinute(System.currentTimeMillis()));
            StringUtil.insertChatListBean(chatListBean);
            listBeans.add(chatListBean);
        }else {
            listBeans.clear();
            listBeans.addAll(ll);
        }

        adapter = new Com_Adapter<ChatListBean>(ChatListActivity.this, R.layout.message_list_item, listBeans) {
            @Override
            public void convert(Com_ViewHolder holder, final ChatListBean chatListBean) {
                if(chatListBean != null){
                    Log.e("in insert holder" ,"chat" + chatListBean.getCount() );
                    holder.setText(R.id.userId,chatListBean.getUserId());
                    TextView count = holder.itemView.findViewById(R.id.count);

                    if(chatListBean.getCount() > 0){
                        BadgeView badge = new BadgeView(ChatListActivity.this, count);
                        badge.setBadgePosition(BadgeView.POSITION_CENTER);
                        badge.setText(chatListBean.getCount() +"");
                        badge.show();
                        holder.itemView.findViewById(R.id.showMessage).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.noMessage).setVisibility(View.GONE);
                    }else {
                        count.setText(" ");
                        holder.itemView.findViewById(R.id.showMessage).setVisibility(View.GONE);
                        holder.itemView.findViewById(R.id.noMessage).setVisibility(View.VISIBLE);
                    }
                    holder.setText(R.id.time,chatListBean.getTime());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent  =  new Intent(ChatListActivity.this,SplashActivity.class);
                            intent.putExtra("userId",chatListBean.getUserId());
                            startActivity(intent);
                        }
                    });
                }
            }
        };
        recyclerView.setAdapter(adapter);

        if(userId != null){
            setWebSocket(userId);
            reciveMessage();
        }

    }

    public void setWebSocket(String fromUserId) {
//        LogOutUtil.d("insert fromUserId" + fromUserId);
        webSocketSubscriber = new WebSocketSubscriber() {
            @Override
            protected void onOpen(@NonNull WebSocket webSocket) {
                Log.d("MainActivity", " on WebSocket open");
                mwebSocket = webSocket;
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
                LogOutUtil.d("insert user");
                byte[] bytes = byteString.toByteArray();

//                插入操作，进行
                Message message = (Message) StringUtil.byteToObject(bytes);

                ChatListBean chatListBean = new ChatListBean();
                chatListBean.setTime(StringUtil.getToMinute(message.getSentTime()));
                int count = StringUtil.getUserIdCount(message.getTargetId());
                chatListBean.setCount(count + 1);

                Log.e("insert ",chatListBean.getCount() + "");
                chatListBean.setUserId(message.getSenderId());

//                只要有消息来，就会触发进行存储
                StringUtil.insertChatListBean(chatListBean);

                String absolutePath  = getApplicationContext().getFilesDir().getAbsolutePath();
                String fileName = String.valueOf(System.currentTimeMillis());
//                ByteString进行存储
                StringUtil.createFile(
                        absolutePath,fileName);
                String path = absolutePath + File.pathSeparator + fileName;
                StringUtil.writeFile(bytes,path,false);

//                将这个消息，存储到数据库
                SqlMessage sqlMessage = new SqlMessage();
                sqlMessage.setTime(String.valueOf(message.getSentTime()));
                sqlMessage.setPath(path);
                sqlMessage.setUserId(message.getTargetId());
//                只要有消息来，就会存储S在qlMesage之中
                StringUtil.insertSqlMessage(sqlMessage);
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

        WebSocketUtil.getInstance()
                .connect(url, ChatListActivity.this, webSocketSubscriber);
    }

    public void reciveMessage() {

        Observable.interval(0, 5, TimeUnit.SECONDS)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return aLong + 1;
                    }
                })
                .take(50)
                .observeOn(AndroidSchedulers.mainThread())//ui线程中进行控件更新
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        List<ChatListBean> chatListBeans = StringUtil.query();
                        LogOutUtil.d("list" + chatListBeans.size());
                        if(chatListBeans.size() > 0)
                        for (ChatListBean c :
                                chatListBeans) {
                            if(!listBeans.contains(c) && !"13724158682".equals(c.getUserId())){
                                LogOutUtil.d("insert  in for" + c.getUserId() );
                                listBeans.add(c);
                            }

                        }
                    }
                }).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Long num) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                if(disposable != null){
                    disposable.dispose();
                }
            }

            @Override
            public void onComplete() {
                //回复原来初始状态

            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
