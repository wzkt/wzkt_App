package com.Alan.eva.tools;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.Alan.eva.ui.activity.BigPicActivity;

/**
 * Created by CW on 2017/3/22.
 * 大图浏览监听
 */
public class OnBigPicListener implements View.OnClickListener {
    private Context context;
    private String url;

    public OnBigPicListener(Context context, String url) {
        this.context = context;
        this.url = url;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, BigPicActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }
}
