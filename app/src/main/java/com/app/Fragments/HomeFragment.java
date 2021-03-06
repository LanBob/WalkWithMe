package com.app.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import com.app.JMS.util.LogUtil;
import com.app.R;
import com.app.Util.MyUrl;
import com.app.activity.Home_show_item;
import com.app.activity.PersonMainPage;
import com.app.activity.SearchActivity;
import com.app.commonAdapter.Com_Adapter;
import com.app.commonAdapter.Com_ViewHolder;
import com.app.modle.HttpMethods;
import com.app.modle.ResponseResult;
import com.app.entity.Find_item_dao;
import com.app.entity.Home_mid_dao;
import com.app.entity.Home_nine_dao;
import com.app.view.Roll.ColorPointHintView;
import com.app.view.Roll.LoopPagerAdapter;
import com.app.view.Roll.RollPagerView;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Lusr on 2016-01-28.
 * PagerAdapter用法简介
 * 首先，如果继承pageradapter，至少必须重写下面的四个方法：
 * <p>
 * 1. instantiateItem(ViewGroup, int)
 * 2. destroyItem(ViewGroup, int, Object)
 * 3. getCount()
 * 4. isViewFromObject(View, Object)
 */
public class HomeFragment extends Fragment {

    //=====================================
    public RollPagerView mViewPager;
    EditText tvSearch;
    LinearLayout mSearchLayout;
    //    ScrollView mScrollView;
    boolean isExpand = false;
    Toolbar toolbar;
    private TransitionSet mSet;
    //=====================================
    private View mRootView;
    //=========home_nine_recyclerViee=============================================
    private ImageView imageView;

    private RecyclerView home_nine_recyclerview;
    private static final String TAG = "HomeFragment";

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
    String home_nine_name[] = {
            "风景", "摄影", "手工", "人文", "养生", "节日", "探险", "其他"
    };

    private Home_nine_dao home_nine_dao;
    private List<Home_nine_dao> home_nine_list_dao;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ScrollView scrollView;
//    private Button button;

    //=========home_nine_recyclerViee=============================================

    //=========home_mid_recyclerViee=============================================
    private RecyclerView home_mid_recyclerView;
//    String[] home_mid_string = {"现代化风格建筑群，优美景色等你来", "悠悠的小船，汗水夹杂着快乐，那一抹暖阳最美！", "爬山，假期是最好的运动，山上有你想要的一切", "云南最好的景色当给你不一样的感受，大千世界，只为一景", "带你转悠带你飞"};

    Integer[] hmoe_mid_image = {
            R.drawable.home_1, R.drawable.home_3, R.drawable.home_4,
            R.drawable.home_5, R.drawable.home_6
    };
    private List<Home_mid_dao> home_mid_list;
    private Home_mid_dao hmoe_mid_dao;

    private RecyclerView.Adapter adapter;
    //==============================================home_mid_recycler
    private List<Find_item_dao> find_item_daoList;

    //==============================================


