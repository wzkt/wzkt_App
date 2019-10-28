package com.Alan.eva.ui.holder;

import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.Alan.eva.R;
import com.Alan.eva.result.QueryMonitorRes;
import com.Alan.eva.tools.LogUtil;
import com.Alan.eva.ui.core.AbsViewHolder;

/**
 * Created by CW on 2017/5/5.
 *
 */
public class MonitorItemHolder extends AbsViewHolder {

    private AppCompatTextView tv_monitor_list_item_temp;
    private AppCompatTextView tv_monitor_list_item_room;
    private AppCompatTextView tv_monitor_list_item_power;
    private AppCompatTextView tv_monitor_list_item_time;

    public MonitorItemHolder(View itemView) {
        super(itemView);
        tv_monitor_list_item_temp = (AppCompatTextView) itemView.findViewById(R.id.tv_monitor_list_item_temp);
        tv_monitor_list_item_room = (AppCompatTextView) itemView.findViewById(R.id.tv_monitor_list_item_room);
        tv_monitor_list_item_power = (AppCompatTextView) itemView.findViewById(R.id.tv_monitor_list_item_power);
        tv_monitor_list_item_time = (AppCompatTextView) itemView.findViewById(R.id.tv_monitor_list_item_time);
    }

    public void bindData(QueryMonitorRes mon) {
        tv_monitor_list_item_temp.setText(mon.getBody_temperature()+"℃");
        tv_monitor_list_item_room.setText(mon.getRoom_temperature()+"℃");
        tv_monitor_list_item_power.setText(mon.getPower()+"%");
        LogUtil.info("mon:" + mon.getCreate_time());
        tv_monitor_list_item_time.setText("时间:"+mon.getCreate_time());
    }
}
