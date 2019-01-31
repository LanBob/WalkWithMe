package com.app.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.app.R;
import com.app.view.TurnCardListView;
import com.app.entity.Check;
import com.spark.submitbutton.SubmitButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 翻页答题页的内容设置
 */
public class Authenticate extends AppCompatActivity implements View.OnClickListener {

    private Check check;
    private List<Check> checkList;
    private RecyclerView recyclerView;
    private List<String> mid;
    private Map<Integer, Integer> choosen;
    private int score;
    private SubmitButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.turcard);
        score = 0;
        checkList = new ArrayList<>();

        choosen = new HashMap<>();

        for (int i = 0; i < 8; ++i) {
            check = new Check();
            mid = new ArrayList<>();
            for (int j = 0; j < 4; ++j) {
                mid.add(((char) ('A' + j)) + ". 第第第第第第第第第第第第" + i + "选项" + (j + 1));
            }
            check.setList(mid);
            check.setTitle("titlee" + (i + 1) + "this is my first time to deturmine .");
            check.setAns(i % 3);
            checkList.add(check);
        }
        //List -> List<String>,title,ans


        findViewById(R.id.newest).setOnClickListener(this);
        findViewById(R.id.newer).setOnClickListener(this);
        findViewById(R.id.older_1).setOnClickListener(this);
        findViewById(R.id.older_2).setOnClickListener(this);
        findViewById(R.id.older_3).setOnClickListener(this);
        button = findViewById(R.id.button);
        button.setOnClickListener(this);

        TurnCardListView list = (TurnCardListView) findViewById(R.id.card_list);

        list.setOnTurnListener(new TurnCardListView.OnTurnListener() {
            @Override
            public void onTurned(int position) {
                Toast.makeText(Authenticate.this, "position = " + position, Toast.LENGTH_SHORT).show();
            }
        });


        list.setAdapter(new BaseAdapter() {
            int[] colors = {0xffFF9800, 0xff3F51B5, 0xff673AB7, 0xff006064, 0xffC51162, 0xffFFEB3B, 0xff795548, 0xff9E9E9E};

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
//                if (child == null) {
                child = LayoutInflater.from(parent.getContext()).inflate(R.layout.turcard_item_message, parent, false);
//                }
                //设置第几题的数字
                ((TextView) child.findViewById(R.id.pos)).setText("" + (position + 1));
                child.findViewById(R.id.image).setBackgroundColor(colors[position]);

                Check dao = checkList.get(position);

                //下一题
                child.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TurnCardListView cardList = (TurnCardListView) findViewById(R.id.card_list);
                        cardList.turnTo(cardList.getPosition() + 1);
                    }
                });
                ((TextView) child.findViewById(R.id.test_title)).setText(dao.getTitle());

                List<String> option_list = dao.getList();

                ((RadioButton) child.findViewById(R.id.first)).setText(option_list.get(0) + "我想和你去收到的是是的发啊个个阿是，但是 的是多少的是的发 个个个");
                ((RadioButton) child.findViewById(R.id.second)).setText(option_list.get(1));
                ((RadioButton) child.findViewById(R.id.third)).setText(option_list.get(2));
                ((RadioButton) child.findViewById(R.id.four)).setText(option_list.get(3));

                ((RadioGroup) child.findViewById(R.id.radio_group)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        int flag = 0;
                        switch (checkedId) {
                            case R.id.first:
                                choosen.put(position, 0);
                                break;
                            case R.id.second:
                                choosen.put(position, 1);
                                break;
                            case R.id.third:
                                choosen.put(position, 2);
                                break;
                            case R.id.four:
                                choosen.put(position, 3);
                                break;
                        }
                    }
                });
                return child;
            }
        });
    }

    boolean isBottom;

    @Override
    public void onClick(View v) {
        TurnCardListView cardList = (TurnCardListView) findViewById(R.id.card_list);
        switch (v.getId()) {
            case R.id.newest:
                //            cardList.setAdapter(cardList.getAdapter());
                cardList.turnTo(0);
                break;
            case R.id.newer:
                cardList.turnBy(-1);
                break;
            case R.id.older_1:
                cardList.turnBy(1);
                break;
            case R.id.older_2:
                cardList.turnBy(2);
                break;
            case R.id.older_3:
                cardList.turnBy(3);
                break;
            case R.id.button:
                //通过choosen和各个答案对比得出成绩
                int len = checkList.size();
                for (int i = 0; i < len; ++i) {
                    if (!choosen.containsKey(i)) {
                        //如果不包含
                        choosen.put(i, -1);
                    }
                    if (choosen.get(i) == checkList.get(i).getAns()) {
                        score += 1;
                    }
                }
                Log.e("总得分", " 分数是" + score);
                //处理答案
                score = 0;
                break;
        }

    }
}
