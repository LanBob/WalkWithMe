package com.app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.JMS.util.LogUtil;
import com.app.JMS.util.PictureFileUtil;
import com.app.R;
import com.app.Util.CityUtil;
import com.app.Fragments.MainActivity;

import com.app.Util.StringUtil;
import com.app.modle.HttpMethods;
import com.app.modle.ResponseResult;
import com.app.entity.Person_setting;
import com.app.view.CircleImageView;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class EditOwnData extends AppCompatActivity {
    private ActionBar actionBar;
    private OptionsPickerView pvOptions;
    private Button button;
    public static final int REQUEST_CODE_IMAGE = 0000;
    private LinearLayout head;
    private CircleImageView edit_head_image;
    private String headImagePath;

    ArrayList<String> provinceBeanList = new ArrayList<>();
    ArrayList<List<String>> cityList = new ArrayList<>();
    ArrayList<List<List<String>>> districtList = new ArrayList<>();


    //================所在地
//    private TextView item_left;
    private TextView position_city;
    //================所在地

    private int mycode = 0;

    private String[] name = {"手机号", "昵称", "性别", "年龄", "个性说明", "爱好"};
    private String[] setHint = {"手机号","使用昵称","男或女","年华","想说的话……","喜欢什么"};

    private String city;

//    private EditText phone_num;
    private EditText alias;
    private EditText sex;
    private EditText age;
    private EditText introduce;
    private EditText love;
//    private static SharedPreferencesHelper helpser = null;
//    static {
//        helper = new SharedPreferencesHelper(MainApplication.getContext(),"loginState");
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editowndata);
        //检查登录状态并跳转

        initview();

