package com.peihou.waterpurifer.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.base.BaseActivity;
import com.peihou.waterpurifer.base.MyApplication;

import butterknife.BindView;
import butterknife.OnClick;

public class DetailsActivity extends BaseActivity {
    int position ,wPurifierfilter;
    @BindView(R.id.rl_xq_1)
    RelativeLayout rl_xq_1;
    @BindView(R.id.rl_xq_2)
    RelativeLayout rl_xq_2;
    @BindView(R.id.rl_xq_3)
    RelativeLayout rl_xq_3;
    @BindView(R.id.rl_xq_4)
    RelativeLayout rl_xq_4;
    @BindView(R.id.rl_xq_5)
    RelativeLayout rl_xq_5;
    @BindView(R.id.tv_lvx_name)
    TextView tv_lvx_name;
    @BindView(R.id.tv_lvx_sy)
    TextView tv_lvx_sy;
    @BindView(R.id.tv_xq_text)
    TextView tv_xq_text;

    MyApplication application;
    @Override
    public void initParms(Bundle parms) {


    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_lvxq;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
          Intent intent = getIntent();
         position= intent.getIntExtra("position",-1);
        wPurifierfilter = intent.getIntExtra("wPurifierfilter",90);
        tv_lvx_sy.setText("剩余"+wPurifierfilter+"%");
         if (position==1){
             rl_xq_1.setBackground(getResources().getDrawable(R.drawable.bg_lvxg));
             rl_xq_2.setBackground(getResources().getDrawable(R.drawable.bg_lvx));
             tv_lvx_name.setText("UDF颗粒活性炭滤芯");
             tv_xq_text.setText("颗粒活性炭滤芯（UDF）集吸附、过滤拦截、催化作用于一体，可有效去除杀虫剂、农药残余物、有机溶剂及其他工业造成的化学污染。还能有效去除水中的有机物、余氯及其他具有放射性的物质，并有脱色、去味的效果。");
         }else if (position==2){
             rl_xq_1.setBackground(getResources().getDrawable(R.drawable.bg_lvxg));
             rl_xq_3.setBackground(getResources().getDrawable(R.drawable.bg_lvx));
             tv_lvx_name.setText("CTO压缩活性炭滤芯");
             tv_xq_text.setText("在净水器中压缩活性炭滤芯（CTO）可以深层吸附水中的异色、异味、余氯，并过滤掉细微杂质。在纯水机中用于第三级，还起到保护RO膜的作用。");
         }else if (position==3){
             rl_xq_1.setBackground(getResources().getDrawable(R.drawable.bg_lvxg));
             rl_xq_4.setBackground(getResources().getDrawable(R.drawable.bg_lvx));
             tv_lvx_name.setText("RO反渗透滤芯");
             tv_xq_text.setText("RO膜能够有效的去除水中钙、镁、细菌、有机物、无机物、金属离子和放射性物质等，经过该装置净化出的水晶莹清澈、甜美甘醇。");
         }else if (position==4){
             rl_xq_1.setBackground(getResources().getDrawable(R.drawable.bg_lvxg));
             rl_xq_5.setBackground(getResources().getDrawable(R.drawable.bg_lvx));
             tv_lvx_name.setText("T33后置活性炭滤芯");
             tv_xq_text.setText("作为纯水机的最后一道工序，可以进一步吸附水中异色异味，增加净化水的含氧量，主要改善水的口感。尤其是对使用压力储水桶的纯水机，后置活性炭可以有效吸附掉储水桶产生的异味。");
         }
    }

    @Override
    public void doBusiness(Context mContext) {

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
}
