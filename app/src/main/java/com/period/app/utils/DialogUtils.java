package com.period.app.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.period.app.R;
import com.period.app.bean.DatePhysiologyBean;
import com.period.app.core.XCoreFactory;
import com.period.app.core.calendardialog.ICalendarDialogManger;
import com.period.app.core.data.IDataMgr;
import com.period.app.core.dba.IDbaManger;
import com.period.app.dialog.calendar.ChangePeriodtimeDialog;
import com.period.app.dialog.calendar.SettingPeriodStartDialog;
import com.period.app.dialog.calendar.StatusEditorDialog;


public class DialogUtils {
    public static int VALUE_INT_MAIN_FRAGMENT = 0;
    public static int VALUE_INT_CALENDAR_FRAGMENT = 1;

    // 0 设置开始弹窗  1 设置结束弹窗  2 修改开始弹窗   3 修改结束弹窗  5 设置开始设置结束取消周期
    @SuppressLint("ShowToast")
    public static void  getMonthOfDay(Context context,int pageTag, int tag, long selectTime, long currentime,DialogConfimListener dialogConfimListener){
        long currentTime = System.currentTimeMillis();
        SettingPeriodStartDialog mSettingPeriodStartDialog;
        ChangePeriodtimeDialog mChangePeriodtimeDialog;
        StatusEditorDialog statusEditorDialog;
        IDataMgr iDataMgr = (IDataMgr) XCoreFactory.getInstance().createInstance(IDataMgr.class);
        ICalendarDialogManger mICalendarDialogManger=(ICalendarDialogManger) XCoreFactory.getInstance().createInstance(ICalendarDialogManger.class);
        IDbaManger mIDbaManger=(IDbaManger) XCoreFactory.getInstance().createInstance(IDbaManger.class);
        switch (tag){
            case 0:
                mSettingPeriodStartDialog=new SettingPeriodStartDialog(context,context.getString(SettingPeriodStartDialog.VALUE_INT_START_TEXT),"");
                mSettingPeriodStartDialog.setOnClickListenerSave(v -> {
                    if(pageTag==VALUE_INT_MAIN_FRAGMENT){
                        LogUtils.mainConfirmLog("setMenstrualStart");
                    }else {
                        LogUtils.calendarLog("setMenstrualStart");
                    }
                    DatePhysiologyBean datePhysiologyBean= mIDbaManger.queryTimeAfterStartData(selectTime);
                    if (datePhysiologyBean==null){
                        mICalendarDialogManger.SettingStart(selectTime);
                    }else {
                        if ((datePhysiologyBean.getCurrentDate()-selectTime)>1000*60*60*24){
                            mICalendarDialogManger.SettingStart(selectTime);
                        }else {
                            Toast.makeText(context, R.string.reselect_the_time,Toast.LENGTH_SHORT).show();
                        }

                    }
                    if(dialogConfimListener!=null)
                        dialogConfimListener.confirm();
                    mSettingPeriodStartDialog.cancel();
                });
                mSettingPeriodStartDialog.setOnClickListenerCancel(v -> mSettingPeriodStartDialog.cancel());
                mSettingPeriodStartDialog.show();
                break;
            case 1:
                mSettingPeriodStartDialog=new SettingPeriodStartDialog(context,context.getString(SettingPeriodStartDialog.VALUE_INT_END_TEXT),"");
                mSettingPeriodStartDialog.setOnClickListenerSave(v -> {
                    if(pageTag == VALUE_INT_MAIN_FRAGMENT) {
                        LogUtils.mainConfirmLog("setMenstrualEnd");
                    }else {
                        LogUtils.calendarLog("setMenstrualEnd");
                    }
                    mICalendarDialogManger.SettingEnd(selectTime);
                    if(dialogConfimListener!=null)
                        dialogConfimListener.confirm();
                    mSettingPeriodStartDialog.cancel();
                });
                mSettingPeriodStartDialog.setOnClickListenerCancel(v -> mSettingPeriodStartDialog.cancel());
                mSettingPeriodStartDialog.show();
                break;
            case 2:

                break;
            case 3:
                break;
            case 5:
                statusEditorDialog=new StatusEditorDialog(context);
                statusEditorDialog.setOnClickListenerSave(v -> {
                    if (statusEditorDialog.getStatus()==1){
                        DatePhysiologyBean datePhysiologyBean1=mIDbaManger.queryTimebeforeStartData(selectTime);
                        mICalendarDialogManger.ChangeStart(selectTime,datePhysiologyBean1.getCurrentDate());
                        DatePhysiologyBean afterStart=mIDbaManger.queryTimeAfterStartData(selectTime);
                        if (afterStart==null){
                            DatePhysiologyBean afterEnd=mIDbaManger.queryTimeAfterEndData(selectTime);
                            if (afterEnd==null){
                                iDataMgr.setManualstart(selectTime);
                            }
                        }
                        if(pageTag==VALUE_INT_MAIN_FRAGMENT){
                            LogUtils.mainConfirmLog("setMenstrualStart2");
                        }else {
                            LogUtils.calendarConfirmLog("setMenstrualStart2");
                        }
                    }else if (statusEditorDialog.getStatus()==2){
                        DatePhysiologyBean datePhysiologyBean1=mIDbaManger.queryTimeAfterStartData(selectTime);
                        if (datePhysiologyBean1==null){
                            DatePhysiologyBean datePhysiologyBean2=mIDbaManger.queryTimeAfterEndData(selectTime);
                            if (datePhysiologyBean2==null){
                                mICalendarDialogManger.SettingEnd(CalendarUtil.getZeroDate(System.currentTimeMillis()));
                                mICalendarDialogManger.ChangeEnd(selectTime,CalendarUtil.getZeroDate(System.currentTimeMillis()));
                            }else {
                                mICalendarDialogManger.ChangeEnd(selectTime,datePhysiologyBean2.getCurrentDate());
                            }
                        }else {
                            DatePhysiologyBean datePhysiologyBean3=mIDbaManger.queryTimeAfterEndData(selectTime);
                            mICalendarDialogManger.ChangeEnd(selectTime,datePhysiologyBean3.getCurrentDate());
                        }
                        if(pageTag==VALUE_INT_MAIN_FRAGMENT){
                            LogUtils.mainConfirmLog("setMenstrualEnd2");
                        }else {
                            LogUtils.calendarConfirmLog("setMenstrualEnd2");
                        }
                    }else if (statusEditorDialog.getStatus()==3){
                        DatePhysiologyBean datePhysiologyBean1=mIDbaManger.queryTimebeforeStartData(selectTime);
                        mICalendarDialogManger.cancelStart(datePhysiologyBean1.getCurrentDate(),CalendarUtil.getZeroDate(System.currentTimeMillis()));
                        if(pageTag==VALUE_INT_MAIN_FRAGMENT){
                            LogUtils.mainConfirmLog("deleteSelectedMenstrual");
                        }else {
                            LogUtils.calendarConfirmLog("deleteSelectedMenstrual");
                        }
                    }else {
                        if(pageTag==VALUE_INT_MAIN_FRAGMENT){
                            LogUtils.mainConfirmLog("noAdjust");
                        }else {
                            LogUtils.calendarConfirmLog("noAdjust");
                        }
                    }
                    if(dialogConfimListener!=null)
                        dialogConfimListener.confirm();
                    statusEditorDialog.cancel();
                });
                statusEditorDialog.setOnClickListenerCancel(v -> statusEditorDialog.cancel());
                statusEditorDialog.show();
                break;

        }
    }

    public interface DialogConfimListener{
        void confirm();
        void cancel();
    }
}
