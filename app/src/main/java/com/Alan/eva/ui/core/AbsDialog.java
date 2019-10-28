package com.Alan.eva.ui.core;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

class AbsDialog extends Dialog {

    private boolean needInputMethod = false;

    public AbsDialog(Context context, boolean needInputMethod) {
        super(context);
        this.needInputMethod = needInputMethod;
    }

    AbsDialog(Context context, int theme, boolean needInputMethod) {
        super(context, theme);
        this.needInputMethod = needInputMethod;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window win = getWindow();
        if (win != null) {
            win.setBackgroundDrawableResource(android.R.color.transparent);
        }
        if (needInputMethod) {
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        } else {
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }
}
