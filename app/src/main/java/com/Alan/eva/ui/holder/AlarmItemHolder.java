package com.Alan.eva.ui.holder;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.CheckBox;

import com.Alan.eva.R;
import com.Alan.eva.result.QueryMonitorRes;
import com.Alan.eva.tools.alarm.Alarm;
import com.Alan.eva.tools.alarm.AlarmClockManager;
import com.Alan.eva.tools.alarm.AlarmHandle;
import com.Alan.eva.ui.activity.SetTimeActivity;
import com.Alan.eva.ui.core.AbsViewHolder;

/**
 * Created by CW on 2017/5/5.
 * 选择铃声时的操作
 */
public class AlarmItemHolder extends AbsViewHolder { private AppCompatTextView tv_alarm_list_item_time;
    private AppCompatTextView tv_alarm_list_item_repeat;
    private AppCompatCheckBox cb_alarm_list_item_open;

    public AlarmItemHolder(View itemView) {
        super(itemView);
        tv_alarm_list_item_repeat = (AppCompatTextView) itemView.findViewById(R.id.tv_alarm_list_item_repeat);
        tv_alarm_list_item_time = (AppCompatTextView) itemView.findViewById(R.id.tv_alarm_list_item_time);
        cb_alarm_list_item_open = (AppCompatCheckBox) itemView.findViewById(R.id.cb_alarm_list_item_open);
    }

    public void bindData(Alarm alarm) {
        String hourStr = alarm.hour > 9 ? String.valueOf(alarm.hour) : String.valueOf("0" + alarm.hour);
        String minutesStr = alarm.minutes > 9 ? String.valueOf(alarm.minutes) : String.valueOf("0" + alarm.minutes);
        tv_alarm_list_item_time.setText(String.valueOf(hourStr + ":" + minutesStr));
        tv_alarm_list_item_repeat.setText(alarm.repeat);
        cb_alarm_list_item_open.setChecked(alarm.enabled == 1);
        cb_alarm_list_item_open.setOnClickListener(v -> {
            ContentValues values = new ContentValues();
            boolean isChecked = ((CheckBox) v).isChecked();
            values.put(Alarm.Columns.ENABLED, isChecked ? 1 : 0);
            AlarmHandle.updateAlarm(getActivity(), values, alarm.id,alarm);
            alarm.enabled = isChecked ? 1 : 0;
            if (isChecked) {
                AlarmClockManager.setAlarm(getActivity(), alarm);
            } else {
                AlarmClockManager.cancelAlarm(getActivity(), alarm.id);
            }
        });
        getRootView().setOnClickListener(v -> {
            Intent intent = getIntent(SetTimeActivity.class);
            intent.putExtra("alarm", alarm);
            getActivity().startActivityForResult(intent, 10);
        });
    }
}
