package com.app.Util;

import android.util.Log;

public class LogUtil {

    public static final int OUT = 1;

    public static final int UPLINE = 0;


    private static final int level = 1;

    public static void v(String mess) {
        if (level >= OUT) {
            Log.v(getTag(), mess);
        }
    }

    public static void d(String mess) {
        if (level >= OUT) {
            Log.d(getTag(), mess);
        }
    }

    public static void i(String mess) {
        if (level >= OUT) {
            Log.i(getTag(), mess);
        }
    }

    public static void w(String mess) {
        if (level >= OUT) {
            Log.w(getTag(), mess);
        }
    }

    public static void e(String mess) {
        if (level >= OUT) {
            Log.e(getTag(), mess);
        }
    }

    private static String getTag() {
        StackTraceElement[] trace = new Throwable().fillInStackTrace()
                .getStackTrace();
        String callingClass = "";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(LogUtil.class)) {
                callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass
                        .lastIndexOf('.') + 1);
                break;
            }
        }
        return callingClass;
    }

}
