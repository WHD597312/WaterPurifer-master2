package com.peihou.waterpurifer.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.adapter.CityAdapter;
import com.peihou.waterpurifer.adapter.RepairListAdapter;
import com.peihou.waterpurifer.base.BaseActivity;
import com.peihou.waterpurifer.base.MyApplication;
import com.peihou.waterpurifer.bean.City;
import com.peihou.waterpurifer.bean.District;
import com.peihou.waterpurifer.bean.Province;
import com.peihou.waterpurifer.database.dao.daoImp.EquipmentImpl;
import com.peihou.waterpurifer.pojo.Equipment;
import com.peihou.waterpurifer.util.HttpUtils;
import com.peihou.waterpurifer.util.NetWorkUtil;
import com.peihou.waterpurifer.util.ToastUtil;
import com.peihou.waterpurifer.util.view.ScreenSizeUtils;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.OnClick;


public class RepairActivity extends BaseActivity {

    @BindView(R.id.rl_main_father)
    RelativeLayout rl_main_father;
    @BindView(R.id.tv_repair_type)
    TextView tv_repair_type;
    @BindView(R.id.tv_repair_time)
    TextView tv_repair_time;
    @BindView(R.id.tv_repair_adresxq)
    TextView tv_repair_adresxq;
    @BindView(R.id.et_repair_adressxq)
    EditText et_repair_adressxq;
    @BindView(R.id.et_repair_ms)
    EditText et_repair_ms;
    @BindView(R.id.tv_repair_repir)
    TextView tv_repair_repir;
    @BindView(R.id.tv_repair_name1)
    TextView tv_repair_name1;
    @BindView(R.id.tv_repair_gz)
    TextView tv_repair_gz;
    private TimePickerView  pvCustomTime;
    private PopupWindow mPopWindow;
    private View contentViewSign;

    List<Province> list = null;
    Province province = null;

    List<City> cities = null;
    City city = null;

    List<District> districts = null;
    District district = null;

    int sign_sheng = 0, sign_city = 0, isDefault = 1, receiveId = 0;
    String receiveProvince, receiveCity, receiveCounty, receiveAddress;
    private ProgressDialog progressDialog;
    MyApplication application;
    String repairTime,repairAddress1,repairAddress2 ;
    SharedPreferences preferences ;
    String phone,userId;
    List<Equipment> equipmentList ;
    EquipmentImpl equipmentDao;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_repair;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        progressDialog = new ProgressDialog(this);
        preferences = getSharedPreferences("my",MODE_PRIVATE);
        phone = preferences.getString("phone","");
        userId =preferences.getString("userId","");
        equipmentDao = new EquipmentImpl(getApplicationContext());
        equipmentList = equipmentDao.findDeviceByRoleFlag(0);
       initCustomTimePicker();

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    RepairAsyncTask task;
    @OnClick({R.id.iv_main_memu,R.id.iv_repair_xq,R.id.rl_repair_type,R.id.rl_repair_time,
    R.id.rl_repair_adres,R.id.bt_repair_qd,R.id.rl_repair_name
    })
    public void onClick(View view){
        switch (view.getId()){
            case R.id.rl_repair_name:
                if (equipmentList.size()>0){
                    customDialog();
                }else {
                    toast("对不起，您没有可维修的设备");
                }

                break;

            case R.id.iv_main_memu:
                finish();
                break;
            case R.id.iv_repair_xq:
                startActivity(new Intent(this,XqRepairActivity.class));
                break;
            case R.id.rl_repair_type:
                List<Equipment> list = new ArrayList<>();
                String [] name = new String[]{"报警故障","传感器故障","异常故障","其他"};
                for (int i = 0;i<4;i++){
                    Equipment equipment = new Equipment();
                    equipment.setDeviceMac(name[i]);
                    list.add(equipment);
                }
                customDialog1(list);
                break;

            case R.id.rl_repair_time:
             pvCustomTime.show();

                break;
            case R.id.rl_repair_adres:

                if (view != null) {
                InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert inputmanger != null;
                inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        showPopup();
                    }
                }, 300);

