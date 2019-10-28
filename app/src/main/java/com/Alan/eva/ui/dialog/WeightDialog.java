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
 * 体重选择对话框
 */
public class WeightDialog extends AbsDialogCreator {
    private WheelView wheel_dialog_weight_selector;
    private ArrayList<String> weightList;
    private View.OnClickListener onOK;

    public WeightDialog(Context context) {
        super(context);
        if (Tools.isListEmpty(weightList)) {
            weightList = new ArrayList<>();
            for (int i = 1; i <= 200; i++) {
                weightList.add(String.valueOf(i));
            }
        }
    }

    @Override
    public int getRootViewId() {
        return R.layout.dialog_choose_weight;
    }

    @Override
    public void findView(View rootView) {
        AppCompatTextView tv_dialog_weight_selector_ok = (AppCompatTextView) rootView.findViewById(R.id.tv_dialog_weight_selector_ok);
        wheel_dialog_weight_selector = (WheelView) rootView.findViewById(R.id.wheel_dialog_weight_selector);
        tv_dialog_weight_selector_ok.setOnClickListener(onOK);
        if (wheel_dialog_weight_selector.isEmpty()) {
            wheel_dialog_weight_selector.setItems(weightList);
        }
    }

    public void setOnOK(View.OnClickListener onOK) {
        this.onOK = onOK;
    }

    public String getWeight() {
        return wheel_dialog_weight_selector.getSelectedItem();
    }
}
