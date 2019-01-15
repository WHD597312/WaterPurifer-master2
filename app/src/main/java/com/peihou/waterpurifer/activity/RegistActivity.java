package com.peihou.waterpurifer.activity;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.adapter.RepairListAdapter;
import com.peihou.waterpurifer.base.BaseActivity;
import com.peihou.waterpurifer.base.MyApplication;
import com.peihou.waterpurifer.database.dao.daoImp.EquipmentImpl;
import com.peihou.waterpurifer.pojo.Equipment;
import com.peihou.waterpurifer.util.HttpUtils;
import com.peihou.waterpurifer.util.Mobile;
import com.peihou.waterpurifer.util.NetWorkUtil;
import com.peihou.waterpurifer.util.ToastUtil;
import com.peihou.waterpurifer.util.view.ScreenSizeUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.OnClick;


public class RegistActivity extends BaseActivity {
    MyApplication application;
    @BindView(R.id.et_res_name)
    EditText et_name;
    @BindView(R.id.et_res_user)
    EditText et_pswd1;
    @BindView(R.id.et_res_pswd)
    EditText et_pswd2;
    @BindView(R.id.et_res_dz)
    EditText et_res_dz;
    SharedPreferences preferences;
    private ProgressDialog progressDialog;
    String phone ;
    String password1;
    String password2;
    String adress;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_regist;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        progressDialog = new ProgressDialog(this);
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        application.addActivity(this);


    }

    @Override
    public void doBusiness(Context mContext) {

    }
    RegistAsyncTask task;
    @OnClick({R.id.btn_res_res ,R.id.iv_res_fh})
    @Override
    public void widgetClick(View v) {

        switch (v.getId()) {

            case R.id.btn_res_res:
                phone = et_name.getText().toString().trim();
                password1 = et_pswd1.getText().toString().trim();
                password2 = et_pswd2.getText().toString().trim();
                adress = et_res_dz.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort(this, "账号码不能为空");
                    break;
                } else if (!Mobile.isMobile(phone)) {
                    ToastUtil.showShort(this, "手机号码不合法");
                    break;
                }
                if (TextUtils.isEmpty(password1)) {
                    ToastUtil.showShort(this, "请输入密码");
                    break;
                }
                if (TextUtils.isEmpty(password2)) {
                    ToastUtil.showShort(this, "请再次输入密码");
                    break;
                }
                if (TextUtils.isEmpty(adress)) {
                    ToastUtil.showShort(this, "请输入地址");
                    break;
                }
                if (!password1.equals(password2)) {
                    ToastUtil.showShort(this, "两次密码输入不一致");
                    break;
                }
                if (password1.length()<6||password1.length()>18){
                    ToastUtil.showShort(this,"密码位数应该大于6小于18");
                }else {
                    boolean isConn = NetWorkUtil.isConn(MyApplication.getContext());
                    if (isConn){
                        showProgressDialog("正在注册，请稍后。。。");
                        Map<String, Object> params = new HashMap<>();
                        params.put("phone", phone);
                        params.put("password", password1);
                        params.put("address", adress);
                          task=  new RegistAsyncTask() ;
                          task.execute(params);
                            new Thread(){
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


                    }else {
                        ToastUtil.showShort(this, "无网络可用，请检查网络");
                    }
                }


                break;
            case R.id.iv_res_fh:
                finish();
                break;

        }

    }



    //显示dialog
    public void showProgressDialog(String message) {

        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }
    @SuppressLint("HandlerLeak")
    private  Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ("TimeOut".equals(msg.obj)){
                if (progressDialog!=null&&progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(RegistActivity.this,"请求超时,请重试",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @SuppressLint("StaticFieldLeak")
    class RegistAsyncTask extends AsyncTask<Map<String,Object>,Void,String> {

        @SafeVarargs
        @Override
        protected final String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String, Object> params = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/user/register", params);
            Log.e("back", "--->"+result);
            if (!ToastUtil.isEmpty(result)) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getString("returnCode");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return code;

        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (s) {
                case "10001":
                    if (progressDialog!=null&&progressDialog.isShowing())
                        progressDialog.dismiss();
                    ToastUtil.showShort(RegistActivity.this, "手机号已存在");
                    break;
                case "100":
                    if (progressDialog!=null&&progressDialog.isShowing())
                        progressDialog.dismiss();
                        Intent intent = new Intent(RegistActivity.this, LoginActivity.class);
                        startActivity(intent);
                    ToastUtil.showShort(RegistActivity.this, "注册成功");

                    break;
                default:
                    if (progressDialog!=null&&progressDialog.isShowing())
                        progressDialog.dismiss();
                   ToastUtil.showShort(RegistActivity.this, "注册失败，请重试");

                    break;
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();




    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler!=null)
            handler.removeCallbacksAndMessages(null);
    }

}