                break;
            case R.id.bt_repair_qd:
                repairAddress2 = et_repair_adressxq.getText().toString().trim();
                String repairDesc = et_repair_ms.getText().toString().trim();
                String repairDeviceMac = tv_repair_name1.getText().toString().trim();
                String repairType = tv_repair_gz.getText().toString().trim();
                if (TextUtils.isEmpty(repairDeviceMac)){
                    toast("请选择设备");
                    break;
                }
                if(TextUtils.isEmpty(repairType)){
                    toast("请选择故障类型");
                    break;
                }
              if (TextUtils.isEmpty(repairTime)){
                    ToastUtil.showShort(this,"请选择维修时间");
                    break;
                }
                if (TextUtils.isEmpty(repairAddress1)){
                    ToastUtil.showShort(this,"请选择维修地址");
                    break;
                }
                if (TextUtils.isEmpty(repairAddress2)){
                    ToastUtil.showShort(this,"请添加详细地址");
                    break;
                }
                Map<String,Object> param = new HashMap<>();
                param.put("repairDeviceMac",repairDeviceMac );
                param.put("repairType",repairType);
                param.put("repairTime",repairTime);
                param.put("repairAddress",repairAddress1+repairAddress2);
                param.put("repairDesc",repairDesc);
                param.put("repairPhone",phone);
                param.put("repairCreatorId",userId);

                    boolean isConn = NetWorkUtil.isConn(MyApplication.getContext());
                    if (isConn) {
                        showProgressDialog("正在上传，请稍后。。。");
                        task=  new RepairAsyncTask() ;
                        task.execute(param);
                        new Thread(){
                            public void run() {
                                try {

                                    task.get(5, TimeUnit.SECONDS);
                                } catch (InterruptedException e) {
                                } catch (ExecutionException e) {
                                } catch (TimeoutException e) {
                                    Message message = new Message();
                                    message.obj = "TimeOut";
                                    handler.sendMessage(message);
                                }
                            }
                        }.start();

                    }else {
                        ToastUtil.showShort(this,"无网络可用，请检查网络");
                    }

