package com.period.app.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import com.period.app.R;
import com.period.app.core.XCoreFactory;
import com.period.app.core.reminder.IReminderMgr;

/**
 * Created by WangGuiLi
 * on 2018/12/13
 */
public class ReminderTextDialog extends CustomDialog {

    private int mType;
    private EditText mETCustom;
    private IReminderMgr mIReminderMgr;

    public ReminderTextDialog(@NonNull Context context,int type) {
        super(context);
        this.mType = type;
        mIReminderMgr = (IReminderMgr) XCoreFactory.getInstance().createInstance(IReminderMgr.class);
        initView(context);
    }

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.view_reminder_text, null);
        mETCustom = view.findViewById(R.id.et_custom_text);
        setCustomView(view);
        setTitle(context.getResources().getString(R.string.dialog_reminder_text_title));
        mETCustom.setText(mIReminderMgr.getReminderCustomText(mType));
    }


    @Override
    public String[] getResult() {
        String[] result = new String[1];
        result[0] = mETCustom.getText().toString();
        return result;
    }
}
