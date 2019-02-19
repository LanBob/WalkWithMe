package com.app.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.JMS.util.LogUtil;
import com.app.R;
import com.app.Util.SharedPreferencesHelper;
import com.app.Util.StringUtil;
import com.app.Fragments.MainActivity;
import com.app.MainApplication;
import com.app.modle.HttpMethods;
import com.app.modle.ResponseResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class Login extends AppCompatActivity implements View.OnClickListener {


    public static Context context;
    private TextView topText;
    //密码登录
    private EditText layout_account;
    private EditText layout_password;
    private Button layoutLogin;
    private CheckBox checkBox_password;
    private CheckBox checkBox_login;


//    忘记密码
    private TextView layout_forget_password;

    private int code = 0;
    private static SharedPreferencesHelper helper = null;

    static {
        helper = new SharedPreferencesHelper(MainApplication.getContext(), "loginState");
    }

    //----------------------------------------注册相关
    private Button layout1_regist;
    private TextView add_count;
    private EditText layout1_code;


    private FrameLayout frameLayout;
    private FrameLayout frameLayout1;
    private FrameLayout frameLayout3;
    private FrameLayout frameLayout4;

    //=========验证码登录
    private TextView messageCode;
    private Button codeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        context = this;
        initView();
        initData();
    }

    //初始化这些账号密码
    private void initData() {

        //获取SharedPreferences对象，使用自定义类的方法来获取对象
//        helper = new SharedPreferencesHelper(MainApplication.getContext(), "loginState");

        //判断用户第一次登陆
        if (firstLogin()) {
            checkBox_password.setChecked(false);//取消记住密码的复选框
            checkBox_login.setChecked(false);//取消自动登录的复选框
        }

        String name = helper.getString("name");
        String pass = helper.getString("password");
        //判断曾经是否记住密码
        if (remenberPassword()) {
            checkBox_password.setChecked(true);//勾选记住密码
            layout_account.setText("" + name);
            layout_password.setText("" + pass);//把密码和账号输入到输入框中
        } else {
            //           setTextName();//把用户账号放到输入账号的输入框中
//            String name = helper.getString("name");
            if (name == null) {
                name = "";
            }
            layout_account.setText("" + name);
        }

        //判断是否自动登录
        if (helper.getBoolean("autoLogin", false)) {
            checkBox_login.setChecked(true);
            login();//去登录就可以
        }
    }

    private void initView() {
        layoutLogin = findViewById(R.id.layout_login);//登录按钮
        layout1_regist = findViewById(R.id.layout1_regist);//注册
        layout_account = findViewById(R.id.layout_account);//账号
        layout_password = findViewById(R.id.layout_password);//密码
        checkBox_password = findViewById(R.id.layout_remember_password);//记住密码
        checkBox_login = findViewById(R.id.layout_auto_login);//自动登录
        add_count = findViewById(R.id.layout_add_count);//注册账号
        frameLayout = findViewById(R.id.login_layout);
        frameLayout1 = findViewById(R.id.login_layout_1);
        frameLayout3 = findViewById(R.id.login_layout_3);
        messageCode = findViewById(R.id.messageCode);
        codeButton = findViewById(R.id.clock);
        layout1_code = findViewById(R.id.layout1_code);
        layout_forget_password = findViewById(R.id.layout_forget_password);
        topText = findViewById(R.id.topText);
        frameLayout4 = findViewById(R.id.login_layout_4);


        layoutLogin.setOnClickListener(this);
        layout1_regist.setOnClickListener(this);
        layout_account.setOnClickListener(this);
        add_count.setOnClickListener(this);
        messageCode.setOnClickListener(this);
        codeButton.setOnClickListener(this);
        layout_forget_password.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_login://登录
                //进行登录操作
                login();
                //在这里跳转到完成认证
                //  Intent intent = new Intent(Login.this, Authenticate.class);
                //startActivity(intent);
                break;

            case R.id.layout_add_count://跳转到注册页面
                Log.e("sd", "ff");
                frameLayout1.setVisibility(View.VISIBLE);
                frameLayout.setVisibility(View.GONE);
                frameLayout3.setVisibility(View.GONE);
                frameLayout4.setVisibility(View.GONE);
                topText.setText("注册页面");
                break;

            case R.id.messageCode://跳转到验证码登录界面
                frameLayout1.setVisibility(View.GONE);
                frameLayout.setVisibility(View.GONE);
                frameLayout3.setVisibility(View.VISIBLE);
                frameLayout4.setVisibility(View.GONE);
                topText.setText("验证码登录");
                break;

            case R.id.layout_forget_password:
                topText.setText("更改密码");
                frameLayout1.setVisibility(View.GONE);
                frameLayout.setVisibility(View.GONE);
                frameLayout3.setVisibility(View.GONE);
                frameLayout4.setVisibility(View.VISIBLE);
                break;

            case R.id.layout1_regist://注册
                String username = ((EditText) findViewById(R.id.layout1_account)).getText().toString().trim();
                String pwd = ((EditText) findViewById(R.id.layout1_password)).getText().toString().trim();
                String messageCode = layout1_code.getText().toString();
                if (StringUtil.isMobile(username) && StringUtil.isInteger(messageCode)) {
                    code = 0;
                    regist(username, pwd,messageCode);
                } else {
                    Toast.makeText(Login.this, "请输入数字号码", Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.clock://倒计时并获取验证码
                String messageCodeUserName = ((EditText) findViewById(R.id.messageCodeUserName)).getText().toString().trim();
                String code = ((EditText) findViewById(R.id.code)).getText().toString().trim();
                if (messageCodeUserName == "" || messageCodeUserName == null || !StringUtil.isMobile(messageCodeUserName)) {
                    Toast.makeText(Login.this, "请输入正确手机号码", Toast.LENGTH_LONG).show();
                    break;
                }
                if (!StringUtil.isInteger(code) || code == "" || code == null) {
                    Toast.makeText(Login.this, "请输入正确验证码", Toast.LENGTH_LONG).show();
                    break;
                }

                final int count = 60;//倒计时10秒
                Observable.interval(0, 1, TimeUnit.SECONDS)
                        .take(count + 1)
                        .map(new Function<Long, Long>() {
                            @Override
                            public Long apply(Long aLong) throws Exception {
                                return count - aLong;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())//ui线程中进行控件更新
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                codeButton.setEnabled(false);
                                codeButton.setTextColor(Color.BLACK);
                            }
                        }).subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long num) {

                        codeButton.setText("剩余" + num + "秒");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        //回复原来初始状态
                        codeButton.setEnabled(true);
                        codeButton.setText("发送验证码");
                    }
                });

