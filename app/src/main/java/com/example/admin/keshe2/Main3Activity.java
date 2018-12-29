package com.example.admin.keshe2;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

public class Main3Activity extends AppCompatActivity {
    MyHelper myHelper = new MyHelper(this);
    SQLiteDatabase db;
    ContentValues values;
    private EditText xh;
    private Button time;
    private Button chaxun;
    String xhh = null;
    String timee = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chaxun);
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
        //xhh = xh.getText().toString();
        Calendar ca = Calendar.getInstance();
        final int mYear = ca.get(Calendar.YEAR);
        final int mMonth = ca.get(Calendar.MONTH);
        final int mDay = ca.get(Calendar.DAY_OF_MONTH);
        time = (Button) findViewById(R.id.time);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Main3Activity.this, onDateSetListener, mYear, mMonth, mDay).show();
            }
        });
        chaxun = (Button) findViewById(R.id.chaxun);
        chaxun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xhh = xh.getText().toString();
                timee = time.getText().toString();
                select(xhh,timee);
            }
        });
    }

        private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;
            String days;
            if (mMonth + 1 < 10) {
                if (mDay < 10) {
                    days = new StringBuffer().append(mYear).append("-").append("0").
                            append(mMonth + 1).append("-").append("0").append(mDay).toString();
                } else {
                    days = new StringBuffer().append(mYear).append("-").append("0").
                            append(mMonth + 1).append("-").append(mDay).toString();
                }

            } else {
                if (mDay < 10) {
                    days = new StringBuffer().append(mYear).append("-").
                            append(mMonth + 1).append("-").append("0").append(mDay).toString();
                } else {
                    days = new StringBuffer().append(mYear).append("-").
                            append(mMonth + 1).append("-").append(mDay).toString();
                }

            }
            time.setText(days);
        }
    };

    void select(String xh,String time){
        values = new ContentValues();
        String d1 = "'"+time+" 00:00:00'";
        String d2 = "'"+time+" 23:59:59'";
        String b = " AND timestamp>=? AND";
        String c = " timestamp<=?";
        String selection = "id=?"+b+c;//+" AND timestamp >= "+d1+" AND timestamp <= "+d2;
        String[] selectionArgs={xh,d1,d2};
        db = myHelper.getReadableDatabase();
        //Cursor cursor = db.query("qiandao", null, selection, selectionArgs, null, null,null);
        Cursor cursor = db.rawQuery("select * from qiandao where id=?",new String[]{xh});
        String t = "";
        if(cursor == null) {
            cursor.moveToFirst();
            do {
                System.out.println(cursor.getString(0) + cursor.getString(1));
                t = t + cursor.getString(0) + " " + cursor.getString(1) + "\n";
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
        }
        else
            t = "无签到记录";
        AlertDialog dialog;
        dialog = new AlertDialog.Builder(this).setTitle("签到记录")
                .setMessage(t)
                .setPositiveButton("确定",null)
                .create();
        dialog.show();
    }

}
