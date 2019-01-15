package com.peihou.waterpurifer.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.base.BaseActivity;

import butterknife.OnClick;

public class HowuseActivity extends BaseActivity {
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_howuse;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    @OnClick({R.id.iv_main_memu})
    public void onClick (View view){
        switch (view.getId()){
            case R.id.iv_main_memu:
                finish();
                break;
        }
    }
}
