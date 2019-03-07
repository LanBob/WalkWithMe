package com.app.JMS.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.JMS.bean.ChatListBean;

import java.util.List;

public class DBHelper extends SQLiteOpenHelper{

    //带全部参数的构造函数，此构造函数必不可少
    public DBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库sql语句并 执行
        String sql = "create table chatbean(userId varchar(20),count int,time varchar(20))";
        String sql6 = "create table sqlmessage(userId varchar(20),time varchar(20),path varchar(20))";
        db.execSQL(sql);
        db.execSQL(sql6);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



}