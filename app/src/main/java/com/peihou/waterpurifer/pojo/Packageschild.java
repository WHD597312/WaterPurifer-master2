package com.peihou.waterpurifer.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Packageschild {
    @Id(autoincrement = false)
    private Long  pchildId; /* '套餐id',*/
    private int pchildNum; /*流量、时间*/
    private String pchildName;
    private int  pchildOldPrice;
    private double pchildNewPrice;
    private int pchildDiscount;
    private long parentId;
    @Generated(hash = 1858536981)
    public Packageschild(Long pchildId, int pchildNum, String pchildName,
            int pchildOldPrice, double pchildNewPrice, int pchildDiscount,
            long parentId) {
        this.pchildId = pchildId;
        this.pchildNum = pchildNum;
        this.pchildName = pchildName;
        this.pchildOldPrice = pchildOldPrice;
        this.pchildNewPrice = pchildNewPrice;
        this.pchildDiscount = pchildDiscount;
        this.parentId = parentId;
    }
    @Generated(hash = 2017362485)
    public Packageschild() {
    }
    public long getParentId() {
        return this.parentId;
    }
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }
    public int getPchildDiscount() {
        return this.pchildDiscount;
    }
    public void setPchildDiscount(int pchildDiscount) {
        this.pchildDiscount = pchildDiscount;
    }
    public double getPchildNewPrice() {
        return this.pchildNewPrice;
    }
    public void setPchildNewPrice(double pchildNewPrice) {
        this.pchildNewPrice = pchildNewPrice;
    }
    public int getPchildOldPrice() {
        return this.pchildOldPrice;
    }
    public void setPchildOldPrice(int pchildOldPrice) {
        this.pchildOldPrice = pchildOldPrice;
    }
    public String getPchildName() {
        return this.pchildName;
    }
    public void setPchildName(String pchildName) {
        this.pchildName = pchildName;
    }
    public int getPchildNum() {
        return this.pchildNum;
    }
    public void setPchildNum(int pchildNum) {
        this.pchildNum = pchildNum;
    }
    public Long getPchildId() {
        return this.pchildId;
    }
    public void setPchildId(Long pchildId) {
        this.pchildId = pchildId;
    }
    
    

}
