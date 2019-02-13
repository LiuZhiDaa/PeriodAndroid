package com.period.app.core.prediction;


import android.util.Log;

import com.haibin.calendarview.Calendar;
import com.period.app.R;
import com.period.app.bean.DatePhysiologyBean;
import com.period.app.constants.PeriodConstant;
import com.period.app.core.XCoreFactory;

import com.period.app.core.calendardialog.ICalendarDialogManger;
import com.period.app.core.data.IDataMgr;
import com.period.app.core.dba.IDbaManger;
import com.period.app.utils.CalendarUtil;
import com.period.app.utils.TimeUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PredictionManger implements IPredictionManger {
    //    private final IDataMgr mgr;
    private final IDbaManger mIDbaManger;
    private final long VALUE_LONG_ONE_DAY_MILLIS = 1000 * 60 * 60 * 24;
    private final long VALUE_LONG_OVULATION_DAYS = 14;
    private final long VALUE_LONG_START_OVULATION_DAYS = 19;
    private final long VALUE_LONG_END_OVULATION_DAYS = 10;


    //生理周期
    private int phyCycle = 28;
    //月经周期
    private int menstrualPeriod = 5;
    long latelyDate;
    //最近经期开始时间

    private IDataMgr mIDataMgr;

    private static final String TAG = "MainFragment";
    private final ICalendarDialogManger mICalendarDialogMgr;
    private long menstrual;

    public PredictionManger() {
//        mgr = (IDataMgr) XCoreFactory.getInstance().createInstance(IDataMgr.class);
        mIDbaManger = (IDbaManger) XCoreFactory.getInstance().createInstance(IDbaManger.class);
        mIDataMgr = (IDataMgr) XCoreFactory.getInstance().createInstance(IDataMgr.class);
        mICalendarDialogMgr = (ICalendarDialogManger) XCoreFactory.getInstance().createInstance(ICalendarDialogManger.class);
        phyCycle = mIDataMgr.getPhyCycle();
        menstrualPeriod = mIDataMgr.getMenstrualDuration();
    }

    @Override
    public Map<String, Calendar> getpredictionMa(int year, int month, long currentTime) {
        phyCycle = mIDataMgr.getPhyCycle();
        menstrualPeriod = mIDataMgr.getMenstrualDuration();
        Map<String, Calendar> map = new HashMap<>();
        int size = TimeUtils.getMonthOfDay(year, month);
        if (mIDataMgr.getManualstart()==1000){
            DatePhysiologyBean nearlyPeriodStimeBean=mIDbaManger.queryMostNearlyPeriodStime();
            if (nearlyPeriodStimeBean!=null){
                latelyDate = nearlyPeriodStimeBean.getCurrentDate();
            }else {
                return map;
            }
        }else {
            latelyDate = mIDataMgr.getManualstart();
        }
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        calendar.set(java.util.Calendar.YEAR, year);
        calendar.set(java.util.Calendar.MONTH, month - 1);
        for (int i = 1; i < size + 1; i++) {
            calendar.set(java.util.Calendar.DAY_OF_MONTH, i);
            if (currentTime > calendar.getTimeInMillis()) {
                DatePhysiologyBean datePhysiologyBean = mIDbaManger.queryPredictionData(calendar.getTimeInMillis());
                if (datePhysiologyBean != null) {
                    if (datePhysiologyBean.getCurrentState() == 0) {
                        map.put(getSchemeCalendar(year, month, i, 0xFFFFFFFF, "安全期").toString(),
                                getSchemeCalendar(year, month, i, 0xFFFFFFFF, "安全期"));
                    } else if (datePhysiologyBean.getCurrentState() == 1) {
                        map.put(getSchemeCalendar(year, month, i, 0xFFFE60A3, "实际经期").toString(),
                                getSchemeCalendar(year, month, i,0xFFFE60A3, "实际经期"));
                    } else if (datePhysiologyBean.getCurrentState() == 2) {
                        map.put(getSchemeCalendar(year, month, i, 0xFFFFFFFF, "安全期").toString(),
                                getSchemeCalendar(year, month, i, 0xFFFFFFFF, "安全期"));
                    } else if (datePhysiologyBean.getCurrentState() == 3) {
                        map.put(getSchemeCalendar(year, month, i, 0xFFFFFFFF, "排卵期").toString(),
                                getSchemeCalendar(year, month, i, 0xFFFFFFFF, "排卵期"));
                    } else if (datePhysiologyBean.getCurrentState() == 4) {
                        map.put(getSchemeCalendar(year, month, i, 0xFFFFFFFF, "排卵日").toString(),
                                getSchemeCalendar(year, month, i, 0xFFFFFFFF, "排卵日"));
                    }
                }
            } else {
                DatePhysiologyBean datePhysiologyBean = mIDbaManger.queryPredictionData(calendar.getTimeInMillis());
                if (datePhysiologyBean != null) {
                    if (datePhysiologyBean.getCurrentState() == 0) {
                        map.put(getSchemeCalendar(year, month, i, 0xFFFFFFFF, "安全期").toString(),
                                getSchemeCalendar(year, month, i, 0xFFFFFFFF, "安全期"));
                    } else if (datePhysiologyBean.getCurrentState() == 1) {
                        map.put(getSchemeCalendar(year, month, i, 0xFFFE60A3, "实际经期").toString(),
                                getSchemeCalendar(year, month, i, 0xFFFE60A3, "实际经期"));
                    } else if (datePhysiologyBean.getCurrentState() == 2) {
                        map.put(getSchemeCalendar(year, month, i, 0xFFFFFFFF, "安全期").toString(),
                                getSchemeCalendar(year, month, i, 0xFFFFFFFF, "安全期"));
                    } else if (datePhysiologyBean.getCurrentState() == 3) {
                        map.put(getSchemeCalendar(year, month, i, 0xFFFFFFFF, "排卵期").toString(),
                                getSchemeCalendar(year, month, i, 0xFFFFFFFF, "排卵期"));
                    } else if (datePhysiologyBean.getCurrentState() == 4) {
                        map.put(getSchemeCalendar(year, month, i, 0xFFFFFFFF, "排卵日").toString(),
                                getSchemeCalendar(year, month, i, 0xFFFFFFFF, "排卵日"));
                    }
                } else {
                    //判断当前日期与最近月经日期间隔是否处于一个生理周期
                    if (TimeUtils.isStaySamePhysiology(calendar.getTimeInMillis(), latelyDate, phyCycle)) {
                        //计算日期是否在月经期内
                        if (TimeUtils.isStayMenstruation(calendar.getTimeInMillis(), latelyDate, menstrualPeriod)) {
                            DatePhysiologyBean afterEnd=mIDbaManger.queryTimeAfterEndData(latelyDate);
                            if (calendar.getTimeInMillis() <= currentTime) {
                                if (afterEnd!=null) {
                                    map.put(getSchemeCalendar(year, month, i, 0xFFFFFFFF, "安全期").toString(),
                                            getSchemeCalendar(year, month, i, 0xFFFFFFFF, "安全期"));
                                } else {
                                    int sizes=CalendarUtil.daysBetween(latelyDate,CalendarUtil.getZeroDate(calendar.getTimeInMillis()));
                                    if (sizes==0){
                                        map.put(getSchemeCalendar(year, month, i, 0xFFffd5ea, "预测经期开始").toString(),
                                                getSchemeCalendar(year, month, i, 0xFFffd5ea, "预测经期开始"));
                                    }else if (sizes>0&&sizes<mIDataMgr.getMenstrualDuration()-1){
                                        map.put(getSchemeCalendar(year, month, i, 0xFFffd5ea, "预测经期").toString(),
                                                getSchemeCalendar(year, month, i, 0xFFffd5ea, "预测经期"));
                                    }else if (sizes==mIDataMgr.getMenstrualDuration()-1){
                                        map.put(getSchemeCalendar(year, month, i, 0xFFffd5ea, "预测经期结束").toString(),
                                                getSchemeCalendar(year, month, i, 0xFFffd5ea, "预测经期结束"));
                                    }

                                }
                            } else {

                                if (afterEnd!=null) {
                                    map.put(getSchemeCalendar(year, month, i, 0xFFFFFFFF, "安全期").toString(),
                                            getSchemeCalendar(year, month, i, 0xFFFFFFFF, "安全期"));
                                } else {
                                    int sizes=CalendarUtil.daysBetween(latelyDate,CalendarUtil.getZeroDate(calendar.getTimeInMillis()));
                                    if (sizes==0){
                                        map.put(getSchemeCalendar(year, month, i, 0xFFffd5ea, "预测经期开始").toString(),
                                                getSchemeCalendar(year, month, i, 0xFFffd5ea, "预测经期开始"));
                                    }else if (sizes>0&&sizes<mIDataMgr.getMenstrualDuration()-1){
                                        map.put(getSchemeCalendar(year, month, i, 0xFFffd5ea, "预测经期").toString(),
                                                getSchemeCalendar(year, month, i, 0xFFffd5ea, "预测经期"));
                                    }else if (sizes==mIDataMgr.getMenstrualDuration()-1){
                                        map.put(getSchemeCalendar(year, month, i, 0xFFffd5ea, "预测经期结束").toString(),
                                                getSchemeCalendar(year, month, i, 0xFFffd5ea, "预测经期结束"));
                                    }
                                }

                            }
                        } else if (TimeUtils.isOvulation(calendar.getTimeInMillis(), latelyDate, phyCycle)) {
                            //计算日期是否是排卵期
                            //计算日期是否是排卵日
                            if (TimeUtils.isOvulationDay(calendar.getTimeInMillis(), latelyDate, phyCycle)) {
                                map.put(getSchemeCalendar(year, month, i, 0xFFFFFFFF, "排卵日").toString(),
                                        getSchemeCalendar(year, month, i, 0xFFFFFFFF, "排卵日"));

                            } else {
                                map.put(getSchemeCalendar(year, month, i, 0xFFFFFFFF, "排卵期").toString(),
                                        getSchemeCalendar(year, month, i, 0xFFFFFFFF, "排卵期"));
                            }
                        } else {
                            map.put(getSchemeCalendar(year, month, i, 0xFFFFFFFF, "安全期").toString(),
                                    getSchemeCalendar(year, month, i, 0xFFFFFFFF, "安全期"));
                        }
                    } else {
                        long NewlyTime = TimeUtils.getNewlyTime(calendar.getTimeInMillis(), latelyDate, phyCycle);
                        //计算日期是否在月经期内
                        if (TimeUtils.isStayMenstruation(calendar.getTimeInMillis(), NewlyTime, menstrualPeriod)) {
                            int sizes=CalendarUtil.daysBetween(NewlyTime,CalendarUtil.getZeroDate(calendar.getTimeInMillis()));
                            if (sizes==0){
                                map.put(getSchemeCalendar(year, month, i, 0xFFffd5ea, "预测经期开始").toString(),
                                        getSchemeCalendar(year, month, i, 0xFFffd5ea, "预测经期开始"));
                            }else if (sizes>0&&sizes<mIDataMgr.getMenstrualDuration()-1){
                                map.put(getSchemeCalendar(year, month, i, 0xFFffd5ea, "预测经期").toString(),
                                        getSchemeCalendar(year, month, i, 0xFFffd5ea, "预测经期"));
                            }else if (sizes==mIDataMgr.getMenstrualDuration()-1){
                                map.put(getSchemeCalendar(year, month, i, 0xFFffd5ea, "预测经期结束").toString(),
                                        getSchemeCalendar(year, month, i, 0xFFffd5ea, "预测经期结束"));
                            }
                        }
                        //计算日期是否是排卵期
                        else if (TimeUtils.isOvulation(calendar.getTimeInMillis(), NewlyTime, phyCycle)) {
                            //计算日期是否是排卵日
                            if (TimeUtils.isOvulationDay(calendar.getTimeInMillis(), NewlyTime, phyCycle)) {
                                map.put(getSchemeCalendar(year, month, i, 0xFFFFFFFF, "排卵日").toString(),
                                        getSchemeCalendar(year, month, i, 0xFFFFFFFF, "排卵日"));
                            } else {
                                map.put(getSchemeCalendar(year, month, i, 0xFFFFFFFF, "排卵期").toString(),
                                        getSchemeCalendar(year, month, i, 0xFFFFFFFF, "排卵期"));
                            }
                        } else {
                            map.put(getSchemeCalendar(year, month, i, 0xFFFFFFFF, "安全期").toString(),
                                    getSchemeCalendar(year, month, i, 0xFFFFFFFF, "安全期"));
                        }
                    }
                }

            }
        }
        return map;
    }

    /**
     * 获取所处的生理周期状态
     *
     * @param currentTime
     * @return
     */
    @Override
    public int getState(long currentTime) {
        currentTime = CalendarUtil.getZeroDate(currentTime);
        //没有保存最近经期时间，返回NO_DATA
        if (mIDbaManger.queryPredictionData(currentTime) == null && mIDataMgr.getRecentMenstrualPeriod() == -1) {
            return PeriodConstant.VALUE_INT_STATE_NO_DATA;
        }

        //获取最近经期时间
        long menstrual = -1;
        DatePhysiologyBean datePhysiologyBeans = mIDbaManger.queryMostNearlyPeriodStime();
        if (datePhysiologyBeans != null) {
            menstrual = datePhysiologyBeans.getCurrentDate();
            long zeroDate = CalendarUtil.getZeroDate(mIDataMgr.getRecentMenstrualPeriod());
            if (zeroDate > menstrual)
                menstrual = zeroDate;
        } else {
            menstrual = mIDataMgr.getRecentMenstrualPeriod();
        }
        if (currentTime < menstrual) {
            menstrual = mIDataMgr.getRecentMenstrualPeriod();
        }
        menstrual = CalendarUtil.getZeroDate(menstrual);
        int phyCycle = mIDataMgr.getPhyCycle();
        int menstrualPeriod = mIDataMgr.getMenstrualDuration();

        //不在同一生理周期
        if (!TimeUtils.isStaySamePhysiology(currentTime, menstrual, phyCycle)) {

            menstrual = TimeUtils.getNewlyTime(currentTime, menstrual, phyCycle);
        }
        //计算日期是否在月经期内
        if (TimeUtils.isStayMenstruation(currentTime, menstrual, menstrualPeriod)) {
            return PeriodConstant.VALUE_INT_STATE_PERIOD;
            //计算日期是否是排卵期
        } else if (TimeUtils.isOvulation(currentTime, menstrual, phyCycle)) {
            //计算日期是否是排卵日
            if (TimeUtils.isOvulationDay(currentTime, menstrual, phyCycle)) {
                return PeriodConstant.VALUE_INT_STATE_OVULATION_DAY;
            } else {
                return PeriodConstant.VALUE_INT_STATE_OVULATION;
            }
        } else {
            return PeriodConstant.VALUE_INT_STATE_SAFETY;
        }
    }


    /**
     * 获取在经期第几天
     *
     * @param currentTime 当前时间
     * @return
     */
    @Override
    public int getDayInMenstrual(long currentTime) {
        currentTime = CalendarUtil.getZeroDate(currentTime);
        DatePhysiologyBean datePhysiologyBean = mIDbaManger.queryTimebeforeStartData(currentTime);
        if (datePhysiologyBean != null) {
            long spTime = mIDataMgr.getRecentMenstrualPeriod();
            long dbaTime = datePhysiologyBean.getCurrentDate();
            if (spTime > dbaTime) {
                if (spTime > currentTime) {
                    menstrual = dbaTime;
                } else {
                    menstrual = spTime;
                }
            } else {
                menstrual = dbaTime;
            }

        } else {
            menstrual = mIDataMgr.getRecentMenstrualPeriod();
        }

        int phyCycle = mIDataMgr.getPhyCycle();

        //不在同一生理周期
        if (!TimeUtils.isStaySamePhysiology(currentTime, menstrual, phyCycle)) {
            menstrual = TimeUtils.getNewlyTime(currentTime, menstrual, phyCycle);
        }
        int between_days = (int) ((currentTime - menstrual) / VALUE_LONG_ONE_DAY_MILLIS);
        return Math.abs(between_days);
    }

    @Override
    public int getDayInOvulation(long currentTime) {
        currentTime = CalendarUtil.getZeroDate(currentTime);
        long menstrualTime = getRecentMenstrualTime();
        int phyCycle = mIDataMgr.getPhyCycle();

        //不在同一生理周期
        if (!TimeUtils.isStaySamePhysiology(currentTime, menstrualTime, phyCycle)) {
            menstrualTime = TimeUtils.getNewlyTime(currentTime, menstrualTime, phyCycle);
        }

        long between_days = currentTime - (menstrualTime + (phyCycle - 19) * VALUE_LONG_ONE_DAY_MILLIS);
        return Math.abs((int) (between_days / VALUE_LONG_ONE_DAY_MILLIS));
    }

    @Override
    public int getDayInSafety(long currentTime) {
        currentTime = CalendarUtil.getZeroDate(currentTime);
        long menstrualTime = getRecentMenstrualTime();
        int phyCycle = mIDataMgr.getPhyCycle();
        int menDuration = mIDataMgr.getMenstrualDuration();

//        long nextMenTime = getNextMStartTime();
        //不在同一生理周期
        if (!TimeUtils.isStaySamePhysiology(currentTime, menstrualTime, phyCycle)) {
            menstrualTime = TimeUtils.getNewlyTime(currentTime, menstrualTime, phyCycle);
        }
        long menstrualETime = menstrualTime + menDuration * VALUE_LONG_ONE_DAY_MILLIS;
        long startOvuTime = menstrualTime + (phyCycle - 19) * VALUE_LONG_ONE_DAY_MILLIS;
        long endOvuTime = menstrualTime + (phyCycle - 10) * VALUE_LONG_ONE_DAY_MILLIS;


        if (currentTime <= startOvuTime) {
//            long between_days=currentTime-(menstrualTime+(phyCycle-14)*VALUE_LONG_ONE_DAY_MILLIS);
            long between_days = (currentTime - menstrualETime) / VALUE_LONG_ONE_DAY_MILLIS;
            return Math.abs((int) (between_days));
        } else if (currentTime >= endOvuTime) {
            long after_between_days = (currentTime - endOvuTime - 1) / VALUE_LONG_ONE_DAY_MILLIS;
            return Math.abs((int) (after_between_days));
        }
        return 0;
    }

    /**
     * 是否是预测经期
     *
     * @return true:预测经期，界面显示forecast  false:实际经期，界面隐藏forecast
     */
    @Override
    public boolean isForecastPeriodState() {
        DatePhysiologyBean datePhysiologyBeans = mIDbaManger.queryMostNearlyPeriodStime();
        if (datePhysiologyBeans != null) {
            if (1 == datePhysiologyBeans.getCurrentState())
                return false;
        }
        return true;
    }


    @Override
    public int getDialog(long time) {
        return 0;
    }


    /**
     * 下次月经开始时间
     *
     * @return
     */
    @Override
    public long getNextMStartTime(long currentTime, int hour, int min) {
        long currTime = currentTime;
        currentTime = CalendarUtil.getZeroDate(currentTime);                                                        //初始化后的当前时间
        long recentMenstrualPeriod = getRecentMenstrualTime();                                        //最近的经期时间
        int phyCycle = mIDataMgr.getPhyCycle();                                                                   //生理期的时长

        //在同一个生理周期内,说明经期已经开始，则返回下一个经期的开始日期，否则根据当前的时间获取最近的经期时间
        //如果不在同一个生理周期，根据currentTime可直接获取最近的经期时间
        if (TimeUtils.isStaySamePhysiology(currentTime, recentMenstrualPeriod, phyCycle)) {
            //判断当前是否大于最近开始时间
            //如果大于，说明已经超过了开始时间，则获取下周期的开始时间。
            //如果等于，则判断小时和分钟是否大于当前时间，
            //如果设置的时间时分秒大于当前时间的时分秒，则获取当前周期的开始时间，否则，获取下一周期的开始时间
            if (currentTime > recentMenstrualPeriod) {
                currentTime = currentTime + phyCycle * VALUE_LONG_ONE_DAY_MILLIS;
            } else if (currentTime == recentMenstrualPeriod) {
                long settingTime = CalendarUtil.getTimeForMillis(currentTime, hour, min);
                if (currTime < settingTime) {
                    currentTime = CalendarUtil.getTimeForMillis(currentTime, hour, min);
                } else {
                    currentTime = currentTime + phyCycle * VALUE_LONG_ONE_DAY_MILLIS;
                }
            }
        } else {
            long nearlySTime = TimeUtils.getNewlyTime(currentTime, recentMenstrualPeriod, phyCycle);
            if (currentTime > nearlySTime) {
                currentTime = currentTime + phyCycle * VALUE_LONG_ONE_DAY_MILLIS;
            } else if (currentTime == nearlySTime) {
                currentTime = CalendarUtil.getTimeForMillis(currentTime, hour, min);
            }
        }
        long nextMStartTime = TimeUtils.getNewlyTime(currentTime, recentMenstrualPeriod, phyCycle);
        return nextMStartTime;
    }

    /**
     * 下次月经结束时间
     *
     * @return
     */
    @Override
    public long getNextMEndTime(long currentTime, int hour, int min) {
        long currTime = currentTime;
        currentTime = CalendarUtil.getZeroDate(currentTime);                                                        //初始化后的当前时间
        long menstrualPeriodTime = (mIDataMgr.getMenstrualDuration() - 1) * VALUE_LONG_ONE_DAY_MILLIS;              //经期时间
        long recentMenstrualPeriod = getRecentMenstrualTime();                                        //最近的经期时间
        int menDuration = mIDataMgr.getMenstrualDuration();                                                       //经期的时长
        int phyCycle = mIDataMgr.getPhyCycle();                                                                   //生理期的时长

        long longBetweenDays = (currentTime - recentMenstrualPeriod) / VALUE_LONG_ONE_DAY_MILLIS;
        int betweenDays = Integer.parseInt(String.valueOf(longBetweenDays));                                        //与最近经期相差的天数
        long nextMendTime = 0;
        if (TimeUtils.isStaySamePhysiology(currentTime, recentMenstrualPeriod, phyCycle)) {
            long endTime = recentMenstrualPeriod + ((menDuration - 1) * 24 * 60 * 60 * 1000);
            if (currentTime > endTime) {
                nextMendTime = getNextMStartTime(currTime, hour, min) + ((menDuration - 1) * 24 * 60 * 60 * 1000);
            } else if (currentTime == endTime) {
                long endTimeForHM = CalendarUtil.getTimeForMillis(endTime, hour, min);
                if (currTime > endTimeForHM) {
                    nextMendTime = getNextMStartTime(currTime, hour, min) + ((menDuration - 1) * 24 * 60 * 60 * 1000);
                } else if (currTime <= endTimeForHM) {
                    nextMendTime = endTime;
                }
            } else if (currentTime < endTime) {
                nextMendTime = endTime;
            }
        } else {
            long nearlySTime = TimeUtils.getNewlyTime(currentTime, recentMenstrualPeriod, phyCycle);
            long endTime = nearlySTime + ((menDuration - 1) * 24 * 60 * 60 * 1000);
            if (currentTime > endTime) {
                nextMendTime = getNextMStartTime(currTime, hour, min) + ((menDuration - 1) * 24 * 60 * 60 * 1000);
            } else if (currentTime == endTime) {
                nextMendTime = endTime;
            } else if (currentTime < endTime) {
                nextMendTime = endTime;
            }

        }

        return nextMendTime;
    }

    /**
     * 下次排卵期开始时间
     *
     * @return
     */
    @Override
    public long getNextOvulationStartTime(long currentTime, int hour, int min) {
        long currTime = currentTime;
        currentTime = CalendarUtil.getZeroDate(currentTime);                                                        //初始化后的当前时间
        int phyCycle = mIDataMgr.getPhyCycle();                                                                   //生理期的时长

        //无论在不在同一个生理周期内，都直接获取下一个月经期的开始时间
        long nextMStartTime = getNextMStartTime(currTime, hour, min);
        long currentOvulationSTime = nextMStartTime - VALUE_LONG_START_OVULATION_DAYS * VALUE_LONG_ONE_DAY_MILLIS;
        long menstrualPeriodTime = (mIDataMgr.getMenstrualDuration() - 1) * VALUE_LONG_ONE_DAY_MILLIS;              //经期时间
        long recentMenstrualPeriod = mIDataMgr.getRecentMenstrualPeriod();                                        //最近的经期时间
        long ovulationTime = (phyCycle - VALUE_LONG_START_OVULATION_DAYS) * VALUE_LONG_ONE_DAY_MILLIS;
        long nextOvulationSTime = nextMStartTime + ovulationTime;

        currentOvulationSTime = CalendarUtil.getTimeForMillis(currentOvulationSTime, hour, min); // 根据设置的时间，返回当前周期的日-时-分
        //当前时间在本周期的排卵日之前，说明排卵日还没到，直接返回本周期的排卵日
        if (currTime < currentOvulationSTime) {
            return currentOvulationSTime;
        } else {
            //否则说明已经超过了本周期的排卵日，返回下周期的排卵日期
            return nextOvulationSTime;
        }
    }

    /**
     * 下次排卵期结束时间
     *
     * @return
     */
    @Override
    public long getNextOvulationEndTime(long currentTime, int hour, int min) {
        long currTime = currentTime;
        currentTime = CalendarUtil.getZeroDate(currentTime);                                                        //初始化后的当前时间
        int phyCycle = mIDataMgr.getPhyCycle();                                                                   //生理期的时长

        //无论在不在同一个生理周期内，都直接获取下一个月经期的开始时间
        long nextMStartTime = getNextMStartTime(currTime, hour, min);
        long currentOvulationETime = nextMStartTime - VALUE_LONG_END_OVULATION_DAYS * VALUE_LONG_ONE_DAY_MILLIS;
        long menstrualPeriodTime = (mIDataMgr.getMenstrualDuration() - 1) * VALUE_LONG_ONE_DAY_MILLIS;              //经期时间
        long recentMenstrualPeriod = mIDataMgr.getRecentMenstrualPeriod();                                        //最近的经期时间
        long ovulationTime = (phyCycle - VALUE_LONG_END_OVULATION_DAYS) * VALUE_LONG_ONE_DAY_MILLIS;
        long nextOvulationETime = nextMStartTime + ovulationTime;

        currentOvulationETime = CalendarUtil.getTimeForMillis(currentOvulationETime, hour, min); // 根据设置的时间，返回当前周期的日-时-分
        //当前时间在本周期的排卵日之前，说明排卵日还没到，直接返回本周期的排卵日
        if (currTime < currentOvulationETime) {
            return currentOvulationETime;
        } else {
            //否则说明已经超过了本周期的排卵日，返回下周期的排卵日期
            return nextOvulationETime;
        }
    }

    /**
     * 下次排卵日时间
     *
     * @return
     */
    @Override
    public long getNextOvulationDayTime(long currentTime, int hour, int min) {
        long currTime = currentTime;
        currentTime = CalendarUtil.getZeroDate(currentTime);                                                        //初始化后的当前时间
        int phyCycle = mIDataMgr.getPhyCycle();                                                                   //生理期的时长

        //无论在不在同一个生理周期内，都直接获取下一个月经期的开始时间
        long nextMStartTime = getNextMStartTime(currTime, hour, min);
        long menstrualPeriodTime = (mIDataMgr.getMenstrualDuration() - 1) * VALUE_LONG_ONE_DAY_MILLIS;              //经期时间
        long recentMenstrualPeriod = getRecentMenstrualTime();                                        //最近的经期时间
        long currentOvulationTime = nextMStartTime - VALUE_LONG_OVULATION_DAYS * VALUE_LONG_ONE_DAY_MILLIS;               //当前周期的排卵日期
        long ovulationTime = (phyCycle - VALUE_LONG_OVULATION_DAYS) * VALUE_LONG_ONE_DAY_MILLIS;
        long nextOvulationTime = nextMStartTime + ovulationTime;//下一个周期的排卵日期

        currentOvulationTime = CalendarUtil.getTimeForMillis(currentOvulationTime, hour, min); // 根据设置的时间，返回当前周期的日-时-分
        //当前时间在本周期的排卵日之前，说明排卵日还没到，直接返回本周期的排卵日
        if (currTime < currentOvulationTime) {
            return currentOvulationTime;
        } else {
            //否则说明已经超过了本周期的排卵日，返回下周期的排卵日期
            return nextOvulationTime;
        }
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }

    @Override
    public long getRecentMenstrualTime() {
        long menstrual = -1;
        DatePhysiologyBean datePhysiologyBeans = mIDbaManger.queryMostNearlyPeriodStime();
        if (datePhysiologyBeans != null) {
            menstrual = datePhysiologyBeans.getCurrentDate();
            long zeroDate = CalendarUtil.getZeroDate(mIDataMgr.getRecentMenstrualPeriod());
            if (zeroDate > menstrual)
                menstrual = zeroDate;
        } else {
            menstrual = mIDataMgr.getRecentMenstrualPeriod();
        }
        menstrual = CalendarUtil.getZeroDate(menstrual);
        return menstrual;
    }

    /**
     * @param menstrualTime 设置的经期开始日
     * @param endTime
     */
    @Override
    public void savePredictDateList(long menstrualTime, long endTime) {
        menstrualTime = CalendarUtil.getZeroDate(menstrualTime);
        long currentTime = CalendarUtil.getZeroDate(endTime);

        //中间间隔的天数
        int bettweenDays = TimeUtils.daysBetween(menstrualTime, currentTime);
        //生理周期长度
        int phyCycle = mIDataMgr.getPhyCycle();
        //经期周期长度
        int peridDuration = mIDataMgr.getMenstrualDuration();
        //有几圈儿
        int circle = bettweenDays / phyCycle;


        for (long i = 0; i <= bettweenDays; i++) {
            long time = menstrualTime + i * 1000 * 60 * 60 * 24;
            int state = getState(time);
            if (state == PeriodConstant.VALUE_INT_STATE_PERIOD) {
                int dayInPerid = Math.abs(mIDbaManger.querySameStateCountBeforeCurrent(time, 1));
                int periodDuration = mIDataMgr.getMenstrualDuration();
                DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                datePhysiologyBean.setCurrentDate(time);

                datePhysiologyBean.setMIndexofPeriod(dayInPerid);
                //经期开始日
                if (dayInPerid == 0) {
                    boolean isHistory = CalendarUtil.getZeroDate(time) < CalendarUtil.getZeroDate(System.currentTimeMillis());
                    if (i == bettweenDays && circle > 0 && isHistory) {
                        datePhysiologyBean.setCurrentState(2);
                        datePhysiologyBean.setIsPeriodStart(false);
                        datePhysiologyBean.setIsPeriodEnd(false);
                    } else {
                        mIDataMgr.setManualstart(CalendarUtil.getZeroDate(time));
//                        mIDataMgr.setRecentMenstrualPeriod(CalendarUtil.getZeroDate(time));
                        datePhysiologyBean.setIsPeriodStart(true);
                        datePhysiologyBean.setIsPeriodEnd(false);
                        boolean isFuture = CalendarUtil.getZeroDate(time) > CalendarUtil.getZeroDate(System.currentTimeMillis());
                        if (isFuture) {
                            datePhysiologyBean.setCurrentState(0);
                        } else {
                            datePhysiologyBean.setCurrentState(1);
                        }
                    }
                } else if (dayInPerid == periodDuration - 1 || ((CalendarUtil.getZeroDate(endTime) < CalendarUtil.getZeroDate(System.currentTimeMillis()) && i == bettweenDays))) {
                    mIDataMgr.setManualstart(1000);
//                    mIDataMgr.setRecentMenstrualPeriod(-1);
                    datePhysiologyBean.setIsPeriodStart(false);
                    datePhysiologyBean.setIsPeriodEnd(true);
                    boolean isFuture = CalendarUtil.getZeroDate(time) > CalendarUtil.getZeroDate(System.currentTimeMillis());
                    if (isFuture) {
                        datePhysiologyBean.setCurrentState(0);
                    } else {
                        datePhysiologyBean.setCurrentState(1);
                    }
                } else {
                    boolean isFuture = CalendarUtil.getZeroDate(time) > CalendarUtil.getZeroDate(System.currentTimeMillis());
                    if (isFuture) {
                        datePhysiologyBean.setCurrentState(0);
                    } else {
                        datePhysiologyBean.setCurrentState(1);
                    }
                }
//                Log.e("MainFragment","save Predict DateList ...................bettweenDays = "+bettweenDays+".............phyCycle*circle="+(phyCycle*circle)+"....i="+i);
//                //如果多出一天的经期，则设置为安全期
//                if(i==bettweenDays&&CalendarUtil.getZeroDate(time)<CalendarUtil.getZeroDate(System.currentTimeMillis())){
//                    datePhysiologyBean.setCurrentState(2);
//                    Log.e("MainFragment","save Predict DateList ..............................bettweenDays="+bettweenDays+".....phyCycle*circle="+(phyCycle*circle)+"....i="+i);
//                    Log.e("MainFragment","save Predict DateList ..............................add safty state in period state");
//                }

                mIDbaManger.modifyPredictionData(datePhysiologyBean);
            } else if (state == PeriodConstant.VALUE_INT_STATE_OVULATION_DAY) {
                DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                datePhysiologyBean.setCurrentDate(time);
                datePhysiologyBean.setCurrentState(4);
                mIDbaManger.modifyPredictionData(datePhysiologyBean);
            } else if (state == PeriodConstant.VALUE_INT_STATE_SAFETY) {
//                int dayInSafety = getDayInSafety(time);
                int dayInSafety = Math.abs(mIDbaManger.querySameStateCountBeforeCurrent(time, 2));
                DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                datePhysiologyBean.setCurrentDate(time);
                datePhysiologyBean.setCurrentState(2);
                datePhysiologyBean.setMIndexofPeriod(dayInSafety);
                mIDbaManger.modifyPredictionData(datePhysiologyBean);
            } else if (state == PeriodConstant.VALUE_INT_STATE_OVULATION) {
//                int dayInOvu = getDayInOvulation(time);
                int dayInOvu = Math.abs(mIDbaManger.querySameStateCountBeforeCurrent(time, 3));
                DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                datePhysiologyBean.setCurrentDate(time);
                datePhysiologyBean.setCurrentState(3);
                datePhysiologyBean.setMIndexofPeriod(dayInOvu);
                mIDbaManger.modifyPredictionData(datePhysiologyBean);
            }
        }
    }

    /**
     * 补足历史数据
     *
     * @param startTime 数据的开始时间
     * @param endTime   数据结束的时间
     */
    @Override
    public void saveHistoryDateList(long startTime, long endTime) {
        startTime = CalendarUtil.getZeroDate(startTime);
        endTime = CalendarUtil.getZeroDate(endTime);

        //中间间隔的天数
        int bettweenDays = TimeUtils.daysBetween(startTime, endTime);
        //生理周期长度
        int phyCycle = mIDataMgr.getPhyCycle();
        //有几圈儿
        int circle = bettweenDays / phyCycle;

        for (long i = 0; i <= bettweenDays; i++) {
            long time = startTime + i * 1000 * 60 * 60 * 24;
            int state = getState(time);
            if (state == PeriodConstant.VALUE_INT_STATE_PERIOD) {
//                int dayInPerid = getDayInMenstrual(time);
                int dayInPerid = Math.abs(mIDbaManger.querySameStateCountBeforeCurrent(time, 1));
                int periodDuration = mIDataMgr.getMenstrualDuration();
                DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                datePhysiologyBean.setCurrentDate(time);

                if (i == bettweenDays && (CalendarUtil.getZeroDate(startTime) != CalendarUtil.getZeroDate(System.currentTimeMillis()))) {
                    datePhysiologyBean.setCurrentState(0);
                } else {
                    datePhysiologyBean.setCurrentState(1);
                }
                datePhysiologyBean.setMIndexofPeriod(dayInPerid);
                if (dayInPerid == 0) {
                    mIDataMgr.setManualstart(time);
                    datePhysiologyBean.setIsPeriodStart(true);
                    datePhysiologyBean.setIsPeriodEnd(false);
                } else if (dayInPerid == periodDuration - 1 || ((CalendarUtil.getZeroDate(endTime) < CalendarUtil.getZeroDate(System.currentTimeMillis()) && i == bettweenDays))) {
                    mIDataMgr.setManualstart(1000);
                    datePhysiologyBean.setIsPeriodStart(false);
                    datePhysiologyBean.setIsPeriodEnd(true);
                }
                mIDbaManger.modifyPredictionData(datePhysiologyBean);
            } else if (state == PeriodConstant.VALUE_INT_STATE_OVULATION_DAY) {
                DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                datePhysiologyBean.setCurrentDate(time);
                datePhysiologyBean.setCurrentState(4);
                mIDbaManger.modifyPredictionData(datePhysiologyBean);
            } else if (state == PeriodConstant.VALUE_INT_STATE_SAFETY) {
                int dayInSafety = getDayInSafety(time);
                DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                datePhysiologyBean.setCurrentDate(time);
                datePhysiologyBean.setCurrentState(2);
                datePhysiologyBean.setMIndexofPeriod(dayInSafety);
                mIDbaManger.modifyPredictionData(datePhysiologyBean);
            } else if (state == PeriodConstant.VALUE_INT_STATE_OVULATION) {
                int dayInOvu = getDayInOvulation(time);
                DatePhysiologyBean datePhysiologyBean = new DatePhysiologyBean();
                datePhysiologyBean.setCurrentDate(time);
                datePhysiologyBean.setCurrentState(3);
                datePhysiologyBean.setMIndexofPeriod(dayInOvu);
                mIDbaManger.modifyPredictionData(datePhysiologyBean);
            }
        }
    }
}
