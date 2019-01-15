package com.peihou.waterpurifer.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.peihou.waterpurifer.MainActivity;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.base.BaseActivity;
import com.peihou.waterpurifer.base.MyApplication;
import com.peihou.waterpurifer.database.dao.daoImp.EquipmentImpl;
import com.peihou.waterpurifer.pojo.Equipment;
import com.peihou.waterpurifer.service.MQTTMessageReveiver;
import com.peihou.waterpurifer.util.HttpUtils;
import com.peihou.waterpurifer.util.Mobile;
import com.peihou.waterpurifer.util.NetWorkUtil;
import com.peihou.waterpurifer.util.ToastUtil;
import com.peihou.waterpurifer.util.view.ScreenSizeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.OnClick;


public class LoginActivity extends BaseActivity {
    MyApplication application;

    @BindView(R.id.et_name)
    EditText et_name;
    @BindView(R.id.et_pswd)
    EditText et_pswd;
    @BindView(R.id.image_seepwd)
    ImageView image_seepwd;
    String phone,password,userId;
    SharedPreferences preferences;
    EquipmentImpl equmentDao;
    List<Equipment> equipment;
    private ProgressDialog progressDialog;
    private MQTTMessageReveiver myReceiver;
    boolean isHideFirst;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_login;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        et_name.setText(preferences.getString("phone", ""));
        equmentDao = new EquipmentImpl(getApplicationContext());
        progressDialog = new ProgressDialog(this);

        Log.e("phone", "doInBackground: -->"+preferences.getString("phone","")+"...."+ preferences.getString("password",""));
        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_pswd.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            application.removeAllActivity();//**退出主页面*//*
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void doBusiness(Context mContext) {
      List<Equipment> list =  equmentDao.findAll();
      for (int i =0;i<list.size();i++){

      }

    }

    @Override
    public void widgetClick(View v) {

}

//    class GetTodayWaterAsyncTask extends AsyncTask<Equipment,Void,String>{
//
//
//        @Override
//        protected String doInBackground(Equipment... equipment) {
//            String code = "";
//            String deviceMac = equipment[0].getDeviceMac();
//            String result = HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+"/data/getDeviceAmount?deviceMac="+deviceMac);
//            Log.e("result", "doInBackground: -->"+result );
//            if (!TextUtils.isEmpty(result)){
//                try {
//                    JSONObject jsonObject = new JSONObject(result);
//                    JSONArray jsonArray = jsonObject.getJSONArray("returnData");
//                    int  todayUse = jsonObject.getInt("seven");
//                    equipment[0].setTodayUse(todayUse);
//                    equmentDao.update(equipment[0]);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            return code;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            switch (s){
//                case "100":
//
//                    break;
//
//            }
//        }
//    }

    LoginAsyncTask task;
    @OnClick({R.id.btn_login, R.id.tv_register, R.id.tv_forget_pswd, R.id.image_seepwd})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_register:
                startActivity(new Intent(this, RegistActivity.class));
//                ToastUtil.showShort(this,"注册");


                break;
            case R.id.btn_login:

                phone = et_name.getText().toString().trim();
                password = et_pswd.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort(this, "账号码不能为空");

