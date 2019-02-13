package com.period.app.core.dba;

import android.os.Message;

import com.period.app.XApplication;
import com.period.app.bean.DatePhysiologyBean;
import com.period.app.bean.gen.DatePhysiologyBeanDao;
import com.period.app.constants.DatePhysiologyConstant;
import com.period.app.constants.PeriodConstant;
import com.period.app.core.XCoreFactory;
import com.period.app.core.calendardialog.ICalendarDialogManger;
import com.period.app.core.data.IDataMgr;
import com.period.app.core.prediction.IPredictionManger;
import com.period.app.utils.CalendarUtil;
import com.period.app.utils.TimeUtils;

import java.util.List;

import ulric.li.XLibFactory;
import ulric.li.xlib.intf.IXThreadPool;
import ulric.li.xlib.intf.IXThreadPoolListener;

public class DbaManger implements IDbaManger {
    private final long VALUE_LONG_ONE_DAY_MILLIS = 1000 * 60 * 60 * 24;

    @Override
    public void addPredictionData(DatePhysiologyBean datePhysiologyBean) {
        if (null == datePhysiologyBean) {
            return;
        }
        DatePhysiologyBeanDao datePhysiologyBeanDao = XApplication.getInstance().getDatePhysiologyBeanDao();
        datePhysiologyBeanDao.insertOrReplace(datePhysiologyBean);
    }

    @Override
    public void deletePredictionData(long date) {
        DatePhysiologyBeanDao datePhysiologyBeanDao = XApplication.getInstance().getDatePhysiologyBeanDao();
        DatePhysiologyBean datePhysiologyBean = datePhysiologyBeanDao.queryBuilder().where(DatePhysiologyBeanDao.Properties.CurrentDate.eq(date)).build().unique();
        if (datePhysiologyBean != null)
            datePhysiologyBeanDao.delete(datePhysiologyBean);
    }

    @Override
    public void modifyPredictionData(DatePhysiologyBean datePhysiologyBean) {
        DatePhysiologyBeanDao datePhysiologyBeanDao = XApplication.getInstance().getDatePhysiologyBeanDao();
        datePhysiologyBeanDao.insertOrReplace(datePhysiologyBean);

    }

    @Override
    public DatePhysiologyBean queryMostNearlyTime() {
        DatePhysiologyBeanDao datePhysiologyBeanDao = XApplication.getInstance().getDatePhysiologyBeanDao();
        datePhysiologyBeanDao.detachAll();
        List<DatePhysiologyBean> datePhysiologyBean = datePhysiologyBeanDao.queryBuilder()
                .orderDesc(DatePhysiologyBeanDao.Properties.CurrentDate).build().list();
        if (datePhysiologyBean != null && datePhysiologyBean.size() > 0) {
            return datePhysiologyBean.get(0);
        }
        return null;
    }

    @Override
    public DatePhysiologyBean queryPredictionData(long date) {
        DatePhysiologyBeanDao datePhysiologyBeanDao = XApplication.getInstance().getDatePhysiologyBeanDao();
        datePhysiologyBeanDao.detachAll();
        DatePhysiologyBean datePhysiologyBean = datePhysiologyBeanDao
                .queryBuilder()
                .where(DatePhysiologyBeanDao.Properties.CurrentDate.eq(date))
                .build().unique();
        return datePhysiologyBean;
    }

    @Override
    public DatePhysiologyBean queryMostNearlyPeriodStime() {
        DatePhysiologyBeanDao datePhysiologyBeanDao = XApplication.getInstance().getDatePhysiologyBeanDao();
        List<DatePhysiologyBean> datePhysiologyBean = datePhysiologyBeanDao.queryBuilder().where(DatePhysiologyBeanDao.Properties.IsPeriodStart.eq(true))
                .orderDesc(DatePhysiologyBeanDao.Properties.CurrentDate).build().list();
        if (datePhysiologyBean != null && datePhysiologyBean.size() > 0) {
            return datePhysiologyBean.get(0);
        }
        return null;
    }

