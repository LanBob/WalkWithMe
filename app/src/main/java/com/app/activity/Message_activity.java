package com.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.app.JMS.bean.Message;
import com.app.R;
import com.app.Util.MyUrl;
import com.app.Util.SharedPreferencesHelper;
import com.app.Util.StringUtil;
import com.app.commonAdapter.Com_Adapter;
import com.app.commonAdapter.Com_ViewHolder;
import com.app.MainApplication;
import com.app.modle.WebSocketUtil;
import com.app.entity.Message_chat_dao;
import com.dhh.websocket.WebSocketSubscriber;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import okhttp3.WebSocket;
import okio.ByteString;

public class Message_activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Message_chat_dao message_chat_dao;
    private List<Message_chat_dao> list_dao;
    private Button button;
    private EditText editText;
    private Com_Adapter<Message_chat_dao> com_adapter;
    private TextView textView;
    private Button add_button;
    private LinearLayout linearLayout;
    private Boolean aBoolean;
    private ImageButton message_image_button;
    private ImageButton message_position_button;
    //相机相关==============================================
    private ArrayList<String> mSelectPath;
    private static final int REQUEST_IMAGE = 2;
    private ViewGroup.LayoutParams l;
    private Long userID;

    private WebSocket mwebSocket;
    private WebSocketSubscriber webSocketSubscriber;

    private String toUserId = "555";
    private String fromUserId = "444";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity);

        SharedPreferencesHelper helper = new SharedPreferencesHelper(MainApplication.getContext(), "loginState");
        userID= helper.getLong("userId");
        Log.e("id",userID + " ");

        webSocketSubscriber = new WebSocketSubscriber() {
            @Override
            protected void onOpen(@NonNull WebSocket webSocket) {
                Log.d("MainActivity", " on WebSocket open");
                mwebSocket = webSocket;
//                mwebSocket.send("haha");
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
                Log.d("MainActivity", byteString.toString());
            }

            @Override
            protected void onReconnect() {
                Log.d("MainActivity", "onReconnect");
            }

            @Override
            protected void onClose() {
                Log.d("MainActivity", "onClose");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        };

        String url = MyUrl.add_Wsurl(fromUserId+"/" + toUserId);
        Log.e("webweb","ww" +  url);

        WebSocketUtil.getInstance()
                .connect(url,this,webSocketSubscriber);

        ////===========================
        //设置用户相关，包括
        ////===========================


        //==相机选择=========================
        mSelectPath = new ArrayList<>();

        //===========================
        aBoolean = false;
        recyclerView = (RecyclerView) findViewById(R.id.message_msg_recyclerview);
        button = (Button) findViewById(R.id.msg_send);
        editText = (EditText) findViewById(R.id.msg_input_text);
        textView = (TextView) findViewById(R.id.message_msg_back);
        add_button = (Button) findViewById(R.id.message_add);
        linearLayout = (LinearLayout) findViewById(R.id.message_add_layout);
        message_image_button = (ImageButton) findViewById(R.id.message_image_button);
        message_position_button = (ImageButton) findViewById(R.id.message_position_button);

        message_position_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Message_activity.this, "位置功能未开放", Toast.LENGTH_SHORT).show();
            }
        });
        message_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiImageSelector multiImageSelector = MultiImageSelector.create();

                multiImageSelector.showCamera(true) // 是否显示相机. 默认为显示
                        .count(1) // 最大选择图片数量, 默认为9. 只有在选择模式为多选时有效
                        .multi() // 多选模式, 默认模式;
                        .origin(mSelectPath) // 默认已选择图片. 只有在选择模式为多选时有效
                        .start(Message_activity.this, REQUEST_IMAGE);
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String context = editText.getText().toString();

                if ("".equals(context) || context == null) {
                    Toast.makeText(Message_activity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    message_chat_dao = new Message_chat_dao();
                    message_chat_dao.setType(1);
                    message_chat_dao.setContent(context);
                    list_dao.add(message_chat_dao);

                    com_adapter.notifyItemChanged(list_dao.size() - 1);//新消息刷新显示
                    recyclerView.scrollToPosition(list_dao.size() - 1);
                    editText.setText("");
//                    webSocketUtil.send("url",context);
                    Message message = new Message();
//                    message.setFromUserId(fromUserId);
//                    message.setToUserId(toUserId);
//                    message.setMessage(context.getBytes());

//                    ByteString byteString = StringUtil.getByteString(message);
//                    mwebSocket.send(byteString);
                }
            }
        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aBoolean) {
                    //如果已经展开
                    linearLayout.setVisibility(View.GONE);
                    aBoolean = false;
                } else {
                    linearLayout.setVisibility(View.VISIBLE);
                    aBoolean = true;
                }
            }
        });

        initData();
        Log.e("size", list_dao.size() + " ");
        recyclerView.setLayoutManager(new LinearLayoutManager(Message_activity.this));
        recyclerView.setAdapter(com_adapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            List<File> fileList = new ArrayList<>();
            File f = null;
            FileInputStream in;
            ByteArrayOutputStream out;
            byte[] ret = null;
            //得到图片路径
            for (String s: mSelectPath) {
                message_chat_dao = new Message_chat_dao();
                message_chat_dao.setType(1);
                message_chat_dao.setContent(null);
                if (s != null)
                {
                    message_chat_dao.setPath(s);
                    list_dao.add(message_chat_dao);
                    f= new File(s);
                    try {
                        in = new FileInputStream(f);
                        out = new ByteArrayOutputStream(4096);
                        byte[] b = new byte[4096];
                        int n;
                        while ((n = in.read(b)) != -1) {
                            out.write(b, 0, n);
                        }
                        in.close();
                        out.close();
                        ret = out.toByteArray();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    fileList.add(f);
                }
            }

            com_adapter.notifyItemChanged(list_dao.size() - 1);//新消息刷新显示

            Message message = new Message();
//            message.setFromUserId(fromUserId);
//            message.setToUserId(toUserId);
//            message.setMessage(StringUtil.getBytes(f));

            /**
             * 下面发送二进制消息
             */
            ByteString byteString = StringUtil.getByteString(message);

            Log.e("len", String.valueOf(byteString.size()));
//            mwebSocket.send(byteString);
        }
    }

    public void initData() {
        list_dao = new ArrayList<Message_chat_dao>();
        for (int i = 1; i < 3; ++i) {
            message_chat_dao = new Message_chat_dao();
            message_chat_dao.setContent("hi,how are you ?");
            message_chat_dao.setType(0);
            list_dao.add(message_chat_dao);
            message_chat_dao = new Message_chat_dao();
            message_chat_dao.setContent("I am fine .Thanks");
            message_chat_dao.setType(1);
            list_dao.add(message_chat_dao);
        }

        com_adapter = new Com_Adapter<Message_chat_dao>(Message_activity.this, R.layout.message_activity_chat_item, list_dao) {
            @Override
            public void convert(Com_ViewHolder holder, Message_chat_dao message_chat_dao) {
                if (message_chat_dao.getType() == Message_chat_dao.TYPE_RECEIVED) {
                    //收数据
                    holder.itemView.findViewById(R.id.left_layout).setVisibility(View.VISIBLE);
                    holder.itemView.findViewById(R.id.right_layout).setVisibility(View.GONE);
                    if (message_chat_dao.getContent() != null) {//如果是文字数据
                        holder.itemView.findViewById(R.id.left_msg).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.left_iamge_card_view).setVisibility(View.GONE);
                        holder.setText(R.id.left_msg, message_chat_dao.getContent());
                    } else {
                        //holder.setImageResource(R.id.left_image, message_chat_dao.getPath());
                        //如果是图片数据
                        holder.itemView.findViewById(R.id.left_msg).setVisibility(View.GONE);
                        holder.itemView.findViewById(R.id.left_iamge_card_view).setVisibility(View.VISIBLE);
                       // holder.setPathImage(R.id.left_image,message_chat_dao.getPath());
                    }

                } else {
                    //发送
                    holder.itemView.findViewById(R.id.left_layout).setVisibility(View.GONE);
                    holder.itemView.findViewById(R.id.right_layout).setVisibility(View.VISIBLE);
                   // holder.setText(R.id.right_msg, message_chat_dao.getContent());

                    if (message_chat_dao.getContent() != null) {
                        //发送消息
                        holder.itemView.findViewById(R.id.right_msg).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.right_iamge_card_view).setVisibility(View.GONE);
                        holder.setText(R.id.right_msg, message_chat_dao.getContent());
                    } else {
                        //图片
                        holder.itemView.findViewById(R.id.right_msg).setVisibility(View.GONE);
                        holder.itemView.findViewById(R.id.right_iamge_card_view).setVisibility(View.VISIBLE);
                        holder.setPathImage(R.id.right_image,message_chat_dao.getPath());
                    }
                }
            }
        };

    }

}


            /*
            //list
            String url = "http://172.21.212.125:8080/app/message";

            //======================
            MyOkhttpUtil.okHttpUploadListFile(url, fileList, "image",
                    MyOkhttpUtil.FILE_TYPE_IMAGE, new MyCallBackUtil.CallBackString() {
                        @Override
                        public void onFailure(Call call, Exception e) {
                            Log.e("error",e.toString() + ", " + call.toString());
                        }

                        @Override
                        public void onResponse(String response) {
                            Log.e("resssss",response);
                        }
                    });
            */