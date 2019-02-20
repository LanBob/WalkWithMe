package com.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
    private ImageView main_page_top_imageview;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    String home_nine_name[] = {
            "风景", "摄影", "手工", "人文", "养生", "节日", "探险", "其他"
    };
    Integer home_nine_path[] = {
            R.drawable.home_nine_scenery,
            R.drawable.home_nine_photography,
            R.drawable.home_nine_manual,
            R.drawable.home_nine_humanity,
            R.drawable.home_nine_healthcare,
            R.drawable.home_nine_festival,
            R.drawable.home_nine_explore,
            R.drawable.home_nine_else
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.type_find_view);
        Intent intent = getIntent();
        list = new ArrayList<>();
        int type = intent.getIntExtra("type",1);

        initView(type);
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
            public void convert(Com_ViewHolder holder, final Find_item_dao find_item_dao) {
                holder.setImageResource(R.id.home_mid_image,MyUrl.add_Path(find_item_dao.getDefaultpath()));
                holder.setText(R.id.home_mid_title,find_item_dao.getTitle());
                holder.setText(R.id.home_mid_money,"¥ " + find_item_dao.getMoney());
                holder.setText(R.id.home_mid_city,find_item_dao.getCity());
                holder.setText(R.id.home_mid_star,find_item_dao.getStar()+"");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Home_show_item.this,PersonMainPage.class);
                        Log.e("viewID", find_item_dao.getId() + " ");
                        intent.putExtra("viewID", find_item_dao.getId());
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void initView(int type) {
        toolbar = findViewById(R.id.main_page_toolBar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout = findViewById(R.id.main_page_collapsing);
        main_page_top_imageview = findViewById(R.id.main_page_top_imageview);
        main_page_top_imageview.setImageResource(home_nine_path[type-1]);
        collapsingToolbarLayout.setTitle(home_nine_name[type-1]);

//        switch (type){
//            case 1:
//                main_page_top_imageview.setImageResource(home_nine_path[type]);
//                break;
//            case 2:
//                break;
//            case 3:
//                break;
//            case 4:
//                break;
//            case 5:
//                break;
//            case 6:
//                break;
//            case 7:
//                break;
//            case 8:
//                break;
//        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
}
