package com.period.app.dialog.calendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.period.app.R;
import com.period.app.bean.DatePhysiologyBean;
import com.period.app.bean.TimeFrame;
import com.period.app.core.XCoreFactory;
import com.period.app.core.data.IDataMgr;
import com.period.app.core.dba.IDbaManger;
import com.period.app.dialog.CustomDialog;
import com.period.app.utils.CalendarUtil;
import com.period.app.utils.TimeUtils;
import com.period.app.widget.wheelview.WheelMonthDayDateView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangePeriodtimeDialog extends CustomDialog {

    @BindView(R.id.changetime_tvs)
    TextView changetimeTv;
    @BindView(R.id.wheel_change_period)
    WheelMonthDayDateView wheelChangePeriod;
    @BindView(R.id.changetime_swich)
    CheckBox changetimeSwich;
    @BindView(R.id.changetime_content_tv)
    TextView changetimeContentTv;
    @BindView(R.id.swich_rl)
    RelativeLayout swichRl;

    private boolean tag;
    private boolean state;
    private long selectTime;
    private final IDbaManger mIDbaManger;
    private TimeFrame mTimeFrame;


    //state true 开始标记  fale 关闭标记
    public ChangePeriodtimeDialog(@NonNull Context context, boolean tag, boolean state, long selectTime, TimeFrame mTimeFrame) {
        super(context);
        this.state = state;
        this.tag = tag;
        this.selectTime = selectTime;
        this.mTimeFrame = mTimeFrame;
        mIDbaManger = (IDbaManger) XCoreFactory.getInstance().createInstance(IDbaManger.class);
        initView(context);
    }


    private void initView(Context context) {
        View view = View.inflate(context, R.layout.view_change_time, null);
        ButterKnife.bind(this, view);
        if (state) {
            changetimeTv.setText(R.string.menstrual_period);
            changetimeContentTv.setText(R.string.adjust_the_start_of_menstrual_period);
//            if (tag) {
//                wheelChangePeriod.setEnabled(false);
//                changetimeSwich.setChecked(true);
//            } else {
//
//                wheelChangePeriod.setEnabled(false);
//                changetimeSwich.setChecked(false);
//            }
        } else {
            DatePhysiologyBean startBean = mIDbaManger.queryTimeAfterStartData(selectTime);
            if (startBean!=null){
                swichRl.setVisibility(View.VISIBLE);
            }else {

                long startTime=mIDbaManger.queryTimebeforeStartData(selectTime).getCurrentDate();
                int size= TimeUtils.daysBetween(startTime,CalendarUtil.getZeroDate(System.currentTimeMillis()))+1;
                IDataMgr mgr = (IDataMgr) XCoreFactory.getInstance().createInstance(IDataMgr.class);
                if (size>mgr.getPhyCycle()){
                    swichRl.setVisibility(View.INVISIBLE);
                }else {
                    swichRl.setVisibility(View.VISIBLE);
                }


            }
            DatePhysiologyBean afterStart=mIDbaManger.queryTimeAfterStartData(selectTime);
            if (afterStart!=null){
                changetimeTv.setText(context.getText(R.string.menstrual_period));
            }else {
                changetimeTv.setText(context.getText(R.string.end_of_men_period));
                changetimeTv.setTextColor(context.getResources().getColor(R.color.menstrualLengthWheelCenterColor));
            }

            changetimeContentTv.setText(R.string.adjust_the_end_of_menstrual_period);
//            if (tag) {
//                wheelChangePeriod.setEnabled(false);
//                changetimeSwich.setChecked(true);
//            } else {
//                wheelChangePeriod.setEnabled(false);
//
//                changetimeSwich.setChecked(false);
//            }
        }
//        changetimeSwich.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                wheelChangePeriod.setCenterTextColor(context.getResources().getColor(R.color.reminderTimeWheelCenterColor));
//
//                wheelChangePeriod.setEnabled(true);
//            } else {
//                wheelChangePeriod.setCenterTextColor(context.getResources().getColor(R.color.dialogNegativeColor));
//                wheelChangePeriod.setEnabled(false);
//            }
//        });
        wheelChangePeriod.setStartDate(mTimeFrame.getStartYear(), mTimeFrame.getStartMonth(), mTimeFrame.getStartDay());
        wheelChangePeriod.setEndDate(mTimeFrame.getEndYear(), mTimeFrame.getEndtMonth(), mTimeFrame.getEndtDay());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(selectTime);
        wheelChangePeriod.setInitDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH));
        setCustomView(view);
        setTitle("Eidt");
    }


    public long getSelectTime() {

        return timeTolong(wheelChangePeriod.getSelectedYear(), wheelChangePeriod.getSelectedMonth(), wheelChangePeriod.getSelectedDay());
    }

    public boolean getChecked(){
        return  changetimeSwich.isChecked();
    }

    private long timeTolong(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTimeInMillis();
    }
}
