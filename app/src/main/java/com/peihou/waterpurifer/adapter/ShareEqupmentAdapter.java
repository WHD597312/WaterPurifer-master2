package com.peihou.waterpurifer.adapter;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.activity.UserActivity;
import com.peihou.waterpurifer.device.activity.AddDeviceActivity;
import com.peihou.waterpurifer.pojo.Equipment;
import com.peihou.waterpurifer.util.view.ScreenSizeUtils;

import java.util.List;

import butterknife.OnItemClick;

public class ShareEqupmentAdapter extends RecyclerView.Adapter<ShareEqupmentAdapter.MyViewHolder> {

    private List<Equipment> mData;
    private Context context;
    private OnItemClickListener onItemClickListener;
    public ShareEqupmentAdapter(Context context , List<Equipment> list ) {
        this.context = context;
        this.mData = list;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_sharequpment,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

              holder.tv_seqp_name.setText(mData.get(position).getDeviceMac());

              holder.itemView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      onItemClickListener.onItemClick(v, position);
                  }
              });
              holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                  @Override
                  public boolean onLongClick(View v) {

                      onItemClickListener.onLongClick(v, position);
                      return false;
                  }
              });

    }
    public void SetOnItemClick(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener ;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onLongClick(View view, int posotion);
    }
    /**
     * 自定义对话框
     */
    private void customDialog() {
        final Dialog dialog = new Dialog(context, R.style.MyDialog);
        View view = View.inflate(context, R.layout.dialog_shareequ, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dialog_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dialog_qd);
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(true);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(context).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(context).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        tv_dialog_qx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.dismiss();
            }

        });
        tv_dialog_qd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }



    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_seqp_pic;
        TextView tv_seqp_name;
        RelativeLayout rl_sequitem;
        public MyViewHolder(View itemView) {
            super(itemView);
            iv_seqp_pic = (ImageView) itemView.findViewById(R.id.iv_seqp_pic);
            tv_seqp_name= (TextView)itemView.findViewById(R.id.tv_seqp_name);
            rl_sequitem=(RelativeLayout) itemView.findViewById(R.id.rl_sequitem);
        }
    }


}
