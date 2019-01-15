package com.peihou.waterpurifer.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.base.BaseActivity;
import com.peihou.waterpurifer.base.MyApplication;
import com.peihou.waterpurifer.database.dao.daoImp.EquipmentImpl;
import com.peihou.waterpurifer.database.dao.daoImp.TimerTaskImpl;
import com.peihou.waterpurifer.dialog.TimeTaskDialog;
import com.peihou.waterpurifer.pojo.Equipment;
import com.peihou.waterpurifer.pojo.Task;
import com.peihou.waterpurifer.pojo.TimerTask;
import com.peihou.waterpurifer.pojo.TimerTask2;
import com.peihou.waterpurifer.service.MQService;
import com.peihou.waterpurifer.util.Utils;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TimerTaskActivity extends BaseActivity {

    public static boolean running = false;
    MyApplication application;
    @BindView(R.id.list_view)
    RecyclerView list_view;
    String macAddress;//设备mac地址
    int week;//星期
    //    TaskAdapter adapter;
//    @BindView(R.id.tv_timer_task_set)
//    TextView tv_timer_task_set;
    @BindView(R.id.view_main_1)
    View view_main_1;
    MyAdapter adapter;

    private TimerTaskImpl timerTaskDao;
    private EquipmentImpl equipmentDao;
    private Equipment equipment;

    List<Task> list;


    @Override
    public void initParms(Bundle parms) {
        macAddress = parms.getString("macAddress");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_timer_task;
    }

    Task task1, task2, task3, task4, task5, task6, task7;
    List<TimerTask> weekTimerTask;


    private ProgressDialog progressDialog;

    @Override
    public void initView(View view) {

        if (application == null) {
            application = (MyApplication) getApplication();
        }

        progressDialog = new ProgressDialog(this);
        running = true;
        timerTaskDao = new TimerTaskImpl(getApplicationContext());
        equipmentDao = new EquipmentImpl(getApplicationContext());
        equipment = equipmentDao.findDeviceByMacAddress2(macAddress);

        equipment.setDeviceMac(macAddress);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        String s = year + "-" + month + "-" + day + " " + hour + ":" + min;

        list = new ArrayList<>();
        task1 = new Task("上温", "0℃");
        task2 = new Task("下温", "0℃");
        task3 = new Task("净水时间周期", "00/秒");
        task4 = new Task("最长净水时间", "0");
        task5 = new Task("校准时间", s);
        task6 = new Task("无水监测灵敏度", "");
        task6.setVisibility(1);
        task7 = new Task();
        task7.setType(1);

        list.add(0, task1);
        list.add(1, task2);
        list.add(2, task3);
        list.add(3, task4);
        list.add(4, task5);
        list.add(5, task6);
        list.add(6, task7);
        for (int i = 0; i < list.size(); i++) {
            Log.i("type", "-->" + list.get(i).getType());
        }

        for (int i = 0; i <= 24; i++) {
            hours.add("" + i);
        }
        for (int i = 0; i <= 24; i++) {
            hours.add("" + i);
        }
        for (int i = 0; i < 12; i++) {
            hours.add("" + i);
        }

        for (int i = 0; i <= 59; i++) {
            mins.add("" + i);
        }
        int week2 = calendar.get(Calendar.DAY_OF_WEEK);
        week = Utils.getWeek(week2);
        setTimerTask(week);
//        adapter = new TaskAdapter(this, list);
        adapter = new MyAdapter(list, this);
        list_view.setLayoutManager(new LinearLayoutManager(this));

//        list_view.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        list_view.setAdapter(adapter);
        IntentFilter filter = new IntentFilter("TimerTaskActivity");
        receiver = new MessageReceiver();
        registerReceiver(receiver, filter);
        clockintent = new Intent(TimerTaskActivity.this, MQService.class);
        boundclock = bindService(clockintent, clockconnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }


    int commit = 0;//1.表示提交新增定时，2表示提交粘贴功能 3表示删除功能

    @OnClick({R.id.btn_commit_timer, R.id.back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_commit_timer:
                if (commit == 1) {
                    addTimer();
                } else if (commit == 2) {
                    new TimerTaskAsync().execute(pasterWeek);
                }
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    PopupWindow popupWindow;
    LoopView openHour;
    LoopView openMin;
    LoopView closeHour;
    LoopView closeMin;
    List<String> hours = new ArrayList<>();
    List<String> mins = new ArrayList<>();

    int hour = 0;
    int min = 0;

    int hour2 = 0;
    int min2 = 0;
    List<TimerTask> taskList = new ArrayList<>();

    private void popupView() {

        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }

        View view = View.inflate(this, R.layout.dialog_timer_task, null);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;


        openHour = view.findViewById(R.id.openHour);
        openMin = view.findViewById(R.id.openMin);
        closeHour = view.findViewById(R.id.closeHour);
        closeMin = view.findViewById(R.id.closeMin);
        TextView button_ensure = view.findViewById(R.id.button_ensure);
        TextView button_cancel = view.findViewById(R.id.button_cancel);

        openHour.setItems(hours);
        openHour.setCenterTextColor(Color.parseColor("#22ccfd"));
        openHour.setOuterTextColor(Color.parseColor("#bbbbbb"));
        openHour.setTextSize(18);
        openHour.setInitPosition(0);
        openHour.setItemsVisibleCount(7);

        closeHour.setItems(hours);
        closeHour.setCenterTextColor(Color.parseColor("#22ccfd"));
        closeHour.setOuterTextColor(Color.parseColor("#bbbbbb"));
        closeHour.setTextSize(18);
        closeHour.setInitPosition(0);
        closeHour.setItemsVisibleCount(7);

        openMin.setItems(mins);
        openMin.setCenterTextColor(Color.parseColor("#22ccfd"));
        openMin.setOuterTextColor(Color.parseColor("#bbbbbb"));
        openMin.setTextSize(18);
        openMin.setInitPosition(0);
        openMin.setItemsVisibleCount(7);

        closeMin.setItems(mins);
        closeMin.setCenterTextColor(Color.parseColor("#22ccfd"));
        closeMin.setOuterTextColor(Color.parseColor("#bbbbbb"));
        closeMin.setTextSize(18);
        closeMin.setInitPosition(0);
        closeMin.setItemsVisibleCount(7);

        hour = 0;
        min = 0;
        hour2 = 0;
        min2 = 0;
        openHour.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                String s = hours.get(index);
                hour = Integer.parseInt(s);
            }
        });
        openMin.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                String s = mins.get(index);
                min = Integer.parseInt(s);
            }
        });
        closeHour.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                String s = hours.get(index);
                hour2 = Integer.parseInt(s);
            }
        });
        closeMin.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                String s = mins.get(index);
                min2 = Integer.parseInt(s);
            }
        });

        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //点击空白处时，隐藏掉pop窗口
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);


        //添加弹出、弹入的动画
        popupWindow.setAnimationStyle(R.style.Popupwindow);

        final ColorDrawable dw = new ColorDrawable(0x00ffffff);
        popupWindow.setBackgroundDrawable(dw);
