package com.peihou.waterpurifer.pojo;

public class TimerTask2 {
    int start;
    int end;
    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public TimerTask2() {
    }

    public TimerTask2(int start, int end) {
        this.start = start;
        this.end = end;
    }
}
