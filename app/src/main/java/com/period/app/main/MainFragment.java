package com.period.app.main;

import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.period.app.R;
import com.period.app.base.BaseFragment;
import com.period.app.bean.DatePhysiologyBean;
import com.period.app.constants.PeriodConstant;
import com.period.app.core.XCoreFactory;
import com.period.app.core.calendardialog.ICalendarDialogManger;
import com.period.app.core.calendardialog.ICalendarListener;
import com.period.app.core.data.IDataMgr;
import com.period.app.core.dba.IDbaManger;
import com.period.app.core.prediction.IPredictionManger;
import com.period.app.dialog.PeriodOperationDialog;
import com.period.app.utils.CalendarUtil;
import com.period.app.utils.DialogUtils;
import com.period.app.utils.LogUtils;
import com.period.app.utils.StatusBarUtils;

import java.util.Calendar;
import java.util.Objects;
import java.util.logging.Handler;

import butterknife.BindView;
import butterknife.OnClick;
import ulric.li.utils.UtilsLog;

/**
 * Created by WangGuiLi
 * on 2018/12/11
 * 首页
 */
public class MainFragment extends BaseFragment {

    private static final String TAG = "MainFragment";
    @BindView(R.id.iv_bg)
    ImageView mIvBg;
    @BindView(R.id.iv_top_bg)
    ImageView mIvTopBg;
    @BindView(R.id.tv_circle_forecast)
    TextView mTvForecast;
    @BindView(R.id.tv_main_title)
    TextView mTvMainTitle;
    @BindView(R.id.tv_content)
    TextView mTvContent;
    @BindView(R.id.tv_sub_content)
    TextView mTvSubContent;
    @BindView(R.id.iv_circle)
    ImageView mIvCircle;
    @BindView(R.id.tv_circle_sub_title)
    TextView mTvCircleSubTitle;
    @BindView(R.id.tv_circle_main_title)
    TextView mTvCircleMainTitle;
    @BindView(R.id.rl_circle_content)
    ConstraintLayout mRlCircleContent;
    @BindView(R.id.tv_date)
    TextView mTvDate;
    @BindView(R.id.bt_operation)
    Button mBtOperation;
    @BindView(R.id.tv_operation)
    TextView mTvOperation;
    private IPredictionManger mMIpredictionManger;
    private ICalendarDialogManger mICalendarDialogManger;
    private IDbaManger mIDbaManger;
    private int mState;
    IDataMgr mIDataMgr;
    private int mOperationStage;

    @Override
    protected void init(View rootView) {
        mICalendarDialogManger = (ICalendarDialogManger) XCoreFactory.getInstance().createInstance(ICalendarDialogManger.class);
        mIDbaManger = (IDbaManger) XCoreFactory.getInstance().createInstance(IDbaManger.class);
        mIDataMgr = (IDataMgr) XCoreFactory.getInstance().createInstance(IDataMgr.class);
        mICalendarDialogManger.addListener(mICalendarListener);
        initUI(true);
    }

    /**
     * 设置日期
     */
    private void setDate() {
        //当前日期
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String[] weeks = getResources().getStringArray(R.array.weeks);
        String[] months = getResources().getStringArray(R.array.months);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String date = getString(R.string.str_format_main_date, weeks[dayOfWeek - 1], months[month], day);
        mTvDate.setText(date);
        mIDataMgr.setOpenMainFragmentTime(System.currentTimeMillis());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isResumed())
            return;
        if (isVisibleToUser)
            initUI(true);
    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @OnClick(R.id.bt_operation)
    public void onViewClicked() {
        PeriodOperationDialog periodOperationDialog = new PeriodOperationDialog(Objects.requireNonNull(getActivity()), mOperationStage);
        periodOperationDialog.setOnClickListenerSave(v -> {
            long selectTime = periodOperationDialog.getSelectTime();
            if (mOperationStage == PeriodConstant.VALUE_INT_OPERATION_MENSTRUATION_START) {
                LogUtils.ThreeFieldLog("main", "click_confirm", "setMenstruationStart");
                mICalendarDialogManger.SettingStart(selectTime);
            } else if (mOperationStage == PeriodConstant.VALUE_INT_OPERATION_MENSTRUATION_END) {
                LogUtils.ThreeFieldLog("main", "click_confirm", "setMenstruationEnd");
                mICalendarDialogManger.SettingEnd(selectTime);
            }
            initUI(true);
        });
        if (!getActivity().isFinishing()) {
            periodOperationDialog.show();
        }
    }

