package com.example.admin.keshe2;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main2Activity extends AppCompatActivity {
    MyHelper myHelper = new MyHelper(this);
    SQLiteDatabase db;
    ContentValues values;
    private ImageView imageView;
    private Uri imageUri;
    private Button go;
    private EditText xm;
    private EditText xh;
    int i=2;
    String text = null;
    String xmm = null;
    String xhh = null;

    private static final int TAKE_PHOTO1 = 11;// 拍照
    private static final int LOCAL_CROP = 12;// 本地图库
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhuce);
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

    private void takePhotoOrSelectPicture(final int i) {
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
        if(i == 0) {
            // 弹出对话框提示用户拍照或者是通过本地图库选择图片
            new AlertDialog.Builder(Main2Activity.this)
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            switch (which) {
                                // 选择了拍照
                                case 0:
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
                                    startActivityForResult(intent, TAKE_PHOTO1);
                                    break;
                                // 调用系统图库
                                case 1:

                                    // 创建Intent，用于打开手机本地图库选择图片
                                    Intent intent1 = new Intent(Intent.ACTION_PICK,
                                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    // 启动intent打开本地图库
                                    startActivityForResult(intent1, LOCAL_CROP);
                                    break;

                            }

                        }
                    }).show();
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    void initView(){
        imageView=(ImageView)findViewById(R.id.image);
        xm= (EditText) findViewById(R.id.xm);
        xh= (EditText) findViewById(R.id.xh);
        go = (Button) findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoOrSelectPicture(0);
            }
        });

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    public void onActivityResult(int req, int resultCode, Intent data) {
        switch (req) {

            case TAKE_PHOTO1:// 拍照

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
                                xhh = xh.getText().toString();
                                String result = FaceAdd.add(text,xhh);
                                JSONObject jsonObj = new JSONObject(result);
                                String error_msg = jsonObj.optString("error_msg");
                                i = zhuce(error_msg);
                                if(i == 1) {
                                    Looper.prepare();
                                    Toast.makeText(Main2Activity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else {
                                    Looper.prepare();
                                    Toast.makeText(Main2Activity.this, "注册失败！请重试", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                System.out.println(i+"iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    // 启动intent，开始裁剪
                    //startActivityForResult(intent, CROP_PHOTO);
                }
                break;
            case LOCAL_CROP:// 系统图库

                if(resultCode == RESULT_OK){
                    // 创建intent用于裁剪图片
                    Intent intent1 = new Intent("com.android.camera.action.CROP");
                    // 获取图库所选图片的uri
                    Uri uri = data.getData();
                    text=getRealPathFromURI(uri);
                    //System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+text);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                xhh = xh.getText().toString();
                                String result = FaceAdd.add(text,xhh);
                                System.out.println(result+"ttttttttttttttttttttttttttttttttttttttttttttttttttttttt");
                                JSONObject jsonObj = new JSONObject(result);
                                String error_msg = jsonObj.optString("error_msg");
                                i = zhuce(error_msg);
                                if(i == 1) {
                                    Looper.prepare();
                                    Toast.makeText(Main2Activity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else {
                                    Looper.prepare();
                                    Toast.makeText(Main2Activity.this, "注册失败！请重试", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                System.out.println(i+"iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    intent1.setDataAndType(uri,"image/*");
                    //  设置裁剪图片的宽高
                    intent1.putExtra("outputX", 300);
                    intent1.putExtra("outputY", 300);
                    // 裁剪后返回数据
                    intent1.putExtra("return-data", true);
                    // 启动intent，开始裁剪
                    //startActivityForResult(intent1, CROP_PHOTO);
                }
                break;
            default:
                break;
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        Uri uri = contentUri;
        String[] proj = { MediaStore.Images.Media.DATA };

        Cursor actualimagecursor = managedQuery(uri,proj,null,null,null);

        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        actualimagecursor.moveToFirst();

        String img_path = actualimagecursor.getString(actual_image_column_index);

        return  img_path;
    }

    int zhuce(String error_msg){
        if(error_msg.equals("SUCCESS"))
        {

            xmm = xm.getText().toString();
            xhh = xh.getText().toString();
            values = new ContentValues();
            values.put("id",xhh);
            values.put("name",xmm);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            values.put("timestamp",df.format(new Date()));
            //System.out.println(df.format(new Date())+"111111111111111111111111111111111111111111111");// new Date()为获取当前系统时间
            db.insert("zhuce", null, values);
            db = myHelper.getReadableDatabase();
            Cursor cursor = db.query("zhuce", null, null, null, null, null,null);
            cursor.moveToFirst();
            System.out.println(cursor.getString(0)+cursor.getString(1)+cursor.getString(2));
            while(cursor.moveToNext())
                System.out.println(cursor.getString(0)+cursor.getString(1)+cursor.getString(2));
            cursor.close();
            db.close();
            return 1;
        }
        return -1;
    }

}
