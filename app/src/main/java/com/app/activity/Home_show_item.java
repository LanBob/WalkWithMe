package com.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.R;
import com.app.Util.MyUrl;
import com.app.commonAdapter.Com_Adapter;
import com.app.commonAdapter.Com_ViewHolder;
import com.app.modle.HttpMethods;
import com.app.modle.ResponseResult;
import com.app.entity.Find_item_dao;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class Home_show_item extends AppCompatActivity implements View.OnClickListener{

    private List<Find_item_dao> list;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_show_item);
        Intent intent = getIntent();
        list = new ArrayList<>();
        int type = intent.getIntExtra("type",1);
        Log.e("tupe",type + "");
        //==============================================可以在这里每次获取数据
        Observer<ResponseResult<List<Find_item_dao>>> observer = new Observer<ResponseResult<List<Find_item_dao>>>() {
            int len = 0;
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(ResponseResult<List<Find_item_dao>> listResponseResult) {
                list.clear();
                list.addAll(listResponseResult.getData());
                Log.e("list",list.size() + " ");
                if(list.size() > 0)
                Log.e("content",list.get(0).getTitle());

            }

            @Override
            public void onError(Throwable e) {
                Log.e("error", e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e("com", "complete");
                //可能会犯错，因为list= re，getList（）得到的不是同一个list对象
                adapter.notifyDataSetChanged();
            }
        };

        HttpMethods.getInstance()
                .getFind_item(type,observer);


        findViewById(R.id.home_show_item_introduce).setOnClickListener(this);
        findViewById(R.id.home_show_item_cardview).setOnClickListener(this);
        recyclerView = findViewById(R.id.home_show_item_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Home_show_item.this,LinearLayout.VERTICAL,false)
        {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };


        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new Com_Adapter<Find_item_dao>(Home_show_item.this,R.layout.home_mid_item,list){

            @Override
            public void convert(Com_ViewHolder holder, Find_item_dao find_item_dao) {
                holder.setImageResource(R.id.home_mid_image,MyUrl.add_Path(find_item_dao.getDefaultpath()));
                holder.setText(R.id.home_mid_name,find_item_dao.getTitle());
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_show_item_introduce:
                Toast.makeText(Home_show_item.this,"onclick introduce",Toast.LENGTH_SHORT).show();
                break;
            case R.id.home_show_item_cardview:
                Toast.makeText(Home_show_item.this,"onclick card",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
