package com.period.app.core.calendardialog;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


import com.period.app.R;
import com.period.app.bean.DatePhysiologyBean;
import com.period.app.bean.TimeFrame;
import com.period.app.core.XCoreFactory;
import com.period.app.core.data.IDataMgr;
import com.period.app.core.dba.IDbaManger;
import com.period.app.core.prediction.IPredictionManger;
import com.period.app.dialog.calendar.SettingPeriodStartDialog;
import com.period.app.dialog.calendar.TipsDialog;
import com.period.app.utils.CalendarUtil;
import com.period.app.utils.LogUtils;

import java.util.Calendar;

import ulric.li.XLibFactory;
import ulric.li.xlib.impl.XObserver;
import ulric.li.xlib.intf.IXThreadPool;
import ulric.li.xlib.intf.IXThreadPoolListener;

public class CalendarDialogManger extends XObserver<ICalendarListener> implements ICalendarDialogManger {

    private final IDbaManger mIDbaManger;
    private final IDataMgr mIDataMgr;

    public CalendarDialogManger() {
        mIDbaManger = (IDbaManger) XCoreFactory.getInstance().createInstance(IDbaManger.class);
        mIDataMgr = (IDataMgr) XCoreFactory.getInstance().createInstance(IDataMgr.class);

    }

    // 0 设置开始弹窗  1 设置结束弹窗  2 修改开始弹窗   3 修改结束弹窗  4时间在最新的月经周期之内但不是在当天，点击的时间为开始时间与当天之间
    @Override
    public int EditDialog(long currentTime, long selectTime) {
        DatePhysiologyBean datePhysiologyBean = mIDbaManger.queryPredictionData(selectTime);
        if (datePhysiologyBean != null) {
            if (datePhysiologyBean.getCurrentState() == 1 && !datePhysiologyBean.getIsPeriodStart() && !datePhysiologyBean.getIsPeriodEnd()) {
                return 5;
            }
            if (!datePhysiologyBean.getIsPeriodEnd() && !datePhysiologyBean.getIsPeriodStart()) {
                if (mIDataMgr.getManualstart() == 1000) {
                    return 0;
                } else {
                    if (datePhysiologyBean.getCurrentDate() > mIDataMgr.getManualstart()) {
                        if (selectTime == currentTime) {
                            return 1;
                        } else {
                            return 4;
                        }
                    } else {
                        return 0;
                    }

                }
            } else if (datePhysiologyBean.getIsPeriodStart()) {
                return 2;
            } else if (datePhysiologyBean.getIsPeriodEnd()) {
                return 3;
            }
        }
        return 0;
    }

