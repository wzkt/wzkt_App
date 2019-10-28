package com.Alan.eva.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.Alan.eva.R;
import com.Alan.eva.tools.LogUtil;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.tools.alarm.Alarm;
import com.Alan.eva.tools.alarm.AlarmClockManager;
import com.Alan.eva.tools.alarm.AlarmHandle;
import com.Alan.eva.tools.alarm.AlarmNotificationManager;
import com.Alan.eva.ui.core.AbsActivity;

import java.util.Calendar;

public class SetTimeActivity extends AbsActivity implements View.OnClickListener {
    // 打开对话框的标志
    private final static int SHOW_REPEAT = 1;
    private final static int DEL_ALARM = 3;
    private Toolbar tool_bar_alarm_title;
    private TimePicker time_picker_time_indicator;
    private AppCompatTextView tv_set_time_repeat_count;
    private AppCompatTextView tv_set_time_bell_name;
    private AppCompatButton btn_set_time_delete;
    private AppCompatCheckBox cb_set_time_is_vibration;
    private AppCompatEditText edit_set_time_medicine;

    private Alarm alarm;

    private String bellPath;
    // 是否打开震动
    private int vibration = 1;
    // 记录重复方式 0只响一次，1周一到周五，2每天
    private int repeat = 0;
    private int repeatOld = 0;

    @Override
    public Activity getCurrActivity() {
        return this;
    }

    @Override
    public int getRootViewId() {
        return R.layout.ac_add_new_alarm;
    }

