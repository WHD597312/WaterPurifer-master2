package com.peihou.waterpurifer.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import com.peihou.waterpurifer.MainActivity;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.adapter.EqupmentAdapter;
import com.peihou.waterpurifer.adapter.SpaceItemDecoration;
import com.peihou.waterpurifer.base.BaseActivity;
import com.peihou.waterpurifer.base.MyApplication;
import com.peihou.waterpurifer.database.dao.daoImp.EquipmentImpl;
import com.peihou.waterpurifer.pojo.Equipment;
import com.peihou.waterpurifer.util.HttpUtils;
import com.peihou.waterpurifer.util.ToastUtil;
import com.peihou.waterpurifer.util.view.ScreenSizeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import butterknife.BindView;
import butterknife.OnClick;

public class EqupmentActivity extends BaseActivity {
    @BindView(R.id.rv_equment)
    RecyclerView rv_equment;
    MyApplication application;
    String deviceMac,userId;
    EquipmentImpl equmentDao;
    Equipment equipment;
    List<Equipment> equipments;
    private ProgressDialog progressDialog;
    SharedPreferences preferences;
    EqupmentAdapter equpmentAdapter;
    boolean isShare=false;
    public static boolean isRunning = false;
    MessageReceiver receiver;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_equpment;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        isRunning = true;
        application.addActivity(this);
        equmentDao = new EquipmentImpl(getApplicationContext());
        equipment = new Equipment();
//        equipments = new ArrayList<>();
//        Log.e("ddddd", "initView: -->"+ equipments.size() );
        progressDialog = new ProgressDialog(this);
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        userId = preferences.getString("userId","");
//        equipments = equmentDao.findAll();
        equipments = equmentDao.findDeviceByRoleFlag(0);
        equipments.add(equipment);
        equpmentAdapter = new EqupmentAdapter(this, equipments,this,application);
        rv_equment.setLayoutManager(new GridLayoutManager(this,2));
        rv_equment.addItemDecoration(new SpaceItemDecoration(22,22,25));
        rv_equment.setAdapter(equpmentAdapter);
        equpmentAdapter.SetOnItemClick(new EqupmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.e("share", "onItemClick: -->"+isShare );
                if (!isShare){
                    Intent intent = new Intent(EqupmentActivity.this,MainActivity.class);
                    intent.putExtra("pos",position);
                    intent.putExtra("RoleFlag",0);
                    startActivity(intent);
                }else {
                    ShareDialog(position);
                }

            }

            @Override
            public void onLongClick(View view, int posotion) {
                if (!isShare){
                    customDialog(posotion);
                }
            }
        });
        IntentFilter intentFilter = new IntentFilter("EqupmentActivity");
        receiver = new MessageReceiver();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        equipment = equmentDao.findAll();
