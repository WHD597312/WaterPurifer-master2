package com.peihou.waterpurifer.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.peihou.waterpurifer.PayFailActivity;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.activity.PaySuccessActivity;
import com.peihou.waterpurifer.base.MyApplication;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;



public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;
    String appid="wx44acbeed9571e8cf";
    String orderNumber;
    MyApplication application;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        application = (MyApplication) getApplication();
        api = WXAPIFactory.createWXAPI(this,appid);
        api.handleIntent(getIntent(), this);


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
//        SharedPreferences userSettings = getSharedPreferences("order", 0);
//        orderNumber = userSettings.getString("orderNumber", "0");
        //有时候支付结果还需要发送给服务器确认支付状态
        if (resp.getType()== ConstantsAPI.COMMAND_PAY_BY_WX){
            if (resp.errCode==0){
//                Toast.makeText(WXPayEntryActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
//                Intent intent=new Intent(WXPayEntryActivity.this, PaySuccessActivity.class);
//                intent.putExtra("orderNumber",orderNumber);
//                startActivity(intent);
                Toast.makeText(this,"111111111",Toast.LENGTH_SHORT).show();
                finish();
            }else if (resp.errCode==-2){

//                Toast.makeText(WXPayEntryActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
//                Intent intent=new Intent(WXPayEntryActivity.this, PayFailActivity.class);
//                intent.putExtra("type","Pay");
//                intent.putExtra("orderNumber",orderNumber);
//                startActivity(intent);
                Toast.makeText(this,"2222222",Toast.LENGTH_SHORT).show();

                finish();
            }else {
                Toast.makeText(this,"支付失败",Toast.LENGTH_LONG).show();
                Log.e("qqqqqqSSS",resp.errStr+"?");
            }
            application.destoryActivity("WXPayActivity");
            finish();

        }


    }
}