    //查询某一特点时间之前最近的经期结束时间
    @Override
    public DatePhysiologyBean queryTimebeforeEndData(long date) {
        DatePhysiologyBeanDao datePhysiologyBeanDao = XApplication.getInstance().getDatePhysiologyBeanDao();
        List<DatePhysiologyBean> datePhysiologyBean = datePhysiologyBeanDao.queryBuilder()
                .where(DatePhysiologyBeanDao.Properties.IsPeriodEnd.eq(true))
                .where(DatePhysiologyBeanDao.Properties.CurrentDate.lt(date))
                .orderDesc(DatePhysiologyBeanDao.Properties.CurrentDate).build().list();
        if (datePhysiologyBean != null && datePhysiologyBean.size() > 0) {
            return datePhysiologyBean.get(0);
        }
        return null;
    }

    //查询某一特点时间之前最近的经期开始时间
    @Override
    public DatePhysiologyBean queryTimebeforeStartData(long date) {
        DatePhysiologyBeanDao datePhysiologyBeanDao = XApplication.getInstance().getDatePhysiologyBeanDao();
        List<DatePhysiologyBean> datePhysiologyBean = datePhysiologyBeanDao.queryBuilder()
                .where(DatePhysiologyBeanDao.Properties.IsPeriodStart.eq(true))
                .where(DatePhysiologyBeanDao.Properties.CurrentDate.lt(date))
                .orderDesc(DatePhysiologyBeanDao.Properties.CurrentDate).build().list();
        if (datePhysiologyBean != null && datePhysiologyBean.size() > 0) {
            return datePhysiologyBean.get(0);
        }
        return null;
    }


    //查询某一特定时间之后最近的经期开始时间
    @Override
    public DatePhysiologyBean queryTimeAfterStartData(long date) {
        DatePhysiologyBeanDao datePhysiologyBeanDao = XApplication.getInstance().getDatePhysiologyBeanDao();
        List<DatePhysiologyBean> datePhysiologyBean = datePhysiologyBeanDao.queryBuilder()
                .where(DatePhysiologyBeanDao.Properties.IsPeriodStart.eq(true))
                .where(DatePhysiologyBeanDao.Properties.CurrentDate.gt(date))
                .orderAsc(DatePhysiologyBeanDao.Properties.CurrentDate).build().list();
        if (datePhysiologyBean != null && datePhysiologyBean.size() > 0) {
            return datePhysiologyBean.get(0);
        }
        return null;
    }

    //查询某一特定时间之后最近的经期结束时间
    @Override
    public DatePhysiologyBean queryTimeAfterEndData(long date) {
        DatePhysiologyBeanDao datePhysiologyBeanDao = XApplication.getInstance().getDatePhysiologyBeanDao();
        List<DatePhysiologyBean> datePhysiologyBean = datePhysiologyBeanDao.queryBuilder()
                .where(DatePhysiologyBeanDao.Properties.IsPeriodEnd.eq(true))
                .where(DatePhysiologyBeanDao.Properties.CurrentDate.gt(date))
                .orderAsc(DatePhysiologyBeanDao.Properties.CurrentDate).build().list();
        if (datePhysiologyBean != null && datePhysiologyBean.size() > 0) {
            return datePhysiologyBean.get(0);
        }
        return null;
    }


//    /**
//     * 查询相同周期的经期开始时间
//     * @param date
//     * @return
//     */
//    @Override
//    public DatePhysiologyBean queryTimeSameCycleStartData(long date) {
//        DatePhysiologyBeanDao datePhysiologyBeanDao = XApplication.getInstance().getDatePhysiologyBeanDao();
//        List<DatePhysiologyBean> datePhysiologyBean = datePhysiologyBeanDao.queryBuilder()
//                .where(DatePhysiologyBeanDao.Properties.IsPeriodStart.eq(true))
//                .where(DatePhysiologyBeanDao.Properties.CurrentDate.le(date))
//                .orderAsc(DatePhysiologyBeanDao.Properties.CurrentDate).build().list();
//        if (datePhysiologyBean != null && datePhysiologyBean.size() > 0) {
//            return datePhysiologyBean.get(0);
//        }
//        return null;
//    }
//
//
//    /**
//     * 查询相同周期的经期结束时间
//     * @param date
//     * @return
//     */
//    @Override
//    public DatePhysiologyBean queryTimeSameCycleEndData(long date) {
//        DatePhysiologyBeanDao datePhysiologyBeanDao = XApplication.getInstance().getDatePhysiologyBeanDao();
//        List<DatePhysiologyBean> datePhysiologyBean = datePhysiologyBeanDao.queryBuilder()
//                .where(DatePhysiologyBeanDao.Properties.IsPeriodEnd.eq(true))
//                .where(DatePhysiologyBeanDao.Properties.CurrentDate.ge(date))
//                .orderAsc(DatePhysiologyBeanDao.Properties.CurrentDate).build().list();
//        if (datePhysiologyBean != null && datePhysiologyBean.size() > 0) {
//            return datePhysiologyBean.get(0);
//        }
//        return null;
//    }


