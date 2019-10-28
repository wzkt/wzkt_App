package com.Alan.eva.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.Alan.eva.R;
import com.Alan.eva.model.SysBellBean;
import com.Alan.eva.ui.activity.SelectBellActivity;
import com.Alan.eva.ui.core.AbsRAdapter;
import com.Alan.eva.ui.holder.SysBellsHolder;

import java.util.ArrayList;

/**
 * Created by CW on 2017/5/11.
 * 系统铃声适配器
 */
public class SysBellAdapter extends AbsRAdapter<SysBellBean, SysBellsHolder> {
    private int currIndex;
    private SelectBellActivity activity;

    public SysBellAdapter(ArrayList<SysBellBean> dataList, SelectBellActivity activity) {
        super(dataList, activity);
        this.currIndex = -1;
        this.activity = activity;
    }

    @Override
    public SysBellsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getInflater().inflate(R.layout.item_sys_bell_holder, parent, false);
        return new SysBellsHolder(view, activity);
    }

    @Override
    public void onBindViewHolder(SysBellsHolder holder, int position) {
        holder.bindData(getDataList(), position, this);
    }

    public int getCurrIndex() {
        return currIndex;
    }

    public void setIndex(int index) {
        if (currIndex != index) {
            ArrayList<SysBellBean> list = getDataList();
            if (currIndex >= 0) {
                SysBellBean bellBean = list.get(currIndex);
                bellBean.setCheck(false);
                list.set(currIndex, bellBean);
                this.notifyItemChanged(currIndex);
            }
            SysBellBean bellBean = list.get(index);
            bellBean.setCheck(true);
            list.set(index, bellBean);
            this.notifyItemChanged(index);
            this.currIndex = index;
        }
    }
}
