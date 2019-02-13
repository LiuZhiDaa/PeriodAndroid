package com.period.app.core.prediction;

import com.haibin.calendarview.Calendar;

import java.util.Map;

import ulric.li.xlib.intf.IXManager;

public interface IPredictionManger extends IXManager {
    Map<String, Calendar> getpredictionMa(int year, int month,long currentTime);
    int getDialog(long time);

    /**
     * 获取当前状态
     * @param currentTime 当前时间
     * @return 当前所处的状态
     */
    int getState(long currentTime);

    /**
     * 获取在经期第几天
     * @return
     */
    int getDayInMenstrual(long currentTime);


    /**
     * 获取在排卵期的第几天
     * @param currentTime
     * @return
     */
    int getDayInOvulation(long currentTime);

    /**
     * 获取在安全期的第几天
     * @param currentTime
     * @return
     */
    int getDayInSafety(long currentTime);

    /**是否是预测经期**
     * @return
     */
    boolean isForecastPeriodState();

    /**
     * 获取最近经期时间
     * @return
     */
    long getRecentMenstrualTime();

    /**
     * 保存预测经期的数据
     * @param startTime
     */
    void savePredictDateList(long startTime,long endTime);

    /**
     * 保存历史数据
     * @param startTime
     * @param endTime
     */
    void saveHistoryDateList(long startTime,long endTime);

    /**
     * time时间时的下次月经结束时间
     * @param currentTime
     * @return
     */
    long getNextMStartTime(long currentTime, int hour, int min);


    /**
     * 下次月经 结束时间
     * @return
     */
    long getNextMEndTime(long currentTime, int hour, int min);

    /**
     * 下次排卵期开始时间
     * @param currentTime
     * @return
     */
    long getNextOvulationStartTime(long currentTime, int hour, int min);

    /**
     * 下次排卵期结束时间
     * @param currentTime
     * @return
     */
    long getNextOvulationEndTime(long currentTime, int hour, int min);

    /**
     * 下次排卵日时间
     * @param currentTime
     * @return
     */
    long getNextOvulationDayTime(long currentTime, int hour, int min);


}
