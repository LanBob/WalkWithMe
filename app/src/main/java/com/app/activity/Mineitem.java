package com.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.R;
import com.app.Util.SharedPreferencesHelper;
import com.app.commonAdapter.Com_Adapter;
import com.app.commonAdapter.Com_ViewHolder;
import com.app.Fragments.MainActivity;
import com.app.MainApplication;
import com.app.view.StepDialog;
import com.app.view.ZoomOutPageTransformer;
import com.app.entity.Find_item_dao;
import com.app.entity.Myfind_dao;
import com.app.entity.Mytraval_dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * index 表
 * "申请成为导游", "我的旅行", "订单", "修改密码", "反馈", "退出登录", "添加账号", "关于"
 * 1           2          3        4        5       6          7         8
 * <p>
 * <p>
 * Token:
 * 客户端使用用户名跟密码请求登录
 * 服务端收到请求，去验证用户名与密码
 * 验证成功后，服务端会签发一个 Token，再把这个 Token 发送给客户端
 * 客户端收到 Token 以后可以把它存储起来，比如放在 Cookie 里或者 Local Storage 里
 * 客户端每次向服务端请求资源的时候需要带着服务端签发的 Token
 * 服务端收到请求，然后去验证客户端请求里面带着的 Token，如果验证成功，就向客户端返回请求的数据
 */

public class Mineitem extends AppCompatActivity {

    private ActionBar actionBar;
    private TextView textView;
    private String text;
    private ImageView imageView;

    //我的旅行
    private RecyclerView mytraval_recyclerView;
    private TextView mytraval_textView;
    private List<Mytraval_dao> mytraval_list;


    //发现关注=========================================
    private List<Myfind_dao> myfind_daos;
    private RecyclerView myfind_recyclerview;
    private TextView myfind_nothing;

    private List<Find_item_dao> find_item_list;

    //反馈
    private Button feedbackButton;
    private EditText feedback_editText_title;
    private EditText feedback_editText_body;
    private static SharedPreferencesHelper helper = null;
    static {
        helper = new SharedPreferencesHelper(MainApplication.getContext(), "loginState");
    }

    //修改密码
    private Button change_password_button;
    private EditText change_password_username;
    private EditText change_password_old;
    private EditText change_password_new;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int index = -1;
        index = Integer.parseInt(intent.getStringExtra("index"));
        Log.e("index", "" + index);
        switch (index) {
            case 1:
                setContentView(R.layout.login);
                StepDialog.getInstance()
                        .setPageTransformer(new ZoomOutPageTransformer())
                        .setCanceledOnTouchOutside(true)
                        .setImages(new int[]{R.drawable.home_backgroud, R.drawable.home_nine_else, R.drawable.home_nine_humanity, R.drawable.home_nine_healthcare})
                        .show(getFragmentManager());
                actionBar = getSupportActionBar();
                actionBar.setTitle("申请成为导游");
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);

                break;
            case 2:
                setContentView(R.layout.main_item_2);
                actionBar = getSupportActionBar();
                actionBar.setTitle("我的旅行");
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
                //============================获取订单消息,无或者recyclerView
                mytraval_recyclerView = (RecyclerView) findViewById(R.id.mine_item_mytraval_recyclerview);
                mytraval_textView = (TextView) findViewById(R.id.mine_item_mytraval_nothing);

                mytraval_list = new ArrayList<>();
                Mytraval_dao dao = new Mytraval_dao();
                dao.setTitle("欢迎您");
                dao.setPosition("广州");
                dao.setMoney(new BigDecimal(50.0));
                mytraval_list.add(dao);

                if (mytraval_list.size() > 0) {
                    mytraval_textView.setVisibility(View.GONE);
                    mytraval_recyclerView.setVisibility(View.VISIBLE);
                    mytraval_recyclerView.setLayoutManager(new LinearLayoutManager(Mineitem.this));

                    mytraval_recyclerView.setAdapter(new Com_Adapter<Mytraval_dao>(Mineitem.this, R.layout.main_item_2_item, mytraval_list) {
                        @Override
                        public void convert(Com_ViewHolder holder, Mytraval_dao mytraval_dao) {
                            holder.setText(R.id.main_item_2_item_title, mytraval_dao.getTitle());
                            holder.setText(R.id.main_item_2_item_position, mytraval_dao.getPosition());
                            holder.setText(R.id.main_item_2_item_money, mytraval_dao.getMoney().toString());
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(Mineitem.this, "show 订单", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } else {
                    mytraval_textView.setVisibility(View.VISIBLE);
                    mytraval_recyclerView.setVisibility(View.GONE);
                }

                //============================

                break;
            case 3:

                break;
            case 4:
                setContentView(R.layout.main_item_4);
                actionBar = getSupportActionBar();
                actionBar.setTitle("修改密码");
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
                change_password_button = (Button) findViewById(R.id.change_password_button);
                change_password_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String username = change_password_username.getText().toString();
                        String old_password = change_password_old.getText().toString();
                        String new_password = change_password_new.getText().toString();

                        if(username.length() <= 1 || old_password.length() <= 1 || new_password.length() <= 1){
                        }
                    }
                });

                break;
            case 5:
                setContentView(R.layout.main_item_5);
                actionBar = getSupportActionBar();
                actionBar.setTitle("反馈");
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
                feedback_editText_title = (EditText) findViewById(R.id.feedback_title);
                feedback_editText_body = (EditText) findViewById(R.id.feedback_body);
                feedbackButton = (Button) findViewById(R.id.feedback_buttom);

                feedbackButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String title = feedback_editText_title.getText().toString();
                        String body = feedback_editText_body.getText().toString();

                        if(title.length() <= 1 || body.length() <= 1){
                            Toast.makeText(Mineitem.this,"问题标题或内容不能为空",Toast.LENGTH_SHORT).show();
                        }else if(title.length() > 20 || body.length() > 100){
                            Toast.makeText(Mineitem.this,"问题标题或内容超限制",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(Mineitem.this,"" + title + ","+ body,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case 6:
                setContentView(R.layout.main_item_6);
                /*
                actionBar = getSupportActionBar();
                actionBar.setTitle("退出登录");
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
                */

                //马上又跳回来
//                new SharedPreferencesHelper(MainApplication.getContext(), "in")
                helper.putValues(new SharedPreferencesHelper.ContentValue("isAlreadyLogin", "N"));
//                new SharedPreferencesHelper(MainApplication.getContext(), "user")
                helper.putValues(new SharedPreferencesHelper.ContentValue("username", ""));

                Intent myintent = new Intent(Mineitem.this, MainActivity.class);
                myintent.putExtra("position", 3);
                finish();
                startActivity(myintent);
                break;

            case 7:
                setContentView(R.layout.main_item_7);
                actionBar = getSupportActionBar();
                actionBar.setTitle("添加账号");
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
                break;
            default:
                Log.e("ee", "e11");
                setContentView(R.layout.main_item_8);
                actionBar = getSupportActionBar();
                actionBar.setTitle("关于");
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);

                break;
        }


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

}