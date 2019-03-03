package com.app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.app.R;
import com.app.Util.LoadingDialogUtil;
import com.app.Util.MyUrl;
import com.app.Util.StringUtil;
import com.app.commonAdapter.Com_Adapter;
import com.app.commonAdapter.Com_ViewHolder;
import com.app.entity.CommentDao;
import com.app.entity.HeadImage;
import com.app.modle.HttpMethods;
import com.app.modle.ResponseResult;
import com.app.view.CircleImageView;
import com.app.view.CommentDialog;
import com.app.view.GoodView;
import com.app.entity.Star_collection;
import com.app.entity.View_show_dao;
import com.app.entity.Person_main_page_dao;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sackcentury.shinebuttonlib.ShineButton;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class PersonMainPage extends AppCompatActivity {
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView main_top_imageView;
    private ActionBar actionBar;
    private List<Person_main_page_dao> list_dao;
    private Person_main_page_dao dao;
    private ImageView content_imageview;
    private ImageView imageView;
    private Boolean isYes;
    private Boolean isCollection;
    private TextView yesNum;
    private TextView collection_num;
    private TextView main_page_position_name;
    private TextView personMainPageMoney;
    private CircleImageView headImage;
    ///
    private DrawerLayout mDrawerLayout = null;
    private LinearLayout leftChat;
    private LinearLayout leftMainPage;
    private CircleImageView leftImageView;
    private CommentDialog dialog;

    private TextView upMytime;


    private TextView content_textview;
    private ShineButton star_button;
    private ShineButton collection_button;
    LoadingDialogUtil loadingDialogUtil;
//    private Long userID;
    private Long view_show_id = 0L;
//    String user = "";
    private String follower;
    private String followed;
//    private static SharedPreferencesHelper helper;
//    static {
//        helper = new SharedPreferencesHelper(MainApplication.getContext(),"loginState");
//    }

    //===================
    Observer<ResponseResult<View_show_dao>> view_show_observer;
    Observer<ResponseResult<String>> star_observer;
    Observer<ResponseResult<String>> collection_observer;
    Observer<ResponseResult<String>> follow_observer;
    Observer<ResponseResult<Star_collection>> star_collection_observer;
    Observer<ResponseResult<Integer>> star_collection_follow;
    Observer<ResponseResult<HeadImage>> headImageObserver;
    Observer<ResponseResult<View_show_dao>> mainObserver;
    Observer<ResponseResult<List<CommentDao>>> getCommentObserver;
    Observer<ResponseResult<String>> commentObserver;
    //===================

    //RecyclerView评论
    private RecyclerView.Adapter adapter;
    private List<CommentDao> commentDaoList;
    private TextView eat;
    private TextView live;
    private TextView scoreTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_main_page);
        follower = StringUtil.getValue("username");
        Intent intent = getIntent();
        //============================================================通过ID获取数据
        view_show_id = intent.getLongExtra("viewID", 1L);
        commentDaoList = new ArrayList<>();
        upMytime = findViewById(R.id.upMytime);
        eat = findViewById(R.id.eat);
        live = findViewById(R.id.live);

        scoreTextView = findViewById(R.id.score);
        initData();
        initView();

        //=============================底部设置点赞和关注之类
        if (star_button != null)
            star_button.init(this);

        //加1按钮
        final GoodView goodView = new GoodView(this);

        star_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isYes == false) {
                    isYes = true;
                    yesNum.setText((Integer.parseInt(yesNum.getText().toString()) + 1) + "");
                    goodView.setText("赞！ +1");
                    goodView.show(v);

                HttpMethods.getInstance()
                        .add_Star(view_show_id.toString(),follower,1,star_observer);

                } else {
                    isYes = false;
                    yesNum.setText((Integer.parseInt(yesNum.getText().toString()) - 1) + "");
                    goodView.setText("不喜欢 -1");
                    goodView.show(v);
                    HttpMethods.getInstance()
                            .add_Star(view_show_id.toString(),follower,0,star_observer);
                }
            }
        });

        collection_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isCollection == false){//如果没有点赞
                    isCollection = true;
                    collection_num.setText((Integer.parseInt(collection_num.getText().toString()) +1) +"");

                    HttpMethods.getInstance()
                            .add_Collection(view_show_id.toString(),follower,1,collection_observer);

                }else{
                    isCollection = false;
                    collection_num.setText((Integer.parseInt(collection_num.getText().toString()) -1) +"");
                    HttpMethods.getInstance()
                            .add_Collection(view_show_id.toString(),follower,0,collection_observer);
                }
            }
        });

