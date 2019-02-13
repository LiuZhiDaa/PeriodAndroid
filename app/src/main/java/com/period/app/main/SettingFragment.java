package com.period.app.main;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.period.app.Constants;
import com.period.app.R;
import com.period.app.XConfig;
import com.period.app.base.BaseFragment;
import com.period.app.constants.RemindConstant;
import com.period.app.core.XCoreFactory;
import com.period.app.core.calendardialog.ICalendarDialogManger;
import com.period.app.core.data.IDataMgr;
import com.period.app.dialog.AdjustCycleLengthDialog;
import com.period.app.dialog.AdjustMentrualLengthDialog;
import com.period.app.utils.CalendarUtil;
import com.period.app.utils.IntentUtil;
import com.period.app.utils.LogUtils;


import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by WangGuiLi
 * on 2018/12/11
 * 设置界面
 */
public class SettingFragment extends BaseFragment {

    @BindView(R.id.lv_phy_cycle_length)
    LinearLayout mLvPhyCycleLenght;
    @BindView(R.id.tv_phy_cycle)
    TextView mTvPhyCycle;
    @BindView(R.id.lv_menstrual_length)
    LinearLayout mLvMenstrualLength;
    @BindView(R.id.tv_menstrual_days)
    TextView mTvMenstrualDays;
    @BindView(R.id.iv_menstrual_arrow)
    ImageView ivMenstrualArrow;
    @BindView(R.id.iv_menstrua_start_arrow)
    ImageView ivMenstruaStartArrow;
    @BindView(R.id.iv_end_menstrua_arrow)
    ImageView ivEndMenstruaArrow;
    @BindView(R.id.iv_ovulation_start_arrow)
    ImageView ivOvulationStartArrow;
    @BindView(R.id.iv_ovulation_day_arrow)
    ImageView ivOvulationDayArrow;
    @BindView(R.id.iv_end_ovulation_arrow)
    ImageView ivEndOvulationArrow;
    @BindView(R.id.lv_reminder_time)
    LinearLayout lvReminderTime;
    @BindView(R.id.lv_end_menstrua)
    LinearLayout mLvEndMenstrua;
    @BindView(R.id.lv_ovulation_start_prompt)
    LinearLayout mLvOvulationStartPrompt;
    private String[] mReminderCustomTexts;
    private IDataMgr mIDataManager;
    private ICalendarDialogManger mICalendarDialogManger;


    @Override
    protected void init(View rootView) {
        mICalendarDialogManger = (ICalendarDialogManger) XCoreFactory.getInstance().createInstance(ICalendarDialogManger.class);
        mIDataManager = (IDataMgr) XCoreFactory.getInstance().createInstance(IDataMgr.class);
        mReminderCustomTexts = getResources().getStringArray(R.array.reminder_custom_texts);
        initData();
    }


    private void initData() {
        mTvPhyCycle.setText(mIDataManager.getPhyCycle() + " " + XCoreFactory.getApplication().getResources().getString(R.string.days));
        mTvMenstrualDays.setText(mIDataManager.getMenstrualDuration() + " " + XCoreFactory.getApplication().getResources().getString(R.string.days));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void onLazyLoad() {

    }

    @OnClick({R.id.lv_menstrual_length, R.id.lv_reminder_time, R.id.lv_end_menstrua, R.id.lv_ovulation_start_prompt,
            R.id.lv_phy_cycle_length, R.id.lv_ovulation_day_prompt, R.id.lv_ovulation_end_prompt, R.id.lin_privacy,
            R.id.lin_feedback, R.id.lin_about})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lv_phy_cycle_length:
                LogUtils.moreLog("click_physiologicalCycleLength");
                AdjustCycleLengthDialog adjustCycleLengthDialog = new AdjustCycleLengthDialog(getActivity());
                adjustCycleLengthDialog.setOnClickListenerSave(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] result = adjustCycleLengthDialog.getResult();
                        mIDataManager.setPhyCycle(result[0]);
                        mTvPhyCycle.setText(mIDataManager.getPhyCycle() + " " + XCoreFactory.getApplication().getResources().getString(R.string.days));
                        java.util.Calendar calendar = java.util.Calendar.getInstance();
                        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                        calendar.set(java.util.Calendar.MINUTE, 0);
                        calendar.set(java.util.Calendar.SECOND, 0);
                        calendar.set(java.util.Calendar.MILLISECOND, 0);
                        calendar.set(java.util.Calendar.YEAR, CalendarUtil.year);
                        calendar.set(java.util.Calendar.MONTH, CalendarUtil.month);
                        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
                        mICalendarDialogManger.update(calendar.getTimeInMillis(), true);
                    }
                });
                if (!getActivity().isFinishing()) {
                    adjustCycleLengthDialog.show();
                }
                break;
            case R.id.lv_menstrual_length:
                LogUtils.moreLog("click_menstrualLength");
                AdjustMentrualLengthDialog adjustMentrualLengthDialog = new AdjustMentrualLengthDialog(getActivity());
                adjustMentrualLengthDialog.setOnClickListenerSave(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] result = adjustMentrualLengthDialog.getResult();
                        mIDataManager.setMenstrualDuration(result[0]);
                        mTvMenstrualDays.setText(mIDataManager.getMenstrualDuration() + " " + XCoreFactory.getApplication().getResources().getString(R.string.days));
                        java.util.Calendar calendar = java.util.Calendar.getInstance();
                        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                        calendar.set(java.util.Calendar.MINUTE, 0);
                        calendar.set(java.util.Calendar.SECOND, 0);
                        calendar.set(java.util.Calendar.MILLISECOND, 0);
                        calendar.set(java.util.Calendar.YEAR, CalendarUtil.year);
                        calendar.set(java.util.Calendar.MONTH, CalendarUtil.month);
                        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
                        mICalendarDialogManger.update(calendar.getTimeInMillis(), true);
                    }
                });
                if (!getActivity().isFinishing()) {
                    adjustMentrualLengthDialog.show();
                }
                break;
            case R.id.lv_reminder_time:
                LogUtils.moreLog("click_menstruationStartReminder");
                ReminderActivity.launch(getActivity(), RemindConstant.VAULE_INT_TYPE_MEN_START);
                break;
            case R.id.lv_end_menstrua:
                LogUtils.moreLog("click_menstruationEndReminder");
                ReminderActivity.launch(getActivity(), RemindConstant.VAULE_INT_TYPE_MEN_END);
                break;

            case R.id.lv_ovulation_start_prompt:
                LogUtils.moreLog("click_ovulationStartReminder");
                ReminderActivity.launch(getActivity(), RemindConstant.VAULE_INT_TYPE_OVULATION_START);
                break;
            case R.id.lv_ovulation_day_prompt:
                LogUtils.moreLog("click_ovulationDayReminder");
                ReminderActivity.launch(getActivity(), RemindConstant.VAULE_INT_TYPE_OVULATION_DAY);
                break;
            case R.id.lv_ovulation_end_prompt:
                LogUtils.moreLog("click_ovulationEndReminder");
                ReminderActivity.launch(getActivity(), RemindConstant.VAULE_INT_TYPE_OVULATION_END);
                break;
            case R.id.lin_privacy:
                LogUtils.moreLog("click_privacy");
                IntentUtil.openBrowse(getActivity(), Constants.VALUE_STRING_PRIVACY_POLICY_URL);
                break;
            case R.id.lin_feedback:
                IntentUtil.toEmail(getActivity());
                LogUtils.moreLog("click_feedback");
                break;
            case R.id.lin_about:
                AboutActivity.start(getActivity());
                LogUtils.moreLog("click_about");
                break;
        }
    }

}
