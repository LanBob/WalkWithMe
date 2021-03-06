package com.app.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.JMS.util.PictureFileUtil;
import com.app.R;
import com.app.Util.CityUtil;
import com.app.Util.LoadingDialogUtil;
import com.app.Util.LogOutUtil;
import com.app.Util.StringUtil;
import com.app.modle.HttpMethods;
import com.app.modle.ResponseResult;
import com.app.entity.View_show_dao;
import com.app.view.SwitchView;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.sendtion.xrichtext.RichTextEditor;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;


public class EditActivity extends AppCompatActivity {
    private File file;

    private ArrayList<String> mSelectPath;
    private ImageView imageButton;
    private ActionBar actionBar;
    //启动actviity的请求码
    private static final int REQUEST_IMAGE = 2;
    private RichTextEditor richTextEditor;
    private TextView type_choose_textView;
    private TextView position_textView;
    private TextView save_textView;
    private EditText editMoney;

    private String userId;

    private View_show_dao viewshow_dao;
    LoadingDialogUtil loadingDialogUtil;
    private EditText editText;
    String items[] = new String[]{"风景", "摄影", "手工", "人文", "养生", "节日", "风景", "其他"};
    int items_int[] = new int[]{1, 2, 3, 4, 5, 6, 7, 8};


    //=====================================
    private OptionsPickerView pvOptions;
    ArrayList<String> provinceBeanList = new ArrayList<>();
    ArrayList<List<String>> cityList = new ArrayList<>();
    ArrayList<List<List<String>>> districtList = new ArrayList<>();
    //=====================================

    //    =============================新增四个模块
    private EditText inputDetailAddress;
    private EditText inputRouteAddress;
    private SwitchView switchViewEat;
    private SwitchView switchViewLive;

    private String eat = "";
    private String live = "";
    private String defalut_path;
    private String city = "";
//    =============================新增四个模块