//        Observer<ResponseResult<View_show_dao>>
        //view_show_id
        HttpMethods.getInstance()
                .getView_show_dao(view_show_id, mainObserver);
        //===========加载主界面=================================================
    }

    private void initView() {
        isYes = false;
        isCollection = false;
        yesNum = findViewById(R.id.yesnum);
        collection_num = findViewById(R.id.collection_num);
        toolbar = findViewById(R.id.main_page_toolBar);
        collapsingToolbarLayout = findViewById(R.id.main_page_collapsing);
        main_top_imageView = findViewById(R.id.main_page_top_imageview);
        main_page_position_name = findViewById(R.id.main_page_position_name);
        personMainPageMoney = findViewById(R.id.PersonMainPageMoney);
        headImage = findViewById(R.id.headImage);
        star_button =  findViewById(R.id.person_page_yes_no);
        collection_button = findViewById(R.id.person_page_collection);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        leftMainPage = findViewById(R.id.leftMainPage);
        leftChat = findViewById(R.id.leftChat);

        //通过java添加 图片
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            /**
             * 当抽屉滑动状态改变的时候被调用
             * 状态值是STATE_IDLE（闲置--0）, STATE_DRAGGING（拖拽的--1）, STATE_SETTLING（固定--2）中之一。
             * 抽屉打开的时候，点击抽屉，drawer的状态就会变成STATE_DRAGGING，然后变成STATE_IDLE
             */
            @Override
            public void onDrawerStateChanged(int arg0) {
//                Log.i("drawer", "drawer的状态：" + arg0);
            }

            /**
             * 当抽屉被滑动的时候调用此方法
             * arg1 表示 滑动的幅度（0-1）
             */
            @Override
            public void onDrawerSlide(View arg0, float arg1) {
                Log.i("drawer", arg1 + "");
            }

            /**
             * 当一个抽屉被完全打开的时候被调用
             */
            @Override
            public void onDrawerOpened(View arg0) {
//                Log.i("drawer", "抽屉被完全打开了！");
            }

            /**
             * 当一个抽屉完全关闭的时候调用此方法
             */
            @Override
            public void onDrawerClosed(View arg0) {
//                Log.i("drawer", "抽屉被完全关闭了！");
            }
        });
        headImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            }
        });
