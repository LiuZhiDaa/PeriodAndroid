package ulric.li.xui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.DrawFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ulric.li.xui.utils.UtilsSize;

public class XLineChartView extends View {
    private Context mContext = null;

    private DrawFilter mDrawFilter = null;

    private Paint mPaintBackgroundHorizontalText = null;
    private Paint mPaintBackgroundVerticalText = null;
    private Paint mPaintBackgroundHorizontal = null;
    private Paint mPaintLine = null;
    private Paint mPaintFill = null;
    private Paint mPaintCircle = null;
    private Paint mPaintUnknown = null;
    private Path mPathLine = null;
    private Path mPathFill = null;
    private Path mPathUnknown = null;

    private PathEffect mPathEffect = null;

    private Shader mShaderLine = null;
    private Shader mShaderFill = null;

    private int mWidth = 0;
    private int mHeight = 0;

    private int mBackgroundHorizontalCount = 6;
    private int mBackgroundVerticalCount = 5;
    private int mBackgroundVerticalMaxValue = 100;
    private int mBackgroundLineWidth = 0;
    private int mBackgroundTextSize = 0;
    private int mLineWidth = 0;
    private float mCircleRadius = 0;

    private int mTopDistance = 0;
    private int mBottomDistance = 0;
    private float mLeftDistance = 0;
    private float mRightDistance = 0;
    private float mPathRightDistance = 0;
    private int mBottomPos = 0;
    private float mPathWidth = 0;
    private int mPathHeight = 0;
    private int mVerticalItemHeight = 0;
    private float mHorizontalItemWidth = 0;
    private int mBackgroundVerticalLineHeight = 0;

    private List<String> mListHorizontalText = null;
    private List<String> mListVerticalText = null;
    private ArrayList<PointF> mListPointF = null;
    private PointF mPointFStart = null;
    private PointF mPointFEnd = null;

    private boolean mIsNeedDraw = false;

    private static final int VALUE_INT_BACKGROUND_TEXT_COLOR = 0x30ffffff;
    private static final int VALUE_INT_BACKGROUND_LINE_COLOR = 0x08ffffff;
    private static final int VALUE_INT_UNKNOWN_LINE_COLOR = 0x30ffffff;
    private static final int VALUE_INT_UNKNOWN_FILL_COLOR = 0x08ffffff;
    private static final int VALUE_INT_LINE_TOP_COLOR = 0xff9efcda;
    private static final int VALUE_INT_LINE_BOTTOM_COLOR = 0x881aa9de;
    private static final int VALUE_INT_FILL_TOP_COLOR = 0x889efcda;
    private static final int VALUE_INT_FILL_BOTTOM_COLOR = 0x331aa9de;

    public XLineChartView(Context context) {
        super(context);
        mContext = context;
        _init();
    }