    public static final int REQUEST_CODE_IMAGE = 0000;
    public static final int RESULT_OK           = -1;
    public static final int REQUEST_CODE_VEDIO = 1111;
    public static final int REQUEST_CODE_FILE = 2222;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editxml);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Edit");
        initView();
        initData();
        userId = StringUtil.getValue("username");
        if (pvOptions == null) {
            option_city(EditActivity.this);
        }

        type_choose_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(EditActivity.this).setTitle("请选择分类")
                        .setItems(items, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                type_choose_textView.setText(items[which]);
                                viewshow_dao.setType(items_int[which]);
                            }
                        }).create();
                dialog.show();
            }
        });
        position_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvOptions.show();
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MultiImageSelector multiImageSelector = MultiImageSelector.create();
//
//                multiImageSelector.showCamera(true) // 是否显示相机. 默认为显示
//                        .count(9) // 最大选择图片数量, 默认为9. 只有在选择模式为多选时有效
//                        .multi() // 多选模式, 默认模式;
//                        .origin(mSelectPath) // 默认已选择图片. 只有在选择模式为多选时有效
//                        .start(EditActivity.this, REQUEST_IMAGE);

                PictureFileUtil.openGalleryPic(EditActivity.this, REQUEST_CODE_IMAGE);
            }

        });
        switchViewEat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchViewEat.isOpened()) {
                    eat = "是";
                } else {
                    eat = "否";
                }
            }
        });
        switchViewLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchViewLive.isOpened()) {
                    live = "是";
                } else {
                    live = "否";
                }
            }
        });


        //设置数据
        save_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //=====================获取editText数据
                String title = String.valueOf(editText.getText()).trim();
                String money = String.valueOf(editMoney.getText()).trim();
                String detailAddress = String.valueOf(inputDetailAddress.getText()).trim();
                String routeAddress = String.valueOf(inputRouteAddress.getText()).trim();

                BigDecimal bigDecimal = null;
                Double d = 0.0;
                if (StringUtil.isBigDecimal(money)) {
                    bigDecimal = new BigDecimal(money);
                    d = bigDecimal.doubleValue();
                }

                if (title != null && money != null && !"".equals(title) &&
                        !"".equals(money) && StringUtil.isBigDecimal(money) && d > 0
                        && detailAddress != null && !"".equals(detailAddress)
                        && routeAddress != null && !"".equals(routeAddress)) {

                    loadingDialogUtil = new LoadingDialogUtil(EditActivity.this);
                    loadingDialogUtil.setCanceledOnTouchOutside(false);
                    loadingDialogUtil.show();
                    List<RichTextEditor.EditData> editList = richTextEditor.buildEditData();
                    StringBuffer content = new StringBuffer();
                    List<File> files = new ArrayList<>();

                    for (RichTextEditor.EditData itemData : editList) {
                        if (itemData.inputStr != null) {
                            content.append(itemData.inputStr);
                        } else if (itemData.imagePath != null) {
                            String path_1 = itemData.imagePath.substring(itemData.imagePath.lastIndexOf("/") + 1);
                            if (defalut_path == null) {
                                defalut_path = path_1;
                            }
                            content.append("<img src=\"").append(path_1).append("\"/>");
                            File f = new File(itemData.imagePath);
                            files.add(f);
                        }
                    }
                    if (files.size() < 1 || defalut_path == null) {
                        loadingDialogUtil.cancel();
                        Toast.makeText(EditActivity.this, "至少选择一张图片", Toast.LENGTH_SHORT).show();
                    } else {
                        viewshow_dao.setIntroduce(content.toString());
                        viewshow_dao.setMoney(bigDecimal);
                        viewshow_dao.setUser_id(Long.valueOf(userId));
                        viewshow_dao.setDefaultpath(defalut_path);
                        viewshow_dao.setCity(city);
                        viewshow_dao.setTitle(title);
                        String time = StringUtil.millToTime(System.currentTimeMillis());
                        LogOutUtil.d("time" + time);
                        viewshow_dao.setMyTime(time);
                        viewshow_dao.setDetailAddress(detailAddress);
                        viewshow_dao.setRoute(routeAddress);
                        viewshow_dao.setFirendlyToLive(live);
                        viewshow_dao.setFriendlyToEat(eat);
                        viewshow_dao.setScore(0);


                        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

                        String json = gson.toJson(viewshow_dao);

//                        Log.e("json", json);
                        LogOutUtil.d(json);

                        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), json);

                        Observer<ResponseResult<String>> observer = new Observer<ResponseResult<String>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(ResponseResult<String> stringResponseResult) {
                                if (stringResponseResult.getCode() == 0) {
                                    Toast.makeText(EditActivity.this, "发布失败", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(EditActivity.this, "发布成功", Toast.LENGTH_LONG).show();
                                }
                                loadingDialogUtil.cancel();
                            }

                            @Override
                            public void onError(Throwable e) {
//                                Log.e("e", "error_message" + e.getMessage());
                                LogOutUtil.d(e.getMessage());
                                loadingDialogUtil.cancel();
                            }

                            @Override
                            public void onComplete() {
                                finish();
                            }
                        };

                        HttpMethods.getInstance()
                                .uploadEditText(requestBody, files, observer);
                    }
                } else {
                    Toast.makeText(EditActivity.this, "请检查标题或者价格", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initData() {
        mSelectPath = new ArrayList<>();
        viewshow_dao = new View_show_dao();
        eat = "否";
        live = "否";
    }

    private void initView() {
        imageButton = findViewById(R.id.edit_camera);
        richTextEditor = findViewById(R.id.et_new_content);
        type_choose_textView = findViewById(R.id.type_choose);
        save_textView = findViewById(R.id.save_view);
        position_textView = findViewById(R.id.positiong_choose);
        editText = findViewById(R.id.et_new_title);
        editMoney = findViewById(R.id.editMoney);

        inputDetailAddress = findViewById(R.id.inputDetailAddress);
        inputRouteAddress = findViewById(R.id.inputRouteAddress);
        switchViewEat = findViewById(R.id.switchViewEat);
        switchViewLive = findViewById(R.id.switchViewLive);
    }

    private void option_city(Context context) {

        //  创建选项选择器
        CityUtil.init(this);
        provinceBeanList = CityUtil.getProvinceBeanList();
        cityList = CityUtil.getCityList();
        districtList = CityUtil.getDistrictList();


        pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = provinceBeanList.get(options1)
                        + cityList.get(options1).get(options2)
                        + districtList.get(options1).get(options2).get(options3);
                position_textView.setText(tx);
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //对用户按home icon的处理，本例只需关闭activity，就可返回上一activity，即主activity。
                richTextEditor = null;
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case REQUEST_CODE_IMAGE:
                    Log.e("success","Success" + REQUEST_CODE_IMAGE);
                    // 图片选择结果回调
                    List<LocalMedia> selectListPic = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : selectListPic) {
                        if (media != null)
                            richTextEditor.addImageViewAtIndex(richTextEditor.getLastIndex(), media.getPath());
                        richTextEditor.addEditTextAtIndex(richTextEditor.getLastIndex(), "");
                    }
                    break;
            }
        }


//        if (data != null) {
//            mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
//            for (String s : mSelectPath) {
//                if (s != null)
//                    richTextEditor.addImageViewAtIndex(richTextEditor.getLastIndex(), s);
//                richTextEditor.addEditTextAtIndex(richTextEditor.getLastIndex(), "");
//            }
//        }
    }
}




/*
防止无法插入文字：et_new_content.addEditTextAtIndex(et_new_content.getLastIndex(), "");
插入图片方法：et_new_content.addImageViewAtIndex(et_new_content.getLastIndex(), imagePath);
插入文字方法：et_new_content.addEditTextAtIndex(et_new_content.getLastIndex(), text);

*/