    /**
     * 该对象是当前状态的第几天
     *
     * @param date
     * @return
     */
    @Override
    public int querySameStateCountBeforeCurrent(long date, int state) {
        date = CalendarUtil.getZeroDate(date);
        IPredictionManger iPredictionManger = (IPredictionManger) XCoreFactory.getInstance().createInstance(IPredictionManger.class);
        IDataMgr iDataMgr = (IDataMgr) XCoreFactory.getInstance().createInstance(IDataMgr.class);
        DatePhysiologyBean beforeTodayBean = queryPredictionData(date - VALUE_LONG_ONE_DAY_MILLIS);

        long recentMenstrualTime = iPredictionManger.getRecentMenstrualTime();
        //在数据库中查询到前一天的数据，说明数据库中进行了保存，则以数据库为准
        if (date < recentMenstrualTime && beforeTodayBean != null) {
            for (int i = 0; ; i++) {
                Long l=Long.parseLong(String.valueOf(i));
                long time = date - l * VALUE_LONG_ONE_DAY_MILLIS;
                DatePhysiologyBean datePhysiologyBean = queryPredictionData(time);
                if (time < recentMenstrualTime) {
                    //查询历史数据
                    if (datePhysiologyBean != null) {
                        int currentState = datePhysiologyBean.getCurrentState();
                        if (state == DatePhysiologyConstant.VALUE_INT_STATE_PERIOD_FORECAST || state == DatePhysiologyConstant.VALUE_INT_STATE_PERIOD_ACTUAL) {
                            if (datePhysiologyBean.getCurrentState() == 0 || datePhysiologyBean.getCurrentState() == 1) {
                                continue;
                            }else {
                                return (i - 1) > 0 ? (i - 1) : 0;
                            }
                        } else if (state == DatePhysiologyConstant.VALUE_INT_STATE_OVULATION) {
                            if (currentState == DatePhysiologyConstant.VALUE_INT_STATE_OVULATION || currentState == DatePhysiologyConstant.VALUE_INT_STATE_OVULATION_DAY) {
                                continue;
                            }else {
                                return (i - 1) > 0 ? (i - 1) : 0;
                            }
                        } else if (state== DatePhysiologyConstant.VALUE_INT_STATE_SAFETY) {
                            if (currentState == DatePhysiologyConstant.VALUE_INT_STATE_SAFETY ) {
                                continue;
                            }else {
                                return (i - 1) > 0 ? (i - 1) : 0;
                            }
                        } else {
                            return (i - 1) > 0 ? (i - 1) : 0;
                        }
                        //预测数据
                    } else {
                        if (state == DatePhysiologyConstant.VALUE_INT_STATE_PERIOD_FORECAST || state == DatePhysiologyConstant.VALUE_INT_STATE_PERIOD_ACTUAL) {
                            return iPredictionManger.getDayInMenstrual(date);
                        } else if (state == DatePhysiologyConstant.VALUE_INT_STATE_SAFETY) {
                            return iPredictionManger.getDayInSafety(date);
                        } else if (state == DatePhysiologyConstant.VALUE_INT_STATE_OVULATION) {
                            return iPredictionManger.getDayInOvulation(date);
                        } else {
                            return 0;
                        }
                    }
                } else {
                    if (state == DatePhysiologyConstant.VALUE_INT_STATE_PERIOD_FORECAST || state == DatePhysiologyConstant.VALUE_INT_STATE_PERIOD_ACTUAL) {
                        return iPredictionManger.getDayInMenstrual(date);
                    } else if (state == DatePhysiologyConstant.VALUE_INT_STATE_SAFETY) {
                        return iPredictionManger.getDayInSafety(date);
                    } else if (state == DatePhysiologyConstant.VALUE_INT_STATE_OVULATION) {
                        return iPredictionManger.getDayInOvulation(date);
                    } else {
                        return 0;
                    }
                }
            }
            //数据库中查询不到数据，则以预测的为准
        } else {
            if (state == DatePhysiologyConstant.VALUE_INT_STATE_PERIOD_FORECAST || state == DatePhysiologyConstant.VALUE_INT_STATE_PERIOD_ACTUAL) {
                return iPredictionManger.getDayInMenstrual(date);
            } else if (state == DatePhysiologyConstant.VALUE_INT_STATE_SAFETY) {
                return iPredictionManger.getDayInSafety(date);
            } else if (state == DatePhysiologyConstant.VALUE_INT_STATE_OVULATION) {
                return iPredictionManger.getDayInOvulation(date);
            } else {
                return 0;
            }
        }
    }

