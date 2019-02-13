package com.period.app.widget.wheelview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.period.app.R;
import com.period.app.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author XuChuanting
 * on 2018/12/18-11:49
 */
public class WheelDateView extends FrameLayout {
    @BindView(R.id.wheel_view_year)
    WheelView wheelViewYear;
    @BindView(R.id.wheel_view_month)
    WheelView wheelViewMonth;
    @BindView(R.id.wheel_view_day)
    WheelView wheelViewDay;

    int yearStart, monthStart, dayStart;
    int yearEnd, monthEnd, dayEnd;
    private List<String> mListYears;
    private List<String> mListMonths;
    private List<String> mListDays;
    private int mYearInit;
    private int mMonthInit;
    private int mDayInit;

    private long mStartTime;

    int textColorOuter = 0xffbbbbbb;
    int textColorCenter = 0xff4d4d4d;
    int lineColor = 0xffe6e6e6;

    public WheelDateView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public WheelDateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_wheel_date, this);
        ButterKnife.bind(this);

        Calendar calendar = Calendar.getInstance();
        yearEnd = calendar.get(Calendar.YEAR);
        monthEnd = calendar.get(Calendar.MONTH) + 1;
        dayEnd = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.YEAR, -1);
        yearStart = calendar.get(Calendar.YEAR);
        monthStart = calendar.get(Calendar.MONTH) + 1;
        dayStart = calendar.get(Calendar.DAY_OF_MONTH);

        mListYears = new ArrayList<>();
        mListMonths = new ArrayList<>();
        mListDays = new ArrayList<>();

        mYearInit = yearEnd;
        mMonthInit = monthEnd;
        mDayInit = dayEnd;
        initDate();
    }

    private void init(Context context,AttributeSet attrs) {
        inflate(context, R.layout.view_wheel_date, this);
        ButterKnife.bind(this);

        Calendar calendar = Calendar.getInstance();
        yearEnd = calendar.get(Calendar.YEAR);
        monthEnd = calendar.get(Calendar.MONTH) + 1;
        dayEnd = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.YEAR, -1);
        yearStart = calendar.get(Calendar.YEAR);
        monthStart = calendar.get(Calendar.MONTH) + 1;
        dayStart = calendar.get(Calendar.DAY_OF_MONTH);

        mListYears = new ArrayList<>();
        mListMonths = new ArrayList<>();
        mListDays = new ArrayList<>();

        mYearInit = yearEnd;
        mMonthInit = monthEnd;
        mDayInit = dayEnd;
        initDate();
        initWheelView(context,attrs);
    }
    private void initWheelView(Context context,AttributeSet attrs){
        TypedArray attribute = context.obtainStyledAttributes(attrs, R.styleable.WheelView);
        lineColor = attribute.getColor(R.styleable.WheelView_lineColor, lineColor);
        textColorCenter = attribute.getColor(R.styleable.WheelView_textColorCenter, textColorCenter);
        textColorOuter = attribute.getColor(R.styleable.WheelView_textColorOuter, textColorOuter);

        attribute.recycle();


        wheelViewYear.setCenterTextColor(textColorCenter);
        wheelViewMonth.setCenterTextColor(textColorCenter);
        wheelViewDay.setCenterTextColor(textColorCenter);

        wheelViewYear.setOuterTextColor(textColorOuter);
        wheelViewMonth.setOuterTextColor(textColorOuter);
        wheelViewDay.setOuterTextColor(textColorOuter);

        wheelViewYear.setDividerColor(lineColor);
        wheelViewMonth.setDividerColor(lineColor);
        wheelViewDay.setDividerColor(lineColor);
    }

    private void initDate() {
        mListYears.clear();
        for (int i = yearStart; i < yearEnd + 1; i++) {
            mListYears.add(i + "");
        }
        int yearIndex = mListYears.indexOf(String.valueOf(mYearInit));
        if (yearIndex == -1) {
            yearIndex = 0;
        }
        wheelViewYear.setItems(mListYears, yearIndex);
        resetMonths();
        resetDays();
        wheelViewYear.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selectedIndex, String item) {
                mMonthInit = Integer.parseInt(wheelViewMonth.getSelectedItem());
                mDayInit = Integer.parseInt(wheelViewDay.getSelectedItem());
                resetMonths();
                resetDays();
            }
        });

        wheelViewMonth.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selectedIndex, String item) {
                mDayInit = Integer.parseInt(wheelViewDay.getSelectedItem());
                resetDays();
            }
        });
    }

    public void setStartDate(int year, int month, int day) {
        yearStart = year;
        monthStart = month;
        dayStart = day;
        initDate();
    }

    public void setEndDate(int year, int month, int day) {
        yearEnd = year;
        monthEnd = month;
        dayEnd = day;
        initDate();
    }

    public void setStartDate(long startDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDate);
        yearStart = calendar.get(Calendar.YEAR);
        monthStart = calendar.get(Calendar.MONTH) + 1;
        dayStart = calendar.get(Calendar.DAY_OF_MONTH);
        initDate();
    }

    public void setEndDate(long endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(endDate);
        yearEnd = calendar.get(Calendar.YEAR);
        monthEnd = calendar.get(Calendar.MONTH) + 1;
        dayEnd = calendar.get(Calendar.DAY_OF_MONTH);
        initDate();
    }

    private void resetMonths() {
        int year = Integer.parseInt(wheelViewYear.getSelectedItem());
        mListMonths.clear();
        if(yearStart==yearEnd){
            for (int i = monthStart; i < monthEnd + 1; i++) {
                mListMonths.add(i + "");
            }
        }else {
            if (year == yearStart) {
                for (int i = monthStart; i < 12 + 1; i++) {
                    mListMonths.add(i + "");
                }
            } else if (year == yearEnd) {
                for (int i = 1; i < monthEnd + 1; i++) {
                    mListMonths.add(i + "");
                }
            } else {
                for (int i = 1; i < 13; i++) {
                    mListMonths.add(i + "");
                }
            }
        }
        int monthIndex = mListMonths.indexOf(String.valueOf(mMonthInit));
        if (monthIndex == -1) {
            monthIndex = 0;
        }
        wheelViewMonth.setItems(mListMonths, monthIndex);
    }

    private void resetDays() {
        int year = Integer.parseInt(wheelViewYear.getSelectedItem());
        int month = Integer.parseInt(wheelViewMonth.getSelectedItem());
        mListDays.clear();
        if(yearStart==yearEnd&&monthStart==monthEnd){
            for (int i = dayStart; i < dayEnd + 1; i++) {
                mListDays.add(i + "");
            }
        }else {
            int monthOfDay = TimeUtils.getMonthOfDay(year, month);
            if (year == yearStart && month == monthStart) {
                for (int i = dayStart; i < monthOfDay + 1; i++) {
                    mListDays.add(i + "");
                }
            } else if (year == yearEnd && month == monthEnd) {
                for (int i = 1; i < dayEnd + 1; i++) {
                    mListDays.add(i + "");
                }
            } else {
                for (int i = 1; i < monthOfDay + 1; i++) {
                    mListDays.add(i + "");
                }
            }
        }
        int dayIndex = mListDays.indexOf(String.valueOf(mDayInit));
        if (dayIndex == -1) {
            dayIndex = 0;
        }
        wheelViewDay.setItems(mListDays, dayIndex);
    }

    public void setInitDate(int year, int month, int day) {
        this.mYearInit = year;
        this.mMonthInit = month;
        this.mDayInit = day;
        initDate();
    }

    public int getSelectedYear() {
        return Integer.parseInt(wheelViewYear.getSelectedItem());
    }

    public int getSelectedMonth() {
        return Integer.parseInt(wheelViewMonth.getSelectedItem());
    }

    public int getSelectedDay() {
        return Integer.parseInt(wheelViewDay.getSelectedItem());
    }
}
