package com.peihou.waterpurifer.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

@Entity
public class TimerTask implements Serializable {
    @Id(autoincrement = true)
    Long id;

    String macAddress;
    int bussMode;//商业模式
    int funCode;//功能码
    int week;


    

    int openHour;
    int openMin;
    int closeHour;
    int closeMin;
    int openHour2;
    int openMin2;
    int closeHour2;
    int closeMin2;
    int openHour3;
    int openMin3;
    int closeHour3;
    int closeMin3;

    
    public TimerTask(String macAddress, int week, int openHour, int openMin, int closeHour, int closeMin, int openHour2, int openMin2, int closeHour2, int closeMin2, int openHour3, int openMin3, int closeHour3, int closeMin3) {
        this.macAddress = macAddress;
        this.week = week;
        this.openHour = openHour;
        this.openMin = openMin;
        this.closeHour = closeHour;
        this.closeMin = closeMin;
        this.openHour2 = openHour2;
        this.openMin2 = openMin2;
        this.closeHour2 = closeHour2;
        this.closeMin2 = closeMin2;
        this.openHour3 = openHour3;
        this.openMin3 = openMin3;
        this.closeHour3 = closeHour3;
        this.closeMin3 = closeMin3;
    }
    @Generated(hash = 1662181069)
    public TimerTask(Long id, String macAddress, int bussMode, int funCode, int week, int openHour, int openMin, int closeHour, int closeMin, int openHour2, int openMin2, int closeHour2, int closeMin2, int openHour3, int openMin3,
            int closeHour3, int closeMin3) {
        this.id = id;
        this.macAddress = macAddress;
        this.bussMode = bussMode;
        this.funCode = funCode;
        this.week = week;
        this.openHour = openHour;
        this.openMin = openMin;
        this.closeHour = closeHour;
        this.closeMin = closeMin;
        this.openHour2 = openHour2;
        this.openMin2 = openMin2;
        this.closeHour2 = closeHour2;
        this.closeMin2 = closeMin2;
        this.openHour3 = openHour3;
        this.openMin3 = openMin3;
        this.closeHour3 = closeHour3;
        this.closeMin3 = closeMin3;
    }
    @Generated(hash = 589238981)
    public TimerTask() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int getBussMode() {
        return bussMode;
    }

    public void setBussMode(int bussMode) {
        this.bussMode = bussMode;
    }

    public int getFunCode() {
        return funCode;
    }

    public void setFunCode(int funCode) {
        this.funCode = funCode;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getOpenHour() {
        return openHour;
    }

    public void setOpenHour(int openHour) {
        this.openHour = openHour;
    }

    public int getOpenMin() {
        return openMin;
    }

    public void setOpenMin(int openMin) {
        this.openMin = openMin;
    }

    public int getCloseHour() {
        return closeHour;
    }

    public void setCloseHour(int closeHour) {
        this.closeHour = closeHour;
    }

    public int getCloseMin() {
        return closeMin;
    }

    public void setCloseMin(int closeMin) {
        this.closeMin = closeMin;
    }

    public int getOpenHour2() {
        return openHour2;
    }

    public void setOpenHour2(int openHour2) {
        this.openHour2 = openHour2;
    }

    public int getOpenMin2() {
        return openMin2;
    }

    public void setOpenMin2(int openMin2) {
        this.openMin2 = openMin2;
    }

    public int getCloseHour2() {
        return closeHour2;
    }

    public void setCloseHour2(int closeHour2) {
        this.closeHour2 = closeHour2;
    }

    public int getCloseMin2() {
        return closeMin2;
    }

    public void setCloseMin2(int closeMin2) {
        this.closeMin2 = closeMin2;
    }

    public int getOpenHour3() {
        return openHour3;
    }

    public void setOpenHour3(int openHour3) {
        this.openHour3 = openHour3;
    }

    public int getOpenMin3() {
        return openMin3;
    }

    public void setOpenMin3(int openMin3) {
        this.openMin3 = openMin3;
    }

    public int getCloseHour3() {
        return closeHour3;
    }

    public void setCloseHour3(int closeHour3) {
        this.closeHour3 = closeHour3;
    }

    public int getCloseMin3() {
        return closeMin3;
    }

    public void setCloseMin3(int closeMin3) {
        this.closeMin3 = closeMin3;
    }

    @Override
    public String toString() {
        return "TimerTask{" +
                "week=" + week +
                ",macAddress="+macAddress+
                ", openHour=" + openHour +
                ", openMin=" + openMin +
                ", closeHour=" + closeHour +
                ", closeMin=" + closeMin +
                ", openHour2=" + openHour2 +
                ", openMin2=" + openMin2 +
                ", closeHour2=" + closeHour2 +
                ", closeMin2=" + closeMin2 +
                ", openHour3=" + openHour3 +
                ", openMin3=" + openMin3 +
                ", closeHour3=" + closeHour3 +
                ", closeMin3=" + closeMin3 +
                '}';
    }
}
