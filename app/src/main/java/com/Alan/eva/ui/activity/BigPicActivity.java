package com.Alan.eva.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewGroup;

import com.Alan.eva.R;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.ui.EApp;
import com.Alan.eva.ui.core.AbsActivity;

/**
 * Created by CW on 2017/3/22.
 * 大图浏览界面
 */
public class BigPicActivity extends AbsActivity {
    private AppCompatImageView iv_big_pic_shower;

    @Override
    public Activity getCurrActivity() {
        return this;
    }

    @Override
    public int getRootViewId() {
        return R.layout.ac_big_picture;
    }

    @Override
    public void findView(View rootView) {
        iv_big_pic_shower = (AppCompatImageView) getView(R.id.iv_big_pic_shower);
        iv_big_pic_shower.setOnClickListener(v -> currFinish());
        int wid = EApp.getApp().getScreenWidth();
        ViewGroup.LayoutParams lp = iv_big_pic_shower.getLayoutParams();
        lp.width = wid;
        lp.height = wid;
        iv_big_pic_shower.setLayoutParams(lp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String pic = intent.getStringExtra("url");
        Tools.display(iv_big_pic_shower, pic);
    }
}
