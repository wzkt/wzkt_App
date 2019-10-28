package com.Alan.eva.ui.dialog;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;

import com.Alan.eva.R;
import com.Alan.eva.ui.core.AbsDialogCreator;

/**
 * Created by CW on 2017/3/15.
 * 操作型对话框
 */
public class OperateDialog extends AbsDialogCreator {
    private String content;
    private String cancel = "取消";
    private String ok = "确定";
    private View.OnClickListener onCancel;
    private View.OnClickListener onOk;

    public OperateDialog(Context context) {
        super(context);
    }

    @Override
    public int getRootViewId() {
        return R.layout.dialog_operator;
    }

    @Override
    public void findView(View rootView) {
        AppCompatTextView tv_operate_dialog_content = (AppCompatTextView) rootView.findViewById(R.id.tv_operate_dialog_content);
        AppCompatTextView tv_operate_dialog_cancel = (AppCompatTextView) rootView.findViewById(R.id.tv_operate_dialog_cancel);
        AppCompatTextView tv_operate_dialog_ok = (AppCompatTextView) rootView.findViewById(R.id.tv_operate_dialog_ok);
        tv_operate_dialog_content.setText(content);
        if (TextUtils.isEmpty(cancel)) {
            tv_operate_dialog_cancel.setVisibility(View.GONE);
        }else {
            tv_operate_dialog_cancel.setVisibility(View.VISIBLE);
            tv_operate_dialog_cancel.setText(cancel);
            if (onCancel == null) {
                tv_operate_dialog_cancel.setOnClickListener(new MyCloseListener());
            } else {
                tv_operate_dialog_cancel.setOnClickListener(onCancel);
            }
        }
        tv_operate_dialog_ok.setText(ok);
        tv_operate_dialog_ok.setOnClickListener(onOk);
    }

    public void setContent(String content) {
        this.content = content;
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
}