    @Override
    public int querySameStateCountInMainDay(long date, int state) {
        date = CalendarUtil.getZeroDate(date);
        //在数据库中查询到前一天的数据，说明数据库中进行了保存，则以数据库为准
        for (int i = 1; ; i++) {
            Long l=Long.parseLong(String.valueOf(i));
            long time = date - l * VALUE_LONG_ONE_DAY_MILLIS;
            DatePhysiologyBean datePhysiologyBean = queryPredictionData(time);
            //查询历史数据
            if (datePhysiologyBean != null) {
                int currentState = datePhysiologyBean.getCurrentState();
                if (state == DatePhysiologyConstant.VALUE_INT_STATE_PERIOD_FORECAST || state == DatePhysiologyConstant.VALUE_INT_STATE_PERIOD_ACTUAL) {
                   boolean isPeriod = datePhysiologyBean.getCurrentState() == DatePhysiologyConstant.VALUE_INT_STATE_PERIOD_FORECAST || datePhysiologyBean.getCurrentState() == DatePhysiologyConstant.VALUE_INT_STATE_PERIOD_ACTUAL;
                   boolean isPeriodEnd = datePhysiologyBean.getIsPeriodEnd();
                   if (isPeriod&&!isPeriodEnd){
                        continue;
                    }else {
                        return (i - 1) > 0 ? (i - 1) : 0;
                    }
                } else if (state == DatePhysiologyConstant.VALUE_INT_STATE_OVULATION) {
                    if (currentState == DatePhysiologyConstant.VALUE_INT_STATE_OVULATION || currentState == DatePhysiologyConstant.VALUE_INT_STATE_OVULATION_DAY) {
                        continue;
                    }else{
                        return (i - 1) > 0 ? (i - 1) : 0;
                    }
                } else if (state == DatePhysiologyConstant.VALUE_INT_STATE_SAFETY) {
                    if (currentState == DatePhysiologyConstant.VALUE_INT_STATE_SAFETY) {
                        continue;
                    }else{
                        return (i - 1) > 0 ? (i - 1) : 0;
                    }
                } else {
                    return (i - 1) > 0 ? (i - 1) : 0;
                }
            } else {
                return (i - 1) > 0 ? (i - 1) : 0;
            }
        }
    }

    @Override
    public int queryStateCount(long date) {
        int sizes=0;
        for (long i = 0; ; i++){
            long newTime = date + i * 1000 * 60 * 60 * 24;
            DatePhysiologyBean datePhysiologyBeans = queryPredictionData(newTime);
            if (datePhysiologyBeans!=null){
                if(datePhysiologyBeans.getCurrentState()==1){
                    sizes=sizes+1;
                }else {
                    return sizes;
                }
            }else {
                return sizes;
            }
        }
    }