    @Override
    public void SettingStart(long time) {
        IXThreadPool mIXThreadPool = (IXThreadPool) XLibFactory.getInstance().createInstance(IXThreadPool.class);
        time = CalendarUtil.getZeroDate(time);
        //先判断是否为当前月
        Log.d("lzd", "设置开始时间1  =======" + System.currentTimeMillis());
        long current = CalendarUtil.getZeroDate(System.currentTimeMillis());
        Calendar selectCalendar = Calendar.getInstance();
        selectCalendar.setTimeInMillis(time);
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(System.currentTimeMillis());
        long finalTime = time;
        mIXThreadPool.addTask(new IXThreadPoolListener() {
            @Override
            public void onTaskRun() {
                if (selectCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {
                    Log.d("lzd", "设置开始时间2  =======" + System.currentTimeMillis());
                    //是当前月
                    DatePhysiologyBean beforeEndBean = mIDbaManger.queryTimebeforeEndData(finalTime);
                    if (beforeEndBean != null) {
                        Log.d("lzd", "设置开始时间3  =======" + System.currentTimeMillis());
                        //有上次结束时间
                        DatePhysiologyBean afterStart = mIDbaManger.queryTimeAfterStartData(finalTime);
                        //补位法补充上次结束时间-本次开始时间之间的数据
                        PatchPosition(beforeEndBean.getCurrentDate() + 1000 * 3600 * 24, finalTime - 1000 * 3600 * 24);
                        //补充本次月经日期
                        int sizes = mIDataMgr.getMenstrualDuration();
                        for (long i = 0; i < sizes; i++) {
                            long times = finalTime + i * 1000 * 60 * 60 * 24;
                            if (times > current) {
                                continue;
                            }
                            DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                            datePhysiologyBean.setCurrentDate(times);
                            datePhysiologyBean.setCurrentState(1);
                            datePhysiologyBean.setMIndexofPeriod(Long.valueOf(i).intValue());
                            if (i == 0) {
                                mIDataMgr.setManualstart(times);
                                datePhysiologyBean.setIsPeriodStart(true);
                            } else {
                                datePhysiologyBean.setIsPeriodStart(false);
                            }
                            if (i == sizes - 1) {
                                if (times == CalendarUtil.getZeroDate(System.currentTimeMillis())) {
                                    datePhysiologyBean.setIsPeriodEnd(false);
                                } else {
                                    mIDataMgr.setManualstart(1000);
                                    datePhysiologyBean.setIsPeriodEnd(true);
                                }

                            } else {
                                datePhysiologyBean.setIsPeriodEnd(false);
                            }
                            mIDbaManger.modifyPredictionData(datePhysiologyBean);
                        }
                        Log.d("lzd", "设置开始时间5  =======" + System.currentTimeMillis());
                        if (afterStart != null) {
                            //有下次开始时间
                            DatePhysiologyBean afterEnd = mIDbaManger.queryTimeAfterEndData(finalTime);
                            if (afterEnd!=null){
                                PatchPosition(afterEnd.getCurrentDate() + 1000 * 3600 * 24, afterStart.getCurrentDate() - 1000 * 3600 * 24);
                            }
                        } else {
                            //无下次开始时间
                            //补充本次月经日期结束到当天数据
                            Log.d("lzd", "设置开始时间7  =======" + System.currentTimeMillis());
                            Long l = Long.parseLong(String.valueOf(mIDataMgr.getPhyCycle()));
                            long nextStartTime = finalTime + 1000 * 3600 * 24 * l;
                            if (nextStartTime > current) {
                                mIDataMgr.setManualstart(finalTime);
                                if (daysBetween(finalTime, current) + 1 >= mIDataMgr.getMenstrualDuration()) {
                                    //经期结束日为当天则查询不到经期结束日
                                    if (mIDbaManger.queryTimeAfterEndData(finalTime) != null) {
                                        PatchPositions(mIDbaManger.queryTimeAfterEndData(finalTime).getCurrentDate() + 1000 * 3600 * 24,
                                                nextStartTime - 1000 * 3600 * 24, current);
                                    }
                                }
                            } else {
                                mIDataMgr.setManualstart(current);
                                PatchPosition(mIDbaManger.queryTimeAfterEndData(finalTime).getCurrentDate() + 1000 * 3600 * 24, current - 1000 * 3600 * 24);
                            }
                        }

                    } else {
                        Log.d("lzd", "设置开始时间10  =======" + System.currentTimeMillis());
                        //无上次结束时间 需要推测上次开始时间来得到结束时间
                        Long l = Long.parseLong(String.valueOf(mIDataMgr.getPhyCycle()));
                        java.util.Calendar calendar = java.util.Calendar.getInstance();
                        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                        calendar.set(java.util.Calendar.MINUTE, 0);
                        calendar.set(java.util.Calendar.SECOND, 0);
                        calendar.set(java.util.Calendar.MILLISECOND, 0);
                        calendar.set(java.util.Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
                        calendar.set(java.util.Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
                        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
                        DatePhysiologyBean afterStart = mIDbaManger.queryTimeAfterStartData(finalTime);
                        //补位法补充1号-本次开始时间之间的数据
                        PatchPosition(calendar.getTimeInMillis(), finalTime - 1000 * 3600 * 24);
                        //补充本次月经日期
                        int sizes = mIDataMgr.getMenstrualDuration();
                        for (long i = 0; i < sizes; i++) {
                            long times = finalTime + i * 1000 * 60 * 60 * 24;
                            if (times > current) {
                                continue;
                            }
                            DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                            datePhysiologyBean.setCurrentDate(times);
                            datePhysiologyBean.setCurrentState(1);
                            datePhysiologyBean.setMIndexofPeriod(Long.valueOf(i).intValue());
                            if (i == 0) {
                                datePhysiologyBean.setIsPeriodStart(true);
                            } else {
                                datePhysiologyBean.setIsPeriodStart(false);
                            }
                            if (i == sizes - 1) {
                                if (times == CalendarUtil.getZeroDate(System.currentTimeMillis())) {
                                    datePhysiologyBean.setIsPeriodEnd(false);
                                } else {
                                    datePhysiologyBean.setIsPeriodEnd(true);
                                }
                            } else {
                                datePhysiologyBean.setIsPeriodEnd(false);
                            }
                            mIDbaManger.modifyPredictionData(datePhysiologyBean);
                        }
                        Log.d("lzd", "设置开始时间12  =======" + System.currentTimeMillis());
                        if (afterStart != null) {
                            //有下次开始时间
                            DatePhysiologyBean afterEnd = mIDbaManger.queryTimeAfterEndData(finalTime);
                            if (afterEnd != null)
                                PatchPosition(afterEnd.getCurrentDate() + 1000 * 3600 * 24, afterStart.getCurrentDate() - 1000 * 3600 * 24);
                            Log.d("lzd", "设置开始时间13  =======" + System.currentTimeMillis());
                        } else {
                            //无下次开始时间
                            //补充本次月经日期结束到当天数据
                            long nextStartTime = finalTime + 1000 * 3600 * 24 * l;
                            if (nextStartTime > current) {
                                mIDataMgr.setManualstart(finalTime);
                                if (daysBetween(finalTime, current) + 1 >= mIDataMgr.getMenstrualDuration()) {
                                    if (mIDbaManger.queryTimeAfterEndData(finalTime) != null) {
                                        PatchPositions(mIDbaManger.queryTimeAfterEndData(finalTime).getCurrentDate() + 1000 * 3600 * 24,
                                                nextStartTime - 1000 * 3600 * 24, current);
                                    }
                                }

                            } else {
                                mIDataMgr.setManualstart(current);
                                PatchPosition(mIDbaManger.queryTimeAfterEndData(finalTime).getCurrentDate() + 1000 * 3600 * 24, current - 1000 * 3600 * 24);
                            }
                        }

                    }
                } else {
                    //不是当前月
                    Log.d("lzd", "设置开始时间17  =======" + System.currentTimeMillis());
                    DatePhysiologyBean beforeEndBean = mIDbaManger.queryTimebeforeEndData(finalTime);
                    if (beforeEndBean != null) {
                        Log.d("lzd", "设置开始时间18  =======" + System.currentTimeMillis());

                        //有上次结束时间
                        PatchPosition(beforeEndBean.getCurrentDate() + 1000 * 3600 * 24, finalTime - 1000 * 3600 * 24);
                        //补充本次月经日期
                        int sizes = mIDataMgr.getMenstrualDuration();
                        for (long i = 0; i < sizes; i++) {
                            long times = finalTime + i * 1000 * 60 * 60 * 24;
                            DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                            datePhysiologyBean.setCurrentDate(times);
                            datePhysiologyBean.setCurrentState(1);
                            datePhysiologyBean.setMIndexofPeriod(Long.valueOf(i).intValue());
                            if (i == 0) {
                                datePhysiologyBean.setIsPeriodStart(true);
                            } else {
                                datePhysiologyBean.setIsPeriodStart(false);
                            }
                            if (i == sizes - 1) {
                                datePhysiologyBean.setIsPeriodEnd(true);
                            } else {
                                datePhysiologyBean.setIsPeriodEnd(false);
                            }
                            mIDbaManger.modifyPredictionData(datePhysiologyBean);
                        }
                        Log.d("lzd", "设置开始时间20 =======" + System.currentTimeMillis());
                        DatePhysiologyBean afterStart = mIDbaManger.queryTimeAfterStartData(finalTime);
                        if (afterStart != null) {
                            //有下次开始时间
                            //补位法补充上次结束时间-本次开始时间之间的数据
                            Log.d("lzd", "设置开始时间21 =======" + System.currentTimeMillis());
                            DatePhysiologyBean afterEnd = mIDbaManger.queryTimeAfterEndData(finalTime);
                            PatchPosition(afterEnd.getCurrentDate() + 1000 * 3600 * 24, afterStart.getCurrentDate() - 1000 * 3600 * 24);

                        } else {
                            //没有下次开始时间
                            //补充本次月经日期结束到当天数据
                            Log.d("lzd", "设置开始时间23 =======" + System.currentTimeMillis());
                            Long l = Long.parseLong(String.valueOf(mIDataMgr.getPhyCycle()));
                            long nextStartTime = finalTime + 1000 * 3600 * 24 * l;
                            if (nextStartTime > current) {
                                mIDataMgr.setManualstart(finalTime);
                                PatchPositions(mIDbaManger.queryTimeAfterEndData(finalTime).getCurrentDate() + 1000 * 3600 * 24,
                                        nextStartTime - 1000 * 3600 * 24, current);
                            } else {
                                mIDataMgr.setManualstart(current);
                                PatchPosition(mIDbaManger.queryTimeAfterEndData(finalTime).getCurrentDate() + 1000 * 3600 * 24, current - 1000 * 3600 * 24);
                            }

                        }
                    } else {
                        Log.d("lzd", "设置开始时间26 =======" + System.currentTimeMillis());
                        DatePhysiologyBean afterStart = mIDbaManger.queryTimeAfterStartData(finalTime);
                        //无上次结束时间  需要推测上次开始时间来得到结束时间
                        Long l = Long.parseLong(String.valueOf(mIDataMgr.getPhyCycle()));
                        java.util.Calendar calendar = java.util.Calendar.getInstance();
                        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                        calendar.set(java.util.Calendar.MINUTE, 0);
                        calendar.set(java.util.Calendar.SECOND, 0);
                        calendar.set(java.util.Calendar.MILLISECOND, 0);
                        if (selectCalendar.get(Calendar.MONTH) > 0) {
                            calendar.set(java.util.Calendar.YEAR, selectCalendar.get(Calendar.YEAR));
                            calendar.set(java.util.Calendar.MONTH, selectCalendar.get(Calendar.MONTH) - 1);
                        } else {
                            calendar.set(java.util.Calendar.YEAR, selectCalendar.get(Calendar.YEAR) - 1);
                            calendar.set(java.util.Calendar.MONTH, 0);
                        }
                        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
                        //补位法补充上月1号-本次开始时间之间的数据
                        PatchPosition(calendar.getTimeInMillis(), finalTime - 1000 * 3600 * 24);
                        //补充本次月经日期
                        int sizes = mIDataMgr.getMenstrualDuration();
                        for (long i = 0; i < sizes; i++) {
                            long times = finalTime + i * 1000 * 60 * 60 * 24;
                            DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                            datePhysiologyBean.setCurrentDate(times);
                            datePhysiologyBean.setCurrentState(1);
                            datePhysiologyBean.setMIndexofPeriod(Long.valueOf(i).intValue());
                            if (i == 0) {
                                datePhysiologyBean.setIsPeriodStart(true);
                            } else {
                                datePhysiologyBean.setIsPeriodStart(false);
                            }
                            if (i == sizes - 1) {
                                datePhysiologyBean.setIsPeriodEnd(true);
                            } else {
                                datePhysiologyBean.setIsPeriodEnd(false);
                            }
                            mIDbaManger.modifyPredictionData(datePhysiologyBean);
                        }
                        Log.d("lzd", "设置开始时间28 =======" + System.currentTimeMillis());
                        if (afterStart != null) {
                            //有下次开始时间
                            DatePhysiologyBean afterEnd = mIDbaManger.queryTimeAfterEndData(finalTime);
                            PatchPosition(afterEnd.getCurrentDate() + 1000 * 3600 * 24, afterStart.getCurrentDate() - 1000 * 3600 * 24);

                        } else {
                            //没有下次开始时间
                            long nextStartTime = finalTime + 1000 * 3600 * 24 * l;
                            if (nextStartTime > current) {
                                PatchPositions(mIDbaManger.queryTimeAfterEndData(finalTime).getCurrentDate() + 1000 * 3600 * 24,
                                        nextStartTime - 1000 * 3600 * 24, current);
                            } else {
                                mIDataMgr.setManualstart(current);
                                PatchPosition(mIDbaManger.queryTimeAfterEndData(finalTime).getCurrentDate() + 1000 * 3600 * 24, current - 1000 * 3600 * 24);
                            }
                        }

                    }

                }
            }

            @Override
            public void onTaskComplete() {
                Log.d("lzd", "发送更新通知 =======" + System.currentTimeMillis());
                CalendarDialogManger.this.notify(iCalendarListener -> iCalendarListener.toUpdate(CalendarUtil.year, CalendarUtil.month, false));
            }

            @Override
            public void onMessage(Message msg) {

            }
        });


    }

    /**
     * 设置经期结束
     *
     * @param time
     */
    @Override
    public void SettingEnd(long time) {
        IXThreadPool mIXThreadPool = (IXThreadPool) XLibFactory.getInstance().createInstance(IXThreadPool.class);
        mIXThreadPool.addTask(new IXThreadPoolListener() {
            @Override
            public void onTaskRun() {
                long newTime = CalendarUtil.getZeroDate(time);                                                              //设置为经期结束的时间
                DatePhysiologyBean startPhysiologyBean = mIDbaManger.queryTimebeforeStartData(newTime);                  //查询本周期的经期开始时间

                DatePhysiologyBean afterStartPhysiologyBean = mIDbaManger.queryTimeAfterStartData(newTime);                 //查询time之后最近的经期开始时间
                if (startPhysiologyBean == null)
                    return;
                long startPeriodTime = CalendarUtil.getZeroDate(startPhysiologyBean.getCurrentDate());                      //查询当前周期的经期开始时间         //补充数据的结束时间
                long zeroCurrentTime = CalendarUtil.getZeroDate(System.currentTimeMillis());                                //当前时间
                Long cycle = Long.parseLong(String.valueOf(mIDataMgr.getPhyCycle()));

                updatePeriodData(startPeriodTime, newTime, true);                                                                 //更新经期开始日到当前时间的经期数据
                long addDataEndTime;
                //补位法补充经期结束日到下次经期开始时间之间的数据
                if (afterStartPhysiologyBean == null) {                                                                  //没有下次经期开始时间
                    long afterStartTime = startPeriodTime + cycle * 1000 * 3600 * 24;
                    if (afterStartTime <= zeroCurrentTime) {
                        addDataEndTime = zeroCurrentTime;
                        PatchPosition(newTime + 1000 * 3600 * 24, addDataEndTime - 1000 * 3600 * 24);
                    } else {
                        addDataEndTime = afterStartTime;
                        PatchPositions(newTime + 1000 * 3600 * 24, addDataEndTime - 1000 * 3600 * 24, zeroCurrentTime);
                    }
                } else {                                                                                                  //有下次经期开始时间
                    long afterEndTime = CalendarUtil.getZeroDate(afterStartPhysiologyBean.getCurrentDate());
                    PatchPosition(newTime + 1000 * 3600 * 24, afterEndTime - 1000 * 3600 * 24);
                }
            }

            @Override
            public void onTaskComplete() {
                CalendarDialogManger.this.notify(iCalendarListener -> iCalendarListener.toUpdate(CalendarUtil.year, CalendarUtil.month, false));
            }

            @Override
            public void onMessage(Message msg) {

            }
        });


    }

    @Override
    public void ChangeStart(long newTime, long oldTime) {
        IXThreadPool mIXThreadPool = (IXThreadPool) XLibFactory.getInstance().createInstance(IXThreadPool.class);
        mIXThreadPool.addTask(new IXThreadPoolListener() {
            @Override
            public void onTaskRun() {
                DatePhysiologyBean datePhysiologyBean = mIDbaManger.queryTimebeforeEndData(newTime);
                if (datePhysiologyBean != null) {
                    mIDbaManger.deletePredictionData(oldTime);
                    DatePhysiologyBean datePhysiologyBean0 = mIDbaManger.queryTimeAfterStartData(oldTime);
                    if (datePhysiologyBean0 == null) {
                        mIDataMgr.setManualstart(newTime);
                    }
                    DatePhysiologyBean afterEnd = mIDbaManger.queryTimeAfterEndData(newTime);
                    if (afterEnd != null) {
                        int lengths = daysBetween(newTime, oldTime);
                        for (long j = 0; j < lengths + 1; j++) {
                            long newTimes = newTime + j * 1000 * 60 * 60 * 24;
                            DatePhysiologyBean datePhysiologyBean1 = new DatePhysiologyBean();
                            datePhysiologyBean1.setCurrentDate(newTimes);
                            datePhysiologyBean1.setCurrentState(1);
                            if (j == 0) {
                                datePhysiologyBean1.setIsPeriodStart(true);
                            } else {
                                datePhysiologyBean1.setIsPeriodStart(false);
                            }
                            datePhysiologyBean1.setIsPeriodEnd(false);
                            mIDbaManger.modifyPredictionData(datePhysiologyBean1);
                        }
                        PatchPosition(datePhysiologyBean.getCurrentDate() + 1000 * 3600 * 24, newTime - 1000 * 3600 * 24);
                        if (datePhysiologyBean0 != null) {
                            PatchPosition(afterEnd.getCurrentDate() + 1000 * 3600 * 24, datePhysiologyBean0.getCurrentDate() - 1000 * 3600 * 24);
                        } else {
                            long currentime = CalendarUtil.getZeroDate(System.currentTimeMillis());
                            Long l = Long.parseLong(String.valueOf(mIDataMgr.getPhyCycle()));
                            long nextStartTime = newTime + 1000 * 3600 * 24 * l;
                            if (nextStartTime <= currentime) {
                                PatchPosition(afterEnd.getCurrentDate() + 1000 * 3600 * 24, currentime - 1000 * 3600 * 24);
                                mIDataMgr.setManualstart(currentime);
                            } else {
                                PatchPositions(afterEnd.getCurrentDate() + 1000 * 3600 * 24, nextStartTime - 1000 * 3600 * 24, currentime);
                            }
                        }
                    } else {
                        PatchPosition(datePhysiologyBean.getCurrentDate() + 1000 * 3600 * 24, newTime - 1000 * 3600 * 24);
                        long current = CalendarUtil.getZeroDate(System.currentTimeMillis());
                        int lengths = daysBetween(newTime, current) + 1;
                        if (lengths <= mIDataMgr.getMenstrualDuration()) {
                            for (long j = 0; j < lengths; j++) {
                                long newTimes = newTime + j * 1000 * 60 * 60 * 24;
                                DatePhysiologyBean datePhysiologyBean1 = new DatePhysiologyBean();
                                datePhysiologyBean1.setCurrentDate(newTimes);
                                datePhysiologyBean1.setCurrentState(1);
                                if (j == 0) {
                                    datePhysiologyBean1.setIsPeriodStart(true);
                                } else {
                                    datePhysiologyBean1.setIsPeriodStart(false);
                                }
                                datePhysiologyBean1.setIsPeriodEnd(false);
                                mIDbaManger.modifyPredictionData(datePhysiologyBean1);
                            }
                        } else {
                            for (long j = 0; j < mIDataMgr.getMenstrualDuration(); j++) {
                                long newTimes = newTime + j * 1000 * 60 * 60 * 24;
                                DatePhysiologyBean datePhysiologyBean1 = new DatePhysiologyBean();
                                datePhysiologyBean1.setCurrentDate(newTimes);
                                datePhysiologyBean1.setCurrentState(1);
                                if (j == 0) {
                                    datePhysiologyBean1.setIsPeriodStart(true);
                                } else {
                                    datePhysiologyBean1.setIsPeriodStart(false);
                                }
                                if (j == mIDataMgr.getMenstrualDuration() - 1) {
                                    datePhysiologyBean1.setIsPeriodEnd(true);
                                } else {
                                    datePhysiologyBean1.setIsPeriodEnd(false);
                                }
                                mIDbaManger.modifyPredictionData(datePhysiologyBean1);
                            }
                            long currentime = CalendarUtil.getZeroDate(System.currentTimeMillis());
                            DatePhysiologyBean afterEndBean = mIDbaManger.queryTimeAfterEndData(newTime);
                            int sizes = daysBetween(afterEndBean.getCurrentDate(), currentime);
                            for (long j = 1; j < sizes + 1; j++) {
                                long newTimes = afterEndBean.getCurrentDate() + j * 1000 * 60 * 60 * 24;
                                if (newTimes > currentime) {
                                    continue;
                                } else {
                                    mIDbaManger.deletePredictionData(newTimes);
                                }
                            }
                            Long l = Long.parseLong(String.valueOf(mIDataMgr.getPhyCycle()));
                            long nextStartTime = newTime + 1000 * 3600 * 24 * l;
                            if (nextStartTime <= currentime) {
                                PatchPosition(newTime + 1000 * 3600 * 24, currentime - 1000 * 3600 * 24);
                            } else {
                                PatchPositions(afterEndBean.getCurrentDate() + 1000 * 3600 * 24, nextStartTime - 1000 * 3600 * 24, currentime);
                            }
                        }
                    }
                } else {
                    Calendar selectCalendar = Calendar.getInstance();
                    selectCalendar.setTimeInMillis(newTime);
                    Calendar currentCalendar = Calendar.getInstance();
                    currentCalendar.setTimeInMillis(System.currentTimeMillis());
                    if (selectCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {
                        mIDbaManger.deletePredictionData(oldTime);
                        DatePhysiologyBean afterStart = mIDbaManger.queryTimeAfterStartData(newTime);
                        if (afterStart != null) {
                            int lengths = daysBetween(newTime, oldTime);
                            for (long j = 0; j < lengths + 1; j++) {
                                long newTimes = newTime + j * 1000 * 60 * 60 * 24;
                                DatePhysiologyBean datePhysiologyBean1 = new DatePhysiologyBean();
                                datePhysiologyBean1.setCurrentDate(newTimes);
                                datePhysiologyBean1.setCurrentState(1);
                                if (j == 0) {
                                    datePhysiologyBean1.setIsPeriodStart(true);
                                } else {
                                    datePhysiologyBean1.setIsPeriodStart(false);
                                }
                                datePhysiologyBean1.setIsPeriodEnd(false);
                                mIDbaManger.modifyPredictionData(datePhysiologyBean1);
                            }
                        } else {
                            mIDataMgr.setManualstart(newTime);
                            DatePhysiologyBean afterEnd = mIDbaManger.queryTimeAfterEndData(newTime);
                            if (afterEnd != null) {
                                int lengths = daysBetween(newTime, oldTime);
                                for (long j = 0; j < lengths + 1; j++) {
                                    long newTimes = newTime + j * 1000 * 60 * 60 * 24;
                                    DatePhysiologyBean datePhysiologyBean1 = new DatePhysiologyBean();
                                    datePhysiologyBean1.setCurrentDate(newTimes);
                                    datePhysiologyBean1.setCurrentState(1);
                                    if (j == 0) {
                                        datePhysiologyBean1.setIsPeriodStart(true);
                                    } else {
                                        datePhysiologyBean1.setIsPeriodStart(false);
                                    }
                                    datePhysiologyBean1.setIsPeriodEnd(false);
                                    mIDbaManger.modifyPredictionData(datePhysiologyBean1);
                                }
                            } else {
                                long current = CalendarUtil.getZeroDate(System.currentTimeMillis());
                                int lengths = daysBetween(newTime, current) + 1;
                                java.util.Calendar calendar = java.util.Calendar.getInstance();
                                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                                calendar.set(java.util.Calendar.MINUTE, 0);
                                calendar.set(java.util.Calendar.SECOND, 0);
                                calendar.set(java.util.Calendar.MILLISECOND, 0);
                                calendar.set(java.util.Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
                                calendar.set(java.util.Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
                                calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
                                PatchPosition(calendar.getTimeInMillis(), newTime - 1000 * 3600 * 24);
                                if (lengths <= mIDataMgr.getMenstrualDuration()) {
                                    for (long j = 0; j < lengths; j++) {
                                        long newTimes = newTime + j * 1000 * 60 * 60 * 24;
                                        DatePhysiologyBean datePhysiologyBean1 = new DatePhysiologyBean();
                                        datePhysiologyBean1.setCurrentDate(newTimes);
                                        datePhysiologyBean1.setCurrentState(1);
                                        if (j == 0) {
                                            datePhysiologyBean1.setIsPeriodStart(true);
                                        } else {
                                            datePhysiologyBean1.setIsPeriodStart(false);
                                        }
                                        datePhysiologyBean1.setIsPeriodEnd(false);
                                        mIDbaManger.modifyPredictionData(datePhysiologyBean1);
                                    }
                                } else {
                                    for (long j = 0; j < mIDataMgr.getMenstrualDuration(); j++) {
                                        long newTimes = newTime + j * 1000 * 60 * 60 * 24;
                                        DatePhysiologyBean datePhysiologyBean1 = new DatePhysiologyBean();
                                        datePhysiologyBean1.setCurrentDate(newTimes);
                                        datePhysiologyBean1.setCurrentState(1);
                                        if (j == 0) {
                                            datePhysiologyBean1.setIsPeriodStart(true);
                                        } else {
                                            datePhysiologyBean1.setIsPeriodStart(false);
                                        }
                                        if (j == mIDataMgr.getMenstrualDuration() - 1) {
                                            datePhysiologyBean1.setIsPeriodEnd(true);
                                        } else {
                                            datePhysiologyBean1.setIsPeriodEnd(false);
                                        }
                                        mIDbaManger.modifyPredictionData(datePhysiologyBean1);
                                    }
                                    long currentime = CalendarUtil.getZeroDate(System.currentTimeMillis());
                                    DatePhysiologyBean afterEndBean = mIDbaManger.queryTimeAfterEndData(newTime);
                                    int sizes = daysBetween(afterEndBean.getCurrentDate(), currentime);
                                    for (long j = 1; j < sizes + 1; j++) {
                                        long newTimes = afterEndBean.getCurrentDate() + j * 1000 * 60 * 60 * 24;
                                        if (newTimes > currentime) {
                                            continue;
                                        } else {
                                            mIDbaManger.deletePredictionData(newTimes);
                                        }
                                    }
                                    Long l = Long.parseLong(String.valueOf(mIDataMgr.getPhyCycle()));
                                    long nextStartTime = newTime + 1000 * 3600 * 24 * l;
                                    if (nextStartTime <= currentime) {
                                        PatchPosition(newTime + 1000 * 3600 * 24, currentime - 1000 * 3600 * 24);
                                    } else {
                                        PatchPositions(afterEndBean.getCurrentDate() + 1000 * 3600 * 24, nextStartTime - 1000 * 3600 * 24, currentime);
                                    }
                                }
                            }
                        }
                    } else {
                        mIDbaManger.deletePredictionData(oldTime);
                        java.util.Calendar calendar = java.util.Calendar.getInstance();
                        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                        calendar.set(java.util.Calendar.MINUTE, 0);
                        calendar.set(java.util.Calendar.SECOND, 0);
                        calendar.set(java.util.Calendar.MILLISECOND, 0);
                        if (selectCalendar.get(Calendar.MONTH) > 0) {
                            calendar.set(java.util.Calendar.YEAR, selectCalendar.get(Calendar.YEAR));
                            calendar.set(java.util.Calendar.MONTH, selectCalendar.get(Calendar.MONTH) - 1);
                        } else {
                            calendar.set(java.util.Calendar.YEAR, selectCalendar.get(Calendar.YEAR) - 1);
                            calendar.set(java.util.Calendar.MONTH, 0);
                        }
                        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
                        PatchPosition(calendar.getTimeInMillis(), newTime - 1000 * 3600 * 24);
                        DatePhysiologyBean afterEnd = mIDbaManger.queryTimeAfterEndData(newTime);
                        long currentime = CalendarUtil.getZeroDate(System.currentTimeMillis());
                        if (afterEnd != null) {
                            int lengths = daysBetween(newTime, oldTime);
                            for (long j = 0; j < lengths + 1; j++) {
                                long newTimes = newTime + j * 1000 * 60 * 60 * 24;
                                DatePhysiologyBean datePhysiologyBean1 = new DatePhysiologyBean();
                                datePhysiologyBean1.setCurrentDate(newTimes);
                                datePhysiologyBean1.setCurrentState(1);
                                if (j == 0) {
                                    datePhysiologyBean1.setIsPeriodStart(true);
                                } else {
                                    datePhysiologyBean1.setIsPeriodStart(false);
                                }
                                datePhysiologyBean1.setIsPeriodEnd(false);
                                mIDbaManger.modifyPredictionData(datePhysiologyBean1);
                            }
                            DatePhysiologyBean afterSyart = mIDbaManger.queryTimeAfterStartData(newTime);
                            if (afterSyart == null) {
                                Long l = Long.parseLong(String.valueOf(mIDataMgr.getPhyCycle()));
                                long nextStartTime = newTime + 1000 * 3600 * 24 * l;
                                if (nextStartTime <= currentime) {
                                    mIDataMgr.setManualstart(currentime);
                                    PatchPosition(afterEnd.getCurrentDate() + 1000 * 3600 * 24, currentime - 1000 * 3600 * 24);
                                } else {
                                    DatePhysiologyBean datePhysiologyBean1 = mIDbaManger.queryTimeAfterStartData(newTime);
                                    if (datePhysiologyBean1 == null) {
                                        mIDataMgr.setManualstart(newTime);
                                    }
                                    PatchPositions(afterEnd.getCurrentDate() + 1000 * 3600 * 24, nextStartTime - 1000 * 3600 * 24, currentime);
                                }
                            }

                        } else {
                            int lengths = daysBetween(newTime, currentime);
                            for (long j = 0; j < lengths + 1; j++) {
                                long newTimes = newTime + j * 1000 * 60 * 60 * 24;
                                if (newTimes > currentime) {
                                    continue;
                                }
                                DatePhysiologyBean datePhysiologyBean1 = new DatePhysiologyBean();
                                datePhysiologyBean1.setCurrentDate(newTimes);
                                datePhysiologyBean1.setCurrentState(1);
                                if (j == 0) {
                                    datePhysiologyBean1.setIsPeriodStart(true);
                                } else {
                                    datePhysiologyBean1.setIsPeriodStart(false);
                                }
                                datePhysiologyBean1.setIsPeriodEnd(false);
                                mIDbaManger.modifyPredictionData(datePhysiologyBean1);
                            }
                        }

                    }
                }
                DatePhysiologyBean afterStart = mIDbaManger.queryTimeAfterStartData(newTime);
                if (afterStart == null) {
                    if (newTime > mIDataMgr.getManualstart())
                        mIDataMgr.setManualstart(newTime);
                }
            }

            @Override
            public void onTaskComplete() {
                CalendarDialogManger.this.notify(iCalendarListener -> iCalendarListener.toUpdate(CalendarUtil.year, CalendarUtil.month, false));
            }

            @Override
            public void onMessage(Message msg) {

            }
        });
    }

    @Override
    public void ChangeEnd(long newTime, long oldTime) {
        newTime = CalendarUtil.getZeroDate(newTime);
        oldTime = CalendarUtil.getZeroDate(oldTime);
        IXThreadPool mIXThreadPool = (IXThreadPool) XLibFactory.getInstance().createInstance(IXThreadPool.class);
        long finalOldTime = oldTime;
        long finalNewTime = newTime;
        long finalNewTime1 = newTime;
        mIXThreadPool.addTask(new IXThreadPoolListener() {
            @Override
            public void onTaskRun() {
                DatePhysiologyBean startPhysiologyBean = mIDbaManger.queryTimebeforeStartData(finalNewTime);
                DatePhysiologyBean afterStartPhysiologyBean = mIDbaManger.queryTimeAfterStartData(finalNewTime);
                if (startPhysiologyBean == null)
                    return;

                long startPeriodTime = CalendarUtil.getZeroDate(startPhysiologyBean.getCurrentDate());                      //查询当前周期的经期开始时间
                long times = CalendarUtil.getZeroDate(mIDataMgr.getManualstart());                                           //手动设置的经期开始时间
                long endTime;                                                                                              //补充数据的结束时间
                long zeroCurrentTime = CalendarUtil.getZeroDate(System.currentTimeMillis());                                //当前时间
                Long cycle = Long.parseLong(String.valueOf(mIDataMgr.getPhyCycle()));

                if (finalNewTime > finalOldTime) {                                                                                       //向后移动
                    if (startPeriodTime == times) {                                                                            //当前周期,将数据补充到当前日期
                        endTime = finalNewTime >= zeroCurrentTime ? zeroCurrentTime : finalNewTime;
                    } else {                                                                                                //历史周期，将数据补充到经期结束日
                        endTime = finalNewTime;
                    }
                    updatePeriodData(finalOldTime, endTime, true);                                                                      //更新上次结束日到新的结束日之间的数据
                } else {                                                                                                    //向前移动
                    if (startPeriodTime == times) {                                                                            //当前周期,将数据补充到当前日期

                        endTime = finalNewTime >= zeroCurrentTime ? zeroCurrentTime : finalNewTime;
                    } else {                                                                                                //历史周期，将数据补充到经期结束日
                        endTime = finalNewTime;
                    }
                    updatePeriodData(finalNewTime, finalNewTime1, true);                                                                      //只更新当天的数据

                    long addDataEndTime;
                    //补位法补充经期结束日到下次经期开始时间之间的数据
                    if (afterStartPhysiologyBean == null) {
                        long afterStartTime = startPeriodTime + cycle * 1000 * 3600 * 24;
                        if (afterStartTime <= zeroCurrentTime) {
                            addDataEndTime = zeroCurrentTime;
                            PatchPosition(endTime + 1000 * 3600 * 24, addDataEndTime - 1000 * 3600 * 24);
                        } else {
                            addDataEndTime = afterStartTime;
                            PatchPositions(endTime + 1000 * 3600 * 24, addDataEndTime - 1000 * 3600 * 24, zeroCurrentTime);
                        }
                    } else {
                        long afterStartTime = CalendarUtil.getZeroDate(afterStartPhysiologyBean.getCurrentDate());
                        if (afterStartTime <= zeroCurrentTime) {
                            addDataEndTime = zeroCurrentTime;
                            PatchPosition(endTime + 1000 * 3600 * 24, addDataEndTime - 1000 * 3600 * 24);
                        } else {
                            addDataEndTime = afterStartTime;
                            PatchPositions(endTime + 1000 * 3600 * 24, addDataEndTime - 1000 * 3600 * 24, zeroCurrentTime);
                        }
                    }
                }

                mIDataMgr.setManualstart(1000);
            }

            @Override
            public void onTaskComplete() {
                CalendarDialogManger.this.notify(iCalendarListener -> iCalendarListener.toUpdate(CalendarUtil.year, CalendarUtil.month, false));
            }

            @Override
            public void onMessage(Message msg) {

            }
        });


    }


    public void updatePeriodData(long startTime, long endTime, boolean isSetEnd) {
        int size = daysBetween(startTime, endTime);
        for (long i = 0; i <= size; i++) {
            long newtime = startTime + i * 1000 * 60 * 60 * 24;
            if (newtime > endTime) {
                return;
            } else {
                DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                datePhysiologyBean.setCurrentDate(newtime);
                datePhysiologyBean.setCurrentState(1);
                datePhysiologyBean.setMIndexofPeriod(Long.valueOf(i).intValue());
//                datePhysiologyBean.setIsPeriodStart(false);
                if (i == 0) {
                    datePhysiologyBean.setIsPeriodStart(true);
                    datePhysiologyBean.setIsPeriodEnd(false);
                } else if (i == size) {
                    datePhysiologyBean.setIsPeriodStart(false);
                    if (isSetEnd) {
                        datePhysiologyBean.setIsPeriodEnd(true);
                    } else {
                        datePhysiologyBean.setIsPeriodEnd(false);
                    }
                } else {
                    datePhysiologyBean.setIsPeriodStart(false);
                    datePhysiologyBean.setIsPeriodEnd(false);
                }
                mIDbaManger.modifyPredictionData(datePhysiologyBean);
            }
        }
    }

    @Override
    public void cancelStart(long time, long currentime) {
        IXThreadPool mIXThreadPool = (IXThreadPool) XLibFactory.getInstance().createInstance(IXThreadPool.class);
        mIXThreadPool.addTask(new IXThreadPoolListener() {
            @Override
            public void onTaskRun() {
                //前无
                DatePhysiologyBean beforeEndBean = mIDbaManger.queryTimebeforeEndData(time);
                if (beforeEndBean == null) {
                    //前无后无
                    DatePhysiologyBean afterStartBean = mIDbaManger.queryTimeAfterStartData(time);
                    if (afterStartBean == null) {
                        //后无周期前无周期
                        Calendar selectCalendar = Calendar.getInstance();
                        selectCalendar.setTimeInMillis(time);
                        Calendar currentCalendar = Calendar.getInstance();
                        currentCalendar.setTimeInMillis(System.currentTimeMillis());
                        if (selectCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {
                            java.util.Calendar calendar = java.util.Calendar.getInstance();
                            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                            calendar.set(java.util.Calendar.MINUTE, 0);
                            calendar.set(java.util.Calendar.SECOND, 0);
                            calendar.set(java.util.Calendar.MILLISECOND, 0);
                            calendar.set(java.util.Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
                            calendar.set(java.util.Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
                            calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
                            int sizes = daysBetween(calendar.getTimeInMillis(), currentime);
                            for (long j = 0; j < sizes + 1; j++) {
                                long newTimes = calendar.getTimeInMillis() + j * 1000 * 60 * 60 * 24;
                                if (newTimes > currentime) {
                                    continue;
                                } else {
                                    mIDbaManger.deletePredictionData(newTimes);
                                }
                            }
                        } else {
                            java.util.Calendar calendar = java.util.Calendar.getInstance();
                            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                            calendar.set(java.util.Calendar.MINUTE, 0);
                            calendar.set(java.util.Calendar.SECOND, 0);
                            calendar.set(java.util.Calendar.MILLISECOND, 0);
                            if (selectCalendar.get(Calendar.MONTH) > 0) {
                                calendar.set(java.util.Calendar.YEAR, selectCalendar.get(Calendar.YEAR));
                                calendar.set(java.util.Calendar.MONTH, selectCalendar.get(Calendar.MONTH) - 1);
                            } else {
                                calendar.set(java.util.Calendar.YEAR, selectCalendar.get(Calendar.YEAR) - 1);
                                calendar.set(java.util.Calendar.MONTH, 0);
                            }
                            calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
                            int sizes = daysBetween(calendar.getTimeInMillis(), currentime);
                            for (long j = 0; j < sizes + 1; j++) {
                                long newTimes = calendar.getTimeInMillis() + j * 1000 * 60 * 60 * 24;
                                if (newTimes > currentime) {
                                    continue;
                                } else {
                                    mIDbaManger.deletePredictionData(newTimes);
                                }
                            }
                        }
                        mIDataMgr.setManualstart(1000);

                    } else {
                        //前无后有
                        Calendar selectCalendars = Calendar.getInstance();
                        selectCalendars.setTimeInMillis(time);
                        Calendar currentCalendar = Calendar.getInstance();
                        currentCalendar.setTimeInMillis(System.currentTimeMillis());
                        if (selectCalendars.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {
                            java.util.Calendar calendar = java.util.Calendar.getInstance();
                            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                            calendar.set(java.util.Calendar.MINUTE, 0);
                            calendar.set(java.util.Calendar.SECOND, 0);
                            calendar.set(java.util.Calendar.MILLISECOND, 0);
                            calendar.set(java.util.Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
                            calendar.set(java.util.Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
                            calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
                            int sizes = daysBetween(calendar.getTimeInMillis(), afterStartBean.getCurrentDate() - 1000 * 3600 * 24);
                            for (long j = 0; j < sizes + 1; j++) {
                                long newTimes = calendar.getTimeInMillis() + j * 1000 * 60 * 60 * 24;
                                if (newTimes >= afterStartBean.getCurrentDate() - 1000 * 3600 * 24) {
                                    continue;
                                } else {
                                    mIDbaManger.deletePredictionData(newTimes);
                                }
                            }
                        } else {
                            java.util.Calendar calendar = java.util.Calendar.getInstance();
                            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                            calendar.set(java.util.Calendar.MINUTE, 0);
                            calendar.set(java.util.Calendar.SECOND, 0);
                            calendar.set(java.util.Calendar.MILLISECOND, 0);
                            if (selectCalendars.get(Calendar.MONTH) > 0) {
                                calendar.set(java.util.Calendar.YEAR, selectCalendars.get(Calendar.YEAR));
                                calendar.set(java.util.Calendar.MONTH, selectCalendars.get(Calendar.MONTH) - 1);
                            } else {
                                calendar.set(java.util.Calendar.YEAR, selectCalendars.get(Calendar.YEAR) - 1);
                                calendar.set(java.util.Calendar.MONTH, 0);
                            }
                            calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
                            int sizes = daysBetween(calendar.getTimeInMillis(), afterStartBean.getCurrentDate());
                            for (long j = 0; j < sizes + 1; j++) {
                                long newTimes = calendar.getTimeInMillis() + j * 1000 * 60 * 60 * 24;
                                if (newTimes >= afterStartBean.getCurrentDate()) {
                                    continue;
                                } else {
                                    mIDbaManger.deletePredictionData(newTimes);
                                }
                            }
                        }
                        //判断后有这个月份是否为当前月   是当前月则从当前月1号开始使用补位法
                        //先判断是否为当前月
                        Calendar selectCalendar = Calendar.getInstance();
                        selectCalendar.setTimeInMillis(afterStartBean.getCurrentDate());
                        if (selectCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {
                            java.util.Calendar calendar = java.util.Calendar.getInstance();
                            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                            calendar.set(java.util.Calendar.MINUTE, 0);
                            calendar.set(java.util.Calendar.SECOND, 0);
                            calendar.set(java.util.Calendar.MILLISECOND, 0);
                            calendar.set(java.util.Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
                            calendar.set(java.util.Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
                            calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
                            //补位法补充1号-本次开始时间之间的数据
                            PatchPosition(calendar.getTimeInMillis(), afterStartBean.getCurrentDate() - 1000 * 3600 * 24);
                        } else {
                            java.util.Calendar calendar = java.util.Calendar.getInstance();
                            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                            calendar.set(java.util.Calendar.MINUTE, 0);
                            calendar.set(java.util.Calendar.SECOND, 0);
                            calendar.set(java.util.Calendar.MILLISECOND, 0);
                            if (selectCalendar.get(Calendar.MONTH) > 0) {
                                calendar.set(java.util.Calendar.YEAR, selectCalendar.get(Calendar.YEAR));
                                calendar.set(java.util.Calendar.MONTH, selectCalendar.get(Calendar.MONTH) - 1);
                            } else {
                                calendar.set(java.util.Calendar.YEAR, selectCalendar.get(Calendar.YEAR) - 1);
                                calendar.set(java.util.Calendar.MONTH, 0);
                            }
                            calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
                            //补位法补充上月1号-本次开始时间之间的数据
                            PatchPosition(calendar.getTimeInMillis(), afterStartBean.getCurrentDate() - 1000 * 3600 * 24);

                        }
                    }

                } else {
                    //前有
                    //前有后无
                    DatePhysiologyBean afterStartBean = mIDbaManger.queryTimeAfterStartData(time);
                    if (afterStartBean == null) {
                        int sizes = daysBetween(time, currentime);
                        for (long j = 0; j < sizes + 1; j++) {
                            long newTimes = time + j * 1000 * 60 * 60 * 24;
                            if (newTimes > currentime) {
                                continue;
                            } else {
                                mIDbaManger.deletePredictionData(newTimes);
                            }
                        }
                        //补位法补充上次结束-本次开始时间之间的数据
                        long beforeStart = mIDbaManger.queryTimebeforeStartData(time).getCurrentDate();
                        int size = CalendarUtil.daysBetween(beforeStart, currentime);
                        if (size <= mIDataMgr.getPhyCycle()) {
                            mIDataMgr.setManualstart(beforeStart);
                            Long l = Long.parseLong(String.valueOf(mIDataMgr.getPhyCycle()));
                            long nextStart = beforeStart + 1000 * 3600 * 24 * l;
                            PatchPositions(beforeEndBean.getCurrentDate() + 1000 * 3600 * 24, nextStart - 1000 * 3600 * 24, currentime);
                        } else {
                            mIDataMgr.setManualstart(currentime);
                            PatchPosition(beforeEndBean.getCurrentDate() + 1000 * 3600 * 24, currentime - 1000 * 3600 * 24);
                        }


                    } else {
                        //前有后有
                        //补位法补充上次结束-下次开始时间之间的数据
                        PatchPosition(beforeEndBean.getCurrentDate() + 1000 * 3600 * 24, afterStartBean.getCurrentDate() - 1000 * 3600 * 24);
                    }
                }
            }

            @Override
            public void onTaskComplete() {
                CalendarDialogManger.this.notify(iCalendarListener -> iCalendarListener.toUpdate(CalendarUtil.year, CalendarUtil.month, false));
            }

            @Override
            public void onMessage(Message msg) {
            }
        });


    }

    @Override
    public void cancelEnd(long time, long currentime) {
        IXThreadPool mIXThreadPool = (IXThreadPool) XLibFactory.getInstance().createInstance(IXThreadPool.class);
        mIXThreadPool.addTask(new IXThreadPoolListener() {
            @Override
            public void onTaskRun() {
                DatePhysiologyBean startBean = mIDbaManger.queryTimeAfterStartData(time);
                if (startBean != null) {

                } else {
                    long startTime = mIDbaManger.queryTimebeforeStartData(time).getCurrentDate();
                    int size = daysBetween(startTime, currentime);
                    if (size < mIDataMgr.getPhyCycle()) {
                        mIDataMgr.setManualstart(startTime);
                        for (long j = 0; j < size + 1; j++) {
                            long newTimes = startTime + j * 1000 * 60 * 60 * 24;
                            if (newTimes > currentime) {
                                CalendarDialogManger.this.notify(iCalendarListener -> iCalendarListener.toUpdate(CalendarUtil.year, CalendarUtil.month, false));
                                return;
                            } else {
                                DatePhysiologyBean bean = new DatePhysiologyBean();
                                bean.setCurrentDate(newTimes);
                                bean.setCurrentState(1);
                                if (j == 0) {
                                    bean.setIsPeriodStart(true);
                                } else {
                                    bean.setIsPeriodStart(false);
                                }
                                bean.setIsPeriodEnd(false);
                                bean.setMIndexofPeriod((int) j);
                                mIDbaManger.modifyPredictionData(bean);
                            }
                        }
                    } else {

                    }

                }
            }

            @Override
            public void onTaskComplete() {
                CalendarDialogManger.this.notify(iCalendarListener -> iCalendarListener.toUpdate(CalendarUtil.year, CalendarUtil.month, false));
            }

            @Override
            public void onMessage(Message msg) {

            }
        });
    }

    @Override
    public void update(long time, boolean reset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        CalendarDialogManger.this.notify(iCalendarListener -> iCalendarListener.toUpdate(CalendarUtil.year, CalendarUtil.month, reset));
    }


    @Override
    public void PatchPosition(long startTime, long endTime) {
        //没有条件的补位
        long kTime = startTime;
        long eTime = endTime;
        int size = daysBetween(kTime, eTime) + 2;
        for (long j = 0; j < size; j++) {
            long newTimes = eTime - j * 1000 * 60 * 60 * 24;
            if (newTimes < kTime) {
                return;
            } else {
                if (j < 9) {
                    DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                    datePhysiologyBean.setCurrentDate(newTimes);
                    datePhysiologyBean.setCurrentState(2);
                    datePhysiologyBean.setIsPeriodStart(false);
                    datePhysiologyBean.setIsPeriodEnd(false);
                    mIDbaManger.modifyPredictionData(datePhysiologyBean);
                } else if (j < 13) {
                    DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                    datePhysiologyBean.setCurrentDate(newTimes);
                    datePhysiologyBean.setCurrentState(3);
                    datePhysiologyBean.setIsPeriodStart(false);
                    datePhysiologyBean.setIsPeriodEnd(false);
                    mIDbaManger.modifyPredictionData(datePhysiologyBean);
                } else if (j == 13) {
                    DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                    datePhysiologyBean.setCurrentDate(newTimes);
                    datePhysiologyBean.setCurrentState(4);
                    datePhysiologyBean.setIsPeriodStart(false);
                    datePhysiologyBean.setIsPeriodEnd(false);
                    mIDbaManger.modifyPredictionData(datePhysiologyBean);
                } else if (j < 19) {
                    DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                    datePhysiologyBean.setCurrentDate(newTimes);
                    datePhysiologyBean.setCurrentState(3);
                    datePhysiologyBean.setIsPeriodStart(false);
                    datePhysiologyBean.setIsPeriodEnd(false);
                    mIDbaManger.modifyPredictionData(datePhysiologyBean);
                } else {
                    DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                    datePhysiologyBean.setCurrentDate(newTimes);
                    datePhysiologyBean.setCurrentState(2);
                    datePhysiologyBean.setIsPeriodStart(false);
                    datePhysiologyBean.setIsPeriodEnd(false);
                    mIDbaManger.modifyPredictionData(datePhysiologyBean);
                }
            }
        }
    }

    @Override
    public void PatchPositions(long startTime, long endTime, long currentime) {
        //补位大于某个时间跳过
        long kTime = startTime;
        long eTime = endTime;
        int size = daysBetween(kTime, eTime) + 2;
        for (long j = 0; j < size; j++) {
            long newTimes = eTime - j * 1000 * 60 * 60 * 24;
            if (newTimes > currentime) {
                continue;
            }
            if (newTimes < kTime) {
                return;
            } else {
                if (j < 9) {
                    DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                    datePhysiologyBean.setCurrentDate(newTimes);
                    datePhysiologyBean.setCurrentState(2);
                    datePhysiologyBean.setIsPeriodStart(false);
                    datePhysiologyBean.setIsPeriodEnd(false);
                    mIDbaManger.modifyPredictionData(datePhysiologyBean);
                } else if (j < 13) {
                    DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                    datePhysiologyBean.setCurrentDate(newTimes);
                    datePhysiologyBean.setCurrentState(3);
                    datePhysiologyBean.setIsPeriodStart(false);
                    datePhysiologyBean.setIsPeriodEnd(false);
                    mIDbaManger.modifyPredictionData(datePhysiologyBean);
                } else if (j == 13) {
                    DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                    datePhysiologyBean.setCurrentDate(newTimes);
                    datePhysiologyBean.setCurrentState(4);
                    datePhysiologyBean.setIsPeriodStart(false);
                    datePhysiologyBean.setIsPeriodEnd(false);
                    mIDbaManger.modifyPredictionData(datePhysiologyBean);
                } else if (j < 19) {
                    DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                    datePhysiologyBean.setCurrentDate(newTimes);
                    datePhysiologyBean.setCurrentState(3);
                    datePhysiologyBean.setIsPeriodStart(false);
                    datePhysiologyBean.setIsPeriodEnd(false);
                    mIDbaManger.modifyPredictionData(datePhysiologyBean);
                } else {
                    DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                    datePhysiologyBean.setCurrentDate(newTimes);
                    datePhysiologyBean.setCurrentState(2);
                    datePhysiologyBean.setIsPeriodStart(false);
                    datePhysiologyBean.setIsPeriodEnd(false);
                    mIDbaManger.modifyPredictionData(datePhysiologyBean);
                }
            }
        }
    }

    @Override
    public void PatchPositionss(long startTime, long endTime, long time) {
        //补位小于某个时间停止
        long kTime = startTime;
        long eTime = endTime;
        int size = daysBetween(kTime, eTime) + 1;
        for (long j = 0; j < size; j++) {
            long newTimes = eTime - j * 1000 * 60 * 60 * 24;
            if (newTimes < time) {

                return;
            }
            if (newTimes < kTime) {

                return;
            } else {
                if (j < 9) {
                    DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                    datePhysiologyBean.setCurrentDate(newTimes);
                    datePhysiologyBean.setCurrentState(2);
                    datePhysiologyBean.setIsPeriodStart(false);
                    datePhysiologyBean.setIsPeriodEnd(false);
                    mIDbaManger.modifyPredictionData(datePhysiologyBean);
                } else if (j < 13) {
                    DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                    datePhysiologyBean.setCurrentDate(newTimes);
                    datePhysiologyBean.setCurrentState(3);
                    datePhysiologyBean.setIsPeriodStart(false);
                    datePhysiologyBean.setIsPeriodEnd(false);
                    mIDbaManger.modifyPredictionData(datePhysiologyBean);
                } else if (j == 13) {
                    DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                    datePhysiologyBean.setCurrentDate(newTimes);
                    datePhysiologyBean.setCurrentState(4);
                    datePhysiologyBean.setIsPeriodStart(false);
                    datePhysiologyBean.setIsPeriodEnd(false);
                    mIDbaManger.modifyPredictionData(datePhysiologyBean);
                } else if (j < 19) {
                    DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                    datePhysiologyBean.setCurrentDate(newTimes);
                    datePhysiologyBean.setCurrentState(3);
                    datePhysiologyBean.setIsPeriodStart(false);
                    datePhysiologyBean.setIsPeriodEnd(false);
                    mIDbaManger.modifyPredictionData(datePhysiologyBean);
                } else {
                    DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                    datePhysiologyBean.setCurrentDate(newTimes);
                    datePhysiologyBean.setCurrentState(2);
                    datePhysiologyBean.setIsPeriodStart(false);
                    datePhysiologyBean.setIsPeriodEnd(false);
                    mIDbaManger.modifyPredictionData(datePhysiologyBean);
                }
            }
        }
    }

    @Override
    public void updateFragment() {
        IXThreadPool mIXThreadPool = (IXThreadPool) XLibFactory.getInstance().createInstance(IXThreadPool.class);
        mIXThreadPool.addTask(new IXThreadPoolListener() {
            @Override
            public void onTaskRun() {
                long current = CalendarUtil.getZeroDate(System.currentTimeMillis());
                DatePhysiologyBean afterEnd = mIDbaManger.queryTimebeforeEndData(current);
                if (afterEnd != null) {
                    long endtime = afterEnd.getCurrentDate();
                    DatePhysiologyBean afterStart = mIDbaManger.queryTimeAfterStartData(endtime);
                    if (afterStart == null) {
                        Long l = Long.parseLong(String.valueOf(mIDataMgr.getPhyCycle()));
                        long nextStartTime = mIDbaManger.queryTimebeforeStartData(endtime).getCurrentDate() + 1000 * 3600 * 24 * l;
                        if (nextStartTime <= current) {
                            mIDataMgr.setManualstart(current);
                            PatchPosition(afterEnd.getCurrentDate() + 1000 * 3600 * 24, current - 1000 * 3600 * 24);
                            mIDbaManger.deletePredictionData(current);
                        } else {
                            mIDataMgr.setManualstart(mIDbaManger.queryTimebeforeStartData(endtime).getCurrentDate());
                            PatchPositions(afterEnd.getCurrentDate() + 1000 * 3600 * 24, nextStartTime - 1000 * 3600 * 24, current);
                        }
                    }
                }
            }

            @Override
            public void onTaskComplete() {
                CalendarDialogManger.this.notify(iCalendarListener -> iCalendarListener.toUpdate(CalendarUtil.year, CalendarUtil.month, false));
            }

            @Override
            public void onMessage(Message msg) {

            }
        });

    }

    @Override
    public void HandlingClickEvents(boolean isChecked, int startOrend, long selecttTime, Activity context) {
        //0 设置开始 1 取消开始 2设置结束 3取消结束
        long currentTime = CalendarUtil.getZeroDate(System.currentTimeMillis());
        if (!isChecked) {
            if (startOrend == 0) {
                Log.d("lzd", "时间2  =======" + CalendarUtil.getStringDate(System.currentTimeMillis()));
                DatePhysiologyBean datePhysiologyBean = mIDbaManger.queryTimeAfterStartData(selecttTime);
                if (datePhysiologyBean == null) {
                    Log.d("lzd", "时间3  =======" + CalendarUtil.getStringDate(System.currentTimeMillis()));
                    SettingStart(selecttTime);
                    LogUtils.ThreeFieldLog("calendar", "click_MenstruationStart", "Yes");

                } else {
                    long time = datePhysiologyBean.getCurrentDate();
                    int size = CalendarUtil.daysBetween(selecttTime, time);
                    if (size >= 5 + mIDataMgr.getMenstrualDuration()) {
                        //设置新的开始
                        Log.d("lzd", "时间4  =======" + CalendarUtil.getStringDate(System.currentTimeMillis()));
                        SettingStart(selecttTime);
                        LogUtils.ThreeFieldLog("calendar", "click_MenstruationStart", "Yes");

                    } else {
                        //修改新的开始
                        int sizes = mIDbaManger.queryStateCount(time);
                        if (sizes == mIDataMgr.getPhyCycle() || sizes + CalendarUtil.daysBetween(selecttTime, time) > mIDataMgr.getPhyCycle()) {
                            //整个周期已经全部是经期
                            TipsDialog tipsDialog = new TipsDialog(context, context.getString(R.string.string_tip2));
                            tipsDialog.setOnClickListenerSave(v1 -> {
                                tipsDialog.cancel();
                            });
                            if (!context.isFinishing()) {
                                tipsDialog.show();
                            }

                            CalendarDialogManger.this.notify(iCalendarListener -> iCalendarListener.toUpdate(CalendarUtil.year, CalendarUtil.month, false));
                            LogUtils.ThreeFieldLog("calendar", "click_confirm", "overMenstruationUpperLimit");

                        } else {
                            //修改经期开始
                            java.util.Calendar oldtime = java.util.Calendar.getInstance();
                            oldtime.setTimeInMillis(time);
                            java.util.Calendar selectTime = java.util.Calendar.getInstance();
                            selectTime.setTimeInMillis(selecttTime);
                            SettingPeriodStartDialog mSettingPeriodStartDialog = new SettingPeriodStartDialog(context, context.getString(R.string.string_a) + " " + getMonthName(oldtime.get(java.util.Calendar.MONTH) + 1) + " " + oldtime.get(java.util.Calendar.DAY_OF_MONTH) + " " + context.getString(R.string.string_b) +
                                    context.getString(R.string.string_c) + " " + getMonthName(selectTime.get(java.util.Calendar.MONTH) + 1) + " " + selectTime.get(java.util.Calendar.DAY_OF_MONTH), context.getString(R.string.string_tips));
                            mSettingPeriodStartDialog.setOnClickListenerCancel(v1 -> {
                                CalendarDialogManger.this.notify(iCalendarListener -> iCalendarListener.toUpdate(CalendarUtil.year, CalendarUtil.month, false));
                                mSettingPeriodStartDialog.cancel();
                            });
                            mSettingPeriodStartDialog.setOnClickListenerSave(v1 -> {
                                ChangeStart(selecttTime, time);
                                LogUtils.ThreeFieldLog("calendar", "click_confirm", "menstruationStartAdvance");
                            });
                            mSettingPeriodStartDialog.setCanceledOnTouchOutside(false);
                            if (!context.isFinishing()) {
                                mSettingPeriodStartDialog.show();
                            }


                        }
                    }
                }
            } else if (startOrend == 2) {
                DatePhysiologyBean datePhysiologyBean = mIDbaManger.queryTimeAfterStartData(selecttTime);
                if (datePhysiologyBean != null) {
                    if (CalendarUtil.daysBetween(selecttTime, datePhysiologyBean.getCurrentDate()) <= 5) {
                        TipsDialog tipsDialog = new TipsDialog(context, context.getString(R.string.string_d));
                        tipsDialog.setOnClickListenerSave(v1 -> {
                            tipsDialog.cancel();
                        });
                        if (!context.isFinishing()) {
                            tipsDialog.show();
                        }
                        CalendarDialogManger.this.notify(iCalendarListener -> iCalendarListener.toUpdate(CalendarUtil.year, CalendarUtil.month, false));
                        LogUtils.ThreeFieldLog("calendar", "click_confirm", "menstruationDistance5Days");
                    } else if (mIDbaManger.queryPredictionData(selecttTime).getCurrentState() == 1 && !mIDbaManger.queryPredictionData(selecttTime).getIsPeriodStart()
                            && !mIDbaManger.queryPredictionData(selecttTime).getIsPeriodEnd()) {
                        SettingEnd(selecttTime);
                        LogUtils.ThreeFieldLog("calendar", "click_MenstruationEnd", "Yes");

                    } else if (CalendarUtil.daysBetween(selecttTime, datePhysiologyBean.getCurrentDate()) > 5) {
                        DatePhysiologyBean datePhysiologyBean1 = mIDbaManger.queryTimebeforeStartData(selecttTime);
                        int size = CalendarUtil.daysBetween(datePhysiologyBean1.getCurrentDate(), selecttTime);
                        if (size >= mIDataMgr.getPhyCycle()) {
                            TipsDialog tipsDialog = new TipsDialog(context, context.getString(R.string.string_d));
                            tipsDialog.setOnClickListenerSave(v1 -> {
                                tipsDialog.cancel();
                            });
                            if (!context.isFinishing()) {
                                tipsDialog.show();

                            }
                            CalendarDialogManger.this.notify(iCalendarListener -> iCalendarListener.toUpdate(CalendarUtil.year, CalendarUtil.month, false));

                            LogUtils.ThreeFieldLog("calendar", "click_confirm", "menstruationDistance5Days");

                        } else {
                            SettingEnd(selecttTime);
                            LogUtils.ThreeFieldLog("calendar", "click_MenstruationEnd", "Yes");

                        }
                    }
                } else {
                    DatePhysiologyBean datePhysiologyBean1 = mIDbaManger.queryTimebeforeStartData(selecttTime);
                    int size = CalendarUtil.daysBetween(datePhysiologyBean1.getCurrentDate(), selecttTime);
                    if (size >= mIDataMgr.getPhyCycle()) {
                        TipsDialog tipsDialog = new TipsDialog(context, context.getString(R.string.string_tip2));
                        tipsDialog.setOnClickListenerSave(v1 -> {
                            tipsDialog.cancel();
                        });
                        if (!context.isFinishing()) {
                            tipsDialog.show();

                        }
                        CalendarDialogManger.this.notify(iCalendarListener -> iCalendarListener.toUpdate(CalendarUtil.year, CalendarUtil.month, false));

                        LogUtils.ThreeFieldLog("calendar", "click_confirm", "overMenstruationUpperLimit");

                    } else {
                        Log.d("lzd", "时间5  =======" + CalendarUtil.getStringDate(System.currentTimeMillis()));
                        SettingEnd(selecttTime);
                        LogUtils.ThreeFieldLog("calendar", "click_MenstruationEnd", "Yes");
                    }
                }
            }
        } else {
            if (startOrend == 1) {
                Log.d("lzd", "时间6  =======" + CalendarUtil.getStringDate(System.currentTimeMillis()));
                cancelStart(selecttTime, currentTime);
                LogUtils.ThreeFieldLog("calendar", "click_MenstruationStart", "No");


            } else if (startOrend == 3) {
                DatePhysiologyBean datePhysiologyBean = mIDbaManger.queryTimeAfterStartData(selecttTime);
                if (datePhysiologyBean != null) {
                    TipsDialog tipsDialog = new TipsDialog(context, context.getString(R.string.tip1));
                    tipsDialog.setOnClickListenerSave(v1 -> {
                        tipsDialog.cancel();
                    });
                    if (!context.isFinishing()) {
                        tipsDialog.show();
                    }
                    CalendarDialogManger.this.notify(iCalendarListener -> iCalendarListener.toUpdate(CalendarUtil.year, CalendarUtil.month, false));

                    LogUtils.ThreeFieldLog("calendar", "click_confirm", "removeHistoryMenstruationEnd");


                } else {
                    DatePhysiologyBean beforeStart = mIDbaManger.queryTimebeforeStartData(selecttTime);
                    int size = CalendarUtil.daysBetween(beforeStart.getCurrentDate(), CalendarUtil.getZeroDate(System.currentTimeMillis())) + 1;
                    if (size > mIDataMgr.getPhyCycle()) {
                        TipsDialog tipsDialog = new TipsDialog(context, context.getString(R.string.tip1));
                        tipsDialog.setOnClickListenerSave(v1 -> {
                            tipsDialog.cancel();
                        });
                        if (!context.isFinishing()) {
                            tipsDialog.show();
                        }
                        CalendarDialogManger.this.notify(iCalendarListener -> iCalendarListener.toUpdate(CalendarUtil.year, CalendarUtil.month, false));

                        LogUtils.ThreeFieldLog("calendar", "click_confirm", "removeHistoryMenstruationEnd");

                    } else {
                        Log.d("lzd", "时间7  =======" + CalendarUtil.getStringDate(System.currentTimeMillis()));
                        cancelEnd(selecttTime, currentTime);
                        LogUtils.ThreeFieldLog("calendar", "click_MenstruationEnd", "No");
                    }
                }
            }
        }
//        return 0;
    }

    public static int daysBetween(long smdate, long bdate) {
        long between_days = (bdate - smdate) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    private String getMonthName(int monthh) {
        switch (monthh) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";

        }
        return "Unknown";
    }
}