//        上述打开抽屉

        leftMainPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PersonMainPage.this,"请求主页",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PersonMainPage.this,OwnMainPage.class);
                intent.putExtra("userId",followed);
                startActivity(intent);
            }
        });
        leftChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PersonMainPage.this,"请求联系 Ta",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initData() {
        loadingDialogUtil = new LoadingDialogUtil(PersonMainPage.this);
        loadingDialogUtil.setCanceledOnTouchOutside(false);

        adapter = new Com_Adapter<CommentDao>(PersonMainPage.this,R.layout.comment,commentDaoList){

            @Override
            public void convert(Com_ViewHolder holder, CommentDao commentDao) {
                holder.setText(R.id.commentName, commentDao.getUserName());
                holder.setText(R.id.commentComment, commentDao.getComment());
                holder.setText(R.id.mytime,StringUtil.millToTime(Long.valueOf(commentDao.getMytime())));
                holder.setImageResource(R.id.commentHeadImage, MyUrl.add_Path(commentDao.getDefaultImage()));
            }
        };

        ///====================动态设置图片的长和宽
        final LinearLayout view = findViewById(R.id.main_page_show_content);
        final ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        //设置Glide占位图
        final RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.gray_bg)
                .error(R.drawable.bg)
                .priority(Priority.HIGH)
                .override(lp.width, lp.height)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        mainObserver = new Observer<ResponseResult<View_show_dao>>() {
            @Override
            public void onSubscribe(Disposable d) {
                loadingDialogUtil.show();
            }

            @Override
            public void onNext(ResponseResult<View_show_dao> find_item_daoResponseResult) {
                //返回结果
                Log.e("message", find_item_daoResponseResult.getData().getIntroduce() + "。");
                View_show_dao dao = find_item_daoResponseResult.getData();
                followed = dao.getUser_id().toString();
                view_show_id = dao.getId();
                main_page_position_name.setText(dao.getCity());
                personMainPageMoney.setText("¥ " + dao.getMoney());
                upMytime.setText(dao.getMyTime());
                if("是".equals(dao.getFriendlyToEat())){
                    eat.setTextColor(getResources().getColor(R.color.blue));
                    eat.setText("餐饮方便： √");
                }else {
                    eat.setTextColor(getResources().getColor(R.color.gray));
                    eat.setText("餐饮不方便： ×");
                }
                if("是".equals(dao.getFirendlyToLive())){
                    eat.setText("住宿方便： √");
                    eat.setTextColor(getResources().getColor(R.color.blue));
                }else {
                    eat.setTextColor(getResources().getColor(R.color.gray));
                    eat.setText("住宿不方便： ×");
                }
                scoreTextView.setText("综合评分: " + dao.getScore());


                //设置背景图
                String bgurl = MyUrl.add_Path(dao.getDefaultpath());
                Glide.with(PersonMainPage.this)
                        .load(bgurl)
                        .apply(options)
                        .into(main_top_imageView);

                collapsingToolbarLayout.setTitle(dao.getTitle());
                String introduce = dao.getIntroduce();
                Log.e("before", introduce);
                List<String> list = StringUtil.cutStringByImgTag(introduce);
                for (String s : list) {
                    Pattern p_img = Pattern.compile("<(img|IMG)(.*?)(/>|></img>|>)");
                    Matcher m_img = p_img.matcher(s);
                    boolean result_img = m_img.find();
                    //如果是图片
                    if (result_img) {
                        Pattern p_src = Pattern.compile("(src|SRC)=(\"|\')(.*?)(\"|\')");
                        Matcher m_src = p_src.matcher(s);
                        if (m_src.find()) {
                            s = m_src.group(3);
                            content_imageview = new ImageView(PersonMainPage.this);
                            content_imageview.setLayoutParams(lp);
                            content_imageview.setAdjustViewBounds(true);
                            content_imageview.setPadding(5, 10, 5, 10);
                            //通过增加实现
                            view.addView(content_imageview);
                            String url = MyUrl.add_Path(s);
                            Log.e("url", url);
                            Glide.with(PersonMainPage.this)
                                    .load(url)
                                    .apply(options)
                                    .into(content_imageview);
                        }
                    } else {
                        content_textview = new TextView(PersonMainPage.this);
                        content_textview.setLayoutParams(lp);
                        content_textview.setPadding(15, 30, 15, 30);
                        Log.e("s", s);
                        view.addView(content_textview);
                        content_textview.setText("   " + s);
                    }//
                }
//                增加线路和详细地址
                content_textview = new TextView(PersonMainPage.this);
                content_textview.setLayoutParams(lp);
                content_textview.setPadding(30, 30, 15, 30);
                view.addView(content_textview);
                content_textview.setText("详细地址："+dao.getDetailAddress());

                content_textview = new TextView(PersonMainPage.this);
                content_textview.setLayoutParams(lp);
                content_textview.setPadding(30, 30, 15, 30);
                view.addView(content_textview);
                content_textview.setText("推荐路线："+dao.getRoute());
//                增加线路和详细地址




                ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,100);
                //for结束，内容展示完成，增加一个评论按钮
                ImageView commont = new ImageView(PersonMainPage.this);
                commont.setImageResource(R.drawable.pinglun);
                commont.setLayoutParams(layoutParams);
                commont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ///
                        dialog = new CommentDialog(PersonMainPage.this,new CommentDialog.OnClickCallBack(){

                            @Override
                            public String send(String comment) {
                                //需要发送评论
                                HttpMethods.getInstance()
                                        .comment(follower,view_show_id.toString(),comment,commentObserver);
                                Toast.makeText(PersonMainPage.this,"PersonMainPage 评论" + comment, +Toast.LENGTH_LONG).show();

                                return null;
                            }
                        });
                        dialog.show();
                    }
                });
                view.addView(commont);

                ViewGroup.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                TextView textView = new TextView(PersonMainPage.this);
                textView.setTextColor(getResources().getColor(R.color.blue));
                textView.setLayoutParams(layoutParams);
                textView.setTextSize(19);
                textView.setText("     以下是评论区:");
                view.addView(textView);

                ViewGroup.LayoutParams ll = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 190);

                //尝试
                RecyclerView recyclerView = new RecyclerView(PersonMainPage.this);
                recyclerView.setLayoutParams(ll);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PersonMainPage.this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adapter);
                view.addView(recyclerView);
            }
            @Override
            public void onError(Throwable e) {
                //错误结果
                Log.e("error", "" + e.getMessage());
                loadingDialogUtil.cancel();
                Toast.makeText(PersonMainPage.this,"错误请重试或反馈",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                //这时才能得到View_show_id
                loadingDialogUtil.cancel();

                /**
                 * 获取有多少个点赞和收藏
                 */
                HttpMethods.getInstance()
                        .getStarColllection(view_show_id.toString(),star_collection_observer);

                HttpMethods.getInstance()
                        .getHeadImage(followed,headImageObserver);
                HttpMethods.getInstance()
                        .getCommentByViewShow(view_show_id.toString(),getCommentObserver);
            }
        };

        star_observer = new Observer<ResponseResult<String>>() {
            @Override
            public void onSubscribe(Disposable d) {
                loadingDialogUtil.show();
            }

            @Override
            public void onNext(ResponseResult<String> stringResponseResult) {

            }

            @Override
            public void onError(Throwable e) {
                loadingDialogUtil.cancel();
            }

            @Override
            public void onComplete() {
                loadingDialogUtil.cancel();
                Toast.makeText(PersonMainPage.this,"点赞成功",Toast.LENGTH_SHORT).show();
            }
        };

        collection_observer = new Observer<ResponseResult<String>>() {
            @Override
            public void onSubscribe(Disposable d) {
                loadingDialogUtil.show();
            }

            @Override
            public void onNext(ResponseResult<String> stringResponseResult) {

            }

            @Override
            public void onError(Throwable e) {
                loadingDialogUtil.cancel();
            }

            @Override
            public void onComplete() {
                loadingDialogUtil.cancel();
                Toast.makeText(PersonMainPage.this,"收藏成功",Toast.LENGTH_SHORT).show();
            }
        };
        follow_observer = new Observer<ResponseResult<String>>() {
            @Override
            public void onSubscribe(Disposable d) {
                loadingDialogUtil.show();
            }

            @Override
            public void onNext(ResponseResult<String> stringResponseResult) {

            }

            @Override
            public void onError(Throwable e) {
                loadingDialogUtil.cancel();
            }

            @Override
            public void onComplete() {
                loadingDialogUtil.cancel();
                Toast.makeText(PersonMainPage.this,"操作成功",Toast.LENGTH_SHORT).show();
            }
        };

        star_collection_observer = new Observer<ResponseResult<Star_collection>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<Star_collection> star_collectionResponseResult) {
                Star_collection dao = star_collectionResponseResult.getData();
                if(dao != null) {
                    yesNum.setText(dao.getStar() + "");
                    collection_num.setText(dao.getCollection() + "");
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

                /**
                 * 获取点赞
                 */
                HttpMethods.getInstance()
                        .star_collection_follow(view_show_id.toString(),follower,0,star_collection_follow);

                /**
                 * 获取是否点赞
                 */
                HttpMethods.getInstance()
                        .star_collection_follow(view_show_id.toString(),follower,1,star_collection_follow);
            }
        };

        star_collection_follow = new Observer<ResponseResult<Integer>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<Integer> integerResponseResult) {
                String message = integerResponseResult.getMessage();
                int code = integerResponseResult.getData();
                if("star".equals(message)){
                    if(code == 1){//已经执行
                        star_button.setChecked(true);
                        isYes = true;
                    }else{
                        star_button.setChecked(false);
                        isYes = false;
                    }
                }else if("collection".equals(message)){
                    if(code == 1){//已经执行
                        collection_button.setChecked(true);
                        isCollection = true;
                    }else{
                        collection_button.setChecked(false);
                        isCollection = false;
                    }
                }else if("follow".equals(message)){
                    if(code == 1){//已经执行

                    }else{

                    }
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        leftImageView = findViewById(R.id.leftImageView);
        headImageObserver = new Observer<ResponseResult<HeadImage>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<HeadImage> headImageResponseResult) {

                Log.e("data",headImageResponseResult.getData().getHead_image());
                HeadImage headImageData = headImageResponseResult.getData();
                String headImageUrl = "";
                if(headImageData != null){
                    headImageUrl = headImageData.getHead_image();
                }
                if(headImageUrl != "" || headImageUrl != null){
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.gray_bg)
                            .error(R.drawable.chat_girl)
                            .priority(Priority.HIGH)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

                    Glide.with(PersonMainPage.this)
                            .load(MyUrl.add_Path(headImageUrl))
                            .apply(options)
                            .into(headImage);

                    Glide.with(PersonMainPage.this)
                            .load(MyUrl.add_Path(headImageUrl))
                            .apply(options)
                            .into(leftImageView);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        getCommentObserver = new Observer<ResponseResult<List<CommentDao>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<List<CommentDao>> listResponseResult) {

                List<CommentDao> list = listResponseResult.getData();
                if( list != null&& listResponseResult.getCode() != 0){
                    commentDaoList.clear();
                    commentDaoList.addAll(list);
                    //得到数据
                    Log.e("size",commentDaoList.size() + " " +commentDaoList.get(0));
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                //刷新
                adapter.notifyDataSetChanged();
            }
        };

        commentObserver = new Observer<ResponseResult<String>>() {
            @Override
            public void onSubscribe(Disposable d) {
                loadingDialogUtil.show();
            }

            @Override
            public void onNext(ResponseResult<String> stringResponseResult) {
                int code = stringResponseResult.getCode();
                if(code == 0){
                    Toast.makeText(PersonMainPage.this,"评论出错啦",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(PersonMainPage.this,"评论成功",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(PersonMainPage.this,"评论出错啦",Toast.LENGTH_SHORT).show();
                loadingDialogUtil.cancel();
            }

            @Override
            public void onComplete() {
                loadingDialogUtil.cancel();
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.follow:
                HttpMethods.getInstance()
                        .add_Follow(follower,followed,1,follow_observer);
                break;
            case R.id.unfollow:
                HttpMethods.getInstance()
                        .add_Follow(follower,followed,0,follow_observer);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }
}