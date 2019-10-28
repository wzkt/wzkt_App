package com.Alan.eva.ui.holder;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.Alan.eva.R;
import com.Alan.eva.model.SysBellBean;
import com.Alan.eva.ui.activity.SelectBellActivity;
import com.Alan.eva.ui.adapter.SysBellAdapter;
import com.Alan.eva.ui.core.AbsViewHolder;

import java.util.ArrayList;

/**
 * Created by CW on 2017/5/11.
 * 系统铃声
 */
public class SysBellsHolder extends AbsViewHolder {
    private AppCompatTextView tv_sys_bell_name;
    private AppCompatImageView iv_sys_bell_checked;
    private SelectBellActivity activity;

    public SysBellsHolder(View itemView, SelectBellActivity activity) {
        super(itemView);
        this.activity = activity;
        tv_sys_bell_name = (AppCompatTextView) itemView.findViewById(R.id.tv_sys_bell_name);
        iv_sys_bell_checked = (AppCompatImageView) itemView.findViewById(R.id.iv_sys_bell_checked);
    }

    public void bindData(ArrayList<SysBellBean> dataList, int position, SysBellAdapter adapter) {
        SysBellBean bellBean = dataList.get(position);
        String name = bellBean.getName();
        String path = bellBean.getPath();
        boolean check = bellBean.isCheck();
        tv_sys_bell_name.setText(name);
        if (check) {
            iv_sys_bell_checked.setVisibility(View.VISIBLE);
        } else {
            iv_sys_bell_checked.setVisibility(View.GONE);
        }
        getRootView().setOnClickListener(v -> {
            activity.playMusic(name, path);
            adapter.setIndex(position);
        });
    }
}
