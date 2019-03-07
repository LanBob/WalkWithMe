package com.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.R;
import com.app.Util.MyUrl;
import com.app.Util.StringUtil;
import com.app.commonAdapter.Com_Adapter;
import com.app.commonAdapter.Com_ViewHolder;
import com.app.entity.View_show_dao;
import com.app.modle.HttpMethods;
import com.app.modle.ResponseResult;
import com.app.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ManagerNeededScoreActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView neededScore;
    private RecyclerView.Adapter neededScoreAdapter;
    private List<View_show_dao> neededScoreList;
    private CircleImageView defaultImage;
    private TextView title;
    private TextView mytime;
    private String notInterScore = "未完成导游相互验证";
    private String completeInterScore = "已完成导游相互验证";
    private Observer<ResponseResult<List<View_show_dao>>> responseResultObserver;
    private Observer<ResponseResult<String>> managerUpScoreObserVer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_needed_score);
        initView();
        initData();

        HttpMethods.getInstance()
                .managerNeededToScore(responseResultObserver);
    }

    private void initData() {
        neededScoreList = new ArrayList<>();
        neededScoreAdapter = new Com_Adapter<View_show_dao>(ManagerNeededScoreActivity.this, R.layout.manager_needed_score_item, neededScoreList) {
            @Override
            public void convert(final Com_ViewHolder holder, final View_show_dao view_show_dao) {
                holder.setImageResource(R.id.defaultImage, MyUrl.add_Path(view_show_dao.getDefaultpath()));
                holder.setText(R.id.title, view_show_dao.getTitle());
                holder.setText(R.id.mytime, view_show_dao.getMyTime());
                if (view_show_dao.getScore() < 60) {
                    holder.setText(R.id.state, notInterScore);
                } else {
                    holder.setText(R.id.state, completeInterScore);
                }
                String viewId = null;
                if (view_show_dao.getId() != null) {
                    viewId = String.valueOf(view_show_dao.getId());
                }

                final String finalViewId = viewId;

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
                                                .managerUpScore(finalViewId, score, managerUpScoreObserVer);

                                    } else {
                                        Toast.makeText(ManagerNeededScoreActivity.this, "请检查分数60-100", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(ManagerNeededScoreActivity.this, "请检查分数60-100", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ManagerNeededScoreActivity.this, PersonMainPage.class);
                        intent.putExtra("viewID", view_show_dao.getId());
                        startActivity(intent);
                    }
                });
            }
        };
        neededScore.setAdapter(neededScoreAdapter);

        responseResultObserver = new Observer<ResponseResult<List<View_show_dao>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<List<View_show_dao>> listResponseResult) {
                List<View_show_dao> list = listResponseResult.getData();
                if (list != null) {
                    neededScoreList.clear();
                    neededScoreList.addAll(list);
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Toast.makeText(ManagerNeededScoreActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                neededScoreAdapter.notifyDataSetChanged();
            }
        };

        managerUpScoreObserVer = new Observer<ResponseResult<String>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<String> stringResponseResult) {
                if (stringResponseResult != null) {
                    String viewShowId = stringResponseResult.getMessage();
                    Long viewId = null;
                    if (viewShowId != null)
                        viewId = Long.valueOf(viewShowId);
                    if (viewShowId != null) {
                        if (stringResponseResult.getCode() == 0) {
                            Toast.makeText(ManagerNeededScoreActivity.this, "评分成功,该当地向导申请失败，低于140分", Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < neededScoreList.size(); ++i) {
                                if (neededScoreList.get(i).getId().equals(viewId)) {
                                    neededScoreList.remove(viewId);
                                    break;
                                }
                            }
                        } else {
                            Toast.makeText(ManagerNeededScoreActivity.this, "该当地向导申请通过", Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < neededScoreList.size(); ++i) {
                                if (neededScoreList.get(i).getId().equals(viewId)) {
                                    neededScoreList.remove(viewId);
                                    break;
                                }
                            }
                        }
                    }
                }

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Toast.makeText(ManagerNeededScoreActivity.this, "请稍后", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                neededScoreAdapter.notifyDataSetChanged();
            }
        };
    }

    private void initView() {
        neededScore = findViewById(R.id.neededScore);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ManagerNeededScoreActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        neededScore.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onClick(View v) {

    }
}
