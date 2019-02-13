package com.period.app.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.MonthView;
import com.period.app.R;
import com.period.app.bean.DatePhysiologyBean;
import com.period.app.core.XCoreFactory;
import com.period.app.core.dba.IDbaManger;
import com.period.app.utils.CalendarUtil;

import ulric.li.XLibFactory;
import ulric.li.xlib.intf.IXThreadPool;
import ulric.li.xlib.intf.IXThreadPoolListener;

/**
 * 高仿魅族日历布局
 * Created by huanghaibin on 2017/11/15.
 */

public class ColorfulMonthView extends MonthView {

    private int mRadius;
    /**
     * 圆点半径
     */
    private float mPointRadius;

    private int mPadding;
    protected Paint mLinePaint = new Paint();

    protected Paint mCirclePaint = new Paint();

    private Context mContext;

    static final int TEXT_SIZE = 12;

    private IXThreadPool mIXThreadPool;

    /**
     * 背景圆点
     */
    private Paint mPointPaint = new Paint();
    private final IDbaManger mIDbaManger;
    private DatePhysiologyBean mDatePhysiologyBean;

    public ColorfulMonthView(Context context) {
        super(context);
        mContext = context;
        //兼容硬件加速无效的代码
        setLayerType(View.LAYER_TYPE_SOFTWARE, mSelectedPaint);
        setLayerType(View.LAYER_TYPE_SOFTWARE, mSchemePaint);
        mPointPaint.setAntiAlias(true);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setTextAlign(Paint.Align.CENTER);
        mPointPaint.setColor(Color.RED);
        mPadding = dipToPx(getContext(), 3);
        mPointRadius = dipToPx(context, 2);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(6);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setStrokeWidth(2);
        mCirclePaint.setColor(getResources().getColor(R.color.colorWhite));
        mIXThreadPool = (IXThreadPool) XLibFactory.getInstance().createInstance(IXThreadPool.class);
        mIDbaManger = (IDbaManger) XCoreFactory.getInstance().createInstance(IDbaManger.class);

    }

