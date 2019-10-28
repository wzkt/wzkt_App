package com.Alan.eva.ui.dialog;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.Alan.eva.R;
import com.Alan.eva.ui.core.AbsDialogCreator;

/**
 * Created by CW on 2017/3/10.
 * 性别选择对话框
 */
public class GenderDialog extends AbsDialogCreator {
    private View.OnClickListener onMale, onFemale;

    public GenderDialog(Context context) {
        super(context);
    }

    @Override
    public int getRootViewId() {
        return R.layout.dialog_choose_gender;
    }

    @Override
    public void findView(View rootView) {
        AppCompatTextView tv_gender_choose_female = (AppCompatTextView) rootView.findViewById(R.id.tv_gender_choose_female);
        AppCompatTextView tv_gender_choose_male = (AppCompatTextView) rootView.findViewById(R.id.tv_gender_choose_male);
        tv_gender_choose_female.setOnClickListener(onFemale);
        tv_gender_choose_male.setOnClickListener(onMale);
    }

    public void setOnMale(View.OnClickListener onMale) {
        this.onMale = onMale;
    }

    public void setOnFemale(View.OnClickListener onFemale) {
        this.onFemale = onFemale;
    }
}
