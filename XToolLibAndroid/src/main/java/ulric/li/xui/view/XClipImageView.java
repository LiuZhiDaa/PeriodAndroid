package ulric.li.xui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import ulric.li.xui.utils.UtilsImage;

public class XClipImageView extends ImageView {
    protected Context mContext = null;
    private Paint mPaintBorderLine = null;
    private Paint mPaintBorderOutside = null;
    private int mBorderLineWidth = 2;
    private int mBorderLineColor = Color.parseColor("#ffffffff");
    private int mBorderOutsideColor = Color.parseColor("#bb000000");

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = null;
    private ScaleGestureDetector mScaleGestureDetector = null;
    private GestureDetector mGestureDetector = null;
    private boolean mIsFirst = true;
    private Matrix mMatrixScale = null;
    private boolean mIsAutoScale = false;
    private boolean mIsCanDrag = false;
    private int mLastPointerCount = 0;
    private int mTouchSlop = 0;
    private float mLastX = 0;
    private float mLastY = 0;
    private float mScaleDefaultValue = 1;
    private float mScaleMiddleValue = 2;
    private float mScaleMaxValue = 2;
    private boolean mIsNeedDrawBorderLine = true;

    public XClipImageView(Context context) {
        super(context);
        mContext = context;
        _init();
    }