    @Override
    protected void onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 10 * 3;
    }

    /**
     * 如果需要点击Scheme没有效果，则return true
     *
     * @param canvas    canvas
     * @param calendar  日历日历calendar
     * @param x         日历Card x起点坐标
     * @param y         日历Card y起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return false 则不绘制onDrawScheme，因为这里背景色是互斥的
     */
    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {

        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        mSelectedPaint.setDither(true);
        mSchemePaint.setDither(true);
        mCirclePaint.setDither(true);

        mSelectedPaint.setColor(getResources().getColor(R.color.seletedColors));
        canvas.drawCircle(cx, cy, (mRadius + 5) + 13, mSelectedPaint);
        mSelectedPaint.setColor(getResources().getColor(R.color.shijiJq));
        canvas.drawCircle(cx, cy, (mRadius + 5) + 1, mSelectedPaint);
        mSelectedPaint.setColor(getResources().getColor(R.color.colorWhite));
        canvas.drawCircle(cx, cy, (mRadius + 5), mSelectedPaint);

        mDatePhysiologyBean = mIDbaManger.queryPredictionData(CalendarUtil.getZeroDate(calendar.getTimeInMillis()));
        if (mDatePhysiologyBean != null) {
            if (mDatePhysiologyBean.getCurrentState() == 1) {
                mLinePaint.setColor(getResources().getColor(R.color.shijiJq));
                if (mDatePhysiologyBean.getIsPeriodStart() && !mDatePhysiologyBean.getIsPeriodEnd()) {
                    //开始标记
                    if (calendar.isCurrentDay() && calendar.getWeek() != 6) {
                        canvas.drawRect(cx, cy - (mRadius + 5), x + mItemWidth, cy + (mRadius + 5), mLinePaint);
                    } else {
                        java.util.Calendar selectCalendar = java.util.Calendar.getInstance();
                        selectCalendar.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay());
                        if (calendar.getDay() == selectCalendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)) {
                            canvas.drawRect(cx, cy - (mRadius + 5), x + mItemWidth, cy + (mRadius + 5), mLinePaint);
                        } else {
                            canvas.drawRect(cx, cy - (mRadius + 5), x + (mItemWidth * 3 / 2), cy + (mRadius + 5), mLinePaint);
                        }

                    }
                    canvas.drawCircle(cx, cy, (mRadius + 5), mLinePaint);
                    mSelectedPaint.setColor(getResources().getColor(R.color.seletedColors));
                    canvas.drawCircle(cx, cy, (mRadius + 5) + 13, mSelectedPaint);
                    mSelectedPaint.setColor(getResources().getColor(R.color.shijiJq));
                    canvas.drawCircle(cx, cy, (mRadius + 5) + 1, mSelectedPaint);
                    mSelectedPaint.setColor(getResources().getColor(R.color.colorWhite));
                    canvas.drawCircle(cx, cy, (mRadius + 5), mSelectedPaint);
                } else if (!mDatePhysiologyBean.getIsPeriodStart() && mDatePhysiologyBean.getIsPeriodEnd()) {
                    //结束标记
                    canvas.drawRect(x, cy - (mRadius + 5), cx, cy + (mRadius + 5), mLinePaint);
                    canvas.drawCircle(cx, cy, (mRadius + 5), mLinePaint);
                    mSelectedPaint.setColor(getResources().getColor(R.color.seletedColors));
                    canvas.drawCircle(cx, cy, (mRadius + 5) + 13, mSelectedPaint);
                    mSelectedPaint.setColor(getResources().getColor(R.color.shijiJq));
                    canvas.drawCircle(cx, cy, (mRadius + 5) + 1, mSelectedPaint);
                    mSelectedPaint.setColor(getResources().getColor(R.color.colorWhite));
                    canvas.drawCircle(cx, cy, (mRadius + 5), mSelectedPaint);
                } else {
                    //既不是开始也不是结束
                    if (calendar.isCurrentDay() && calendar.getWeek() != 6) {
                        canvas.drawRect(x, cy - (mRadius + 5), x + mItemWidth, cy + (mRadius + 5), mLinePaint);

                    } else {
                        java.util.Calendar selectCalendar = java.util.Calendar.getInstance();
                        selectCalendar.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay());
                        if (calendar.getDay() == selectCalendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)) {
                            canvas.drawRect(x, cy - (mRadius + 5), x + mItemWidth, cy + (mRadius + 5), mLinePaint);
                        } else {
                            canvas.drawRect(x, cy - (mRadius + 5), x + (mItemWidth * 3 / 2), cy + (mRadius + 5), mLinePaint);
                        }

                    }

                    mSelectedPaint.setColor(getResources().getColor(R.color.seletedColors));
                    canvas.drawCircle(cx, cy, (mRadius + 5) + 13, mSelectedPaint);
                    mSelectedPaint.setColor(getResources().getColor(R.color.shijiJq));
                    canvas.drawCircle(cx, cy, (mRadius + 5) + 1, mSelectedPaint);
                    mSelectedPaint.setColor(getResources().getColor(R.color.colorWhite));
                    canvas.drawCircle(cx, cy, (mRadius + 5), mSelectedPaint);
                }
            }
        } else {
            if (!TextUtils.isEmpty(calendar.getScheme())) {
                mLinePaint.setColor(getResources().getColor(R.color.yuceJq));
                if (calendar.getScheme().equals("预测经期开始")) {
                    //开始标记
                    canvas.drawRect(cx, cy - (mRadius + 5), x + (mItemWidth * 3 / 2), cy + (mRadius + 5), mLinePaint);
                    canvas.drawCircle(cx, cy, (mRadius + 5), mLinePaint);
                    mSelectedPaint.setColor(getResources().getColor(R.color.seletedColors));
                    canvas.drawCircle(cx, cy, (mRadius + 5) + 13, mSelectedPaint);
                    mSelectedPaint.setColor(getResources().getColor(R.color.shijiJq));
                    canvas.drawCircle(cx, cy, (mRadius + 5) + 1, mSelectedPaint);
                    mSelectedPaint.setColor(getResources().getColor(R.color.colorWhite));
                    canvas.drawCircle(cx, cy, (mRadius + 5), mSelectedPaint);
                } else if (calendar.getScheme().equals("预测经期结束")) {
                    //结束标记
                    canvas.drawRect(x, cy - (mRadius + 5), cx, cy + (mRadius + 5), mLinePaint);
                    canvas.drawCircle(cx, cy, (mRadius + 5), mLinePaint);
                    mSelectedPaint.setColor(getResources().getColor(R.color.seletedColors));
                    canvas.drawCircle(cx, cy, (mRadius + 5) + 13, mSelectedPaint);
                    mSelectedPaint.setColor(getResources().getColor(R.color.shijiJq));
                    canvas.drawCircle(cx, cy, (mRadius + 5) + 1, mSelectedPaint);
                    mSelectedPaint.setColor(getResources().getColor(R.color.colorWhite));
                    canvas.drawCircle(cx, cy, (mRadius + 5), mSelectedPaint);

                } else if (calendar.getScheme().equals("预测经期")) {
                    //既不是开始也不是结束
                    if (calendar.isCurrentDay() && calendar.getWeek() != 6) {
                        canvas.drawRect(x, cy - (mRadius + 5), x + mItemWidth, cy + (mRadius + 5), mLinePaint);
                    } else {
                        java.util.Calendar selectCalendar = java.util.Calendar.getInstance();
                        selectCalendar.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay());
                        if (calendar.getDay() == selectCalendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)) {
                            canvas.drawRect(x, cy - (mRadius + 5), x + mItemWidth, cy + (mRadius + 5), mLinePaint);
                        } else {
                            canvas.drawRect(x, cy - (mRadius + 5), x + (mItemWidth * 3 / 2), cy + (mRadius + 5), mLinePaint);
                        }
                    }
                    mSelectedPaint.setColor(getResources().getColor(R.color.seletedColors));
                    canvas.drawCircle(cx, cy, (mRadius + 5) + 13, mSelectedPaint);
                    mSelectedPaint.setColor(getResources().getColor(R.color.shijiJq));
                    canvas.drawCircle(cx, cy, (mRadius + 5) + 1, mSelectedPaint);
                    mSelectedPaint.setColor(getResources().getColor(R.color.colorWhite));
                    canvas.drawCircle(cx, cy, (mRadius + 5), mSelectedPaint);
                }
            }
        }

        return false;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {
        if (!calendar.isCurrentMonth()) {
            return;
        }
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        canvas.drawCircle(cx, cy, mRadius, mSchemePaint);
        DatePhysiologyBean datePhysiologyBean = mIDbaManger.queryPredictionData(CalendarUtil.getZeroDate(calendar.getTimeInMillis()));
        if (datePhysiologyBean != null) {
            if (datePhysiologyBean.getCurrentState() == 1) {
                mLinePaint.setColor(getResources().getColor(R.color.shijiJq));
                if (datePhysiologyBean.getIsPeriodStart() && !datePhysiologyBean.getIsPeriodEnd()) {
                    //开始标记
                    if (calendar.isCurrentDay() && calendar.getWeek() != 6) {
                        canvas.drawRect(cx, cy - (mRadius + 5), x + mItemWidth, cy + (mRadius + 5), mLinePaint);
                    } else {
                        java.util.Calendar selectCalendar = java.util.Calendar.getInstance();
                        selectCalendar.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay());
                        if (calendar.getDay() == selectCalendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)) {
                            canvas.drawRect(cx, cy - (mRadius + 5), x + mItemWidth, cy + (mRadius + 5), mLinePaint);
                        } else {
                            canvas.drawRect(cx, cy - (mRadius + 5), x + (mItemWidth * 3 / 2), cy + (mRadius + 5), mLinePaint);
                        }
                    }
                    canvas.drawCircle(cx, cy, (mRadius + 5), mLinePaint);
                } else if (!datePhysiologyBean.getIsPeriodStart() && datePhysiologyBean.getIsPeriodEnd()) {
                    //结束标记
                    canvas.drawRect(x, cy - (mRadius + 5), cx, cy + (mRadius + 5), mLinePaint);
                    canvas.drawCircle(cx, cy, (mRadius + 5), mLinePaint);
                } else {
                    //既不是开始也不是结束
                    if (calendar.isCurrentDay() && calendar.getWeek() != 6) {
                        canvas.drawRect(x, cy - (mRadius + 5), x + mItemWidth, cy + (mRadius + 5), mLinePaint);

                    } else {
                        java.util.Calendar selectCalendar = java.util.Calendar.getInstance();
                        selectCalendar.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay());
                        if (calendar.getDay() == selectCalendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)) {
                            canvas.drawRect(x, cy - (mRadius + 5), x + mItemWidth, cy + (mRadius + 5), mLinePaint);
                        } else {
                            canvas.drawRect(x, cy - (mRadius + 5), x + (mItemWidth * 3 / 2), cy + (mRadius + 5), mLinePaint);
                        }
                    }
                }
            }
        } else {
            if (!TextUtils.isEmpty(calendar.getScheme())) {
                mLinePaint.setColor(getResources().getColor(R.color.yuceJq));
                if (calendar.getScheme().equals("预测经期开始")) {
                    //开始标记
                    if (calendar.isCurrentDay() && calendar.getWeek() != 6) {
                        canvas.drawRect(cx, cy - (mRadius + 5), x + mItemWidth, cy + (mRadius + 5), mLinePaint);
                    } else {
                        java.util.Calendar selectCalendar = java.util.Calendar.getInstance();
                        selectCalendar.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay());
                        if (calendar.getDay() == selectCalendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)) {
                            canvas.drawRect(cx, cy - (mRadius + 5), x + mItemWidth, cy + (mRadius + 5), mLinePaint);
                        } else {
                            canvas.drawRect(cx, cy - (mRadius + 5), x + (mItemWidth * 3 / 2), cy + (mRadius + 5), mLinePaint);
                        }
                    }
//                    canvas.drawRect(cx, cy - (mRadius+5), x + (mItemWidth*3/2), cy + (mRadius+5), mLinePaint);
                    canvas.drawCircle(cx, cy, (mRadius + 5), mLinePaint);
                } else if (calendar.getScheme().equals("预测经期结束")) {
                    //结束标记
                    canvas.drawRect(x, cy - (mRadius + 5), cx, cy + (mRadius + 5), mLinePaint);
                    canvas.drawCircle(cx, cy, (mRadius + 5), mLinePaint);

                } else if (calendar.getScheme().equals("预测经期")) {
                    //既不是开始也不是结束
//                    canvas.drawRect(x, cy - (mRadius+5), x + (mItemWidth*3/2), cy + (mRadius+5), mLinePaint);
                    if (calendar.isCurrentDay() && calendar.getWeek() != 6) {
                        canvas.drawRect(x, cy - (mRadius + 5), x + mItemWidth, cy + (mRadius + 5), mLinePaint);

                    } else {
                        java.util.Calendar selectCalendar = java.util.Calendar.getInstance();
                        selectCalendar.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay());
                        if (calendar.getDay() == selectCalendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)) {
                            canvas.drawRect(x, cy - (mRadius + 5), x + mItemWidth, cy + (mRadius + 5), mLinePaint);
                        } else {
                            canvas.drawRect(x, cy - (mRadius + 5), x + (mItemWidth * 3 / 2), cy + (mRadius + 5), mLinePaint);
                        }

                    }
                }

            }
        }
    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
