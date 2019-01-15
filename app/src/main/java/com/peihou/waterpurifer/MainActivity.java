package com.peihou.waterpurifer;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.peihou.waterpurifer.activity.EqupmentActivity;
import com.peihou.waterpurifer.activity.TimerTaskActivity;
import com.peihou.waterpurifer.activity.UserActivity;
import com.peihou.waterpurifer.adapter.baseAdapter;
import com.peihou.waterpurifer.adapter.memuAdapter;
import com.peihou.waterpurifer.base.BaseActivity;
import com.peihou.waterpurifer.base.MyApplication;
import com.peihou.waterpurifer.database.dao.daoImp.EquipmentImpl;
import com.peihou.waterpurifer.pojo.Equipment;
import com.peihou.waterpurifer.service.MQService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class MainActivity extends BaseActivity {

    private BottomSheetBehavior bottomSheetBehavior;
    Button button;
    @BindView(R.id.rl_main_1)
    RelativeLayout relativeLayout1;
    @BindView(R.id.tv_main_1)
    TextView textView1;
    @BindView(R.id.tv_main_xh)
    TextView tv_main_xh;
    @BindView(R.id.iv_main_1)
    ImageView iv_main_1;
    @BindView(R.id.iv_main_equ)
    ImageView iv_main_equ;
    @BindView(R.id.tv_top_phone)
    TextView tv_top_phone;
    @BindView(R.id.tv_main_dqsz)
    TextView tv_main_dqsz;
    @BindView(R.id.tv_main_lhsz)
    TextView tv_main_lhsz;
    @BindView(R.id.rl_main_sb)
    RelativeLayout rl_main_sb;
    @BindView(R.id.rl_main_xh)
    RelativeLayout rl_main_xh;
    @BindView(R.id.tv_titlename)
    TextView tv_titlename;
    @BindView(R.id.tv_main_style)
    TextView tv_main_style;
    ImageView iv_top_sl;

    RecyclerView recyclerView, rv_drawer;
    RelativeLayout rl_main_father, bottomSheetLayout1;
    TextView tv_main_sb, tv_main_sz;
    DrawerLayout dl;
    MyApplication application;
    baseAdapter adapter;
    SharedPreferences preferences;
    List<Equipment> equipmentList;
    Equipment equipment;
    EquipmentImpl equipmentDao;
    MessageReceiver receiver;
    public static boolean isRunning = false;
    private boolean clockisBound;
    String macAddress;
    int style;
    String[] mdate = {"PP滤芯", "UDF滤芯", "CTO滤芯", "RO滤芯", "T33滤芯"};
    String[] water = {"10", "13", "12", "11", "16", "12", "19", "12", "10"};
    int postion;
    int RoleFlag;
    private boolean hasData1;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_main;
    }


    @Override
    public void initView(View view) {
        isRunning = true;
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        String phone = preferences.getString("phone", "");
        tv_top_phone.setText(phone);
        Intent intent = getIntent();
        postion = intent.getIntExtra("pos", 0);
        RoleFlag = intent.getIntExtra("RoleFlag", 0);
        initView();//绑定控件
        TopAdapter();//菜单adpter
        equipmentDao = new EquipmentImpl(getApplicationContext());


        List<Equipment> list = equipmentDao.findAll();
        if (list.size() > 0) {
            initListeners();//底部布局
            equipmentList = equipmentDao.findDeviceByRoleFlag(RoleFlag);
            if (equipmentList.size() == 0) {
                RoleFlag = 1;
                equipmentList = equipmentDao.findDeviceByRoleFlag(RoleFlag);
            }
            equipment = equipmentList.get(postion);
            BottomAdpter();//底部布局adpter
            hasData1 = equipment.getHaData();
            Log.e("hasdata", "initView: -->" + hasData1 + equipment.getTodayUse());
            if (hasData1) {
                EquipmentChange(equipment);
            } else {
                OnlineEqument();
            }
            String mac = equipment.getDeviceMac();
            String title = mac.substring(mac.length() - 4);
            tv_titlename.setText("净水器" + title);
            int deviceFlag = equipment.getDeviceFlag();
            int deviceLeaseType = equipment.getDeviceLeaseType();

//            if (deviceLeaseType!=4&&deviceFlag==0){
//               ToastUtil.showShort(this,"没有绑定，请联系经销商");
            //  noEqupment();
//           }
        } else {
            noEqupment();//没有设备
        }

        //绑定services
        clockintent = new Intent(MainActivity.this, MQService.class);
        clockisBound = bindService(clockintent, clockconnection, Context.BIND_AUTO_CREATE);
        //接收mqtt
        IntentFilter intentFilter = new IntentFilter("MainActivity");
        receiver = new MessageReceiver();
        registerReceiver(receiver, intentFilter);

    }

    private void OnlineEqument() {
        textView1.setText("---");
        tv_main_sz.setText("自来水水质：---TDS");
        tv_main_style.setText("");
        tv_main_sb.setText("设备离线");
        tv_main_dqsz.setText("当前水质：—");

    }

    private void noEqupment() {
        rl_main_sb.setVisibility(View.INVISIBLE);
        rl_main_xh.setVisibility(View.INVISIBLE);
        textView1.setText("---");
        tv_main_sz.setVisibility(View.INVISIBLE);
        tv_main_style.setText("");
        iv_top_sl.setVisibility(View.INVISIBLE);
        tv_main_dqsz.setText("当前水质：—");
        tv_main_lhsz.setText("当前无设备，请先添加设备");
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        Log.e("Behaviour", "STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        Log.e("Behaviour", "STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        Log.e("Behaviour", "STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.e("Behaviour", "STATE_HIDDEN");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        Log.e("Behaviour", "STATE_SETTLING");
                        break;
                }
            }


            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    Intent clockintent;
    MQService clcokservice;
    boolean boundclock;
    ServiceConnection clockconnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder = (MQService.LocalBinder) service;
            clcokservice = binder.getService();
            boundclock = true;

            if (equipmentList != null && !hasData1) {
                for (int i = 0; i < equipmentList.size(); i++) {
                    Equipment equipment = equipmentList.get(i);
                    String deviceMac = equipment.getDeviceMac();
//                    clcokservice.getDate(deviceMac);
                    clcokservice.getData(equipment.getDeviceMac(), 0x11);
                    clcokservice.getData(equipment.getDeviceMac(), 0x23);
                    clcokservice.getDate(equipment.getDeviceMac(), 0x31);
                    clcokservice.getDate(equipment.getDeviceMac(), 0x32);
                    clcokservice.getDate(equipment.getDeviceMac(), 0x33);
                    clcokservice.getDate(equipment.getDeviceMac(), 0x34);
                    clcokservice.getDate(equipment.getDeviceMac(), 0x35);
                    clcokservice.getDate(equipment.getDeviceMac(), 0x36);
                    clcokservice.getDate(equipment.getDeviceMac(), 0x37);
                }
            }
            Log.e("QQQQQQQQQQQDDDDDDD", "onServiceConnected: ------->");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    public void initView() {
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheetLayout));
        bottomSheetLayout1 = (RelativeLayout) findViewById(R.id.bottomSheetLayout);
        rl_main_father = (RelativeLayout) findViewById(R.id.rl_main_father);
        recyclerView = (RecyclerView) findViewById(R.id.rv_recy);
        rv_drawer = (RecyclerView) findViewById(R.id.rv_drawer);
        tv_main_sb = (TextView) findViewById(R.id.tv_main_sb);
        tv_main_sz = (TextView) findViewById(R.id.tv_main_sz);
        recyclerView.setAlpha(0);
        iv_top_sl = (ImageView) findViewById(R.id.iv_top_sl);
        button = (Button) findViewById(R.id.btn_login);
        dl = (DrawerLayout) findViewById(R.id.drawerlayout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isRunning = true;

    }

    List<Equipment> list = new ArrayList<>();

    public void BottomAdpter() {

        for (int i = 0; i < 7; i++) {
            Equipment equipment = new Equipment();
            if (i < 5) {
                equipment.setType(0);
                equipment.setName(mdate[i]);
            } else if (i == 5) {
                equipment.setType(1);
            } else {
                equipment.setType(2);
            }
            list.add(equipment);
        }
        adapter = new baseAdapter(this, list, equipment);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void TopAdapter() {
// 关闭手势滑动
        dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        dl.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View content = dl.findViewById(R.id.rl_main_father);
                View memu = drawerView;
                float scale = 1 - slideOffset;
                content.setTranslationX(memu.getMeasuredWidth() * (1 - scale));
                bottomSheetLayout1.setTranslationX(memu.getMeasuredWidth() * (1 - scale));
                bottomSheetLayout1.setAlpha(scale);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // 打开手势滑动
                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // 关闭手势滑动
                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        List<String> mData = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            mData.add("item" + i);
        }
        memuAdapter memuAdapter = new memuAdapter(this, mData);
        rv_drawer.setLayoutManager(new LinearLayoutManager(this));
        rv_drawer.setAdapter(memuAdapter);
    }

    @Override
    public void doBusiness(Context mContext) {
        Intent service = new Intent(this, MQService.class);
        startService(service);
    }

    @Override
    public void widgetClick(View v) {

    }

    @SuppressLint("RtlHardcoded")
    @OnClick({R.id.iv_main_memu, R.id.iv_top_pic, R.id.iv_main_equ, R.id.tv_main_lhsz})
    public void onClik(View view) {
        switch (view.getId()) {
            case R.id.iv_main_memu:
                dl.openDrawer(Gravity.LEFT);
                break;
            case R.id.iv_top_pic:
                startActivity(new Intent(this, UserActivity.class));
                break;
            case R.id.iv_main_equ:
                startActivity(new Intent(this, EqupmentActivity.class));
                break;
            case R.id.tv_main_lhsz:
                if (hasData1) {
                    if (equipment.getBussinessmodule() == 0x44) {
                        Intent intent = new Intent(this, TimerTaskActivity.class);
                        intent.putExtra("macAddress", macAddress);
                        startActivity(intent);
                    }
                }
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出净水器",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
                return false;
            } else {
                application.removeAllActivity();
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    private long exitTime = 0;

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void initListeners() {


        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
                if (slideOffset > 0) {
                    float scral = (float) (0.4 * slideOffset);
                    float scral1 = (float) (0.7 * slideOffset);
//                    Log.e("height", "onSlide: -->" + slideOffset);
                    relativeLayout1.setTranslationY(-relativeLayout1.getMeasuredWidth() * scral);
                    tv_main_sb.setTranslationY(-relativeLayout1.getMeasuredWidth() * scral);
//                    tv_main_sz.setTranslationY(-relativeLayout1.getMeasuredWidth() * scral);
                    tv_main_xh.setTranslationY(-relativeLayout1.getMeasuredWidth() * scral);
                    tv_main_style.setTranslationY(-relativeLayout1.getMeasuredWidth() * scral);
//                    iv_main_1.setAlpha(224 * (1 - slideOffset));
                    iv_main_1.setAlpha(1 - slideOffset);
                    recyclerView.setAlpha(slideOffset);
                    iv_top_sl.setAlpha(1 - slideOffset);
                    tv_main_sb.setAlpha(1 - slideOffset);
                    tv_main_sz.setAlpha(1 - slideOffset);
                    tv_main_xh.setAlpha(1 - slideOffset);
                    tv_main_style.setAlpha(1 - slideOffset);

                }
            }
        });
    }

    public void EquipmentChange(Equipment equipment) {
//        list.get(0).setWWaterStall(postion);
        hasData1 = true;
        macAddress = equipment.getDeviceMac();
        int wPurifierPrimaryQuqlity = equipment.getWPurifierPrimaryQuqlity();
        String wPurifierOutQuqlity = equipment.getWPurifierOutQuqlity() + "";
        int busness = equipment.getBussinessmodule();
        tv_main_lhsz.setText("过滤后水质可饮用");
        if (busness == 0 || busness == 0xff) {
            //00：忽略；11：按水流量租凭；22：按时间租赁；33：按售水量售水型；FF：常规机型
        } else if (busness == 0x11) {
            int RechargeFlow = equipment.getRechargeFlow();
            if (RechargeFlow == 0) {
                tv_main_style.setText("流量:" + (RechargeFlow) + "L");
            } else {
                tv_main_style.setText("流量:" + (RechargeFlow - 1) + "L");
            }
        } else if (busness == 0x22) {
            int RechargeTime = equipment.getRechargeTime();
            if (RechargeTime == 0) {
                tv_main_style.setText("时间:" + RechargeTime + "天");
            } else {
                tv_main_style.setText("时间:" + (RechargeTime - 1) / 24 + "天");
            }
        } else if (busness == 0x33) {
            int wWaterStall = equipment.getWWaterStall();
            if (wWaterStall == 0xEE) {
                tv_main_style.setText("售水量: 有故障");
            } else if (wWaterStall == 0xFF) {
                tv_main_style.setText("售水量: 已用完");
            } else if (wWaterStall == 0xAA) {
                tv_main_style.setText("售水量: 未用完");
            }
        } else if (busness == 0x44) {
            tv_main_style.setText("刘机型");
            tv_main_lhsz.setText("任务详情");
        } else if (busness == 0xFF) {
            tv_main_style.setText("常规用户");
        }
        Log.e("buness", "EquipmentChange: -->" + busness + ">>>>");
        int wMobileSignal = equipment.getWMobileSignal();
        if (wMobileSignal < 10) {
            tv_main_xh.setText("2G信号弱");
        } else {
            tv_main_xh.setText("2G信号强");
        }
        textView1.setText(wPurifierOutQuqlity);
        tv_main_sz.setText("自来水水质：" + wPurifierPrimaryQuqlity + "TDS");
        int Quqlity = Integer.valueOf(wPurifierOutQuqlity);
        if (Quqlity < 90) {
            tv_main_dqsz.setText("当前水质：优");
        } else if (90 < Quqlity && Quqlity <= 250) {
            tv_main_dqsz.setText("当前水质：良");
        } else if (250 < Quqlity && Quqlity <= 600) {
            tv_main_dqsz.setText("当前水质：中");
        } else if (Quqlity > 600) {
            tv_main_dqsz.setText("当前水质：差");
        }
        tv_main_sb.setText("设备在线");
        int IsOpen = equipment.getIsOpen();
        if (IsOpen == 0) {
            tv_main_sb.setText("设备关机");
        } else {
            tv_main_sb.setText("设备开机");
        }

        Log.e("equipment", "EquipmentChange: -->" + equipment.getWPurifierfilter1());
        adapter.haveGetData(true, equipment);
        adapter.notifyDataSetChanged();
    }

    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("qqqqqZZZZ???", "11111");
            String msg = intent.getStringExtra("msg");
            Equipment msg1 = (Equipment) intent.getSerializableExtra("msg1");
            if (msg.equals(equipment.getDeviceMac())) {
                equipment = msg1;
                EquipmentChange(equipment);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (clockisBound) {
            unbindService(clockconnection);
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        isRunning = false;
    }
}
