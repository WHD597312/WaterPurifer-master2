package com.peihou.waterpurifer.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.base.BaseActivity;
import com.peihou.waterpurifer.base.MyApplication;
import com.peihou.waterpurifer.util.view.ScreenSizeUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import butterknife.BindView;
import butterknife.OnClick;


public class UserActivity extends BaseActivity {
    MyApplication application;

    @BindView(R.id.rl_user_address)
    RelativeLayout rl_user_address;
    @BindView(R.id.tv_user_adresxq)
    TextView tv_user_adresxq;
    @BindView(R.id.tv_user_phone)
    TextView tv_user_phone;
    SharedPreferences preferences;
    String phone ;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_user;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        phone = preferences.getString("phone","");
        tv_user_phone.setText(phone);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    @SuppressLint("ApplySharedPref")
    @OnClick({R.id.iv_main_memu,R.id.rl_user_cpass,R.id.rl_user_pic,R.id.rl_user_address ,R.id.bt_user_finish})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_main_memu:
                finish();
                break;

            case R.id.rl_user_cpass:
                startActivity(ChangpassActivity.class);
                break;

            case R.id.rl_user_pic:
//                customDialog();//自定义头像
                break;
            case R.id.rl_user_address:
                startActivity(ChangAddressActivity.class);
                break;
            case R.id.bt_user_finish:
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                startActivity(LoginActivity.class);
                break;
        }
    }







    /**
     * 自定义对话框
     */
    private void customDialog() {
        final Dialog dialog = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_pic, null);
        RelativeLayout rl_dia_pz = (RelativeLayout) view.findViewById(R.id.rl_dia_pz);
        RelativeLayout rl_dia_bd = (RelativeLayout) view.findViewById(R.id.rl_dia_bd);
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(true);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        rl_dia_pz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    //在此处添加你的按键处理 xxx
                    if (Build.VERSION.SDK_INT >= 23) {
                        //android 6.0权限问题
                        if (ContextCompat.checkSelfPermission(UserActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(UserActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(UserActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERAPRESS);
                        } else {
                            startCamera();
                        }
                    } else {
                        startCamera();
                    }
                dialog.dismiss();
                }

        });
        rl_dia_bd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    //android 6.0权限问题
                    if (ContextCompat.checkSelfPermission(UserActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(UserActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(UserActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, ICONPRESS);
                    } else {
                        startGallery();
                    }

                } else {
                    startGallery();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    final static int CAMERA = 1;//拍照
    final static int ICON = 2;//相册
    final static int CAMERAPRESS = 3;//拍照权限
    final static int ICONPRESS = 4;//相册权限
    final static int PICTURE_CUT = 5;//剪切图片
    private static final String TAG = "RoomContentActivity";
    private Uri outputUri;//裁剪完照片保存地址
    Uri imageUri; //图片路径
    File imageFile; //图片文件
    String imagePath;
    private boolean isClickCamera;//是否是拍照裁剪

    //拍照
    public void startCamera() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        imageFile = new File(getExternalCacheDir(), "background3.png");

        try {
            if (imageFile.exists()) {
                imageFile.delete();
            }
            imageFile.createNewFile();
        } catch (IOException e) {

            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT < 24) {
            imageUri = Uri.fromFile(imageFile);
        } else {
            //Android 7.0系统开始 使用本地真实的Uri路径不安全,使用FileProvider封装共享Uri
            //参数二:fileprovider绝对路径 com.dyb.testcamerademo：项目包名

            imageUri = FileProvider.getUriForFile(UserActivity.this, "com.peihou.waterpurifer.fileprovider", imageFile);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //照相
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //指定图片输出地址
        startActivityForResult(intent, CAMERA); //启动照相
    }

    //打开相册
    public void startGallery() {
        Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        startActivityForResult(intent1, ICON);
    }

    /**
     * 裁剪图片
     */
    private void cropPhoto(Uri uri) {
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        // 创建File对象，用于存储裁剪后的图片，避免更改原图
        File file = new File(getExternalCacheDir(), "crop_image2.jpg");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        outputUri = Uri.fromFile(file);
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        //裁剪图片的宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("crop", "true");//可裁剪
        // 裁剪后输出图片的尺寸大小
//        intent.putExtra("outputX", 150);
//        intent.putExtra("outputY", 150);
        intent.putExtra("scale", true);//支持缩放
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//输出图片格式
        intent.putExtra("noFaceDetection", true);//取消人脸识别
        startActivityForResult(intent, PICTURE_CUT);
    }

    // 4.4及以上系统使用这个方法处理图片 相册图片返回的不再是真实的Uri,而是分装过的Uri
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        cropPhoto(uri);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        File file;

        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", "requestCode" + requestCode + "resultCode" + resultCode);
        switch (requestCode) {
            case CAMERA:
                if (resultCode == RESULT_OK) {
                    cropPhoto(imageUri);
                }
                break;
            case ICON:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            case PICTURE_CUT://裁剪完成
                isClickCamera = true;
                Bitmap bitmap2 = null;
                try {
                    if (isClickCamera) {
                        bitmap2 = BitmapFactory.decodeStream(getContentResolver().openInputStream(outputUri));
                    } else {
                        bitmap2 = BitmapFactory.decodeFile(imagePath);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (bitmap2 == null) {
                    break;
                }
//                File file2 = BitmapCompressUtils.compressImage(bitmap2);
//                upImage(file2);
                break;
        }
    }
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        imagePath = getImagePath(uri, null);
        cropPhoto(uri);
    }


}
