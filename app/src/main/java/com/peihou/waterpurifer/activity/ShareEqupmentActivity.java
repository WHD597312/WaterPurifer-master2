package com.peihou.waterpurifer.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.peihou.waterpurifer.MainActivity;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.adapter.ShareEqupmentAdapter;
import com.peihou.waterpurifer.adapter.SpaceItemDecoration;
import com.peihou.waterpurifer.base.BaseActivity;
import com.peihou.waterpurifer.base.MyApplication;
import com.peihou.waterpurifer.database.dao.daoImp.EquipmentImpl;
import com.peihou.waterpurifer.pojo.Equipment;
import com.peihou.waterpurifer.service.MQService;
import com.peihou.waterpurifer.util.HttpUtils;
import com.peihou.waterpurifer.util.ToastUtil;
import com.peihou.waterpurifer.util.view.ScreenSizeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import butterknife.BindView;
import butterknife.OnClick;

public class ShareEqupmentActivity extends BaseActivity {
    @BindView(R.id.rv_equment)
    RecyclerView rv_equment;
    MyApplication application;
    SharedPreferences preferences;
    EquipmentImpl equmentDao;
    List<Equipment> equipmentList ;
    ShareEqupmentAdapter equpmentAdapter;
    private  boolean clockisBound;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_fxequpment;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        preferences = getSharedPreferences("my",MODE_PRIVATE);
        String userId = preferences.getString("userId","");
        clockintent = new Intent(ShareEqupmentActivity.this, MQService.class);
        clockisBound = bindService(clockintent, clockconnection, Context.BIND_AUTO_CREATE);
        equmentDao = new EquipmentImpl(getApplicationContext());
        equipmentList = equmentDao.findDeviceByRoleFlag(1);
        Log.d("DDDDDFFFFF333", "doInBackground: -->"+equipmentList.size());
        for (int i =0 ;i<equipmentList.size();i++){
            equmentDao.delete(equipmentList.get(i));
        }
        equipmentList.clear();
        Log.d("DDDDDFFFFF222", "doInBackground: -->"+equipmentList.size());
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
        Log.e("DDDDTTT", "initView: -->"+equipmentList.size() );
         equpmentAdapter = new ShareEqupmentAdapter(this,equipmentList);
        rv_equment.setLayoutManager(new GridLayoutManager(this,2));
        rv_equment.addItemDecoration(new SpaceItemDecoration(22,22,25));
        rv_equment.setAdapter(equpmentAdapter);
        equpmentAdapter.SetOnItemClick(new ShareEqupmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(ShareEqupmentActivity.this,MainActivity.class);
                intent.putExtra("RoleFlag",1);
                intent.putExtra("pos",position);
                startActivity(intent);
        }

            @Override
            public void onLongClick(View view, int posotion) {
                customDialog();
            }
        });
    }
    @SuppressLint("HandlerLeak")
    private  Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ("TimeOut".equals(msg.obj)){

                Toast.makeText(ShareEqupmentActivity.this,"请求超时,请重试",Toast.LENGTH_SHORT).show();
            }
            if ("updata".equals(msg.obj)){
                equpmentAdapter.notifyDataSetChanged();
            }
        }
    };
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
                        int deviceData = Devices.getInt("deviceData");
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
                        equipment.setTodayUse(deviceData);
                        equmentDao.insert(equipment);
                        equipmentList.add(equipment);
                        String onlineTopicName = "p99/wPurifier1/" + deviceMac + "/transfer";
                        String offlineTopicName = "p99/wPurifier1/" + deviceMac + "/lwt";
                        clcokservice.subscribe(onlineTopicName,1);
                        clcokservice.subscribe(offlineTopicName,1);
                        Log.d("DDDDDFFFFF111", "doInBackground: -->"+equipmentList.size());
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
                    Message message = new Message();
                    message.obj="updata";
                    handler.sendMessage(message);
                    break;

                default:

                    ToastUtil.showShort(ShareEqupmentActivity.this, "查询失败");
                    break;
            }
        }
    }
    /**
     * 自定义对话框
     */
    private void customDialog() {
        final Dialog dialog = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_shareedel, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dialog_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dialog_qd);
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
        tv_dialog_qx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.dismiss();
            }

        });
        tv_dialog_qd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @OnClick({R.id.iv_main_memu})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_main_memu:
                finish();
                break;

        }
    }


    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

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


            Log.e("QQQQQQQQQQQDDDDDDD", "onServiceConnected: ------->");
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
}
