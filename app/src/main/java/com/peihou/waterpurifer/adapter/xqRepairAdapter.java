package com.peihou.waterpurifer.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.dialog.repairDialog;
import com.peihou.waterpurifer.pojo.RepairList;
import com.peihou.waterpurifer.util.HttpUtils;
import com.peihou.waterpurifer.util.ToastUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class xqRepairAdapter extends RecyclerView.Adapter<xqRepairAdapter.MyViewHolder> {

    private List<RepairList> mData;
    private Context context;
    private String[] list= {"等待接单","正在处理","处理完成","已删除"};
    private  String repairId;
    private RepairList repairList;

    public xqRepairAdapter(Context context , List<RepairList> list ) {
        this.context = context;
        this.mData = list;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_xqrepair,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
             holder.tv_xqre_type1.setText(mData.get(position).getRepairDeviceType());
             holder.tv_xqre_jdzt1.setText(list[mData.get(position).getRepairFlag()]);

             if (mData.get(position).getRepairFlag()==2){
                 holder.bt_xqre_td.setText("删除");
                 holder.bt_xqre_td.setTextColor(context.getResources().getColor(R.color.red_pressed));
                 holder.bt_xqre_td.setBackground(context.getDrawable(R.drawable.bg_xqrepair_td));
             } else  if (mData.get(position).getRepairFlag()==3){
                 holder.bt_xqre_td.setText("已删除");
                 holder.bt_xqre_td.setTextColor(context.getResources().getColor(R.color.color_gray2));
                 holder.bt_xqre_td.setBackground(context.getDrawable(R.drawable.bg_xqrepair_td1));
             }else {
                 holder.bt_xqre_td.setText("退单");
                 holder.bt_xqre_td.setTextColor(context.getResources().getColor(R.color.color_toblue));
                 holder.bt_xqre_td.setBackground(context.getDrawable(R.drawable.bg_xqrepair_td));
             }
             holder.tv_xqre_yytime1.setText(mData.get(position).getRepairTime());
             holder.bt_xqre_td.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                    deleteDeviceDialog(position,mData.get(position).getRepairFlag());
                 }
             });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private void deleteDeviceDialog(final int pos,final int Flag) {
        final repairDialog dialog = new repairDialog(context);

        dialog.setOnNegativeClickListener(new repairDialog.OnNegativeClickListener() {
            @Override
            public void onNegativeClick() {
                dialog.dismiss();
            }
        });
        dialog.setOnPositiveClickListener(new repairDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClick() {
                repairId=mData.get(pos).getRepairId()+"";
                Log.e("result", "onPositiveClick: -->"+repairId );
                repairList = mData.get(pos);
                new DeleteRepairAsyncTask().execute();
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.setText(Flag);
    }
    class DeleteRepairAsyncTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            String code = "";
            String result = HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+"/app/user/deleteRepair?repairId="+repairId);
            Log.e("result", "doInBackground: --》"+result );
            try {
                JSONObject jsonObject = new JSONObject(result);
                code = jsonObject.getString("returnCode");


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (s){
                case "100":
                   ToastUtil.showShort(context,"退单成功");
                   mData.remove(repairList);
                   notifyDataSetChanged();
                    break;

                    default:
                    ToastUtil.showShort(context,"退单失败，请重试");
                        break;
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_xqre_type1,tv_xqre_jdzt1,tv_xqre_yytime1;
        Button bt_xqre_td;
        public MyViewHolder(View itemView) {
            super(itemView);
            tv_xqre_type1= (TextView)itemView.findViewById(R.id.tv_xqre_type1);
            tv_xqre_jdzt1= (TextView)itemView.findViewById(R.id.tv_xqre_jdzt1);
            tv_xqre_yytime1= (TextView)itemView.findViewById(R.id.tv_xqre_yytime1);
            bt_xqre_td=(Button) itemView.findViewById(R.id.bt_xqre_td);
        }
    }


}