    //=========home_mid_recyclerViee=============================================

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.home_fragment, container, false);

            initView(container);
            initData();
            getData();

            //=========home_mid_recyclerViee=============================================
            home_mid_recyclerView = mRootView.findViewById(R.id.home_mid_recyclerView);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext()){
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            home_mid_recyclerView.setLayoutManager(linearLayoutManager);
            adapter = new Com_Adapter<Find_item_dao>(getContext(), R.layout.home_mid_item, find_item_daoList) {
                @Override
                public void convert(Com_ViewHolder holder, final Find_item_dao find_item_dao) {
                    if (find_item_dao != null) {
                        holder.setText(R.id.home_mid_title, find_item_dao.getTitle());
                        holder.setText(R.id.home_mid_money, "¥ " + find_item_dao.getMoney());
                        holder.setText(R.id.home_mid_city, find_item_dao.getCity());
                        holder.setText(R.id.home_mid_star, find_item_dao.getStar() + "");
                        holder.setImageResource(R.id.home_mid_image, MyUrl.add_Path(find_item_dao.getDefaultpath()));

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                            Toast.makeText(getContext(), " " + find_item_dao.getTitle(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), PersonMainPage.class);
                                Log.e("viewID", find_item_dao.getId() + " ");
                                intent.putExtra("viewID", find_item_dao.getId());
                                startActivity(intent);
                            }
                        });
                    }
                }
            };

            home_mid_recyclerView.setAdapter(adapter);
            //=========home_mid_recyclerViee=============================================

            //=========home_nine_recyclerViee=============================================
            home_nine_recyclerview = mRootView.findViewById(R.id.home_nine_recyclerview);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
            home_nine_recyclerview.setLayoutManager(gridLayoutManager);
            home_nine_recyclerview.setAdapter(new Com_Adapter<Home_nine_dao>(getContext(), R.layout.home_nine_item, home_nine_list_dao) {
                @Override
                public void convert(Com_ViewHolder holder, final Home_nine_dao home_nine_dao) {
                    if(home_nine_dao != null){
                        holder.setText(R.id.home_nine_item_text, home_nine_dao.getIntroduce());

                        //设置图片
                        holder.setImageResource(R.id.home_nine_item_image, home_nine_dao.getHome_nine_image());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                            Toast.makeText(getContext(), " " + home_nine_dao.getIntroduce(), Toast.LENGTH_SHORT).show();
                                //跳转到相关的页面去
                                Intent intent = new Intent(getActivity(), Home_show_item.class);
                                intent.putExtra("type", home_nine_dao.getType());
                                getActivity().startActivity(intent);
                            }
                        });
                    }

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
                if (listResponseResult.getData() != null && listResponseResult.getData().size() > 0) {
                    find_item_daoList.clear();
                    find_item_daoList.addAll(listResponseResult.getData());
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.e("com", "complete");
                //可能会犯错，因为list= re，getList（）得到的不是同一个list对象
                adapter.notifyDataSetChanged();
            }
        };
//在这里获取第一个和第三个的
        HttpMethods.getInstance()
                .getFind_item(1, observer);
        HttpMethods.getInstance()
                .getFind_item(3, observer);
    }

    private void initView(ViewGroup viewGroup) {

        //========================================顶部搜索框
        mViewPager = mRootView.findViewById(R.id.rollview_pager);
        tvSearch = mRootView.findViewById(R.id.tv_search);
        mSearchLayout = mRootView.findViewById(R.id.ll_search);
        toolbar = mRootView.findViewById(R.id.toolbar);
        imageView = mRootView.findViewById(R.id.editextSearch);

        //========================================顶部搜索框

        scrollView = mRootView.findViewById(R.id.scroll);

        swipeRefreshLayout = mRootView.findViewById(R.id.refresh);
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


        //===========================Search
        final ImageLoopAdapter loopAdapter = new ImageLoopAdapter(mViewPager);
        mViewPager.setAdapter(loopAdapter);
        mViewPager.setHintView(new ColorPointHintView(getContext(), Color.YELLOW, Color.WHITE));


        //设置全屏透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            ViewGroup rootView = (ViewGroup) ((ViewGroup)mRootView.findViewById(android.R.id.content)).getChildAt(0);
//            ViewCompat.setFitsSystemWindows(rootView,false);
            viewGroup.setClipToPadding(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            this.getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            this.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            this.getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        //设置toolbar初始透明度为0
        toolbar.getBackground().mutate().setAlpha(0);
        //scrollview滚动状态监听
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setEnabled(scrollView.getScrollY() == 0);
                }

                //改变toolbar的透明度
                changeToolbarAlpha();
                //滚动距离>=大图高度-toolbar高度 即toolbar完全盖住大图的时候 且不是伸展状态 进行伸展操作
                if (scrollView.getScrollY() >= mViewPager.getHeight() - toolbar.getHeight() && !isExpand) {
                    expand();
                    isExpand = true;
                }
                //滚动距离<=0时 即滚动到顶部时  且当前伸展状态 进行收缩操作
                else if (scrollView.getScrollY() <= 0 && isExpand) {
                    reduce();
                    isExpand = false;
                }
            }
        });
        mSearchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExpand) {
                    expand();
                }
