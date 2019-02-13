package ulric.li.xui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

import java.util.Locale;

public class XDynamicValueTextView extends AppCompatTextView {
    private Context mContext = null;
    private long mLongValue = 0;
    private double mDoubleValue = 0;

    public XDynamicValueTextView(Context context) {
        super(context);
        mContext = context;
        _init();
    }

    public XDynamicValueTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        _init();
    }

    private void _init() {
    }

    public long getLongDynamicValue() {
        return mLongValue;
    }

    public double getDoubleDynamicValue() {
        return mDoubleValue;
    }

    public void setLongDynamicValue(final String strDescription, final long lFromValue, final long lToValue, long lTime) {
        if (lFromValue == lToValue)
            return;

        mLongValue = lFromValue;
        ValueAnimator valueAnimator = ValueAnimator.ofInt((int) lFromValue, (int) lToValue);
        valueAnimator.setDuration(lTime);
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mLongValue = lFromValue;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mLongValue = lToValue;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mLongValue = lToValue;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLongValue = (Integer) animation.getAnimatedValue();
                if (TextUtils.isEmpty(strDescription))
                    setText(String.valueOf(mLongValue));
                else
                    setText(String.format(Locale.ENGLISH, strDescription, mLongValue));
            }
        });

        valueAnimator.start();
    }

    public void setDoubleDynamicValue(final String strDescription, final double dFromValue, final double dToValue, long lTime) {
        if (dFromValue == dToValue)
            return;

        mDoubleValue = dFromValue;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat((float) dFromValue, (float) dToValue);
        valueAnimator.setDuration(lTime);
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mDoubleValue = dFromValue;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mDoubleValue = dToValue;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mDoubleValue = dToValue;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDoubleValue = (Float) animation.getAnimatedValue();
                if (TextUtils.isEmpty(strDescription))
                    setText(String.valueOf(mDoubleValue));
                else
                    setText(String.format(Locale.ENGLISH, strDescription, mDoubleValue));
            }
        });

        valueAnimator.start();
    }
}
