package com.peihou.waterpurifer.database.dao.daoImp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.peihou.waterpurifer.database.dao.DBManager;
import com.peihou.waterpurifer.database.dao.DaoMaster;
import com.peihou.waterpurifer.database.dao.DaoSession;
import com.peihou.waterpurifer.database.dao.EquipmentDao;
import com.peihou.waterpurifer.database.dao.TimerTaskDao;
import com.peihou.waterpurifer.pojo.TimerTask;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TimerTaskImpl {
    private Context context;
    private SQLiteDatabase db;
    private DaoMaster master;
    private TimerTaskDao timerTaskDao;
    private DaoSession session;
    public TimerTaskImpl(Context context) {
        this.context = context;
        db= DBManager.getInstance(context).getWritableDasebase();
        master=new DaoMaster(db);
        session=master.newSession();
        timerTaskDao = session.getTimerTaskDao();
    }
    public void insert(TimerTask timerTask){
        timerTaskDao.insert(timerTask);
    }
    public void update(TimerTask timerTask){
        timerTaskDao.update(timerTask);
    }
    public void delete(TimerTask timerTask){
        timerTaskDao.delete(timerTask);
    }
    public TimerTask findWeekTimerTask(String macAddress,int week){

        WhereCondition whereCondition=timerTaskDao.queryBuilder().and(TimerTaskDao.Properties.MacAddress.eq(macAddress),TimerTaskDao.Properties.Week.eq(week));
        return timerTaskDao.queryBuilder().where(whereCondition).unique();
//        if (timerTasks!=null && !timerTasks.isEmpty()){
//            Log.i("findWeekTimerTask","-->"+timerTasks);
//            timerTask=timerTasks.get(0);
//        }
//        return timerTask;
    }

    public void addTimerTasks(LinkedList<TimerTask> timerTasks){
        for(TimerTask timerTask:timerTasks){
            TimerTask timerTask2=findWeekTimerTask(timerTask.getMacAddress(),timerTask.getWeek());
            if (timerTask2!=null){
                timerTasks.remove(timerTask);
            }
        }
        timerTaskDao.insertInTx(timerTasks);
    }

    public List<TimerTask> find7DayTimerTask(String macAddress){
        return timerTaskDao.queryBuilder().where(TimerTaskDao.Properties.MacAddress.eq(macAddress)).list();
    }

}
