package com.period.app.main;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.period.app.R;
import com.period.app.base.BaseActivity;
import com.period.app.bean.DatePhysiologyBean;
import com.period.app.constants.PeriodConstant;
import com.period.app.core.XCoreFactory;
import com.period.app.core.calendardialog.ICalendarDialogManger;
import com.period.app.core.data.IDataMgr;
import com.period.app.core.dba.IDbaManger;
import com.period.app.core.prediction.IPredictionManger;
import com.period.app.utils.CalendarUtil;
import com.period.app.utils.LogUtils;
import com.period.app.utils.StatusBarUtils;
import com.period.app.widget.wheelview.WheelDateView;
import com.period.app.widget.wheelview.WheelView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;
import ulric.li.utils.UtilsLog;

/**
 * Created by WangGuiLi
 * on 2018/12/11
 */
public class WelcomActivity extends BaseActivity {

    public IDataMgr mIDataManager;
    @BindView(R.id.wheel_view_num)
    WheelView mWheelViewNumber;
    @BindView(R.id.bt_left)
    ImageButton mBtLeft;
    @BindView(R.id.bt_right)
    ImageButton mBtRight;
    @BindView(R.id.bt_skip)
    Button mSkip;
    @BindView(R.id.tv_wheel_view_title)
    TextView mTvTitle;
    @BindView(R.id.wheel_rc_men_period)
    WheelDateView mWheelDateView;
    int mCurrentPage = 0;
    private IDbaManger mIDbaMgr;