    @Override
    public void findView(View rootView) {
        tool_bar_alarm_title = (Toolbar) getView(R.id.tool_bar_alarm_title);
        tool_bar_alarm_title.setTitle("预约");
        tool_bar_alarm_title.setTitleTextColor(Tools.getColor(getCurrActivity(), R.color.white));
        setSupportActionBar(tool_bar_alarm_title);
        tool_bar_alarm_title.setNavigationIcon(R.mipmap.ic_flag_back);
        tool_bar_alarm_title.setNavigationOnClickListener((View v) -> currFinish());
        tool_bar_alarm_title.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.title_bar_set_time_alarm_ok:
                    addAlarm();
                    break;
            }
            return true;
        });
        time_picker_time_indicator = (TimePicker) getView(R.id.time_picker_set_time_indicator);
        time_picker_time_indicator.setIs24HourView(true);
        time_picker_time_indicator.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        tv_set_time_repeat_count = (AppCompatTextView) getView(R.id.tv_set_time_repeat_count);
        tv_set_time_bell_name = (AppCompatTextView) getView(R.id.tv_set_time_bell_name);
        cb_set_time_is_vibration = (AppCompatCheckBox) getView(R.id.cb_set_time_is_vibration);
        edit_set_time_medicine = (AppCompatEditText) getView(R.id.edit_set_time_medicine);
        btn_set_time_delete = (AppCompatButton) getView(R.id.btn_set_time_delete);

        RelativeLayout rl_set_time_repeat_count = (RelativeLayout) getView(R.id.rl_set_time_repeat_count);
        RelativeLayout rl_set_time_bell_name = (RelativeLayout) getView(R.id.rl_set_time_bell_name);
        RelativeLayout rl_set_time_is_vibration = (RelativeLayout) getView(R.id.rl_set_time_is_vibration);
        rl_set_time_repeat_count.setOnClickListener(this);
        rl_set_time_bell_name.setOnClickListener(this);
        rl_set_time_is_vibration.setOnClickListener(this);
        cb_set_time_is_vibration.setClickable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm_title, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        alarm = (Alarm) intent.getSerializableExtra("alarm");
        if (alarm == null) {
            LogUtil.info(tool_bar_alarm_title.getTitle().toString());
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            showTime(hour, minute);
            tv_set_time_repeat_count.setText("只响一次");
            bellPath = getDefaultBell();
            String temp[] = bellPath.split("/");
            tv_set_time_bell_name.setText(temp[temp.length - 1].split("\\.")[0]);
            cb_set_time_is_vibration.setChecked(true);
            btn_set_time_delete.setVisibility(View.GONE);
        } else {
            int hour = alarm.hour;
            int minute = alarm.minutes;
            showTime(hour, minute);
            tv_set_time_repeat_count.setText(alarm.repeat);
            repeatOld = alarm.repeat.equals("只响一次") ? 0 : alarm.repeat.equals("周一到周五") ? 1 : 2;
            repeat = repeatOld;
            bellPath = alarm.bell;
            String temp[] = bellPath.split("/");
            tv_set_time_bell_name.setText(temp[temp.length - 1].split("\\.")[0]);
            cb_set_time_is_vibration.setChecked(alarm.vibrate == 1);
            edit_set_time_medicine.setText(alarm.label);
            btn_set_time_delete.setVisibility(View.VISIBLE);
            btn_set_time_delete.setOnClickListener(this);
        }
    }

    private String getDefaultBell() {
        String ret = "";
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                ret = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
            }
            cursor.close();
        }
        return ret;
    }

    /**
     * 给时间选择器赋时间
     *
     * @param hour   小时
     * @param minute 分钟
     */

    private void showTime(int hour, int minute) {
        LogUtil.info("小时===" + hour + "，，，，分钟===" + minute);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            time_picker_time_indicator.setHour(hour);
            time_picker_time_indicator.setMinute(minute);
        } else {
            //noinspection deprecation
            time_picker_time_indicator.setCurrentHour(hour);
            //noinspection deprecation
            time_picker_time_indicator.setCurrentMinute(minute);
        }
    }

    /**
     * 获取时间选择器上 小时
     *
     * @return 小时数
     */
    private int getHour() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return time_picker_time_indicator.getHour();
        } else {
            //noinspection deprecation
            return time_picker_time_indicator.getCurrentHour();
        }
    }

    private int getMinute() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return time_picker_time_indicator.getMinute();
        } else {
            //noinspection deprecation
            return time_picker_time_indicator.getCurrentMinute();
        }
    }

    /**
     * 添加闹钟
     */
    private void addAlarm() {
        Intent intent = new Intent();
        int hour = getHour();
        int minute = getMinute();
        if (alarm == null) {
            alarm = new Alarm();
            alarm.hour = getHour();
            alarm.minutes = getMinute();
            alarm.repeat = repeat == 0 ? "只响一次" : repeat == 1 ? "周一到周五" : "每天";
            alarm.bell = bellPath;
            alarm.vibrate = vibration;
            alarm.label = TextUtils.isEmpty(edit_set_time_medicine.getText()) ? "" : edit_set_time_medicine.getText().toString();
            alarm.enabled = 1;
            alarm.nextMillis = 0;
            AlarmHandle.addAlarm(getCurrActivity(), alarm);
            intent.putExtra("alarm", alarm);
        } else {
            ContentValues values = new ContentValues();

            values.put(Alarm.Columns.HOUR, hour);
            alarm.hour = hour;

            values.put(Alarm.Columns.MINUTES, minute);
            alarm.minutes = minute;

            values.put(Alarm.Columns.REPEAT, repeat == 0 ? "只响一次" : repeat == 1 ? "周一到周五" : "每天");
            alarm.repeat = repeat == 0 ? "只响一次" : repeat == 1 ? "周一到周五" : "每天";

            if (!TextUtils.isEmpty(bellPath) && !alarm.bell.equals(bellPath)) {
                values.put(Alarm.Columns.BELL, bellPath);
            }
            values.put(Alarm.Columns.VIBRATE, vibration);
            values.put(Alarm.Columns.LABEL, edit_set_time_medicine.getText().toString());

            if (alarm.enabled != 1) {
                values.put(Alarm.Columns.ENABLED, 1);
                alarm.enabled = 1;
            }
            if (values.size() > 0) {
                AlarmHandle.updateOldAlarm(getCurrActivity(), values, alarm.id,alarm);
            }
        }
        intent.putExtra("refresh", true);
        // 返回更新
        setResult(RESULT_OK, intent);
        finish();
    }

    private final int BELL_SELECT_CODE = 0x0002;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_set_time_repeat_count:
                //noinspection deprecation
                showDialog(SHOW_REPEAT);
                break;
            case R.id.rl_set_time_bell_name:
                Intent selectBell = new Intent(getCurrActivity(), SelectBellActivity.class);
                selectBell.putExtra("bellPath", bellPath);
                selectBell.putExtra("bellName", tv_set_time_bell_name.getText());
                startActivityForResult(selectBell, BELL_SELECT_CODE);
                break;
            case R.id.rl_set_time_is_vibration:
                boolean checked = cb_set_time_is_vibration.isChecked();
                if (checked) {
                    vibration = 1;
                } else {
                    vibration = 0;
                }
                break;
            case R.id.btn_set_time_delete:
                //noinspection deprecation
                showDialog(DEL_ALARM);
                break;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case SHOW_REPEAT:
                dialog = new AlertDialog.Builder(getCurrActivity()).setTitle(getResources().getText(R.string.repeat_text))
                        .setSingleChoiceItems(R.array.repeat_item, repeatOld, (dialog12, which) -> {
                            tv_set_time_repeat_count.setText(getResources().getStringArray(R.array.repeat_item)[which]);
                            repeat = which;
                            dialog12.dismiss();
                        }).setNegativeButton("取消", null).create();
                dialog.setCanceledOnTouchOutside(false);
                break;

            case DEL_ALARM:
                dialog = new AlertDialog.Builder(getCurrActivity()).setTitle(getResources().getText(R.string.del_clock))
                        .setMessage("是否删除此闹钟？").setNegativeButton("取消", null)
                        .setPositiveButton("确定", (dialog1, which) -> {
                            if (alarm.enabled == 1) {
                                AlarmClockManager.cancelAlarm(getCurrActivity(), alarm.id);
                            }
                            AlarmHandle.deleteAlarm(getCurrActivity(), alarm.id);
                            setResult(Alarm.DELETE_ALARM);
                            AlarmNotificationManager.cancelNotification();
                            finish();
                        }).create();
                dialog.setCanceledOnTouchOutside(false);
        }
        return dialog;
    }

    // 获得选择铃声的名称和路径
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == BELL_SELECT_CODE) {
                String name = data.getStringExtra("name");
                if (!TextUtils.isEmpty(name)) {
                    tv_set_time_bell_name.setText(name);
                }
                bellPath = data.getStringExtra("path");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
