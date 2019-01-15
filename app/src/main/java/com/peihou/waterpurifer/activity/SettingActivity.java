package com.peihou.waterpurifer.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.base.BaseActivity;
import com.peihou.waterpurifer.base.MyApplication;
import com.peihou.waterpurifer.util.CleanMessageUtil;
import com.peihou.waterpurifer.util.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {
   @BindView(R.id.iv_set_name1)
   ImageView iv_set_name1;
   @BindView(R.id.iv_set_sy)
   ImageView iv_set_sy ;
    SharedPreferences preferences;
   boolean isopen1 ;
   boolean  isopen2 ;
    MyApplication application;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_setting;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        isopen1 = preferences.getBoolean("open1",true);
        isopen2 = preferences.getBoolean("open1",true);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    @SuppressLint("ApplySharedPref")
    @OnClick({R.id.rl_set_pic,R.id.rl_set_type,R.id.iv_main_memu,R.id.rl_set_cpass})
    public void onClick(View view){
        switch (view.getId()){

            case R.id.iv_main_memu:
                finish();
                break;

            case R.id.rl_set_pic:
                if (isopen1){
                    iv_set_name1.setImageResource(R.mipmap.sz_kgg);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("open1",false);
                    editor.commit();
                    isopen1=false;
                }else {
                    iv_set_name1.setImageResource(R.mipmap.sz_kgk);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("open2",true);
                    editor.commit();
                    isopen1=true;
                }

                break;
            case R.id.rl_set_type:
                if (isopen2){
                    iv_set_sy.setImageResource(R.mipmap.sz_kgg);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("open2",false);
                    editor.commit();
                    isopen2=false;
                }else {
                    iv_set_sy.setImageResource(R.mipmap.sz_kgk);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("open2",true);
                    editor.commit();
                    isopen2=true;
                }


                break;

            case R.id.rl_set_cpass:
                try {
                   String size = CleanMessageUtil.getTotalCacheSize(getApplicationContext());
                    Log.e("success", "onClick: -->"+size );
                } catch (Exception e) {
                    e.printStackTrace();
                }
                CleanMessageUtil.clearAllCache(getApplicationContext());
                ToastUtil.showShort(this,"缓存已清理");
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
