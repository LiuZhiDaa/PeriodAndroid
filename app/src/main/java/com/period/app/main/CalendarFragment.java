package com.period.app.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.period.app.R;

import com.period.app.base.BaseFragment;
import com.period.app.bean.DatePhysiologyBean;
import com.period.app.core.XCoreFactory;
import com.period.app.core.calendardialog.ICalendarDialogManger;
import com.period.app.core.calendardialog.ICalendarListener;
import com.period.app.core.data.IDataMgr;
import com.period.app.core.dba.IDbaManger;
import com.period.app.core.prediction.IPredictionManger;
import com.period.app.dialog.LoadingDialog;
import com.period.app.utils.CalendarUtil;
import com.period.app.utils.LogUtils;
import com.period.app.utils.OnClickListener;
import com.period.app.widget.ColorfulMonthView;
import com.period.app.widget.RoundImageView;

import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;

import ulric.li.utils.UtilsLog;

/**
 * Created by WangGuiLi
 * on 2018/12/11
 * 日历界面
 */
public class CalendarFragment extends BaseFragment implements
        CalendarView.OnCalendarSelectListener {

    //最近月经开始时间  1543593600
    @BindView(R.id.calendarView)
    CalendarView calendarView;
    @BindView(R.id.iv_pre)
    RelativeLayout ivPre;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.iv_next)
    RelativeLayout ivNext;
    @BindView(R.id.calendar_edit_tv)
    TextView mTextView;
    @BindView(R.id.cardview)
    CardView cardview;
    @BindView(R.id.text_menstrual)
    TextView textMenstrual;
    @BindView(R.id.checkbox_menstrual)
    CheckBox checkboxMenstrual;
    @BindView(R.id.rl_checkbox_menstrual)
    RelativeLayout rlcheckboxmenstrual;
    @BindView(R.id.back_today)
    RelativeLayout backToday;
    @BindView(R.id.roundimg)
    RoundImageView roundimg;
    @BindView(R.id.loadView)
    RelativeLayout loadView;
    int selectYear = 0;
    int selectMonth = 0;
    int selectDay = 0;
    @BindView(R.id.fl_native_ad)
    FrameLayout mFlNativeAd;
    private long mExitTime = 0;
    private IPredictionManger mIPredictionManger;
    private ICalendarDialogManger mICalendarDialogManger;
    private IDbaManger mIDbaManger;
    private IDataMgr mIDataMgr;
    private long selecttTime;
    private long currentTime;
    private LoadingDialog mLoadingDialog;

    //0 设置开始 1 取消开始 2设置结束 3取消结束 4修改结束后移  5 修改结束前移
    int startOrend = -1;
    private ICalendarListener mICalendarListener = new ICalendarListener() {
        @Override
        public void toUpdate(int year, int month, @NonNull boolean reset) {
            super.toUpdate(year, month, reset);
            Log.d("lzd", "得到更新通知时间  =======" + System.currentTimeMillis());
            if (null != mLoadingDialog && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
            if (reset) {
                mICalendarDialogManger.updateFragment();
            }
            if (calendarView != null) {
                currentTime = CalendarUtil.getZeroDate(System.currentTimeMillis());
                calendarView.post(() -> calendarView.updateCurrentDate());
                calendarView.clearSchemeDate();
                calendarView.setSchemeDate(mIPredictionManger.getpredictionMa(year, month + 1, currentTime));
                if (selecttTime > currentTime) {
                    cardview.setVisibility(View.INVISIBLE);
                    roundimg.setVisibility(View.INVISIBLE);
                    mTextView.setText(R.string.future_data_is_not_editable);
                    mTextView.setVisibility(View.VISIBLE);
                } else {
                    roundimg.setVisibility(View.VISIBLE);
                    cardview.setVisibility(View.VISIBLE);
                    mTextView.setVisibility(View.GONE);
                    DatePhysiologyBean bean = mIDbaManger.queryPredictionData(selecttTime);
                    DatePhysiologyBean beforeEnd = mIDbaManger.queryTimebeforeEndData(selecttTime);
                    //0 设置开始 1 取消开始 2设置结束 3取消结束
                    if (bean == null) {
                        //设置开始
                        textMenstrual.setText(R.string.menstrualstart);
                        checkboxMenstrual.post(() -> {
                            checkboxMenstrual.setChecked(false);
                        });

                        startOrend = 0;
                    } else {
                        if (bean.getIsPeriodStart() || bean.getIsPeriodEnd() || bean.getCurrentState() == 1) {
                            //是开始或结束或是月经中的其他时间
                            if (bean.getIsPeriodStart()) {
                                textMenstrual.setText(R.string.menstrualstart);
                                checkboxMenstrual.post(() -> checkboxMenstrual.setChecked(true));
                                startOrend = 1;
                            } else if (bean.getIsPeriodEnd()) {
                                textMenstrual.setText(R.string.menstrualend);
                                checkboxMenstrual.post(() -> checkboxMenstrual.setChecked(true));
                                startOrend = 3;
                            } else {
                                textMenstrual.setText(R.string.menstrualend);
                                checkboxMenstrual.post(() -> {
                                    checkboxMenstrual.setChecked(false);
                                });
                                startOrend = 2;
                            }
                        } else {
                            //之前有没有结束
                            if (beforeEnd == null) {
                                //没有结束则一定是设置开始
                                textMenstrual.setText(R.string.menstrualstart);
                                checkboxMenstrual.post(() -> {
                                    checkboxMenstrual.setChecked(false);
                                });
                                startOrend = 0;
                            } else {
                                //有结束 则判断选中时间与结束时间是否超过5天
                                int size = CalendarUtil.daysBetween(beforeEnd.getCurrentDate(), selecttTime);
                                if (size > 5) {
                                    //大于5设置开始
                                    textMenstrual.setText(R.string.menstrualstart);
                                    checkboxMenstrual.post(() -> {
                                        checkboxMenstrual.setChecked(false);
                                    });
                                    startOrend = 0;
                                } else {
                                    textMenstrual.setText(R.string.menstrualend);
                                    checkboxMenstrual.post(() -> {
                                        checkboxMenstrual.setChecked(false);
                                    });
                                    startOrend = 2;
                                }
                            }
                        }

                    }


                }
            }
        }
    };



    @Override
    protected void init(View rootView) {
        mLoadingDialog = new LoadingDialog(Objects.requireNonNull(getActivity()));
        mIPredictionManger = (IPredictionManger) XCoreFactory.getInstance().createInstance(IPredictionManger.class);
        mICalendarDialogManger = (ICalendarDialogManger) XCoreFactory.getInstance().createInstance(ICalendarDialogManger.class);
        mIDbaManger = (IDbaManger) XCoreFactory.getInstance().createInstance(IDbaManger.class);
        mIDataMgr = (IDataMgr) XCoreFactory.getInstance().createInstance(IDataMgr.class);
        mICalendarDialogManger.addListener(mICalendarListener);
        ivNext.setOnClickListener(v -> {
            UtilsLog.statisticsLog("calendar", "click_nextMonth", null);
            calendarView.scrollToNext();
        });
        ivPre.setOnClickListener(v -> {
            UtilsLog.statisticsLog("calendar", "click_lastMonth", null);
            calendarView.scrollToPre();
        });
        calendarView.setWeekStarWithSun();
        calendarView.setMonthView(ColorfulMonthView.class);
        calendarView.scrollToCurrent();
        calendarView.setOnCalendarSelectListener(this);

        mTextView.setVisibility(View.GONE);
        int year = calendarView.getCurYear();
        int month = calendarView.getCurMonth();
        int day = calendarView.getCurDay();
        CalendarUtil.year = year;
        CalendarUtil.month = month - 1;

        currentTime = CalendarUtil.getZeroDate(System.currentTimeMillis());

        if (mIDataMgr.getFirstOpenTime() == 1000) {
            mIDataMgr.setFirstOpenTime(currentTime);
        }
        mIDataMgr.setEveryTime(CalendarUtil.getZeroDate(System.currentTimeMillis()));
        String[] monthArray = getResources().getStringArray(R.array.months);
        calendarView.setOnMonthChangeListener((year1, month1) -> calendarView.post(() -> {
            CalendarUtil.year = year1;
            CalendarUtil.month = month1 - 1;
            tvDate.setText(String.format(Locale.getDefault(), "%s-%d", monthArray[month1 - 1], year1));
            if (calendarView!=null){
                calendarView.clearSchemeDate();
                calendarView.setSchemeDate(mIPredictionManger.getpredictionMa(year1, month1, currentTime));
            }
        }));
        backToday.setOnClickListener(v -> {
            calendarView.scrollToCurrent();
            UtilsLog.statisticsLog("calendar", "click_today", null);
        });
        rlcheckboxmenstrual.setOnClickListener(new OnClickListener() {
            @Override
            protected void myOnClickListener(View v) {
                if (startOrend < 0)
                    return;
                if ((System.currentTimeMillis() - mExitTime) > 1000) {
                    mExitTime = System.currentTimeMillis();
//                    loadView.setVisibility(View.VISIBLE);

                    mLoadingDialog.setCancelable(false);
                    mLoadingDialog.setOnKeyListener((dialog, keyCode, event) -> {
                        if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_BACK)
                            return true;
                        return false;
                    });
                    mLoadingDialog.setCanceledOnTouchOutside(false);
                    if (!getActivity().isFinishing()) {
                        mLoadingDialog.show();
                    }
                    Log.d("lzd", "设置开始操作  =======" + System.currentTimeMillis());
                    if (checkboxMenstrual.isChecked()) {
                        mICalendarDialogManger.HandlingClickEvents(true, startOrend, selecttTime, getActivity());
                        checkboxMenstrual.setChecked(true);
                    } else {
                        mICalendarDialogManger.HandlingClickEvents(false, startOrend, selecttTime, getActivity());
                        checkboxMenstrual.setChecked(false);
                    }

                }
            }
        });


    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_calendar;
    }

    @Override
    protected void onLazyLoad() {

    }


    @Override
    public void onCalendarOutOfRange(Calendar calendar) {
    }

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        if (isClick) {
            LogUtils.calendarLog("click_selectDate");
        }
        java.util.Calendar calendars = java.util.Calendar.getInstance();
        calendars.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendars.set(java.util.Calendar.MINUTE, 0);
        calendars.set(java.util.Calendar.SECOND, 0);
        calendars.set(java.util.Calendar.MILLISECOND, 0);
        calendars.set(java.util.Calendar.YEAR, calendar.getYear());
        calendars.set(java.util.Calendar.MONTH, calendar.getMonth() - 1);
        calendars.set(java.util.Calendar.DAY_OF_MONTH, calendar.getDay());
        selectYear = calendar.getYear();
        selectMonth = calendar.getMonth() - 1;
        selectDay = calendar.getDay();
        selecttTime = calendars.getTimeInMillis();
        mICalendarDialogManger.update(calendar.getTimeInMillis(), false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mICalendarDialogManger != null) {
            mICalendarDialogManger.removeListener(mICalendarListener);
        }
    }

}
