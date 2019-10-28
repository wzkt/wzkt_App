package com.Alan.eva.ui.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Alan.eva.R;

public class MyAutoDialogUtil {
 
    private static AlertDialog dialog;
 
    /**
     *
     * @param context
     *            上下文
     * @param text
     *            自定义显示的文字
     * @param id
     *            自定义图片资源
     */
    public static void showScanNumberDialog(final Context context, String text,
                                            int id) {
        // SysApplication.getInstance().exit();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // 创建对话框
        dialog = builder.create();
        // 没有下面这句代码会导致自定义对话框还存在原有的背景
 
        // 弹出对话框
        dialog.show();
        // 以下两行代码是对话框的EditText点击后不能显示输入法的
        dialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        Window window = dialog.getWindow();
        window.setContentView(R.layout.auto_dialog);
        TextView tv_scan_number = (TextView) window.findViewById(R.id.tv_dialoghint);
        tv_scan_number.setText(text);
        // 实例化确定按钮
        Button btn_hint_yes = (Button) window.findViewById(R.id.btn_hint_yes);
        // 实例化取消按钮
        Button btn_hint_no = (Button) window.findViewById(R.id.btn_hint_no);
        // 实例化图片
        ImageView iv_dialoghint = (ImageView) window.findViewById(R.id.iv_dialoghint);
        // 自定义图片的资源
        iv_dialoghint.setImageResource(id);
        btn_hint_yes.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //Toast.makeText(context, "确定", 0).show();
                dialog.dismiss();
            }
        });
        btn_hint_no.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //Toast.makeText(context, "取消", 0).show();
                dialog.dismiss();
            }
        });
    }
 
    public static void dismissScanNumberDialog() {
        dialog.dismiss();
    }
 
}