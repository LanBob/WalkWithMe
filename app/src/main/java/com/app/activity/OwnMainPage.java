package com.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.app.R;
import com.app.Util.MyUrl;
import com.app.Util.StringUtil;
import com.app.commonAdapter.Com_Adapter;
import com.app.commonAdapter.Com_ViewHolder;
import com.app.entity.Find_item_dao;
import com.app.entity.HeadImage;
import com.app.entity.Person_setting;
import com.app.modle.HttpMethods;
import com.app.modle.ResponseResult;
import com.app.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class OwnMainPage extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView.Adapter adapter;
    private List<Find_item_dao> own_main_page_list;
    private RecyclerView gaideRecyclerView;
    private TextView own_main_page_city;
    private TextView own_main_page_introduce;
    private TextView own_main_page_follow;
    private TextView own_main_page_star;
    private CircleImageView own_main_page_headImage;
    private String userId;
    private Observer<ResponseResult<Person_setting>> person_settingObserver;
    Observer<ResponseResult<List<Find_item_dao>>> observer;
    Observer<ResponseResult<HeadImage>> responseResultObserver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.own_main_page);
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        Log.e("userId", userId + "sss");
        initView();
        initData();
        if (StringUtil.isInteger(userId)) {
            HttpMethods.getInstance()
                    .getViewShowDaoByUserId(userId, observer);
            HttpMethods.getInstance()
                    .getPerson(Long.valueOf(userId), person_settingObserver);
            HttpMethods.getInstance()
                    .getHeadImage(userId,responseResultObserver);
        }


    }

    private void initData() {

        observer = new Observer<ResponseResult<List<Find_item_dao>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<List<Find_item_dao>> listResponseResult) {
                own_main_page_list.clear();
                own_main_page_list.addAll(listResponseResult.getData());
                Log.e("size", own_main_page_list.size() + " ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e("error", e.getMessage());
            }

            @Override
            public void onComplete() {
                adapter.notifyDataSetChanged();
            }
        };
        person_settingObserver = new Observer<ResponseResult<Person_setting>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<Person_setting> responseResult) {
                if(responseResult.getCode() == 1){
                    Person_setting person_setting = responseResult.getData();
                    own_main_page_city.setText(person_setting.getCity());
                    own_main_page_introduce.setText(person_setting.getIntroduce());
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
//        userId = "13724158682";
        responseResultObserver = new Observer<ResponseResult<HeadImage>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<HeadImage> headImageResponseResult) {
                HeadImage headImage  = headImageResponseResult.getData();
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.gray_bg)
                        .error(R.drawable.chat_girl)
                        .priority(Priority.HIGH)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

                Glide.with(OwnMainPage.this)
                        .load(MyUrl.add_Path(headImage.getHead_image()))
                        // .listener(mRequestListener)
                        .apply(options)
                        .into(own_main_page_headImage);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

    }

    private void initView() {
        own_main_page_list = new ArrayList<>();
        gaideRecyclerView = findViewById(R.id.gaide);
        own_main_page_city = findViewById(R.id.own_main_page_city);
        own_main_page_introduce = findViewById(R.id.own_main_page_introduce);
        own_main_page_headImage = findViewById(R.id.own_main_page_headImage);
        own_main_page_follow = findViewById(R.id.own_main_page_follow);
        own_main_page_star = findViewById(R.id.own_main_page_star);

        adapter = new Com_Adapter<Find_item_dao>(OwnMainPage.this, R.layout.own_main_page_item, own_main_page_list) {

            @Override
            public void convert(Com_ViewHolder holder, final Find_item_dao find_item_dao) {
                holder.setText(R.id.own_main_page_title, find_item_dao.getTitle());
                String url = produce(find_item_dao.getDefaultpath());
                Log.e("path", "" + url);
                holder.setImageResource(R.id.own_main_page_default_image, url);
                holder.setText(R.id.own_main_page_money, "¥ " + find_item_dao.getMoney());
                holder.setText(R.id.own_main_page_colloection, find_item_dao.getStar() + "");
                Log.e("city", find_item_dao.getCity() + "");

                holder.setText(R.id.own_main_page_city, find_item_dao.getCity());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OwnMainPage.this, PersonMainPage.class);
                        intent.putExtra("viewID", find_item_dao.getId());
                        startActivity(intent);
                    }
                });

            }
        };

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OwnMainPage.this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gaideRecyclerView.setLayoutManager(linearLayoutManager);

        gaideRecyclerView.setAdapter(adapter);
    }

    private String produce(String defaultpath) {
        return MyUrl.add_Path(defaultpath);
    }

    @Override
    public void onClick(View v) {

    }
}