//                tvSearch.setText("搜索当地导游和美景");
                tvSearch.setHint("搜索当地导游和美景");
            }
        });
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExpand) {
                    expand();
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: earch fro " + tvSearch.getText().toString());
                String keyWord = tvSearch.getText().toString().trim();

                if (!"".equals(keyWord.trim())) {
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                    intent.putExtra("search", keyWord);
                    getActivity().startActivity(intent);
                }else {
                    expand();
                }
            }
        });

        //===========================Search


    }


    private void initData() {
        //---------------------------------
        find_item_daoList = new ArrayList<>();
        //---------------------------------

        //=========home_mid_recyclerViee========中间横向的拉动，设置数据，数据来源=====================================
//        home_mid_list = new ArrayList<>();
//        for (int i = 0; i < home_mid_string.length; ++i) {
//            hmoe_mid_dao = new Home_mid_dao();
//            hmoe_mid_dao.setName(home_mid_string[i]);
//            hmoe_mid_dao.setImage(hmoe_mid_image[i]);
//            home_mid_list.add(hmoe_mid_dao);
//        }
        //=========================================以上从网络上获取


        //========================================================home_nine_list_dao
        home_nine_list_dao = new ArrayList<>();
        for (int i = 0; i < home_nine_path.length; ++i) {
            home_nine_dao = new Home_nine_dao();
            home_nine_dao.setHome_nine_image(home_nine_path[i]);
            home_nine_dao.setIntroduce(home_nine_name[i]);
            home_nine_dao.setType(i + 1);
            home_nine_list_dao.add(home_nine_dao);
        }


        //=========home_every_recyclerViee=============================================
//        home_every_list = new ArrayList<Home_everyDao>();
//        for (int i = 0; i < home_every_image.length; ++i) {
//            home_everyDao = new Home_everyDao();
//            home_everyDao.setTime("today");
//            home_everyDao.setImage(home_every_image[i]);
//            home_everyDao.setIntroduce(home_every_string[i]);
//            home_everyDao.setId(i);
//            home_every_list.add(home_everyDao);
//        }
//
//        home_everyday_recyclerView = mRootView.findViewById(R.id.home_everyday_recyclerview);
//        home_everyday_recyclerView.setLayoutManager(home_every_lin);

        LinearLayoutManager home_every_lin = new LinearLayoutManager(getContext()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        home_every_lin.setOrientation(LinearLayoutManager.HORIZONTAL);

    }


    ///=================Search
    private class ImageLoopAdapter extends LoopPagerAdapter {
        int[] imgs = new int[]{
                R.drawable.top1,
                R.drawable.top2,
                R.drawable.top3
        };

        public ImageLoopAdapter(RollPagerView viewPager) {
            super(viewPager);
        }

        @Override
        public View getView(ViewGroup container, int position) {
            LogUtil.d("sss" + position);
            ImageView view = new ImageView(container.getContext());
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            view.setImageResource(imgs[position]);
            return view;
        }

        @Override
        public int getRealCount() {
            return imgs.length;
        }

    }


    private void changeToolbarAlpha() {
        int scrollY = scrollView.getScrollY();
        //快速下拉会引起瞬间scrollY<0
        if (scrollY < 0) {
            toolbar.getBackground().mutate().setAlpha(0);
            return;
        }
        //计算当前透明度比率
        float radio = Math.min(1, scrollY / (mViewPager.getHeight() - toolbar.getHeight() * 1f));
        //设置透明度
        toolbar.getBackground().mutate().setAlpha((int) (radio * 0xFF));
    }


    private void expand() {
        //设置伸展状态时的布局
        tvSearch.setHint("搜索当地导游和美景");
        RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) mSearchLayout.getLayoutParams();
        LayoutParams.width = LayoutParams.MATCH_PARENT;
        LayoutParams.setMargins(dip2px(10), dip2px(10), dip2px(10), dip2px(10));
//        mSearchLayout.setBackgroundColor(getResources().getColor(R.color.white));
        mSearchLayout.setLayoutParams(LayoutParams);
        //开始动画
        beginDelayedTransition(mSearchLayout);
    }

    private void reduce() {
        //设置收缩状态时的布局
        tvSearch.setHint("搜索");
        RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) mSearchLayout.getLayoutParams();
        LayoutParams.width = dip2px(80);
        LayoutParams.setMargins(dip2px(10), dip2px(10), dip2px(10), dip2px(10));
//        mSearchLayout.setBackgroundColor(getResources().getColor(R.color.white));
        mSearchLayout.setLayoutParams(LayoutParams);
        //开始动画
        beginDelayedTransition(mSearchLayout);
    }

    void beginDelayedTransition(ViewGroup view) {
        mSet = new AutoTransition();
        mSet.setDuration(300);
        TransitionManager.beginDelayedTransition(view, mSet);
    }

    private int dip2px(float dpVale) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpVale * scale + 0.5f);
    }
    ///=================Search


}

