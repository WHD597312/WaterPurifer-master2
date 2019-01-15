package com.peihou.waterpurifer.database.dao.daoImp;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.peihou.waterpurifer.database.dao.DBManager;
import com.peihou.waterpurifer.database.dao.DaoMaster;
import com.peihou.waterpurifer.database.dao.DaoSession;
import com.peihou.waterpurifer.database.dao.EquipmentDao;
import com.peihou.waterpurifer.pojo.Equipment;

import java.util.List;


public class EquipmentImpl {
    private Context context;
    private SQLiteDatabase db;
    private DaoMaster master;
    private EquipmentDao equipmentDao;
    private DaoSession session;
    public EquipmentImpl(Context context) {
        this.context = context;
        db= DBManager.getInstance(context).getWritableDasebase();
        master=new DaoMaster(db);
        session=master.newSession();
        equipmentDao = session.getEquipmentDao();
    }

    /**
     * 添加信息
     * @param equipment
     */
    public void insert(Equipment equipment){
        equipmentDao.insert(equipment);
    }

    /**
     * 删除信息
     * @param equipment
     */
    public void delete(Equipment equipment){
        equipmentDao.delete(equipment);
    }

    /**
     * 更新信息
     * @param equipment
     */
    public void update(Equipment equipment){
        equipmentDao.update(equipment);
    }

    public Equipment findById(long Id){
        return equipmentDao.load(Id);
    }
    public List<Equipment> findAll(){
        return equipmentDao.loadAll();
    }

    public void  deleteAll(){
        equipmentDao.deleteAll();
    }

    /**
     * 根据macAddress来查询设备列表
     * @param macAddress
     * @return
     */
    public List<Equipment> findDeviceByMacAddress(String macAddress){
        return equipmentDao.queryBuilder().where(EquipmentDao.Properties.DeviceMac.eq(macAddress)).list();
    }

    /**
     * 根据macAddress来查询设备
     * @param macAddress
     * @return
     */
    public Equipment findDeviceByMacAddress2(String macAddress){
        return equipmentDao.queryBuilder().where(EquipmentDao.Properties.DeviceMac.eq(macAddress)).unique();
    }

    /**
     * 根据macAddress来查询分享设备
     * @param roleFlag
     * @return
     */
    public List<Equipment> findDeviceByRoleFlag(int roleFlag){
        return equipmentDao.queryBuilder().where(EquipmentDao.Properties.RoleFlag.eq(roleFlag)).list();
    }

}
