package com.peihou.waterpurifer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.peihou.waterpurifer.R;
import com.weigan.loopview.LoopView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TimeTaskDialog extends Dialog {
    private Context context;
    @BindView(R.id.openHour) LoopView openHour;
    @BindView(R.id.openMin) LoopView openMin;
    @BindView(R.id.closeHour) LoopView closeHour;
    @BindView(R.id.closeMin) LoopView closeMin;
    private List<String> hours=new ArrayList<>();
    private List<String> mins=new ArrayList<>();
    public TimeTaskDialog(@NonNull Context context) {
        super(context);
        this.context=context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_timer_task);
        ButterKnife.bind(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        for (int i = 0; i <=24 ; i++) {
            hours.add(""+i);
        }
        for (int i = 0; i <=24 ; i++) {
            hours.add(""+i);
        }
        for (int i = 0; i <12 ; i++) {
            hours.add(""+i);
        }
        openHour.setItems(hours);
        openHour.setCenterTextColor(Color.parseColor("#22ccfd"));
        openHour.setOuterTextColor(Color.parseColor("#bbbbbb"));
        openHour.setTextSize(18);
        openHour.setInitPosition(0);
        openHour.setItemsVisibleCount(7);

        closeHour.setItems(hours);
        closeHour.setCenterTextColor(Color.parseColor("#22ccfd"));
        closeHour.setOuterTextColor(Color.parseColor("#bbbbbb"));
        closeHour.setTextSize(18);
        closeHour.setInitPosition(0);
        closeHour.setItemsVisibleCount(7);

        for (int i = 0; i <=59; i++) {
            mins.add(""+i);
        }
        openMin.setItems(mins);
        openMin.setCenterTextColor(Color.parseColor("#22ccfd"));
        openMin.setOuterTextColor(Color.parseColor("#bbbbbb"));
        openMin.setTextSize(18);
        openMin.setInitPosition(0);
        openMin.setItemsVisibleCount(7);

        closeMin.setItems(mins);
        closeMin.setCenterTextColor(Color.parseColor("#22ccfd"));
        closeMin.setOuterTextColor(Color.parseColor("#bbbbbb"));
        closeMin.setTextSize(18);
        closeMin.setInitPosition(0);
        closeMin.setItemsVisibleCount(7);


    }

    @OnClick({R.id.button_cancel, R.id.button_ensure})
    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_cancel:
                if (onNegativeClickListener!=null){
                    onNegativeClickListener.onNegativeClick();
                }
                break;
            case R.id.button_ensure:
                if (onPositiveClickListener!=null){
                    onPositiveClickListener.onPositiveClick();
                }
                break;
        }
    }
    private OnPositiveClickListener onPositiveClickListener;

    public void setOnPositiveClickListener(OnPositiveClickListener onPositiveClickListener) {


        this.onPositiveClickListener = onPositiveClickListener;
    }

    private OnNegativeClickListener onNegativeClickListener;

    public void setOnNegativeClickListener(OnNegativeClickListener onNegativeClickListener) {

        this.onNegativeClickListener = onNegativeClickListener;
    }

    public interface OnPositiveClickListener {
        void onPositiveClick();
    }

    public interface OnNegativeClickListener {
        void onNegativeClick();
    }
}
