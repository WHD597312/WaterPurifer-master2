package com.peihou.waterpurifer.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import com.peihou.waterpurifer.pojo.Equipment;
import com.peihou.waterpurifer.util.view.ScreenSizeUtils;

import java.util.List;

public class AccEqupmentAdapter extends RecyclerView.Adapter<AccEqupmentAdapter.MyViewHolder> {

    private List<Equipment> mData;
    private Context context;
    private OnItemClickListener onItemClickListener;
    public AccEqupmentAdapter(Context context , List<Equipment> list ) {
        this.context = context;
        this.mData = list;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_accequment,parent,false));
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



    @Override
    public int getItemCount() {
        return mData.size();
    }



    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_seqp_pic;
        TextView tv_seqp_name,tv_buy_day,tv_buy_bz;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_seqp_pic = (ImageView) itemView.findViewById(R.id.iv_seqp_pic);
            tv_seqp_name= (TextView)itemView.findViewById(R.id.tv_seqp_name);
            tv_buy_day= (TextView)itemView.findViewById(R.id.tv_buy_day);
            tv_buy_bz= (TextView)itemView.findViewById(R.id.tv_buy_bz);
        }
    }


}
