package com.app.activity;


import android.content.Context;
import android.content.Intent;
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
import com.app.Util.SharedPreferencesHelper;
import com.app.Util.StringUtil;
import com.app.Fragments.MainActivity;
import com.app.MainApplication;
import com.app.modle.HttpMethods;
import com.app.modle.ResponseResult;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class Login extends AppCompatActivity implements View.OnClickListener {


    public static Context context;
    private EditText et_name;
    private EditText et_password;
    private Button mLoginBtn;
    private CheckBox checkBox_password;
    private CheckBox checkBox_login;
    private TextView textView;
    private int code = 0;

    //----------------------------------------注册相关
    private Button btn_login_1;
    private TextView add_count;
    private FrameLayout frameLayout;
    private FrameLayout frameLayout1;

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
        SharedPreferencesHelper helper = new SharedPreferencesHelper(this, "setting");

        //判断用户第一次登陆
        if (firstLogin()) {
            checkBox_password.setChecked(false);//取消记住密码的复选框
            checkBox_login.setChecked(false);//取消自动登录的复选框
        }
        //判断是否记住密码
        if (remenberPassword()) {
            String name = helper.getString("name");
            String pass = helper.getString("password");
            checkBox_password.setChecked(true);//勾选记住密码
            et_name.setText("" + name);
            et_password.setText("" + pass);//把密码和账号输入到输入框中
        } else {
            //           setTextName();//把用户账号放到输入账号的输入框中
            String name = helper.getString("name");
            if (name == null) {
                name = "";
            }
            et_name.setText("" + name);
        }

        //判断是否自动登录
        if (helper.getBoolean("autoLogin", false)) {
            checkBox_login.setChecked(true);
            login();//去登录就可以

        }
    }

    private void initView() {
        mLoginBtn = findViewById(R.id.btn_login);//登录按钮
        btn_login_1 = findViewById(R.id.btn_login_1);//注册
        et_name = findViewById(R.id.et_account);//账号
        et_password = findViewById(R.id.et_password);//密码
        checkBox_password = findViewById(R.id.checkBox_password);//记住密码
        checkBox_login = findViewById(R.id.checkBox_login);//自动登录
        add_count = findViewById(R.id.add_count);//注册账号
        frameLayout = findViewById(R.id.login_layout);
        frameLayout1 = findViewById(R.id.login_layout_1);
        mLoginBtn.setOnClickListener(this);
        btn_login_1.setOnClickListener(this);
        et_name.setOnClickListener(this);
        add_count.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login://登录
                //进行登录操作
                login();
                //在这里跳转到完成认证
              //  Intent intent = new Intent(Login.this, Authenticate.class);
                //startActivity(intent);
                break;

            case R.id.add_count://跳转到注册页面
                Log.e("sd", "ff");
                frameLayout1.setVisibility(View.VISIBLE);
                frameLayout.setVisibility(View.GONE);
                break;

            case R.id.btn_login_1://注册
                String username = ((EditText)findViewById(R.id.et_account_1)).getText().toString().trim();
                String pwd = ((EditText)findViewById(R.id.et_password_1)).getText().toString().trim();
                code = 0;
                regist(username,pwd);
                break;
        }
    }

    /**
     * 注册
     * @param username
     * @param pwd
     */
    private void regist(String username,String pwd){
        btn_login_1.setClickable(false);
        Observer<ResponseResult<String>> observer = new Observer<ResponseResult<String>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseResult<String> stringResponseResult) {

                code  = stringResponseResult.getCode();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("ee",e.getMessage());
                btn_login_1.setClickable(true);
            }

            @Override
            public void onComplete() {
                if(code == 1){
                    frameLayout1.setVisibility(View.GONE);
                    frameLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(Login.this,"注册成功，请登录",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(Login.this,"注册失败",Toast.LENGTH_LONG).show();
                }
                code = 0;
                btn_login_1.setClickable(true);
            }
        };
        Map<String,String> m = new HashMap<>();
        m.put("username",username);
        String md5 = StringUtil.toMD5(pwd);
        m.put("password",md5);
        Log.e("md4",md5);
        HttpMethods.getInstance()
                .Login_check(m,observer);
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

        mLoginBtn.setClickable(false);
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
                mLoginBtn.setClickable(true);
                Toast.makeText(Login.this,"啦啦啦，遇到不可知错误啦……",Toast.LENGTH_SHORT).show();
                code = 0;
            }

            @Override
            public void onComplete() {

                //中间是登录操作
                if (code == 1) {
                    //登录操作，在Mysql里面得到了此用户的信息，并通过登录操作
                    showToast("yes" + getAccount() + getPassword() + ".");

                    //保存登录成功状态
                    new SharedPreferencesHelper(MainApplication.getContext(), "in")
                            .putValues(new SharedPreferencesHelper.ContentValue("check_in", "Y"));

                    new SharedPreferencesHelper(MainApplication.getContext(), "user")
                            .putValues(new SharedPreferencesHelper.ContentValue("username", getAccount()));

                    boolean set_or_not = new SharedPreferencesHelper(MainApplication.getContext(),"set_or_not")
                            .getBoolean("set",false);
/**
 * 如果还没有设置个人信息，就跳转到个人信息去，
 */
                    if (set_or_not) {
                        //如果已经设置个人信息,重新加载Minfragment
                        Intent myintent = new Intent(Login.this, MainActivity.class);
                        myintent.putExtra("position", 3);
                        finish();
                        startActivity(myintent);
                    } else {

                        //还没有设置个人信息
                        //跳转到成功登录的页面
                        Intent intent = new Intent(Login.this, EditOwnData.class);
                        intent.putExtra("username", getAccount());
                        startActivity(intent);

                    }
                    loadCheckBoxState(checkBox_password, checkBox_login);
                    mLoginBtn.setClickable(true);

                }else{
                 Toast.makeText(Login.this,"请检查用户名或者密码",Toast.LENGTH_SHORT).show();
                 mLoginBtn.setClickable(true);
                }
                code = 0;
            }
        };
        String username = getAccount();
        String pwd = getPassword();
        String md5 = StringUtil.toMD5(pwd);
        code = 0;
        HttpMethods.getInstance()
                .login_check(username,md5,observer);
    }


    /**
     * 保存按钮的状态值
     */
    public void loadCheckBoxState(CheckBox checkBox_password, CheckBox checkBox_login) {

        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesHelper helper = new SharedPreferencesHelper(this, "setting");

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
        SharedPreferencesHelper helper = new SharedPreferencesHelper(this, "setting");
        boolean remenberPassword = helper.getBoolean("remenberPassword", false);
        return remenberPassword;
    }

    /**
     * 判断是否是第一次登陆
     */
    private boolean firstLogin() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesHelper helper = new SharedPreferencesHelper(this, "setting");
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
        return et_name.getText().toString().trim();//去掉空格
    }

    /**
     * 获取密码
     */
    public String getPassword() {
        return et_password.getText().toString().trim();//去掉空格
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
