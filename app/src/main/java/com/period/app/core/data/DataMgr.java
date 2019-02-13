package com.period.app.core.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.period.app.R;
import com.period.app.bean.DatePhysiologyBean;
import com.period.app.constants.PeriodConstant;
import com.period.app.core.XCoreFactory;
import com.period.app.core.dba.IDbaManger;
import com.period.app.utils.CalendarUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ulric.li.xlib.impl.XObserver;

/**
 * Created by WangGuiLi
 * on 2018/12/17
 */
public class DataMgr extends XObserver<IDataListener> implements IDataMgr {

    public static final String VALUE_STRING_IS_INFO_COMPLETE = "is_info_complete";
    public static final String VALUE_STRING_PHY_CYCLE = "phy_cycle";
    public static final String VALUE_STRING_MENSTRAL_PERIOD = "menstrual_period";
    public static final String VALUE_STRING_RECENT_MENSTRAL_PERIOD = "recent_menstrual_period";
    public static final String VALUE_IS_FIIRST_OPEN="is_first_open";
    public static final String VALUE_IS_FIIRST_TIME="is_first_time";

    public static final String VALUE_EVERY_OPEN_TIME="every_open_time";

    public static final String VALUE_MANUAL_PERIOD_START="manual_period_start";

    public static final String VALUE_OPEN_MAIN_FRAGMENT_TIME="open_main_fragment_time";

    public static final String VALUE_EVERY_RECEIVER_TIME="every_recevier_time";

    private final int DEFAULT_PHY_CYCLE_DURATION = 28;
    private final int DEFAULT_MEN_PERIOD_DURATION = 5;


    private SharedPreferences sp;

    private Context mContext;
    private List<String> mPhyCycleList;
    private List<String> mMensPeriodList;
    private final IDbaManger mIDbaMgr;

    public DataMgr() {
        mContext = XCoreFactory.getApplication();
        sp = PreferenceManager.getDefaultSharedPreferences(XCoreFactory.getApplication());
        mIDbaMgr = (IDbaManger) XCoreFactory.getInstance().createInstance(IDbaManger.class);
    }

    @Override
    public List<String> getPhyCycleList() {
        if (mPhyCycleList == null) {
            mPhyCycleList = new ArrayList<>();
            for (int i = 21; i <= 100; i++) {
                mPhyCycleList.add(i + " " + mContext.getResources().getString(R.string.days));
            }
        }
        return mPhyCycleList;
    }

    @Override
    public List<String> getMensPeriodList() {
        if (mMensPeriodList==null) {
            mMensPeriodList = new ArrayList<>();
            for (int i = 2; i <= 14; i++) {
                mMensPeriodList.add(i+ " " + mContext.getResources().getString(R.string.days));
            }
        }
        return mMensPeriodList;
    }

    @Override
    public int getDefaultPhyCycleIndex(){
        int index = getPhyCycle();
        return index-21;
    }

    @Override
    public int getDefaultMenPeriodDurationIndex(){
        int index = getMenstrualDuration();
        return index-2;
    }

    @Override
    public void setIsInfoComplete(boolean isInfoComplete) {
        sp.edit().putBoolean(VALUE_STRING_IS_INFO_COMPLETE,isInfoComplete).apply();
    }

    @Override
    public boolean getIsInfoComplete() {
        return sp.getBoolean(VALUE_STRING_IS_INFO_COMPLETE,false);
    }


    /**
     * 获取生理期时间
     *
     * @return
     */
    @Override
    public int getPhyCycle() {
        int phyCycle = sp.getInt(VALUE_STRING_PHY_CYCLE, DEFAULT_PHY_CYCLE_DURATION);
        return phyCycle;
    }

    /**
     * 设置生理期时间
     *
     * @param phyCycle
     */
    @Override
    public void setPhyCycle(String phyCycle) {
        String[] strings = phyCycle.split(" ");
        sp.edit().putInt(VALUE_STRING_PHY_CYCLE, Integer.parseInt(strings[0])).apply();
    }


    /**
     * 获取月经周期
     *
     * @return
     */
    @Override
    public int getMenstrualDuration() {
        int menPeriod = sp.getInt(VALUE_STRING_MENSTRAL_PERIOD, DEFAULT_MEN_PERIOD_DURATION);
        return menPeriod;
    }

    /**
     * 设置月经周期
     *
     * @param menstrualDuration 月经周期
     */
    @Override
    public void setMenstrualDuration(String menstrualDuration) {
        String[] strings = menstrualDuration.split(" ");
        sp.edit().putInt(VALUE_STRING_MENSTRAL_PERIOD, Integer.parseInt(strings[0])).apply();
    }