    public XClipImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        _init();
    }

    private void _init() {
        mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mIsFirst)
                    return;

                mIsFirst = false;
                Drawable drawable = getDrawable();
                if (null == drawable)
                    return;

                int nWidth = getWidth();
                int nHeight = getHeight();
                int nDrawableWidth = drawable.getIntrinsicWidth();
                int nDrawableHeight = drawable.getIntrinsicHeight();
                float fScale = 1;

                if (nDrawableWidth > nWidth && nDrawableHeight < nWidth) {
                    fScale = 1.0f * nWidth / nDrawableHeight;
                } else if (nDrawableHeight > nWidth && nDrawableWidth < nWidth) {
                    fScale = 1.0f * nWidth / nDrawableWidth;
                } else if (nDrawableWidth > nWidth && nDrawableHeight > nWidth) {
                    float fScaleX = nWidth * 1.0f / nDrawableWidth;
                    float fScaleY = nWidth * 1.0f / nDrawableHeight;
                    fScale = Math.max(fScaleX, fScaleY);
                }

                if (nDrawableWidth < nWidth && nDrawableHeight > nWidth) {
                    fScale = 1.0f * nWidth / nDrawableWidth;
                } else if (nDrawableHeight < nWidth && nDrawableWidth > nWidth) {
                    fScale = 1.0f * nWidth / nDrawableHeight;
                } else if (nDrawableWidth < nWidth && nDrawableHeight < nWidth) {
                    float fScaleX = 1.0f * nWidth / nDrawableWidth;
                    float fScaleY = 1.0f * nWidth / nDrawableHeight;
                    fScale = Math.max(fScaleX, fScaleY);
                }

                mScaleDefaultValue = fScale;
                mScaleMiddleValue = mScaleDefaultValue * 2;
                mScaleMaxValue = mScaleDefaultValue * 4;
                mMatrixScale.postTranslate((nWidth - nDrawableWidth) / 2, (nHeight - nDrawableHeight) / 2);
                mMatrixScale.postScale(fScale, fScale, getWidth() / 2, getHeight() / 2);

                setImageMatrix(mMatrixScale);
            }
        };
        mScaleGestureDetector = new ScaleGestureDetector(mContext, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float fScale = getScale();
                float fScaleFactor = detector.getScaleFactor();
                if (getDrawable() == null)
                    return true;

                if ((fScale < mScaleMaxValue && fScaleFactor > 1)
                        || (fScale > mScaleDefaultValue && fScaleFactor < 1)) {
                    if (fScaleFactor * fScale < mScaleDefaultValue) {
                        fScaleFactor = mScaleDefaultValue / fScale;
                    }

                    if (fScaleFactor * fScale > mScaleMaxValue) {
                        fScaleFactor = mScaleMaxValue / fScale;
                    }

                    mMatrixScale.postScale(fScaleFactor, fScaleFactor, detector.getFocusX(), detector.getFocusY());
                    checkBorder();
                    setImageMatrix(mMatrixScale);
                }

                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
            }
        });
        mGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (mIsAutoScale == true)
                    return true;

                float fX = e.getX();
                float fY = e.getY();
                if (getScale() < mScaleMiddleValue) {
                    XClipImageView.this.postDelayed(new AutoScaleRunnable(mScaleMiddleValue, fX, fY), 16);
                    mIsAutoScale = true;
                } else {
                    XClipImageView.this.postDelayed(new AutoScaleRunnable(mScaleDefaultValue, fX, fY), 16);
                    mIsAutoScale = true;
                }

                return true;
            }
        });
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mGestureDetector.onTouchEvent(event))
                    return true;

                mScaleGestureDetector.onTouchEvent(event);
                float fX = 0;
                float fY = 0;
                int nPointerCount = event.getPointerCount();
                for (int nIndex = 0; nIndex < nPointerCount; nIndex++) {
                    fX += event.getX(nIndex);
                    fY += event.getY(nIndex);
                }

                fX = fX / nPointerCount;
                fY = fY / nPointerCount;
                if (nPointerCount != mLastPointerCount) {
                    mIsCanDrag = false;
                    mLastX = fX;
                    mLastY = fY;
                }

                mLastPointerCount = nPointerCount;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        float dx = fX - mLastX;
                        float dy = fY - mLastY;

                        if (!mIsCanDrag) {
                            mIsCanDrag = isCanDrag(dx, dy);
                        }

                        if (mIsCanDrag) {
                            if (getDrawable() != null) {
                                RectF rectF = getMatrixRectF();
                                if (rectF.width() <= getWidth()) {
                                    dx = 0;
                                }

                                if (rectF.height() <= getHeight() - getWidthHeightDifference() * 2) {
                                    dy = 0;
                                }

                                mMatrixScale.postTranslate(dx, dy);
                                checkBorder();
                                setImageMatrix(mMatrixScale);
                            }
                        }

                        mLastX = fX;
                        mLastY = fY;
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mLastPointerCount = 0;
                        break;
                }

                return true;
            }
        });

        mMatrixScale = new Matrix();
        setScaleType(ScaleType.MATRIX);
        updateBorderLinePaint();
        updateBorderOutsidePaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mIsNeedDrawBorderLine) {
            mIsNeedDrawBorderLine = true;
            return;
        }

        int nSaveLayer = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        canvas.drawRect(0, 0, getWidth(), getWidthHeightDifference(), mPaintBorderOutside);
        canvas.drawRect(0, getHeight() - getWidthHeightDifference(), getWidth(), getHeight(), mPaintBorderOutside);
        canvas.drawRect(mBorderLineWidth / 2, getWidthHeightDifference(), getWidth() - mBorderLineWidth / 2, getHeight() - getWidthHeightDifference(), mPaintBorderLine);
        canvas.restoreToCount(nSaveLayer);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(mOnGlobalLayoutListener);
    }

    public Bitmap getClipBitmap() {
        mIsNeedDrawBorderLine = false;
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        Bitmap bitmapResult = Bitmap.createBitmap(bitmap, 0, getWidthHeightDifference(), getWidth(), getWidth());
        return bitmapResult;
    }

    public void saveClipBitmapToFile(String strPath, int nWidth, int nHeight, Bitmap.CompressFormat compressFormat) {
        if (TextUtils.isEmpty(strPath))
            return;

        mIsNeedDrawBorderLine = false;
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        UtilsImage.compressBitmapToFileCenterCrop(mContext, bitmap, strPath, nWidth, nHeight, compressFormat);
    }

    public void setBorderLine(int nBorderLineWidth, int nBorderLineColor) {
        mBorderLineWidth = nBorderLineWidth;
        mBorderLineColor = nBorderLineColor;
        updateBorderLinePaint();
        postInvalidate();
    }

    public void setBorderOutsideColor(int nBorderOutsideColor) {
        mBorderOutsideColor = nBorderOutsideColor;
        updateBorderOutsidePaint();
        postInvalidate();
    }

    private void updateBorderLinePaint() {
        mPaintBorderLine = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mPaintBorderLine.setStrokeWidth(mBorderLineWidth);
        mPaintBorderLine.setColor(mBorderLineColor);
        mPaintBorderLine.setStyle(Paint.Style.STROKE);
    }

    private void updateBorderOutsidePaint() {
        mPaintBorderOutside = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mPaintBorderOutside.setColor(mBorderOutsideColor);
        mPaintBorderOutside.setStyle(Paint.Style.FILL);
    }

    private float getScale() {
        float[] farrayMatrixValue = new float[9];
        mMatrixScale.getValues(farrayMatrixValue);
        return farrayMatrixValue[Matrix.MSCALE_X];
    }

    private RectF getMatrixRectF() {
        Matrix matrix = mMatrixScale;
        RectF rect = new RectF();
        Drawable drawable = getDrawable();
        if (null != drawable) {
            rect.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            matrix.mapRect(rect);
        }

        return rect;
    }

    private int getWidthHeightDifference() {
        return (getHeight() - (getWidth())) / 2;
    }

    private void checkBorder() {
        RectF rect = getMatrixRectF();
        float fDeltaX = 0;
        float fDeltaY = 0;
        int nWidth = getWidth();
        int nHeight = getHeight();
        if (rect.width() >= nWidth) {
            if (rect.left > 0) {
                fDeltaX = -rect.left;
            }

            if (rect.right < nWidth) {
                fDeltaX = nWidth - rect.right;
            }
        }

        if (rect.height() >= nHeight - 2 * getWidthHeightDifference()) {
            if (rect.top > getWidthHeightDifference()) {
                fDeltaY = -rect.top + getWidthHeightDifference();
            }

            if (rect.bottom < nHeight - getWidthHeightDifference()) {
                fDeltaY = nHeight - getWidthHeightDifference() - rect.bottom;
            }
        }

        mMatrixScale.postTranslate(fDeltaX, fDeltaY);
    }

    private boolean isCanDrag(float dx, float dy) {
        return Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
    }

    private class AutoScaleRunnable implements Runnable {
        private float mX = 0;
        private float mY = 0;
        private float mTargetScale = 0;
        private float mTempScale = 0;

        private static final float VALUE_FLOAT_BIGGEST_SCALE_VALUE = 1.07f;
        private static final float VALUE_FLOAT_SMALLEST_SCALE_VALUE = 0.93f;

        public AutoScaleRunnable(float fTargetScale, float fX, float fY) {
            mTargetScale = fTargetScale;
            mX = fX;
            mY = fY;
            mTempScale = getScale() < mTargetScale ? VALUE_FLOAT_BIGGEST_SCALE_VALUE : VALUE_FLOAT_SMALLEST_SCALE_VALUE;
        }

        @Override
        public void run() {
            mMatrixScale.postScale(mTempScale, mTempScale, mX, mY);
            checkBorder();
            setImageMatrix(mMatrixScale);

            final float currentScale = getScale();
            if (((mTempScale > 1) && (currentScale < mTargetScale)) || ((mTempScale < 1) && (mTargetScale < currentScale))) {
                XClipImageView.this.postDelayed(this, 16);
            } else {
                float fDeltaScale = mTargetScale / currentScale;
                mMatrixScale.postScale(fDeltaScale, fDeltaScale, mX, mY);
                checkBorder();
                setImageMatrix(mMatrixScale);
                mIsAutoScale = false;
            }

        }
    }
}
