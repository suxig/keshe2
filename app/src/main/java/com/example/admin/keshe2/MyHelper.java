package com.example.admin.keshe2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyHelper extends SQLiteOpenHelper{
    public MyHelper(Context context){
        super(context, "keshe2.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE zhuce (id VARCHAR(20) PRIMARY KEY, name VARCHAR(20) NOT NULL,timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP)");
        db.execSQL("CREATE TABLE qiandao (id VARCHAR(20),timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
