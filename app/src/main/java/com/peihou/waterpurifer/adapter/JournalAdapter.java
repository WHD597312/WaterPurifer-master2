package com.peihou.waterpurifer.adapter;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.pojo.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.MyViewHolder> {

    private List<Data> mData;
    private Context context;
    private OnItemClickListener onItemClickListener;
    public JournalAdapter(Context context , List<Data> list ) {
        this.context = context;
        this.mData = list;
    }

    public void setmData(List<Data> list) {
           this. mData= list;
//           this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_daylog,parent,false));
        return holder;
    }
    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {


            holder.iv_day_pic.setImageResource(R.mipmap.day_gz);
            holder.tv_day_gzyy.setText(mData.get(position).getFaultType());
            holder.tv_day_time.setText(""+getDateToString(Long.valueOf(mData.get(position).getFaultTime()),"HH:mm:ss"));
            holder.tv_day_sdsj.setText("设备名称："+mData.get(position).getFaultDeviceMac());
//              holder.itemView.setOnClickListener(new View.OnClickListener() {
//                  @Override
//                  public void onClick(View v) {
//                      onItemClickListener.onItemClick(v, position);
//                  }
//              });
//              holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                  @Override
//                  public boolean onLongClick(View v) {
//
//                      onItemClickListener.onLongClick(v, position);
//                      return false;
//                  }
//              });

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
        ImageView iv_day_pic;
        TextView tv_day_zs,tv_day_gzyy,tv_day_time,tv_day_sdsj;
//        RelativeLayout rl_sequitem;
        public MyViewHolder(View itemView) {
            super(itemView);
            iv_day_pic = (ImageView) itemView.findViewById(R.id.iv_day_pic);
            tv_day_zs= (TextView)itemView.findViewById(R.id.tv_day_zs);
            tv_day_gzyy= (TextView)itemView.findViewById(R.id.tv_day_gzyy);
            tv_day_time =(TextView)itemView.findViewById(R.id.tv_day_time);
            tv_day_sdsj= (TextView) itemView.findViewById(R.id.tv_day_sdsj);
//            rl_sequitem=(RelativeLayout) itemView.findViewById(R.id.rl_sequitem);
        }
    }


}
