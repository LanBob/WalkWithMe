package com.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.R;
import com.app.Util.LoadingDialogUtil;
import com.app.Util.StringUtil;
import com.app.entity.IsGoodMan;
import com.app.modle.HttpMethods;
import com.app.modle.ResponseResult;
import com.app.view.SwitchView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sendtion.xrichtext.RichTextEditor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class IsGoodManActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText inputUserName;
    private RichTextEditor richText;
    private Button submit;
    private ArrayList<String> mSelectPath;
    private ImageView tocamera;
    private static final int REQUEST_IMAGE = 2;
    private SwitchView sexButtom;
    private boolean isOpen = false;
    private TextView sexName;
    private String sex;
    private String defalut_path;
    private IsGoodMan isGoodMan;
    private EditText inputAge;
    private String userId = "";
    private Observer<ResponseResult<String>> upIsGoodManObserver;
    private LoadingDialogUtil loadingDialogUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.isgoodman);
        userId = StringUtil.getValue("username");
        initView();
        initData();
    }

    private void initData() {
        mSelectPath = new ArrayList<>();
        upIsGoodManObserver = new Observer<ResponseResult<String>>() {
            @Override
            public void onSubscribe(Disposable d) {
                loadingDialogUtil.show();
            }

            @Override
            public void onNext(ResponseResult<String> stringResponseResult) {
                loadingDialogUtil.cancel();
                if(stringResponseResult.getCode() ==1){
                    Toast.makeText(IsGoodManActivity.this,"操作成功",Toast.LENGTH_LONG).show();
                    inputAge.setText("");
                    inputUserName.setText("");
                }else {
                    Toast.makeText(IsGoodManActivity.this,"操作失败",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                loadingDialogUtil.cancel();
            }

            @Override
            public void onComplete() {
                loadingDialogUtil.cancel();
            }
        };

    }

    private void initView() {
        sex = "男";
        inputUserName = findViewById(R.id.inputUserName);
        sexButtom = findViewById(R.id.sexButtom);
        richText = findViewById(R.id.richText);
        submit = findViewById(R.id.submit);
        tocamera = findViewById(R.id.tocamera);
        sexName = findViewById(R.id.sexName);
        inputAge = findViewById(R.id.inputAge);
        sexButtom.setOnClickListener(this);
        submit.setOnClickListener(this);
        tocamera.setOnClickListener(this);

        loadingDialogUtil = new LoadingDialogUtil(IsGoodManActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit:
                String userName = inputUserName.getText().toString().trim();
                String age = inputAge.getText().toString().trim();
                if(userName != null && age!=null &&userName != "" && age!="" && StringUtil.isInteger(age)){
                    List<RichTextEditor.EditData> editList = richText.buildEditData();
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
                    isGoodMan = new IsGoodMan();
                    isGoodMan.setAge(age);
                    isGoodMan.setIntroduce(content.toString());
                    isGoodMan.setUserName(userName);
                    isGoodMan.setScore(0);
                    if(userId =="" || userId == null){
                        userName = "1";
                    }
                    isGoodMan.setUserId(userId);
                    isGoodMan.setSex(sex);

                    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                    String json = gson.toJson(isGoodMan);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), json);

                    HttpMethods.getInstance()
                            .isgoodman(requestBody,files,upIsGoodManObserver);

                    Toast.makeText(IsGoodManActivity.this,isGoodMan.getIntroduce(),Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(IsGoodManActivity.this,"请检查姓名或年龄",Toast.LENGTH_SHORT).show();
                }
                //提交
                break;
            case R.id.sexButtom:
                isOpen = sexButtom.isOpened();
                if(isOpen){
                    sex = "女";
                    sexName.setText(sex);
                }else {
                    sex = "男";
                    sexName.setText(sex);
                }
                break;
            case R.id.tocamera:
                MultiImageSelector multiImageSelector = MultiImageSelector.create();

                multiImageSelector.showCamera(true) // 是否显示相机. 默认为显示
                        .count(9) // 最大选择图片数量, 默认为9. 只有在选择模式为多选时有效
                        .multi() // 多选模式, 默认模式;
                        .origin(mSelectPath) // 默认已选择图片. 只有在选择模式为多选时有效
                        .start(IsGoodManActivity.this, REQUEST_IMAGE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            for (String s : mSelectPath) {
                if (s != null)
                    richText.addImageViewAtIndex(richText.getLastIndex(), s);
                richText.addEditTextAtIndex(richText.getLastIndex(), "");
            }
        }
    }
}
