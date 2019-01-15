package com.peihou.waterpurifer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.peihou.waterpurifer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 是否退单
 */
public class repairDialog extends Dialog {
    TextView tv_device_del;
    private String name;
    Context context;
    public repairDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
        this.context = context;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popview_delete_device);
        ButterKnife.bind(this);
         tv_device_del = (TextView)  findViewById(R.id.tv_device_del);
        Log.e("DDDDDDDDFFFFFFF", "onCreate: -->"+tv_device_del );
    }
    public void setText (int i){
        if (i==2){
            tv_device_del.setText("确定要删除它吗？");
        }else {
            tv_device_del.setText("确定要退单吗？");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OnClick({R.id.tv_device_cancel, R.id.tv_device_ensure})
    public void onClick(View view){
        switch(view.getId()){
            case R.id.tv_device_cancel:
                if (onNegativeClickListener!=null){
                    onNegativeClickListener.onNegativeClick();
                }
                break;
            case R.id.tv_device_ensure:
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
