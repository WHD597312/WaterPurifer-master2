package com.peihou.waterpurifer.database.dao.daoImp;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.peihou.waterpurifer.database.dao.DBManager;
import com.peihou.waterpurifer.database.dao.DaoMaster;
import com.peihou.waterpurifer.database.dao.DaoSession;
import com.peihou.waterpurifer.database.dao.EquipmentDao;
import com.peihou.waterpurifer.database.dao.PackagesDao;
import com.peihou.waterpurifer.database.dao.PackageschildDao;
import com.peihou.waterpurifer.pojo.Packages;
import com.peihou.waterpurifer.pojo.Packageschild;

import java.util.List;


public class PackageschildImpl {
    private Context context;
    private SQLiteDatabase db;
    private DaoMaster master;
    private PackageschildDao packageschildDao;
    private DaoSession session;
    public PackageschildImpl(Context context) {
        this.context = context;
        db= DBManager.getInstance(context).getWritableDasebase();
        master=new DaoMaster(db);
        session=master.newSession();
        packageschildDao = session.getPackageschildDao();
    }

    /**
     * 添加信息
     * @param packageschild
     */
    public void insert(Packageschild packageschild){
        packageschildDao.insert(packageschild);
    }

    /**
     * 删除信息
     * @param packageschild
     */
    public void delete(Packageschild packageschild){
        packageschildDao.delete(packageschild);
    }

    /**
     * 更新信息
     * @param packageschild
     */
    public void update(Packageschild packageschild){
        packageschildDao.update(packageschild);
    }

    public Packageschild findById(long Id){
        return packageschildDao.load(Id);
    }
    public List<Packageschild> findAll(){
        return packageschildDao.loadAll();
    }

    public void  deleteAll(){
        packageschildDao.deleteAll();
    }
    public List<Packageschild> findByParentId(long parentId){
        return packageschildDao.queryBuilder().where(PackageschildDao.Properties.ParentId.eq(parentId)).list();
    }




}
