package com.peihou.waterpurifer.pojo;

public class Task {
    private String desc;
    private String value;
    private int type;
    private int visibility;

    public Task() {
    }

    public Task(String desc, String value) {
        this.desc = desc;
        this.value = value;
        this.type=type;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
