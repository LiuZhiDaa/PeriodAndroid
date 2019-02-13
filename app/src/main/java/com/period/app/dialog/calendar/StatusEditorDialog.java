package com.period.app.dialog.calendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.period.app.R;
import com.period.app.dialog.CustomDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatusEditorDialog extends CustomDialog {

    @BindView(R.id.radio_start)
    RadioButton radioStart;
    @BindView(R.id.radio_end)
    RadioButton radioEnd;
    @BindView(R.id.radio_delete)
    RadioButton radioDelete;
    @BindView(R.id.radiogroup_select)
    RadioGroup radiogroupSelect;
    private int status=0;   //1 选择开始  2选择结束  3选择删除

    public StatusEditorDialog(@NonNull Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.view_status_eidt, null);
        ButterKnife.bind(this, view);
        radioStart.setOnClickListener(v -> {
            if (radioStart.isChecked()){
                status=1;
            }
        });
        radioEnd.setOnClickListener(v -> {
            if (radioEnd.isChecked()){
                status=2;
            }
        });
        radioDelete.setOnClickListener(v -> {
            if (radioDelete.isChecked()){
                status=3;
            }
        });

        setCustomView(view);
        setTitle("Edit");

    }

    public int getStatus(){
        return status;
    }

}
