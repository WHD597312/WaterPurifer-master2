package com.peihou.waterpurifer.wxapi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.base.MyApplication;
import com.peihou.waterpurifer.util.HttpUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import org.json.JSONObject;

import java.util.Map;

import butterknife.ButterKnife;

public class WXPayActiviy  extends AppCompatActivity {

    String orderNumber;
    private IWXAPI iwxapi;
    SharedPreferences preferences;
    MyApplication application;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
//        Bundle extras = getIntent().getExtras();
        Intent intent = getIntent();
       String partnerId= intent.getStringExtra("partnerId");
        String prepayId=   intent.getStringExtra("prepayId" );
        String packageValue=  intent.getStringExtra("packageValue" );
        String nonceStr=  intent.getStringExtra("nonceStr");
        String timeStamp=   intent.getStringExtra("timeStamp" );
        String sign=   intent.getStringExtra("sign" );
        application = (MyApplication) getApplication();
        application.addDestoryActivity(this,"WXPayActivity");

        preferences = getSharedPreferences("order", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("orderNumber", orderNumber);
        editor.commit();

        iwxapi = WXAPIFactory.createWXAPI(this, appid,false); //初始化微信api
        iwxapi.registerApp(appid); //注册appid  appid可以在开发平台获取
//        new wxpay().execute();
        request = new PayReq();
        request.appId = appid;
        request.partnerId = partnerId ;
        request.prepayId = prepayId;
        request.packageValue=packageValue;
        request.nonceStr = nonceStr;
        request.timeStamp = timeStamp;
        request.sign = sign;
        iwxapi.sendReq(request);

    }



    String appid="wx44acbeed9571e8cf";
    PayReq request;
    class wxpay extends AsyncTask<Map<String, Object>, Void, String>{
        @Override
        protected String doInBackground(Map<String, Object>... maps) {


            String url = "/wxpay/pay";
            url = url + "?orderNumber=" +orderNumber;
            String result = HttpUtils.doGet(WXPayActiviy.this, url);
            String code = "";
            try {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = new JSONObject(result);

                    Log.e("qqqqqqqqqqRRRRR",result);
                    code = jsonObject.getString("returnCode");
                    JSONObject returnData = jsonObject.getJSONObject("returnData");
                    request = new PayReq();
                    request.appId = appid;
                    request.partnerId = returnData.getString("partnerid");
                    request.prepayId = returnData.getString("prepayid");
                    request.packageValue=returnData.getString("package");
                    request.nonceStr = returnData.getString("noncestr");
                    request.timeStamp = returnData.getString("timestamp");
                    request.sign = returnData.getString("sign");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!TextUtils.isEmpty(s) && "100".equals(s)) {
                iwxapi.sendReq(request);

            }
        }
    }


}