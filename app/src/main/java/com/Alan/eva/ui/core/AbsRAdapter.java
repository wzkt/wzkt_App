package com.Alan.eva.ui.core;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.Alan.eva.tools.Tools;

import java.util.ArrayList;

/**
 * Created by wei19 on 2016/9/29.
 * 抽象recyclerView适配器
 */
public abstract class AbsRAdapter<T, VH extends AbsViewHolder> extends RecyclerView.Adapter<VH> {
    private ArrayList<T> dataList;
    private Activity activity;
    private LayoutInflater inflater;

    public AbsRAdapter(ArrayList<T> dataList, Activity activity) {
        this.dataList = dataList;
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
    }

    public AbsRAdapter(ArrayList<T> dataList) {
        this.dataList = dataList;
    }

    public AbsRAdapter(AbsActivity activity) {
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return Tools.isListEmpty(dataList) ? 0 : dataList.size();
    }


    protected ArrayList<T> getDataList() {
        return dataList;
    }

    public void add(T t) {
        if (!Tools.isListEmpty(dataList)) {
            dataList.add(t);
            this.notifyDataSetChanged();
        }
    }

    public void setDataList(ArrayList<T> dataList) {
        this.dataList = dataList;
        if (!Tools.isListEmpty(dataList)) {
            this.notifyDataSetChanged();
        }
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(AbsActivity activity) {
        this.activity = activity;
    }

    protected LayoutInflater getInflater() {
        return inflater;
    }

    protected View inflate(int id) {
        return inflater.inflate(id, null, false);
    }
}