    @Override
    public void supplyData() {
        IDataMgr iDataMgr = (IDataMgr) XCoreFactory.getInstance().createInstance(IDataMgr.class);
        IPredictionManger iPredictionManger = (IPredictionManger) XCoreFactory.getInstance().createInstance(IPredictionManger.class);
        IXThreadPool ixThreadPool = (IXThreadPool) XLibFactory.getInstance().createInstance(IXThreadPool.class);
        IDbaManger iDbaManger = (IDbaManger) XCoreFactory.getInstance().createInstance(IDbaManger.class);
        ICalendarDialogManger iCalendarDialogManger = (ICalendarDialogManger) XCoreFactory.getInstance().createInstance(ICalendarDialogManger.class);
        //是否第一次打开
        if (iDataMgr.getFirstOpenTime() != PeriodConstant.VALUE_INT_UNSET_OPEN_TIME) {
            long current=CalendarUtil.getZeroDate(System.currentTimeMillis());
            DatePhysiologyBean datePhysiologyBean = queryMostNearlyTime();
            if (datePhysiologyBean == null) {
                return;
            }
            if (current-datePhysiologyBean.getCurrentDate()>0){
                //TOD上次经期是否结束，如果结束，则按补位法补充数据
                //patchPositions();
                //上次经期没有结束，则从上次时间到现在都补成经期，且不结束
                //前1个开始
                DatePhysiologyBean beforePhysiologyBean = queryTimebeforeStartData(current);
                long LastStartTime=beforePhysiologyBean.getCurrentDate();
                //上衣开始之间到当天有没有结束
                DatePhysiologyBean afterEnd=queryTimeAfterEndData(LastStartTime);
                if (afterEnd==null){
                    //上次经期未结束，所有数据补为经期
                    int size=CalendarUtil.daysBetween(LastStartTime,current)+1;
                    if (size>iDataMgr.getPhyCycle()){
                        for (long j = 0; j < size; j++){
                            long newTimes=LastStartTime+j*1000*60*60*24;
                            if (newTimes>current){
                                continue;
                            }else {
                                deletePredictionData(newTimes);
                            }
                        }
                        long endTime=0L;
                        for (long j = 0; j < iDataMgr.getMenstrualDuration(); j++){
                            long newTimes=LastStartTime+j*1000*60*60*24;
                            DatePhysiologyBean datePhysiologyBeans = new DatePhysiologyBean();
                            datePhysiologyBeans.setCurrentDate(newTimes);
                            datePhysiologyBeans.setCurrentState(1);
                            if (j==0){
                                datePhysiologyBeans.setIsPeriodStart(true);
                            }else {
                                datePhysiologyBeans.setIsPeriodStart(false);
                            }
                            if (j==iDataMgr.getMenstrualDuration()-1){
                                endTime=newTimes;
                                datePhysiologyBeans.setIsPeriodEnd(true);
                            }else {
                                datePhysiologyBeans.setIsPeriodEnd(false);
                            }
                            modifyPredictionData(datePhysiologyBeans);
                        }
                        iCalendarDialogManger.PatchPosition(endTime+1000*3600*24,current-1000*3600*24);
                        iDataMgr.setManualstart(current);

                    }else {
                        iCalendarDialogManger.updatePeriodData(LastStartTime,current,false);
                    }
                }else {
                    Long l = Long.parseLong(String.valueOf(iDataMgr.getPhyCycle()));
                    long nextStartTime=LastStartTime+1000*3600*24*l;
                    if (nextStartTime<=current){
                        iCalendarDialogManger.PatchPosition(afterEnd.getCurrentDate()+1000*3600*24,current-1000*3600*24);
                        iDataMgr.setManualstart(current);
                    }else {
                        iCalendarDialogManger.PatchPositions(afterEnd.getCurrentDate()+1000*3600*24,nextStartTime-1000*3600*24,current);
                    }
                }
            }


        }
    }


}
