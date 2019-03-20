package com.app.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.app.R;
import com.app.entity.IsGoodMan;
import com.app.view.TurnCardListView;
//import com.spark.submitbutton.SubmitButton;


/**
 * 导游验证介绍
 */
public class Authenticate extends AppCompatActivity implements View.OnClickListener {

    private Button button;
    TurnCardListView list;
    int[] colors = {0xffFF9800, 0xff3F51B5, 0xff673AB7, 0xff006064};
    String[] title = {"完善自己的基本信息，收到通过信息后，进入“我的”-“申请成为导游”进行第二步的填写。\n" +
            "（基本信息包括：姓名，年龄，性别，所在地，学历，工作（有就写是什么工作就职单位证明拍照上传，无就选无），" +
            "有无驾照（有就驾照拍照上传），兴趣爱好，身份证号（身份证正反两面拍照上传））",

            "描述一个自己作为导游的旅行规划，可通过文字，图片，视频等方式上传分享。要求规划必须真实且自己能够实际操作的。",
            "1.系统会自动发送一份与你同一所在地的旅行策划，请你根据真实性，可玩性，安全性等进行打分提交。\n" +
                    "2.在上一步的策划经过其他申请者的打分，当分数≥70才可通过验证\n" +
                    "仅完成上述两个步骤后才可进入第四步\n" +
                    "※第三部完成进度可以通过“我的”-“我的旅行”中进行查看",
            "通过第三步相互验证后，系统助手会发送安全学习网站。申请者必须点进链接观看完成视频后才可最终成为本平台导游。" +
                    "※系统后台会进行监测，务必在无倍速，无快进，无静音条件下100%观看视频，才可通过。"
    };
    private int allPage = 0;
    private int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.turcard);
        button = findViewById(R.id.button);
        button.setOnClickListener(this);
        list = findViewById(R.id.card_list);
        allPage = colors.length;

        list.setOnTurnListener(new TurnCardListView.OnTurnListener() {
            @Override
            public void onTurned(int position) {
                count++;
                Log.e("count ", count + " ");
            }
        });


        list.setAdapter(new BaseAdapter() {

            @Override
            public int getCount() {
                return colors.length;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View child, ViewGroup parent) {
                child = LayoutInflater.from(parent.getContext()).inflate(R.layout.turcard_item_message, parent, false);

                //设置第几题的数字，中间的数字第一页，可以改为步骤
                ((TextView) child.findViewById(R.id.pos)).setText("第" + (position + 1) + "步");
                child.findViewById(R.id.image).setBackgroundColor(colors[position]);
                ((TextView) child.findViewById(R.id.test_title)).setText(title[position]);

                //下一题
                child.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.turnTo(list.getPosition() + 1);
                    }
                });

                return child;
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                if (count < allPage) {
                    Toast.makeText(Authenticate.this, "请先完成阅读", Toast.LENGTH_SHORT).show();
                } else {
//                    t跳转
                    Intent intent = new Intent(Authenticate.this, IsGoodManActivity.class);
                    startActivity(intent);
                }
                break;
        }

    }
}
