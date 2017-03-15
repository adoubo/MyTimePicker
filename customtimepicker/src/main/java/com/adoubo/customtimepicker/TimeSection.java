package com.adoubo.customtimepicker;

/**
 * Created by caoweixin
 * 2017/3/3
 * Email: caoweixin@hikvision.com.cn
 */

public class TimeSection implements Cloneable {

    private int mStartHour;

    private int mStartMinute;

    private int mEndHour;

    private int mEndMinute;

    public TimeSection() {

    }

    public TimeSection(int startHour, int startMinute, int endHour, int endMinute) {
        this.mStartHour = startHour;
        this.mStartMinute = startMinute;
        this.mEndHour = endHour;
        this.mEndMinute = endMinute;
    }

    public int getStartHour() {
        return mStartHour;
    }

    public void setStartHour(int startHour) {
        this.mStartHour = startHour;
    }

    public int getStartMinute() {
        return mStartMinute;
    }

    public void setStartMinute(int startMinute) {
        this.mStartMinute = startMinute;
    }

    public int getEndHour() {
        return mEndHour;
    }

    public void setEndHour(int endHour) {
        this.mEndHour = endHour;
    }

    public int getEndMinute() {
        return mEndMinute;
    }

    public void setEndMinute(int endMinute) {
        this.mEndMinute = endMinute;
    }
}
