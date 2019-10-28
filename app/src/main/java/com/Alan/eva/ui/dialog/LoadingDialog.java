package com.Alan.eva.ui.dialog;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.Alan.eva.R;
import com.Alan.eva.ui.core.AbsDialogCreator;


/**
 * Created by qc on 2015/12/15.
 * 等待对话框
 */
public class LoadingDialog extends AbsDialogCreator {
    private String tips;

    public LoadingDialog(Context context) {
        super(context);
    }

    public LoadingDialog(Context context, boolean needInputMethod) {
        super(context, needInputMethod);
    }

    public LoadingDialog(Context context, int theme, boolean needInputMethod) {
        super(context, theme, needInputMethod);
    }

    @Override
    public int getRootViewId() {
        return R.layout.dialog_loading;
    }

    @Override
    public void findView(View rootView) {
        AppCompatTextView tv_dialog_processing_text = (AppCompatTextView) rootView.findViewById(R.id.tv_dialog_loading_tips);
        tv_dialog_processing_text.setText(tips);
    }

    public void tips(String tip) {
        this.tips = tip;
    }


}
