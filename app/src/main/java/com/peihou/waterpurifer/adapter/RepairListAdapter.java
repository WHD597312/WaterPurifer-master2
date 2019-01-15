package com.peihou.waterpurifer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.pojo.Equipment;
import java.util.List;

public class RepairListAdapter extends RecyclerView.Adapter<RepairListAdapter.MyViewHolder> {

    Context mContext;
    List<Equipment> mData;
    private  OnItemClickListener onItemClickListener;
    boolean IsChoose = false;
    int pos ;
    public RepairListAdapter(Context context , List<Equipment> list){
        this.mContext= context;
        this.mData = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_dialog_choosequ,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.tv_choose_name.setText(mData.get(position).getDeviceMac());
        if (IsChoose&&pos==position){
            holder.tv_choose_name.setTextColor(mContext.getResources().getColor(R.color.color_toblue));
        }else {
            holder.tv_choose_name.setTextColor(mContext.getResources().getColor(R.color.black));
        }

        if (position==mData.size()-1){
            holder.view1.setVisibility(View.GONE);
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

    }
    public void SetOnItemClick( OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener ;
    }
    public void  setColor (boolean b,int pos){
        this.IsChoose = b;
        this.pos = pos;
        notifyDataSetChanged();

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
        TextView tv_choose_name;
        View view1;
        public MyViewHolder(View itemView) {
            super(itemView);
            tv_choose_name = (TextView) itemView.findViewById(R.id.tv_choose_name);
            view1 = (View) itemView.findViewById(R.id.view1);

        }
    }


}