//        equpmentAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning=false;
    }

    /**
     * 自定义对话框
     */
   Dialog dialog;
   int position ;
    private void customDialog(final int pos) {
        dialog  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_shareedel, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dialog_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dialog_qd);
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
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
                    Map<String,Object> params = new HashMap<>();
                    params.put("deviceMac",equipments.get(pos).getDeviceMac());
                    deleteDeviceAsyncTask = new DeleteDeviceAsyncTask();
                    deleteDeviceAsyncTask.execute(params);
                    position =pos;
                    new Thread(){
                        public void run() {
                            try {
                                deleteDeviceAsyncTask.get(5, TimeUnit.SECONDS);
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            } catch (TimeoutException e) {
                                e.printStackTrace();
                                Message message = new Message();
                                message.obj = "TimeOut";
                                handler.sendMessage(message);
                            }
                        }
                    }.start();


            }
        });
        dialog.show();

    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ("TimeOut".equals(msg.obj)){
                ToastUtil.showShort(EqupmentActivity.this,"请求超时,请重试");
            }
        }
    };
    DeleteDeviceAsyncTask deleteDeviceAsyncTask;
    class DeleteDeviceAsyncTask extends AsyncTask<Map<String,Object>,Void,String>{

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> param = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/device/deleteDevice",param);
            Log.e("result", "doInBackground: -->"+result );
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
                    Equipment equipment = EqupmentActivity.this.equipments.get(position);
                    equmentDao.delete(equipment);
                    EqupmentActivity.this.equipments.remove(equipment);
                    equpmentAdapter.notifyItemRemoved(position);
                    equpmentAdapter.notifyItemRangeChanged(position, equpmentAdapter.getItemCount()); //刷新被删除数据，以及其后面的数据
                    dialog.dismiss();
                    break;
                    default:

                        break;
            }
        }
    }

    /**
     * 自定义对话框
     */
    String phone;
    EditText et_dilog_num;
    private void ShareDialog(final int position ) {
        final Dialog dialog = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_shareequ, null);
        TextView tv_dialog_qx = (TextView) view.findViewById(R.id.tv_dialog_qx);
        TextView tv_dialog_qd = (TextView) view.findViewById(R.id.tv_dialog_qd);
         et_dilog_num  = (EditText) view.findViewById(R.id.et_dilog_num);

        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(true);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
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
                phone = et_dilog_num.getText().toString().trim();
                Map<String,Object> params = new HashMap<>();
                params.put("deviceMac",equipments.get(position).getDeviceMac());
                params.put("phone",phone);
                shareEquAsynctask = new ShareEquAsynctask();
                shareEquAsynctask.execute(params);
                new Thread(){

                    public void run() {
                        try {
                            shareEquAsynctask.get(5,TimeUnit.SECONDS);
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                            Message message = new Message();
                            message.obj="TimeOut";
                            handler.sendMessage(message);
                        }
                    }
                };
                dialog.dismiss();

            }
        });
        dialog.show();
    }
    ShareEquAsynctask shareEquAsynctask ;
    public class  ShareEquAsynctask extends AsyncTask<Map<String,Object>,Void,String>{

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> param = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/device/shareDevice",param);
            Log.e("result222", "doInBackground: -->"+result );
            try {
                if (!TextUtils.isEmpty(result)){
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getString("returnCode");
                }

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
                    ToastUtil.showShort(EqupmentActivity.this,"分享成功");
                    setShare(false);
                    equpmentAdapter.setShare(false);
                    equpmentAdapter.notifyDataSetChanged();
                    break;
                    default:
                        ToastUtil.showShort(EqupmentActivity.this,"分享失败");
                        break;
            }
        }
    }

    public void setShare(boolean share){
        isShare = share;
    }

    @OnClick({R.id.iv_main_memu,R.id.tv_main_share})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_main_memu:
                if (!isShare){
                    Intent intent = new Intent(EqupmentActivity.this,MainActivity.class);
                    intent.putExtra("pos",0);
                    intent.putExtra("RoleFlag",0);
                    startActivity(intent);
                }else {
                    setShare(false);
                    equpmentAdapter.setShare(false);
                    equpmentAdapter.notifyDataSetChanged();
                }

                break;

            case R.id.tv_main_share:
                setShare(true);
                equpmentAdapter.setShare(true);
                equpmentAdapter.notifyDataSetChanged();
                break;

        }
    }
    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==600){
            equipments.remove(equipment);
            Equipment equipment1 = (Equipment) data.getSerializableExtra("equipment");
            equipments.add(equipment1);
            equipments.add(equipment);
            Log.e("ddddd", "initView: -->"+ equipments.size() );
            equpmentAdapter.notifyDataSetChanged();
        }
    }

    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("qqqqqZZZZ???", "11111");
            String msg = intent.getStringExtra("msg");
            Equipment msg1 =(Equipment)intent.getSerializableExtra("msg1");
            Log.e("DDDDDDTTTT", "onReceive: -->"+msg1+">>>>"+equipments.get(0)+">>>"+equipments.get(1) );
//            if (msg1!=null && equipments.contains(msg1)){
////                int index=equipments.indexOf(msg1);
////                equipments.set(index,msg1);
////                equpmentAdapter.RefrashData(equipments);
////                equpmentAdapter.notifyDataSetChanged();
////            }
//            equipments = equmentDao.findDeviceByRoleFlag(0);
//            equipments.add(equipment);
//            equpmentAdapter.RefrashData(equipments);
//            equpmentAdapter.notifyDataSetChanged();

        }
    }
}
