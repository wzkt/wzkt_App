package com.Alan.eva.ui.core;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.Alan.eva.tools.Tools;

/**
 * Created by wei19 on 2016/9/29.
 * 抽象视图承载器
 */
public abstract class AbsViewHolder extends RecyclerView.ViewHolder {
    private Activity activity;
    private View view;
    private Intent intent;

    public AbsViewHolder(View itemView) {
        super(itemView);
        this.view = itemView;
    }

    protected Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    protected View getRootView(int id) {
        return view.findViewById(id);
    }

    protected Intent getIntent(Class clazz) {
        if (intent == null) {
            intent = new Intent();
        }
        intent.setClass(getActivity(), clazz);
        return intent;
    }

    public View getRootView() {
        return view;
    }

    protected void showTips(String tips) {
        if (activity != null) {
            Tools.showTips(activity, tips);
        }
    }
}
