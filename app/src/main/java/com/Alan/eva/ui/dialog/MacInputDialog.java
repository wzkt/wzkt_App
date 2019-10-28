package com.Alan.eva.ui.dialog;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.InputType;
import android.view.View;

import com.Alan.eva.R;
import com.Alan.eva.ui.core.AbsDialogCreator;

/**
 * Created by CW on 2017/3/24.
 * 单输入对话框
 */
public class MacInputDialog extends AbsDialogCreator {
    private String title = "请输入内容";
    private String contentHint = "请输入内容";
    private String cancel = "取消";
    private String ok = "确定";
    private View.OnClickListener onCancel;
    private View.OnClickListener onOk;
    private AppCompatEditText edt_input_dialog_content;
    private AppCompatTextView tv_input_dialog_error_tips;

    public MacInputDialog(Context context) {
        super(context, true);
    }

    @Override
    public int getRootViewId() {
        return R.layout.dialog_mac_input_operator;
    }

    @Override
    public void findView(View rootView) {
        AppCompatTextView tv_input_dialog_title = (AppCompatTextView) rootView.findViewById(R.id.mac_tv_input_dialog_title);
        edt_input_dialog_content = (AppCompatEditText) rootView.findViewById(R.id.mac_edt_input_dialog_content);
        tv_input_dialog_error_tips = (AppCompatTextView) rootView.findViewById(R.id.mac_tv_input_dialog_error_tips);
        AppCompatTextView tv_input_dialog_cancel = (AppCompatTextView) rootView.findViewById(R.id.mac_tv_input_dialog_cancel);
        AppCompatTextView tv_input_dialog_ok = (AppCompatTextView) rootView.findViewById(R.id.mac_tv_input_dialog_ok);
        tv_input_dialog_title.setText(title);
        edt_input_dialog_content.setHint(contentHint);
        tv_input_dialog_cancel.setText(cancel);
        tv_input_dialog_cancel.setOnClickListener(onCancel == null ? new MyCloseListener() : onCancel);
        tv_input_dialog_ok.setText(ok);
        tv_input_dialog_ok.setOnClickListener(onOk);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContentHint(String contentHint) {
        this.contentHint = contentHint;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

    public void setOnCancel(View.OnClickListener onCancel) {
        this.onCancel = onCancel;
    }

    public void setOnOk(View.OnClickListener onOk) {
        this.onOk = onOk;
    }

    public String getContent() {
        return edt_input_dialog_content.getText().toString();
    }

    public void setContent(String content){
        edt_input_dialog_content.setText(content);
    }

    public void errorAlert(String tips) {
        tv_input_dialog_error_tips.setVisibility(View.VISIBLE);
        tv_input_dialog_error_tips.setText(tips);
    }
}
