package com.peihou.waterpurifer.pojo;

public class Data {
    int faultId ;
    String faultDeviceMac  ;
    String faultType ;
    String faultTime ;

    public int getFaultId() {
        return faultId;
    }

    public void setFaultId(int faultId) {
        this.faultId = faultId;
    }

    public String getFaultDeviceMac() {
        return faultDeviceMac;
    }

    public void setFaultDeviceMac(String faultDeviceMac) {
        this.faultDeviceMac = faultDeviceMac;
    }

    public String getFaultType() {
        return faultType;
    }

    public void setFaultType(String faultType) {
        this.faultType = faultType;
    }

    public String getFaultTime() {
        return faultTime;
    }

    public void setFaultTime(String faultTime) {
        this.faultTime = faultTime;
    }
}