    private static final int VALUE_INT_DEFAULT_PHY_CYCLE = 28;
    private static final int VALUE_INT_DEFAULT_MENSTRUAL_DURATION = 5;
    private IPredictionManger mIpredictionMgr;
    private ICalendarDialogManger mICalendarDialogManger;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_welcom;
    }

    @Override
    public void init() {
        StatusBarUtils.setWindowStatusBarColor(this, R.color.splashActivityStartBgColor);
        mICalendarDialogManger = (ICalendarDialogManger) XCoreFactory.getInstance().createInstance(ICalendarDialogManger.class);
        mIDbaMgr = (IDbaManger) XCoreFactory.getInstance().createInstance(IDbaManger.class);
        mIDataManager = (IDataMgr) XCoreFactory.getInstance().createInstance(IDataMgr.class);
        mIpredictionMgr = (IPredictionManger) XCoreFactory.getInstance().createInstance(IPredictionManger.class);
        setBtVisibility();
        mWheelViewNumber.setVisibility(View.VISIBLE);
        mWheelDateView.setVisibility(View.GONE);
        mTvTitle.setText(R.string.welcom_phy_cycle_title);
        mWheelViewNumber.setItems(mIDataManager.getPhyCycleList(), mIDataManager.getDefaultPhyCycleIndex());
    }

    @OnClick({R.id.bt_left, R.id.bt_right, R.id.bt_skip, R.id.wheel_view_num})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_left:
                switchToPrePage();
                break;
            case R.id.bt_right:
                switchToNextPage();
                break;
            case R.id.bt_skip:
                switchToSkip();
                break;
        }
    }

    private void switchToSkip() {
        switch (mCurrentPage) {
            case 0:
                LogUtils.welcomeLogSkip("cycleLength");
                mIDataManager.setPhyCycle(VALUE_INT_DEFAULT_PHY_CYCLE+"");

                mCurrentPage++;
                setBtVisibility();
                mWheelViewNumber.setVisibility(View.VISIBLE);
                mWheelDateView.setVisibility(View.GONE);
                mTvTitle.setText(R.string.welcom_menstrual_period_title);
                mWheelViewNumber.setItems(mIDataManager.getMensPeriodList(), mIDataManager.getDefaultMenPeriodDurationIndex());
                break;
            case 1:
                LogUtils.welcomeLogSkip("periodLength");
                mIDataManager.setMenstrualDuration(VALUE_INT_DEFAULT_MENSTRUAL_DURATION+"");
                mCurrentPage++;
                mWheelViewNumber.setVisibility(View.GONE);
                mWheelDateView.setVisibility(View.VISIBLE);
                mTvTitle.setText(R.string.welcom_recent_menstrual_period_title);
                setBtVisibility();
                break;
            case 2:
                LogUtils.welcomeLogSkip("latestPeriodStart");
                mIDataManager.setManualstart(1000);

                mIDataManager.setIsInfoComplete(true);
                goActivity(MainActivity.class);
                finish();
                break;
        }
    }

    private void switchToNextPage() {
        switch (mCurrentPage) {
            case 0:
                LogUtils.welcomeLogNext("cycleLength");
                mIDataManager.setPhyCycle(mWheelViewNumber.getSelectedItem());

                mCurrentPage++;
                setBtVisibility();
                mWheelViewNumber.setVisibility(View.VISIBLE);
                mWheelDateView.setVisibility(View.GONE);
                mTvTitle.setText(R.string.welcom_menstrual_period_title);
                mWheelViewNumber.setItems(mIDataManager.getMensPeriodList(), mIDataManager.getDefaultMenPeriodDurationIndex());
                break;
            case 1:
                LogUtils.welcomeLogNext("periodLength");
                mIDataManager.setMenstrualDuration(mWheelViewNumber.getSelectedItem());
                mCurrentPage++;
                mWheelViewNumber.setVisibility(View.GONE);
                mWheelDateView.setVisibility(View.VISIBLE);
                mTvTitle.setText(R.string.welcom_recent_menstrual_period_title);
                setBtVisibility();
                break;
            case 2:
                LogUtils.welcomeLogNext("latestPeriodStart");
                int selectedYear = mWheelDateView.getSelectedYear();
                int selectedMonth = mWheelDateView.getSelectedMonth();
                int selectedDay = mWheelDateView.getSelectedDay();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, selectedYear);
                calendar.set(Calendar.MONTH, selectedMonth - 1);
                calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                long date = calendar.getTimeInMillis();
//                mIDataManager.setManualstart(CalendarUtil.getZeroDate(date));
//                mIDataManager.setRecentMenstrualPeriod(date);
//                mIpredictionMgr.savePredictDateList(date,System.currentTimeMillis());
                mICalendarDialogManger.SettingStart(CalendarUtil.getZeroDate(date));
                mIDataManager.setIsInfoComplete(true);
                goActivity(MainActivity.class);
                finish();
                break;
        }
    }

    private void switchToPrePage() {
        switch (mCurrentPage) {
            case 0:
                LogUtils.welcomeLogLast("cycleLength");

                break;
            case 1:
                LogUtils.welcomeLogLast("periodLength");
                mCurrentPage--;
                mWheelViewNumber.setVisibility(View.VISIBLE);
                mWheelDateView.setVisibility(View.GONE);
                setBtVisibility();
                mWheelViewNumber.setItems(mIDataManager.getPhyCycleList(), mIDataManager.getDefaultPhyCycleIndex());
                mTvTitle.setText(XCoreFactory.getApplication().getResources().getString(R.string.welcom_phy_cycle_title));
                break;
            case 2:
                LogUtils.welcomeLogLast("latestPeriodStart");
                mCurrentPage--;
                mWheelViewNumber.setVisibility(View.VISIBLE);
                mWheelDateView.setVisibility(View.GONE);
                setBtVisibility();
                mWheelViewNumber.setItems(mIDataManager.getMensPeriodList(), mIDataManager.getDefaultMenPeriodDurationIndex());
                mTvTitle.setText(XCoreFactory.getApplication().getResources().getString(R.string.welcom_menstrual_period_title));
                break;
        }
    }

    private void setBtVisibility() {
        switch (mCurrentPage) {
            case 0:
                mBtLeft.setVisibility(View.GONE);
                mBtRight.setVisibility(View.VISIBLE);
                mBtRight.setImageResource(R.drawable.icon_welcom_next_bt);
                break;
            case 1:
                mBtLeft.setVisibility(View.VISIBLE);
                mBtRight.setVisibility(View.VISIBLE);
                mBtRight.setImageResource(R.drawable.icon_welcom_next_bt);
                break;
            case 2:
                mBtLeft.setVisibility(View.VISIBLE);
                mBtRight.setVisibility(View.VISIBLE);
                mBtRight.setImageResource(R.drawable.icon_welcom_complete_bt);
                break;
        }
    }


}
