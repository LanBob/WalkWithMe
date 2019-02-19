package com.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.app.Util.SharedPreferencesHelper;
import com.app.Util.StringUtil;
import com.app.MainApplication;
import com.app.modle.HttpMethods;
import com.app.modle.ResponseResult;
import com.app.view.GoodView;
import com.app.entity.Star_collection;
import com.app.entity.View_show_dao;
import com.app.entity.Person_main_page_dao;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sackcentury.shinebuttonlib.ShineButton;

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
    private TextView title_view;
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

    private View_show_dao viewshow_dao;
    private TextView content_textview;
    private ShineButton star_button;
    private ShineButton collection_button;
    LoadingDialogUtil loadingDialogUtil;
//    private Long userID;
    private Long view_show_id = 0L;
//    String user = "";
    private String follower;
    private String followed;
    private static SharedPreferencesHelper helper;
    static {
        helper = new SharedPreferencesHelper(MainApplication.getContext(),"loginState");
    }

    //===================
    Observer<ResponseResult<View_show_dao>> view_show_observer;
    Observer<ResponseResult<String>> star_observer;
    Observer<ResponseResult<String>> collection_observer;
    Observer<ResponseResult<String>> follow_observer;
    Observer<ResponseResult<Star_collection>> star_collection_observer;
    Observer<ResponseResult<Integer>> star_collection_follow;
    //===================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_main_page);
        initData();
        initView();
//        follower = new SharedPreferencesHelper(MainApplication.getContext(),"user")
        follower =  helper.getString("username");//follow者

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

        //============================================================通过ID获取数据
        Intent intent = getIntent();
        view_show_id = intent.getLongExtra("viewID", 1L);

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

        Observer<ResponseResult<View_show_dao>> observer = new Observer<ResponseResult<View_show_dao>>() {
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
            }
            @Override
            public void onError(Throwable e) {
                //错误结果
                Log.e("error", "" + e.getMessage());
                loadingDialogUtil.cancel();
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

            }

        };
        //view_show_id
        HttpMethods.getInstance()
                .getView_show_dao(view_show_id, observer);
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
        title_view = findViewById(R.id.main_page_title);
        main_page_position_name = findViewById(R.id.main_page_position_name);
        personMainPageMoney = findViewById(R.id.PersonMainPageMoney);

        //通过java添加 图片
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        star_button =  findViewById(R.id.person_page_yes_no);
        collection_button = findViewById(R.id.person_page_collection);
    }

    private void initData() {
        loadingDialogUtil = new LoadingDialogUtil(PersonMainPage.this);
        loadingDialogUtil.setCanceledOnTouchOutside(false);
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
                Toast.makeText(PersonMainPage.this,"关注成功",Toast.LENGTH_SHORT).show();
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
/*
  toolbar = (Toolbar) findViewById(R.id.main_page_toolBar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.main_page_collapsing);
        main_top_imageView = (ImageView) findViewById(R.id.main_page_top_imageview);
        title_view = (TextView) findViewById(R.id.main_page_title);

        title_view.setText(title);
        list_dao = new ArrayList<>();
        //data :/person_page/2.png
        //  /person_page/up/1.png
        LinearLayout view = (LinearLayout) findViewById(R.id.main_page_show_content);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        //每个人的图片
        for (int i = 1; i <= 4; ++i) {
            dao = new Person_main_page_dao();
            dao.setUrl("/app/find/up/" + i + ".png");
            content_imageview = new ImageView(this);
            content_imageview.setLayoutParams(lp);
            content_imageview.setAdjustViewBounds(true);
            content_imageview.setPadding(5,10,5,10);
            view.addView(content_imageview);
          //  Glide.with(this).load(MyUrl.getUrl()+dao.getUrl()).into(content_imageview);
            Log.e("e","" + dao.getUrl());
        }
*/