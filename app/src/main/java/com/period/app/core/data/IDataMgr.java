package com.period.app.core.data;

import java.util.Date;
import java.util.List;

import ulric.li.xlib.intf.IXManager;
import ulric.li.xlib.intf.IXObserver;

/**
 * Created by WangGuiLi
 * on 2018/12/17
 */
public interface IDataMgr extends IXManager,IXObserver<IDataListener> {



    List<String> getPhyCycleList();
    List<String> getMensPeriodList();

    int getDefaultPhyCycleIndex();
    int getDefaultMenPeriodDurationIndex();

    /**
     * 生理周期、月经期、最近生理周期三个信息是否填写完整
     * @param isInfoComplete
     */
    void setIsInfoComplete(boolean isInfoComplete);
    boolean getIsInfoComplete();

    /**
     * 获取生理期时间
     * @return
     */
    int getPhyCycle();

    /**
     * 设置生理期时间
     * @param phyCycle
     */
    void setPhyCycle(String phyCycle);


    /**
     * 获取月经时间
     * @return
     */
    int getMenstrualDuration();

    /**
     * 设置月经时间
     * @param menstrualPeriod
     */
    void setMenstrualDuration(String menstrualPeriod);

    /**
     * 设置最近月经时间
     * @param menPeriod
     */
    void setRecentMenstrualPeriod(long menPeriod);

    /**
     * 获取最近月经时间
     */
    long getRecentMenstrualPeriod();

    /**
     * 记录首次打开时间
     */
    void setFirstOpenTime(long time);

    long getFirstOpenTime();

    /**
     * 手动开始记录经期开始
     */
    void setManualstart(long time);

    long getManualstart();


    /***
     * 记录每次打开时间
     */
    void  setEveryTime(long time);
    long  getEveryTime();

    /**
     * 保存打开首界面时的时间
     */
    void setOpenMainFragmentTime(long time);
    long getOpenMainFragmentTime();


    /**
     * 记录每次收到广播时间
     */

    void  setEveryReceiverTime(long time);
    long  getEveryReceiverTime();

    /**
     * 获取某个时间段所处的状态
     * @param time
     * @return
     */
    int getState(long time);

    /**
     * 安全期是否处于第一阶段
     * @param time
     * @return
     */
    boolean isSafetyInFirstStage(long time);
}
