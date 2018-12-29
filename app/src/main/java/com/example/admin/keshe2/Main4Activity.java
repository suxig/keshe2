package com.example.admin.keshe2;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

public class Main4Activity extends AppCompatActivity {
    MyHelper myHelper = new MyHelper(this);
    SQLiteDatabase db;
    ContentValues values;
    private EditText xh;
    private Button goo;
    String xhh = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shanchu);
        db = myHelper.getWritableDatabase();
        requestAllPower();
        initView();
    }

    public void requestAllPower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }
    void initView(){
        xh = (EditText) findViewById(R.id.xh);
        goo = (Button) findViewById(R.id.goo);
        goo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        xhh = xh.getText().toString();
                        values = new ContentValues();
                        db = myHelper.getWritableDatabase();
                        int number = db.delete("qiandao","id=?", new String[]{xhh+""});
                        System.out.println(number+"??????????????????????????????");
                        try {
                            String result = FaceDelete.delete(xhh);
                            JSONObject jsonObj = new JSONObject(result);
                            String error_msg = jsonObj.optString("error_msg");
                            if(error_msg.equals("SUCCESS")) {
                                Looper.prepare();
                                Toast.makeText(Main4Activity.this, "注销成功！", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            else {
                                Looper.prepare();
                                Toast.makeText(Main4Activity.this, "注销失败！请重试", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
}
