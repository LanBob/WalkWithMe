package com.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.R;
import com.app.Util.StringUtil;


public class ManagerEntity extends AppCompatActivity implements View.OnClickListener {

    private String s = "c0097c737e5ab497743201ee38673c2d";
    private EditText passWord;
    private Button checkPassWord;
    private String inputPass = "";
    private LinearLayout top;
    private CardView isGoodMan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager);
        initView();
    }

    private void initView() {
        inputPass = "";
        passWord = findViewById(R.id.passWord);
        checkPassWord = findViewById(R.id.checkPassWord);
        isGoodMan = findViewById(R.id.isGoodMan);
        top = findViewById(R.id.top);
        top.setVisibility(View.VISIBLE);
        checkPassWord.setOnClickListener(this);
        isGoodMan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkPassWord:
                inputPass = passWord.getText().toString().trim();
                if (inputPass != null && inputPass != "") {
                    inputPass = StringUtil.toMD5(inputPass);
                    if (s.equals(inputPass)) {
                        Toast.makeText(ManagerEntity.this, "认定成功", Toast.LENGTH_SHORT).show();
                        top.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(ManagerEntity.this, "验证失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.isGoodMan:
                Intent intent = new Intent(ManagerEntity.this,ManagerGoodManActivity.class);
                startActivity(intent);
                break;
        }
    }
}
