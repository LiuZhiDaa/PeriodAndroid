package com.period.app.bean;

/**
 * Created by WangGuiLi
 * on 2018/12/19
 */
public class ReminderInfoBean {
    private boolean isReminde;
    private String reminderCustomText;
    private int advancedDay;
    private String reminderTime;

    public boolean isReminde() {
        return isReminde;
    }

    public void setReminde(boolean reminde) {
        isReminde = reminde;
    }

    public String getReminderCustomText() {
        return reminderCustomText;
    }

    public void setReminderCustomText(String reminderCustomText) {
        this.reminderCustomText = reminderCustomText;
    }

    public int getAdvancedDay() {
        return advancedDay;
    }

    public void setAdvancedDay(int advancedDay) {
        this.advancedDay = advancedDay;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }
}