    public XLineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        _init();
    }

    private void _init() {
        if (Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        mBackgroundLineWidth = UtilsSize.dpToPx(mContext, 1);
        mBackgroundTextSize = UtilsSize.dpToPx(mContext, 10);
        mLineWidth = UtilsSize.dpToPx(mContext, 2);
        mCircleRadius = UtilsSize.dpToPx(mContext, 1.3f);

        mTopDistance = UtilsSize.dpToPx(mContext, 20);
        mBottomDistance = UtilsSize.dpToPx(mContext, 20);
        mLeftDistance = UtilsSize.dpToPx(mContext, 20);
        mRightDistance = UtilsSize.dpToPx(mContext, 30);
        mPathRightDistance = UtilsSize.dpToPx(mContext, 40);
        mBackgroundVerticalLineHeight = UtilsSize.dpToPx(mContext, 3);

        mListHorizontalText = new ArrayList<String>();
        mListVerticalText = new ArrayList<String>();
        mListPointF = new ArrayList<PointF>();

        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        mPaintBackgroundHorizontalText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBackgroundHorizontalText.setTextAlign(Paint.Align.CENTER);
        mPaintBackgroundHorizontalText.setTextSize(mBackgroundTextSize);
        mPaintBackgroundHorizontalText.setColor(VALUE_INT_BACKGROUND_TEXT_COLOR);

        mPaintBackgroundVerticalText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBackgroundVerticalText.setTextAlign(Paint.Align.RIGHT);
        mPaintBackgroundVerticalText.setTextSize(mBackgroundTextSize);
        mPaintBackgroundVerticalText.setColor(VALUE_INT_BACKGROUND_TEXT_COLOR);

        mPaintBackgroundHorizontal = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBackgroundHorizontal.setColor(VALUE_INT_BACKGROUND_LINE_COLOR);
        mPaintBackgroundHorizontal.setStyle(Paint.Style.STROKE);
        mPaintBackgroundHorizontal.setStrokeWidth(mBackgroundLineWidth);

        mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLine.setDither(true);
        mPaintLine.setFilterBitmap(true);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setStrokeCap(Paint.Cap.ROUND);
        mPaintLine.setStrokeJoin(Paint.Join.ROUND);
        mPaintLine.setStrokeWidth(mLineWidth);

        mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintFill.setDither(true);
        mPaintFill.setFilterBitmap(true);
        mPaintFill.setStyle(Paint.Style.FILL);

        mPaintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCircle.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintCircle.setStrokeWidth(mCircleRadius);

        mPaintUnknown = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPathLine = new Path();
        mPathFill = new Path();
        mPathUnknown = new Path();

        mPathEffect = new DashPathEffect(new float[]{5, 5, 5, 5}, 5);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mBottomPos = mHeight - mBottomDistance;
        mPathWidth = mWidth - mLeftDistance - mPathRightDistance;
        mPathHeight = mBottomPos - mTopDistance;
        mHorizontalItemWidth = mPathWidth / (mBackgroundHorizontalCount - 1);
        mVerticalItemHeight = mPathHeight / (mBackgroundVerticalCount - 1);

        mShaderLine = new LinearGradient(0, mTopDistance, 0, mBottomPos,
                VALUE_INT_LINE_TOP_COLOR, VALUE_INT_LINE_BOTTOM_COLOR, Shader.TileMode.CLAMP);
        mShaderFill = new LinearGradient(0, mTopDistance, 0, mBottomPos,
                VALUE_INT_FILL_TOP_COLOR, VALUE_INT_FILL_BOTTOM_COLOR, Shader.TileMode.CLAMP);

        mPaintLine.setShader(mShaderLine);
        mPaintFill.setShader(mShaderFill);
        mPaintCircle.setShader(mShaderLine);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(mDrawFilter);
        super.onDraw(canvas);

        drawHorizontalLine(canvas);
        drawHorizontalText(canvas);
        drawVerticalLine(canvas);
        drawVerticalText(canvas);

        if (!mIsNeedDraw)
            return;

        drawBeforeFill(canvas);
        drawData(canvas);
        drawEndCircle(canvas);
    }

    public void setHorizontalText(List<String> listText) {
        if (null == listText)
            return;

        mListHorizontalText = listText;
        mBackgroundHorizontalCount = listText.size();
    }

    public void setVerticalText(List<String> listText) {
        if (null == listText)
            return;

        mListVerticalText = listText;
        mBackgroundVerticalCount = listText.size();
    }

    public void setData(List<Integer> listData) {
        if (null == listData)
            return;

        mListPointF.clear();
        for (int nIndex = 0; nIndex < listData.size(); nIndex++) {
            int nValue = listData.get(nIndex);
            if (0 == nValue)
                continue;

            PointF pointF = new PointF();
            float fWidth = mPathWidth * (float) (nIndex) / (listData.size() - 1);
            pointF.x = mLeftDistance + fWidth;

            float fCurrentPercentFraction = (float) nValue / mBackgroundVerticalMaxValue;
            float fCurveYHeight = fCurrentPercentFraction * mPathHeight;
            float fScreenCurveHeight = mPathHeight - fCurveYHeight;
            float fCurveYPosition = mTopDistance + fScreenCurveHeight;
            float fCurveMinPosition = mBottomPos - mLineWidth;
            if (fCurveYPosition >= fCurveMinPosition) {
                fCurveYPosition = fCurveMinPosition;
            }

            pointF.y = fCurveYPosition;
            mListPointF.add(pointF);
        }

        resetPath(mListPointF);
        mIsNeedDraw = !mListPointF.isEmpty();
        postInvalidate();
    }

    private void drawHorizontalLine(Canvas canvas) {
        for (int nIndex = 0; nIndex < mBackgroundVerticalCount; nIndex++) {
            int nY = mBottomPos - nIndex * mVerticalItemHeight;
            canvas.drawLine(mLeftDistance, nY, mWidth, nY, mPaintBackgroundHorizontal);
        }
    }

    private void drawHorizontalText(Canvas canvas) {
        for (int nIndex = 0; nIndex < mBackgroundHorizontalCount; nIndex++) {
            int nX = (int) (mLeftDistance + (float) nIndex / (mBackgroundHorizontalCount - 1) * mPathWidth);
            Paint.FontMetrics fm = mPaintBackgroundVerticalText.getFontMetrics();
            canvas.drawText(mListHorizontalText.get(nIndex), nX, mHeight - (fm.descent - fm.ascent) / 2, mPaintBackgroundHorizontalText);
        }
    }

    private void drawVerticalLine(Canvas canvas) {
        for (int nIndex = 0; nIndex < mBackgroundHorizontalCount; nIndex++) {
            float fLeftPos = mLeftDistance + nIndex * mHorizontalItemWidth;
            if (nIndex != 0) {
                canvas.drawLine(fLeftPos, mBottomPos, fLeftPos, mBottomPos + mBackgroundVerticalLineHeight, mPaintBackgroundHorizontal);
            }
        }
    }

    private void drawVerticalText(Canvas canvas) {
        for (int nIndex = 0; nIndex < mBackgroundVerticalCount; nIndex++) {
            int nY = mBottomPos - nIndex * mVerticalItemHeight;
            Paint.FontMetrics fm = mPaintBackgroundVerticalText.getFontMetrics();
            canvas.drawText(mListVerticalText.get(nIndex), mWidth - mBackgroundVerticalLineHeight
                    , nY - (fm.descent - fm.ascent) / 2, mPaintBackgroundVerticalText);
        }
    }

    private void drawBeforeFill(Canvas canvas) {
        if (null == mPointFStart || mLeftDistance == mPointFStart.x)
            return;

        mPaintUnknown.setColor(VALUE_INT_UNKNOWN_LINE_COLOR);
        mPaintUnknown.setPathEffect(mPathEffect);
        canvas.drawLine(mLeftDistance, mBottomPos, mPointFStart.x, mPointFStart.y, mPaintUnknown);

        mPathUnknown.reset();
        mPathUnknown.moveTo(mLeftDistance, mBottomPos);
        mPathUnknown.lineTo(mPointFStart.x, mPointFStart.y);
        mPathUnknown.lineTo(mPointFStart.x, mBottomPos);
        mPathUnknown.close();
        mPaintUnknown.setColor(VALUE_INT_UNKNOWN_FILL_COLOR);
        mPaintUnknown.setPathEffect(null);
        canvas.drawPath(mPathUnknown, mPaintUnknown);
    }

    private void drawData(Canvas canvas) {
        int nSaveLayerCount = canvas.saveLayer(0, 0, mWidth, mHeight, mPaintLine, Canvas.ALL_SAVE_FLAG);
        canvas.drawPath(mPathLine, mPaintLine);
        canvas.drawPath(mPathFill, mPaintFill);
        canvas.restoreToCount(nSaveLayerCount);
    }

    private void resetPath(List<PointF> listPointF) {
        if (null == listPointF || listPointF.isEmpty())
            return;

        mPointFStart = new PointF(listPointF.get(0).x, listPointF.get(0).y);
        mPointFEnd = new PointF(listPointF.get(0).x, listPointF.get(0).y);
        mPathLine.reset();
        mPathFill.reset();

        PointF pointfStart = new PointF();
        PointF pointfEnd = new PointF();
        PointF pointfControlOne = new PointF();
        PointF pointfControlTwo = new PointF();
        float fStartX = 0;

        for (int nIndex = 0; nIndex < listPointF.size() - 1; nIndex++) {
            float fX = listPointF.get(nIndex).x;
            float fY = listPointF.get(nIndex).y;
            if (0 == nIndex) {
                fStartX = fX;
                mPathFill.moveTo(fX, fY);
                mPathLine.moveTo(fX, fY);
            }

            pointfStart = listPointF.get(nIndex);
            pointfEnd = listPointF.get(nIndex + 1);

            float wt = (pointfStart.x + pointfEnd.x) / 2;
            pointfControlOne.y = pointfStart.y;
            pointfControlOne.x = wt;
            pointfControlTwo.y = pointfEnd.y;
            pointfControlTwo.x = wt;

            mPathFill.cubicTo(pointfControlOne.x, pointfControlOne.y, pointfControlTwo.x, pointfControlTwo.y, pointfEnd.x, pointfEnd.y);
            mPathLine.cubicTo(pointfControlOne.x, pointfControlOne.y, pointfControlTwo.x, pointfControlTwo.y, pointfEnd.x, pointfEnd.y);
        }

        if (listPointF.size() > 1) {
            mPointFEnd.x = pointfEnd.x;
            mPointFEnd.y = pointfEnd.y;
            mPathFill.lineTo(pointfEnd.x, mBottomPos);
            mPathFill.lineTo(fStartX, mBottomPos);
            mPathFill.close();
        }
    }

    private void drawEndCircle(Canvas canvas) {
        if (null == mPointFEnd)
            return;

        canvas.drawCircle(mPointFEnd.x, mPointFEnd.y, mCircleRadius, mPaintCircle);
    }
}
