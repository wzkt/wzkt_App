package com.Alan.eva.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.Alan.eva.R;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.tools.alarm.Alarm;
import com.Alan.eva.tools.alarm.AlarmClockManager;
import com.Alan.eva.tools.alarm.AlarmHandle;
import com.Alan.eva.ui.adapter.AlarmAdapter;
import com.Alan.eva.ui.core.AbsActivity;

import java.util.ArrayList;

public class AlarmListActivity extends AbsActivity {

    private RecyclerView recycler_alarm_list_list;
    private AlarmAdapter adapter;

    private final int ADD_NEW_ALARM = 0x0010;

    @Override
    public Activity getCurrActivity() {
        return this;
    }

    @Override
    public int getRootViewId() {
        return R.layout.ac_alarm_list;
    }

    @Override
    public void findView(View rootView) {
        Toolbar tool_bar_child_detail_title = (Toolbar) getView(R.id.tool_bar_title_common);
        tool_bar_child_detail_title.setTitleTextColor(Tools.getColor(getCurrActivity(), R.color.white));
        tool_bar_child_detail_title.setTitle("预约提醒");
        setSupportActionBar(tool_bar_child_detail_title);
        tool_bar_child_detail_title.setNavigationIcon(R.mipmap.ic_flag_back);
        tool_bar_child_detail_title.setNavigationOnClickListener((View v) -> currFinish());
        tool_bar_child_detail_title.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.title_bar_alarm_title_add) {
                Intent intent = getIntent(SetTimeActivity.class);
                startActivityForResult(intent, ADD_NEW_ALARM);
            }
            return true;
        });
        recycler_alarm_list_list = (RecyclerView) getView(R.id.recycler_alarm_list_list);
        LinearLayoutManager manager = new LinearLayoutManager(getCurrActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_alarm_list_list.setLayoutManager(manager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlarmClockManager.setNextAlarm(getCurrActivity());
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm_list_title, menu);
        return true;
    }


    private void refresh() {
        ArrayList<Alarm> alarms = AlarmHandle.getAlarms(getCurrActivity());
        if (Tools.isListEmpty(alarms)) {
            showTips("暂无预约，请添加");
            return;
        }
        notifyAdapter(alarms);
    }

    private void notifyAdapter(ArrayList<Alarm> arrayList) {
        if (adapter == null) {
            adapter = new AlarmAdapter(arrayList, getCurrActivity());
            recycler_alarm_list_list.setAdapter(adapter);
        } else {
            adapter.setDataList(arrayList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_NEW_ALARM) {
                if (data != null) {
                    boolean needRefresh = data.getBooleanExtra("refresh", false);
                    if (needRefresh) {
                        refresh();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
