package com.peihou.waterpurifer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.activity.AccountActivity;
import com.peihou.waterpurifer.activity.CallBackActivity;
import com.peihou.waterpurifer.activity.HowuseActivity;
import com.peihou.waterpurifer.activity.JournalActivity;
import com.peihou.waterpurifer.activity.RepairActivity;
import com.peihou.waterpurifer.activity.SettingActivity;
import com.peihou.waterpurifer.activity.ShareEqupmentActivity;
import com.peihou.waterpurifer.util.ToastUtil;
import java.util.List;

public class memuAdapter extends RecyclerView.Adapter<memuAdapter.MyViewHolder> {

    private List<String> mData;
    private Context context;
    String[] mdate = {"账户", "日志", "分享设备", "报修","使用指南","反馈","设置"};
    int [] imgs = {R.mipmap.menu_zh,R.mipmap.menu_rz,R.mipmap.menu_fx,R.mipmap.menu_bx,R.mipmap.menu_syzn,R.mipmap.menu_fk,R.mipmap.menu_sz};
    public memuAdapter(Context context , List<String> list ) {

        this.context = context;
        this.mData = list;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_main_itemmenu,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
            holder.textView.setText(mdate[position]);
            holder.imageView.setImageResource(imgs[position]);
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position){
                        case 0:
                            context.startActivity(new Intent(context, AccountActivity.class));
                            break;
                        case 1:
                            context.startActivity(new Intent(context, JournalActivity.class));
                            break;
                        case 2:
                            context.startActivity(new Intent(context, ShareEqupmentActivity.class));
                            break;
                        case 3:
                           context.startActivity(new Intent(context, RepairActivity.class));
                            break;
                        case 4:
                            context.startActivity(new Intent(context, HowuseActivity.class));
                            break;
                        case 5:
                            context.startActivity(new Intent(context, CallBackActivity.class));
                            break;
                        case 6:
                            context.startActivity(new Intent(context, SettingActivity.class));
                            break;
                    }
                }
            });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }



    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        RelativeLayout relativeLayout;
        public MyViewHolder(View itemView) {
            super(itemView);
            textView= (TextView)itemView.findViewById(R.id.tv_menu_name);
            imageView=(ImageView) itemView.findViewById(R.id.iv_menu_pic);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rl_menu_1);
        }
    }


}
