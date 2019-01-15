package com.peihou.waterpurifer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.activity.DetailsActivity;
import com.peihou.waterpurifer.pojo.Equipment;
import com.peihou.waterpurifer.util.ToastUtil;

import java.math.BigDecimal;
import java.util.List;

public class baseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Equipment> mData;
    private Equipment mEquipment;
    private Context context;
    private  boolean click;
    private  boolean hasData=false;//是否接收到消息
    public enum Item_Type {
        RECYCLEVIEW_ITEM_TYPE_1,
        RECYCLEVIEW_ITEM_TYPE_2,
        RECYCLEVIEW_ITEM_TYPE_3
    }

    public baseAdapter(Context context ,List<Equipment> list, Equipment  equipment){
        this.context = context;
        this.mData = list;
        this.mEquipment = equipment;

    }



    @Override
    public int getItemCount() {
        return mData.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Item_Type.RECYCLEVIEW_ITEM_TYPE_1.ordinal()) {
            MyViewholder viewholder = new MyViewholder(LayoutInflater.from(context).inflate(R.layout.activity_itemtouch,parent,false));
            return viewholder;

        } else if (viewType == Item_Type.RECYCLEVIEW_ITEM_TYPE_2.ordinal()) {

            View mView = LayoutInflater.from(context).inflate(R.layout.activity_itemtouch1, parent,false);
            ViewHolderB viewHolderb = new ViewHolderB(mView);
            return viewHolderb;
        } else if (viewType == Item_Type.RECYCLEVIEW_ITEM_TYPE_3.ordinal()) {
            View mView = LayoutInflater.from(context).inflate(R.layout.activity_itemtouch2, parent,false);
            ViewHolderC viewHolder = new ViewHolderC(mView);
            return viewHolder;
        }

        return null;
    }
    public void haveGetData(boolean b,Equipment equipment){
        this.hasData = b;
        this.mEquipment = equipment;
    }
    public boolean gethasData(){
        return hasData;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if (position>6){
            ((MyViewholder) holder).relativeLayout.setVisibility(View.GONE);
        }
        if (holder instanceof MyViewholder) {
            if(hasData){
                if (position==0){
                    ((MyViewholder) holder).tv_pens.setText(mEquipment.getWPurifierfilter1()+"%");
                }
                if (position==1){
                    ((MyViewholder) holder).tv_pens.setText(mEquipment.getWPurifierfilter2()+"%");
                }
                if (position==2){
                    ((MyViewholder) holder).tv_pens.setText(mEquipment.getWPurifierfilter3()+"%");
                }
                if (position==3){
                    ((MyViewholder) holder).tv_pens.setText(mEquipment.getWPurifierfilter4()+"%");
                }
                if (position==4){
                    ((MyViewholder) holder).tv_pens.setText(mEquipment.getWPurifierfilter5()+"%");
                }
            }

            ((MyViewholder) holder).tv_num.setText(position+1 + "");
            ((MyViewholder) holder).tv_name.setText(mData.get(position).getName());
            ((MyViewholder) holder).relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hasData){
                        if (!TextUtils.isEmpty(mEquipment.getWPurifierfilter1()+"")){
                        Intent intent = new Intent(context, DetailsActivity.class);
                        intent.putExtra("position",position);
                        int [] size = {mEquipment.getWPurifierfilter1(),mEquipment.getWPurifierfilter2(),mEquipment.getWPurifierfilter3()
                        ,mEquipment.getWPurifierfilter4(),mEquipment.getWPurifierfilter5() };
                        intent.putExtra("wPurifierfilter",size[position]);
                        context.startActivity(intent);
                        }
                    }else {
                        ToastUtil.showShort(context,"设备离线，请检测设备");
                    }

                }
            });
            if (position==4){
                ((MyViewholder) holder).view_1.setVisibility(View.GONE);
            }
        } else if (holder instanceof ViewHolderB) {

        } else if (holder instanceof ViewHolderC) {
//            ((ViewHolderC) holder).tv_ysl_totle.setText(mEquipment.);
            if (hasData){
                int trueFlowmeter = Integer.valueOf(mEquipment.getWTrueFlowmeter());
                if (trueFlowmeter<1000){
                    ((ViewHolderC) holder).tv_ysl_totle.setText( trueFlowmeter+"");
                    ((ViewHolderC) holder).tv_ysl_dw2.setText( "L");

                }else {
                    BigDecimal b = new BigDecimal(trueFlowmeter/1000);
                    //保留1位小数
                    double f1 = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
                    ((ViewHolderC) holder).tv_ysl_totle.setText( f1+"");
                    ((ViewHolderC) holder).tv_ysl_dw2.setText( "t");
                }


            }
            ((ViewHolderC) holder).tv_ysl_today.setText(mEquipment.getTodayUse()+"");

        }

    }

    //返回值赋值给onCreateViewHolder的参数 viewType
    @Override
    public int getItemViewType(int position) {

        if (mData.get(position).getType() == 0) {
            return Item_Type.RECYCLEVIEW_ITEM_TYPE_1.ordinal();
        } else if (mData.get(position).getType() == 1) {
            return Item_Type.RECYCLEVIEW_ITEM_TYPE_2.ordinal();
        } else if (mData.get(position).getType() == 2) {
            return Item_Type.RECYCLEVIEW_ITEM_TYPE_3.ordinal();
        }
        return -1;
    }

    class MyViewholder extends RecyclerView.ViewHolder{
        TextView tv_num , tv_name ,tv_pens;
        View view_1;
        RelativeLayout relativeLayout ;
        public MyViewholder(View itemView) {
            super(itemView);
            tv_num= (TextView)itemView.findViewById(R.id.tv_num);
            tv_name= (TextView)itemView.findViewById(R.id.tv_name);
            tv_pens= (TextView)itemView.findViewById(R.id.tv_pens);
            view_1= (View)itemView.findViewById(R.id.view_1) ;
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rl_item_father);
        }
    }
    public void  setClickable (boolean b){
        this.click = b;
    }

    class ViewHolderB extends RecyclerView.ViewHolder {

        public TextView text;

        public ViewHolderB(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }

    class ViewHolderC extends RecyclerView.ViewHolder {

        public TextView tv_ysl_totle,tv_ysl_today,tv_ysl_dw2;

        public ViewHolderC(View itemView) {
            super(itemView);
            tv_ysl_totle = (TextView) itemView.findViewById(R.id.tv_ysl_totle);
            tv_ysl_today = (TextView) itemView.findViewById(R.id.tv_ysl_today);
            tv_ysl_dw2 = (TextView) itemView.findViewById(R.id.tv_ysl_dw2);
        }
    }


}