                break;
        }
    }


    @SuppressLint("StaticFieldLeak")
    class RepairAsyncTask extends AsyncTask<Map<String,Object>,Void,String> {

        @SafeVarargs
        @Override
        protected final String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String ,Object> prarms = maps[0];
            String result = HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/user/addRepair",prarms);
            Log.e("back", "--->"+result);
            if (!ToastUtil.isEmpty(result)){
                try {
                    JSONObject jsonObject= new JSONObject(result);
                    code = jsonObject.getString("returnCode");
//                    JSONObject returnData = jsonObject.getJSONObject("returnData");


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return code;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            switch (s) {

                case "100":
                    if (progressDialog!=null&&progressDialog.isShowing())
                        progressDialog.dismiss();

                    ToastUtil.showShort(RepairActivity.this, "提交成功");
                    startActivity(XqRepairActivity.class);
                    break;
                default:
                    if (progressDialog!=null&&progressDialog.isShowing())
                        progressDialog.dismiss();
                    ToastUtil.showShort(RepairActivity.this, "提交失败，请重试");

                    break;

            }
        }
    }

    Dialog dialog;
    int Pos ;
     private void customDialog() {
        dialog  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_chooseequ, null);
        TextView tv_choose_qx = (TextView) view.findViewById(R.id.tv_choose_qx);
        TextView tv_choose_qd = (TextView) view.findViewById(R.id.tv_choose_qd);
        RecyclerView rv_choose = (RecyclerView) view.findViewById(R.id.rv_choose);
        final RepairListAdapter repairListAdapter = new RepairListAdapter(this,equipmentList);
        rv_choose.setLayoutManager(new LinearLayoutManager(this));
        rv_choose.setAdapter(repairListAdapter);
        repairListAdapter.SetOnItemClick(new RepairListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                repairListAdapter.setColor(true,position);
                Pos = position;
                Log.e("Click", "onItemClick: -->" );
            }

            @Override
            public void onLongClick(View view, int posotion) {

            }
        });
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = (int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.45f);
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        tv_choose_qx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }

        });
        tv_choose_qd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_repair_name1.setText(equipmentList.get(Pos).getDeviceMac());
                dialog.dismiss();

            }
        });
        dialog.show();

    }


    private void customDialog1(final List<Equipment> list) {
        dialog  = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.dialog_choosegz, null);
        TextView tv_choose_qx = (TextView) view.findViewById(R.id.tv_choose_qx);
        TextView tv_choose_qd = (TextView) view.findViewById(R.id.tv_choose_qd);
        RecyclerView rv_choose = (RecyclerView) view.findViewById(R.id.rv_choosegz);
        final RepairListAdapter repairListAdapter = new RepairListAdapter(this,list);
        rv_choose.setLayoutManager(new LinearLayoutManager(this));
        rv_choose.setAdapter(repairListAdapter);
        repairListAdapter.SetOnItemClick(new RepairListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                repairListAdapter.setColor(true,position);
                Pos = position;
                Log.e("Click", "onItemClick: -->" );
            }

            @Override
            public void onLongClick(View view, int posotion) {

            }
        });
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = (int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.45f);
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        tv_choose_qx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }

        });
        tv_choose_qd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_repair_gz.setText(list.get(Pos).getDeviceMac());
                dialog.dismiss();

            }
        });
        dialog.show();

    }



    //显示dialog
    public void showProgressDialog(String message) {

        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }


    private String getTime(Date date) {//可根据需要自行截取数据显示
        Log.d("getTime()", "choice date millis: " + date.getTime());
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }


    private void initCustomTimePicker() {

        /**
         * @description
         *
         * 注意事项：
         * 1.自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针.
         * 具体可参考demo 里面的两个自定义layout布局。
         * 2.因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
         * setRangDate方法控制起始终止时间(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
         */
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
//        startDate.set(2014, 1, 23);
        startDate.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH));
        Calendar endDate = Calendar.getInstance();
        endDate.set(2027, 2, 28);
        //时间选择器 ，自定义布局
        pvCustomTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                tv_repair_time.setText(getTime(date));
                repairTime=getTime(date);
            }
        })
                /*.setType(TimePickerView.Type.ALL)//default is all
                .setCancelText("Cancel")
                .setSubmitText("Sure")
                .setContentTextSize(18)
                .setTitleSize(20)
                .setTitleText("Title")
                .setTitleColor(Color.BLACK)
               /*.setDividerColor(Color.WHITE)//设置分割线的颜色
                .setTextColorCenter(Color.LTGRAY)//设置选中项的颜色
                .setLineSpacingMultiplier(1.6f)//设置两横线之间的间隔倍数
                .setTitleBgColor(Color.DKGRAY)//标题背景颜色 Night mode
                .setBgColor(Color.BLACK)//滚轮背景颜色 Night mode
                .setSubmitColor(Color.WHITE)
                .setCancelColor(Color.WHITE)*/
                /*.animGravity(Gravity.RIGHT)// default is center*/
                .setTitleBgColor(Color.WHITE)
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        TextView ivCancel = (TextView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.returnData();
                                pvCustomTime.dismiss();
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                pvCustomTime.dismiss();
                            }
                        });
                    }
                })
                .setContentTextSize(18)
                .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                .setType(new boolean[]{ true, true, true,false, false, false})
                .setLabel("年", "月", "日", "时", "分", "秒")
                .setLineSpacingMultiplier(1.2f)
                .setTextXOffset(0, 0, 0, 0, 0, 0)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .build();

    }


    private PopupWindow popupWindow1;
    public void popupmenuWindow() {
        backgroundAlpha(0.4f);
        if (popupWindow1 != null && popupWindow1.isShowing()) {
            return;
        }
        View view = View.inflate(this, R.layout.popview_repiar_style, null);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        RelativeLayout rl_rp_ls = (RelativeLayout) view.findViewById(R.id.rl_rp_ls);
        RelativeLayout rl_rp_zssj = (RelativeLayout) view.findViewById(R.id.rl_rp_zssj);
        RelativeLayout rl_rp_qt = (RelativeLayout) view.findViewById(R.id.rl_rp_qt);

        if (popupWindow1==null)
           popupWindow1 = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            //点击空白处时，隐藏掉pop窗口
        popupWindow1.setFocusable(true);
        popupWindow1.setOutsideTouchable(true);
        //添加弹出、弹入的动画
        popupWindow1.setAnimationStyle(R.style.Popupwindow);
        popupWindow1.showAtLocation(rl_main_father, Gravity.BOTTOM, 0, 0);
        //添加按键事件监听
        popupWindow1.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.rl_rp_ls:
                        tv_repair_type.setText("漏水");
                        popupWindow1.dismiss();
                        backgroundAlpha(1.0f);

                        break;
                    case R.id.rl_rp_zssj:
                        tv_repair_type.setText("制水时间过长");
                        popupWindow1.dismiss();


                        break;
                    case R.id.rl_rp_qt:
                        tv_repair_type.setText("其他原因");
                        popupWindow1.dismiss();

                        break;
                }
            }
        };

        rl_rp_ls.setOnClickListener(listener);
        rl_rp_zssj.setOnClickListener(listener);
        rl_rp_qt.setOnClickListener(listener);
    }

    //设置蒙版
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp =getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

    private RecyclerView recyclerview;

    private View view_dis;
    private ListView listCity;
    private List<String> data = new ArrayList<>();
    private CityAdapter cityAdapter;
    private TextView tv_sheng, tv_shi, tv_qu;
    private RelativeLayout rl_sheng, rl_shi, rl_qu;
    private ImageView img_sheng, img_shi, img_qu ,iv_dia_close;
    private ImageView[] img_city;
    private int sing_city = 0;
    private int p1=-1;
    private int p2=-1;
    private int p3=-1;
    private void showPopup() {
        parser();
        contentViewSign = LayoutInflater.from(this).inflate(R.layout.popup_shop_city, null);
        view_dis = contentViewSign.findViewById(R.id.view_dis);
        listCity = (ListView) contentViewSign.findViewById(R.id.list_city);
        tv_sheng = (TextView) contentViewSign.findViewById(R.id.tv_sheng);
        tv_shi = (TextView) contentViewSign.findViewById(R.id.tv_shi);
        tv_qu = (TextView) contentViewSign.findViewById(R.id.tv_qu);
        rl_sheng = (RelativeLayout) contentViewSign.findViewById(R.id.rl_sheng);
        rl_shi = (RelativeLayout) contentViewSign.findViewById(R.id.rl_shi);
        rl_qu = (RelativeLayout) contentViewSign.findViewById(R.id.rl_qu);
        img_sheng = (ImageView) contentViewSign.findViewById(R.id.img_sheng);
        img_shi = (ImageView) contentViewSign.findViewById(R.id.img_shi);
        img_qu = (ImageView) contentViewSign.findViewById(R.id.img_qu);
        iv_dia_close= contentViewSign.findViewById(R.id.iv_dia_close);
        img_city = new ImageView[]{img_sheng, img_shi, img_qu};

        view_dis.setOnClickListener(this);
        rl_sheng.setOnClickListener(this);
        rl_shi.setOnClickListener(this);
        rl_qu.setOnClickListener(this);
        iv_dia_close.setOnClickListener(this);
        rl_sheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             sing_city=0;
                upData(0);
                img_sheng.setImageResource(R.mipmap.dz_blue);
                img_shi.setImageResource(R.mipmap.dz_gray);
                img_qu.setImageResource(R.mipmap.dz_gray);
                cityAdapter.setCurrentItem(p1);
            }
        });
        rl_shi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sing_city=1;
                upData(1);
                img_sheng.setImageResource(R.mipmap.dz_gray);
                img_shi.setImageResource(R.mipmap.dz_blue);
                img_qu.setImageResource(R.mipmap.dz_gray);
                cityAdapter.setCurrentItem(p2);
            }
        });
        rl_qu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sing_city=2;
                upData(2);
                img_sheng.setImageResource(R.mipmap.dz_gray);
                img_shi.setImageResource(R.mipmap.dz_gray);
                img_qu.setImageResource(R.mipmap.dz_blue);
                cityAdapter.setCurrentItem(p3);
            }
        });
        iv_dia_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
            }
        });
        cityAdapter = new CityAdapter(data, this);
        listCity.setAdapter(cityAdapter);
        listCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (sing_city) {
                    case 0:
                        receiveProvince = data.get(position);
                        tv_sheng.setText(receiveProvince);
                        sign_sheng = position;
                        img_sheng.setImageResource(R.mipmap.dz_gray);
                        img_shi.setImageResource(R.mipmap.dz_blue);
                        p1=position;
                        upData(1);
                        break;
                    case 1:
                        receiveCity = data.get(position);
                        tv_shi.setText(receiveCity);
                        sign_city = position;
                        p2=position;
                        img_shi.setImageResource(R.mipmap.dz_gray);
                        img_qu .setImageResource(R.mipmap.dz_blue);
                        upData(2);
                        break;
                    case 2:
                        receiveCounty = data.get(position);
                        tv_qu.setText(receiveCounty);
                        mPopWindow.dismiss();
                        p3=position;
                        if (tv_sheng.getText().equals("省")||tv_shi.getText().equals("市")){
                            repairAddress1="";
                            tv_repair_adresxq.setText("");
                            ToastUtil.showShort(RepairActivity.this,"请选择正确的地址");
                        }else {
                            repairAddress1=tv_sheng.getText() + " " + tv_shi.getText() + " " + tv_qu.getText();
                            tv_repair_adresxq.setText(tv_sheng.getText() + " " + tv_shi.getText() + " " + tv_qu.getText());
                        }

                        break;


                }

            }

        });
        upData(0);
        mPopWindow = new PopupWindow(contentViewSign);

        mPopWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //在PopupWindow里面就加上下面代码，让键盘弹出时，不会挡住pop窗口。
        mPopWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        //点击空白处时，隐藏掉pop窗口
        mPopWindow.setFocusable(true);