    /**
     * 设置最近月经时间
     *
     * @param menPeriod
     */
    @Override
    public void setRecentMenstrualPeriod(long menPeriod) {
        if(menPeriod!=-1)
            menPeriod = CalendarUtil.getZeroDate(menPeriod);

        sp.edit().putLong(VALUE_STRING_RECENT_MENSTRAL_PERIOD,menPeriod).apply();
    }


    /**
     * 获取最近月经时间
     */
    @Override
    public long getRecentMenstrualPeriod() {
        return sp.getLong(VALUE_STRING_RECENT_MENSTRAL_PERIOD,-1);
    }

    @Override
    public void setFirstOpenTime(long time) {
        sp.edit().putLong(VALUE_IS_FIIRST_TIME,time).apply();
    }

    @Override
    public long getFirstOpenTime() {
        return sp.getLong(VALUE_IS_FIIRST_TIME,1000);
    }



    @Override
    public void setManualstart(long time) {
        sp.edit().putLong(VALUE_MANUAL_PERIOD_START,time).apply();
    }

    /**
     * 值为1000说明当前周期结束了，否则设置的是当前周期开始的时间
     */
    @Override
    public long getManualstart() {
        return sp.getLong(VALUE_MANUAL_PERIOD_START,1000);
    }

    @Override
    public void setEveryTime(long time) {

        sp.edit().putLong(VALUE_EVERY_OPEN_TIME,time).apply();
    }

    @Override
    public long getEveryTime() {
        return sp.getLong(VALUE_EVERY_OPEN_TIME,1000);
    }

    @Override
    public void setOpenMainFragmentTime(long time) {
        sp.edit().putLong(VALUE_OPEN_MAIN_FRAGMENT_TIME,time).apply();
    }

    @Override
    public long getOpenMainFragmentTime() {
        return sp.getLong(VALUE_OPEN_MAIN_FRAGMENT_TIME,-1);
    }

    @Override
    public void setEveryReceiverTime(long time) {
        sp.edit().putLong(VALUE_EVERY_RECEIVER_TIME,time).apply();
    }

    @Override
    public long getEveryReceiverTime() {
        return sp.getLong(VALUE_EVERY_RECEIVER_TIME,1000);
    }


    /**
     * 获取某个时间点的数据所处的状态
     * @return
     */
    @Override
    public int getState(long time) {
        DatePhysiologyBean datePhysiologyBean = mIDbaMgr.queryPredictionData(time);
//        //0 预测经期 1 经期  2 安全期  3 排卵期   4 排卵日
        if(isMenstruationStart(datePhysiologyBean))
            return PeriodConstant.VALUE_INT_OPERATION_MENSTRUATION_START;
        if(isMenstruationEnd(datePhysiologyBean))
            return PeriodConstant.VALUE_INT_OPERATION_MENSTRUATION_END;
        if(isMenstruation(datePhysiologyBean))
            return PeriodConstant.VALUE_INT_OPERATION_MENSTRUATION;
        if(isSafty(datePhysiologyBean))
            return PeriodConstant.VALUE_INT_OPERATION_SAFETY;
        if(isPregnancy(datePhysiologyBean))
            return PeriodConstant.VALUE_INT_OPERATION_PREGNANCY_PROBABILITY;

        return PeriodConstant.VALUE_INT_OPERATION_UNKNOWN;
    }

    /**
     * 安全期是否处于第一阶段
     * @param time
     * @return
     */
    @Override
    public boolean isSafetyInFirstStage(long time) {
        time = CalendarUtil.getZeroDate(time);
        DatePhysiologyBean datePhysiologyBean = mIDbaMgr.queryPredictionData(time);
        if(datePhysiologyBean==null)                                                               //如果当天没有数据，则说明没有
            return false;
        int stage = datePhysiologyBean.getCurrentState();
        if(stage!=2)
            return false;
        for(int i=1;;i++){
            long newTime = time - i*1000*3600*24;
            DatePhysiologyBean preDatePhysiologyBean = mIDbaMgr.queryPredictionData(newTime);
            if(preDatePhysiologyBean==null)
                return true;
            int preState = preDatePhysiologyBean.getCurrentState();
            if(preState==0||preState==1)
                return true;
            if(preState!=stage)
                return false;
        }
    }

