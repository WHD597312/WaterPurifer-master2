package com.peihou.waterpurifer.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.adapter.PackageAdapter;
import com.peihou.waterpurifer.base.BaseActivity;
import com.peihou.waterpurifer.database.dao.PackagesDao;
import com.peihou.waterpurifer.database.dao.daoImp.PackagesImpl;
import com.peihou.waterpurifer.database.dao.daoImp.PackageschildImpl;
import com.peihou.waterpurifer.pojo.Packages;
import com.peihou.waterpurifer.pojo.Packageschild;
import com.peihou.waterpurifer.util.HttpUtils;
import com.peihou.waterpurifer.util.ToastUtil;

import org.json.JSONArray;
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

public class PackagesActivity extends BaseActivity   {

    PackageAdapter packageAdapter;
    PackagesImpl packagesDao;
    PackageschildImpl packageschildDao;
    List<Packages> packagesList1;
    List<Packageschild> packageschildList;
    @BindView(R.id.rv_packages)
    RecyclerView rv_packages;
    int pay=0;
    String[] name = {"一个月","两个月","三个月","四个月","五个月","六个月","1.0 t","2.0 t","3.0 t","一年","两年","三年"};
    int packagePayType;//套餐支付类型
    String packageName;//套餐名称
    long packageId;//固定套餐
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_packages;
    }

    @Override
    public void initView(View view) {
        packageschildDao = new PackageschildImpl(getApplicationContext());
        packagesDao = new PackagesImpl(getApplicationContext());
        packagesList1 = new ArrayList<>();
        packageschildList = new ArrayList<>();
        Intent intent = getIntent();
        int deviceSellerId = intent.getIntExtra("deviceSellerId",-1);
        int devicePayType = intent.getIntExtra("devicePayType",-1);
        int deviceLeaseType = intent.getIntExtra("deviceLeaseType",-1);
        Log.e("DDDDDDTTTTT", "initView: -->deviceSellerId"+deviceSellerId +"...devicePayType"+devicePayType+"...deviceLeaseType"+deviceLeaseType );
        Map<String,Object> params = new HashMap<>();
        params.put("deviceSellerId",deviceSellerId);
        params.put("deviceLeaseType",deviceLeaseType);
        params.put("devicePayType",devicePayType);
        getPackagesAsyncTask = new GetPackagesAsyncTask();
        getPackagesAsyncTask.execute(params);
        new Thread(){
            @Override
            public void run() {
                try {
                    getPackagesAsyncTask.get(5, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                    Message message = new Message();
                    message.obj="TimeOut";

                }
            }
        }.start();
        Packageschild packageschild = new Packageschild();

        packageAdapter = new PackageAdapter(PackagesActivity.this,false,packagesList1,this);
        rv_packages.setLayoutManager(new LinearLayoutManager(PackagesActivity.this));
        rv_packages.setAdapter(packageAdapter);

    }
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ("TimeOut".equals(msg)){
                ToastUtil.showShort(PackagesActivity.this,"服务器繁忙，请重试");
            }
        }
    };
    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    @OnClick({ R.id.iv_main_memu,R.id.tv_buy_package})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_main_memu:
                finish();
                break;
            case R.id.tv_buy_package:
                long orderPackageId=0;
                double pay=-1;
                if (packageschild!=null){
                    orderPackageId=packageschild.getPchildId();
                    pay=packageschild.getPchildNewPrice();
                }
                if (pay==-1){
                    toast("请先选择套餐");
                }else {
                    Intent intent = new Intent(this,CheckstandActivity.class);
                    intent.putExtra("pay",pay);
                    orderPackageId=packageschild.getPchildId();
                    intent.putExtra("orderPackageId",orderPackageId);
                    intent.putExtra("packagePayType",0);
                    startActivity(intent);
                }

                break;
        }
    }
    Packageschild packageschild;
    public  void setPay (Packageschild packageschild){
        this.packageschild=packageschild;
    }

    public Packageschild getPackageschild() {
        return packageschild;
    }

    private GetPackagesAsyncTask getPackagesAsyncTask;
    @SuppressLint("StaticFieldLeak")
    class GetPackagesAsyncTask extends AsyncTask<Map<String,Object>,Void,String>{

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> param = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/user/getPackageList",param);
            Log.e("result", "doInBackground: -->"+result);

            try {
                if (!TextUtils.isEmpty(result)){
                    packagesDao.deleteAll();
                    packageschildDao.deleteAll();
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getString("returnCode");
                    if ( "100".equals(code)){
                        JSONArray jsonArray = jsonObject.getJSONArray("returnData");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject father = jsonArray.getJSONObject(i);
                            packageId = father.getLong("packageId");
                            packagePayType=father.getInt("packagePayType");
                            packageName = father.getString("packageName");
                            JSONArray child = father.getJSONArray("children");
                            for (int j = 0; j < child.length(); j++) {
                                JSONObject jsonObject1 = child.getJSONObject(j);
                                long pchildId = jsonObject1.getLong("pchildId");
                                int pchildNum = jsonObject1.getInt("pchildNum");
                                String pchildName = jsonObject1.getString("pchildName"); /*'套餐名',*/
                                int pchildOldPrice = jsonObject1.getInt("pchildOldPrice"); /* '原价',*/
                                double pchildNewPrice = jsonObject1.getDouble("pchildNewPrice"); /* '现价',*/
                                int pchildDiscount = jsonObject1.getInt("pchildDiscount"); /*'有无折扣',*/
                                long parentId = packageId;  /*'期限/数量'*/
                                Packageschild packageschild = new Packageschild();
                                packageschild.setPchildId(pchildId);
                                packageschild.setPchildNum(pchildNum);
                                packageschild.setPchildName(pchildName);
                                packageschild.setPchildNewPrice(pchildNewPrice);
                                packageschild.setPchildOldPrice(pchildOldPrice);
                                packageschild.setPchildDiscount(pchildDiscount);
                                packageschild.setParentId(parentId);
                                packageschildDao.insert(packageschild);
                            }
                            Packages packages = new Packages();
                            packages.setPackageId(packageId);
                            packages.setPackageName(packageName);
                            packagesDao.insert(packages);

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
                       packagesList1= packagesDao.findAll();
                        packageAdapter.setData(packagesList1);
                        packageAdapter.notifyDataChanged();
                    break;

                    default:

                        break;
            }
        }
    }
}
