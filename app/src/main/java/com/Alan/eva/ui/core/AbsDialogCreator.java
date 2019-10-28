package com.Alan.eva.ui.core;


import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;

import com.Alan.eva.R;


public abstract class AbsDialogCreator {

    private AbsDialog mDialog;
    private Context mContext;
    private LayoutInflater inflater;

    /**
     * 可取消？
     */
    private boolean cancelable = true;

    /**
     * 外部可点击？
     */
    private boolean touchable = true;

    /**
     * 根视图
     */
    private View rootView;

    private IDialogDismiss iDialogDismiss;

    private boolean showing;

    /**
     * 获取根布局ID
     *
     * @return id
     */
    public abstract int getRootViewId();

    /**
     * 绑定布局
     *
     * @param rootView rootView
     */
    public abstract void findView(View rootView);

    public AbsDialogCreator(Context context) {
        this(context, false);
    }

    public AbsDialogCreator(Context context, boolean needInputMethod) {
        this(context, R.style.dialog_transportation, needInputMethod);
    }

    public AbsDialogCreator(Context context, int theme, boolean needInputMethod) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.rootView = inflater.inflate(getRootViewId(), null);
        this.mDialog = new AbsDialog(mContext, theme, needInputMethod);
    }

    /**
     * 设置对话框是否能被取消
     *
     * @param cancelable cancelable
     */
    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    /**
     * 设置对话框是否能在外部点击取消
     *
     * @param touchable touchable
     */
    public void setTouchable(boolean touchable) {
        this.touchable = touchable;
    }

    /**
     * 获取当前对话框的上下文
     *
     * @return 上下文
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * 获取视图过滤器
     *
     * @return 过滤器
     */
    public LayoutInflater getInflater() {
        return inflater;
    }

    /**
     * 生成默认对话框，居中，完全包裹
     */
    public void create() {
        create(Gravity.CENTER);
    }

    /**
     * 生成带有位置的对话 ,完整包裹对话框
     *
     * @param gravity 相对位置
     */
    public void create(int gravity) {
        int width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        create(gravity, width, height);
    }

    public void create(int width, int height) {
        create(Gravity.CENTER, width, height);
    }

    /**
     * 设置相对屏幕位置，主要是为了居中或者居下
     *
     * @param gravity 相对位置
     * @param width   宽度
     * @param height  高度
     */
    public void create(int gravity, int width, int height) {
        findView(rootView);
        mDialog.setCancelable(cancelable);
        mDialog.setCanceledOnTouchOutside(touchable);
        mDialog.setContentView(rootView);
        Window win = mDialog.getWindow();
        if (win != null) {
            win.setGravity(gravity);
            LayoutParams p = win.getAttributes();
            p.width = width;
            p.height = height;
            win.setAttributes(p);
        }
    }

    public Dialog getDialog() {
        return mDialog;
    }

    /**
     * 显示对话框
     */
    public void show() {
        if (mDialog != null && !mDialog.isShowing()) {
            showing = true;
            mDialog.show();
        }
    }

    /**
     * 隐藏对话框
     */
    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            showing = false;
            if (iDialogDismiss != null) {
                iDialogDismiss.onDismiss();
            }
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            boolean isOpen = imm.isActive();//isOpen若返回true，则表示输入法打开
            if (isOpen) {
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    protected View bindView(int id) {
        return rootView.findViewById(id);
    }

    public class MyCloseListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            dismiss();
        }

    }

    public void setOnDialogDismiss(IDialogDismiss iDialogDismiss) {
        this.iDialogDismiss = iDialogDismiss;
    }

    public void showView(View v) {
        v.setVisibility(View.VISIBLE);
    }

    public void hideView(View v) {
        v.setVisibility(View.INVISIBLE);
    }

    public interface IDialogDismiss {
        void onDismiss();
    }

    public boolean isShowing() {
        return showing;
    }
}
