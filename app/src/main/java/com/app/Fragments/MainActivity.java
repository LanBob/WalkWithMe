package com.app.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;


import com.app.R;

import java.util.ArrayList;
import java.util.List;

import site.gemus.openingstartanimation.OpeningStartAnimation;


public class MainActivity extends AppCompatActivity {


    private static Context context;
    /**
     * RecycleView Demo
     */
    private FragmentTabHost mTabHost;
    private ViewPager mViewPager;
    private List<Fragment> mFragmentList;
    private Class mClass[] = {HomeFragment.class, FindFragment.class, MessageFragment.class, MineFragment.class};
    private Fragment mFragment[] = {new HomeFragment(), new FindFragment(), new MessageFragment(), new MineFragment()};
    private String mTitles[] = {"首页", "发现", "客服", "我的"};
    private int mImages[] = {
            R.drawable.tab_home,
            R.drawable.tab_report,
            R.drawable.tab_message,
            R.drawable.tab_mine
    };
//    private WebSocket mwebSocket;

    public static Context getContext() {
        context = MainActivity.getContext();
        return context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * 设置开机动画，其中bitmap是设置中间的图片
         */
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.apps);
        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
        OpeningStartAnimation openingStartAnimation = new OpeningStartAnimation.Builder(this)
                .setAppIcon(drawable)//设置图片
                .setAnimationInterval(400)//设置时间
                .setAppName("walk with me") //设置app名称
                .setAppStatement("世界，就在脚下！")//设置文字
                .create();

        openingStartAnimation.show(this);//显示

        init();
    }

    private void init() {

        initView();

        initEvent();
    }

    private void initView() {
        //找到Tab选项卡
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);

        //FragmentTabHost里面的ViewPager的对象
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mFragmentList = new ArrayList<Fragment>();

        //设置大体对得布局
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        //去掉分割线
        mTabHost.getTabWidget().setDividerDrawable(null);

        for (int i = 0; i < mFragment.length; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTitles[i]).setIndicator(getTabView(i));

            //将对应的fragment添加到控件中去
            mTabHost.addTab(tabSpec, mClass[i], null);

            mFragmentList.add(mFragment[i]);
            //设置背景,检测结论：getTabWidget是底部导航栏，而getChildAt是获取第几个导航栏的位置
            mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.WHITE);
        }

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        });

    }


    //设置底部导航栏的数据，返回一个View对象
    private View getTabView(int index) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item, null);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        TextView title = (TextView) view.findViewById(R.id.title);

        image.setImageResource(mImages[index]);
        title.setText(mTitles[index]);

        return view;
    }

    private void initEvent() {

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                mViewPager.setCurrentItem(mTabHost.getCurrentTab());
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTabHost.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        int pos = getIntent().getIntExtra("position", 0);
        if (pos == 3) {
            mViewPager.setCurrentItem(mTabHost.getCurrentTab());
            mTabHost.setCurrentTab(pos);
        }
        super.onResume();
    }
}
//    WebSocketSubscriber webSocketSubscriber = new WebSocketSubscriber() {
//            @Override
//            protected void onOpen(@NonNull WebSocket webSocket) {
//                Log.d("MainActivity", " on WebSocket open");
//                mwebSocket = webSocket;
//                mwebSocket.send("haha");
//            }
//
//            /**
//             * 接收到消息
//             * @param text
//             */
//            @Override
//            protected void onMessage(@NonNull String text) {
//                Log.d("MainActivity", text);
//            }
//
//            /**
//             * 二进制消息
//             * @param byteString
//             */
//            @Override
//            protected void onMessage(@NonNull ByteString byteString) {
//                Log.d("MainActivity", byteString.toString());
//            }
//
//            @Override
//            protected void onReconnect() {
//                Log.d("MainActivity", "onReconnect");
//            }
//
//            @Override
//            protected void onClose() {
//                Log.d("MainActivity", "onClose");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                super.onError(e);
//            }
//        };
//
//        String url = MyUrl.add_Wsurl("1158746179/1");
//
//        Log.e("MainActivity",url);
//
//        /**
//         * 发起连接
//         */
//        WebSocketUtil.getInstance()
//                    .connect(url, this, webSocketSubscriber);