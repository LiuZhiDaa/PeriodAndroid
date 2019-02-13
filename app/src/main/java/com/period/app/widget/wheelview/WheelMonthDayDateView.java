package com.period.app.widget.wheelview;

import android.content.Context;
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
public class WheelMonthDayDateView extends FrameLayout {
    @BindView(R.id.wheel_view_years)
    WheelView wheelViewYear;
    @BindView(R.id.wheel_view_months)
    WheelView wheelViewMonth;
    @BindView(R.id.wheel_view_days)
    WheelView wheelViewDay;

    int yearStart, monthStart, dayStart;
    int yearEnd, monthEnd, dayEnd;
    private List<String> mListYears;
    private List<String> mListMonths;
    private List<String> mListDays;
    private int mYearInit;
    private int mMonthInit;
    private int mDayInit;

    public WheelMonthDayDateView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public WheelMonthDayDateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_wheel_monthdaydate, this);
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
        wheelViewYear.setOnItemSelectedListener((selectedIndex, item) -> {
            resetMonths();
            resetDays();
        });

        wheelViewMonth.setOnItemSelectedListener((selectedIndex, item) -> resetDays());
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






    private void resetMonths() {
        int year = Integer.parseInt(wheelViewYear.getSelectedItem());
        mListMonths.clear();
        if (yearStart == yearEnd) {
            for (int i = monthStart; i < monthEnd + 1; i++) {
                mListMonths.add(i + "");
            }
        } else {
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
        int monthOfDay = TimeUtils.getMonthOfDay(year, month);
        if (yearStart == yearEnd && monthStart == monthEnd) {
            for (int i = dayStart; i < dayEnd + 1; i++) {
                mListDays.add(i + "");
            }
        } else {
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

    public void setCenterTextColor(int textColorCenter) {
        wheelViewYear.setCenterTextColor(textColorCenter);
        wheelViewMonth.setCenterTextColor(textColorCenter);
        wheelViewDay.setCenterTextColor(textColorCenter);
    }
}
