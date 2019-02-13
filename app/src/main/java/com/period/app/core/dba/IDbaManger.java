package com.period.app.core.dba;

import com.period.app.bean.DatePhysiologyBean;

import ulric.li.xlib.intf.IXManager;

public interface IDbaManger extends IXManager {

    void addPredictionData(DatePhysiologyBean datePhysiologyBean);

    void deletePredictionData(long date);

    void modifyPredictionData(DatePhysiologyBean datePhysiologyBean);
    DatePhysiologyBean queryMostNearlyTime();

    DatePhysiologyBean queryPredictionData(long date);

    DatePhysiologyBean queryMostNearlyPeriodStime();

    //查询某一特点时间之前最近的经期结束时间

    DatePhysiologyBean queryTimebeforeEndData(long date);

    //查询某一特点时间之前最近的经期开始时间

    DatePhysiologyBean queryTimebeforeStartData(long date);


    //查询某一特定时间之后最近的经期开始时间

    DatePhysiologyBean queryTimeAfterStartData(long date);

    //查询某一特定时间之后最近的经期结束时间

    DatePhysiologyBean queryTimeAfterEndData(long date);


    /**
     * 查询相同周期的经期开始时间
     * @param date
     * @return
     */
//    DatePhysiologyBean queryTimeSameCycleStartData(long date);

    /**
     * 查询相同周期的经期结束时间
     * @param date
     * @return
     */
//    DatePhysiologyBean queryTimeSameCycleEndData(long date);

    /**
     * 在数据库中该对象是当前状态的第几天
     * @param date     时间
     * @param state    时间为date时所处的状态
     * @return
     */
    int querySameStateCountBeforeCurrent(long date,int state);


    /**
     * 查询首页时间在数据库中为当前状态的第几天
     * @param date
     * @param state
     * @return
     */
    int querySameStateCountInMainDay(long date,int state);

    int queryStateCount(long date);

    //补充数据
    void supplyData();



}