                    return;
                } else if (!Mobile.isMobile(phone)) {
                    ToastUtil.showShort(this, "手机号码不合法");

                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    ToastUtil.showShort(this, "请输入密码");

                    return;
                }
                boolean isConn = NetWorkUtil.isConn(MyApplication.getContext());
                if (isConn){
                    showProgressDialog("正在登录，请稍后。。。");
                    Map<String, Object> params = new HashMap<>();
                    params.put("phone", phone);
                    params.put("password", password);
                     task =  new LoginAsyncTask() ;
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



                break;
            case R.id.tv_forget_pswd:
                startActivity(new Intent(this, ForgetPswdActivity.class));
                break;
            case R.id.image_seepwd:

                if (isHideFirst) {
                    image_seepwd.setImageResource(R.mipmap.login_see);
                    //密文
                    HideReturnsTransformationMethod method1 = HideReturnsTransformationMethod.getInstance();
                    et_pswd.setTransformationMethod(method1);
                    isHideFirst = false;
                } else {
                    image_seepwd.setImageResource(R.mipmap.login_seeno);
                    //密文
                    TransformationMethod method = PasswordTransformationMethod.getInstance();
                    et_pswd.setTransformationMethod(method);
                    isHideFirst = true;

                }
                // 光标的位置
                int index = et_pswd.getText().toString().length();
                et_pswd.setSelection(index);
                break;


        }
    }

    //显示dialog
    public void showProgressDialog(String message) {

        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    //隐藏dialog
    public void hideProgressDialog() {
        if (progressDialog != null&&progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }


 @SuppressLint("StaticFieldLeak")
 class LoginAsyncTask extends AsyncTask<Map<String,Object>,Void,String>{
     @Override
     protected void onPreExecute() {
         super.onPreExecute();

     }

     @SuppressLint("ApplySharedPref")
     @SafeVarargs
     @Override
     protected final String doInBackground(Map<String, Object>... maps) {
         String code = "";
         Map<String,Object> params = maps[0];
         String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/user/login",params);
         Log.e("result", "doInBackground: -->"+result );
         if (!ToastUtil.isEmpty(result)) {
             try {
                 JSONObject jsonObject = new JSONObject(result);
                 code = jsonObject.getString("returnCode");
                 if ("100".equals(code)) {
                     JSONObject returnData = jsonObject.getJSONObject("returnData");
                     userId = returnData.getString("userId");
                     phone = returnData.getString("phone");
                     password = returnData.getString("password");
                     String token = returnData.getString("token");
                     String address = returnData.getString("address");
                     SharedPreferences.Editor editor = preferences.edit();
                     Log.i("phone", "---->: " + phone + ",,,," + password);
                     editor.putString("phone", phone);
                     editor.putString("userId", userId);
                     editor.putString("password", password);
                     editor.putString("token", token);
                     editor.putString("address", address);
                     editor.commit();
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


                     Map<String,Object> param = new HashMap<>();
                     param.put("deviceUserId",userId);
                     param.put("roleFlag",0);
                     getdeviceAsyncTask = new GetdeviceAsyncTask();
                     getdeviceAsyncTask.execute(param);
                     new Thread(){
                         @Override
                         public void run() {
                             try {
                                 getdeviceAsyncTask.get(5,TimeUnit.SECONDS);
                             } catch (InterruptedException | ExecutionException e) {
                                 e.printStackTrace();
                             } catch (TimeoutException e) {
                                 e.printStackTrace();
                                 Message message = new Message();
                                 message.obj = "TimeOut";
                                 handler.sendMessage(message);

                             }
                         }
                     }.start();



                 break;

             case "10004":
                 hideProgressDialog();
                 ToastUtil.showShort(LoginActivity.this, "用户名或密码错误");


                 break;
             default:
                 hideProgressDialog();
                 ToastUtil.showShort(LoginActivity.this, "登录失败，请重试");

                 break;
         }
     }
 }
    @SuppressLint("HandlerLeak")
    private  Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ("TimeOut".equals(msg.obj)){
                hideProgressDialog();
                Toast.makeText(LoginActivity.this,"请求超时,请重试",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @SuppressLint("StaticFieldLeak")
    GetdeviceAsyncTask getdeviceAsyncTask;
    class GetdeviceAsyncTask extends AsyncTask<Map<String,Object>,Void,String>{

        @SafeVarargs
        @Override
        protected final String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> prarms = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/device/getDeviceList",prarms);
            Log.e("result11", "doInBackground: -->"+result );
            try {
                JSONObject jsonObject = new JSONObject(result);
                code = jsonObject.getString("returnCode");
                if ("100".equals(code)) {
                    equmentDao.deleteAll();
                    JSONArray returnData = jsonObject.getJSONArray("returnData");
                    for (int i = 0; i < returnData.length(); i++) {
                        JSONObject Devices = returnData.getJSONObject(i);
                        long deviceId = Devices.getLong("deviceId");
                        String deviceName = Devices.getString("deviceName");
                        String deviceMac = Devices.getString("deviceMac");
                        int deviceType = Devices.getInt("deviceType");
                        int deviceUserId = Devices.getInt("deviceUserId");
                        int deviceLeaseType = Devices.getInt("deviceLeaseType");
                        int devicePayType = Devices.getInt("devicePayType");
                        int deviceFlag = Devices.getInt("deviceFlag");
                        int deviceSellerId = Devices.getInt("deviceSellerId");
                        int deviceData = Devices.getInt("deviceData");
                        Equipment equipment1 = new Equipment();
                        equipment1.setId(deviceId);
                        equipment1.setDeviceMac(deviceMac);
                        equipment1.setType(deviceType);
                        equipment1.setName(deviceName);
                        equipment1.setDeviceUserId(deviceUserId);
                        equipment1.setDeviceLeaseType(deviceLeaseType);
                        equipment1.setDevicePayType(devicePayType);
                        equipment1.setDeviceFlag(deviceFlag);
                        equipment1.setDeviceSellerId(deviceSellerId);
                        equipment1.setRoleFlag(0);
                        equipment1.setHaData(false);
                        equipment1.setTodayUse(deviceData);
                        equmentDao.insert(equipment1);
                        Log.e("DDDDDDDDDDDDDAAAAA", "doInBackground: -->"+    equmentDao.findDeviceByMacAddress2(deviceMac).getTodayUse() );


                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (s){
                case "100":
                        Map<String,Object> param = new HashMap<>();
                        param.put("deviceUserId",userId);
                        param.put("roleFlag",1);
                        getSharedeviceAsyncTask = new GetSharedeviceAsyncTask();
                        getSharedeviceAsyncTask.execute(param);
                        new  Thread(){
                            public void run() {
                                try {
                                    getSharedeviceAsyncTask.get(5,TimeUnit.SECONDS);
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                } catch (TimeoutException e) {
                                    e.printStackTrace();
                                    Message message = new Message();
                                    message.obj = "TimeOut";
                                    handler.sendMessage(message);

                                }
                            }
                        }.start();

                    break;

                    default:
                         hideProgressDialog();
                        ToastUtil.showShort(LoginActivity.this, "登录失败请重试");
                        break;
            }
        }
    }
    GetSharedeviceAsyncTask getSharedeviceAsyncTask;
    @SuppressLint("StaticFieldLeak")
    class GetSharedeviceAsyncTask extends AsyncTask<Map<String,Object>,Void,String>{

        @SafeVarargs
        @Override
        protected final String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> prarms = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/device/getDeviceList",prarms);
            Log.e("result12", "doInBackground: -->"+result );
            try {
                JSONObject jsonObject = new JSONObject(result);
                code = jsonObject.getString("returnCode");
                if ("100".equals(code)) {
                    JSONArray returnData = jsonObject.getJSONArray("returnData");
                    for (int i = 0; i < returnData.length(); i++) {
                        JSONObject Devices = returnData.getJSONObject(i);
                        long deviceId = Devices.getLong("deviceId");
                        String deviceName = Devices.getString("deviceName");
                        String deviceMac = Devices.getString("deviceMac");
                        int deviceType = Devices.getInt("deviceType");
                        int deviceUserId = Devices.getInt("deviceUserId");
                        int deviceLeaseType = Devices.getInt("deviceLeaseType");
                        int devicePayType = Devices.getInt("devicePayType");
                        int deviceFlag = Devices.getInt("deviceFlag");
                        Equipment equipment = new Equipment();
                        equipment.setId(deviceId);
                        equipment.setDeviceMac(deviceMac);
                        equipment.setType(deviceType);
                        equipment.setName(deviceName);
                        equipment.setDeviceUserId(deviceUserId);
                        equipment.setDeviceLeaseType(deviceLeaseType);
                        equipment.setDevicePayType(devicePayType);
                        equipment.setDeviceFlag(deviceFlag);
                        equipment.setRoleFlag(1);
                        equipment.setHaData(false);
                        equmentDao.insert(equipment);

                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (s){
                case "100":
                    hideProgressDialog();
                    //开启services
                    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                    filter.addAction("mqttmessage2");
                    myReceiver = new MQTTMessageReveiver();
                    LoginActivity.this.registerReceiver(myReceiver, filter);
                    startActivity(MainActivity.class);
                    ToastUtil.showShort(LoginActivity.this, "登录成功");
                    Log.e("size", "onPostExecute: -->"+equmentDao.findAll().size() );
                    break;

                default:
                    hideProgressDialog();
                    ToastUtil.showShort(LoginActivity.this, "登录失败请重试");
                    break;
            }
        }
    }





    @Override
    protected void onStart() {
        super.onStart();
        if (preferences.contains("phone") && !preferences.contains("password")) {
            String phone = preferences.getString("phone", "");
            et_name.setText(phone);
            et_pswd.setText("");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler!=null)
        handler.removeCallbacksAndMessages(null);
        if (myReceiver!=null)
            unregisterReceiver(myReceiver);
    }
}
