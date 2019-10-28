package com.Alan.eva.ui.dialog;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.Alan.eva.R;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.ui.core.AbsDialogCreator;
import com.Alan.eva.ui.widget.WheelView;

import java.util.ArrayList;

/**
 * Created by CW on 2017/3/10.
 * 年龄选择器
 */
public class AgeSelectorDialog extends AbsDialogCreator {
    private ArrayList<String> ages;
    private WheelView wv;
    private View.OnClickListener onAgeOk;

    public AgeSelectorDialog(Context context) {
        super(context);
        if (Tools.isListEmpty(ages)) {
            ages = new ArrayList<>();
            for (int i = 0; i <= 100; i++) {
                ages.add(String.valueOf(i));
            }
        }
    }

    @Override
    public int getRootViewId() {
        return R.layout.dialog_age_selector;
    }

    @Override
    public void findView(View rootView) {
        AppCompatTextView tv_dialog_age_selector_ok = (AppCompatTextView) rootView.findViewById(R.id.tv_dialog_age_selector_ok);
        tv_dialog_age_selector_ok.setOnClickListener(onAgeOk);
        wv = (WheelView) rootView.findViewById(R.id.wheel_dialog_age_selector);
        wv.setOffset(2);
        wv.setSelection(3);
        if (wv.isEmpty()) {
            wv.setItems(ages);
        }
    }

    public void setOnAgeOk(View.OnClickListener onAgeOk) {
        this.onAgeOk = onAgeOk;
    }

    public String getAge() {
        return wv.getSelectedItem();
    }
}
