package com.peihou.waterpurifer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.base.MyApplication;
import com.peihou.waterpurifer.device.activity.AddDeviceActivity;
import com.peihou.waterpurifer.pojo.Equipment;

import java.util.List;

public class EqupmentAdapter extends RecyclerView.Adapter<EqupmentAdapter.MyViewHolder> {

    private List<Equipment> mData;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private Activity activity ;
    MyApplication application;
    private boolean isShare=false;
    public EqupmentAdapter(Context context , List<Equipment> list , Activity activity, MyApplication application) {
        this.context = context;
        this.mData = list;
        this.application = application;
        this.activity = activity;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_equpment,parent,false));
        return holder;
    }

    public void setShare(boolean share) {
        isShare = share;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        if (position<mData.size()-1) {
//            holder.tv_eqp_zx.setText("离线");
//            holder.tv_eqp_kg.setText("电源关");
            holder.tv_eqp_kg.setText("电源开");
            holder.tv_eqp_kg.setTextColor(context.getResources().getColor(R.color.color_toblue));
            holder.tv_eqp_zx.setText("在线");
//            holder.tv_eqp_kg.setTextColor(context.getResources().getColor(R.color.color_gray2));
            holder.iv_eqp_kg.setImageResource(R.mipmap.equpment_kgk);
            holder.tv_eqp_zx.setTextColor(context.getResources().getColor(R.color.color_toblue));
            if (!isShare){
                holder.iv_eqp_lx.setAlpha(0f);
                holder.iv_eqp_share.setVisibility(View.GONE);
                holder.tv_eqp_name.setText(mData.get(position).getName());
                holder.iv_eqp_pic.setImageResource(R.mipmap.equ_jsq);

            }else {
                holder.iv_eqp_lx.setAlpha(0f);
                holder.iv_eqp_share.setVisibility(View.VISIBLE);
                holder.tv_eqp_name.setText(mData.get(position).getName());
                holder.iv_eqp_pic.setImageResource(R.mipmap.equ_jsq);
            }
            if (mData.get(position).getHaData()) {
                 if (mData.get(position).getIsOpen() == 0) {
                holder.tv_eqp_kg.setText("电源关");
                holder.tv_eqp_kg.setTextColor(context.getResources().getColor(R.color.color_gray2));
                holder.tv_eqp_zx.setText("在线");
                holder.iv_eqp_kg.setImageResource(R.mipmap.equpment_kg);
                holder.tv_eqp_zx.setTextColor(context.getResources().getColor(R.color.color_toblue));
                } else {
                holder.tv_eqp_kg.setText("电源开");
                holder.tv_eqp_kg.setTextColor(context.getResources().getColor(R.color.color_toblue));
                holder.tv_eqp_zx.setText("在线");
                holder.iv_eqp_kg.setImageResource(R.mipmap.equpment_kgk);
                holder.tv_eqp_zx.setTextColor(context.getResources().getColor(R.color.color_toblue));
                  }

            }

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

        }else {
            if (!isShare){
                Log.e("qqqqHHH",holder.rl_equitem.getMeasuredHeight()+"??");
                holder.iv_eqp_share.setVisibility(View.GONE);
                holder.rl_equitem.setVisibility(View.VISIBLE);
                holder.iv_eqp_lx.setVisibility(View.INVISIBLE);
                holder.tv_eqp_kg.setText("");
                holder.iv_eqp_kg.setVisibility(View.INVISIBLE);
                holder.iv_eqp_pic.setImageResource(R.mipmap.equpment_add);
                holder.tv_eqp_name.setText("添加设备");
                holder.tv_eqp_zx.setText("");
                holder.rl_equitem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                    context.startActivity(new Intent(context, AddDeviceActivity.class));
                        activity.startActivityForResult(new Intent(context, AddDeviceActivity.class),600);
                    }
                });
            }else {
                holder.rl_equitem.setVisibility(View.GONE);
            }


        }

//        }else if ("1".equals(mData.get(position))){
//            holder.iv_eqp_lx.setAlpha(1f);
//            holder.tv_eqp_name.setText("净水器");
//            holder.tv_eqp_kg.setText("电源关");
//            holder.tv_eqp_kg.setTextColor(context.getResources().getColor(R.color.color_gray2));
//            holder.iv_eqp_kg.setImageResource(R.mipmap.equpment_kg);
//            holder.tv_eqp_zx.setText("离线");
//            holder.tv_eqp_zx.setTextColor(context.getResources().getColor(R.color.color_gray2));
//        }




    }
    public void RefrashData(List<Equipment> list){
        this.mData =list;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void SetOnItemClick(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener ;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onLongClick(View view, int posotion);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_eqp_name,tv_eqp_kg,tv_eqp_zx,iv_eqp_share;
        ImageView iv_eqp_lx,iv_eqp_pic,iv_eqp_kg;
        RelativeLayout rl_equitem;
        public MyViewHolder(View itemView) {
            super(itemView);
            iv_eqp_lx = (ImageView) itemView.findViewById(R.id.iv_eqp_lx);
            iv_eqp_pic = (ImageView) itemView.findViewById(R.id.iv_eqp_pic);
            tv_eqp_name= (TextView)itemView.findViewById(R.id.tv_eqp_name);
            tv_eqp_kg= (TextView)itemView.findViewById(R.id.tv_eqp_kg);
            tv_eqp_zx= (TextView)itemView.findViewById(R.id.tv_eqp_zx);
            iv_eqp_kg = (ImageView) itemView.findViewById(R.id.iv_eqp_kg);
            iv_eqp_share = (TextView) itemView.findViewById(R.id.iv_eqp_share);
            rl_equitem=(RelativeLayout) itemView.findViewById(R.id.rl_equitem);

        }
    }


}