//        mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopWindow. setOutsideTouchable(true);
        mPopWindow.setClippingEnabled(false);
        backgroundAlpha(0.5f);
        //添加pop窗口关闭事件
        mPopWindow.setAnimationStyle(R.style.Popupwindow);
        mPopWindow.setOnDismissListener(new poponDismissListener());
//        mPopWindow.showAsDropDown(findViewById(R.id.li_main_bt));
        mPopWindow.showAtLocation(tv_repair_adresxq, Gravity.BOTTOM, 0, 0);
    }



    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            backgroundAlpha(1f);
        }

    }


    private void upData(int i) {

//        img_city[sing_city].setVisibility(View.INVISIBLE);
        sing_city = i;
//        img_city[sing_city].setVisibility(View.VISIBLE);
        data.clear();
        if (i == 0) {
            for (int a = 0; a < list.size(); a++) {
                data.add(list.get(a).getName());
            }
        } else if (i == 1) {
//            listCity
            if (list.size() > 0) {
                cities = list.get(sign_sheng).getCitys();
                for (int a = 0; a < cities.size(); a++) {
                    data.add(cities.get(a).getName());
                }
            } else {
                Toast.makeText(this, "请选择省份", Toast.LENGTH_SHORT).show();
            }
        } else if (i == 2) {
            if (cities.size() > 0) {
                districts = cities.get(sign_city).getDistricts();
                for (int a = 0; a < districts.size(); a++) {
                        data.add(districts.get(a).getName());
                }
            } else {
                Toast.makeText(this, "请选择城市", Toast.LENGTH_SHORT).show();
            }
        }

        cityAdapter.notifyDataSetChanged();
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 3) {
                showPopup();
            }
            if ("TimeOut".equals(msg.obj)){
                if (progressDialog!=null&&progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(RepairActivity.this,"请求超时,请重试",Toast.LENGTH_SHORT).show();
            }
        }
    };


    public List<Province> parser() {
        // 创建解析器，并制定解析的xml文件
        XmlResourceParser parser = getResources().getXml(R.xml.cities);
        try {
            int type = parser.getEventType();
            while (type != 1) {
                String tag = parser.getName();//获得标签名
                switch (type) {
                    case XmlResourceParser.START_DOCUMENT:
                        list = new ArrayList<Province>();
                        break;
                    case XmlResourceParser.START_TAG:
                        if ("p".equals(tag)) {
                            province = new Province();
                            cities = new ArrayList<City>();
                            int n = parser.getAttributeCount();
                            for (int i = 0; i < n; i++) {
                                //获得属性的名和值
                                String name = parser.getAttributeName(i);
                                String value = parser.getAttributeValue(i);
                                if ("p_id".equals(name)) {
                                    province.setId(value);
                                }
                            }
                        }
                        if ("pn".equals(tag)) {//省名字
                            province.setName(parser.nextText());
                        }
                        if ("c".equals(tag)) {//城市
                            city = new City();
                            districts = new ArrayList<District>();
                            int n = parser.getAttributeCount();
                            for (int i = 0; i < n; i++) {
                                String name = parser.getAttributeName(i);
                                String value = parser.getAttributeValue(i);
                                if ("c_id".equals(name)) {
                                    city.setId(value);
                                }
                            }
                        }
                        if ("cn".equals(tag)) {
                            city.setName(parser.nextText());
                        }
                        if ("d".equals(tag)) {
                            district = new District();
                            int n = parser.getAttributeCount();
                            for (int i = 0; i < n; i++) {
                                String name = parser.getAttributeName(i);
                                String value = parser.getAttributeValue(i);
                                if ("d_id".equals(name)) {
                                    district.setId(value);
                                }
                            }
                            district.setName(parser.nextText());
                            districts.add(district);
                        }
                        break;
                    case XmlResourceParser.END_TAG:
                        if ("c".equals(tag)) {
                            city.setDistricts(districts);
                            cities.add(city);
                        }
                        if ("p".equals(tag)) {
                            province.setCitys(cities);
                            list.add(province);
                        }
                        break;
                    default:
                        break;
                }
                type = parser.next();
            }
        } catch (XmlPullParserException | NumberFormatException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /*catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } */
        return list;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler!=null)
            handler.removeCallbacksAndMessages(null);
    }
}
