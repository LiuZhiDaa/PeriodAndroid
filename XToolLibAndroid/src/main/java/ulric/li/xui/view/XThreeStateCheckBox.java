package ulric.li.xui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import ulric.li.xui.intf.IXThreeStateCheckBoxType;

public class XThreeStateCheckBox extends Button {
    public interface IXThreeStateCheckBoxListener {
        void onClick(View v, int nThreeStateCheckBoxType);
    }

    private IXThreeStateCheckBoxListener mListener = null;

    public void setListener(IXThreeStateCheckBoxListener listener){
        mListener = listener;
    }

    private Context mContext = null;
    private int mThreeStateCheckBoxType = IXThreeStateCheckBoxType.VALUE_INT_THREE_STATE_CHECK_BOX_TYPE_NORMAL;
    private int mResNormalID = 0;
    private int mResCheckedID = 0;
    private int mResIncompleteID = 0;

    public XThreeStateCheckBox(Context context) {
        super(context);
        mContext = context;
        _init();
    }

    public XThreeStateCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        _init();
    }

    private void _init() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IXThreeStateCheckBoxType.VALUE_INT_THREE_STATE_CHECK_BOX_TYPE_CHECKED == mThreeStateCheckBoxType)
                    mThreeStateCheckBoxType = IXThreeStateCheckBoxType.VALUE_INT_THREE_STATE_CHECK_BOX_TYPE_NORMAL;
                else
                    mThreeStateCheckBoxType = IXThreeStateCheckBoxType.VALUE_INT_THREE_STATE_CHECK_BOX_TYPE_CHECKED;

                analyzeImage();
                if (null != mListener)
                    mListener.onClick(XThreeStateCheckBox.this, mThreeStateCheckBoxType);
            }
        });
    }

    public void setResID(int nResNormalID, int nResCheckedID, int nResIncompleteID) {
        mResNormalID = nResNormalID;
        mResCheckedID = nResCheckedID;
        mResIncompleteID = nResIncompleteID;
    }

    public int getThreeStateCheckBoxType() {
        return mThreeStateCheckBoxType;
    }

    public void setThreeStateCheckBoxType(int nThreeStateCheckBoxType) {
        mThreeStateCheckBoxType = nThreeStateCheckBoxType;
        analyzeImage();
    }

    private void analyzeImage() {
        if (IXThreeStateCheckBoxType.VALUE_INT_THREE_STATE_CHECK_BOX_TYPE_NORMAL == mThreeStateCheckBoxType)
            setBackgroundResource(mResNormalID);
        else if (IXThreeStateCheckBoxType.VALUE_INT_THREE_STATE_CHECK_BOX_TYPE_CHECKED == mThreeStateCheckBoxType)
            setBackgroundResource(mResCheckedID);
        else if (IXThreeStateCheckBoxType.VALUE_INT_THREE_STATE_CHECK_BOX_TYPE_INCOMPLETE == mThreeStateCheckBoxType)
            setBackgroundResource(mResIncompleteID);
    }
}
