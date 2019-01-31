package com.app.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.app.R;
import com.app.Util.MyUrl;
import com.app.activity.Home_show_item;
import com.app.commonAdapter.Com_Adapter;
import com.app.commonAdapter.Com_ViewHolder;
import com.app.modle.HttpMethods;
import com.app.modle.ResponseResult;
import com.app.view.Home_Banner_LoopViewPager;
import com.app.entity.Find_item_dao;
import com.app.entity.Home_everyDao;
import com.app.entity.Home_mid_dao;
import com.app.entity.Home_nine_dao;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by donglinghao on 2016-01-28.
 * PagerAdapter用法简介
 * 首先，如果继承pageradapter，至少必须重写下面的四个方法：
 * <p>
 * 1. instantiateItem(ViewGroup, int)
 * 2. destroyItem(ViewGroup, int, Object)
 * 3. getCount()
 * 4. isViewFromObject(View, Object)
 */
public class HomeFragment extends Fragment {

    private View mRootView;
    //=========home_nine_recyclerViee=============================================

    private RecyclerView home_nine_recyclerview;

    Integer[] home_nine_recyclerview_image = {
            R.mipmap.first_light, R.mipmap.second_light,
            R.mipmap.third_light, R.mipmap.fourth_light,
            R.mipmap.first_light, R.mipmap.second_light,
            R.mipmap.third_light, R.mipmap.fourth_light,
    };

    Integer home_nine_path[] ={
            R.drawable.home_nine_scenery,
            R.drawable.home_nine_photography,
            R.drawable.home_nine_manual,
            R.drawable.home_nine_humanity,
            R.drawable.home_nine_healthcare,
            R.drawable.home_nine_festival,
            R.drawable.home_nine_explore,
            R.drawable.home_nine_else
    };
    String home_nine_name[]={
            "风景","摄影","手工","人文","养生","节日","探险","其他"
    };

    private Home_nine_dao home_nine_dao;
    private List<Home_nine_dao> home_nine_list_dao;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ScrollView scrollView;

    //=========home_nine_recyclerViee=============================================

    //=========home_mid_recyclerViee=============================================
    private RecyclerView home_mid_recyclerView;
    String[] home_mid_string = {"现代化风格建筑群，优美景色等你来", "悠悠的小船，汗水夹杂着快乐，那一抹暖阳最美！", "爬山，假期是最好的运动，山上有你想要的一切", "云南最好的景色当给你不一样的感受，大千世界，只为一景", "带你转悠带你飞"};

    Integer[] hmoe_mid_image = {
      R.drawable.home_1,R.drawable.home_3,R.drawable.home_4,
            R.drawable.home_5,R.drawable.home_6
    };
    private List<Home_mid_dao> home_mid_list;
    private Home_mid_dao hmoe_mid_dao;

    private SearchView searchView;
    private RecyclerView.Adapter adapter;
    //==============================================home_mid_recycler
    private List<Find_item_dao> find_item_daoList;

    //==============================================


    //=========home_mid_recyclerViee=============================================

    //=========home_everyday=============================================
    private RecyclerView home_everyday_recyclerView;
    private Home_everyDao home_everyDao;
    private List<Home_everyDao> home_every_list;
    private String[] home_every_string = {"To learn how to play", "Learn 50 fresh words", "Learn the poem by heart"};
    private Integer[] home_every_image = {R.mipmap.first_light, R.mipmap.second_light, R.mipmap.third_light};

    //=========home_everyday=============================================


