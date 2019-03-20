package com.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
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
    private CardView managerNeededScore;
    private LinearLayout manager;
    private ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager);
        actionBar = getSupportActionBar();
        actionBar.setTitle("管理员");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        initView();
        manager.setVisibility(View.GONE);
    }

    private void initView() {
        inputPass = "";
        passWord = findViewById(R.id.passWord);
        checkPassWord = findViewById(R.id.checkPassWord);
        isGoodMan = findViewById(R.id.isGoodMan);
        managerNeededScore = findViewById(R.id.managerNeededScore);
        manager = findViewById(R.id.manager);
        top = findViewById(R.id.top);
        top.setVisibility(View.VISIBLE);
        checkPassWord.setOnClickListener(this);
        isGoodMan.setOnClickListener(this);
        managerNeededScore.setOnClickListener(this);
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
                        manager.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(ManagerEntity.this, "验证失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.isGoodMan:
                Intent intent = new Intent(ManagerEntity.this,ManagerGoodManActivity.class);
                startActivity(intent);
                break;
            case R.id.managerNeededScore:
//
                Intent intent1 = new Intent(ManagerEntity.this,ManagerNeededScoreActivity.class);
                startActivity(intent1);
                break;
        }
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
}