//                loginByMessageCode(messageCodeUserName, code);
                break;

        }
    }

    private void loginByMessageCode(String messageCodeUserName, String code) {
        //传递进来两个东西：手机号和验证码
        Map<String, String> map = new HashMap<>();
        map.put("messageCodeUserName", messageCodeUserName);
        map.put("code", code);
        Observer<ResponseResult<String>> observer = new Observer<ResponseResult<String>>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<String> stringResponseResult) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        HttpMethods.getInstance()
                .loginByMessageCode(map, observer);
    }

    /**
     * 注册
     *
     * @param username
     * @param pwd
     */
    private void regist(String username, String pwd,String messageCode) {
        //messageCode验证码
        layout1_regist.setClickable(false);
        Observer<ResponseResult<String>> observer = new Observer<ResponseResult<String>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<String> stringResponseResult) {

                code = stringResponseResult.getCode();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("ee", e.getMessage());
                layout1_regist.setClickable(true);
            }

            @Override
            public void onComplete() {
                if (code == 1) {
                    frameLayout1.setVisibility(View.GONE);
                    frameLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(Login.this, "注册成功，请登录", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Login.this, "注册失败", Toast.LENGTH_LONG).show();
                }
                code = 0;
                layout1_regist.setClickable(true);
            }
        };
        Map<String, String> m = new HashMap<>();
        m.put("username", username);
        String md5 = StringUtil.toMD5(pwd);
        m.put("password", md5);
        m.put("messageCode",messageCode);
        HttpMethods.getInstance()
                .Login_check(m, observer);
    }


    /**
     * 流程:1、检查是否为空值 2、将密码加密成md5 3、code = 0; 4、提交登录请求
     * 5、根据返回修改code值 6、根据code值，如果code=1则跳转到mine页面，并保存登录状态信息（in文件，键为check_in），
     * 保存用户名信息(user文件，键为username)，保存登录设置信息(setting文件)，最后回复code值；如果code为0，提示出错。
     * 7、
     */
    private void login() {
        //登录操作
        if (getAccount().isEmpty()) {
            showToast("你输入的账号为空！");
            return;
        }
        if (getPassword().isEmpty()) {
            showToast("你输入的密码为空！");
            return;
        }

        layoutLogin.setClickable(false);
        Observer<ResponseResult<String>> observer = new Observer<ResponseResult<String>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<String> stringResponseResult) {
                code = stringResponseResult.getCode();
            }

            @Override
            public void onError(Throwable e) {
                layoutLogin.setClickable(true);
                Toast.makeText(Login.this, "啦啦啦，遇到不可知错误啦……", Toast.LENGTH_SHORT).show();
                code = 0;
            }

            @Override
            public void onComplete() {

                //中间是登录操作
                if (code == 1) {
                    //登录操作，在Mysql里面得到了此用户的信息，并通过登录操作

                    //保存登录成功状态
//                    new SharedPreferencesHelper(MainApplication.getContext(), "loginState")//相当于check_in
                    helper.putValues(new SharedPreferencesHelper.ContentValue("isAlreadyLogin", "Y"));

//                    new SharedPreferencesHelper(MainApplication.getContext(), "loginState")
                    helper.putValues(new SharedPreferencesHelper.ContentValue("username", getAccount()));

                    //是否设置了个人消息
//                    boolean isAlreadySetOwnData = new SharedPreferencesHelper(MainApplication.getContext(),"loginState")
                    boolean isAlreadySetOwnData = helper.getBoolean("isAlreadySetOwnData", false);
/**
 * 如果还没有设置个人信息，就跳转到个人信息去，
 */
                    if (isAlreadySetOwnData) {
                        //如果已经设置个人信息,重新加载Minfragment
                        Intent myintent = new Intent(Login.this, MainActivity.class);
                        myintent.putExtra("position", 3);
                        finish();
                        startActivity(myintent);
                    } else {
                        //还没有设置个人信息
                        Toast.makeText(Login.this, "首次登录须设置个人信息", Toast.LENGTH_LONG).show();
                        //跳转到成功登录的页面
                        Intent intent = new Intent(Login.this, EditOwnData.class);
                        intent.putExtra("username", getAccount());
                        startActivity(intent);
                    }
                    loadCheckBoxState(checkBox_password, checkBox_login);
                    layoutLogin.setClickable(true);

                } else {
                    Toast.makeText(Login.this, "请检查用户名或者密码", Toast.LENGTH_SHORT).show();
                    layoutLogin.setClickable(true);
                }
                code = 0;
            }
        };
        String username = getAccount();
        String pwd = getPassword();
        String md5 = StringUtil.toMD5(pwd);
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("md5", md5);
        code = 0;
        HttpMethods.getInstance()
                .login_check(map, observer);
    }


    /**
     * 登录成功后，更新保存按钮的状态值
     */
    public void loadCheckBoxState(CheckBox checkBox_password, CheckBox checkBox_login) {

        //获取SharedPreferences对象，使用自定义类的方法来获取对象
//        SharedPreferencesHelper helper = new SharedPreferencesHelper(MainApplication.getContext(), "loginState");

        //如果设置自动登录
        if (checkBox_login.isChecked()) {
            //自动登录。且记住密码
            helper.putValues(
                    new SharedPreferencesHelper.ContentValue("remenberPassword", true),
                    new SharedPreferencesHelper.ContentValue("autoLogin", true),
                    new SharedPreferencesHelper.ContentValue("password", getPassword()),
                    new SharedPreferencesHelper.ContentValue("name", getAccount()));

        } else if (!checkBox_password.isChecked()) {
            //也没有记住密码，没记住自动登录
            helper.putValues(
                    new SharedPreferencesHelper.ContentValue("remenberPassword", false),
                    new SharedPreferencesHelper.ContentValue("autoLogin", false),
                    new SharedPreferencesHelper.ContentValue("password", ""),
                    new SharedPreferencesHelper.ContentValue("name", getAccount()));

        } else if (checkBox_password.isChecked()) {   //如果保存密码，没有自动登录
            //创建记住密码为选中和自动登录是默认不选,保存密码数据
            helper.putValues(
                    new SharedPreferencesHelper.ContentValue("remenberPassword", true),
                    new SharedPreferencesHelper.ContentValue("autoLogin", false),
                    new SharedPreferencesHelper.ContentValue("password", getPassword()),
                    new SharedPreferencesHelper.ContentValue("name", getAccount()));
        }
    }


    /**
     * 判断是否记住密码
     */
    private boolean remenberPassword() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