    private void initBtOperation() {
        mOperationStage = mIDataMgr.getState(CalendarUtil.getZeroDate(System.currentTimeMillis()));
        if (mOperationStage == PeriodConstant.VALUE_INT_OPERATION_MENSTRUATION_START) {
            String title = getResources().getString(R.string.menstruation_start);
            showBt(title);
        } else if (mOperationStage == PeriodConstant.VALUE_INT_OPERATION_MENSTRUATION_END) {
            String title = getResources().getString(R.string.menstruation_end);
            showBt(title);
        } else if (mOperationStage == PeriodConstant.VALUE_INT_OPERATION_MENSTRUATION) {
            showTv();
        } else if (mOperationStage == PeriodConstant.VALUE_INT_OPERATION_SAFETY) {
            showTv();
        } else if (mOperationStage == PeriodConstant.VALUE_INT_OPERATION_PREGNANCY_PROBABILITY) {
            showTv();
        } else {
            showTv();
        }
    }

    private void showBt(String title) {
        mTvOperation.setVisibility(View.GONE);
        mBtOperation.setVisibility(View.VISIBLE);
        mBtOperation.setText(title);
    }

    private void showTv() {
        mTvOperation.setVisibility(View.VISIBLE);
        mBtOperation.setVisibility(View.GONE);
        mBtOperation.setText(R.string.operation_tips);
    }

