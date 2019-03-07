package com.app.JMS.util;

import android.util.Log;

public class LogUtil {
    public static final int OUT = 1;

    public static final int UPLINE = 0;

    private static final int level = 1;

    public static void d(String msg) {
        if (level >= OUT)
            Log.d("chatui", msg);
    }
}
