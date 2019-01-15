package com.peihou.waterpurifer.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.adapter.xqRepairAdapter;
import com.peihou.waterpurifer.base.BaseActivity;
import com.peihou.waterpurifer.base.MyApplication;
import com.peihou.waterpurifer.device.activity.AddDeviceActivity;
import com.peihou.waterpurifer.pojo.RepairList;
import com.peihou.waterpurifer.util.HttpUtils;
import com.peihou.waterpurifer.util.NetWorkUtil;
import com.peihou.waterpurifer.util.ToastUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.OnClick;

public class XqRepairActivity extends BaseActivity {
    @BindView(R.id.rv_xqrepair)
    RecyclerView rv_xqrepair;
    MyApplication application;
    SharedPreferences preferences;
    String userId;
    RepairList repairList;
    List<RepairList> repairLists;
    private ProgressDialog progressDialog;
    getRepairListAsyncTask task;
    RefreshLayout refreshLayou;
    xqRepairAdapter xqRepairAdapter;
    int pageNum=1;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_xqrepair;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        preferences = getSharedPreferences("my",MODE_PRIVATE);
        userId = preferences.getString("userId","");
        repairLists = new ArrayList<>();
        getRepairList();
        refreshLayou = view. findViewById(R.id.refreshLayout_xq);
        refreshLayou.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

                boolean isConn = NetWorkUtil.isConn(MyApplication.getContext());
                if (isConn){
                    repairLists.clear();
                    pageNum=1;
                    getRepairList();
                }else {
                    ToastUtil.showShort( XqRepairActivity.this,"无网络可用，请检查网络");
                }

            }

        });

        refreshLayou.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                boolean isConn = NetWorkUtil.isConn(MyApplication.getContext());
                if (isConn){
//                    refreshLayout.finishLoadMore(5000,false,false);
                    pageNum++;
                    getRepairList();
                }else {
                    ToastUtil.showShort( XqRepairActivity.this,"无网络可用，请检查网络");
                }
            }
        });


    }
    public void getRepairList(){
        boolean isConn = NetWorkUtil.isConn(MyApplication.getContext());
        if (isConn) {
            task =  new  getRepairListAsyncTask();
            task.execute();
            new Thread(){
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
        }else {
            ToastUtil.showShort(this, "无网络可用，请检查网络");
        }
    }

    @Override
    public void doBusiness(Context mContext) {
        xqRepairAdapter = new xqRepairAdapter(XqRepairActivity.this,repairLists);
        rv_xqrepair.setLayoutManager(new LinearLayoutManager(XqRepairActivity.this));
        rv_xqrepair.addItemDecoration(new SpacesItemDecoration(20));
        rv_xqrepair.setAdapter(xqRepairAdapter);
    }

    @Override
    public void widgetClick(View v) {

    }

    @OnClick({R.id.iv_main_memu})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_main_memu:
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
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ("TimeOut".equals(msg.obj)){
                Toast.makeText(XqRepairActivity.this,"请求超时,请重试",Toast.LENGTH_SHORT).show();
            }
        }
    };
    @SuppressLint("StaticFieldLeak")
    class getRepairListAsyncTask extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            String code = "";
            String result = HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+"/app/user/getRepairList?repairCreatorId="+userId+"&pageNum="+pageNum);
            Log.e("result", "doInBackground: --》"+result );
            try {
                if (!TextUtils.isEmpty(result)){
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getString("returnCode");
                    if ("100".equals(code)) {
                        JSONArray returnData = jsonObject.getJSONArray("returnData");
                            for (int j = 0; j < returnData.length(); j++) {
                                JSONObject jsonObject1 = returnData.getJSONObject(j);
                                RepairList repairList = new RepairList();
                                repairList.setRepairDeviceType(jsonObject1.getString("repairDeviceType"));
                                repairList.setRepairFlag(jsonObject1.getInt("repairFlag"));
                                repairList.setRepairTime(jsonObject1.getString("repairTime"));
                                repairList.setRepairDeviceId(jsonObject1.getLong("repairDeviceMac"));
                                repairList.setRepairId(jsonObject1.getLong("repairId"));
                                repairLists.add(repairList);
                            }
                        }
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
                    refreshLayou.finishLoadMore(true);
                    refreshLayou.finishRefresh(true);
                    xqRepairAdapter.notifyDataSetChanged();
                    break;
                case "20010":
                    refreshLayou.finishLoadMore(false);
                    ToastUtil.showShort(XqRepairActivity.this,"没有更多的报修信息");
                    break;

                case "20009":
                    refreshLayou.finishRefresh(false);
                    ToastUtil.showShort(XqRepairActivity.this,"暂时无报修设备");
                    break;
            }
        }
    }


    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler!=null)
            handler.removeCallbacksAndMessages(null);
    }
}

