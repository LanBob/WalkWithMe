package com.app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.app.Fragments.HomeFragment;
import com.app.JMS.util.LogUtil;
import com.app.R;
import com.app.Util.MyUrl;
import com.app.commonAdapter.Com_Adapter;
import com.app.commonAdapter.Com_ViewHolder;
import com.app.entity.Find_item_dao;
import com.app.entity.View_show_dao;
import com.app.modle.HttpMethods;
import com.app.modle.ResponseResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class SearchActivity extends AppCompatActivity{

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.searchScoll)
    public ScrollView scrollView;

    @BindView(R.id.searchRefresh)
    public SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.search_result_view)
    public RecyclerView recyclerView;

    private TransitionSet mSet;

    private RecyclerView.Adapter adapter;

    private List<View_show_dao> view_show_daoList;

    boolean isExpand = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchactivity);

        String searchText = getIntent().getStringExtra("search");
        LogUtil.d("searchText" + searchText);

        ButterKnife.bind(this);
        initData(searchText);
        initView();
    }

    private void initData(String searchText) {
        Observer<ResponseResult<List<View_show_dao>>> observer = new Observer<ResponseResult<List<View_show_dao>>>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(ResponseResult<List<View_show_dao>> listResponseResult) {
                view_show_daoList.clear();
                view_show_daoList.addAll(listResponseResult.getData());
                if (view_show_daoList.size() > 0)
                    Log.e("content", view_show_daoList.get(0).getTitle());
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
                .searchByKeyWord(searchText,observer);
    }

    private void initView() {
        toolbar.inflateMenu(R.menu.mytolbar_menu);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        view_show_daoList = new ArrayList<>();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

            adapter = new Com_Adapter<View_show_dao>(this, R.layout.search_item, view_show_daoList) {
            @Override
            public void convert(Com_ViewHolder holder, final View_show_dao view_show_dao) {
                if(view_show_dao != null){
                    holder.setText(R.id.search_item_position_text,view_show_dao.getCity());
                    holder.setText(R.id.search_item_star,view_show_dao.getStar() +"");
                    holder.setText(R.id.search_item_money,"¥ " + view_show_dao.getMoney());
                    holder.setImageResource(R.id.search_item_image, MyUrl.add_Path(view_show_dao.getDefaultpath()));
                    holder.setText(R.id.search_item_text,view_show_dao.getTitle());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SearchActivity.this, PersonMainPage.class);
                            intent.putExtra("viewID", view_show_dao.getId());
                            startActivity(intent);
                        }
                    });

                }
            }
        };
        recyclerView.setAdapter(adapter);


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_search:
                        //搜索框显示
                        break;
                }
                return false;
            }
        });
    }

}