//        popupWindow.showAsDropDown(relative4, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popupWindow.showAtLocation(view_main_1, Gravity.CENTER, 0, 0);
        //添加按键事件监听
        backgroundAlpha(0.4f);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_cancel:
                        popupWindow.dismiss();
                        backgroundAlpha(1f);
                        break;
                    case R.id.button_ensure:
                        Toast.makeText(TimerTaskActivity.this, "请点击提交定时按钮发送定时", Toast.LENGTH_SHORT).show();
                        commit = 1;
                        popupWindow.dismiss();
                        backgroundAlpha(1f);
                        break;
                }
            }
        };
        button_cancel.setOnClickListener(listener);
        button_ensure.setOnClickListener(listener);
    }

    List<TimerTask2> timeTasks = new ArrayList<>();

    TimerTask2 timerTask11;
    TimerTask2 timerTask22;
    TimerTask2 timerTask33;

    TimerTask timerTask01=new TimerTask();

    public void addTimer() {
        int start = hour * 60 + min;
        int end = hour2 * 60 + min2;
        if (start >= end) {
            Toast.makeText(TimerTaskActivity.this, "开始时间必须小于结束时间", Toast.LENGTH_SHORT).show();
        } else {
            TimerTask timerTask2 = timerTaskDao.findWeekTimerTask(macAddress, week);
            int openHour = timerTask2.getOpenHour();
            int openMin = timerTask2.getOpenMin();
            int closeHour = timerTask2.getCloseHour();
            int closeMin = timerTask2.getCloseMin();

            int openHour2 = timerTask2.getOpenHour2();
            int openMin2 = timerTask2.getOpenMin2();
            int closeHour2 = timerTask2.getCloseHour2();
            int closeMin2 = timerTask2.getCloseMin2();

            int openHour3 = timerTask2.getOpenHour3();
            int openMin3 = timerTask2.getOpenMin3();
            int closeHour3 = timerTask2.getCloseHour3();
            int closeMin3 = timerTask2.getCloseMin3();
            int start1 = openHour * 60 + openMin;
            int end1 = closeHour * 60 + closeMin;

            int start2 = openHour2 * 60 + openMin2;
            int end2 = closeHour2 * 60 + closeMin2;

            int start3 = openHour3 * 60 + openMin3;
            int end3 = closeHour3 * 60 + closeMin3;

            timerTask01.setMacAddress(macAddress);
            timerTask01.setWeek(week);
            timerTask01.setOpenHour(openHour);
            timerTask01.setOpenMin(openMin);
            timerTask01.setCloseHour(closeHour);
            timerTask01.setCloseMin(closeMin);

            timerTask01.setOpenHour2(openHour2);
            timerTask01.setOpenMin2(openMin2);
            timerTask01.setCloseHour2(closeHour2);
            timerTask01.setCloseMin2(closeMin2);

            timerTask01.setOpenHour3(openHour3);
            timerTask01.setOpenMin3(openMin3);
            timerTask01.setCloseHour3(closeHour3);
            timerTask01.setCloseMin3(closeMin3);
            if (timerTask11==null && timerTask22==null && timerTask33==null){
                timerTask11=new TimerTask2();
                timerTask22=new TimerTask2();
                timerTask33=new TimerTask2();
            }

            timerTask11.setStart(start1);
            timerTask11.setEnd(end1);
            timerTask22.setStart(start2);
            timerTask22.setEnd(end2);
            timerTask33.setStart(start3);
            timerTask33.setEnd(end3);
            timeTasks.clear();
            timeTasks.add(timerTask11);
            timeTasks.add(timerTask22);
            timeTasks.add(timerTask33);
            int i = 0;
            for (TimerTask2 t : timeTasks) {
                //判断要添加的对象 开始结束点  是否 都小于等于开始时间 或者都大于等于结束时间
                if ((start <= t.getStart() && end <= t.getStart()) || start >= t.getEnd() && end >= t.getEnd()) {
                    i++;
                }
            }
            //如果i和list的长度相等 说明和以前添加的都不交叉 可以添加
            if (i == timeTasks.size()) {
                if (timerTask2 == null) {
                    timerTask2 = new TimerTask();
                    timerTask2.setMacAddress(macAddress);
                    timerTask2.setWeek(week);
                    timerTaskDao.insert(timerTask2);
                }

                int j = 0;
                if (start1 == 0 && end1 == 0) {
                    timerTask01.setOpenHour(hour);
                    timerTask01.setOpenMin(min);
                    timerTask01.setCloseHour(hour2);
                    timerTask01.setCloseMin(min2);
                    j = 1;
                } else if (start2 == 0 && end2 == 0) {
                    timerTask01.setOpenHour2(hour);
                    timerTask01.setOpenMin2(min);
                    timerTask01.setCloseHour2(hour2);
                    timerTask01.setCloseMin2(min2);
                    j = 2;
                } else if (start3 == 0 && end3 == 0) {
                    timerTask01.setOpenHour3(hour);
                    timerTask01.setOpenMin3(min);
                    timerTask01.setCloseHour3(hour2);
                    timerTask01.setCloseMin3(min2);
                    j = 3;
                }

                hour = 0;
                hour2 = 0;
                min = 0;
                min2 = 0;
                if (mqService != null) {
                    boolean success = mqService.sendTimerTask(timerTask01, week);
                    if (success) {
                        Toast.makeText(TimerTaskActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        TimerTask timerTask3=timerTaskDao.findWeekTimerTask(macAddress,week);
                        Log.i("ssss","-->"+timerTask3);
                    }
                }
            } else {
                Toast.makeText(TimerTaskActivity.this, "该时间段已存在", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //设置蒙版
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

    TextView tv_add_task;//新增定时
    TextView tv_copy;//复制
    TextView tv_day_1;//周一
    TextView tv_day_2;//周二
    TextView tv_day_3;//周三
    TextView tv_day_4;//周四
    TextView tv_day_5;//周五
    TextView tv_day_6;//周六
    TextView tv_day_7;//周天
    RelativeLayout task_1;//第一段定时布局
    RelativeLayout task_2;//第二段定时布局
    RelativeLayout task_3;//第三段定时布局
    TextView tv_open_one;//第一段定时开
    TextView tv_close_one;//第一段定时关
    TextView tv_open_two;//第二段定时开
    TextView tv_close_two;//第二段定时关
    TextView tv_open_thr;//第三段定时开
    TextView tv_close_thr;//第三段定时关
    TextView button_delete;//删除第一段定时
    TextView button_delete2;//删除第二段定时
    TextView button_delete3;//删除第三段定时
    TextView weeks[] = new TextView[7];//存储 1-7控件
    TimerTask timerTask;

    int copy = 0;//0表示复制 1表示粘贴，-1表示取消掉粘贴


    Map<Integer, Integer> pasterWeek = new HashMap<>();//要粘贴的周数

    /***
     * 这7个变量，如果为1，表示要粘贴的周数，如果为0，表示要取消粘贴的周数
     * */
    int paster, paster2, paster3, paster4, paster5, paster6, paster7;

    class MyAdapter extends RecyclerView.Adapter {
        public static final int TOP_TYPE = 0;
        public static final int TOP_TYPE2 = 1;
        private List<Task> list;
        private Context context;

        public MyAdapter(List<Task> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == TOP_TYPE) {
                view = View.inflate(context, R.layout.item_task_top, null);
                return new TopHolder(view);
            } else {
                view = View.inflate(context, R.layout.item_timer_task, null);
                task_1 = view.findViewById(R.id.task_1);
                task_2 = view.findViewById(R.id.task_2);
                task_3 = view.findViewById(R.id.task_3);
                tv_day_1 = view.findViewById(R.id.tv_day_1);
                tv_day_2 = view.findViewById(R.id.tv_day_2);
                tv_day_3 = view.findViewById(R.id.tv_day_3);
                tv_day_4 = view.findViewById(R.id.tv_day_4);
                tv_day_5 = view.findViewById(R.id.tv_day_5);
                tv_day_6 = view.findViewById(R.id.tv_day_6);
                tv_day_7 = view.findViewById(R.id.tv_day_7);
                tv_add_task = view.findViewById(R.id.tv_add_task);
                tv_copy = view.findViewById(R.id.tv_copy);
                tv_open_one = view.findViewById(R.id.tv_open_one);
                tv_close_one = view.findViewById(R.id.tv_close_one);
                tv_open_two = view.findViewById(R.id.tv_open_two);
                tv_close_two = view.findViewById(R.id.tv_close_two);
                tv_open_thr = view.findViewById(R.id.tv_open_thr);
                tv_close_thr = view.findViewById(R.id.tv_close_thr);
                button_delete = view.findViewById(R.id.button_delete);
                button_delete2 = view.findViewById(R.id.button_delete2);
                button_delete3 = view.findViewById(R.id.button_delete3);
                weeks[0] = tv_day_1;
                weeks[1] = tv_day_2;
                weeks[2] = tv_day_3;
                weeks[3] = tv_day_4;
                weeks[4] = tv_day_5;
                weeks[5] = tv_day_6;
                weeks[6] = tv_day_7;
                Log.i("ViewHolder", "-->:" + "ViewHolder");
                return new BottomHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Task task = list.get(position);
            Log.i("task", "-->" + task.getType());

            if (task.getType() == 0) {
                String desc = task.getDesc();
                String value = task.getValue();
                TextView tv_desc = holder.itemView.findViewById(R.id.tv_desc);
                TextView tv_value = holder.itemView.findViewById(R.id.tv_value);
                tv_desc.setText(desc + "");
                tv_value.setText(value);
                if (task.getVisibility() == 1) {
                    holder.itemView.findViewById(R.id.view1).setVisibility(View.GONE);
                } else {
                    holder.itemView.findViewById(R.id.view1).setVisibility(View.VISIBLE);
                }
            } else if (task.getType() == 1) {
                if (copy == 0) {
                    tv_copy.setText("复制");
                }
                clickwWeek(weeks[week - 1]);
                if (timerTask != null) {
                    Log.i("timerTask", "-->" + timerTask);
                    int openHour = timerTask.getOpenHour();
                    int openMin = timerTask.getOpenMin();
                    int closeHour = timerTask.getCloseHour();
                    int closeMin = timerTask.getCloseMin();

                    int openHour2 = timerTask.getOpenHour2();
                    int openMin2 = timerTask.getOpenMin2();
                    int closeHour2 = timerTask.getCloseHour2();
                    int closeMin2 = timerTask.getCloseMin2();

                    int openHour3 = timerTask.getOpenHour3();
                    int openMin3 = timerTask.getOpenMin3();
                    int closeHour3 = timerTask.getCloseHour3();
                    int closeMin3 = timerTask.getCloseMin3();
                    if (openHour == 0 && openMin == 0 && closeHour == 0 && closeMin == 0) {
                        task_1.setVisibility(View.GONE);
                    } else {
                        task_1.setVisibility(View.VISIBLE);
                        tv_open_one.setText(openHour + ":" + openMin);
                        tv_close_one.setText(closeHour + ":" + closeMin);
                    }
                    if (openHour2 == 0 && openMin2 == 0 && closeHour2 == 0 && closeMin2 == 0) {
                        task_2.setVisibility(View.GONE);
                    } else {
                        task_2.setVisibility(View.VISIBLE);
                        tv_open_two.setText(openHour2 + ":" + openMin2);
                        tv_close_two.setText(closeHour2 + ":" + closeMin2);
                    }
                    if (openHour3 == 0 && openMin3 == 0 && closeHour3 == 0 && closeMin3 == 0) {
                        task_3.setVisibility(View.GONE);
                    } else {
                        task_3.setVisibility(View.VISIBLE);
                        tv_open_two.setText(openHour2 + ":" + openMin2);
                        tv_close_two.setText(closeHour2 + ":" + closeMin2);
                    }
                } else {
                    task_1.setVisibility(View.GONE);
                    task_2.setVisibility(View.GONE);
                    task_3.setVisibility(View.GONE);
                }
                tv_day_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (copy == 1) {
                            if (week != 1) {
                                if (paster == 0) {
                                    paster = 1;
                                    tv_day_1.setTextColor(Color.WHITE);
                                    pasterWeek.put(1, paster);
                                    tv_day_1.setBackground(getResources().getDrawable(R.drawable.paster_back));
                                } else if (paster == 1) {
                                    paster = 0;
                                    tv_day_1.setTextColor(Color.parseColor("#4a4a4a"));
                                    pasterWeek.put(1, paster);
                                    tv_day_1.setBackground(getResources().getDrawable(R.drawable.week_back));
                                }
                            } else {
                                Toast.makeText(TimerTaskActivity.this, "请选择其他星期", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            week = 1;
                            setTimerTask(1);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                tv_day_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (copy == 1) {
                            if (week != 2) {
                                if (paster2 == 0) {
                                    paster2 = 1;
                                    tv_day_2.setTextColor(Color.WHITE);
                                    pasterWeek.put(2, paster2);
                                    tv_day_2.setBackground(getResources().getDrawable(R.drawable.paster_back));
                                } else if (paster2 == 1) {
                                    paster2 = 0;
                                    tv_day_2.setTextColor(Color.parseColor("#4a4a4a"));
                                    pasterWeek.put(2, paster2);
                                    tv_day_2.setBackground(getResources().getDrawable(R.drawable.week_back2));
                                }
                            } else {
                                Toast.makeText(TimerTaskActivity.this, "请选择其他星期", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            week = 2;
                            setTimerTask(2);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                tv_day_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (copy == 1) {
                            if (week != 3) {
                                if (paster3 == 0) {
                                    paster3 = 1;
                                    tv_day_3.setTextColor(Color.WHITE);
                                    pasterWeek.put(3, paster3);
                                    tv_day_3.setBackground(getResources().getDrawable(R.drawable.paster_back));
                                } else if (paster3 == 1) {
                                    paster3 = 0;
                                    tv_day_3.setTextColor(Color.parseColor("#4a4a4a"));
                                    pasterWeek.put(3, paster3);
                                    tv_day_3.setBackground(getResources().getDrawable(R.drawable.week_back2));
                                }
                            } else {
                                Toast.makeText(TimerTaskActivity.this, "请选择其他星期", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            week = 3;
                            setTimerTask(week);
                            adapter.notifyDataSetChanged();
                        }

                    }
                });
                tv_day_4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (copy == 1) {
                            if (week != 4) {
                                if (paster4 == 0) {
                                    paster4 = 1;
                                    tv_day_4.setTextColor(Color.WHITE);
                                    pasterWeek.put(4, paster4);
                                    tv_day_4.setBackground(getResources().getDrawable(R.drawable.paster_back));
                                } else if (paster4 == 1) {
                                    paster4 = 0;
                                    tv_day_4.setTextColor(Color.parseColor("#4a4a4a"));
                                    pasterWeek.put(4, paster4);
                                    tv_day_4.setBackground(getResources().getDrawable(R.drawable.week_back2));
                                }
                            } else {
                                Toast.makeText(TimerTaskActivity.this, "请选择其他星期", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            week = 4;
                            setTimerTask(4);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                tv_day_5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (copy == 1) {
                            if (week != 5) {
                                if (paster5 == 0) {
                                    paster5 = 1;
                                    tv_day_5.setTextColor(Color.WHITE);
                                    pasterWeek.put(5, paster5);
                                    tv_day_5.setBackground(getResources().getDrawable(R.drawable.paster_back));
                                } else if (paster5 == 1) {
                                    paster5 = 0;
                                    tv_day_5.setTextColor(Color.parseColor("#4a4a4a"));
                                    pasterWeek.put(5, paster5);
                                    tv_day_5.setBackground(getResources().getDrawable(R.drawable.week_back2));
                                }
                            } else {
                                Toast.makeText(TimerTaskActivity.this, "请选择其他星期", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            week = 5;
                            setTimerTask(5);
                            adapter.notifyDataSetChanged();
                        }

                    }
                });
                tv_day_6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (copy == 1) {
                            if (week != 6) {
                                if (paster6 == 0) {
                                    paster6 = 1;
                                    tv_day_6.setTextColor(Color.WHITE);
                                    pasterWeek.put(6, paster6);
                                    tv_day_6.setBackground(getResources().getDrawable(R.drawable.paster_back));
                                } else if (paster6 == 1) {
                                    paster6 = 0;
                                    tv_day_6.setTextColor(Color.parseColor("#4a4a4a"));
                                    pasterWeek.put(6, paster6);
                                    tv_day_6.setBackground(getResources().getDrawable(R.drawable.week_back2));
                                }
                            } else {
                                Toast.makeText(TimerTaskActivity.this, "请选择其他星期", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            week = 6;
                            setTimerTask(6);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                tv_day_7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (copy == 1) {
                            if (week != 7) {
                                if (paster7 == 0) {
                                    paster7 = 1;
                                    tv_day_7.setTextColor(Color.WHITE);
                                    pasterWeek.put(7, paster7);
                                    tv_day_7.setBackground(getResources().getDrawable(R.drawable.paster_back));
                                } else if (paster7 == 1) {
                                    paster7 = 0;
                                    tv_day_7.setTextColor(Color.parseColor("#4a4a4a"));
                                    pasterWeek.put(7, paster7);
                                    tv_day_7.setBackground(getResources().getDrawable(R.drawable.week_back2));
                                }
                            } else {
                                Toast.makeText(TimerTaskActivity.this, "请选择其他星期", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            week = 7;
                            setTimerTask(7);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

                tv_add_task.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (copy == 1) {
                            Toast.makeText(TimerTaskActivity.this, "你正在进行粘贴操作", Toast.LENGTH_SHORT).show();
                        } else if (copy == -1) {
                            Toast.makeText(TimerTaskActivity.this, "请取消你的粘贴操作", Toast.LENGTH_SHORT).show();
                        } else {
                            popupView();
                        }
                    }
                });
                tv_copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (copy == 0) {
                            tv_copy.setText("粘贴");
                            copy = 1;
                            commit = 2;
                        } else if (copy == 1) {
                            tv_copy.setText("取消");
                            commit = 0;
                            paster = 0;
                            paster2 = 0;
                            paster3 = 0;
                            paster4 = 0;
                            paster5 = 0;
                            paster6 = 0;
                            paster7 = 0;
                            pasterWeek.clear();
                            copy = 0;
                            adapter.notifyDataSetChanged();
                        } else if (copy == -1) {
                            commit = 0;
                            tv_copy.setText("复制");
                            pasterWeek.clear();
                            paster = 0;
                            paster2 = 0;
                            paster3 = 0;
                            paster4 = 0;
                            paster5 = 0;
                            paster6 = 0;
                            paster7 = 0;
                            pasterWeek.clear();
                            copy = 0;
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                button_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (copy == 1) {
                            Toast.makeText(TimerTaskActivity.this, "你正在进行粘贴操作", Toast.LENGTH_SHORT).show();
                        } else if (copy == -1) {
                            Toast.makeText(TimerTaskActivity.this, "请取消你的粘贴操作", Toast.LENGTH_SHORT).show();
                        } else {
                            timerTask = timerTaskDao.findWeekTimerTask(macAddress, week);

                            int openHour2 = timerTask.getOpenHour2();
                            int openMin2 = timerTask.getOpenMin2();
                            int closeHour2 = timerTask.getCloseHour2();
                            int closeMin2 = timerTask.getCloseMin2();

                            int openHour3 = timerTask.getOpenHour3();
                            int openMin3 = timerTask.getOpenMin3();
                            int closeHour3 = timerTask.getCloseHour3();
                            int closeMin3 = timerTask.getCloseMin3();
                            timerTask01.setMacAddress(macAddress);
                            timerTask01.setWeek(week);
                            timerTask01.setOpenHour(0);
                            timerTask01.setOpenMin(0);
                            timerTask01.setCloseHour(0);
                            timerTask01.setCloseMin(0);

                            timerTask01.setOpenHour2(openHour2);
                            timerTask01.setOpenMin2(openMin2);
                            timerTask01.setCloseHour2(closeHour2);
                            timerTask01.setCloseMin2(closeMin2);

                            timerTask01.setOpenHour3(openHour3);
                            timerTask01.setOpenMin3(openMin3);
                            timerTask01.setCloseHour3(closeHour3);
                            timerTask01.setCloseMin3(closeMin3);
                            deleteTimerTask(timerTask01);
//                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                button_delete2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (copy == 1) {
                            Toast.makeText(TimerTaskActivity.this, "你正在进行粘贴操作", Toast.LENGTH_SHORT).show();
                        } else if (copy == -1) {
                            Toast.makeText(TimerTaskActivity.this, "请取消你的粘贴操作", Toast.LENGTH_SHORT).show();
                        } else {
                            timerTask = timerTaskDao.findWeekTimerTask(macAddress, week);
//                            timerTask.setOpenHour2(0);
//                            timerTask.setOpenMin2(0);
//                            timerTask.setCloseHour2(0);
//                            timerTask.setCloseMin2(0);
//                            timerTaskDao.update(timerTask);
//                            setTimerTask(week);
//                            deleteTimerTask(timerTask);
//                            adapter.notifyDataSetChanged();
                            int openHour = timerTask.getOpenHour();
                            int openMin = timerTask.getOpenMin();
                            int closeHour = timerTask.getCloseHour();
                            int closeMin = timerTask.getCloseMin();

                            int openHour3 = timerTask.getOpenHour3();
                            int openMin3 = timerTask.getOpenMin3();
                            int closeHour3 = timerTask.getCloseHour3();
                            int closeMin3 = timerTask.getCloseMin3();
                            timerTask01.setMacAddress(macAddress);
                            timerTask01.setWeek(week);
                            timerTask01.setOpenHour(openHour);
                            timerTask01.setOpenMin(openMin);
                            timerTask01.setCloseHour(closeHour);
                            timerTask01.setCloseMin(closeMin);

                            timerTask01.setOpenHour2(0);
                            timerTask01.setOpenMin2(0);
                            timerTask01.setCloseHour2(0);
                            timerTask01.setCloseMin2(0);

                            timerTask01.setOpenHour3(openHour3);
                            timerTask01.setOpenMin3(openMin3);
                            timerTask01.setCloseHour3(closeHour3);
                            timerTask01.setCloseMin3(closeMin3);
                            deleteTimerTask(timerTask01);
                        }

//                        Toast.makeText(TimerTaskActivity.this, "请点击提交定时按钮删除", Toast.LENGTH_SHORT).show();
                    }
                });
                button_delete3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (copy == 1) {
                            Toast.makeText(TimerTaskActivity.this, "你正在进行粘贴操作", Toast.LENGTH_SHORT).show();
                        } else if (copy == -1) {
                            Toast.makeText(TimerTaskActivity.this, "请取消你的粘贴操作", Toast.LENGTH_SHORT).show();
                        } else {
                            timerTask = timerTaskDao.findWeekTimerTask(macAddress, week);
//                            timerTask.setOpenHour3(0);
//                            timerTask.setOpenMin3(0);
//                            timerTask.setCloseHour3(0);
//                            timerTask.setCloseMin3(0);
//                            timerTaskDao.update(timerTask);
//                            setTimerTask(week);
//                            deleteTimerTask(timerTask);
//                            adapter.notifyDataSetChanged();
                            int openHour = timerTask.getOpenHour();
                            int openMin = timerTask.getOpenMin();
                            int closeHour = timerTask.getCloseHour();
                            int closeMin = timerTask.getCloseMin();

                            int openHour2 = timerTask.getOpenHour2();
                            int openMin2 = timerTask.getOpenMin2();
                            int closeHour2 = timerTask.getCloseHour2();
                            int closeMin2 = timerTask.getCloseMin2();


                            timerTask01.setMacAddress(macAddress);
                            timerTask01.setWeek(week);
                            timerTask01.setOpenHour(openHour);
                            timerTask01.setOpenMin(openMin);
                            timerTask01.setCloseHour(closeHour);
                            timerTask01.setCloseMin(closeMin);

                            timerTask01.setOpenHour2(openHour2);
                            timerTask01.setOpenMin2(openMin2);
                            timerTask01.setCloseHour2(closeHour2);
                            timerTask01.setCloseMin2(closeMin2);

                            timerTask01.setOpenHour3(0);
                            timerTask01.setOpenMin3(0);
                            timerTask01.setCloseHour3(0);
                            timerTask01.setCloseMin3(0);
                            deleteTimerTask(timerTask01);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public int getItemViewType(int position) {
            Task task = list.get(position);
            if (task.getType() == 1) {
                return TOP_TYPE2;
            } else
                return TOP_TYPE;
        }
    }

    private void clickwWeek(TextView textView) {
        for (int i = 0; i < 7; i++) {
            if (textView == weeks[i]) {
                weeks[i].setTextColor(Color.WHITE);
                weeks[i].setBackground(getResources().getDrawable(R.drawable.week_back));
            } else {
                weeks[i].setTextColor(Color.parseColor("#4a4a4a"));
                weeks[i].setBackground(getResources().getDrawable(R.drawable.week_back2));
            }
        }
    }

    class TimerTaskAsync extends AsyncTask<Map<Integer, Integer>, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog != null) {
                progressDialog.setMessage("正在加载数据,请稍后...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected Integer doInBackground(Map<Integer, Integer>... maps) {
            int code = 0;
            try {
                TimerTask timerTask = timerTaskDao.findWeekTimerTask(macAddress, week);
                int openHour = timerTask.getOpenHour();
                int openMin = timerTask.getOpenMin();
                int closeHour = timerTask.getCloseHour();
                int closeMin = timerTask.getCloseMin();

                int openHour2 = timerTask.getOpenHour2();
                int openMin2 = timerTask.getOpenMin2();
                int closeHour2 = timerTask.getCloseHour2();
                int closeMin2 = timerTask.getCloseMin2();

                int openHour3 = timerTask.getOpenHour3();
                int openMin3 = timerTask.getOpenMin3();
                int closeHour3 = timerTask.getCloseHour3();
                int closeMin3 = timerTask.getCloseMin3();
                timerTask01.setMacAddress(macAddress);
                timerTask01.setWeek(week);
                timerTask01.setOpenHour(openHour);
                timerTask01.setOpenMin(openMin);
                timerTask01.setCloseHour(closeHour);
                timerTask01.setCloseMin(closeMin);

                timerTask01.setOpenHour2(openHour2);
                timerTask01.setOpenMin2(openMin2);
                timerTask01.setCloseHour2(closeHour2);
                timerTask01.setCloseMin2(closeMin2);

                timerTask01.setOpenHour3(openHour3);
                timerTask01.setOpenMin3(openMin3);
                timerTask01.setCloseHour3(closeHour3);
                timerTask01.setCloseMin3(closeMin3);
                int size = 0;
                for (Map.Entry<Integer, Integer> entry : pasterWeek.entrySet()) {
                    int key = entry.getKey();
                    int value = entry.getValue();
                    if (value != 0 && mqService != null) {
                        Log.i("Mapkey", "-->" + key);
                        boolean success = mqService.sendTimerTask(timerTask01, key);
                        if (success) {
                            TimerTask timerTask2 = timerTaskDao.findWeekTimerTask(macAddress, key);
                            if (timerTask2 == null) {
                                openHour = timerTask.getOpenHour();
                                openMin = timerTask.getOpenMin();
                                closeHour = timerTask.getCloseHour();
                                closeMin = timerTask.getCloseMin();

                                openHour2 = timerTask.getOpenHour2();
                                openMin2 = timerTask.getOpenMin2();
                                closeHour2 = timerTask.getCloseHour2();
                                closeMin2 = timerTask.getCloseMin2();

                                openHour3 = timerTask.getOpenHour3();
                                openMin3 = timerTask.getOpenMin3();
                                closeHour3 = timerTask.getCloseHour3();
                                closeMin3 = timerTask.getCloseMin3();
                                timerTask2 = new TimerTask(macAddress, key, openHour, openMin, closeHour, closeMin, openHour2, openMin2, closeHour2, closeMin2, openHour3, openMin3, closeHour3, closeMin3);
                                timerTaskDao.insert(timerTask2);
                            }
                        }
                        Thread.sleep(4 * 1000);
                        size += 1;
                    }
                }
                if (size == pasterWeek.size()) {
                    code = 2000;
                }
            } catch (Exception e) {
                e.printStackTrace();
                code = 2000;
            }
            return code;
        }

        @Override
        protected void onPostExecute(Integer code) {
            super.onPostExecute(code);
            progressDialog.dismiss();
            pasterWeek.clear();
                paster = 0;
                paster2 = 0;
                paster3 = 0;
                paster4 = 0;
                paster5 = 0;
                paster6 = 0;
                paster7 = 0;
                pasterWeek.clear();
                copy = 0;
                commit = 0;
                adapter.notifyDataSetChanged();
        }
    }

//    int timeQuantum = 0;

    public void deleteTimerTask(TimerTask timerTask) {
        if (mqService != null) {
            boolean success=mqService.sendTimerTask(timerTask, week);
            if (success){
                Toast.makeText(this,"提交成功",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"提交失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    class TopHolder extends RecyclerView.ViewHolder {

        public TopHolder(View view) {
            super(view);
        }
    }

    class BottomHolder extends RecyclerView.ViewHolder {
        public BottomHolder(View view) {
            super(view);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();
        running = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        running = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (boundclock) {
            unbindService(clockconnection);
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
        }

        if (progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        running = false;
    }

    Intent clockintent;
    MQService mqService;
    boolean boundclock;
    ServiceConnection clockconnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder = (MQService.LocalBinder) service;
            mqService = binder.getService();
            boundclock = true;
            Log.e("QQQQQQQQQQQDDDDDDD", "onServiceConnected: ------->");
            if (mqService != null) {
                new DayAsync().execute();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    /***
     * 加载7天定时任务与设备基础数据
     */
    class DayAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog != null) {
                progressDialog.setMessage("正在加载数据,请稍后...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                for (int i = 1; i <= 7; i++) {
                    TimerTask timerTask = timerTaskDao.findWeekTimerTask(macAddress, i);
                    Log.i("TimerTaskMac", "-->" + timerTask + "week=" + i);
                    if (timerTask == null) {
                        timerTask = new TimerTask(macAddress, i, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                        timerTaskDao.insert(timerTask);
                    } else {
                        timerTask.setWeek(i);
                        timerTask.setMacAddress(macAddress);
                        timerTaskDao.update(timerTask);
                    }
                }
                if (mqService != null) {
                    for (int i = 0; i < 8; i++) {
                        if (i == 0) {
                            mqService.getData(equipment.getDeviceMac(), 0x23);
                        } else if (i == 1) {
                            mqService.getDate(equipment.getDeviceMac(), 0x31);
                        } else if (i == 2) {
                            mqService.getDate(equipment.getDeviceMac(), 0x32);

                        } else if (i == 3) {
                            mqService.getDate(equipment.getDeviceMac(), 0x33);

                        } else if (i == 4) {
                            mqService.getDate(equipment.getDeviceMac(), 0x34);

                        } else if (i == 5) {

                            mqService.getDate(equipment.getDeviceMac(), 0x35);

                        } else if (i == 6) {
                            mqService.getDate(equipment.getDeviceMac(), 0x36);
                        } else if (i == 7) {
                            mqService.getDate(equipment.getDeviceMac(), 0x37);
                        }
                        Thread.sleep(2000);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    }

    MessageReceiver receiver;

    class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String macAddress = intent.getStringExtra("macAddress");

                int funCode = intent.getIntExtra("funCode", 0);
                if (0x23 == funCode) {
                    Equipment equipment2 = (Equipment) intent.getSerializableExtra("equipment");
                    if (equipment != null && macAddress.equals(equipment.getDeviceMac())) {
                        equipment = equipment2;
                        int hour = equipment.getHour();
                        int min = equipment.getMin();
                        int upTemp = equipment.getUpTemp();
                        int downTemp = equipment.getDownTemp();
                        int noWaterDS = equipment.getNoWaterDS();
                        int inflowTime = equipment.getInflowTime();
                        int maxInflowTime = equipment.getMaxInflowTime();
                        task1.setValue(upTemp + "℃");
                        task2.setValue(downTemp + "℃");
                        task3.setValue(inflowTime + "/秒");
                        task4.setValue(maxInflowTime + "");
                        task5.setValue(hour + ":" + min);
                        task6.setValue("" + noWaterDS);
                        list.set(0, task1);
                        list.set(1, task2);
                        list.set(2, task3);
                        list.set(3, task4);
                        list.set(4, task5);
                        list.set(5, task6);
                        adapter.notifyDataSetChanged();
                    }
                } else if (0x22 == funCode) {
//                    TimerTask timerTask2 = (TimerTask) intent.getSerializableExtra("timerTask");
                    int week2 = intent.getIntExtra("week", 0);
                    if (week == week2) {
                        setTimerTask(week);
                        task7.setValue("1");
                        list.set(6, task7);
                        TimerTask timerTask2= (TimerTask) intent.getSerializableExtra("timerTask");
                        timerTask=timerTask2;
                        timerTaskDao.update(timerTask2);
                        adapter.notifyDataSetChanged();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setTimerTask(int week) {
        timerTask = timerTaskDao.findWeekTimerTask(macAddress, week);
    }
}
