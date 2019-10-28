package com.Alan.eva.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.Alan.eva.result.QueryMonitorRes;
import com.Alan.eva.tools.LogUtil;
import com.Alan.eva.ui.core.AbsRAdapter;
import com.Alan.eva.ui.holder.MonitorItemHolder;

import java.util.ArrayList;

import static com.Alan.eva.R.layout.item_monitor_hislist_holder;

/**
 * Created by CW on 2017/5/5.
 *
 */
public class MonitorAdapter extends AbsRAdapter<QueryMonitorRes, MonitorItemHolder> {
    public MonitorAdapter(ArrayList<QueryMonitorRes> dataList, Activity activity) {
        super(dataList, activity);
    }

    @Override
    public MonitorItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getInflater().inflate(item_monitor_hislist_holder, parent, false);
        MonitorItemHolder holder = new MonitorItemHolder(view);
        holder.setActivity(getActivity());
        return holder;
    }

    @Override
    public void onBindViewHolder(MonitorItemHolder holder, int position) {
        QueryMonitorRes mon = getDataList().get(position);
        holder.bindData(mon);
    }
}
