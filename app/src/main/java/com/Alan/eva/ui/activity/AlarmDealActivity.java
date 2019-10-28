package com.Alan.eva.ui.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.Alan.eva.R;
import com.Alan.eva.tools.alarm.Alarm;
import com.Alan.eva.tools.alarm.AlarmClockManager;
import com.Alan.eva.tools.alarm.AlarmHandle;
import com.Alan.eva.tools.alarm.AlarmService;

public class AlarmDealActivity extends Activity {
    private Context context;
    private TextView tvshow;
    // 闹钟内容
    private Alarm alarm;
    // 服务
    private AlarmService alarmService;
    private ServiceConnection SConn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 链接服务成功得到服务
            alarmService = ((AlarmService.MyBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            alarmService = null;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置无标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        final Window win = getWindow();
        // 四个参数，锁屏显示，解锁，保持屏幕常亮，打开屏幕
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.ac_alarm_play);
        init();
        initView();
    }

    private void initView() {
        tvshow = (TextView) findViewById(R.id.alarmdel_tv_showlable);
        if(alarm!=null)tvshow.setText(alarm.label);
    }

    private void init() {
        context = this;
        int id = getIntent().getIntExtra(Alarm.Columns._ID, 0);

        Log.e("hjs", "init: "+id);
        if(id==0){
            if(alarm==null){
                alarm = new Alarm();
            }
            alarm.repeat="只响一次";
            alarm.bell="/system/media/audio/alarms/Beep.ogg";
            alarm.vibrate=1;
            alarm.enabled = 1;
            Intent intent = new Intent(this, AlarmService.class);
            intent.putExtra("alarm", alarm);
            bindService(intent, SConn, Context.BIND_AUTO_CREATE);
        }
        if (id != 0) {
            // 根据ID获得闹钟的详细信息
            alarm = AlarmHandle.getAlarm(context, id);
            // 开启服务，监听电话状态和音乐播放
            Intent intent = new Intent(this, AlarmService.class);

            if(alarm==null){
                alarm = new Alarm();
            }
            if(TextUtils.isEmpty(alarm.bell)){
            alarm.repeat="只响一次";
            alarm.bell="/system/media/audio/alarms/Beep.ogg";
            alarm.vibrate=1;
            alarm.enabled = 1;
            }

            intent.putExtra("alarm", alarm);
            bindService(intent, SConn, Context.BIND_AUTO_CREATE);
        }

    }

    private void alarmFinish(Alarm alarm) {
        if(alarm==null) return;
        if (alarmService != null) {
            alarmService.stop();
        }

        String[] repeats = context.getResources().getStringArray(R.array.repeat_item);
        Log.v("hjs", "alarm.repeat.equals(repeats[Alarm.ALARM_ONCE])="+alarm.repeat.equals(repeats[Alarm.ALARM_ONCE]));
        if (alarm.repeat.equals(repeats[Alarm.ALARM_ONCE])) {
            ContentValues values = new ContentValues();
            values.put(Alarm.Columns.ENABLED, 0);
            // 更新数据库
            AlarmHandle.updateAlarm(context, values, alarm.id,alarm);

        } else {
            long timeMillis = AlarmClockManager.time2Millis(alarm.hour, alarm.minutes, alarm.repeat, repeats);
            // 将下次响铃时间的毫秒数存到数据库
            ContentValues values = new ContentValues();
            values.put(Alarm.Columns.NEXTMILLIS, timeMillis);
            AlarmHandle.updateAlarm(context, values, alarm.id,alarm);

        }
        AlarmClockManager.setNextAlarm(context);
    }

    protected void onDestroy() {
        super.onDestroy();
        release();
        // 销毁指定服务
        if (alarmService != null) {
            unbindService(SConn);
        }
    }

    // 释放铃声和震动资源
    private void release() {
        // 关闭铃声和震动
        if (alarmService != null) {
            alarmService.stop();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_HOME:
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_MUTE:
                return true;
            default:
                return false;
        }
    }

    public void clickButton(View v) {
        switch (v.getId()) {
            case R.id.alarmDel_btn_cancel:
                release();
                alarmFinish(alarm);
                //setResult(Alarm.DELETE_ALARM);
                finish();
                break;
            default:
                break;
        }
    }

}