    /**
     * 首页点击按钮后是否是经期开始
     * @param datePhysiologyBean
     * @return
     */
    private boolean isMenstruationStart(DatePhysiologyBean datePhysiologyBean){
        if(datePhysiologyBean==null)                                                               //无数据时返回经期开始
            return true;
        int stage = datePhysiologyBean.getCurrentState();                                           //0 预测经期 1 经期  2 安全期  3 排卵期   4 排卵日
        if(stage==0)
            return true;
        if(stage==2&&!isSafetyInFirstStage(datePhysiologyBean.getCurrentDate()))                    //安全期并且是第二阶段则返回经期开始
            return true;
        return false;
    }


    /**
     * 首页点击按钮后是否是经期结束
     * @param datePhysiologyBean
     * @return
     */
    private boolean isMenstruationEnd(DatePhysiologyBean datePhysiologyBean){
        if(datePhysiologyBean==null)
            return false;
        DatePhysiologyBean startDatePhysiologyBean = mIDbaMgr.queryTimebeforeStartData(datePhysiologyBean.getCurrentDate());         //本周期的经期开始时间
        if(startDatePhysiologyBean==null)
            return false;
        int stage = datePhysiologyBean.getCurrentState();
        boolean isRealPeriod = stage==1;
        boolean isPeriodEnd = datePhysiologyBean.getIsPeriodEnd();
        Long periodDuration = Long.parseLong(String.valueOf(getMenstrualDuration()-1));
        if (datePhysiologyBean.getIsPeriodStart()){
            long predictEndTime = datePhysiologyBean.getCurrentDate() + periodDuration*1000*3600*24;
            boolean isOverPeriodDate = datePhysiologyBean.getCurrentDate()>=predictEndTime;                             //当前时间是否超过了预测的经期时间

            if(isRealPeriod&&!isPeriodEnd&&isOverPeriodDate)                                                                                //0 预测经期 1 经期  2 安全期  3 排卵期   4 排卵日
                return true;
        }else {
            long predictEndTime = startDatePhysiologyBean.getCurrentDate() + periodDuration*1000*3600*24;
            boolean isOverPeriodDate = datePhysiologyBean.getCurrentDate()>=predictEndTime;                             //当前时间是否超过了预测的经期时间

            if(isRealPeriod&&!isPeriodEnd&&isOverPeriodDate)                                                                                //0 预测经期 1 经期  2 安全期  3 排卵期   4 排卵日
                return true;
        }

        return false;
    }

    /**
     * 首页点击按钮后是否处于经期
     * @param datePhysiologyBean
     * @return
     */
    private boolean isMenstruation(DatePhysiologyBean datePhysiologyBean){
        if(datePhysiologyBean==null)
            return false;
        DatePhysiologyBean startDatePhysiologyBean = mIDbaMgr.queryTimebeforeStartData(datePhysiologyBean.getCurrentDate());         //本周期的经期开始时间
        if(startDatePhysiologyBean==null)
            return false;
        int stage = datePhysiologyBean.getCurrentState();
        boolean isRealPeriod = stage==1;
        boolean isPeriodEnd = datePhysiologyBean.getIsPeriodEnd();
        Long periodDuration = Long.parseLong(String.valueOf(getMenstrualDuration()-1));
        long predictEndTime = startDatePhysiologyBean.getCurrentDate() + periodDuration*1000*3600*24;
        boolean isOverPeriodDate = datePhysiologyBean.getCurrentDate()>=predictEndTime;                             //当前时间是否超过了预测的经期时间
        if(isRealPeriod&&!isPeriodEnd&&!isOverPeriodDate)
            return true;
        return false;
    }


    /**
     * 首页点击按钮后是否处于安全期
     * @param datePhysiologyBean
     * @return
     */
    private boolean isSafty(DatePhysiologyBean datePhysiologyBean){
        if(datePhysiologyBean==null)
            return false;
        int stage = datePhysiologyBean.getCurrentState();
        if(stage==2&&isSafetyInFirstStage(datePhysiologyBean.getCurrentDate()))                                      //0 预测经期 1 经期  2 安全期  3 排卵期   4 排卵日
            return true;
        return false;
    }


    /**
     * 首页是否处于怀孕概率期
     * @param datePhysiologyBean
     * @return
     */
    private boolean isPregnancy(DatePhysiologyBean datePhysiologyBean){
        if(datePhysiologyBean==null)
            return false;
        int stage = datePhysiologyBean.getCurrentState();                                           //0 预测经期 1 经期  2 安全期  3 排卵期   4 排卵日
        if(stage==3||stage==4)
            return true;
        return false;
    }
}
