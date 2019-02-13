package ulric.li.xui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import ulric.li.xui.utils.UtilsImage;

public class XCircleImageView extends ImageView {
    protected Context mContext = null;
    private int mBorderLineWidth = 0;
    private int mBorderLineColor = Color.parseColor("#ffffffff");

    public XCircleImageView(Context context) {
        super(context);
        mContext = context;
        _init();
    }

    public XCircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        _init();
    }

    private void _init() {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap = UtilsImage.drawableToBitmap(mContext, getDrawable());
        if (null == bitmap)
            return;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setStyle(Paint.Style.FILL);

        int nSaveLayer = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, (getWidth() - mBorderLineWidth / 2) / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(0, 0, getWidth(), getHeight()), paint);
        paint.setXfermode(null);
        canvas.restoreToCount(nSaveLayer);

        if (mBorderLineWidth > 0) {
            Paint paintLine = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            paintLine.setStrokeWidth(mBorderLineWidth);
            paintLine.setColor(mBorderLineColor);
            paintLine.setStyle(Paint.Style.STROKE);

            nSaveLayer = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, (getWidth() - mBorderLineWidth) / 2, paintLine);
            canvas.restoreToCount(nSaveLayer);
        }
    }

    public void setBorderLineWidth(int nBorderLineWidth) {
        mBorderLineWidth = nBorderLineWidth;
        postInvalidate();
    }

    public void setBorderLineColor(int nBorderLineColor) {
        mBorderLineColor = nBorderLineColor;
        postInvalidate();
    }
}
