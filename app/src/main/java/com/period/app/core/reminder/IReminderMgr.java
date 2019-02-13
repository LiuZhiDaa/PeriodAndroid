package com.period.app.core.reminder;

import com.period.app.bean.ReminderInfoBean;

import ulric.li.xlib.intf.IXManager;

/**
 * Created by WangGuiLi
 * on 2018/12/19
 */
public interface IReminderMgr extends IXManager{

    boolean isSwitchOn(int type);
    void setSwitch(int type,boolean isSwitchOn);
    String getReminderCustomText(int type);
    void setReminderText(int type,String reminderText);
    String getReminderTime(int type);
    void setReminderTime(int type,String reminderTime);
    int getAdvanceDay(int type);
    void setAdvanceDay(int type,int advanceDay);

    void setRemindAlarm(int type, boolean next);

    /**
     * 重置所有的提醒
     */
    void resetAllRemind();

    void showNotification(int type);
}
