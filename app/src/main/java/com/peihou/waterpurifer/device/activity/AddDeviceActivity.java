package com.peihou.waterpurifer.device.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.peihou.waterpurifer.MainActivity;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.base.BaseActivity;
import com.peihou.waterpurifer.base.MyApplication;
import com.peihou.waterpurifer.database.dao.daoImp.EquipmentImpl;
import com.peihou.waterpurifer.esptouch.EspWifiAdminSimple;
import com.peihou.waterpurifer.esptouch.EsptouchTask;
import com.peihou.waterpurifer.esptouch.IEsptouchListener;
import com.peihou.waterpurifer.esptouch.IEsptouchResult;
import com.peihou.waterpurifer.esptouch.IEsptouchTask;
import com.peihou.waterpurifer.pojo.Equipment;
import com.peihou.waterpurifer.service.MQService;
import com.peihou.waterpurifer.util.HttpUtils;
import com.peihou.waterpurifer.util.NetWorkUtil;
import com.peihou.waterpurifer.util.ToastUtil;
import com.peihou.waterpurifer.zxing.android.CaptureActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddDeviceActivity extends BaseActivity {
    MyApplication application;
    Unbinder unbinder;
    @BindView(R.id.et_ssid)
    EditText et_ssid;//IEMI
    @BindView(R.id.iv_pz_wifi)
    ImageView iv_pz_wifi;
    @BindView(R.id.tv_pz_wifi)
    TextView tv_pz_wifi;
    @BindView(R.id.iv_pz_sm)
    ImageView iv_pz_sm;
    @BindView(R.id.tv_pz_sm)
    TextView tv_pz_sm;
    @BindView(R.id.view_pz_sm)
    View view_pz_sm;
    @BindView(R.id.view_pz_wifi)
    View view_pz_wifi;
    @BindView(R.id.rl_add_sm)
    RelativeLayout rl_add_sm;
    @BindView(R.id.rl_add_wifi)
    RelativeLayout rl_add_wifi;
    @BindView(R.id.iv_add_bs)
    ImageView iv_add_bs;
    @BindView(R.id.et_add_id)
    EditText et_add_id;
    private EspWifiAdminSimple mWifiAdmin;
    private ProgressDialog mProgressDialog;
    String deviceMac,userId;
    EquipmentImpl equmentDao;
    Equipment equipment;
    List<Equipment> equipments;
    private ProgressDialog progressDialog;
    SharedPreferences preferences;
    private  boolean clockisBound;
    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";
    private static final int REQUEST_CODE_SCAN = 0x0000;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_add_device;
    }

    @Override
    public void initView(View view) {
//        mWifiAdmin = new EspWifiAdminSimple(this);
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        iv_add_bs .setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(AddDeviceActivity.this,R.color.white)));
         equmentDao = new EquipmentImpl(getApplicationContext());
         equipment = new Equipment();
         equipments = new ArrayList<>();
         equipments = equmentDao.findAll();
        progressDialog = new ProgressDialog(this);
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        userId = preferences.getString("userId","");
        //绑定services
        clockintent = new Intent(AddDeviceActivity.this, MQService.class);
        clockisBound = bindService(clockintent, clockconnection, Context.BIND_AUTO_CREATE);
    }
    Intent clockintent;
    MQService clcokservice;
    boolean boundclock;
    ServiceConnection clockconnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder = (MQService.LocalBinder) service;
            clcokservice = binder.getService();
            boundclock = true;
            Log.e("QQQQQQQQQQQDDDDDDD", "onServiceConnected: ------->" );
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    protected void onStart() {
        super.onStart();
//        String ssid=mWifiAdmin.getWifiConnectedSsid();
//        et_ssid.setText(ssid);
    }
    addDeviceAsyncTask task;
    @OnClick({R.id.btn_ensure,R.id.et_add_tx,R.id.rl_add_2,R.id.rl_add_1,R.id.iv_main_memu,R.id.btn_add_qd})
    public void onClick(View view){
        switch (view.getId()){
//            case R.id.btn_ensure:
//                String ssid=et_ssid.getText().toString();
//                String apBssid=mWifiAdmin.getWifiConnectedBssid();
//                String apPassword=et_pswd.getText().toString();
//                String taskResultCountStr = "1";
//                new EsptouchAsyncTask3().execute(ssid, apBssid, apPassword, taskResultCountStr);
//                break;
            case R.id.et_add_tx:
                //动态权限申请
                if (ContextCompat.checkSelfPermission(AddDeviceActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddDeviceActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    goScan();
                }
                break;
            case R.id.rl_add_2:
                iv_pz_wifi.setImageResource(R.mipmap.equ_sdk);
                iv_pz_sm.setImageResource(R.mipmap.equ_smg);
                tv_pz_sm.setTextColor(getResources().getColor(R.color.color_gray2));
                tv_pz_wifi.setTextColor(getResources().getColor(R.color.color_toblue));
                view_pz_sm.setVisibility(View.GONE);
                view_pz_wifi.setVisibility(View.VISIBLE);
                rl_add_wifi.setVisibility(View.VISIBLE);
                rl_add_sm.setVisibility(View.GONE);
                break;
            case R.id.rl_add_1:

                iv_pz_wifi.setImageResource(R.mipmap.equ_sdg);
                iv_pz_sm.setImageResource(R.mipmap.equ_smk);
                tv_pz_sm.setTextColor(getResources().getColor(R.color.color_toblue));
                tv_pz_wifi.setTextColor(getResources().getColor(R.color.color_gray2));
                view_pz_sm.setVisibility(View.VISIBLE);
                view_pz_wifi.setVisibility(View.GONE);
                rl_add_wifi.setVisibility(View.GONE);
                rl_add_sm.setVisibility(View.VISIBLE);
                break;

            case R.id.iv_main_memu:
                finish();
                break;

            case R.id.btn_ensure:
                //手动添加确定
                deviceMac = et_ssid.getText().toString().trim();
                if (TextUtils.isEmpty(deviceMac)) {
                    ToastUtil.showShort(this, "账号码不能为空");
                    break;
                }
                for (int i = 0; i< equipments.size(); i++){
                     equipment = equipments.get(i);
                     String id = equipment.getId()+"";
                     if (id.equals(deviceMac)){
                         ToastUtil.showShort(this,"设备已添加");
                         break;
                     }
                }

                boolean isConn = NetWorkUtil.isConn(MyApplication.getContext());
                if (isConn){
                    showProgressDialog("正在配置，请稍后。。。");
                    Map<String, Object> params = new HashMap<>();
                    params.put("deviceMac", deviceMac);
                    params.put("deviceType", 8);
                    params.put("deviceName", deviceMac);
                    params.put("deviceUserId", Integer.valueOf(userId));
                    task =  new addDeviceAsyncTask() ;
                    task.execute(params);
                    new Thread(){
                        public void run() {
                            try {

                                task.get(5, TimeUnit.SECONDS);
                            } catch (InterruptedException | ExecutionException ignored) {
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
            case R.id.btn_add_qd:
                deviceMac = et_add_id.getText().toString().trim();
                if (TextUtils.isEmpty(deviceMac)) {
                    ToastUtil.showShort(this, "账号码不能为空");
                    break;
                }
                for (int i = 0; i< equipments.size(); i++){
                    equipment = equipments.get(i);
                    String id = equipment.getId()+"";
                    if (id.equals(deviceMac)){
                        ToastUtil.showShort(this,"设备已添加");
                        break;
                    }
                }

                boolean isConn1 = NetWorkUtil.isConn(MyApplication.getContext());
                if (isConn1){
                    showProgressDialog("正在配置，请稍后。。。");
                    Map<String, Object> params = new HashMap<>();
                    params.put("deviceMac", deviceMac);
                    params.put("deviceType", 8);
                    params.put("deviceName", deviceMac);
                    params.put("deviceUserId", userId);
                    try {
                        new addDeviceAsyncTask().execute(params).get(5, TimeUnit.SECONDS);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                        Message message = new Message();
                        message.obj = "TimeOut";
                        handler.sendMessage(message);
                    }
                }else {
                    ToastUtil.showShort(this, "无网络可用，请检查网络");
                }
                break;

        }
    }

    /**
     * 跳转到扫码界面扫码
     */
    private void goScan(){
        Intent intent = new Intent(AddDeviceActivity.this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goScan();
                } else {
                    Toast.makeText(this, "你拒绝了权限申请，可能无法打开相机扫码哟！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                //返回的文本内容
                String content = data.getStringExtra(DECODED_CONTENT_KEY);
                //返回的BitMap图像
//                Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);
                if (content.contains("P99")){
                    String IMEI= content.substring(content.indexOf(":")+1);
                    et_add_id.setText(IMEI);
//                ToastUtil.showShort(this,content);
                }else {
                    ToastUtil.showShort(this,"请扫描正确的二维码");
                }


            }
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ("TimeOut".equals(msg.obj)){
                if (progressDialog!=null&&progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(AddDeviceActivity.this,"请求超时,请重试",Toast.LENGTH_SHORT).show();
            }
        }
    };
    Equipment equipment2;
    /**
     * 添加设备
     * */
    class addDeviceAsyncTask extends AsyncTask<Map<String,Object>,Void,String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String ,Object> prarms = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/device/addNewDevice",prarms);
            Log.e("back", "--->"+result);
            if (!ToastUtil.isEmpty(result)){
                try {
                    JSONObject jsonObject= new JSONObject(result);
                    code = jsonObject.getString("returnCode");
//                    JSONObject returnData = jsonObject.getJSONObject("returnData");
                    if ("100".equals(code)){
                        Equipment equipment = new Equipment();
                        equipment.setName(deviceMac);
                        equipment.setType(8);
                        equipment.setDeviceMac(deviceMac);
                        equipment.setId(Long.valueOf(deviceMac));
                        equmentDao.insert(equipment);
                        equipment2 = equipment;
                        String onlineTopicName = "p99/wPurifier1/" + deviceMac + "/transfer";
                        String offlineTopicName = "p99/wPurifier1/" + deviceMac + "/lwt";
                        clcokservice.subscribe(onlineTopicName,1);
                        clcokservice.subscribe(offlineTopicName,1);
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
                    if (progressDialog!=null&&progressDialog.isShowing())
                        progressDialog.dismiss();
                    ToastUtil.showShort(AddDeviceActivity.this, "配置成功");
                    Intent intent=new Intent();
                    intent.putExtra("equipment", equipment2);
                    intent.putExtra("activation",false);
                    setResult(600,intent);
                    finish();
                    break;

                case "10007":
                    if (progressDialog!=null&&progressDialog.isShowing())
                        progressDialog.dismiss();
                    ToastUtil.showShort(AddDeviceActivity.this, "您已添加该设备，请耐心等候代理商激活");
                    break;
                case "10006":
                    if (progressDialog!=null&&progressDialog.isShowing())
                        progressDialog.dismiss();
                    ToastUtil.showShort(AddDeviceActivity.this, "设备已被其他用户绑定，请联系代理商");
                    break;
                case "10005":
                    if (progressDialog!=null&&progressDialog.isShowing())
                        progressDialog.dismiss();
                    ToastUtil.showShort(AddDeviceActivity.this, "设备还未注册，请联系代理商");
                    break;

                default:
                    if (progressDialog!=null&&progressDialog.isShowing())
                        progressDialog.dismiss();
                    ToastUtil.showShort(AddDeviceActivity.this, "配置失败，请重试");
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

    /**
     * WIFI模块配置（暂时没用）
     * */

    private IEsptouchTask mEsptouchTask;

    private class EsptouchAsyncTask3 extends AsyncTask<String, Void, List<IEsptouchResult>> {


        // without the lock, if the user tap confirm and cancel quickly enough,
        // the bug will arise. the reason is follows:
        // 0. task is starting created, but not finished
        // 1. the task is cancel for the task hasn't been created, it do nothing
        // 2. task is created
        // 3. Oops, the task should be cancelled, but it is running
        private final Object mLock = new Object();

        @Override
        protected void onPreExecute() {
//            popupWindow();
//            addDeviceDialog=new AddDeviceDialog(AddDeviceActivity.this);
//            addDeviceDialog.setCanceledOnTouchOutside(false);
//            addDeviceDialog.show();
            mProgressDialog = new ProgressDialog(AddDeviceActivity.this);
            mProgressDialog.setMessage("正在配置, 请耐心等待...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
//            CountTimer countTimer = new CountTimer(30000, 1000);
//            countTimer.start();
        }

        @Override
        protected List<IEsptouchResult> doInBackground(String... params) {
            int taskResultCount = -1;
            synchronized (mLock) {
                // !!!NOTICE
                String apSsid = params[0];
                String apBssid = params[1];
                String apPassword = params[2];
                String taskResultCountStr = params[3];
                taskResultCount = Integer.parseInt(taskResultCountStr);
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, AddDeviceActivity.this);
                mEsptouchTask.setEsptouchListener(myListener);
            }
            List<IEsptouchResult> resultList = mEsptouchTask.executeForResults(taskResultCount);
            return resultList;
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {
            IEsptouchResult firstResult = result.get(0);
            // check whether the task is cancelled and no results received
            if (!firstResult.isCancelled()) {
                int count = 0;
                // max results to be displayed, if it is more than maxDisplayCount,
                // just show the count of redundant ones
                final int maxDisplayCount = 5;
                // the task received some results including cancelled while
                // executing before receiving enough results
                if (firstResult.isSuc()) {
                    StringBuilder sb = new StringBuilder();
                    try {
                        Thread.sleep(300);
                        Log.i("IEsptouchResult", "-->" + result.size());
                        for (IEsptouchResult resultInList : result) {
                            //                String ssid=et_ssid.getText().toString();
                            String ssid = resultInList.getBssid();
                            sb.append("配置成功" + ssid);
                            if (!TextUtils.isEmpty(ssid)) {
                                mProgressDialog.dismiss();
                                Toast.makeText(AddDeviceActivity.this, "配置成功,ssid=" + ssid, Toast.LENGTH_LONG).show();
                                break;
                            }
                            count++;
                            if (count >= maxDisplayCount) {
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (count < result.size()) {
                        sb.append("\nthere's " + (result.size() - count)
                                + " more result(s) without showing\n");
                    }
                } else {
                    mProgressDialog.dismiss();
                    Toast.makeText(AddDeviceActivity.this, "配置失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    private void onEsptoucResultAddedPerform(final IEsptouchResult result) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String text = result.getBssid() + " is connected to the wifi";
//                Toast.makeText(AddDeviceActivity.this, text,
//                        Toast.LENGTH_LONG).show();
            }

        });
    }

    private IEsptouchListener myListener = new IEsptouchListener() {

        @Override
        public void onEsptouchResultAdded(final IEsptouchResult result) {
            onEsptoucResultAddedPerform(result);
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(unbinder!=null)
            unbinder.unbind();//解绑注解
        if (handler!=null)
            handler.removeCallbacksAndMessages(null);
        if (clockisBound)
            unbindService(clockconnection);
    }

}