//        String in = new SharedPreferencesHelper(MainApplication.getContext(),"loginState")
        String in =  StringUtil.getValue("isAlreadyLogin");
        if("Y".equals(in)){
            actionBar = getSupportActionBar();
            actionBar.setTitle("设置个人信息");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            button = findViewById(R.id.save);

            final Observer<ResponseResult<String >> observer = new Observer<ResponseResult<String>>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(ResponseResult<String> stringResponseResult) {
                    int code = stringResponseResult.getCode();
                    Log.e("result",code + "code ");
                    if(code == 1){//success
//                        helper.putValues(new SharedPreferencesHelper.ContentValue("isAlreadyLogin", "Y"));
//                        helper.putValues(new SharedPreferencesHelper.ContentValue("isAlreadySetOwnData",true));
                        StringUtil.putValue("isAlreadyLogin", "Y");
                        StringUtil.putValue("isAlreadySetOwnData","Y");
                        mycode = 1;
                    }else{
                        Toast.makeText(EditOwnData.this,"遇到不可知错误……",Toast.LENGTH_SHORT).show();
                        mycode = 0;
                    }
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {
                    if(mycode == 1){
                        Intent myintent = new Intent(EditOwnData.this, MainActivity.class);
                        myintent.putExtra("position", 3);
                        finish();
                        startActivity(myintent);
                    }
                }
            };

//          上传
            final Observer<ResponseResult<String>> responseResultObserver = new Observer<ResponseResult<String>>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(ResponseResult<String> headImageResponseResult) {
                    if(headImageResponseResult.getCode() == 1){//成功
                        Toast.makeText(EditOwnData.this,"头像上传成功",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(EditOwnData.this,"头像上传失败",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            };

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //先设置默认信息，防止空值，再进行上传数据
                    Person_setting person_setting = new Person_setting();
//                    String num = String.valueOf(phone_num.getText());
//                    String phone = helper.getString("username");
                    String phone = StringUtil.getValue("username");
                    String else_name = String.valueOf(alias.getText());
                    String s = String.valueOf(sex.getText());
                    int ag = 0;
                    String agag = age.getText().toString();
                    if(agag != null)
                    {
                        ag = Integer.parseInt(agag);
                    }
                    String said = String.valueOf(introduce.getText());
                    String loves = String.valueOf(love.getText());
                    if(agag ==null||phone == null || alias == null || s ==null||said ==null||loves==null || city == null || headImagePath== null){
                        Toast.makeText(EditOwnData.this,"请检查输入是否有空值",Toast.LENGTH_LONG).show();
                    }else{
                        person_setting.setCity(city);
                        person_setting.setPhone_num(phone);
                        person_setting.setAlias(else_name);
                        person_setting.setAge(ag);
                        person_setting.setLove(loves);
                        person_setting.setSex(s);
                        person_setting.setIntroduce(said);

                        /**
                         * 获取Id
                         */
//                        String username = new SharedPreferencesHelper(MainApplication.getContext(),"user")
//                        String username =helper.getString("username");
                        String username = StringUtil.getValue("username");
                        Long id = Long.valueOf(username);
                        if(id == null){
                            Toast.makeText(EditOwnData.this,"请重新登录",Toast.LENGTH_LONG).show();
                        }

                        /**
                         * 设置set_or_not
                         */
//                        new SharedPreferencesHelper(MainApplication.getContext(), "set_or_not")
//                        helper.putValues(new SharedPreferencesHelper.ContentValue("isAlreadySetOwnData", true));
                        StringUtil.putValue("isAlreadySetOwnData", "Y");
                        person_setting.setId(id);
                        File f = new File(headImagePath);
                        List<File> fileList = new ArrayList<>();
                        fileList.add(f);

                        HttpMethods.getInstance()
                                .Person_settting(person_setting,observer);

                        HttpMethods.getInstance()
                                .HeadImageUpload(username,fileList,responseResultObserver);

                        LogUtil.d("personSetting OK");
                    }
                }
            });


            ///============================================================选择器
//            item_left =  findViewById(R.id.editOwnDataitemText);
//            item_left.setText("所在地");
            position_city =  findViewById(R.id.select_province);
            position_city.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pvOptions.show();
                }
            });



            //  创建选项选择器
            CityUtil.init(this);
            provinceBeanList = CityUtil.getProvinceBeanList();
            cityList = CityUtil.getCityList();
            districtList = CityUtil.getDistrictList();


            pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    //返回的分别是三个级别的选中位置
                    String tx = provinceBeanList.get(options1)
                            + cityList.get(options1).get(options2)
                            + districtList.get(options1).get(options2).get(options3);
                    position_city.setText(tx);
                    city = tx;

                }
            }).setSubmitText("确定")//确定按钮文字
                    .setCancelText("取消")//取消按钮文字
                    .setTitleText("城市选择")//标题
                    .setSubCalSize(18)//确定和取消文字大小
                    .setTitleSize(20)//标题文字大小
                    .setTitleColor(Color.BLACK)//标题文字颜色
                    .setSubmitColor(Color.BLACK)//确定按钮文字颜色
                    .setCancelColor(Color.BLACK)//取消按钮文字颜色
                    .setTitleBgColor(0xFF666666)//标题背景颜色 Night mode  #
                    .setBgColor(0xFF333333)//滚轮背景颜色 Night mode
                    .setContentTextSize(18)//滚轮文字大小//.setLinkage(false)//设置是否联动，默认true
                    .setLabels("", "", "")//设置选择的三级单位
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .setCyclic(false, false, false)//循环与否
                    .setSelectOptions(0, 0, 0)  //设置默认选中项
                    .setOutSideCancelable(false)//点击外部dismiss default true
                    .isDialog(true)//是否显示为对话框样式
                    .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                    .build();

            pvOptions.setPicker(provinceBeanList, cityList, districtList);//添加数据源
            ///============================================================


        }else{
            Intent intent  = new Intent(EditOwnData.this,Login.class);
            startActivity(intent);
        }
    }

    private void initview() {
//        phone_num = findViewById(R.id.phone_num);
        head = findViewById(R.id.head);
        edit_head_image = findViewById(R.id.edit_head_image);
        alias = findViewById(R.id.else_name);
        sex = findViewById(R.id.sex);
        age = findViewById(R.id.age);
        introduce = findViewById(R.id.say);
        love = findViewById(R.id.love);
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureFileUtil.openGalleryPic(EditOwnData.this, REQUEST_CODE_IMAGE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //对用户按home icon的处理，本例只需关闭activity，就可返回上一activity，即主activity。
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("e","resultCode " + requestCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_IMAGE:
                    Log.e("e","获取图片路径成功");
                    List<LocalMedia> selectListPic = PictureSelector.obtainMultipleResult(data);
                    String headImage = selectListPic.get(0).getPath();
//                    GlideUtils.loadChatImage(EditOwnData.this,headImagePath,edit_head_image);
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.gray_bg)
                            .error(R.drawable.chat_girl)
                            .priority(Priority.HIGH)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

                    Glide.with(EditOwnData.this)
                            .load(headImage)
                            // .listener(mRequestListener)
                            .apply(options)
                            .into(edit_head_image);

                    LogUtil.d("获取图片路径成功:" + headImagePath);
                    headImagePath = headImage;
                    break;
            }
        }
    }

//    private void sendHeaImage(String phone,String headImagePath) {
//        System.out.println(phone + "  headImagePath" + headImagePath);
//    }

}