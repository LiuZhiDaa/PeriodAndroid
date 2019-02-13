package com.period.app.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.period.app.R;
import com.period.app.core.XCoreFactory;
import com.period.app.core.reminder.IReminderMgr;
import com.period.app.widget.wheelview.WheelView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by WangGuiLi
 * on 2018/12/13
 */
public class ReminderTimeDialog extends CustomDialog {

//    private ISettingMgr mISettingMgr;
    private WheelView mWheelViewDay;
    private WheelView mWheelViewHour;
    private WheelView mWheelViewMinute;
    private TextView mTvUnit;
    private TextView mTvSplit;

    private int mType;


    public ReminderTimeDialog(@NonNull Context context,int type) {
        super(context);
        this.mType = type;
        initView(context);
    }

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.view_reminder_time, null);
        setCustomView(view);
        mTvUnit = view.findViewById(R.id.tv_unit);
        mTvUnit.setText("-");
        mTvSplit = view.findViewById(R.id.tv_split);
        mTvSplit.setText(":");
        mWheelViewDay = view.findViewById(R.id.wheel_view_day);
        mWheelViewHour = view.findViewById(R.id.wheel_view_hour);
        mWheelViewMinute = view.findViewById(R.id.wheel_view_minute);

        String[] array = context.getResources().getStringArray(R.array.advance_days);
        List<String> dayList = Arrays.asList(array);
        IReminderMgr iReminderMgr = (IReminderMgr)XCoreFactory.getInstance().createInstance(IReminderMgr.class);
        mWheelViewDay.setItems(dayList, iReminderMgr.getAdvanceDay(mType));
        setTitle(XCoreFactory.getApplication().getResources().getString(R.string.dialog_reminder_time_title));

        String reminderTime = iReminderMgr.getReminderTime(mType);
        String[] timeArry = reminderTime.split(":");
        int hourIndex = Integer.parseInt(timeArry[0]);
        int minuteIndex = Integer.parseInt(timeArry[1]);

        List<String> hourList = new ArrayList<>();
        for(int i=0;i<24;i++){
            if(i<10){
                hourList.add("0"+i);
            }else {
                hourList.add(i + "");
            }
        }
        mWheelViewHour.setItems(hourList,hourIndex);

        List<String> minuteList = new ArrayList<>();
        for(int i=0;i<60;i++){
            if(i<10){
                minuteList.add("0"+i);
            }else {
                minuteList.add(i + "");
            }
        }
        mWheelViewMinute.setItems(minuteList,minuteIndex);
    }

    public void setUnit(String unit) {
        if (mTvUnit != null) {
            mTvUnit.setText("-");
        }
        if(mTvSplit != null){
            mTvSplit.setText(":");
        }
    }


    @Override
    public String[] getResult() {
        String[] result = new String[2];
        result[0] =  mWheelViewDay.getSelectedPosition()+"";
        String time = mWheelViewHour.getSelectedItem() + ":" + mWheelViewMinute.getSelectedItem();
        result[1]= time;
        return result;
    }
}