//        SharedPreferencesHelper helper = new SharedPreferencesHelper(MainApplication.getContext(), "loginState");
        boolean remenberPassword = helper.getBoolean("remenberPassword", false);
        return remenberPassword;
    }

    /**
     * 判断是否是第一次登陆
     */
    private boolean firstLogin() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
//        SharedPreferencesHelper helper = new SharedPreferencesHelper(MainApplication.getContext(), "loginState");
        boolean first = helper.getBoolean("first", true);//如果是第一个就返回true
        if (!first) {
            Log.e("first", "frst");
            //创建一个ContentVa对象（自定义的）设置不是第一次登录，,并创建记住密码和自动登录是默认不选，创建账号和密码为空
            helper.putValues(new SharedPreferencesHelper.ContentValue("first", true),
                    new SharedPreferencesHelper.ContentValue("remenberPassword", false),
                    new SharedPreferencesHelper.ContentValue("autoLogin", false),
                    new SharedPreferencesHelper.ContentValue("name", ""),
                    new SharedPreferencesHelper.ContentValue("password", ""));
            return true;
        }
        return false;
    }

    /**
     * 获取账号
     */
    public String getAccount() {
        return layout_account.getText().toString().trim();//去掉空格
    }

    /**
     * 获取密码
     */
    public String getPassword() {
        return layout_password.getText().toString().trim();//去掉空格
    }


    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }


}
