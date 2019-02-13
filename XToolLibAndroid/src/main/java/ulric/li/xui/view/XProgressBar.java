package ulric.li.xui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class XProgressBar extends View {
    private Context mContext = null;
    private Paint mPaint = null;
    private float mProgress = 0;
    private int mColorBackground = 0;
    private int mColorProgress = 0;

    public XProgressBar(Context context) {
        super(context);
        mContext = context;
        _init();
    }

    public XProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        _init();
    }

    private void _init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int nWidth = getWidth();
        int nHeight = getHeight();
        if (nWidth <= nHeight)
            return;

        int nSaveLayer = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        mPaint.setColor(mColorBackground);
        canvas.drawArc(new RectF(0, 0, getHeight(), getHeight()), 90, 180, true, mPaint);
        canvas.drawArc(new RectF(getWidth() - getHeight(), 0, getWidth(), getHeight()), -90, 180, true, mPaint);
        canvas.drawRect(getHeight() / 2, 0, getWidth() - getHeight() / 2, getHeight(), mPaint);

        if (mProgress < 1)
            canvas.clipRect(0, 0, (int) (getWidth() * mProgress), getHeight());

        mPaint.setColor(mColorProgress);
        canvas.drawArc(new RectF(0, 0, getHeight(), getHeight()), 90, 180, true, mPaint);
        canvas.drawArc(new RectF(getWidth() - getHeight(), 0, getWidth(), getHeight()), -90, 180, true, mPaint);
        canvas.drawRect(getHeight() / 2, 0, getWidth() - getHeight() / 2, getHeight(), mPaint);
        canvas.restoreToCount(nSaveLayer);
    }

    public void setColor(int nColorBackground, int nColorProgress) {
        mColorBackground = nColorBackground;
        mColorProgress = nColorProgress;
    }

    public void setProgress(float fProgress) {
        mProgress = Math.max(0, Math.min(1, fProgress));
        postInvalidate();
    }

    public void setProgress(final float fFromProgress, final float fToProgress, long lTime) {
        if (fFromProgress == fToProgress)
            return;

        mProgress = fFromProgress;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(fFromProgress, fToProgress);
        valueAnimator.setDuration(lTime);
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                setProgress(fFromProgress);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setProgress(fToProgress);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                setProgress(fToProgress);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (Float) animation.getAnimatedValue();
                setProgress(mProgress);
            }
        });

        valueAnimator.start();
    }
}