    private ICalendarListener mICalendarListener = new ICalendarListener() {
        @Override
        public void toUpdate(int year, int month, boolean isreset) {
            super.toUpdate(year, month, isreset);
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                initUI(false);
            });
        }
    };

    /**
     * 设置当前状态
     */
    private void setSate() {
        if (mIDbaManger == null)
            mIDbaManger = (IDbaManger) XCoreFactory.getInstance().createInstance(IDbaManger.class);
        if (mMIpredictionManger == null)
            mMIpredictionManger = (IPredictionManger) XCoreFactory.getInstance().createInstance(IPredictionManger.class);

        DatePhysiologyBean datePhysiologyBean = mIDbaManger.queryPredictionData(CalendarUtil.getZeroDate(System.currentTimeMillis()));
        if (datePhysiologyBean == null) {
            //查询当前时间之前是否有经期开始时间
            DatePhysiologyBean beforeDatePhysiologyBean = mIDbaManger.queryTimebeforeStartData(CalendarUtil.getZeroDate(System.currentTimeMillis()));
            if (beforeDatePhysiologyBean != null) {
                mState = 0;
                setForecastState();
            } else {
                mState = -1;
                setNoDataState();
            }
        } else {
            mState = datePhysiologyBean.getCurrentState();
            int dayInPeriod = Math.abs(mIDbaManger.querySameStateCountInMainDay(datePhysiologyBean.getCurrentDate(), datePhysiologyBean.getCurrentState())) + 1;
            if (mState == 1) {
                mTvForecast.setVisibility(View.GONE);
                setPeriodState(dayInPeriod);
            } else if (mState == 2) {
                setSafetyState(dayInPeriod);
            } else if (mState == 3) {
                setOvulationState(dayInPeriod);
            } else if (mState == 4) {
                setOvulationDayState();
            }
        }
    }

    private void setSafetyState(int dayInPeriod) {
        mTvMainTitle.setBackgroundColor(getResources().getColor(R.color.mainFragmentBtTextGreenColor));
        mIvTopBg.setBackgroundColor(getResources().getColor(R.color.mainFragmentBtTextGreenColor));
        mTvForecast.setVisibility(View.GONE);
        mIvBg.setImageResource(R.drawable.icon_green_bg);
//        mIvCircle.setImageResource(R.drawable.icon_green_circle);
        mBtOperation.setBackground(getResources().getDrawable(R.drawable.bt_main_fragment_green_backgroud));
//        mBtOperation.setTextColor(getResources().getColor(R.color.mainFragmentBtTextGreenColor));
//        mTvSubTitle.setText(R.string.welcom_to);
//        mTvMainTitle.setText(R.string.period_calendar);
        mTvContent.setText(R.string.low_probability);
        mTvSubContent.setText(R.string.ovulation_period);
        mTvCircleSubTitle.setVisibility(View.VISIBLE);
        mTvCircleSubTitle.setText(R.string.safety_in);
        mTvCircleMainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.main_fragment_circle_sub_text_size));
        mTvCircleMainTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mTvCircleMainTitle.setText(getString(R.string.format_days, Math.abs(dayInPeriod)));
        mTvCircleMainTitle.setTextColor(getResources().getColor(R.color.mainFragmentCircleTitleTextColor));
        mTvCircleMainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.main_fragment_circle_sub_text_size));
    }

    private void setOvulationState(int dayInPeriod) {
        mTvMainTitle.setBackgroundColor(getResources().getColor(R.color.mainFragmentBtTextPurpleColor));
        mIvTopBg.setBackgroundColor(getResources().getColor(R.color.mainFragmentBtTextPurpleColor));
        mTvForecast.setVisibility(View.GONE);
        mIvBg.setImageResource(R.drawable.icon_purple_bg);
//        mIvCircle.setImageResource(R.drawable.icon_purple_circle);
        mBtOperation.setBackground(getResources().getDrawable(R.drawable.bt_main_fragment_purple_backgroud));
//        mBtOperation.setTextColor(getResources().getColor(R.color.mainFragmentBtTextPurpleColor));
//        mTvSubTitle.setText(R.string.welcom_to);
//        mTvMainTitle.setText(R.string.period_calendar);
        mTvContent.setText(R.string.medium_probability);
        mTvSubContent.setText(R.string.ovulation_period);
        mTvCircleSubTitle.setVisibility(View.VISIBLE);
        mTvCircleSubTitle.setText(R.string.ovulation_in);
        mTvCircleMainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.main_fragment_circle_sub_text_size));
        mTvCircleMainTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mTvCircleMainTitle.setText(getString(R.string.format_days, Math.abs(dayInPeriod)));
        mTvCircleMainTitle.setTextColor(getResources().getColor(R.color.mainFragmentCircleTitleTextColor));
        mTvCircleMainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.main_fragment_circle_sub_text_size));
    }

    private void setOvulationDayState() {
        mTvMainTitle.setBackgroundColor(getResources().getColor(R.color.mainFragmentBtTextPurpleColor));
        mIvTopBg.setBackgroundColor(getResources().getColor(R.color.mainFragmentBtTextPurpleColor));
        mTvForecast.setVisibility(View.GONE);
        mIvBg.setImageResource(R.drawable.icon_purple_bg);
//        mIvCircle.setImageResource(R.drawable.icon_purple_circle);
        mBtOperation.setBackground(getResources().getDrawable(R.drawable.bt_main_fragment_purple_backgroud));
//        mBtOperation.setTextColor(getResources().getColor(R.color.mainFragmentBtTextPurpleColor));
//        mTvSubTitle.setText(R.string.welcom_to);
//        mTvMainTitle.setText(R.string.period_calendar);
        mTvContent.setText(R.string.high_probability);
        mTvSubContent.setText(R.string.ovulation_period);
        mTvCircleMainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.main_fragment_circle_ovu_day_text_size));
        mTvCircleSubTitle.setVisibility(View.GONE);
        mTvCircleMainTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mTvCircleMainTitle.setText(R.string.ovulation_day);
        mTvCircleMainTitle.setTextColor(getResources().getColor(R.color.mainFragmentCircleTitleTextColor));
        mTvCircleMainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.main_fragment_circle_sub_text_size));
    }


    private void setForecastState() {
        mTvMainTitle.setBackgroundColor(getResources().getColor(R.color.mainFragmentBtTextPinkColor));
        mIvTopBg.setBackgroundColor(getResources().getColor(R.color.mainFragmentBtTextPinkColor));
        mIvBg.setImageResource(R.drawable.icon_pink_bg);
        mBtOperation.setBackground(getResources().getDrawable(R.drawable.bt_main_fragment_pink_backgroud));
        mTvContent.setText(R.string.low_probability);
        mTvSubContent.setText(R.string.men_period);
        mTvCircleSubTitle.setVisibility(View.VISIBLE);
        mTvCircleSubTitle.setText(R.string.forecast);
        mTvCircleMainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.main_fragment_circle_sub_text_size));
        mTvCircleMainTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mTvCircleMainTitle.setTextColor(getResources().getColor(R.color.mainFragmentCircleTitleTextColor));
        mTvCircleMainTitle.setText(R.string.menstruation);
        mTvCircleMainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.main_fragment_circle_forecast_sub_text_size));
    }

    private void setPeriodState(int dayInPeriod) {
        mTvMainTitle.setBackgroundColor(getResources().getColor(R.color.mainFragmentBtTextPinkColor));
        mIvTopBg.setBackgroundColor(getResources().getColor(R.color.mainFragmentBtTextPinkColor));
        mIvBg.setImageResource(R.drawable.icon_pink_bg);
//        mIvCircle.setImageResource(R.drawable.icon_pink_circle);
        mBtOperation.setBackground(getResources().getDrawable(R.drawable.bt_main_fragment_pink_backgroud));
//        mBtOperation.setTextColor(getResources().getColor(R.color.mainFragmentBtTextPinkColor));
//        mTvSubTitle.setText(R.string.welcom_to);
//        mTvMainTitle.setText(R.string.period_calendar);
        mTvContent.setText(R.string.low_probability);
        mTvSubContent.setText(R.string.men_period);
        mTvCircleSubTitle.setVisibility(View.VISIBLE);
        mTvCircleSubTitle.setText(R.string.period_in);
        mTvCircleMainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.main_fragment_circle_sub_text_size));
        mTvCircleMainTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mTvCircleMainTitle.setTextColor(getResources().getColor(R.color.mainFragmentCircleTitleTextColor));
        mTvCircleMainTitle.setText(getString(R.string.format_days, Math.abs(dayInPeriod)));
        mTvCircleMainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.main_fragment_circle_sub_text_size));
    }

    private void setNoDataState() {
        mTvMainTitle.setBackgroundColor(getResources().getColor(R.color.mainFragmentBtTextPinkColor));
        mIvTopBg.setBackgroundColor(getResources().getColor(R.color.mainFragmentBtTextPinkColor));
        mTvForecast.setVisibility(View.GONE);
        mIvBg.setImageResource(R.drawable.icon_pink_bg);
//        mIvCircle.setImageResource(R.drawable.icon_pink_circle);
        mBtOperation.setBackground(getResources().getDrawable(R.drawable.bt_main_fragment_pink_backgroud));
//        mBtOperation.setTextColor(getResources().getColor(R.color.mainFragmentBtTextPinkColor));
//        mTvSubTitle.setText(R.string.welcom_to);
//        mTvMainTitle.setText(R.string.period_calendar);
        mTvContent.setText(R.string.please_set);
        mTvSubContent.setText(R.string.the_latest_men_day);
        mTvCircleSubTitle.setVisibility(View.GONE);
        mTvCircleMainTitle.setText(R.string.no_data);
        mTvCircleMainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.main_fragment_circle_sub_text_size));
        mTvCircleMainTitle.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        mTvCircleMainTitle.setTextColor(getResources().getColor(R.color.settingItemTitleDaysTextColor));
        mTvCircleMainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.main_fragment_circle_sub_text_size));
    }

    private void initUI(boolean isReset) {
        setDate();
        setSate();
        initBtOperation();
        if (isReset) {
            setMainFragmentTitleBar();
        }

    }

    /**
     * 设置状态栏
     */
    private void setMainFragmentTitleBar() {
        if (getActivity() == null) {
            return;
        }
        if (mMIpredictionManger == null)
            mMIpredictionManger = (IPredictionManger) XCoreFactory.getInstance().createInstance(IPredictionManger.class);

        if (mState == 0 || mState == 1) {
            StatusBarUtils.setWindowStatusBarColor(getActivity(), R.color.mainFragmentBtTextPinkColor);
        } else if (mState == 4) {
            StatusBarUtils.setWindowStatusBarColor(getActivity(), R.color.mainFragmentBtTextPurpleColor);
        } else if (mState == 2) {
            StatusBarUtils.setWindowStatusBarColor(getActivity(), R.color.mainFragmentBtTextGreenColor);
        } else if (mState == 3) {
            StatusBarUtils.setWindowStatusBarColor(getActivity(), R.color.mainFragmentBtTextPurpleColor);
        } else {
            StatusBarUtils.setWindowStatusBarColor(getActivity(), R.color.mainFragmentBtTextPinkColor);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mICalendarDialogManger != null) {
            mICalendarDialogManger.removeListener(mICalendarListener);
        }

    }
}
