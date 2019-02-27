package com.app.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.R;
import com.app.Util.MyUrl;
import com.app.Util.StringUtil;
import com.app.commonAdapter.Com_Adapter;
import com.app.commonAdapter.Com_ViewHolder;
import com.app.modle.HttpMethods;
import com.app.modle.ResponseResult;
import com.app.entity.Find_item_dao;
import com.app.entity.Person_dao;

import java.util.ArrayList;
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
    private List<Find_item_dao> starList;
    private List<Find_item_dao> collectionList;
    private List<Person_dao> followList;

    private RecyclerView.Adapter starAdapter;
    private RecyclerView.Adapter collectionAdapter;
    private RecyclerView.Adapter followAdapter;
    private String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_item_3);
        actionBar = getSupportActionBar();
        actionBar.setTitle("关注发现");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        userName = StringUtil.getValue("username");

        initData();

        if (!StringUtil.isMobile(userName)) {
            userName = "1";
        }
    }

    private void initData() {
        starList = new ArrayList<>();
        followList = new ArrayList<>();
        collectionList = new ArrayList<>();

        star_recyclerview = findViewById(R.id.star_recyclerview);
        collection_recyclerview = findViewById(R.id.collection_recyclerview);
        follow_recyclerview = findViewById(R.id.follow_recyclerview);

        starAdapter = new Com_Adapter<Find_item_dao>(Follow_collection_star.this, R.layout.main_item_3_star, starList) {
            @Override
            public void convert(Com_ViewHolder holder, final Find_item_dao find_item_dao) {
                if (find_item_dao != null) {
                    holder.setText(R.id.main_item_3_star_position, find_item_dao.getCity());
                    holder.setText(R.id.main_item_3_star_title, find_item_dao.getTitle());
                    holder.setText(R.id.main_item_3_star_money, "¥  " + find_item_dao.getMoney());
                    holder.setImageResource(R.id.main_item_3_star_imageView, MyUrl.add_Path(find_item_dao.getDefaultpath()));
                    Log.e("star",find_item_dao.getDefaultpath());

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Follow_collection_star.this, PersonMainPage.class);
                            if (find_item_dao.getId() != null) {
                                intent.putExtra("viewID", find_item_dao.getId());
                                startActivity(intent);
                            } else {
                                Toast.makeText(Follow_collection_star.this, "出错啦", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        };
        collectionAdapter = new Com_Adapter<Find_item_dao>(Follow_collection_star.this, R.layout.main_item_3_collection, collectionList) {
            @Override
            public void convert(Com_ViewHolder holder, final Find_item_dao find_item_dao) {
                if (find_item_dao != null) {
                    holder.setText(R.id.main_item_3_collection_position, find_item_dao.getCity());
                    holder.setText(R.id.main_item_3_collection_title, find_item_dao.getTitle());
                    holder.setText(R.id.main_item_3_collection_money, "¥  " + find_item_dao.getMoney());
                    holder.setImageResource(R.id.main_item_3_collection_imageView, MyUrl.add_Path(find_item_dao.getDefaultpath()));
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Follow_collection_star.this, PersonMainPage.class);
                            if (find_item_dao.getId() != null) {
                                intent.putExtra("viewID", find_item_dao.getId());
                                startActivity(intent);
                            } else {
                                Toast.makeText(Follow_collection_star.this, "出错啦", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        };
        followAdapter = new Com_Adapter<Person_dao>(Follow_collection_star.this, R.layout.main_item_3_follow, followList) {

            @Override
            public void convert(Com_ViewHolder holder, final Person_dao person_dao) {
                if (person_dao != null) {
                    Log.e("dao", person_dao.getIntroduce());
                    holder.setText(R.id.main_item_3_follow_userName, person_dao.getName());
                    holder.setText(R.id.main_item_3_follow_introduce, person_dao.getIntroduce());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Follow_collection_star.this,OwnMainPage.class);
                            String id = person_dao.getId().toString();
                            Log.e("userId in Follow",id);
                            intent.putExtra("userId",id);
                            startActivity(intent);
                        }
                    });
                }
            }
        };

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Follow_collection_star.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(Follow_collection_star.this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);

        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(Follow_collection_star.this);
        linearLayoutManager3.setOrientation(LinearLayoutManager.VERTICAL);

        star_recyclerview.setLayoutManager(linearLayoutManager);
        star_recyclerview.setAdapter(starAdapter);

        collection_recyclerview.setLayoutManager(linearLayoutManager1);
        collection_recyclerview.setAdapter(collectionAdapter);

        follow_recyclerview.setLayoutManager(linearLayoutManager3);
        follow_recyclerview.setAdapter(followAdapter);


        star_observer = new Observer<ResponseResult<List<Find_item_dao>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<List<Find_item_dao>> listResponseResult) {
                List<Find_item_dao> list = listResponseResult.getData();
                Log.e("star", "star" + list.size());
                if (list != null && list.size() != 0) {
                    starList.clear();
                    starList.addAll(list);
                }

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                starAdapter.notifyDataSetChanged();
            }
        };
        follow_observer = new Observer<ResponseResult<List<Person_dao>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<List<Person_dao>> listResponseResult) {
                List<Person_dao> list = listResponseResult.getData();
                Log.e("follow", "follow" + list.size());
                if (list != null && list.size() != 0) {
                    followList.clear();
                    followList.addAll(list);
                }

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                followAdapter.notifyDataSetChanged();
            }
        };
        collection_observer = new Observer<ResponseResult<List<Find_item_dao>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<List<Find_item_dao>> listResponseResult) {
                List<Find_item_dao> list = listResponseResult.getData();
                Log.e("collection", "collection" + list.size());
                if (list != null && list.size() != 0) {
                    collectionList.clear();
                    collectionList.addAll(list);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                collectionAdapter.notifyDataSetChanged();
            }
        };

        HttpMethods.getInstance()
                .getFollow(userName, follow_observer);

        HttpMethods.getInstance()
                .getStar(userName, star_observer);

        HttpMethods.getInstance()
                .getCollection(userName, collection_observer);
    }

}
