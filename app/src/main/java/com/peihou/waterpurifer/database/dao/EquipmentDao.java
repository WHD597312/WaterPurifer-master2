package com.peihou.waterpurifer.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.peihou.waterpurifer.pojo.Equipment;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "EQUIPMENT".
*/
public class EquipmentDao extends AbstractDao<Equipment, Long> {

    public static final String TABLENAME = "EQUIPMENT";

    /**
     * Properties of entity Equipment.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Type = new Property(1, int.class, "type", false, "TYPE");
        public final static Property TodayUse = new Property(2, int.class, "todayUse", false, "TODAY_USE");
        public final static Property HaData = new Property(3, boolean.class, "haData", false, "HA_DATA");
        public final static Property Name = new Property(4, String.class, "name", false, "NAME");
        public final static Property DeviceMac = new Property(5, String.class, "deviceMac", false, "DEVICE_MAC");
        public final static Property DeviceMCU = new Property(6, String.class, "deviceMCU", false, "DEVICE_MCU");
        public final static Property RoleFlag = new Property(7, int.class, "roleFlag", false, "ROLE_FLAG");
        public final static Property DeviceLeaseType = new Property(8, int.class, "deviceLeaseType", false, "DEVICE_LEASE_TYPE");
        public final static Property DevicePayType = new Property(9, int.class, "devicePayType", false, "DEVICE_PAY_TYPE");
        public final static Property DeviceSellerId = new Property(10, int.class, "deviceSellerId", false, "DEVICE_SELLER_ID");
        public final static Property DeviceFlag = new Property(11, int.class, "deviceFlag", false, "DEVICE_FLAG");
        public final static Property DeviceUserId = new Property(12, int.class, "deviceUserId", false, "DEVICE_USER_ID");
        public final static Property WPurifierState = new Property(13, int.class, "wPurifierState", false, "W_PURIFIER_STATE");
        public final static Property Bussinessmodule = new Property(14, int.class, "bussinessmodule", false, "BUSSINESSMODULE");
        public final static Property WPurifierfilter = new Property(15, int.class, "wPurifierfilter", false, "W_PURIFIERFILTER");
        public final static Property WPurifierfilter1 = new Property(16, int.class, "wPurifierfilter1", false, "W_PURIFIERFILTER1");
        public final static Property WPurifierfilter2 = new Property(17, int.class, "wPurifierfilter2", false, "W_PURIFIERFILTER2");
        public final static Property WPurifierfilter3 = new Property(18, int.class, "wPurifierfilter3", false, "W_PURIFIERFILTER3");
        public final static Property WPurifierfilter4 = new Property(19, int.class, "wPurifierfilter4", false, "W_PURIFIERFILTER4");
        public final static Property WPurifierfilter5 = new Property(20, int.class, "wPurifierfilter5", false, "W_PURIFIERFILTER5");
        public final static Property WTrueFlowmeter = new Property(21, int.class, "wTrueFlowmeter", false, "W_TRUE_FLOWMETER");
        public final static Property WPurifierPrimaryQuqlity = new Property(22, int.class, "wPurifierPrimaryQuqlity", false, "W_PURIFIER_PRIMARY_QUQLITY");
        public final static Property FlowmeterWarm = new Property(23, int.class, "FlowmeterWarm", false, "FLOWMETER_WARM");
        public final static Property WPurifierOutQuqlity = new Property(24, int.class, "wPurifierOutQuqlity", false, "W_PURIFIER_OUT_QUQLITY");
        public final static Property WTotalProductionTime = new Property(25, int.class, "wTotalProductionTime", false, "W_TOTAL_PRODUCTION_TIME");
        public final static Property WContinuiProductionTime = new Property(26, int.class, "wContinuiProductionTime", false, "W_CONTINUI_PRODUCTION_TIME");
        public final static Property WWaterStall = new Property(27, int.class, "wWaterStall", false, "W_WATER_STALL");
        public final static Property WMobileSignal = new Property(28, int.class, "wMobileSignal", false, "W_MOBILE_SIGNAL");
        public final static Property IsOpen = new Property(29, int.class, "IsOpen", false, "IS_OPEN");
        public final static Property HavaWater = new Property(30, int.class, "HavaWater", false, "HAVA_WATER");
        public final static Property WaterWash = new Property(31, int.class, "WaterWash", false, "WATER_WASH");
        public final static Property MakeWater = new Property(32, int.class, "MakeWater", false, "MAKE_WATER");
        public final static Property IsFull = new Property(33, int.class, "IsFull", false, "IS_FULL");
        public final static Property Repair = new Property(34, int.class, "Repair", false, "REPAIR");
        public final static Property IsLeakage = new Property(35, int.class, "IsLeakage", false, "IS_LEAKAGE");
        public final static Property Alarming = new Property(36, int.class, "Alarming", false, "ALARMING");
        public final static Property Warming = new Property(37, int.class, "Warming", false, "WARMING");
        public final static Property AlarmState = new Property(38, int.class, "AlarmState", false, "ALARM_STATE");
        public final static Property AlarmIsLeakage = new Property(39, int.class, "AlarmIsLeakage", false, "ALARM_IS_LEAKAGE");
        public final static Property ContinuProduction = new Property(40, int.class, "ContinuProduction", false, "CONTINU_PRODUCTION");
        public final static Property AlarmFlowmeter = new Property(41, int.class, "AlarmFlowmeter", false, "ALARM_FLOWMETER");
        public final static Property AlarmWash = new Property(42, int.class, "AlarmWash", false, "ALARM_WASH");
        public final static Property RechargeTime = new Property(43, int.class, "RechargeTime", false, "RECHARGE_TIME");
        public final static Property RechargeFlow = new Property(44, int.class, "RechargeFlow", false, "RECHARGE_FLOW");
        public final static Property BackwaterInterval = new Property(45, int.class, "BackwaterInterval", false, "BACKWATER_INTERVAL");
        public final static Property BackwashTime = new Property(46, int.class, "BackwashTime", false, "BACKWASH_TIME");
        public final static Property BackwashInterval = new Property(47, int.class, "BackwashInterval", false, "BACKWASH_INTERVAL");
        public final static Property MachineType = new Property(48, int.class, "MachineType", false, "MACHINE_TYPE");
        public final static Property WashTime = new Property(49, int.class, "WashTime", false, "WASH_TIME");
        public final static Property IsReset = new Property(50, int.class, "isReset", false, "IS_RESET");
        public final static Property IsReset2 = new Property(51, int.class, "isReset2", false, "IS_RESET2");
        public final static Property Gear = new Property(52, int.class, "gear", false, "GEAR");
        public final static Property Week = new Property(53, int.class, "week", false, "WEEK");
        public final static Property Hour = new Property(54, int.class, "hour", false, "HOUR");
        public final static Property Min = new Property(55, int.class, "min", false, "MIN");
        public final static Property UpTemp = new Property(56, int.class, "upTemp", false, "UP_TEMP");
        public final static Property DownTemp = new Property(57, int.class, "downTemp", false, "DOWN_TEMP");
        public final static Property NoWaterDS = new Property(58, int.class, "noWaterDS", false, "NO_WATER_DS");
        public final static Property InflowTime = new Property(59, int.class, "inflowTime", false, "INFLOW_TIME");
        public final static Property MaxInflowTime = new Property(60, int.class, "maxInflowTime", false, "MAX_INFLOW_TIME");
    }


    public EquipmentDao(DaoConfig config) {
        super(config);
    }
    
    public EquipmentDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"EQUIPMENT\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"TYPE\" INTEGER NOT NULL ," + // 1: type
                "\"TODAY_USE\" INTEGER NOT NULL ," + // 2: todayUse
                "\"HA_DATA\" INTEGER NOT NULL ," + // 3: haData
                "\"NAME\" TEXT," + // 4: name
                "\"DEVICE_MAC\" TEXT," + // 5: deviceMac
                "\"DEVICE_MCU\" TEXT," + // 6: deviceMCU
                "\"ROLE_FLAG\" INTEGER NOT NULL ," + // 7: roleFlag
                "\"DEVICE_LEASE_TYPE\" INTEGER NOT NULL ," + // 8: deviceLeaseType
                "\"DEVICE_PAY_TYPE\" INTEGER NOT NULL ," + // 9: devicePayType
                "\"DEVICE_SELLER_ID\" INTEGER NOT NULL ," + // 10: deviceSellerId
                "\"DEVICE_FLAG\" INTEGER NOT NULL ," + // 11: deviceFlag
                "\"DEVICE_USER_ID\" INTEGER NOT NULL ," + // 12: deviceUserId
                "\"W_PURIFIER_STATE\" INTEGER NOT NULL ," + // 13: wPurifierState
                "\"BUSSINESSMODULE\" INTEGER NOT NULL ," + // 14: bussinessmodule
                "\"W_PURIFIERFILTER\" INTEGER NOT NULL ," + // 15: wPurifierfilter
                "\"W_PURIFIERFILTER1\" INTEGER NOT NULL ," + // 16: wPurifierfilter1
                "\"W_PURIFIERFILTER2\" INTEGER NOT NULL ," + // 17: wPurifierfilter2
                "\"W_PURIFIERFILTER3\" INTEGER NOT NULL ," + // 18: wPurifierfilter3
                "\"W_PURIFIERFILTER4\" INTEGER NOT NULL ," + // 19: wPurifierfilter4
                "\"W_PURIFIERFILTER5\" INTEGER NOT NULL ," + // 20: wPurifierfilter5
                "\"W_TRUE_FLOWMETER\" INTEGER NOT NULL ," + // 21: wTrueFlowmeter
                "\"W_PURIFIER_PRIMARY_QUQLITY\" INTEGER NOT NULL ," + // 22: wPurifierPrimaryQuqlity
                "\"FLOWMETER_WARM\" INTEGER NOT NULL ," + // 23: FlowmeterWarm
                "\"W_PURIFIER_OUT_QUQLITY\" INTEGER NOT NULL ," + // 24: wPurifierOutQuqlity
                "\"W_TOTAL_PRODUCTION_TIME\" INTEGER NOT NULL ," + // 25: wTotalProductionTime
                "\"W_CONTINUI_PRODUCTION_TIME\" INTEGER NOT NULL ," + // 26: wContinuiProductionTime
                "\"W_WATER_STALL\" INTEGER NOT NULL ," + // 27: wWaterStall
                "\"W_MOBILE_SIGNAL\" INTEGER NOT NULL ," + // 28: wMobileSignal
                "\"IS_OPEN\" INTEGER NOT NULL ," + // 29: IsOpen
                "\"HAVA_WATER\" INTEGER NOT NULL ," + // 30: HavaWater
                "\"WATER_WASH\" INTEGER NOT NULL ," + // 31: WaterWash
                "\"MAKE_WATER\" INTEGER NOT NULL ," + // 32: MakeWater
                "\"IS_FULL\" INTEGER NOT NULL ," + // 33: IsFull
                "\"REPAIR\" INTEGER NOT NULL ," + // 34: Repair
                "\"IS_LEAKAGE\" INTEGER NOT NULL ," + // 35: IsLeakage
                "\"ALARMING\" INTEGER NOT NULL ," + // 36: Alarming
                "\"WARMING\" INTEGER NOT NULL ," + // 37: Warming
                "\"ALARM_STATE\" INTEGER NOT NULL ," + // 38: AlarmState
                "\"ALARM_IS_LEAKAGE\" INTEGER NOT NULL ," + // 39: AlarmIsLeakage
                "\"CONTINU_PRODUCTION\" INTEGER NOT NULL ," + // 40: ContinuProduction
                "\"ALARM_FLOWMETER\" INTEGER NOT NULL ," + // 41: AlarmFlowmeter
                "\"ALARM_WASH\" INTEGER NOT NULL ," + // 42: AlarmWash
                "\"RECHARGE_TIME\" INTEGER NOT NULL ," + // 43: RechargeTime
                "\"RECHARGE_FLOW\" INTEGER NOT NULL ," + // 44: RechargeFlow
                "\"BACKWATER_INTERVAL\" INTEGER NOT NULL ," + // 45: BackwaterInterval
                "\"BACKWASH_TIME\" INTEGER NOT NULL ," + // 46: BackwashTime
                "\"BACKWASH_INTERVAL\" INTEGER NOT NULL ," + // 47: BackwashInterval
                "\"MACHINE_TYPE\" INTEGER NOT NULL ," + // 48: MachineType
                "\"WASH_TIME\" INTEGER NOT NULL ," + // 49: WashTime
                "\"IS_RESET\" INTEGER NOT NULL ," + // 50: isReset
                "\"IS_RESET2\" INTEGER NOT NULL ," + // 51: isReset2
                "\"GEAR\" INTEGER NOT NULL ," + // 52: gear
                "\"WEEK\" INTEGER NOT NULL ," + // 53: week
                "\"HOUR\" INTEGER NOT NULL ," + // 54: hour
                "\"MIN\" INTEGER NOT NULL ," + // 55: min
                "\"UP_TEMP\" INTEGER NOT NULL ," + // 56: upTemp
                "\"DOWN_TEMP\" INTEGER NOT NULL ," + // 57: downTemp
                "\"NO_WATER_DS\" INTEGER NOT NULL ," + // 58: noWaterDS
                "\"INFLOW_TIME\" INTEGER NOT NULL ," + // 59: inflowTime
                "\"MAX_INFLOW_TIME\" INTEGER NOT NULL );"); // 60: maxInflowTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"EQUIPMENT\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Equipment entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getType());
        stmt.bindLong(3, entity.getTodayUse());
        stmt.bindLong(4, entity.getHaData() ? 1L: 0L);
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
 
        String deviceMac = entity.getDeviceMac();
        if (deviceMac != null) {
            stmt.bindString(6, deviceMac);
        }
 
        String deviceMCU = entity.getDeviceMCU();
        if (deviceMCU != null) {
            stmt.bindString(7, deviceMCU);
        }
        stmt.bindLong(8, entity.getRoleFlag());
        stmt.bindLong(9, entity.getDeviceLeaseType());
        stmt.bindLong(10, entity.getDevicePayType());
        stmt.bindLong(11, entity.getDeviceSellerId());
        stmt.bindLong(12, entity.getDeviceFlag());
        stmt.bindLong(13, entity.getDeviceUserId());
        stmt.bindLong(14, entity.getWPurifierState());
        stmt.bindLong(15, entity.getBussinessmodule());
        stmt.bindLong(16, entity.getWPurifierfilter());
        stmt.bindLong(17, entity.getWPurifierfilter1());
        stmt.bindLong(18, entity.getWPurifierfilter2());
        stmt.bindLong(19, entity.getWPurifierfilter3());
        stmt.bindLong(20, entity.getWPurifierfilter4());
        stmt.bindLong(21, entity.getWPurifierfilter5());
        stmt.bindLong(22, entity.getWTrueFlowmeter());
        stmt.bindLong(23, entity.getWPurifierPrimaryQuqlity());
        stmt.bindLong(24, entity.getFlowmeterWarm());
        stmt.bindLong(25, entity.getWPurifierOutQuqlity());
        stmt.bindLong(26, entity.getWTotalProductionTime());
        stmt.bindLong(27, entity.getWContinuiProductionTime());
        stmt.bindLong(28, entity.getWWaterStall());
        stmt.bindLong(29, entity.getWMobileSignal());
        stmt.bindLong(30, entity.getIsOpen());
        stmt.bindLong(31, entity.getHavaWater());
        stmt.bindLong(32, entity.getWaterWash());
        stmt.bindLong(33, entity.getMakeWater());
        stmt.bindLong(34, entity.getIsFull());
        stmt.bindLong(35, entity.getRepair());
        stmt.bindLong(36, entity.getIsLeakage());
        stmt.bindLong(37, entity.getAlarming());
        stmt.bindLong(38, entity.getWarming());
        stmt.bindLong(39, entity.getAlarmState());
        stmt.bindLong(40, entity.getAlarmIsLeakage());
        stmt.bindLong(41, entity.getContinuProduction());
        stmt.bindLong(42, entity.getAlarmFlowmeter());
        stmt.bindLong(43, entity.getAlarmWash());
        stmt.bindLong(44, entity.getRechargeTime());
        stmt.bindLong(45, entity.getRechargeFlow());
        stmt.bindLong(46, entity.getBackwaterInterval());
        stmt.bindLong(47, entity.getBackwashTime());
        stmt.bindLong(48, entity.getBackwashInterval());
        stmt.bindLong(49, entity.getMachineType());
        stmt.bindLong(50, entity.getWashTime());
        stmt.bindLong(51, entity.getIsReset());
        stmt.bindLong(52, entity.getIsReset2());
        stmt.bindLong(53, entity.getGear());
        stmt.bindLong(54, entity.getWeek());
        stmt.bindLong(55, entity.getHour());
        stmt.bindLong(56, entity.getMin());
        stmt.bindLong(57, entity.getUpTemp());
        stmt.bindLong(58, entity.getDownTemp());
        stmt.bindLong(59, entity.getNoWaterDS());
        stmt.bindLong(60, entity.getInflowTime());
        stmt.bindLong(61, entity.getMaxInflowTime());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Equipment entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getType());
        stmt.bindLong(3, entity.getTodayUse());
        stmt.bindLong(4, entity.getHaData() ? 1L: 0L);
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
 
        String deviceMac = entity.getDeviceMac();
        if (deviceMac != null) {
            stmt.bindString(6, deviceMac);
        }
 
        String deviceMCU = entity.getDeviceMCU();
        if (deviceMCU != null) {
            stmt.bindString(7, deviceMCU);
        }
        stmt.bindLong(8, entity.getRoleFlag());
        stmt.bindLong(9, entity.getDeviceLeaseType());
        stmt.bindLong(10, entity.getDevicePayType());
        stmt.bindLong(11, entity.getDeviceSellerId());
        stmt.bindLong(12, entity.getDeviceFlag());
        stmt.bindLong(13, entity.getDeviceUserId());
        stmt.bindLong(14, entity.getWPurifierState());
        stmt.bindLong(15, entity.getBussinessmodule());
        stmt.bindLong(16, entity.getWPurifierfilter());
        stmt.bindLong(17, entity.getWPurifierfilter1());
        stmt.bindLong(18, entity.getWPurifierfilter2());
        stmt.bindLong(19, entity.getWPurifierfilter3());
        stmt.bindLong(20, entity.getWPurifierfilter4());
        stmt.bindLong(21, entity.getWPurifierfilter5());
        stmt.bindLong(22, entity.getWTrueFlowmeter());
        stmt.bindLong(23, entity.getWPurifierPrimaryQuqlity());
        stmt.bindLong(24, entity.getFlowmeterWarm());
        stmt.bindLong(25, entity.getWPurifierOutQuqlity());
        stmt.bindLong(26, entity.getWTotalProductionTime());
        stmt.bindLong(27, entity.getWContinuiProductionTime());
        stmt.bindLong(28, entity.getWWaterStall());
        stmt.bindLong(29, entity.getWMobileSignal());
        stmt.bindLong(30, entity.getIsOpen());
        stmt.bindLong(31, entity.getHavaWater());
        stmt.bindLong(32, entity.getWaterWash());
        stmt.bindLong(33, entity.getMakeWater());
        stmt.bindLong(34, entity.getIsFull());
        stmt.bindLong(35, entity.getRepair());
        stmt.bindLong(36, entity.getIsLeakage());
        stmt.bindLong(37, entity.getAlarming());
        stmt.bindLong(38, entity.getWarming());
        stmt.bindLong(39, entity.getAlarmState());
        stmt.bindLong(40, entity.getAlarmIsLeakage());
        stmt.bindLong(41, entity.getContinuProduction());
        stmt.bindLong(42, entity.getAlarmFlowmeter());
        stmt.bindLong(43, entity.getAlarmWash());
        stmt.bindLong(44, entity.getRechargeTime());
        stmt.bindLong(45, entity.getRechargeFlow());
        stmt.bindLong(46, entity.getBackwaterInterval());
        stmt.bindLong(47, entity.getBackwashTime());
        stmt.bindLong(48, entity.getBackwashInterval());
        stmt.bindLong(49, entity.getMachineType());
        stmt.bindLong(50, entity.getWashTime());
        stmt.bindLong(51, entity.getIsReset());
        stmt.bindLong(52, entity.getIsReset2());
        stmt.bindLong(53, entity.getGear());
        stmt.bindLong(54, entity.getWeek());
        stmt.bindLong(55, entity.getHour());
        stmt.bindLong(56, entity.getMin());
        stmt.bindLong(57, entity.getUpTemp());
        stmt.bindLong(58, entity.getDownTemp());
        stmt.bindLong(59, entity.getNoWaterDS());
        stmt.bindLong(60, entity.getInflowTime());
        stmt.bindLong(61, entity.getMaxInflowTime());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Equipment readEntity(Cursor cursor, int offset) {
        Equipment entity = new Equipment( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // type
            cursor.getInt(offset + 2), // todayUse
            cursor.getShort(offset + 3) != 0, // haData
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // name
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // deviceMac
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // deviceMCU
            cursor.getInt(offset + 7), // roleFlag
            cursor.getInt(offset + 8), // deviceLeaseType
            cursor.getInt(offset + 9), // devicePayType
            cursor.getInt(offset + 10), // deviceSellerId
            cursor.getInt(offset + 11), // deviceFlag
            cursor.getInt(offset + 12), // deviceUserId
            cursor.getInt(offset + 13), // wPurifierState
            cursor.getInt(offset + 14), // bussinessmodule
            cursor.getInt(offset + 15), // wPurifierfilter
            cursor.getInt(offset + 16), // wPurifierfilter1
            cursor.getInt(offset + 17), // wPurifierfilter2
            cursor.getInt(offset + 18), // wPurifierfilter3
            cursor.getInt(offset + 19), // wPurifierfilter4
            cursor.getInt(offset + 20), // wPurifierfilter5
            cursor.getInt(offset + 21), // wTrueFlowmeter
            cursor.getInt(offset + 22), // wPurifierPrimaryQuqlity
            cursor.getInt(offset + 23), // FlowmeterWarm
            cursor.getInt(offset + 24), // wPurifierOutQuqlity
            cursor.getInt(offset + 25), // wTotalProductionTime
            cursor.getInt(offset + 26), // wContinuiProductionTime
            cursor.getInt(offset + 27), // wWaterStall
            cursor.getInt(offset + 28), // wMobileSignal
            cursor.getInt(offset + 29), // IsOpen
            cursor.getInt(offset + 30), // HavaWater
            cursor.getInt(offset + 31), // WaterWash
            cursor.getInt(offset + 32), // MakeWater
            cursor.getInt(offset + 33), // IsFull
            cursor.getInt(offset + 34), // Repair
            cursor.getInt(offset + 35), // IsLeakage
            cursor.getInt(offset + 36), // Alarming
            cursor.getInt(offset + 37), // Warming
            cursor.getInt(offset + 38), // AlarmState
            cursor.getInt(offset + 39), // AlarmIsLeakage
            cursor.getInt(offset + 40), // ContinuProduction
            cursor.getInt(offset + 41), // AlarmFlowmeter
            cursor.getInt(offset + 42), // AlarmWash
            cursor.getInt(offset + 43), // RechargeTime
            cursor.getInt(offset + 44), // RechargeFlow
            cursor.getInt(offset + 45), // BackwaterInterval
            cursor.getInt(offset + 46), // BackwashTime
            cursor.getInt(offset + 47), // BackwashInterval
            cursor.getInt(offset + 48), // MachineType
            cursor.getInt(offset + 49), // WashTime
            cursor.getInt(offset + 50), // isReset
            cursor.getInt(offset + 51), // isReset2
            cursor.getInt(offset + 52), // gear
            cursor.getInt(offset + 53), // week
            cursor.getInt(offset + 54), // hour
            cursor.getInt(offset + 55), // min
            cursor.getInt(offset + 56), // upTemp
            cursor.getInt(offset + 57), // downTemp
            cursor.getInt(offset + 58), // noWaterDS
            cursor.getInt(offset + 59), // inflowTime
            cursor.getInt(offset + 60) // maxInflowTime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Equipment entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setType(cursor.getInt(offset + 1));
        entity.setTodayUse(cursor.getInt(offset + 2));
        entity.setHaData(cursor.getShort(offset + 3) != 0);
        entity.setName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setDeviceMac(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setDeviceMCU(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setRoleFlag(cursor.getInt(offset + 7));
        entity.setDeviceLeaseType(cursor.getInt(offset + 8));
        entity.setDevicePayType(cursor.getInt(offset + 9));
        entity.setDeviceSellerId(cursor.getInt(offset + 10));
        entity.setDeviceFlag(cursor.getInt(offset + 11));
        entity.setDeviceUserId(cursor.getInt(offset + 12));
        entity.setWPurifierState(cursor.getInt(offset + 13));
        entity.setBussinessmodule(cursor.getInt(offset + 14));
        entity.setWPurifierfilter(cursor.getInt(offset + 15));
        entity.setWPurifierfilter1(cursor.getInt(offset + 16));
        entity.setWPurifierfilter2(cursor.getInt(offset + 17));
        entity.setWPurifierfilter3(cursor.getInt(offset + 18));
        entity.setWPurifierfilter4(cursor.getInt(offset + 19));
        entity.setWPurifierfilter5(cursor.getInt(offset + 20));
        entity.setWTrueFlowmeter(cursor.getInt(offset + 21));
        entity.setWPurifierPrimaryQuqlity(cursor.getInt(offset + 22));
        entity.setFlowmeterWarm(cursor.getInt(offset + 23));
        entity.setWPurifierOutQuqlity(cursor.getInt(offset + 24));
        entity.setWTotalProductionTime(cursor.getInt(offset + 25));
        entity.setWContinuiProductionTime(cursor.getInt(offset + 26));
        entity.setWWaterStall(cursor.getInt(offset + 27));
        entity.setWMobileSignal(cursor.getInt(offset + 28));
        entity.setIsOpen(cursor.getInt(offset + 29));
        entity.setHavaWater(cursor.getInt(offset + 30));
        entity.setWaterWash(cursor.getInt(offset + 31));
        entity.setMakeWater(cursor.getInt(offset + 32));
        entity.setIsFull(cursor.getInt(offset + 33));
        entity.setRepair(cursor.getInt(offset + 34));
        entity.setIsLeakage(cursor.getInt(offset + 35));
        entity.setAlarming(cursor.getInt(offset + 36));
        entity.setWarming(cursor.getInt(offset + 37));
        entity.setAlarmState(cursor.getInt(offset + 38));
        entity.setAlarmIsLeakage(cursor.getInt(offset + 39));
        entity.setContinuProduction(cursor.getInt(offset + 40));
        entity.setAlarmFlowmeter(cursor.getInt(offset + 41));
        entity.setAlarmWash(cursor.getInt(offset + 42));
        entity.setRechargeTime(cursor.getInt(offset + 43));
        entity.setRechargeFlow(cursor.getInt(offset + 44));
        entity.setBackwaterInterval(cursor.getInt(offset + 45));
        entity.setBackwashTime(cursor.getInt(offset + 46));
        entity.setBackwashInterval(cursor.getInt(offset + 47));
        entity.setMachineType(cursor.getInt(offset + 48));
        entity.setWashTime(cursor.getInt(offset + 49));
        entity.setIsReset(cursor.getInt(offset + 50));
        entity.setIsReset2(cursor.getInt(offset + 51));
        entity.setGear(cursor.getInt(offset + 52));
        entity.setWeek(cursor.getInt(offset + 53));
        entity.setHour(cursor.getInt(offset + 54));
        entity.setMin(cursor.getInt(offset + 55));
        entity.setUpTemp(cursor.getInt(offset + 56));
        entity.setDownTemp(cursor.getInt(offset + 57));
        entity.setNoWaterDS(cursor.getInt(offset + 58));
        entity.setInflowTime(cursor.getInt(offset + 59));
        entity.setMaxInflowTime(cursor.getInt(offset + 60));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Equipment entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Equipment entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Equipment entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
