package com.peihou.waterpurifer.pojo;

public class RepairList {
    private long repairId;
    private long repairDeviceId;
    private String  repairDeviceName;

    public long getRepairId() {
        return repairId;
    }

    public void setRepairId(long repairId) {
        this.repairId = repairId;
    }

    private String repairDeviceType;
    private String repairTime;
    private String repairAddress;
    private int  repairFlag;

    public long getRepairDeviceId() {
        return repairDeviceId;
    }

    public void setRepairDeviceId(long repairDeviceId) {
        this.repairDeviceId = repairDeviceId;
    }

    public String getRepairDeviceName() {
        return repairDeviceName;
    }

    public void setRepairDeviceName(String repairDeviceName) {
        this.repairDeviceName = repairDeviceName;
    }

    public String getRepairDeviceType() {
        return repairDeviceType;
    }

    public void setRepairDeviceType(String repairDeviceType) {
        this.repairDeviceType = repairDeviceType;
    }

    public String getRepairTime() {
        return repairTime;
    }

    public void setRepairTime(String repairTime) {
        this.repairTime = repairTime;
    }

    public String getRepairAddress() {
        return repairAddress;
    }

    public void setRepairAddress(String repairAddress) {
        this.repairAddress = repairAddress;
    }

    public int getRepairFlag() {
        return repairFlag;
    }

    public void setRepairFlag(int repairFlag) {
        this.repairFlag = repairFlag;
    }


}
