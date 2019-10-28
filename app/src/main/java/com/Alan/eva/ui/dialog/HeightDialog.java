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
 * Created by CW on 2017/3/13.
 * 选择身高对话框
 */
public class HeightDialog extends AbsDialogCreator {
    private WheelView wheel_dialog_height_high_selector;
    private ArrayList<String> highList;

    private View.OnClickListener onOK;

    public HeightDialog(Context context) {
        super(context);
        if (Tools.isListEmpty(highList)) {
            highList = new ArrayList<>();
            for (int i = 20; i <= 220; i += 2) {
                highList.add(String.valueOf(i));
            }
        }
    }

    @Override
    public int getRootViewId() {
        return R.layout.dialog_choose_height;
    }

    @Override
    public void findView(View rootView) {
        AppCompatTextView tv_dialog_height_selector_ok = (AppCompatTextView) rootView.findViewById(R.id.tv_dialog_height_selector_ok);
        wheel_dialog_height_high_selector = (WheelView) rootView.findViewById(R.id.wheel_dialog_height_high_selector);
        tv_dialog_height_selector_ok.setOnClickListener(onOK);
        if (wheel_dialog_height_high_selector.isEmpty()) {
            wheel_dialog_height_high_selector.setItems(highList);
        }
    }

    public void setOnOK(View.OnClickListener onOK) {
        this.onOK = onOK;
    }

    public String getHeight() {
        return wheel_dialog_height_high_selector.getSelectedItem();
    }
}
