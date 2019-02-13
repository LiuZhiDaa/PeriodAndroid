package com.period.app.core.calendardialog;



import android.app.Activity;
import android.content.Context;

import ulric.li.xlib.intf.IXManager;
import ulric.li.xlib.intf.IXObserver;

public interface ICalendarDialogManger extends IXManager,IXObserver<ICalendarListener> {
    int EditDialog(long currentTime,long selectTime);

    void SettingStart(long time);

    void SettingEnd(long time);

    void ChangeStart(long newTime,long oldTime);

    void ChangeEnd(long newTime,long oldTime);

    void cancelStart(long time, long currentime);

    void cancelEnd(long time, long currentime);

    void update(long time,boolean reset);

    void PatchPosition(long startTime,long endTime);

    void PatchPositions(long startTime,long endTime, long currentime);

    void PatchPositionss(long startTime,long endTime, long time);

    void updateFragment();

    void HandlingClickEvents(boolean isChecked,int startOrend,long selectTime, Activity context);

    /**
     * 补充经期数据
     * @param startPeriodTime
     * @param newTime
     */
    void updatePeriodData(long startPeriodTime, long newTime,boolean isSetEnd);


}
