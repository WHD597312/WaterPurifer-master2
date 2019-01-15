package com.peihou.waterpurifer.activity;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.base.BaseActivity;
import com.peihou.waterpurifer.base.MyApplication;
import com.peihou.waterpurifer.util.HttpUtils;
import com.peihou.waterpurifer.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


public class ForgetPswdActivity extends BaseActivity {
    MyApplication application;
    @BindView(R.id.et_fg_phone)
    EditText et_phone;
    @BindView(R.id.et_fg_code)
    EditText et_code;
    @BindView(R.id.et_fg_password)
    EditText et_password;
    @BindView(R.id.btn_fg_code)
    Button btn_get_code;
    SharedPreferences preferences;
    boolean isHideFirst;
    private boolean ready;
    String phone;
    String password;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_forgtpassword;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        application.addActivity(this);
        if (Build.VERSION.SDK_INT >= 23) {
            int readPhone = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
            int receiveSms = checkSelfPermission(Manifest.permission.RECEIVE_SMS);
            int readSms = checkSelfPermission(Manifest.permission.READ_SMS);
//            int readContacts = checkSelfPermission(Manifest.permission.READ_CONTACTS);读取俩昔日
            int readSdcard = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

            int requestCode = 0;
            ArrayList<String> permissions = new ArrayList<String>();
            if (readPhone != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 0;
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (receiveSms != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 1;
                permissions.add(Manifest.permission.RECEIVE_SMS);
            }
            if (readSms != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 2;
                permissions.add(Manifest.permission.READ_SMS);
            }
//            if (readContacts != PackageManager.PERMISSION_GRANTED) {
//                requestCode |= 1 << 3;
//                permissions.add(Manifest.permission.READ_CONTACTS);
//            }
            if (readSdcard != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 4;
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (requestCode > 0) {
                String[] permission = new String[permissions.size()];
                this.requestPermissions(permissions.toArray(permission), requestCode);
                return;
            }
        }
        registerSDK();
    }

    private void registerSDK() {
        // 在尝试读取通信录时以弹窗提示用户（可选功能）
        SMSSDK.setAskPermisionOnReadContact(false);
        final Handler handler = new Handler((Handler.Callback) this);
        EventHandler eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        // 注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);
        ready = true;
    }

    @Override
    public void doBusiness(Context mContext) {

    }
    @OnClick({R.id.btn_fg_finish,R.id.btn_fg_code,R.id.iv_for_fh})
    @Override
    public void widgetClick(View v) {

        switch (v.getId()) {
            case R.id.btn_fg_finish:

                String code=et_code.getText().toString().trim();
                 password=et_password.getText().toString().trim();
                if (TextUtils.isEmpty(phone)){
                    ToastUtil.showShort(this,"手机号码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(code)){
                    ToastUtil.showShort(this,"请输入验证码");

                    return;
                }
                if (TextUtils.isEmpty(password)){
                    ToastUtil.showShort(this,"请输入密码");
                    return;
                }
                if (password.length()<6||password.length()>18){
                    ToastUtil.showShort(this,"密码位数应该大于6小于18");
                }else {
                    Map<String,Object> params=new HashMap<>();
                    params.put("phone",phone);
                    params.put("code",code);
                    params.put("password",password);
                    new ForgetAsyncTask().execute(params);
                }

//                new getShopAsync().execute(params);
                break;
            case R.id.btn_fg_code:
                 phone = et_phone.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort(this,"手机号码不能为空");
                } else {
                    Map<String,Object> params = new HashMap<>();
                    params.put("phone",phone);
                    new hasPhoneAsyncTask().execute(params);

//                    Map<String,Object> params1=new HashMap<>();
//                    params1.put("phone",phone);
//                    new PhoneExiseAsyncTask().execute(params1);
                }
                break;
            case R.id.iv_for_fh:
                finish();
                break;
        }


    }
    class ForgetAsyncTask extends AsyncTask<Map<String,Object>,Void,String>{

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> param = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/user/forgetPassword",param);
            if (!TextUtils.isEmpty(result)){
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getString("returnCode");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return code;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (s){
                case "100":
                    toast("修改成功");
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("phone",phone);
                    editor.putString("password",password);
                    editor.commit();
                    startActivity(LoginActivity.class);
                    break;
                default:

                    break;
            }
        }
    }



    class hasPhoneAsyncTask extends AsyncTask<Map<String,Object>,Void,String>{

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> param = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/user/questPhoneIsExist",param);
            Log.e("result", "doInBackground: -->"+result );
            if (!TextUtils.isEmpty(result)){
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getString("returnCode");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return code;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (s){
                case "100":
                    SMSSDK.getVerificationCode("86",phone);
                    countTimer=new CountTimer(60000,1000);
                    countTimer.start();
                    break;
                    default:
                    toast("账号不存在，请注册新的账号");
                        break;
            }
        }
    }

    CountTimer countTimer;
    class CountTimer extends CountDownTimer {
        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        /**
         * 倒计时过程中调用
         *
         * @param millisUntilFinished
         */
        @Override
        public void onTick(long millisUntilFinished) {
            Log.e("Tag", "倒计时=" + (millisUntilFinished/1000));
            if (btn_get_code!=null){
                btn_get_code.setText(millisUntilFinished / 1000 + "s");
                //设置倒计时中的按钮外观
                btn_get_code.setClickable(false);//倒计时过程中将按钮设置为不可点击
//            btn_get_code.setBackgroundColor(Color.parseColor("#c7c7c7"));
                btn_get_code.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray));
                btn_get_code.setTextSize(16);
            }
        }

        /**
         * 倒计时完成后调用
         */
        @Override
        public void onFinish() {
            Log.e("Tag", "倒计时完成");
            //设置倒计时结束之后的按钮样式
//            btn_get_code.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_blue_light));
//            btn_get_code.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
            if (btn_get_code!=null){
                btn_get_code.setTextSize(16);
                btn_get_code.setText("重新发送");
                btn_get_code.setClickable(true);
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        if (ready) {
            // 销毁回调监听接口
            SMSSDK.unregisterAllEventHandler();
        }
        if (countTimer!=null){
            countTimer.cancel();
        }

        super.onDestroy();
    }
}
