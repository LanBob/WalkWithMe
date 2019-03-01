package com.app.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.app.R;
import com.app.entity.IsGoodMan;
import com.app.view.TurnCardListView;
import com.spark.submitbutton.SubmitButton;


/**
 * 导游验证介绍
 */
public class Authenticate extends AppCompatActivity implements View.OnClickListener {

    private SubmitButton button;
    TurnCardListView list;
    int[] colors = {0xffFF9800, 0xff3F51B5, 0xff673AB7, 0xff006064};
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
                Log.e("count ",count +" ");
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
                ((TextView) child.findViewById(R.id.pos)).setText("第" + (position + 1) +"步");
                child.findViewById(R.id.image).setBackgroundColor(colors[position]);

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
                if(count < allPage){
                    Toast.makeText(Authenticate.this,"请先完成阅读",Toast.LENGTH_SHORT).show();
                }else {
//                    t跳转
                    Intent intent = new Intent(Authenticate.this, IsGoodManActivity.class);
                    startActivity(intent);
                }
                break;
        }

    }
}
