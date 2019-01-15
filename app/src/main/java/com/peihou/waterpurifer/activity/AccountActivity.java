package com.peihou.waterpurifer.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.peihou.waterpurifer.MainActivity;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.activity.alipay.PayActivity;
import com.peihou.waterpurifer.activity.alipay.PayResult;
import com.peihou.waterpurifer.adapter.AccEqupmentAdapter;
import com.peihou.waterpurifer.adapter.ShareEqupmentAdapter;
import com.peihou.waterpurifer.adapter.SpaceItemDecoration;
import com.peihou.waterpurifer.base.BaseActivity;
import com.peihou.waterpurifer.base.MyApplication;
import com.peihou.waterpurifer.database.dao.daoImp.EquipmentImpl;
import com.peihou.waterpurifer.pojo.Equipment;
import com.peihou.waterpurifer.util.HttpUtils;

import org.json.JSONException;
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

public class AccountActivity extends BaseActivity {

    @BindView(R.id.rv_account)
    RecyclerView rv_account;
    MyApplication application;
    EquipmentImpl equipmentDao;
    Equipment equipment;
    List<Equipment> equipments ;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_account;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        equipmentDao = new EquipmentImpl(getApplicationContext());
        equipments = equipmentDao.findDeviceByRoleFlag(0);
        AccEqupmentAdapter equpmentAdapter = new AccEqupmentAdapter(this,equipments);
        rv_account.setLayoutManager(new GridLayoutManager(this,2));
        rv_account.addItemDecoration(new SpaceItemDecoration(22,22,25));
        rv_account.setAdapter(equpmentAdapter);
        equpmentAdapter.SetOnItemClick(new AccEqupmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                    int deviceSellerId =  equipments.get(position).getDeviceSellerId();
                    int deviceLeaseType = equipments.get(position).getDeviceLeaseType();
                int devicePayType = equipments.get(position).getDevicePayType();
                    Intent intent = new Intent(AccountActivity.this,PackagesActivity.class);
                    intent.putExtra("deviceSellerId",deviceSellerId);
                    intent.putExtra("deviceLeaseType",deviceLeaseType);
                    intent.putExtra("devicePayType",devicePayType);
                    startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int posotion) {

            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    PayAsyncTask task;
    @OnClick({ R.id.iv_main_memu})
    public void onClick(View view){
        switch (view.getId()){


//                Map<String,Object> param = new HashMap<>();
//                param.put("payFor",0);
//                param.put("payType",0);
//                task = new PayAsyncTask();
//                task.execute(param);
//                new Thread(){
//                    public void run() {
//                        try {
//
//                            task.get(5, TimeUnit.SECONDS);
//                        } catch (InterruptedException e) {
//                        } catch (ExecutionException e) {
//                        } catch (TimeoutException e) {
//                            Message message = new Message();
//                            message.obj = "TimeOut";
//                            handler.sendMessage(message);
//                        }
//                    }
//                }.start();

            case R.id.iv_main_memu:
                finish();
                break;
        }
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ("TimeOut".equals(msg.obj)){
                Toast.makeText(AccountActivity.this,"请求超时,请重试",Toast.LENGTH_SHORT).show();
            }
        }
    };
    String orderString, orderNumber;
    @SuppressLint("StaticFieldLeak")
    class PayAsyncTask extends AsyncTask<Map<String,Object>,Void,String>{

        @SafeVarargs
        @Override
        protected final String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> params = maps[0];
            String result = HttpUtils.postOkHpptRequest("http://47.98.131.11:8093/app/createOrder",params);
            Log.e("result", "doInBackground: -->"+result );
            try {
                JSONObject jsonObject = new JSONObject(result);
                code = jsonObject.getString("returnCode");
                if (!TextUtils.isEmpty(code) && "100".equals(code)) {
                    orderNumber = jsonObject.getString("orderNo");
                    orderString = jsonObject.getString("orderString");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (s){
                case "100":
                    Intent intent = new Intent(AccountActivity.this,PayActivity.class);
                    intent.putExtra("orderNumber", orderNumber);
                    intent.putExtra("orderString", orderString);
                    startActivity(intent);
                    break;
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler!=null)
            handler.removeCallbacksAndMessages(null);
    }
}
