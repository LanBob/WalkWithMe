package com.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.R;
import com.app.Util.LoadingDialogUtil;
import com.app.Util.StringUtil;
import com.app.commonAdapter.Com_Adapter;
import com.app.commonAdapter.Com_ViewHolder;
import com.app.entity.IsGoodMan;
import com.app.modle.HttpMethods;
import com.app.modle.ResponseResult;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ManagerGoodManActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView notScore;
    private RecyclerView alreadyScore;
    private RecyclerView.Adapter notScoreAdapter;
    private RecyclerView.Adapter alreadyAdapter;
    private List<IsGoodMan> notScoreGoodManList;
    private List<IsGoodMan> alreadyGoodManList;
    private Observer<ResponseResult<String>> upScore;
    private Observer<ResponseResult<List<IsGoodMan>>> notScoreResultList;
    private Observer<ResponseResult<List<IsGoodMan>>> alreadyScoreResultList;
    private LoadingDialogUtil loadingDialogUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_is_goodman);
        initView();
        intiData();
        HttpMethods.getInstance()
                .getIsGoodMan("0", notScoreResultList);
        HttpMethods.getInstance()
                .getIsGoodMan("1", alreadyScoreResultList);
    }

    private void intiData() {
        notScoreGoodManList = new ArrayList<>();
        alreadyGoodManList = new ArrayList<>();

        notScoreAdapter = new Com_Adapter<IsGoodMan>(ManagerGoodManActivity.this, R.layout.manager_is_goodman_item, notScoreGoodManList) {
            @Override
            public void convert(final Com_ViewHolder holder, final IsGoodMan isGoodMan) {
                if (isGoodMan != null) {
                    holder.setText(R.id.userName, isGoodMan.getUserName());
                    holder.setText(R.id.age, "年龄 : " + isGoodMan.getAge());
                    holder.setText(R.id.sex, "性别：" + isGoodMan.getSex());
                    holder.itemView.findViewById(R.id.scoreButtom)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    EditText editText = holder.itemView.findViewById(R.id.score);
                                    String scor = editText.getText().toString();
                                    int score = 0;
                                    if (StringUtil.isInteger(scor)) {
                                        score = Integer.parseInt(scor);
                                        if (score >= 60 && score <= 100) {
                                            HttpMethods.getInstance()
                                                    .managerUploadScore(isGoodMan.getUserId(), score, upScore);
                                        } else {
                                            Toast.makeText(ManagerGoodManActivity.this, "请检查分数60-100", Toast.LENGTH_SHORT).show();
                                        }
                                    }else {
                                        Toast.makeText(ManagerGoodManActivity.this, "请检查分数60-100", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        };

        alreadyAdapter = new Com_Adapter<IsGoodMan>(ManagerGoodManActivity.this, R.layout.manager_is_goodman_item, alreadyGoodManList) {
            @Override
            public void convert(Com_ViewHolder holder, IsGoodMan isGoodMan) {
                holder.setText(R.id.userName, isGoodMan.getUserName());
                holder.setText(R.id.age, "年龄 : " + isGoodMan.getAge());
                holder.setText(R.id.sex, "性别：" + isGoodMan.getSex());
                holder.itemView.findViewById(R.id.text).setVisibility(View.GONE);
                holder.itemView.findViewById(R.id.score).setVisibility(View.GONE);
                holder.itemView.findViewById(R.id.scoreButtom).setVisibility(View.GONE);
            }
        };

        notScore.setAdapter(notScoreAdapter);
        alreadyScore.setAdapter(alreadyAdapter);

//        上传分数,返回UserId
        upScore = new Observer<ResponseResult<String>>() {
            @Override
            public void onSubscribe(Disposable d) {
                loadingDialogUtil.show();
            }

            @Override
            public void onNext(ResponseResult<String> stringResponseResult) {
                if (stringResponseResult.getCode() == 1) {
                    loadingDialogUtil.cancel();
                    Toast.makeText(ManagerGoodManActivity.this, "评分成功", Toast.LENGTH_SHORT).show();
                    String userId = stringResponseResult.getData();
                    for (int i = 0;i<notScoreGoodManList.size();++i){
                        IsGoodMan isGoodMan = notScoreGoodManList.get(i);
                        if(isGoodMan.getUserId().equals(userId)){
                            notScoreGoodManList.remove(isGoodMan);
                            alreadyGoodManList.add(isGoodMan);
                            break;
                        }
                    }
                } else {
                    loadingDialogUtil.cancel();
                    Toast.makeText(ManagerGoodManActivity.this, "评分失败，请稍后", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (loadingDialogUtil.isShowing())
                    loadingDialogUtil.cancel();
            }

            @Override
            public void onComplete() {
                if (loadingDialogUtil.isShowing())
                    loadingDialogUtil.cancel();
                notScoreAdapter.notifyDataSetChanged();
                alreadyAdapter.notifyDataSetChanged();
            }
        };
//        没有评分的项目
        notScoreResultList = new Observer<ResponseResult<List<IsGoodMan>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<List<IsGoodMan>> listResponseResult) {
                if (listResponseResult.getCode() == 1) {
                    notScoreGoodManList.clear();
                    notScoreGoodManList.addAll(listResponseResult.getData());
                } else {
                    Toast.makeText(ManagerGoodManActivity.this, "查询失败，请稍后", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(ManagerGoodManActivity.this, "查询失败，请稍后", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                notScoreAdapter.notifyDataSetChanged();
            }
        };

        alreadyScoreResultList = new Observer<ResponseResult<List<IsGoodMan>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<List<IsGoodMan>> listResponseResult) {
                if (listResponseResult.getCode() == 1) {
                    alreadyGoodManList.clear();
                    alreadyGoodManList.addAll(listResponseResult.getData());
                } else {
                    Toast.makeText(ManagerGoodManActivity.this, "查询失败，请稍后", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(ManagerGoodManActivity.this, "查询失败，请稍后", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                alreadyAdapter.notifyDataSetChanged();
            }
        };
    }

    private void initView() {
        notScore = findViewById(R.id.notScore);
        alreadyScore = findViewById(R.id.alreadyScore);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ManagerGoodManActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        notScore.setLayoutManager(linearLayoutManager);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(ManagerGoodManActivity.this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        alreadyScore.setLayoutManager(linearLayoutManager1);

        loadingDialogUtil = new LoadingDialogUtil(ManagerGoodManActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
