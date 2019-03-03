package com.app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import com.app.Util.LoadingDialogUtil;
import com.app.Util.MyUrl;
import com.app.Util.StringUtil;
import com.app.commonAdapter.Com_Adapter;
import com.app.commonAdapter.Com_ViewHolder;
import com.app.Fragments.MainActivity;
//import com.app.MainApplication;
import com.app.entity.View_show_dao;
import com.app.modle.HttpMethods;
import com.app.modle.ResponseResult;
import com.app.view.StepDialog;
import com.app.view.ZoomOutPageTransformer;
import com.app.entity.Find_item_dao;
import com.app.entity.Myfind_dao;
import com.app.entity.Mytraval_dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


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
    private RecyclerView myViewShow;
    private RecyclerView.Adapter myViewShowAdapter;
    private List<View_show_dao> view_show_daoList;
    private Observer<ResponseResult<List<View_show_dao>>> responseResultObserver;
    private String notInterScore = "未完成导游相互验证";
    private String completeInterScore = "系统未完成验证";
    private String failedScore = "申请当地旅游点失败";
    private String pass = "验证通过";
    private LoadingDialogUtil loadingDialogUtil;


    //发现关注=========================================
    private List<Myfind_dao> myfind_daos;
    private RecyclerView myfind_recyclerview;
    private TextView myfind_nothing;

    private List<Find_item_dao> find_item_list;

    //反馈
    private Button feedbackButton;
    private EditText feedback_editText_title;
    private EditText feedback_editText_body;

    //修改密码
    private Button change_password_button;
    private EditText change_password_username;
    private EditText change_password_old;
    private EditText change_password_new;
    private Observer<ResponseResult<String>> observer;
    private String userId;
    private Observer<ResponseResult<String>> deleteObserver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int index = -1;
        index = Integer.parseInt(intent.getStringExtra("index"));
        userId = "";
        userId = StringUtil.getValue("username");
        view_show_daoList = new ArrayList<>();

        initData();
        switch (index) {
            case 1:

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
                myViewShow = findViewById(R.id.myViewShow);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Mineitem.this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                myViewShow.setLayoutManager(linearLayoutManager);
                myViewShow.setAdapter(myViewShowAdapter);

                HttpMethods.getInstance()
                        .getViewShowByUserId(userId, responseResultObserver);
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

                        if (username.length() <= 1 || old_password.length() <= 1 || new_password.length() <= 1) {
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

                        if (title.length() <= 1 || body.length() <= 1) {
                            Toast.makeText(Mineitem.this, "问题标题或内容不能为空", Toast.LENGTH_SHORT).show();
                        } else if (title.length() > 20 || body.length() > 100) {
                            Toast.makeText(Mineitem.this, "问题标题或内容超限制", Toast.LENGTH_SHORT).show();
                        } else {
                            HttpMethods.getInstance()
                                    .feedBack(userId, title, body, observer);
                        }
                    }
                });
                break;
            case 6:
                setContentView(R.layout.main_item_6);
                //马上又跳回来
                StringUtil.putValue("isAlreadyLogin", "N");
                StringUtil.putValue("username", "");
                StringUtil.putValue("name", "");
                StringUtil.putValue("score", "0");


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

    private void initData() {
        loadingDialogUtil = new LoadingDialogUtil(Mineitem.this);
        observer = new Observer<ResponseResult<String>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<String> stringResponseResult) {
                if (stringResponseResult.getCode() == 0) {
                    Toast.makeText(Mineitem.this, "反馈失败,请稍后", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Mineitem.this, "已完成，感谢您的反馈！", Toast.LENGTH_LONG).show();
                    feedback_editText_title.setText("");
                    feedback_editText_body.setText("");
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        responseResultObserver = new Observer<ResponseResult<List<View_show_dao>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<List<View_show_dao>> listResponseResult) {
                List<View_show_dao> list = listResponseResult.getData();
                if (list != null) {
                    view_show_daoList.clear();
                    view_show_daoList.addAll(list);
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        };

        myViewShowAdapter = new Com_Adapter<View_show_dao>(Mineitem.this, R.layout.main_item_2_item, view_show_daoList) {

            @Override
            public void convert(Com_ViewHolder holder, final View_show_dao view_show_dao) {
                if (view_show_dao != null) {
                    holder.setImageResource(R.id.main_item_2_item_iamgeview, MyUrl.add_Path(view_show_dao.getDefaultpath()));
                    holder.setText(R.id.main_item_2_item_title, view_show_dao.getTitle());
                    holder.setText(R.id.mytime, view_show_dao.getMyTime());
                    holder.setText(R.id.main_item_2_item_money, "¥ " + view_show_dao.getMoney());
                    if (view_show_dao.getScore() < 60) {
                        holder.setText(R.id.main_item_2_item_state, "状态:" + notInterScore);
                    } else if (view_show_dao.getScore() >= 60 && view_show_dao.getScore() <= 100) {
                        holder.setText(R.id.main_item_2_item_state, "状态:" + completeInterScore);
                    } else if (view_show_dao.getScore() >= 120 && view_show_dao.getScore() < 140) {
                        holder.setText(R.id.main_item_2_item_state, "状态:" + failedScore);
                    } else if (view_show_dao.getScore() >= 140) {
                        holder.setText(R.id.main_item_2_item_state, "状态:" + pass);
                    }

                    holder.itemView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            AlertDialog.Builder dialog = new AlertDialog.Builder(Mineitem.this);
                            dialog.setTitle("请选择");
                            dialog.setMessage("删除后无法恢复,确定吗？");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    确定删除
                                    HttpMethods.getInstance()
                                            .deleteViewShowById(String.valueOf(view_show_dao.getId()),deleteObserver);
                                }
                            });
                            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Mineitem.this,"您取消了操作",Toast.LENGTH_SHORT).show();
                                }
                            });
                            dialog.show();
                        }
                    });
                    holder.itemView.findViewById(R.id.show).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Mineitem.this,PersonMainPage.class);
                            intent.putExtra("viewID",view_show_dao.getId());
                            startActivity(intent);
                        }
                    });
                }
            }
        };

        deleteObserver = new Observer<ResponseResult<String>>() {
            @Override
            public void onSubscribe(Disposable d) {
                loadingDialogUtil.show();
            }

            @Override
            public void onNext(ResponseResult<String> stringResponseResult) {
                if(stringResponseResult.getCode() == 1){
                    Toast.makeText(Mineitem.this,"操作成功",Toast.LENGTH_SHORT).show();
                    for (int i =0;i<view_show_daoList.size();++i){
                        if(stringResponseResult.getMessage().equals(String.valueOf(view_show_daoList.get(i).getId()))){
                            view_show_daoList.remove(i);
                            break;
                        }
                    }
                }else {
                    Toast.makeText(Mineitem.this,"操作失败",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                loadingDialogUtil.cancel();
            }

            @Override
            public void onComplete() {
                loadingDialogUtil.cancel();
                myViewShowAdapter.notifyDataSetChanged();
            }
        };
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