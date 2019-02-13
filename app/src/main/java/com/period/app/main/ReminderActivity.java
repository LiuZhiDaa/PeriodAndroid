package com.period.app.main;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.period.app.R;
import com.period.app.base.BaseActivity;
import com.period.app.constants.RemindConstant;
import com.period.app.core.XCoreFactory;
import com.period.app.core.reminder.IReminderMgr;
import com.period.app.dialog.ReminderTextDialog;
import com.period.app.dialog.ReminderTimeDialog;
import com.period.app.utils.LogUtils;
import com.period.app.widget.MyToolbar;
import com.period.app.widget.wheelview.WheelView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by WangGuiLi
 * on 2018/12/18
 * 提醒的界面
 */
public class ReminderActivity extends BaseActivity {

    @BindView(R.id.tool_bar)
    MyToolbar mMyToolbar;
    @BindView(R.id.tv_reminder_length)
    TextView tvReminderLength;
    @BindView(R.id.sw_reminder)
    Switch swReminder;
    @BindView(R.id.lv_reminder_switch)
    LinearLayout lvReminderSwitch;
    @BindView(R.id.lv_reminder_custom_text)
    LinearLayout lvReminderCustomText;
    @BindView(R.id.tv_reminder_text)
    TextView tvReminderText;
    @BindView(R.id.tv_reminder_custom_text)
    TextView tvReminderCustomText;
    @BindView(R.id.tv_reminder_time)
    TextView tvReminderTime;
    @BindView(R.id.tv_time_text)
    TextView tvTimeText;
    @BindView(R.id.lv_reminder_time)
    LinearLayout lvReminderTime;
    private String[] mReminderTitles;
    private String[] mReminderSwitchTexts;
    private String[] mReminderCustomTexts;
    private IReminderMgr mReminderMgr;
    private int mType;
    private WheelView mWheelViewDay;
    private String[] mDayArray;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_reminder;
    }

    @Override
    public void init() {
        Intent intent = getIntent();
        mType = intent.getIntExtra(RemindConstant.TYPE, RemindConstant.VAULE_INT_TYPE_MEN_START);
        mReminderTitles = getResources().getStringArray(R.array.reminder_titles);
        mReminderSwitchTexts = getResources().getStringArray(R.array.reminder_switch_texts);
        mReminderCustomTexts = getResources().getStringArray(R.array.reminder_custom_texts);


        mMyToolbar.setTitle(mReminderTitles[mType]);
        tvReminderLength.setText(mReminderSwitchTexts[mType]);
//        tvReminderText.setText(mReminderCustomTexts[mType]);
        mReminderMgr = (IReminderMgr) XCoreFactory.getInstance().createInstance(IReminderMgr.class);

        swReminder.setChecked(mReminderMgr.isSwitchOn(mType));
        tvReminderCustomText.setText(mReminderMgr.getReminderCustomText(mType));
        mDayArray = getResources().getStringArray(R.array.advance_days);
        tvTimeText.setText(getString(R.string.str_reminder_time,mDayArray[mReminderMgr.getAdvanceDay(mType)] , mReminderMgr.getReminderTime(mType)));

        swReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mReminderMgr.setSwitch(mType, isChecked);
                mReminderMgr.setRemindAlarm(mType, false);
                addLog(isChecked);
            }
        });

    }

    public static void launch(Context context, int type) {
        Intent intent = new Intent();
        intent.setClass(context, ReminderActivity.class);
        intent.putExtra(RemindConstant.TYPE, type);
        context.startActivity(intent);
    }

    @OnClick({R.id.lv_reminder_custom_text,R.id.lv_reminder_time})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lv_reminder_custom_text:
                final ReminderTextDialog reminderTextDialog = new ReminderTextDialog(ReminderActivity.this, mType);
                reminderTextDialog.setOnClickListenerSave(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] result = reminderTextDialog.getResult();
                        mReminderMgr.setReminderText(mType, result[0]);
                        tvReminderCustomText.setText(result[0]);
                        reminderTextDialog.dismiss();
                        tvTimeText.setText(getString(R.string.str_reminder_time,mDayArray[mReminderMgr.getAdvanceDay(mType)] , mReminderMgr.getReminderTime(mType)));
                    }
                });
                reminderTextDialog.show();
                addTextAndTimeLog();
                break;
            case R.id.lv_reminder_time:
                final ReminderTimeDialog reminderTimeDialog = new ReminderTimeDialog(ReminderActivity.this,mType);
                reminderTimeDialog.setOnClickListenerSave(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] result = reminderTimeDialog.getResult();
                        mReminderMgr.setAdvanceDay(mType,Integer.parseInt(result[0]));
                        mReminderMgr.setReminderTime(mType,result[1]);
                        mReminderMgr.setRemindAlarm(mType, false);
                        reminderTimeDialog.dismiss();
                        tvTimeText.setText(getString(R.string.str_reminder_time,mDayArray[mReminderMgr.getAdvanceDay(mType)] , mReminderMgr.getReminderTime(mType)));
                    }
                });
                reminderTimeDialog.show();
                LogUtils.reminderLog("click_reminderTime");
                break;
        }
    }

    private void addLog(boolean state){
        switch (mType){
            case RemindConstant.VAULE_INT_TYPE_MEN_START:
                if (state){
                    LogUtils.settingLog("click_menstruationStartReminderOn");
                }else{
                    LogUtils.settingLog("click_menstruationStartReminderOff");
                }
                break;
            case RemindConstant.VAULE_INT_TYPE_MEN_END:
                if (state){
                    LogUtils.settingLog("click_menstruationEndReminderOn");
                }else{
                    LogUtils.settingLog("click_menstruationEndReminderOff");
                }
                break;
            case RemindConstant.VAULE_INT_TYPE_OVULATION_START:
                if (state){
                    LogUtils.settingLog("click_ovulationStartReminderOn");
                }else{
                    LogUtils.settingLog("click_ovulationStartReminderOff");
                }
                break;
            case RemindConstant.VAULE_INT_TYPE_OVULATION_DAY:
                if (state){
                    LogUtils.settingLog("click_ovulationDayReminderOn");
                }else{
                    LogUtils.settingLog("click_ovulationDayReminderOff");
                }
                break;
            case RemindConstant.VAULE_INT_TYPE_OVULATION_END:
                if (state){
                    LogUtils.settingLog("click_ovulationEndReminderOn");
                }else{
                    LogUtils.settingLog("click_ovulationEndReminderOff");
                }
                break;
        }
    }
    private void addTextAndTimeLog(){
        switch (mType){
            case RemindConstant.VAULE_INT_TYPE_MEN_START:
                LogUtils.settingLogText("menstruationStart");
                break;
            case RemindConstant.VAULE_INT_TYPE_MEN_END:
                LogUtils.settingLogText("menstruationend");
                break;
            case RemindConstant.VAULE_INT_TYPE_OVULATION_START:
                LogUtils.settingLogText("ovulationStart");
                break;
            case RemindConstant.VAULE_INT_TYPE_OVULATION_DAY:
                LogUtils.settingLogText("ovulationDay");
                break;
            case RemindConstant.VAULE_INT_TYPE_OVULATION_END:
                LogUtils.settingLogText("ovulationEnd");
                break;
        }
    }

}
