package com.peihou.waterpurifer.service;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.peihou.waterpurifer.MainActivity;
import com.peihou.waterpurifer.activity.EqupmentActivity;
import com.peihou.waterpurifer.activity.TimerTaskActivity;
import com.peihou.waterpurifer.database.dao.daoImp.EquipmentImpl;
import com.peihou.waterpurifer.database.dao.daoImp.TimerTaskImpl;
import com.peihou.waterpurifer.pojo.Equipment;
import com.peihou.waterpurifer.pojo.TimerTask;
import com.peihou.waterpurifer.util.TenTwoUtil;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MQService extends Service {

    private String host = "tcp://47.98.131.11:1883";
    /**
     * 主机名称
     */
    private String userName = "admin";
    /**
     * 用户名
     */
    private String passWord = "Xr7891122";
    /**
     * 密码
     */
    private Context mContext = this;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private MqttClient client;
    private MqttConnectOptions options;
    String clientId = "";
    LocalBinder binder = new LocalBinder();
    /***
     * 头码
     */
    private int headCode = 0X90;

    /***
     * 商业模块00：忽略；11：按水流量租
     凭；22：按时间租赁；33：
     按售水量售水型；FF：常规
     机型
     */
    private int[] bussinessmodule = {0X00, 0X11, 0X22, 0X33, 0XFF};
    EquipmentImpl equipmentDao;
    TimerTaskImpl timerTaskDao;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("MQService", "-->onCreate");
        init();
        equipmentDao = new EquipmentImpl(getApplicationContext());
        timerTaskDao = new TimerTaskImpl(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("MQService", "-->onStartCommand");
        connect();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * 初始化MQTT
     */
    private void init() {
        try {
            //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存

            clientId = UUID.randomUUID().toString();
            client = new MqttClient(host, clientId,
                    new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(true);
            //设置连接的用户名
            options.setUserName(userName);
            //设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(15);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(60);


            //设置回调
            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，一般在这里面进行重连
                    System.out.println("connectionLost----------");
                    startReconnect();
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish后会执行到这里
                    System.out.println("deliveryComplete---------"
                            + token.isComplete());
                }

                @Override
                public void messageArrived(String topicName, MqttMessage message) {
                    try {
                        Log.i("topicName", "topicName:" + topicName);
                        String msg = message.toString();
                        new LoadAsyncTask().execute(topicName, message.toString());
                        Log.i("message", "message:" + msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDate(String mac, int funCode) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            int headCode = 0x55;
            int checkCode = (headCode + funCode) % 256;
            int endCode = 0x88;
            jsonArray.put(0, headCode);
            jsonArray.put(1, funCode);
            jsonArray.put(2, checkCode);
            jsonArray.put(3, endCode);
            jsonObject.put("WPurifier", jsonArray);
            String topicName = "p99/wPurifier1/" + mac + "/set";
            String payLoad = jsonObject.toString();
            publish(topicName, 1, payLoad);
            Log.e("getData", "getDate: -->");
            Log.e("FFFDDDD", "getDate:获取数据 " + mac);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能码为
     * 0x11:基本功能查询
     * 0x23:基础设置
     * 0x31:周一定时设置查询
     * 0x32:周二定时设置查询
     * 0x33:周三定时设置查询
     * 0x34:周四定时设置查询
     * 0x35:周五定时设置查询
     * 0x36:周六定时设置查询
     * 0x37:周七定时设置查询
     *
     * @param mac
     * @param funCode
     */
    public void getData(String mac, int funCode) {
        try {
            int headCode = 0x55;

            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(0, headCode);
            jsonArray.put(1, funCode);
            int sum = 0;
            for (int i = 0; i < 2; i++) {
                sum += jsonArray.getInt(i);
            }
            int checkCode = sum % 256;
            jsonArray.put(2, checkCode);
            int endCode = 0x88;
            jsonArray.put(3, endCode);
            jsonObject.put("WPurifier", jsonArray);
            String topicName = "p99/wPurifier1/" + mac + "/set";
            String payLoad = jsonObject.toString();
            boolean success = publish(topicName, 1, payLoad);
            if (!success)
                publish(topicName, 1, payLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送基本功能
     *
     * @param equipment
     */
    public void sendData(Equipment equipment) {
        int headCode = 0x90;
        int busMode = equipment.getBussinessmodule();
        int funCode = 0x11;
        int dataLength = 0x14;
        int runState = equipment.getIsOpen();
        int wPurifierfilter1 = equipment.getWPurifierfilter1();
        int wPurifierfilter2 = equipment.getWPurifierfilter2();
        int wPurifierfilter3 = equipment.getWPurifierfilter3();
        int wPurifierfilter4 = equipment.getWPurifierfilter4();
        int wPurifierfilter5 = equipment.getWPurifierfilter5();
        int IsLeakage = equipment.getIsLeakage();
        int x[] = new int[8];
        x[0] = wPurifierfilter1;
        x[1] = wPurifierfilter2;
        x[2] = wPurifierfilter3;
        x[3] = wPurifierfilter4;
        x[4] = wPurifierfilter5;
        if (IsLeakage == 0) {
            x[5] = 0;
            x[6] = 0;
        } else if (IsLeakage == 1) {
            x[5] = 0;
            x[6] = 1;
        } else if (IsLeakage == 2) {
            x[5] = 1;
            x[6] = 0;
        }
        int content = TenTwoUtil.changeToTen(x);
        int isReset = equipment.getIsReset();/**流量计计量清0*/
        int wPurifierPrimaryQuqlity = equipment.getWPurifierPrimaryQuqlity();/**原水TDS*/
        int wPurifierOutQuqlity = equipment.getWPurifierOutQuqlity();/**净水TDS*/
        int isReset2 = equipment.getIsReset2();/**是否清除计水时间*/
        int wContinuiProductionTime = equipment.getWContinuiProductionTime();/**设置连续制水时间*/
        int RechargeTime = equipment.getRechargeTime();/**设置充值租赁时间*/
        int RechargeFlow = equipment.getRechargeFlow();/**设置租赁流量*/
        int gear = equipment.getGear();/**设置售水量档位*/
        int ready = 0;
        int ready2 = 0;
        int ready3 = 0;
        int BackwashTime = 0;
        int wMobileSignal = equipment.getWMobileSignal();/**移动信号强弱*/
        int machineType = 0;
        int endCode = 0x09;

        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(0, headCode);
            jsonArray.put(1, busMode);
            jsonArray.put(2, funCode);
            jsonArray.put(3, dataLength);
            jsonArray.put(4, runState);
            jsonArray.put(5, content);
            jsonArray.put(6, isReset);
            jsonArray.put(7, wPurifierPrimaryQuqlity / 256);
            jsonArray.put(8, wPurifierPrimaryQuqlity % 256);
            jsonArray.put(9, wPurifierOutQuqlity);
            jsonArray.put(10, isReset2);
            jsonArray.put(11, wContinuiProductionTime);
            jsonArray.put(12, RechargeTime / 256);
            jsonArray.put(13, RechargeTime % 256);
            jsonArray.put(14, RechargeFlow / 256);
            jsonArray.put(15, RechargeFlow % 256);
            jsonArray.put(16, gear);
            jsonArray.put(17, ready);
            jsonArray.put(18, ready2);
            jsonArray.put(19, ready3);
            jsonArray.put(20, BackwashTime);
            jsonArray.put(21, wMobileSignal);
            jsonArray.put(22, machineType);
            int sum = 0;
            for (int i = 0; i < 23; i++) {
                sum += jsonArray.getInt(i);
            }
            int checkCode = sum % 256;
            jsonArray.put(23, checkCode);
            jsonArray.put(24, endCode);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("WPurifier", jsonArray);
            String mac = equipment.getDeviceMac();
            String topicName = "p99/wPurifier1/" + mac + "/set";
            String payLoad = jsonObject.toString();
            boolean success = publish(topicName, 1, payLoad);
            if (!success)
                success = publish(topicName, 1, payLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean sendTimerTask(TimerTask timerTask,int week) {
        boolean success=false;
        try {
            int headCode = 0x90;
            int bussMode = 68;
            int funCode = 0x22;
            int dataLength = 0x11;
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
            int sum = 0;
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(0, headCode);
            jsonArray.put(1, bussMode);
            jsonArray.put(2, funCode);
            jsonArray.put(3, dataLength);
            jsonArray.put(4, week);
            jsonArray.put(5, openHour);
            jsonArray.put(6, openMin);
            jsonArray.put(7, closeHour);
            jsonArray.put(8, closeMin);
            jsonArray.put(9, openHour2);
            jsonArray.put(10, openMin2);
            jsonArray.put(11, closeHour2);
            jsonArray.put(12, closeMin2);
            jsonArray.put(13, openHour3);
            jsonArray.put(14, openMin3);
            jsonArray.put(15, closeHour3);
            jsonArray.put(16, closeMin3);
            for (int i = 0; i < 17; i++) {
                sum += jsonArray.getInt(i);
            }
            int checkCode = sum % 256;
            jsonArray.put(17, checkCode);
            int endCode = 0x09;
            jsonArray.put(18, endCode);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("WPurifier", jsonArray);
            String mac = timerTask.getMacAddress();
            String topicName = "p99/wPurifier1/" + mac + "/set";
            String payLoad = jsonObject.toString();
            success = publish(topicName, 1, payLoad);
            if (!success)
                success = publish(topicName, 1, payLoad);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public void sendBasic(Equipment equipment) {
        try {
            int headCode = 0x90;
            int bussMode = equipment.getBussinessmodule();
            int funCode = 0x23;
            int dataLengh = 0x12;
            int week = equipment.getWeek();//系统星期
            int hour = equipment.getHour();//系统小时
            int min = equipment.getMin();//系统分钟
            int upTemp = equipment.getUpTemp();//上温
            int downTemp = equipment.getDownTemp();//下温
            int noWaterDS = equipment.getNoWaterDS();//无水监测灵敏度
            int inflowTime = equipment.getInflowTime();//进水时间
            int maxInflowTime = equipment.getMaxInflowTime();//最长进水时间
            int endCode = 0x09;
            int sum = 0;
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(0, headCode);
            jsonArray.put(1, bussMode);
            jsonArray.put(2, funCode);
            jsonArray.put(3, dataLengh);
            jsonArray.put(4, week);
            jsonArray.put(5, hour);
            jsonArray.put(6, min);
            jsonArray.put(7, upTemp);
            jsonArray.put(8, downTemp);
            jsonArray.put(9, noWaterDS);
            jsonArray.put(10, inflowTime);
            jsonArray.put(11, maxInflowTime);
            jsonArray.put(12, 0);
            jsonArray.put(13, 0);
            jsonArray.put(14, 0);
            jsonArray.put(15, 0);
            jsonArray.put(16, 0);
            jsonArray.put(17, 0);
            jsonArray.put(18, 0);
            jsonArray.put(19, 0);
            jsonArray.put(20, 0);
            jsonArray.put(21, 0);
            for (int i = 0; i < 22; i++) {
                sum += jsonArray.getInt(i);
            }
            int checkCode = sum % 256;
            jsonArray.put(22, checkCode);
            jsonArray.put(23, endCode);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("WPurifier", jsonArray);
            String mac = equipment.getDeviceMac();
            String topicName = "p99/wPurifier1/" + mac + "/set";
            String payLoad = jsonObject.toString();
            boolean success = publish(topicName, 1, payLoad);
            if (!success)
                success = publish(topicName, 1, payLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class LoadAsyncTask extends AsyncTask<String, Void, Object> {

        @Override
        protected Object doInBackground(String... strings) {

            String topicName = strings[0];/**收到的主题*/
            String message = strings[1];/**收到的消息*/
            Log.i("topicName", "-->:" + topicName);
            String macAddress = null;
            if (topicName.startsWith("p99/wPurifier1")) {
                macAddress = topicName.substring(15, topicName.lastIndexOf("/"));
            }
            JSONArray messageJsonArray = null;
            JSONObject messageJsonObject = null;
//            Equipment equipment = null;
            Equipment equipment = equipmentDao.findDeviceByMacAddress2(macAddress);
//            equipment=new Equipment();
            try {
                if (!TextUtils.isEmpty(message) && message.startsWith("{") && message.endsWith("}")) {
                    messageJsonObject = new JSONObject(message);
                }
                if (messageJsonObject != null && messageJsonObject.has("WPurifier")) {
                    messageJsonArray = messageJsonObject.getJSONArray("WPurifier");
                }

                int funCode = -1;
                int week = -1;
                TimerTask timerTask = null;
                if (topicName.contains("transfer")){
                    if (equipment != null && messageJsonArray != null && messageJsonArray.getInt(2) == 0x11) {
                        Log.e("hasData", "getDate: -->");
                        int wPurifierState;/*净水器状态*/
                        int bussinessmodule;/*商业模式*/
                        /*净水器滤芯寿命 1-5*/
                        int wPurifierfilter1, wPurifierfilter2, wPurifierfilter3, wPurifierfilter4, wPurifierfilter5;
                        String deviceMCU;
                        int wTrueFlowmeter;/*净水器流量计实际值*/
                        int wPurifierPrimaryQuqlity;/*净水器原生水质*/
                        int FlowmeterWarm;/**净水器流量计报警*/
                        int wPurifierOutQuqlity;/*净水器出水水质*/
                        int wTotalProductionTime;/*净水器累计制水时间*/
                        int wContinuiProductionTime;/**净水器连续制水时间*/
                        int wWaterStall;/*净水器售水量档位*/
                        int wMobileSignal;/*净水器移动信号*/
                        int IsOpen;/**净水器是否开机*/
                        int HavaWater;/**净水器是否有水*/
                        int WaterWash;/**净水器是否冲洗*/
                        int MakeWater;/**净水器是否制水*/
                        int IsFull;/**净水器是否冲满*/
                        int Repair;/**净水器检修*/
                        int IsLeakage;/**净水器是否漏水*/
                        int Warming;/**净水器温度值*/
                        int AlarmState;/**净水器设备报警状态*/
                        int AlarmIsLeakage;/**净水器报警漏水*/
                        int ContinuProduction;/**净水器连续制水*/
                        int AlarmFlowmeter;/**净水器报警流量计错误*/
                        int AlarmWash;/**净水器报警冲洗电磁阀错误*/
                        int RechargeTime;/**净水器租凭充值时间*/
                        int RechargeFlow;/**净水器剩余充值流量*/
                        int BackwaterInterval;/**净水器回水间隔时间*/
                        int BackwashTime;/**净水器回水冲洗时间*/
                        int BackwashInterval;/**净水器冲洗间隔*/
                        int MachineType;/**净水机器类型*/
                        int WashTime;/*净水机冲洗时间*/

                        bussinessmodule = messageJsonArray.getInt(1);
                        deviceMCU = messageJsonArray.getString(3);
                        wPurifierState = messageJsonArray.getInt(5);
                    /*bit0:0表示关机，1表示开机;bit1:0表示无缺水，1表示有缺水； bit2:制水；bit3:冲洗；bit4:水满；bit5:
                    检修；(以上各位为0是表示无报警，1表示有报警）；bit6：漏水检测状态，1功能开启，有漏水检测；0功能关闭，无漏水检测*/
                        int[] y = TenTwoUtil.changeToTwo(wPurifierState);
                        IsOpen = y[0];
                        HavaWater = y[1];
                        MakeWater = y[2];
                        WaterWash = y[3];
                        IsFull = y[4];
                        Repair = y[5];
                        IsLeakage = y[6];
                        wPurifierfilter1 = messageJsonArray.getInt(6);
                        wPurifierfilter2 = messageJsonArray.getInt(7);
                        wPurifierfilter3 = messageJsonArray.getInt(8);
                        wPurifierfilter4 = messageJsonArray.getInt(9);
                        wPurifierfilter5 = messageJsonArray.getInt(10);
                        FlowmeterWarm = messageJsonArray.getInt(11);
                        wTrueFlowmeter = messageJsonArray.getInt(12) * 256 + messageJsonArray.getInt(13);
                        Warming = messageJsonArray.getInt(14);
                        wPurifierPrimaryQuqlity = messageJsonArray.getInt(15) * 256 + messageJsonArray.getInt(16);
                        wPurifierOutQuqlity = messageJsonArray.getInt(17);
                        wTotalProductionTime = messageJsonArray.getInt(18) * 256 + messageJsonArray.getInt(19);
                        AlarmState = messageJsonArray.getInt(20);
                        int[] x = TenTwoUtil.changeToTwo(AlarmState);
                        ContinuProduction = x[0];
                        AlarmIsLeakage = x[1];
                        AlarmFlowmeter = x[2];
                        AlarmWash = x[3];
                        wContinuiProductionTime = messageJsonArray.getInt(21);
                        RechargeTime = messageJsonArray.getInt(22) * 256 + messageJsonArray.getInt(23);
                        RechargeFlow = messageJsonArray.getInt(24) * 256 + messageJsonArray.getInt(25);
                        BackwaterInterval = messageJsonArray.getInt(26) * 256 + messageJsonArray.getInt(27);
                        BackwashTime = messageJsonArray.getInt(28);
                        BackwashInterval = messageJsonArray.getInt(29) * 256 + messageJsonArray.getInt(30);
                        wWaterStall = messageJsonArray.getInt(31);
                        WashTime = messageJsonArray.getInt(35);
                        wMobileSignal = messageJsonArray.getInt(36);
                        MachineType = messageJsonArray.getInt(37);

                        equipment.setWPurifierState(wPurifierState);
                        equipment.setBussinessmodule(bussinessmodule);
                        equipment.setWPurifierfilter1(wPurifierfilter1);
                        equipment.setWPurifierfilter2(wPurifierfilter2);
                        equipment.setWPurifierfilter3(wPurifierfilter3);
                        equipment.setWPurifierfilter4(wPurifierfilter4);
                        equipment.setWPurifierfilter5(wPurifierfilter5);
                        equipment.setDeviceMCU(deviceMCU);
                        equipment.setIsOpen(IsOpen);
                        equipment.setHavaWater(HavaWater);
                        equipment.setMakeWater(MakeWater);
                        equipment.setWaterWash(WaterWash);
                        equipment.setIsFull(IsFull);
                        equipment.setRepair(Repair);
                        equipment.setIsLeakage(IsLeakage);
                        equipment.setAlarmIsLeakage(AlarmIsLeakage);
                        equipment.setFlowmeterWarm(FlowmeterWarm);
                        equipment.setWTrueFlowmeter(wTrueFlowmeter);
                        equipment.setWarming(Warming);
                        equipment.setWPurifierPrimaryQuqlity(wPurifierPrimaryQuqlity);
                        equipment.setWPurifierOutQuqlity(wPurifierOutQuqlity);
                        equipment.setWTotalProductionTime(wTotalProductionTime);
                        equipment.setContinuProduction(ContinuProduction);
                        equipment.setAlarmFlowmeter(AlarmFlowmeter);
                        equipment.setAlarmWash(AlarmWash);
                        equipment.setWContinuiProductionTime(wContinuiProductionTime);
                        equipment.setRechargeTime(RechargeTime);
                        equipment.setRechargeFlow(RechargeFlow);
                        equipment.setBackwashInterval(BackwashInterval);
                        equipment.setBackwashTime(BackwashTime);
                        equipment.setBackwaterInterval(BackwaterInterval);
                        equipment.setWWaterStall(wWaterStall);
                        equipment.setWashTime(WashTime);
                        equipment.setWMobileSignal(wMobileSignal);
                        equipment.setMachineType(MachineType);
                        equipment.setHaData(true);
                        equipmentDao.update(equipment);
                    } else if (messageJsonArray != null && messageJsonArray.getInt(2) == 0x22) {
                        funCode = 0x22;
                        int bussMode = messageJsonArray.getInt(1);//商业模式
                        week = messageJsonArray.getInt(4);
                        int openHour = messageJsonArray.getInt(5);
                        int openMin = messageJsonArray.getInt(6);
                        int closeHour = messageJsonArray.getInt(7);
                        int closeMin = messageJsonArray.getInt(8);
                        int openHour2 = messageJsonArray.getInt(9);
                        int openMin2 = messageJsonArray.getInt(10);
                        int closeHour2 = messageJsonArray.getInt(11);
                        int closeMin2 = messageJsonArray.getInt(12);
                        int openHour3 = messageJsonArray.getInt(13);
                        int openMin3 = messageJsonArray.getInt(14);
                        int closeHour3 = messageJsonArray.getInt(15);
                        int closeMin3 = messageJsonArray.getInt(16);
                        timerTask=timerTaskDao.findWeekTimerTask(macAddress,week);

                        if (timerTask==null){
                            timerTask=new TimerTask(macAddress,week,openHour,openMin,closeHour,closeMin,openHour2,openMin2,closeHour2,closeMin2,openHour3,openMin3,closeHour3,closeMin3);
                            timerTaskDao.insert(timerTask);
                        }else {
                            timerTask.setOpenHour(openHour);
                            timerTask.setOpenMin(openMin);
                            timerTask.setCloseHour(closeHour);
                            timerTask.setCloseMin(closeMin);

                            timerTask.setOpenHour2(openHour2);
                            timerTask.setOpenMin2(openMin2);
                            timerTask.setCloseHour2(closeHour2);
                            timerTask.setCloseMin2(closeMin2);

                            timerTask.setOpenHour3(openHour3);
                            timerTask.setOpenMin3(openMin3);
                            timerTask.setCloseHour3(closeHour3);
                            timerTask.setCloseMin3(closeMin3);
                            timerTaskDao.update(timerTask);
                        }

                    } else if (messageJsonArray != null && messageJsonArray.getInt(2) == 0x23) {
                        funCode = 0x23;
                        int bussMode = messageJsonArray.getInt(2);
                        week = messageJsonArray.getInt(4);//系统星期
                        int hour = messageJsonArray.getInt(5);//系统小时
                        int min = messageJsonArray.getInt(6);//系统分钟
                        int upTemp = messageJsonArray.getInt(7);//上温
                        int downTemp = messageJsonArray.getInt(8);//下温
                        int noWaterDS = messageJsonArray.getInt(9);//无水监测灵敏度
                        int inflowTime = messageJsonArray.getInt(10);//进水时间
                        int maxInflowTime = messageJsonArray.getInt(11);//最长进水时间
                        equipment.setBussinessmodule(bussMode);
                        equipment.setWeek(week);
                        equipment.setHour(hour);
                        equipment.setMin(min);
                        equipment.setUpTemp(upTemp);
                        equipment.setDownTemp(downTemp);
                        equipment.setNoWaterDS(noWaterDS);
                        equipment.setInflowTime(inflowTime);
                        equipment.setMaxInflowTime(maxInflowTime);
                        equipmentDao.update(equipment);

                    }
                }


                if (MainActivity.isRunning) {
                    Intent mqttIntent = new Intent("MainActivity");
                    mqttIntent.putExtra("msg", macAddress);
                    mqttIntent.putExtra("msg1", equipment);
                    sendBroadcast(mqttIntent);
                } else if (EqupmentActivity.isRunning) {
                    Intent mqttIntent = new Intent("EqupmentActivity");
                    mqttIntent.putExtra("msg", macAddress);
                    mqttIntent.putExtra("msg1", equipment);
                    sendBroadcast(mqttIntent);
                } else if (TimerTaskActivity.running) {
                    Intent mqttIntent = new Intent("TimerTaskActivity");
                    mqttIntent.putExtra("macAddress", macAddress);
                    if (0x23 == funCode) {
                        mqttIntent.putExtra("equipment", equipment);
                        mqttIntent.putExtra("funCode", funCode);
                    } else if (0x22 == funCode) {
                        mqttIntent.putExtra("week", week);
                        mqttIntent.putExtra("funCode", funCode);
                        mqttIntent.putExtra("timerTask",timerTask);
                    }

                    sendBroadcast(mqttIntent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    public List<String> getTopicNames() {
        List<String> list = new ArrayList<>();
        String onlineTopicName = "";
        String offlineTopicName = "";
        List<Equipment> equipments = equipmentDao.findAll();
        for (int i = 0; i < equipments.size(); i++) {
            Equipment equipment = equipments.get(i);
            String macAddress = equipment.getDeviceMac();
//            String macAddress="1234567890";
            onlineTopicName = "p99/wPurifier1/" + macAddress + "/transfer";
            offlineTopicName = "p99/wPurifier1/" + macAddress + "/lwt";
            list.add(onlineTopicName);
            list.add(offlineTopicName);
        }
//        String macAddress="1234567890";
//        onlineTopicName = "p99/wPurifier1/" + macAddress + "/transfer";
//        offlineTopicName = "p99/wPurifier1/" + macAddress + "/lwt";
//        list.add(onlineTopicName);
//        list.add(offlineTopicName);
        return list;
    }

    /***
     * 连接MQTT
     */
    public void connect() {
        try {
            if (client != null && !client.isConnected()) {
                client.connect(options);
            }
            List<String> topicNames = getTopicNames();
            new ConAsync().execute(topicNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重新连接MQTT
     */
    private void startReconnect() {

        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (!client.isConnected()) {
                    connect();
                }
            }
        }, 0 * 1000, 1 * 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 发送MQTT主题
     */

    public boolean publish(String topicName, int qos, String payload) {
        boolean flag = false;
        if (client != null && client.isConnected()) {
            try {
                MqttMessage message = new MqttMessage(payload.getBytes("utf-8"));
                qos = 1;
                //设置保留消息
                if (topicName.contains("friend")) {
                    message.setRetained(true);
                }
                if (topicName.contains("clockuniversal")) {
                    message.setRetained(true);
                }
                message.setQos(qos);
                client.publish(topicName, message);
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 订阅MQTT主题
     *
     * @param topicName
     * @param qos
     * @return
     */
    public boolean subscribe(String topicName, int qos) {
        boolean flag = false;
        if (client != null && client.isConnected()) {
            try {

                client.subscribe(topicName, 1);
                flag = true;
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }


    public class LocalBinder extends Binder {

        public MQService getService() {
            Log.i("Binder", "Binder");
            return MQService.this;
        }
    }


    /**
     * @param topicName
     */
    public void unsubscribe(String topicName) {
        if (client != null && client.isConnected()) {
            try {
                client.unsubscribe(topicName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    class ConAsync extends AsyncTask<List<String>, Void, Void> {

        @Override
        protected Void doInBackground(List<String>... lists) {
            try {

                List<String> topicNames = getTopicNames();
                boolean sss = client.isConnected();
                Log.i("sss", "-->" + sss);
                if (client.isConnected() && !topicNames.isEmpty()) {
                    for (String topicName : topicNames) {
                        if (!TextUtils.isEmpty(topicName)) {
                            client.subscribe(topicName, 1);
                            Log.i("client", "-->" + topicName);
                            Log.e("FFFDDDD", "doInBackground: 订阅-->" + topicName);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