//        if (!calendar.isCurrentMonth()) {
//            return;
//        }
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        float baselineY = mTextBaseLine + y;
        int cxs = x + mItemWidth / 2;
        int top = y - mItemHeight / 8;

        Paint paint = new Paint();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_heart);
        if (isSelected) {
            if (hasScheme) {
                if (calendar.getScheme().equals("排卵日")) {
                    canvas.drawBitmap(bitmap, cx - (bitmap.getWidth() / 2), cy - bitmap.getHeight() - CalendarUtil.dipToPx(mContext, 7), paint);
                    mSelectTextPaint.setColor(getResources().getColor(R.color.pailuanR));
                    if (calendar.isCurrentDay()) {
                        mSelectTextPaint.setTextSize(CalendarUtil.dipToPx(mContext, 8));
                        canvas.drawText("Today", cxs, baselineY - CalendarUtil.dipToPx(mContext, 2),
                                mSelectTextPaint);
                    } else {
                        mSelectTextPaint.setTextSize(CalendarUtil.dipToPx(mContext, TEXT_SIZE));
                        canvas.drawText(String.valueOf(calendar.getDay()), cxs, baselineY,
                                mSelectTextPaint);
                    }

                } else if (calendar.getScheme().equals("实际经期")) {
                    mSelectTextPaint.setColor(getResources().getColor(R.color.shijiJq));
                    if (calendar.isCurrentDay()) {
                        mSelectTextPaint.setTextSize(CalendarUtil.dipToPx(mContext, 8));
                        canvas.drawText("Today", cxs, baselineY - CalendarUtil.dipToPx(mContext, 2),
                                mSelectTextPaint);
                    } else {
                        mSelectTextPaint.setTextSize(CalendarUtil.dipToPx(mContext, TEXT_SIZE));
                        canvas.drawText(String.valueOf(calendar.getDay()), cxs, baselineY,
                                mSelectTextPaint);
                    }
                } else if (calendar.getScheme().equals("预测经期") || calendar.getScheme().equals("预测经期开始") || calendar.getScheme().equals("预测经期结束")) {
                    mSelectTextPaint.setColor(getResources().getColor(R.color.shijiJq));
                    if (calendar.isCurrentDay()) {
                        mSelectTextPaint.setTextSize(CalendarUtil.dipToPx(mContext, 8));

                        canvas.drawText("Today", cxs, baselineY - CalendarUtil.dipToPx(mContext, 2),
                                mSelectTextPaint);
                    } else {
                        mSelectTextPaint.setTextSize(CalendarUtil.dipToPx(mContext, TEXT_SIZE));
                        canvas.drawText(String.valueOf(calendar.getDay()), cxs, baselineY,
                                mSelectTextPaint);
                    }
                } else if (calendar.getScheme().equals("排卵期")) {
                    mSelectTextPaint.setColor(getResources().getColor(R.color.pailuanR));
                    if (calendar.isCurrentDay()) {
                        mSelectTextPaint.setTextSize(CalendarUtil.dipToPx(mContext, 8));
                        canvas.drawText("Today", cxs, baselineY - CalendarUtil.dipToPx(mContext, 2),
                                mSelectTextPaint);
                    } else {
                        mSelectTextPaint.setTextSize(CalendarUtil.dipToPx(mContext, TEXT_SIZE));
                        canvas.drawText(String.valueOf(calendar.getDay()), cxs, baselineY,
                                mSelectTextPaint);
                    }

                } else if (calendar.getScheme().equals("安全期")) {
                    mSelectTextPaint.setColor(getResources().getColor(R.color.safeColor));
                    if (calendar.isCurrentDay()) {
                        mSelectTextPaint.setTextSize(CalendarUtil.dipToPx(mContext, 8));
                        canvas.drawText("Today", cxs, baselineY - CalendarUtil.dipToPx(mContext, 2),
                                mSelectTextPaint);
                    } else {
                        mSelectTextPaint.setTextSize(CalendarUtil.dipToPx(mContext, TEXT_SIZE));
                        canvas.drawText(String.valueOf(calendar.getDay()), cxs, baselineY,
                                mSelectTextPaint);
                    }
                } else {
                    mSelectTextPaint.setColor(getResources().getColor(R.color.colorWhite));
                    if (calendar.isCurrentDay()) {
                        mSelectTextPaint.setTextSize(CalendarUtil.dipToPx(mContext, 8));
                        canvas.drawText("Today", cxs, baselineY - CalendarUtil.dipToPx(mContext, 2),
                                mSelectTextPaint);
                    } else {
                        mSelectTextPaint.setTextSize(CalendarUtil.dipToPx(mContext, TEXT_SIZE));
                        canvas.drawText(String.valueOf(calendar.getDay()), cxs, baselineY,
                                mSelectTextPaint);
                    }
                }
            } else {
                mSelectTextPaint.setColor(getResources().getColor(R.color.mainFragmentDateTextColor));
                if (calendar.isCurrentDay()) {
                    mSelectTextPaint.setTextSize(CalendarUtil.dipToPx(mContext, 8));
                    canvas.drawText("Today", cxs, baselineY - CalendarUtil.dipToPx(mContext, 2),
                            mSelectTextPaint);
                } else {
                    mSelectTextPaint.setTextSize(CalendarUtil.dipToPx(mContext, TEXT_SIZE));
                    canvas.drawText(String.valueOf(calendar.getDay()), cxs, baselineY,
                            mSelectTextPaint);
                }
            }

        } else if (hasScheme) {
            if (calendar.getScheme().equals("排卵日")) {
                canvas.drawBitmap(bitmap, cx - (bitmap.getWidth() / 2), cy - bitmap.getHeight() - CalendarUtil.dipToPx(mContext, 7), paint);
            }
            if (calendar.getScheme().equals("安全期")) {
                mCurDayTextPaint.setColor(getResources().getColor(R.color.safeColor));
                mSchemeTextPaint.setColor(getResources().getColor(R.color.safeColor));
            } else if (calendar.getScheme().equals("排卵期")) {
                mCurDayTextPaint.setColor(getResources().getColor(R.color.pailuanR));
                mSchemeTextPaint.setColor(getResources().getColor(R.color.pailuanR));
            } else if (calendar.getScheme().equals("实际经期")) {
                mCurDayTextPaint.setColor(getResources().getColor(R.color.colorWhite));
                mSchemeTextPaint.setColor(getResources().getColor(R.color.colorWhite));
            } else if (calendar.getScheme().equals("预测经期") || calendar.getScheme().equals("预测经期开始") || calendar.getScheme().equals("预测经期结束")) {
                mCurDayTextPaint.setColor(getResources().getColor(R.color.shijiJq));
                mSchemeTextPaint.setColor(getResources().getColor(R.color.shijiJq));
            } else {
                mCurDayTextPaint.setColor(getResources().getColor(R.color.pailuanR));
                mSchemeTextPaint.setColor(getResources().getColor(R.color.pailuanR));
            }
            if (calendar.isCurrentDay()) {
                mCurDayTextPaint.setTextSize(CalendarUtil.dipToPx(mContext, 8));
                canvas.drawText("Today", cxs, baselineY - CalendarUtil.dipToPx(mContext, 2),
                        calendar.isCurrentDay() ? mCurDayTextPaint : calendar.isCurrentMonth() ? mSchemeTextPaint : mOtherMonthTextPaint);
            } else {
                mCurDayTextPaint.setTextSize(CalendarUtil.dipToPx(mContext, TEXT_SIZE));
                canvas.drawText(String.valueOf(calendar.getDay()), cxs, baselineY,
                        calendar.isCurrentDay() ? mCurDayTextPaint : calendar.isCurrentMonth() ? mSchemeTextPaint : mOtherMonthTextPaint);
            }

        } else {
            mCurDayTextPaint.setColor(getResources().getColor(R.color.settingItemTextColor));
            if (calendar.isCurrentDay()) {
                if (calendar.isCurrentMonth()) {
                    mCurDayTextPaint.setTextSize(CalendarUtil.dipToPx(mContext, 8));
                    canvas.drawText("Today", cxs, baselineY - CalendarUtil.dipToPx(mContext, 2),
                            calendar.isCurrentDay() ? mCurDayTextPaint : calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);
                } else {
                    mCurDayTextPaint.setColor(getResources().getColor(R.color.otherDay));
                    mCurDayTextPaint.setTextSize(CalendarUtil.dipToPx(mContext, TEXT_SIZE));
                    canvas.drawText(String.valueOf(calendar.getDay()), cxs, baselineY,
                            calendar.isCurrentDay() ? mCurDayTextPaint : calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);
                }

            } else {
                mCurDayTextPaint.setTextSize(CalendarUtil.dipToPx(mContext, TEXT_SIZE));
                canvas.drawText(String.valueOf(calendar.getDay()), cxs, baselineY,
                        calendar.isCurrentDay() ? mCurDayTextPaint : calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);
            }


        }
    }

    private static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
