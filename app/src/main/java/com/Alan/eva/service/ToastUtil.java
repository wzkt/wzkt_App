package com.Alan.eva.service;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    private static Toast toast = null;

    /**
     * Toast提示
     *
     * @param context
     * @param message 直接传String类型字符串
     */
    public static void show(Context context, String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    /**
     * Ｔoast提示
     *
     * @param context
     * @param message 通过资源id（R.string.xxx）来传递
     */
    public static void show(Context context, int message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
        }
        toast.show();
    }
}
