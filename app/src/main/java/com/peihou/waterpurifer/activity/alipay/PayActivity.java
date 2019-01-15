package com.peihou.waterpurifer.activity.alipay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.alipay.sdk.app.PayTask;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.base.BaseActivity;
import com.peihou.waterpurifer.base.MyApplication;
import java.util.HashMap;
import java.util.Map;



public class PayActivity extends BaseActivity {
    MyApplication application;
    String orderNumber;
    String orderString;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_pay;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        Bundle extras = getIntent().getExtras();
        orderNumber=extras.getString("orderNumber");
        orderString=extras.getString("orderString");
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    Map<String,String> map=new HashMap<>();

    private static final int SDK_PAY_FLAG = 1;

  // 订单信息
    Runnable payRunnable = new Runnable() {

        @Override
        public void run() {
            PayTask alipay = new PayTask(PayActivity.this);
            Map<String, String> result = alipay.payV2(orderString, true);
            Message msg = new Message();
            msg.what = SDK_PAY_FLAG;
            msg.obj = result;
            mHandler.sendMessage(msg);
        }
    };




    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();

                    Log.e("qqqqqFFFF",resultInfo+","+resultStatus);
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(PayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
//                        Intent intent=new Intent(PayActivity.this, PaySuccessActivity.class);
//                        intent.putExtra("orderNumber",orderNumber);
//                        startActivity(intent);
                        finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(PayActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
//                        Intent intent=new Intent(PayActivity.this, PayFailActivity.class);
//                        intent.putExtra("type","Pay");
//                        intent.putExtra("orderNumber",orderNumber);
//                        startActivity(intent);
                        finish();
                    }
                    break;
                }
//                case SDK_AUTH_FLAG: {
//                    @SuppressWarnings("unchecked")
//                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
//                    String resultStatus = authResult.getResultStatus();
//
//                    // 判断resultStatus 为“9000”且result_code
//                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
//                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
//                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
//                        // 传入，则支付账户为该授权账户
//                        Toast.makeText(PayDemoActivity.this,
//                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
//                                .show();
//                    } else {
//                        // 其他状态值则为授权失败
//                        Toast.makeText(PayDemoActivity.this,
//                                "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();
//
//                    }
//                    break;
//                }
                default:
                    break;
            }
        };
    };
}