package com.app.Util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.R;


public class LoadingDialogUtil extends Dialog {
    private TextView tv;

    public LoadingDialogUtil(Context context) {
        super(context, R.style.LoadingDialogStyle);
    }

    private LoadingDialogUtil(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        tv = (TextView)this.findViewById(R.id.tv);
        tv.setText("正在上传中...");
        LinearLayout linearLayout = (LinearLayout)this.findViewById(R.id.LLinearLayout);

        linearLayout.getBackground().setAlpha(210);
    }
}

