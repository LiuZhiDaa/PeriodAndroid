package com.period.app.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.period.app.R;
import com.period.app.core.XCoreFactory;
import com.period.app.core.data.IDataMgr;
import com.period.app.widget.wheelview.WheelView;

/**
 * Created by WangGuiLi
 * on 2018/12/13
 */
public class AdjustCycleLengthDialog extends CustomDialog {

    private WheelView mWheelView;
    private TextView mTvUnit;

    private IDataMgr mIDataManager;

    public AdjustCycleLengthDialog(@NonNull Context context) {
        super(context);
        mIDataManager = (IDataMgr) XCoreFactory.getInstance().createInstance(IDataMgr.class);
        initView(context);
    }

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.view_adjust_cycle_length, null);
        setCustomView(view);
        mTvUnit = view.findViewById(R.id.tv_unit);
        mTvUnit.setText("");
        mWheelView = view.findViewById(R.id.wheel_view_number);
        mWheelView.setItems(mIDataManager.getPhyCycleList(), mIDataManager.getDefaultPhyCycleIndex());
        setTitle(XCoreFactory.getApplication().getResources().getString(R.string.dialog_cycle_length_title));
    }

    public void setUnit(String unit) {
        if (mTvUnit != null) {
            mTvUnit.setText("");
        }
    }


    @Override
    public String[] getResult() {
        String[] result = new String[1];
        result[0] = mWheelView.getSelectedItem();
        return result;
    }
}
