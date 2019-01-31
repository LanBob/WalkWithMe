package com.app.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.R;
import com.app.Util.MyUrl;
import com.app.activity.EditActivity;
import com.app.activity.PersonMainPage;
import com.app.commonAdapter.Com_Adapter;
import com.app.commonAdapter.Com_ViewHolder;
import com.app.modle.HttpMethods;
import com.app.modle.ResponseResult;
import com.app.entity.Find_item_dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by donglinghao on 2016-01-28.
 */
public class FindFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View mRootView;

    //到养生结束
    private String tab_item[] = {"风景", "摄影", "手工", "人文", "养生", "节日", "探险", "其他"};
    private TabLayout tabLayout = null;
    private ViewPager vp_pager;
    private ImageButton mFabButton;
    private RecyclerView.Adapter adapter;
    //==========================================================
    private List<Find_item_dao> find_item_list;

    private static List<Find_item_dao>[] find_item_array;

    //==========================================================

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            //得到布局
            mRootView = inflater.inflate(R.layout.find_fragment, container, false);
            //=======================初始化
            initView();
            initData();
            //=======================
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    //==========================================初始化（顺序不要改）
    private void initView() {
        swipeRefreshLayout = mRootView.findViewById(R.id.find_refresh);
        mFabButton = mRootView.findViewById(R.id.floatCamera);
        tabLayout = mRootView.findViewById(R.id.tablayout);
        vp_pager = mRootView.findViewById(R.id.tab_viewpager);
        vp_pager.setAdapter(new MorePagerAdapter());
        tabLayout.setupWithViewPager(vp_pager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        reflex(tabLayout);//设置tab宽度

        //==============跳转到PersionMainPage
        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditActivity.class);
                getActivity().startActivity(intent);
            }
        });
        //=========================================设置下拉刷新
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.DKGRAY);//设置旋转圈的颜色
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout();
            }
        });
    }

    //==========================================初始化数据（）
    private void initData() {

        if (find_item_array == null) {
            find_item_array = new List[9];
            for (int i = 0; i < find_item_array.length; ++i) {
                List<Find_item_dao> mlist = new ArrayList<>();
                find_item_array[i] = mlist;
            }
        }
        Log.e("sieze", find_item_array.length + " ");
        //===============================================
    }


    //==========================================设置ViewPaper
    final class MorePagerAdapter extends PagerAdapter {

        @Override
        public int getItemPosition(Object object) {
            //刷新全部的数据
            return POSITION_NONE;
        }

        ///======================================设置有个多少个tab
        @Override
        public int getCount() {
            return tab_item.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            //==============================================
            //设置刷新
            //==========================================为每个栏目设置recyclerView
            RecyclerView recyclerView = new RecyclerView(getContext());

            //==============================================================================================
            final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                    StaggeredGridLayoutManager.VERTICAL);//定义瀑布流管理器，第一个参数是列数，第二个是方向。
            layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
            //不设置的话，图片闪烁错位，有可能有整列错位的情况。
            recyclerView.setLayoutManager(layoutManager);

            //setting up our OnScrollListener
            recyclerView.addOnScrollListener(new HidingScrollListener() {
                @Override
                public void onHide() {
                    hideViews();
                }

                @Override
                public void onShow() {
                    showViews();
                }
            });

            //===============================================
            adapter = new Com_Adapter<Find_item_dao>(getContext(), R.layout.find_item, find_item_array[position]) {

                @Override
                public void convert(Com_ViewHolder holder, final Find_item_dao find_item_dao) {
                    holder.setText(R.id.find_item_title, find_item_dao.getTitle());
                    String url = produce(find_item_dao.getDefaultpath());
                    Log.e("path", "" + url);
                    holder.setImageResource(R.id.find_item_default_image, url);
                    holder.setText(R.id.find_item_money, "¥ " + find_item_dao.getMoney());
                    holder.setText(R.id.find_item_colloection, find_item_dao.getStar() + "");
                    Log.e("city", find_item_dao.getCity() + "");
                    holder.setText(R.id.find_item_city, find_item_dao.getCity());
                    ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                    layoutParams.height = 750 + (holder.getLayoutPosition() % 3) * 30 + new Random().nextInt(200);
                    holder.itemView.setLayoutParams(layoutParams);
                    holder.setImageSize(R.id.find_item_default_image, layoutParams.width, layoutParams.height - 300);

                    /**
                     * 通过viewID跳转到响应的页面
                     */
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), PersonMainPage.class);
                            intent.putExtra("viewID", find_item_dao.getId());
                            startActivity(intent);
                        }
                    });

                }
            };
            getData(position);
            if(position == 0)
                getData(0);
            recyclerView.setAdapter(adapter);
            //==============================================================================================
            (container).addView(recyclerView);
            //==========================================
            return recyclerView;
        }

        private String produce(String defaultpath) {
            defaultpath = MyUrl.add_Path(defaultpath);
            return defaultpath;
        }

        private void hideViews() {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFabButton.getLayoutParams();
            int fabBottomMargin = lp.bottomMargin;
            mFabButton.animate().translationY(mFabButton.getHeight() + fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
        }

        private void showViews() {
            mFabButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tab_item[position];
        }
    }

    //===================================获取数据,并刷新数据
    private void getData(final int position) {

        if (find_item_array[position].size() == 0 || find_item_array[position] ==null) {
            Observer<ResponseResult<List<Find_item_dao>>> observer = new Observer<ResponseResult<List<Find_item_dao>>>() {
                @Override
                public void onSubscribe(Disposable d) {
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(true);
                        }
                    });
                }

                @Override
                public void onNext(ResponseResult<List<Find_item_dao>> listResponseResult) {
                    find_item_list = listResponseResult.getData();

                    find_item_array[position].clear();

                    find_item_array[position].addAll(find_item_list);
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("error", e.getMessage());
                }

                @Override
                public void onComplete() {
                   // Toast.makeText(getContext(),"加载" + position +"完成",Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    adapter.notifyDataSetChanged();
                    if(position == 0)
                        refreshLayout();
                }
            };

            HttpMethods.getInstance()
                    .getFind_item(position+1, observer);
        }
    }

    //========================================================无关紧要的代码
    private void refreshLayout() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        vp_pager.getAdapter().notifyDataSetChanged();
                        //每次都要设置这个，消耗时间
                        reflex(tabLayout);
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(), "刷新成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
        swipeRefreshLayout.setRefreshing(false);//设置成true的话，下拉过后就会一直在那里转
    }

    //设置tab的宽度
    public void reflex(final TabLayout tabLayout) {
        //了解源码得知 线的宽度是根据 tabView的宽度来设置的
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //拿到tabLayout的mTabStrip属性
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);

                    int dp10 = 10;

                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);

                        //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                        Field mTextViewField = tabView.getClass().getDeclaredField("textView");
                        mTextViewField.setAccessible(true);

                        TextView mTextView = (TextView) mTextViewField.get(tabView);

                        //==========================================尝试设置字体
                        ViewGroup.LayoutParams lp = mTextView.getLayoutParams();
                        lp.width = 200;
                        lp.height = lp.height + 1 - 1;
                        mTextView.setLayoutParams(lp);
                        //==========================================

                        tabView.invalidate();
                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    //========================================================无关紧要的代码

}

////设置滑动相关
//abstract class HidingScrollListener extends RecyclerView.OnScrollListener {
//
//    private static final int HIDE_THRESHOLD = 20;
//    private int scrolledDistance = 0;
//    private boolean controlsVisible = true;
//
//    @Override
//    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//        super.onScrolled(recyclerView, dx, dy);
//        if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
//            onHide();
//            controlsVisible = false;
//            scrolledDistance = 0;
//        } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
//            onShow();
//            controlsVisible = true;
//            scrolledDistance = 0;
//        }
//        if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
//            scrolledDistance += dy;
//        }
//    }
//
//    public abstract void onHide();
//
//    public abstract void onShow();
//}