package com.peihou.waterpurifer.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Packages implements Cloneable{
    @Id(autoincrement = false)
    private Long packageId;  /*'套餐id'*/
    private String packageName ; /*'套餐名',*/
    public String getPackageName() {
        return this.packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public Long getPackageId() {
        return this.packageId;
    }
    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }
    @Generated(hash = 716287980)
    public Packages(Long packageId, String packageName) {
        this.packageId = packageId;
        this.packageName = packageName;
    }
    @Generated(hash = 688242455)
    public Packages() {
    }


  

}
