package com.period.app.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.period.app.R;
import com.period.app.bean.DatePhysiologyBean;
import com.period.app.constants.PeriodConstant;
import com.period.app.core.XCoreFactory;
import com.period.app.core.dba.IDbaManger;
import com.period.app.utils.CalendarUtil;
import com.period.app.widget.wheelview.WheelDateView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by WangGuiLi
 * on 2019/1/15
 */
public class PeriodOperationDialog extends CustomDialog {

    @BindView(R.id.wheel_period_operation)
    public WheelDateView mWheelDateView;
    private IDbaManger mIDbaMgr;
    private long endTime;
    private long startTime;

    private static final int VALUE_INT_OPERATE_DURATION = 2;
    private Calendar mCalendar;

    public PeriodOperationDialog(@NonNull Context context,int operationStage) {
        super(context);
        initView(context,operationStage);
    }

    private void initView(Context context,int operationStage) {
        View view = View.inflate(context, R.layout.view_period_operation, null);
        setCustomView(view);
        ButterKnife.bind(this, view);

        mIDbaMgr = (IDbaManger) XCoreFactory.getInstance().createInstance(IDbaManger.class);

        mCalendar = Calendar.getInstance();                                                        //当前时间的Calendar对象
        endTime = CalendarUtil.getZeroDate(mCalendar.getTimeInMillis());                          //将当前时间置为结束时间

        String title = "";
        if(operationStage == PeriodConstant.VALUE_INT_OPERATION_MENSTRUATION_START){
            startTime = getStartTime();
            title = XCoreFactory.getApplication().getResources().getString(R.string.select_start_period);
        }else if(operationStage ==PeriodConstant.VALUE_INT_OPERATION_MENSTRUATION_END){
            startTime = getEndTime();
            title = XCoreFactory.getApplication().getResources().getString(R.string.select_end_period);
        }
        setTitle(title);
        mWheelDateView.setStartDate(startTime);
        mWheelDateView.setEndDate(endTime);
    }

    private long getStartTime(){
        long startTime;
        DatePhysiologyBean datePhysiologyBean = mIDbaMgr.queryTimebeforeEndData(endTime);                                           //查询某一时间之前最近的经期开始时间
        mCalendar = Calendar.getInstance();
        mCalendar.add(Calendar.MONTH,-VALUE_INT_OPERATE_DURATION);
        long twoMonthTime = CalendarUtil.getZeroDate(mCalendar.getTimeInMillis()-PeriodConstant.VALUE_LONG_ONE_DAY_MILLIS);//两个月之前的时间
        if(datePhysiologyBean==null){                                                                                               //如果之前没有经期结束时间，则开始时间设置为当前时间的前60天
            startTime = twoMonthTime;
        }else {
            long prePeriodEndTime = CalendarUtil.getZeroDate(datePhysiologyBean.getCurrentDate());                                  //上一次经期结束时间
            if(prePeriodEndTime - endTime > twoMonthTime){                                                                          //如果上次经期结束时间到现在的时间超过了VALUE_INT_OPERATE_DURATION天，则手动置为当前时间之前的VALUE_INT_OPERATE_DURATION天
                startTime = twoMonthTime;
            }else {
                startTime = prePeriodEndTime+6*1000*3600*24;
            }
        }
        return startTime;
    }

    private long getEndTime(){
        long startTime;
        DatePhysiologyBean datePhysiologyBean = mIDbaMgr.queryTimebeforeStartData(endTime);                                         //查询某一时间之前最近的经期开始时间
        mCalendar = Calendar.getInstance();
        mCalendar.add(Calendar.MONTH,-VALUE_INT_OPERATE_DURATION);
        long twoMonthTime = CalendarUtil.getZeroDate(mCalendar.getTimeInMillis()-PeriodConstant.VALUE_LONG_ONE_DAY_MILLIS);//两个月之前的时间
        if(datePhysiologyBean==null){                                                                                                //如果之前没有经期结束时间，则开始时间设置为当前时间的前60天
            startTime = twoMonthTime;
        }else {
            long prePeriodStartTime = CalendarUtil.getZeroDate(datePhysiologyBean.getCurrentDate());                                  //上一次经期结束时间
            if(prePeriodStartTime - endTime > twoMonthTime){                                                                         //如果上次经期结束时间到现在的时间超过了VALUE_INT_OPERATE_DURATION天，则手动置为当前时间之前的两个月
                startTime = twoMonthTime;
            }else {
                startTime = prePeriodStartTime+1*1000*3600*24;
            }
        }
        return startTime;
    }

    public long getSelectTime(){
        int selectedYear = mWheelDateView.getSelectedYear();
        int selectedMonth = mWheelDateView.getSelectedMonth();
        int selectedDay = mWheelDateView.getSelectedDay();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, selectedYear);
        calendar.set(Calendar.MONTH, selectedMonth - 1);
        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
        long date = calendar.getTimeInMillis();
        return date;
    }

}
