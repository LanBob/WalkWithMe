package com.app.view;

import android.graphics.Color;

/**
 *
 * @author lusr
 */
public interface IGoodView {

    int DISTANCE = 60;   // 默认移动距离

    int FROM_Y_DELTA = 0; // Y轴移动起始偏移量

    int TO_Y_DELTA = DISTANCE; // Y轴移动最终偏移量

    float FROM_ALPHA = 1.0f;    // 起始时透明度

    float TO_ALPHA = 0.0f;  // 结束时透明度

    int DURATION = 1000; // 动画时长

    String TEXT = ""; // 默认文本

    int TEXT_SIZE = 18; // 默认文本字体大小

    int TEXT_COLOR = Color.BLACK;   // 默认文本字体颜色

}
/*

Methods:
method 方法	description 描述
void setText(String text)	设置文本（optional）
void setTextInfo(String text, int textColor, int textSize)	设置文本信息（optional）
void setImage(int resId)	设置图片（optional）
void setImage(Drawable drawable)	设置图片（optional）
void setDistance(int dis)	设置移动距离
void setTranslateY(int fromY, int toY)	设置Y轴移动属性
void setAlpha(float fromAlpha, float toAlpha)	设置透明度属性
void setDuration(int duration)	设置动画时长
void reset()	重置属性
void show(View v)	展示（required）

https://github.com/venshine/GoodView
 */