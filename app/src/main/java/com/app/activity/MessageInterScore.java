package com.app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.R;
import com.app.Util.MyUrl;
import com.app.Util.StringUtil;
import com.app.commonAdapter.Com_Adapter;
import com.app.commonAdapter.Com_ViewHolder;
import com.app.entity.Person;
import com.app.entity.View_show_dao;
import com.app.modle.HttpMethods;
import com.app.modle.ResponseResult;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MessageInterScore extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView neededScore;
    private RecyclerView.Adapter adapter;
    private List<View_show_dao> list;
    private Observer<ResponseResult<View_show_dao>> getViewShowDaoObserver;
    private Observer<ResponseResult<String>> upScoreObserver;

    private String notInterScore = "未完成导游相互验证";
    private String completeInterScore = "系统未完成验证";
    private String failedScore = "申请当地旅游点失败";
    private String pass = "验证通过";
    private ActionBar actionBar;

    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_inter_score);
        actionBar = getSupportActionBar();
        actionBar.setTitle("相互评分");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        userId = StringUtil.getValue("username");
        initView();
        initData();
        HttpMethods.getInstance()
                .messageGetViewShowToBeScore(userId,getViewShowDaoObserver);
    }

    private void initData() {
        list = new ArrayList<>();
        adapter = new Com_Adapter<View_show_dao>(MessageInterScore.this, R.layout.message_inter_score_item, list) {
            @Override
            public void convert(final Com_ViewHolder holder, final View_show_dao view_show_dao) {
                if (view_show_dao != null) {
                    holder.setImageResource(R.id.defaultImage, MyUrl.add_Path(view_show_dao.getDefaultpath()));
                    holder.setText(R.id.title, view_show_dao.getTitle());
                    holder.setText(R.id.mytime, view_show_dao.getMyTime());
                    if (view_show_dao.getScore() < 60) {
                        holder.setText(R.id.state, "状态:" + notInterScore);
                    } else if (view_show_dao.getScore() >= 60 && view_show_dao.getScore() <= 100) {
                        holder.setText(R.id.state, "状态:" + completeInterScore);
                    } else if (view_show_dao.getScore() >= 120 && view_show_dao.getScore() < 140) {
                        holder.setText(R.id.state, "状态:" + failedScore);
                    } else if (view_show_dao.getScore() >= 140) {
                        holder.setText(R.id.state, "状态:" + pass);
                    }
                    holder.itemView.findViewById(R.id.scoreButtom).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText editText = holder.itemView.findViewById(R.id.score);
                            String sco = editText.getText().toString();
                            if (StringUtil.isInteger(sco)) {
                                final int score = Integer.valueOf(sco);
                                AlertDialog.Builder dialog = new AlertDialog.Builder(MessageInterScore.this);
                                dialog.setTitle("请选择");
                                dialog.setMessage("确定您打的分数为 " + score + " 分?");
                                dialog.setCancelable(false);
                                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                    确定，提交评分
                                        Long id = view_show_dao.getId();
                                        String viewShowId = String.valueOf(id);
                                        HttpMethods.getInstance()
                                                .messageUpScore(userId,viewShowId,score,upScoreObserver);
                                    }
                                });
                                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(MessageInterScore.this, "您取消了操作", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dialog.show();
                            }
                        }
                    });
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MessageInterScore.this,PersonMainPage.class);
                            intent.putExtra("viewID",view_show_dao.getId());
                            startActivity(intent);
                        }
                    });
                }
            }
        };

        neededScore.setAdapter(adapter);

        getViewShowDaoObserver = new Observer<ResponseResult<View_show_dao>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<View_show_dao> view_show_daoResponseResult) {
                if (view_show_daoResponseResult.getCode() == 1) {
                    list.clear();
                    list.add(view_show_daoResponseResult.getData());
                }else {
                    Log.e("code",""+view_show_daoResponseResult.getCode());
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                adapter.notifyDataSetChanged();
            }
        };

        upScoreObserver = new Observer<ResponseResult<String>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<String> stringResponseResult) {
                if(stringResponseResult.getCode() == 1){
                    list.clear();
                    Toast.makeText(MessageInterScore.this,"尝试评分成功",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                adapter.notifyDataSetChanged();
            }
        };

    }

    private void initView() {
        neededScore = findViewById(R.id.neededScore);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MessageInterScore.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        neededScore.setLayoutManager(linearLayoutManager);
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
        switch (v.getId()) {

        }
    }
}
