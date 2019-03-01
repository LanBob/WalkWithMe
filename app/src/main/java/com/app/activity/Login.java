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

import com.app.R;
//import com.app.Util.SharedPreferencesHelper;
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
    final int count = 60;//倒计时10秒
    private String codeMessage = "";


    //    忘记密码
    private TextView layout_forget_password;

    private int code = 0;
//    private static SharedPreferencesHelper helper = null;
//
//    static {
//        helper = new SharedPreferencesHelper(MainApplication.getContext(), "loginState");
//    }

    //----------------------------------------注册相关
    private Button layout1_regist;
    private TextView add_count;
    private EditText layout1_code;
    private Button layout1_clock;

//    忘记密码，修改密码
    private Button layout4_clock;
    private Button layout4_regist;


    private FrameLayout frameLayout;
    private FrameLayout frameLayout1;
    private FrameLayout frameLayout3;
    private FrameLayout frameLayout4;

    //=========验证码登录
    private TextView messageCode;
    private Button codeButton;
    private Button messageCodeSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        context = this;
        Intent intent = getIntent();
        initView();
        initData();
        String index = intent.getStringExtra("index");
        Log.e("index",index +" ");
        if("4".equals(index)){
            layout_forget_password.performClick();
        }
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

//        String name = helper.getString("name");
//        String pass = helper.getString("password");
        String name = StringUtil.getValue("name");
        String pass = StringUtil.getValue("password");
        //判断曾经是否记住密码
        if ("Y".equals(remenberPassword())) {
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
//        if (helper.getBoolean("autoLogin", false)) {
//            checkBox_login.setChecked(true);
//            login();//去登录就可以
//        }
        //判断是否自动登录
        if("Y".equals(StringUtil.getValue("autoLogin"))){
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
        layout1_clock = findViewById(R.id.layout1_clock);
        layout4_clock = findViewById(R.id.layout4_clock);
        layout4_regist = findViewById(R.id.layout4_regist);
        messageCodeSignIn = findViewById(R.id.messageCodeSignIn);

        layoutLogin.setOnClickListener(this);
        layout1_regist.setOnClickListener(this);
        layout_account.setOnClickListener(this);
        add_count.setOnClickListener(this);
        messageCode.setOnClickListener(this);
        codeButton.setOnClickListener(this);
        layout_forget_password.setOnClickListener(this);
        layout1_clock.setOnClickListener(this);
        layout4_clock.setOnClickListener(this);
        layout4_regist.setOnClickListener(this);
        messageCodeSignIn.setOnClickListener(this);
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
                    regist(username, pwd, messageCode);
                } else {
                    Toast.makeText(Login.this, "请输入数字号码", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.clock://登录界面倒计时并获取验证码
                String messageCodeUserName = ((EditText) findViewById(R.id.messageCodeUserName)).getText().toString().trim();
                String code = ((EditText) findViewById(R.id.code)).getText().toString().trim();
                if ("".equals(messageCodeUserName) || !StringUtil.isMobile(messageCodeUserName)) {
                    Toast.makeText(Login.this, "请输入正确手机号码", Toast.LENGTH_LONG).show();
                    break;
                }
//                if (!StringUtil.isInteger(code) || code == "" || code == null) {
//                    Toast.makeText(Login.this, "1请输入正确验证码", Toast.LENGTH_LONG).show();
//                    break;
//                }
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
                getMessageCode(messageCodeUserName);
                break;
            case R.id.layout1_clock://注册时候获取验证码
                String u = ((EditText) findViewById(R.id.layout1_account)).getText().toString().trim();
                if (StringUtil.isMobile(u)) {
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
                                    layout1_clock.setEnabled(false);
                                    layout1_clock.setTextColor(Color.BLACK);
                                }
                            }).subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Long num) {
                            layout1_clock.setText("剩余" + num + "秒");
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            //回复原来初始状态
                            layout1_clock.setEnabled(true);
                            layout1_clock.setText("发送验证码");
                        }
                    });
                    getMessageCode(u);
                } else {
                    Toast.makeText(Login.this, "请正确输入数字号码", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.layout4_clock://修改密码的获取验证码
                String u4 = ((EditText) findViewById(R.id.layout4_account)).getText().toString().trim();
                if (StringUtil.isMobile(u4)) {
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
                                    layout4_clock.setEnabled(false);
                                    layout4_clock.setTextColor(Color.BLACK);
                                }
                            }).subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Long num) {

                            layout4_clock.setText("剩余" + num + "秒");
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            //回复原来初始状态
                            layout4_clock.setEnabled(true);
                            layout4_clock.setText("发送验证码");
                        }
                    });
                    getMessageCode(u4);//获取验证码
                }else{
                    Toast.makeText(Login.this, "请正确输入数字号码", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.layout4_regist://修改密码按钮
                String username4 = ((EditText) findViewById(R.id.layout4_account)).getText().toString().trim();
                String pwd4 = ((EditText) findViewById(R.id.layout4_password)).getText().toString().trim();
                String messageCode4 = ((EditText) findViewById(R.id.layout4_code)).getText().toString().trim();
                if (StringUtil.isMobile(username4) && StringUtil.isInteger(messageCode4)) {
                    changePassword(username4, pwd4, messageCode4);
                } else {
                    Toast.makeText(Login.this, "请输入数字号码", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.messageCodeSignIn://验证码登录
                String u2 = ((EditText) findViewById(R.id.messageCodeUserName)).getText().toString().trim();
                String code2 = ((EditText) findViewById(R.id.code)).getText().toString().trim();
                if (StringUtil.isMobile(u2) && StringUtil.isInteger(code2)) {
                    loginByMessageCode(u2,code2);
                } else {
                    Toast.makeText(Login.this, "请输入数字号码", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void loginByMessageCode(final String username, String code){
        if(codeMessage.equals(code)){//如果验证码正确
            Observer<ResponseResult<String>> observer = new Observer<ResponseResult<String>>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(ResponseResult<String> stringResponseResult) {
                    if(stringResponseResult.getCode() == 1){
                        Toast.makeText(Login.this, "登录成功", Toast.LENGTH_SHORT).show();
//                        helper.putValues(new SharedPreferencesHelper.ContentValue("isAlreadyLogin", "Y"));
                        StringUtil.putValue("isAlreadyLogin", "Y");
                        StringUtil.putValue("username",username);
//                    new SharedPreferencesHelper(MainApplication.getContext(), "loginState")
//                        helper.putValues(new SharedPreferencesHelper.ContentValue("username",username));
                    }else{
                        Toast.makeText(Login.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }

                @Override
                public void onComplete() {

                    Intent myintent = new Intent(Login.this, MainActivity.class);
                    myintent.putExtra("position", 3);
                    finish();
                    startActivity(myintent);
                    //登录成功后将用户名修改为这个Username

                }
            };
            Map<String,String> map = new HashMap<>();
            map.put("username",username);
            HttpMethods.getInstance()
                    .loginByMessageCode(map,observer);
        }else{
            Toast.makeText(Login.this, "输入的验证码不正确", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 修改密码
     * @param username4
     * @param pwd4
     * @param messageCode4
     */
    private void changePassword(String username4, String pwd4, String messageCode4) {
        if (codeMessage.equals(messageCode4)) {//如果验证码正确了
            Observer<ResponseResult<String>> observer = new Observer<ResponseResult<String>>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(ResponseResult<String> stringResponseResult) {
                    if(stringResponseResult.getCode() == 1){
                        Toast.makeText(Login.this, "成功，请重新登录", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Login.this, "修改失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }

                @Override
                public void onComplete() {
                    Intent myintent = new Intent(Login.this, MainActivity.class);
                    myintent.putExtra("position", 3);
                    finish();
                    startActivity(myintent);
                }
            };
            Map<String,String> map = new HashMap<>();
            map.put("username",username4);
            map.put("password",StringUtil.toMD5(pwd4));
            HttpMethods.getInstance()
                    .changePassword(map,observer);

        }else{
            Toast.makeText(Login.this, "输入的验证码不正确", Toast.LENGTH_SHORT).show();
        }
    }

    //获取验证码并保存在codeMessage
    private void getMessageCode(String messageCodeUserName) {
        //传递进来两个东西：手机号和验证码
        Observer<ResponseResult<String>> observer = new Observer<ResponseResult<String>>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<String> stringResponseResult) {
                if (stringResponseResult.getCode() == 1) {//如果code是0，就可以获取
                    codeMessage = stringResponseResult.getData().toString();//得到短信验证码，暂时保存
                    Toast.makeText(Login.this, "验证码已发送至您的手机，请注意查收", Toast.LENGTH_SHORT).show();
                    Log.e("codeMessage",codeMessage);
                } else {
                    Toast.makeText(Login.this, "无法获取验证码", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        HttpMethods.getInstance()
                .getMessageCode(messageCodeUserName, observer);
    }

    /**
     * 注册
     *
     * @param username
     * @param pwd
     */
    private void regist(String username, String pwd, String messageCode) {
        if (codeMessage.equals(messageCode)) {//如果验证码正确了
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

                    Intent myintent = new Intent(Login.this, MainActivity.class);
                    myintent.putExtra("position", 3);
                    finish();
                    startActivity(myintent);
                }
            };
            Map<String, String> m = new HashMap<>();
            m.put("username", username);
            String md5 = StringUtil.toMD5(pwd);
            m.put("password", md5);
            HttpMethods.getInstance()
                    .Login_check(m, observer);
        } else {
            Toast.makeText(Login.this, "输入的验证码不正确", Toast.LENGTH_SHORT).show();
        }

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
                Log.e("message", getAccount() + " 和" + getPassword());
                e.printStackTrace();
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
                    StringUtil.putValue("isAlreadyLogin", "Y");
                    StringUtil.putValue("username", getAccount());
//                    StringUtil.putValue("isAlreadySetOwnData", "Y");
                    String isAlreadySetOwnData = StringUtil.getValue("isAlreadySetOwnData");
//                    helper.putValues(new SharedPreferencesHelper.ContentValue("isAlreadyLogin", "Y"));

//                    new SharedPreferencesHelper(MainApplication.getContext(), "loginState")
//                    helper.putValues(new SharedPreferencesHelper.ContentValue("username", getAccount()));

                    //是否设置了个人消息
//                    boolean isAlreadySetOwnData = new SharedPreferencesHelper(MainApplication.getContext(),"loginState")
//                    boolean isAlreadySetOwnData = helper.getBoolean("isAlreadySetOwnData", false);
/**
 * 如果还没有设置个人信息，就跳转到个人信息去，
 */
//                    if (isAlreadySetOwnData) {
                    if("Y".equals(isAlreadySetOwnData)){
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
            StringUtil.putValue("remenberPassword", "Y");
            StringUtil.putValue("autoLogin", "Y");
            StringUtil.putValue("password", getPassword());
            StringUtil.putValue("name", getAccount());
//            helper.putValues(
//                    new SharedPreferencesHelper.ContentValue("remenberPassword", true),
//                    new SharedPreferencesHelper.ContentValue("autoLogin", true),
//                    new SharedPreferencesHelper.ContentValue("password", getPassword()),
//                    new SharedPreferencesHelper.ContentValue("name", getAccount()));

        } else if (!checkBox_password.isChecked()) {
            StringUtil.putValue("remenberPassword", "N");
            StringUtil.putValue("autoLogin", "N");
            StringUtil.putValue("password", getPassword());
            StringUtil.putValue("name", getAccount());
            //也没有记住密码，没记住自动登录
//            helper.putValues(
//                    new SharedPreferencesHelper.ContentValue("remenberPassword", false),
//                    new SharedPreferencesHelper.ContentValue("autoLogin", false),
//                    new SharedPreferencesHelper.ContentValue("password", ""),
//                    new SharedPreferencesHelper.ContentValue("name", getAccount()));

        } else if (checkBox_password.isChecked()) {   //如果保存密码，没有自动登录
            //创建记住密码为选中和自动登录是默认不选,保存密码数据
            StringUtil.putValue("remenberPassword", "Y");
            StringUtil.putValue("autoLogin", "N");
            StringUtil.putValue("password", getPassword());
            StringUtil.putValue("name", getAccount());

//            helper.putValues(
//                    new SharedPreferencesHelper.ContentValue("remenberPassword", true),
//                    new SharedPreferencesHelper.ContentValue("autoLogin", false),
//                    new SharedPreferencesHelper.ContentValue("password", getPassword()),
//                    new SharedPreferencesHelper.ContentValue("name", getAccount()));
        }
    }


    /**
     * 判断是否记住密码
     */
    private String remenberPassword() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
//        SharedPreferencesHelper helper = new SharedPreferencesHelper(MainApplication.getContext(), "loginState");
//        boolean remenberPassword = helper.getBoolean("remenberPassword", false);
        return StringUtil.getValue("remenberPassword");
    }

    /**
     * 判断是否是第一次登陆
     */
    private boolean firstLogin() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
//        SharedPreferencesHelper helper = new SharedPreferencesHelper(MainApplication.getContext(), "loginState");
//        boolean first = helper.getBoolean("first", true);//如果是第一个就返回true
        String first = StringUtil.getValue("first");//如果是第一次登录，返回Y
        if ("Y".equals(first)) {//如果是第一次登录
            Log.e("first", "frst");
            //创建一个ContentVa对象（自定义的）设置不是第一次登录，,并创建记住密码和自动登录是默认不选，创建账号和密码为空
//            helper.putValues(new SharedPreferencesHelper.ContentValue("first", true),
//                    new SharedPreferencesHelper.ContentValue("remenberPassword", false),
//                    new SharedPreferencesHelper.ContentValue("autoLogin", false),
//                    new SharedPreferencesHelper.ContentValue("name", ""),
//                    new SharedPreferencesHelper.ContentValue("password", ""));
            StringUtil.putValue("first","N");
            StringUtil.putValue("remenberPassword", "N");
            StringUtil.putValue("autoLogin", "N");
            StringUtil.putValue("name", "");
            StringUtil.putValue("password", "");
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
