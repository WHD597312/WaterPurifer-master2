package com.peihou.waterpurifer.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.adapter.GlideImageLoader;
import com.peihou.waterpurifer.adapter.ImagePickerAdapter;
import com.peihou.waterpurifer.base.BaseActivity;
import com.peihou.waterpurifer.base.MyApplication;
import com.peihou.waterpurifer.dialog.SelectDialog;
import com.peihou.waterpurifer.util.HttpUtils;
import com.peihou.waterpurifer.util.NetWorkUtil;
import com.peihou.waterpurifer.util.ToastUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class CallBackActivity extends BaseActivity/* implements ImagePickerAdapter.OnRecyclerViewItemClickListener*/{
    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
//    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
//    @BindView(R.id.recyclerView1)
//    RecyclerView recyclerView;
    @BindView(R.id.et_content)
    EditText et_content;/**编辑内容*/
    @BindView(R.id.textView)
    TextView textView;
//    private int maxImgCount = 3;               //允许选择图片最大数
    private ImagePickerAdapter adapter;
    private ProgressDialog progressDialog;
    Unbinder unbinder;
    MyApplication application;
    SharedPreferences preferences;
    String userId ;
    @Override
    public void initParms(Bundle parms) {


    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_callbake;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        progressDialog = new ProgressDialog(this);
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        userId = preferences.getString("userId","");
//        initImagePicker();
//        selImageList = new ArrayList<>();
//        adapter = new ImagePickerAdapter(this, selImageList, maxImgCount);
//        adapter.setOnItemClickListener(this);
//        progressDialog = new ProgressDialog(this);
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setAdapter(adapter);
        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("CharSequence","-->"+s);
                int len=s.length();
                if (len<=200){
                    textView.setText(len+"/200");
                }else {
                    Toast.makeText(CallBackActivity.this,"你已超过输入的范围",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    CallBackAsyncTask task;
    @OnClick({R.id.back,R.id.btn_submit})
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_submit:
                String description = et_content.getText().toString();
                if (TextUtils.isEmpty(description)) {
                    ToastUtil.showShort(this, "请输入反馈意见");
                    break;
                }
                Map<String, Object> param = new HashMap<>();
                param.put("userId", userId);
                param.put("description", description);

                    boolean isConn = NetWorkUtil.isConn(MyApplication.getContext());
                    if (isConn) {
                        showProgressDialog("正在上传，请稍后。。。");
                        task = new CallBackAsyncTask();
                        task.execute(param);
                        new Thread() {
                            public void run() {
                                try {

                                    task.get(5, TimeUnit.SECONDS);
                                } catch (InterruptedException e) {
                                } catch (ExecutionException e) {
                                } catch (TimeoutException e) {
                                    Message message = new Message();
                                    message.obj = "TimeOut";
                                    handler.sendMessage(message);
                                }
                            }
                        }.start();
                    } else {
                        ToastUtil.showShort(this, "无网络可用，请检查网络");
                    }
                break;
        }
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ("TimeOut".equals(msg.obj)){
                if (progressDialog!=null&&progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(CallBackActivity.this,"请求超时,请重试",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @SuppressLint("StaticFieldLeak")
    class CallBackAsyncTask extends AsyncTask<Map<String,Object>,Void,String>{

        @SafeVarargs
        @Override
        protected final String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String ,Object> prarms = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/user/feedbackProblem",prarms);
            Log.e("back", "--->"+result);
            if (!ToastUtil.isEmpty(result)){
                try {
                    JSONObject jsonObject= new JSONObject(result);
                    code = jsonObject.getString("returnCode");
//                    JSONObject returnData = jsonObject.getJSONObject("returnData");


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return code;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

                switch (s) {

                    case "100":
                        if (progressDialog!=null&&progressDialog.isShowing())
                            progressDialog.dismiss();

                        ToastUtil.showShort(CallBackActivity.this, "提交成功");
                        finish();
                        break;
                    default:
                        if (progressDialog!=null&&progressDialog.isShowing())
                            progressDialog.dismiss();
                        ToastUtil.showShort(CallBackActivity.this, "提交失败，请重试");

                        break;

            }
        }
    }


    //显示dialog
    public void showProgressDialog(String message) {

        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }





    /*  private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(false);                      //显示拍照按钮
        imagePicker.setCrop(true);                            //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(3);              //选中数量限制
        imagePicker.setMultiMode(false);                      //多选
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

  @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case IMAGE_ITEM_ADD:
                List<String> names = new ArrayList<>();
                names.add("拍照");
                names.add("相册");
                showDialog(new SelectDialog.SelectDialogListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0: // 直接调起相机
                                //打开选择,本次允许选择的数量
                                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                                Intent intent = new Intent(CallBackActivity.this, ImageGridActivity.class);
                                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS,true); // 是否是直接打开相机
                                startActivityForResult(intent, REQUEST_CODE_SELECT);
                                break;
                            case 1:
                                //打开选择,本次允许选择的数量
                                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                                Intent intent1 = new Intent(CallBackActivity.this, ImageGridActivity.class);
                                startActivityForResult(intent1, REQUEST_CODE_SELECT);
                                break;
                            default:
                                break;
                        }
                    }
                }, names);
                break;
            default:
                //打开预览
                Intent intentPreview = new Intent(this, ImagePreviewDelActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) adapter.getImages());
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS,true);
                startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null){
                    int size=images.size();
                    textView2.setText(size+"/3");
                    selImageList.addAll(images);
                    adapter.setImages(selImageList);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null){
                    selImageList.clear();
                    selImageList.addAll(images);
                    int size=images.size();
                    textView2.setText(size+"/3");
                    adapter.setImages(selImageList);
                }
            }
        }
    }*/
   /* private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(this, R.style.MyDialog, listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler!=null)
            handler.removeCallbacksAndMessages(null);
    }
}
