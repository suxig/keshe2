package com.example.admin.keshe2;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity{
    MyHelper myHelper = new MyHelper(this);
    SQLiteDatabase db;
    ContentValues values;
    private ImageView imageView;
    private Uri imageUri;
    String text = null;
    String user = null;
    int i = 2;
    String error_msg = null;
    String user_id = null;
    String score = null;

    private static final int TAKE_PHOTO2 = 13;// 拍照

    private Button zhuce;
    private Button qiandao;
    private Button chaxun;
    private Button shanchu;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestAllPower();
        db = myHelper.getWritableDatabase();
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

    private void takePhotoOrSelectPicture(int i) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        CharSequence[] items = {"拍照","图库"};// 裁剪items选项
        if(i == 1) {
            // 创建文件保存拍照的图片
            File takePhotoImage = new File(Environment.getExternalStorageDirectory(), "take_photo_image.jpg");
            try {
                // 文件存在，删除文件
                if (takePhotoImage.exists()) {
                    takePhotoImage.delete();
                }
                // 根据路径名自动的创建一个新的空文件
                takePhotoImage.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 获取图片文件的uri对象
            imageUri = Uri.fromFile(takePhotoImage);
            // 创建Intent，用于启动手机的照相机拍照
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // 指定输出到文件uri中
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            // 启动intent开始拍照
            startActivityForResult(intent, TAKE_PHOTO2);
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    void initView(){
        imageView=(ImageView)findViewById(R.id.image);
        zhuce = (Button) findViewById(R.id.zhuce);
        zhuce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Main2Activity.class);
                startActivity(intent);
                //takePhotoOrSelectPicture(0);
            }
        });
        qiandao = (Button) findViewById(R.id.qiandao);
        qiandao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoOrSelectPicture(1);
            }
        });
        chaxun = (Button) findViewById(R.id.chaxun);
        chaxun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Main3Activity.class);
                startActivity(intent);
                //takePhotoOrSelectPicture(0);
            }
        });
        shanchu = (Button) findViewById(R.id.shanchu);
        shanchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Main4Activity.class);
                startActivity(intent);
                //takePhotoOrSelectPicture(0);
            }
        });
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    public void onActivityResult(int req, int resultCode, Intent data) {
        switch (req) {
            case TAKE_PHOTO2:// 拍照

                if(resultCode == RESULT_OK){
                    // 创建intent用于裁剪图片
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    // 设置数据为文件uri，类型为图片格式
                    intent.setDataAndType(imageUri,"image/*");
                    // 允许裁剪
                    intent.putExtra("scale",true);
                    // 指定输出到文件uri中
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    text = imageUri.toString();
                    text = text.substring(7);
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+text);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String result = FaceSearch.search(text);
                                JSONObject jsonObj = new JSONObject(result);
                                JSONObject result1 = (JSONObject)jsonObj.getJSONObject("result");
                                //JSONObject result2 =  result1.getJSONObject("user_list");
                                JSONArray Jarray  = result1.getJSONArray("user_list");
                                JSONObject result2 = null;
                                for (int i = 0; i < Jarray.length(); i++)
                                {
                                    result2 = Jarray.getJSONObject(i);
                                    System.out.println(result2);
                                }
                                error_msg = jsonObj.optString("error_msg");
                                user_id = result2.optString("user_id");
                                score = result2.optString("score");
                                //System.out.println("user_id:"+user_id+"score:"+score+"222222222222222222222222222222222");
                                //if(error_msg != null && user_id != null & score != null)
                                i = zhuce();
                                if(i == 1) {
                                    Looper.prepare();
                                    Toast.makeText(MainActivity.this, "签到成功！", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else {
                                    Looper.prepare();
                                    Toast.makeText(MainActivity.this, "签到失败！请重试", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Looper.prepare();
                                Toast.makeText(MainActivity.this, "签到失败！请重试", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Looper.prepare();
                                Toast.makeText(MainActivity.this, "签到失败！请重试", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }
                    }).start();
                    // 启动intent，开始裁剪
                    //startActivityForResult(intent, CROP_PHOTO);
                }
                break;

            default:
                break;
        }
    }

    int zhuce(){
        float sourceF = Float.valueOf(score);
        if(error_msg.equals("SUCCESS")&&(sourceF>80.0))
        {
            values = new ContentValues();
            //System.out.println(user_id+"111111111111111111111111111111111111111111111");
            values.put("id",user_id);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            values.put("timestamp",df.format(new Date()));
            //System.out.println(df.format(new Date())+"111111111111111111111111111111111111111111111");// new Date()为获取当前系统时间
            db.insert("qiandao", null, values);
            db = myHelper.getReadableDatabase();
            Cursor cursor = db.query("qiandao", null, null, null, null, null,null);
            cursor.moveToFirst();
            System.out.println(cursor.getString(0)+cursor.getString(1)+"ffffffffffffffffffffffffffffffffffffffffffffffffff");
            while(cursor.moveToNext())
                System.out.println(cursor.getString(0)+cursor.getString(1)+"ssssssssssssssssssssssssssssssssssssssssssssssssssssss");
            cursor.close();
            db.close();
            return 1;
        }
        return -1;
    }

}
