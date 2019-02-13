package com.period.app.dialog.calendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.period.app.R;
import com.period.app.core.XCoreFactory;
import com.period.app.dialog.CustomTextDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TipsDialog extends CustomTextDialog {

    @BindView(R.id.tv_dialog_start)
    TextView tvDialogStart;

    public TipsDialog(@NonNull Context context,String text) {
        super(context);
        initView(context,text);
    }

    private void initView(Context context, String text) {
        View view = View.inflate(context, R.layout.view_start_text, null);
        ButterKnife.bind(this, view);
        if (!TextUtils.isEmpty(text)) {
            tvDialogStart.setText(text);
        }
        setCustomView(view);
        setTitle("Tips");
    }

}