    //============Banner
    List<Integer> banner_image = new ArrayList<>();
    private Home_Banner_LoopViewPager Home_Banner_Loop_viewPager;
    private int Banner_itemPosition = 0;
    //轮播时间间隔
    private static final int TIME = 1700;
    private Handler mHandler = new Handler();
    Runnable runnableForViewPager = new Runnable() {
        @Override
        public void run() {
            try {
                Banner_itemPosition++;
                Home_Banner_Loop_viewPager.setCurrentItem(Banner_itemPosition % banner_image.size());
                mHandler.postDelayed(this, TIME);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    //============Banner

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.home_fragment, container, false);
            initView();
            initData();
            getData();

            //=========home_mid_recyclerViee=============================================
            home_mid_recyclerView =  mRootView.findViewById(R.id.home_mid_recyclerView);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            home_mid_recyclerView.setLayoutManager(linearLayoutManager);
            adapter = new Com_Adapter<Find_item_dao>(getContext(), R.layout.home_mid_item, find_item_daoList) {
                @Override
                public void convert(Com_ViewHolder holder, final Find_item_dao find_item_dao) {

                    holder.setText(R.id.home_mid_name, find_item_dao.getTitle());
                    holder.setText(R.id.type,find_item_dao.getType() + "人文");
                    holder.setText(R.id.money,"¥ : " + find_item_dao.getMoney() + " 元起");
                    holder.setImageResource(R.id.home_mid_image, MyUrl.add_Path(find_item_dao.getDefaultpath()));

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), " " + find_item_dao.getTitle(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            };

            home_mid_recyclerView.setAdapter(adapter);
            //=========home_mid_recyclerViee=============================================

            //=========home_every_recyclerViee=============================================
            home_everyday_recyclerView.setAdapter(new Com_Adapter<Home_everyDao>(getContext(), R.layout.everyday_item, home_every_list) {
                @Override
                public void convert(Com_ViewHolder holder, final Home_everyDao home_everyDao) {
                    holder.setText(R.id.home_every_text, home_everyDao.getIntroduce());
                    holder.setText(R.id.home_every_daytime, home_everyDao.getTime());
                    holder.setCircleImageView(R.id.home_every_image, home_everyDao.getImage());

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), " " + home_everyDao.getIntroduce(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    switch (home_everyDao.getId()) {
                        case 0:
                            holder.itemView.setBackgroundColor(Color.parseColor("#00C39A"));
                            break;
                        case 1:
                            holder.itemView.setBackgroundColor(Color.parseColor("#76BEE6"));
                            break;
                        default:
                            holder.itemView.setBackgroundColor(Color.parseColor("#657DC1"));
                            break;
                    }
                }
            });
            //=========home_every_recyclerViee=============================================

            //=========home_nine_recyclerViee=============================================
            home_nine_recyclerview = mRootView.findViewById(R.id.home_nine_recyclerview);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
            home_nine_recyclerview.setLayoutManager(gridLayoutManager);
            home_nine_recyclerview.setAdapter(new Com_Adapter<Home_nine_dao>(getContext(), R.layout.home_nine_item, home_nine_list_dao) {
                @Override
                public void convert(Com_ViewHolder holder, final Home_nine_dao home_nine_dao) {

                    holder.setText(R.id.home_nine_item_text, home_nine_dao.getIntroduce());

                    //设置图片
                    holder.setImageResource(R.id.home_nine_item_image, home_nine_dao.getHome_nine_image());

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), " " + home_nine_dao.getIntroduce(), Toast.LENGTH_SHORT).show();
                            //跳转到相关的页面去
                            Intent intent = new Intent(getActivity(), Home_show_item.class);
                            intent.putExtra("type",home_nine_dao.getType());
                            getActivity().startActivity(intent);
                        }
                    });
                }
            });
            //=========home_nine_recyclerViee=============================================
        }

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }

        return mRootView;
    }

    private void getData() {
        Observer<ResponseResult<List<Find_item_dao>>> observer = new Observer<ResponseResult<List<Find_item_dao>>>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(ResponseResult<List<Find_item_dao>> listResponseResult) {
                find_item_daoList.clear();
                find_item_daoList.addAll(listResponseResult.getData());
                Log.e("list",find_item_daoList.size() + " ");
                if(find_item_daoList.size() > 0)
                    Log.e("content",find_item_daoList.get(0).getTitle());
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
                .getFind_item(1,observer);
        HttpMethods.getInstance()
                .getFind_item(3,observer);
    }

    private void initData() {
        //---------------------------------
        find_item_daoList = new ArrayList<>();
        //---------------------------------

        //=========home_mid_recyclerViee========中间横向的拉动，设置数据，数据来源=====================================
        home_mid_list = new ArrayList<>();
        for (int i = 0; i < home_mid_string.length; ++i) {
            hmoe_mid_dao = new Home_mid_dao();
            hmoe_mid_dao.setName(home_mid_string[i]);
            hmoe_mid_dao.setImage(hmoe_mid_image[i]);
            home_mid_list.add(hmoe_mid_dao);
        }
        //=========================================以上从网络上获取


        //========================================================home_nine_list_dao
        home_nine_list_dao = new ArrayList<>();
        for (int i = 0; i < home_nine_recyclerview_image.length; ++i) {
            home_nine_dao = new Home_nine_dao();
            home_nine_dao.setHome_nine_image(home_nine_path[i]);
            home_nine_dao.setIntroduce(home_nine_name[i]);
            home_nine_dao.setType(i +1);
            home_nine_list_dao.add(home_nine_dao);
        }


        //=========home_every_recyclerViee=============================================
        home_every_list = new ArrayList<Home_everyDao>();
        for (int i = 0; i < home_every_image.length; ++i) {
            home_everyDao = new Home_everyDao();
            home_everyDao.setTime("today");
            home_everyDao.setImage(home_every_image[i]);
            home_everyDao.setIntroduce(home_every_string[i]);
            home_everyDao.setId(i);
            home_every_list.add(home_everyDao);
        }

        home_everyday_recyclerView =  mRootView.findViewById(R.id.home_everyday_recyclerview);
        LinearLayoutManager home_every_lin = new LinearLayoutManager(getContext());
        home_every_lin.setOrientation(LinearLayoutManager.HORIZONTAL);
        home_everyday_recyclerView.setLayoutManager(home_every_lin);

    }

    private void initView() {
        //========================================顶部搜索框
        searchView =  mRootView.findViewById(R.id.searchview);
        swipeRefreshLayout =  mRootView.findViewById(R.id.refresh);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.DKGRAY);//设置旋转圈的颜色
        //下拉监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        if (scrollView != null) {
            scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setEnabled(scrollView.getScrollY() == 0);
                    }
                }
            });
        }
        //========================================
        //==========================================================设置searchView
        searchView.findViewById(R.id.searchview);
        SearchView.SearchAutoComplete searchTextArea = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchTextArea.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

        View viewById = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        if (viewById != null) {
            viewById.setBackgroundColor(Color.TRANSPARENT);
        }
        android.widget.LinearLayout.LayoutParams layoutParams = (android.widget.LinearLayout.LayoutParams) searchTextArea.getLayoutParams();
        layoutParams.bottomMargin = -6;
        searchTextArea.setLayoutParams(layoutParams);
        //==========================================================设置searchView



        //============Banner
        banner_image.add(R.mipmap.first_light);
        banner_image.add(R.mipmap.second_light);
        banner_image.add(R.mipmap.third_light);
        banner_image.add(R.mipmap.fourth_light);
        Home_Banner_Loop_viewPager =  mRootView.findViewById(R.id.loopviewPager);
        Home_Banner_Loop_viewPager.setAdapter(new SamplePagerAdapter());
        mHandler.postDelayed(runnableForViewPager, TIME);
        //============Banner
    }


    //============Banner=============================================================================
    public class SamplePagerAdapter extends PagerAdapter {
        private int mSize;

        //设置数量的多少
        public SamplePagerAdapter() {
        }

        public SamplePagerAdapter(int count) {
            mSize = count;
        }

        // 获取要滑动的控件的数量，在这里我们以滑动的广告栏为例，那么这里就应该是展示的广告图片的ImageView数量
        @Override
        public int getCount() {
            mSize = banner_image.size();
            return mSize;
        }

        // 来判断显示的是否是同一张图片，这里我们将两个参数相比较返回即可
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        // PagerAdapter只缓存三张要显示的图片，如果滑动的图片超出了缓存的范围，就会调用这个方法，将图片销毁
        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
            view.removeView((View) object);
        }


        // 当要显示的图片可以进行缓存的时候，会调用这个方法进行显示图片的初始化，我们将要显示的ImageView加入到ViewGroup中，然后作为返回值返回即可
        @Override
        public Object instantiateItem(ViewGroup view, final int position) {

            final ImageView imageView = new ImageView(view.getContext());

            imageView.setImageResource(banner_image.get(position));

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), " " + position, Toast.LENGTH_SHORT).show();
                }
            });

            view.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return imageView;
        }

        // 增加item
        public void addItem() {
            mSize++;
            notifyDataSetChanged();
        }

        // 删除item
        public void removeItem() {
            mSize--;
            mSize = mSize < 0 ? 0 : mSize;

            notifyDataSetChanged();
        }
    }
    //============Banner=============================================================================

}

