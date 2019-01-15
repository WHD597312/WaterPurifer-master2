package com.peihou.waterpurifer.database.dao.daoImp;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.peihou.waterpurifer.database.dao.DBManager;
import com.peihou.waterpurifer.database.dao.DaoMaster;
import com.peihou.waterpurifer.database.dao.DaoSession;
import com.peihou.waterpurifer.database.dao.EquipmentDao;
import com.peihou.waterpurifer.database.dao.PackagesDao;
import com.peihou.waterpurifer.pojo.Equipment;
import com.peihou.waterpurifer.pojo.Packages;

import java.util.List;


public class PackagesImpl {
    private Context context;
    private SQLiteDatabase db;
    private DaoMaster master;
    private PackagesDao packagesDao;
    private DaoSession session;
    public PackagesImpl(Context context) {
        this.context = context;
        db= DBManager.getInstance(context).getWritableDasebase();
        master=new DaoMaster(db);
        session=master.newSession();
        packagesDao = session.getPackagesDao();
    }

    /**
     * 添加信息
     * @param packages
     */
    public void insert(Packages packages){
        packagesDao.insert(packages);
    }

    /**
     * 删除信息
     * @param packages
     */
    public void delete(Packages packages){
        packagesDao.delete(packages);
    }

    /**
     * 更新信息
     * @param packages
     */
    public void update(Packages packages){
        packagesDao.update(packages);
    }

    public Packages findById(long Id){
        return packagesDao.load(Id);
    }
    public List<Packages> findAll(){
        return packagesDao.loadAll();
    }

    public void  deleteAll(){
        packagesDao.deleteAll();
    }





}
