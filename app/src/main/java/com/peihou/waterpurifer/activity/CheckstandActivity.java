package com.peihou.waterpurifer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.github.mikephil.charting.formatter.IFillFormatter;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.activity.alipay.PayActivity;
import com.peihou.waterpurifer.base.BaseActivity;
import com.peihou.waterpurifer.util.HttpUtils;
import com.peihou.waterpurifer.wxapi.WXPayActiviy;
import com.tencent.mm.opensdk.modelpay.PayReq;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class CheckstandActivity extends BaseActivity {

    @BindView(R.id.check_zhb) CheckBox check_zhb;
    @BindView(R.id.check_wx) CheckBox check_wx;
    @BindView(R.id.tv_order_amount)
    TextView tv_order_amount;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_checkstand;
    }

    int price;//价格
    long orderPackageId;//订单Id
    int packagePayType;//订单类型
    @Override
    public void initView(View view) {
        Intent intent = getIntent();
        price =  intent.getIntExtra("pay",0);
        orderPackageId=intent.getLongExtra("orderPackageId",0);
        packagePayType=intent.getIntExtra("packagePayType",0);
        tv_order_amount.setText(price+"");
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    int payWay=0;//0代表什么都没选，1表示支付宝支付，2表示微信支付
    @OnClick({R.id.iv_main_memu,R.id.check_zhb,R.id.check_wx,R.id.btn_pay})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_main_memu:
                finish();
                break;
            case R.id.check_zhb:
               if (check_zhb.isChecked()){
                   if (check_wx.isChecked()){
                       check_wx.setChecked(false);
                   }
                   check_zhb.setChecked(true);
                   payWay=1;
               }else {
                   check_zhb.setChecked(false);
                   payWay=0;
               }
                break;
            case R.id.check_wx:
                if (check_wx.isChecked()){
                    if (check_zhb.isChecked()){
                        check_zhb.setChecked(false);
                    }
                    check_wx.setChecked(true);
                    payWay=2;
                }else {
                    check_wx.setChecked(false);
                    payWay=0;
                }
                break;
            case R.id.btn_pay:
                int orderPayType=0;
                if (payWay==1){
                    orderPayType=0;
                }else if (payWay==2){
                    orderPayType=1;
                }
                Map<String,Object> params=new HashMap<>();
                params.put("orderPayType",orderPayType);
                params.put("orderPackageId",orderPackageId);
                new CreateOrderAsync().execute(params);
                break;
        }
    }
    String url=HttpUtils.ipAddress+"/app/user/createOrder";

    int orderNumber;
    String orderString;
    PayReq request;
    String appid="wx44acbeed9571e8cf";
    String partnerId,prepayId,packageValue,nonceStr,timeStamp,sign;
    class CreateOrderAsync extends AsyncTask<Map<String,Object>,Void,Integer>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Map<String, Object>... maps) {
            Map<String,Object> params=maps[0];
            int code=0;
            try {
                String result=HttpUtils.postOkHpptRequest(url,params);
                Log.i("result","-->"+result);
                if (!TextUtils.isEmpty(result)){
                    JSONObject jsonObject=new JSONObject(result);
                    code=jsonObject.getInt("returnCode");
                    if (code==100){
                        if (payWay==1){
                            orderNumber=jsonObject.getInt("orderNo");
                            orderString=jsonObject.getString("orderString");
                        }else if (payWay==2) {
                            partnerId = jsonObject.getString("partnerid");
                            prepayId = jsonObject.getString("prepayid");
                            packageValue = jsonObject.getString("package");
                            nonceStr = jsonObject.getString("noncestr");
                            timeStamp = jsonObject.getString("timestamp");
                            sign = jsonObject.getString("sign");
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(Integer code) {
            super.onPostExecute(code);
            if (code==100){
                if (payWay==1){
                    Intent intent=new Intent(CheckstandActivity.this,PayActivity.class);
                    intent.putExtra("orderNumber",orderNumber);
                    intent.putExtra("orderString",orderString);
                    startActivity(intent);
                }else if (payWay==2){
                    Intent intent=new Intent(CheckstandActivity.this,WXPayActiviy.class);
                    intent.putExtra("partnerId",partnerId);
                    intent.putExtra("prepayId",prepayId);
                    intent.putExtra("packageValue",packageValue);
                    intent.putExtra("nonceStr",nonceStr);
                    intent.putExtra("timeStamp",timeStamp);
                    intent.putExtra("sign",sign);
                    startActivity(intent);
                }

            }
        }
    }

}
