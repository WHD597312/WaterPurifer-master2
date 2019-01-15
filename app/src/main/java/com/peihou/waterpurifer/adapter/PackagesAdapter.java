package com.peihou.waterpurifer.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.activity.PackagesActivity;
import com.peihou.waterpurifer.pojo.Packages;
import com.peihou.waterpurifer.pojo.Packageschild;

import java.util.List;

public class PackagesAdapter extends RecyclerView.Adapter<PackagesAdapter.MyViewHolder> {

    private List<Packageschild> mData;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private int myposition = -1;

    private int whatPos;
    PackagesActivity packagesActivity;

    public PackagesAdapter(Context context, List<Packageschild> list, Activity activity) {
        this.context = context;
        this.mData = list;
        this.packagesActivity = (PackagesActivity) activity;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyViewHolder viewholder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_packages, parent, false));
        return viewholder;
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        if (myposition == position) {
            holder.tv_buy_title.setTextColor(context.getResources().getColor(R.color.color_toblue));
            holder.tv_pakage_bz1.setTextColor(context.getResources().getColor(R.color.color_toblue));
            holder.tv_packages_price.setTextColor(context.getResources().getColor(R.color.color_toblue));
            holder.tv_packages_jq.setTextColor(context.getResources().getColor(R.color.color_toblue));
            holder.tv_pakage_bz2.setTextColor(context.getResources().getColor(R.color.color_toblue));
            holder.view.setBackgroundColor(context.getResources().getColor(R.color.color_toblue));
            holder.rl_package.setBackground(context.getDrawable(R.drawable.bg_packageblue));
        } else {
            holder.tv_buy_title.setTextColor(Color.parseColor("#bebebe"));
            holder.tv_pakage_bz1.setTextColor(Color.parseColor("#bebebe"));
            holder.tv_packages_price.setTextColor(Color.parseColor("#bebebe"));
            holder.tv_packages_jq.setTextColor(Color.parseColor("#bebebe"));
            holder.tv_pakage_bz2.setTextColor(Color.parseColor("#bebebe"));
            holder.view.setBackgroundColor(Color.parseColor("#bebebe"));
            holder.rl_package.setBackground(context.getDrawable(R.drawable.bg_packages));
        }

        holder.tv_buy_title.setText(mData.get(position).getPchildName());

        if (mData.get(position).getPchildDiscount() == 1) {
            holder.tv_packages_jq.setText("现：" + mData.get(position).getPchildNewPrice());
            holder.tv_packages_price.setText(mData.get(position).getPchildOldPrice() + "");
        } else {
            holder.tv_packages_jq.setText("现：" + mData.get(position).getPchildOldPrice());
            holder.rl_pac_old.setVisibility(View.INVISIBLE);
            holder.tv_pakage_bz2.setVisibility(View.INVISIBLE);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, position);
                setIndex(position);
                Packageschild packageschild = mData.get(position);
                if (mData.get(position).getPchildDiscount() == 1) {
                    Log.i("packageschild",packageschild.getPchildId()+"");
                    packagesActivity.setPay(packageschild);
                } else {
                    packagesActivity.setPay(packageschild);
                }
            }
        });

    }

    public void setWhatPos(int pos) {
        this.whatPos = pos;

    }

    public void setIndex(int myposition) {
        this.myposition = myposition;
        notifyDataSetChanged();
    }

    public void SetOnItemClick(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;

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
        TextView tv_buy_title, tv_pakage_bz1, tv_packages_price, tv_pakage_bz2, tv_packages_jq;
        View view;
        RelativeLayout rl_package, rl_pac_old;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_seqp_pic = (ImageView) itemView.findViewById(R.id.iv_seqp_pic);
            tv_buy_title = (TextView) itemView.findViewById(R.id.tv_buy_title);
            tv_pakage_bz1 = (TextView) itemView.findViewById(R.id.tv_pakage_bz1);
            tv_pakage_bz2 = (TextView) itemView.findViewById(R.id.tv_pakage_bz2);
            tv_packages_price = (TextView) itemView.findViewById(R.id.tv_packages_price);
            tv_packages_jq = (TextView) itemView.findViewById(R.id.tv_packages_jq);
            view = (View) itemView.findViewById(R.id.view1);
            rl_package = (RelativeLayout) itemView.findViewById(R.id.rl_package);
            rl_pac_old = (RelativeLayout) itemView.findViewById(R.id.rl_pac_old);
        }
    }


}
