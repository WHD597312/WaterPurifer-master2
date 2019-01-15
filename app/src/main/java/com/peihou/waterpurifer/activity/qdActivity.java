package com.peihou.waterpurifer.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import com.peihou.waterpurifer.MainActivity;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.base.BaseActivity;
import com.peihou.waterpurifer.base.MyApplication;
import com.peihou.waterpurifer.database.dao.daoImp.EquipmentImpl;
import com.peihou.waterpurifer.pojo.Equipment;
import com.peihou.waterpurifer.service.MQTTMessageReveiver;
import com.peihou.waterpurifer.util.HttpUtils;
import com.peihou.waterpurifer.util.NetWorkUtil;
import com.peihou.waterpurifer.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class qdActivity extends BaseActivity {
    String str;
    SharedPreferences preferences;

    String phone;
    String userId;
    String password;
    LoginAsyncTask task;
    SharedPreferences mPositionPreferences;
    private MyApplication application;
    CountDownTimer countDownTimer;
    private boolean running ;
    EquipmentImpl equmentDao;
    List<Equipment> equipment;
    private MQTTMessageReveiver myReceiver;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_qdy;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        equmentDao = new EquipmentImpl(getApplicationContext());
        phone=preferences.getString("phone", "");
        password = preferences.getString("password", "");
    }

    @Override
    public void doBusiness(Context mContext) {
        if (!TextUtils.isEmpty(phone)&&!TextUtils.isEmpty(password)) {
            boolean isConn = NetWorkUtil.isConn(MyApplication.getContext());
            if (isConn) {

                Map<String, Object> params = new HashMap<>();
                params.put("phone", phone);
                params.put("password", password);
                task = new LoginAsyncTask();
                task.execute(params);
                new Thread() {
                    public void run() {
                        try {

                            task.get(5, TimeUnit.SECONDS);
                        } catch (InterruptedException | ExecutionException e) {
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
        }else {
            startActivity(LoginActivity.class);
        }

    }

    @Override
    public void widgetClick(View v) {

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            application.removeActivity(this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("StaticFieldLeak")
    class LoginAsyncTask extends AsyncTask<Map<String,Object>,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
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
                toast("用户名或密码错误");
                startActivity(LoginActivity.class);
                break;
            default:
                toast("登录失败，请重试");
                startActivity(LoginActivity.class);
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
                toast("请求超时,请重试");
                startActivity(LoginActivity.class);

            }
        }
    };

    @SuppressLint("StaticFieldLeak")
    GetdeviceAsyncTask getdeviceAsyncTask;
    class GetdeviceAsyncTask extends AsyncTask<Map<String,Object>,Void,String> {

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
                ToastUtil.showShort(qdActivity.this, "登录失败请重试");
                startActivity(LoginActivity.class);
                break;
        }
    }
}
GetSharedeviceAsyncTask getSharedeviceAsyncTask;
@SuppressLint("StaticFieldLeak")
class GetSharedeviceAsyncTask extends AsyncTask<Map<String,Object>,Void,String> {

    @SafeVarargs
    @Override
    protected final String doInBackground(Map<String, Object>... maps) {
        String code = "";
        Map<String, Object> prarms = maps[0];
        String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress + "/app/device/getDeviceList", prarms);
        Log.e("result12", "doInBackground: -->" + result);
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
        switch (s) {
            case "100":
                //开启services
                IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                filter.addAction("mqttmessage2");
                myReceiver = new MQTTMessageReveiver();
                qdActivity.this.registerReceiver(myReceiver, filter);
                startActivity(MainActivity.class);
                ToastUtil.showShort(qdActivity.this, "登录成功");
                break;

            default:
                startActivity(LoginActivity.class);
                ToastUtil.showShort(qdActivity.this, "登录失败请重试");
                break;
        }
    }

  }





    @Override
    protected void onPause() {
        super.onPause();
        running=false;
    }

    @Override
    protected void onStop() {
        super.onStop();

        running=false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler!=null)
            handler.removeCallbacksAndMessages(null);
           running=false;
        if (myReceiver!=null)
            unregisterReceiver(myReceiver);
    }
}
