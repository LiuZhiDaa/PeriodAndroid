package com.period.app.dialog.calendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.period.app.R;
import com.period.app.core.XCoreFactory;
import com.period.app.dialog.CustomDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingPeriodStartDialog extends CustomDialog {

    public static final int VALUE_INT_START_TEXT = R.string.string_dialog_period_start;
    public static final int VALUE_INT_END_TEXT = R.string.is_the_selected_date_set_to_the_end_of_the_period;
    public static final int VALUE_INT_CANCEL_START_TEXT = R.string.cancel_start_text;
    public static final int VALUE_INT_CANCEL_END_TEXT = R.string.cancel_end_text;

    @BindView(R.id.tv_dialog_start)
    TextView tvDialogStart;
    boolean state;

    public SettingPeriodStartDialog(@NonNull Context context, String text, String title) {
        super(context);
        this.state=state;
        initView(context, text, title);
    }

    private void initView(Context context, String text, String title) {
        View view = View.inflate(context, R.layout.view_start_text, null);
        ButterKnife.bind(this,view);
            if (!TextUtils.isEmpty(text)){
                tvDialogStart.setText(text);
            }else {
                tvDialogStart.setText(R.string.string_dialog_period_start);
            }
        setCustomView(view);
        if (TextUtils.isEmpty(title)) {
            setTitle(XCoreFactory.getApplication().getResources().getString(R.string.dialog_calendar_title));
        }else{
            setTitle(title);
        }
    }
}
