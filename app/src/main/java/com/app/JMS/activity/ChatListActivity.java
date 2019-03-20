package com.app.JMS.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.app.JMS.ChatService;
import com.app.JMS.bean.ChatListBean;
import com.app.JMS.bean.Message;
import com.app.JMS.bean.SqlMessage;
import com.app.JMS.util.MessageUtil;
import com.app.R;
import com.app.Util.LogOutUtil;
import com.app.Util.MyUrl;
import com.app.Util.StringUtil;
import com.app.commonAdapter.Com_Adapter;
import com.app.commonAdapter.Com_ViewHolder;
import com.app.modle.WebSocketUtil;
import com.app.view.BadgeView;
import com.dhh.websocket.WebSocketSubscriber;
import com.dhh.websocket.WebSocketSubscriber2;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
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


    private  WebSocket mwebSocket;
    private  WebSocketSubscriber webSocketSubscriber;

    private Disposable disposable;
    private String userId = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_message_list);
        actionBar = getSupportActionBar();
        actionBar.setTitle("聊天列表");
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

        adapter = new Com_Adapter<ChatListBean>(ChatListActivity.this, R.layout.message_list_item, listBeans) {
            @Override
            public void convert(Com_ViewHolder holder, final ChatListBean chatListBean) {
                if (chatListBean != null) {
                    holder.setText(R.id.userId, chatListBean.getUserId());

                    if (chatListBean.getCount() > 0) {
                        TextView count = holder.itemView.findViewById(R.id.count);
                        BadgeView badge = new BadgeView(ChatListActivity.this, count);
                        badge.setBadgePosition(BadgeView.POSITION_CENTER);
//                        Log.e("more zeor","do it show");
                        count.setVisibility(View.VISIBLE);
                        badge.setText(chatListBean.getCount() + "");
                        badge.show();
                        holder.itemView.findViewById(R.id.showMessage).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.noMessage).setVisibility(View.GONE);
                    } else {
                        holder.itemView.findViewById(R.id.showMessage).setVisibility(View.GONE);
                        holder.itemView.findViewById(R.id.noMessage).setVisibility(View.VISIBLE);
                    }
                    holder.setText(R.id.time, chatListBean.getTime());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ChatListActivity.this, SplashActivity.class);
                            intent.putExtra("userId", chatListBean.getUserId());
                            startActivity(intent);
                        }
                    });
                }
            }
        };
        recyclerView.setAdapter(adapter);
        if (userId != null && StringUtil.isMobile(userId)) {
            setWebSocket(userId,ChatListActivity.this);
            Log.e("use", userId);
            reciveMessage();
        }

    }

    private void setWebSocket(String fromUserId, Activity activity) {
        webSocketSubscriber = new WebSocketSubscriber() {
            @Override
            protected void onOpen(@NonNull WebSocket webSocket) {
                Log.e("open","open");
                super.onOpen(webSocket);
            }

            @Override
            protected void onMessage(@NonNull String text) {
                Log.e("text","text" + text);
                super.onMessage(text);
            }

            @Override
            protected void onMessage(@NonNull ByteString byteString) {
                Log.e("byteString","byteString" + byteString.toString());
                MessageUtil.savaMessage(byteString,getApplicationContext().getFilesDir().getAbsolutePath());
                super.onMessage(byteString);
            }

            @Override
            protected void onReconnect() {
                Log.e("onReconnect","onReconnect");
                super.onReconnect();
            }

            @Override
            protected void onClose() {
                Log.e("onClose","onClose");
                super.onClose();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("Throwable","e" + e.getMessage());
                super.onError(e);
            }
        };
        WebSocketUtil.getInstance().connect(MyUrl.add_Wsurl(fromUserId),
                activity,webSocketSubscriber);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        if (disposable != null) {
            disposable.dispose();
        }
        super.onStop();
    }

    @Override
    protected void onRestart() {
        reciveMessage();
        super.onRestart();
    }

    public void closeTimer(){
        if (disposable != null) {
            disposable.dispose();
        }
    }
    public void reciveMessage() {

        Observable.interval(0, 3, TimeUnit.SECONDS)
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

                    }
                }).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Long num) {

                listBeans.clear();
//                        查询所有
                List<ChatListBean> chatListBeans = StringUtil.query();
                listBeans.addAll(chatListBeans);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                if (disposable != null) {
                    disposable.dispose();
                }
            }

            @Override
            public void onComplete() {
                //回复原来初始状态
                closeTimer();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //对用户按home icon的处理，本例只需关闭activity，就可返回上一activity，即主activity。
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }
}
