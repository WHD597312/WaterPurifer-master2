package com.peihou.waterpurifer.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.peihou.waterpurifer.MainActivity;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.base.BaseActivity;
import com.peihou.waterpurifer.base.MyApplication;
import com.peihou.waterpurifer.util.HttpUtils;
import com.peihou.waterpurifer.util.ToastUtil;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.OnClick;


public class ChangpassActivity extends BaseActivity {
    MyApplication application;
    @BindView(R.id.et_change_xg1)
    EditText et_change_xg1;
    @BindView(R.id.et_change_xg2)
    EditText et_change_xg2;
    String password1,password2,phone;
    SharedPreferences preferences;
    private ProgressDialog progressDialog;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_changepass;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        phone = preferences.getString("phone","");
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    ChangpassAsyncTask task;
    @OnClick({R.id.iv_main_memu,R.id.bt_chan_qd})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_main_memu:
                finish();
                break;

            case R.id.bt_chan_qd:
                password1 = et_change_xg1.getText().toString().trim();
                password2 = et_change_xg2.getText().toString().trim();
                if (TextUtils.isEmpty(password1)) {
                    ToastUtil.showShort(this, "请输入原密码");
                    break;
                }
                if (TextUtils.isEmpty(password2)) {
                    ToastUtil.showShort(this, "请输入新密码");
                    break;
                }
                if (password1.equals(password2)){
                    ToastUtil.showShort(this, "新密码与旧密码不能相同");
                    break;
                }
                if (password1.length()<6||password1.length()>18){
                    ToastUtil.showShort(this,"密码位数应该大于6小于18");
                }else {

                    Map<String, Object> params = new HashMap<>();
                    params.put("phone",phone);
                    params.put("password", password2);
                    params.put("oldPassword", password1);

                        task = new ChangpassAsyncTask();
                        task.execute(params);
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
                }
                break;
        }
    }
    //显示dialog
    public void showProgressDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    class ChangpassAsyncTask extends AsyncTask<Map<String,Object>,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("正在上传，请稍后。。。");
        }

        @SafeVarargs
        @Override
        protected final String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> params = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/user/resetPassword",params);
            Log.e("result", "doInBackground: -->"+result );
            if (!ToastUtil.isEmpty(result)) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getString("returnCode");
                    JSONObject returnData = jsonObject.getJSONObject("returnData");
                    if ("100".equals(code)){


                    }


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
                    startActivity(MainActivity.class);
                    progressDialog.dismiss();
                    ToastUtil.showShort(ChangpassActivity.this, "修改成功");
                    break;
                default:
                    progressDialog.dismiss();
                    ToastUtil.showShort(ChangpassActivity.this, "修改失败，请重试");
                    break;
            }
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
                Toast.makeText(ChangpassActivity.this,"请求超时,请重试",Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler!=null)
            handler.removeCallbacksAndMessages(null);
    }
}
