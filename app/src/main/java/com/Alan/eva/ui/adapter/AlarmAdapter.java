package com.Alan.eva.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.Alan.eva.tools.alarm.Alarm;
import com.Alan.eva.ui.core.AbsRAdapter;
import com.Alan.eva.ui.holder.AlarmItemHolder;

import java.util.ArrayList;

import static com.Alan.eva.R.layout.item_alarm_list_holder;

/**
 * Created by CW on 2017/5/5.
 * 铃声选择界面
 */
public class AlarmAdapter extends AbsRAdapter<Alarm, AlarmItemHolder> {
    public AlarmAdapter(ArrayList<Alarm> dataList, Activity activity) {
        super(dataList, activity);
    }

    @Override
    public AlarmItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getInflater().inflate(item_alarm_list_holder, parent, false);
        AlarmItemHolder holder = new AlarmItemHolder(view);
        holder.setActivity(getActivity());
        return holder;
    }

    @Override
    public void onBindViewHolder(AlarmItemHolder holder, int position) {
        Alarm alarm = getDataList().get(position);
        holder.bindData(alarm);
    }
}
