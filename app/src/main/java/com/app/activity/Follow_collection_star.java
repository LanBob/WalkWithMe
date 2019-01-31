package com.app.activity;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.app.R;
import com.app.modle.HttpMethods;
import com.app.modle.ResponseResult;
import com.app.entity.Find_item_dao;
import com.app.entity.Person_dao;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class Follow_collection_star extends AppCompatActivity {

    private ActionBar actionBar;
    private Observer<ResponseResult<List<Find_item_dao>>> star_observer;
    private Observer<ResponseResult<List<Find_item_dao>>> collection_observer;
    private Observer<ResponseResult<List<Person_dao>>> follow_observer;
    private RecyclerView star_recyclerview;
    private RecyclerView collection_recyclerview;
    private RecyclerView follow_recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_item_3);
        actionBar = getSupportActionBar();
        actionBar.setTitle("关注发现");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        initData();
        star_recyclerview = findViewById(R.id.star_recyclerview);
        collection_recyclerview = findViewById(R.id.collection_recyclerview);
        follow_recyclerview = findViewById(R.id.follow_recyclerview);

        HttpMethods.getInstance()
                .getFollow("13424158682",follow_observer);
        HttpMethods.getInstance()
                .getStar("11",star_observer);
        HttpMethods.getInstance()
                .getCollection("116",collection_observer);

    }

    private void initData() {
        star_observer = new Observer<ResponseResult<List<Find_item_dao>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<List<Find_item_dao>> listResponseResult) {
                List list = listResponseResult.getData();
                Log.e("star","star" + list.get(0));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        follow_observer = new Observer<ResponseResult<List<Person_dao>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<List<Person_dao>> listResponseResult) {
                List list = listResponseResult.getData();
                Log.e("follow","follow" + list.get(0));

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        collection_observer = new Observer<ResponseResult<List<Find_item_dao>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<List<Find_item_dao>> listResponseResult) {
                List list = listResponseResult.getData();
                Log.e("collection","collection" + list.get(0));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

}
