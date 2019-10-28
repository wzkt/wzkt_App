package com.Alan.eva.tools.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class AlarmHandle {

    private final static String TAG = "hjs";
    private static final int INTERVAL = 1000 * 60 * 60 * 24;// 24h
    public AlarmHandle() {
    }

    // 增加一个闹钟
    public static void addAlarm(Context context, Alarm alarm) {

//        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            ContentValues values = alarm2ContentValues(alarm);
            Uri uri = context.getContentResolver().insert(Alarm.Columns.CONTENT_URI, values);
            alarm.id = (int) ContentUris.parseId(uri);

            Log.e(TAG, "alarm.id" +alarm.id);
            Log.e(TAG, "增加了一条闹钟" + alarm.hour + "" + alarm.minutes);
//        }else {
            AlarmManager alarmService = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            Calendar instance = Calendar.getInstance();
//            instance.set(Calendar.HOUR_OF_DAY, alarm.hour);
//            instance.set(Calendar.MINUTE, alarm.minutes);
//            instance.set(Calendar.SECOND, 01);
        instance.set(instance.get(Calendar.YEAR), instance.get(Calendar.MONTH), instance.get
                (Calendar.DAY_OF_MONTH), alarm.hour, alarm.minutes, 10);

            Intent alarmIntent = new Intent(context, AlarmReceiver.class);

            alarmIntent.putExtra("realtime", instance.getTimeInMillis());
            alarmIntent.putExtra("id",alarm.id);
             Log.e(TAG, "alarm.id" +alarm.id);

            PendingIntent broadcast = PendingIntent.getBroadcast(context, alarm.id, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmService.setRepeating(AlarmManager.RTC_WAKEUP, instance.getTimeInMillis(), INTERVAL, broadcast);
            //Log.e(TAG, "增加了一条闹钟instance.getTimeInMillis()" + instance.getTimeInMillis());


//        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
//        {
//            Log.e(TAG,">=.M");
//            alarmService.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,instance.getTimeInMillis(),broadcast);
//        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
//            Log.e(TAG,">=.KITKAT");
//            alarmService.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,instance.getTimeInMillis(),broadcast);
//        }else{
//            Log.e(TAG,">=.serrep");
//            alarmService.setRepeating(AlarmManager.RTC_WAKEUP, instance.getTimeInMillis(), INTERVAL, broadcast);
//        }
//        }

    }

    // 删除一个闹钟
    public static void deleteAlarm(Context context, int alarmId) {

        Uri uri = ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, alarmId);
        context.getContentResolver().delete(uri, null, null);
        Log.v(TAG, "删除一个闹钟  alarmId"+alarmId);

        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, alarmId, i, PendingIntent.FLAG_CANCEL_CURRENT);
        manager.cancel(pi);
    }

    // 删除了所有闹钟
    public static void deleteAllAlarm(Context context) {
        context.getContentResolver().delete(Alarm.Columns.CONTENT_URI, null, null);
        Log.v(TAG, "删除了所有闹钟");

        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent i = new Intent(context, AlarmclockReceive.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        manager.cancel(pi);
    }


    public static void updateAlarm(Context context, ContentValues values, int alarmId,Alarm alarm) {

//        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
        Uri uri = ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, alarmId);
        context.getContentResolver().update(uri, values, null, null);
        Log.e(TAG, "修改了一条闹钟  alarmId" + alarmId);

//        ArrayList<Alarm>  all  = getAlarms(context);
//        for(int i = 0 ;i<all.size();i++) {
//            Log.e("hjs", "alarm.alarmId: " + all.get(i).id);
//            Log.e("hjs", "alarm.enabled: " + all.get(i).enabled);
//            Log.e("hjs", "alarm.minutes: " + all.get(i).minutes);
//        }
//        }else {
//        if(values==null){
//            Log.e(TAG, "values===values" + values);
//            return;
//        }
//        int timevalue=0;
//        try {
//            timevalue = (int) values.get(Alarm.Columns.ENABLED);
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        Log.e(TAG, "alarm.repeat" + alarm.repeat);
        int timevalue=0;
        if(alarm.repeat.startsWith("只")){
            timevalue = 0;
        }else{
            timevalue = 1;
        }

        Log.v(TAG, "修改了一timevalue" + timevalue);
        if (timevalue == 0) {
            Log.v(TAG, "cancelAlarm   alarm.id" + alarm.id);
            AlarmClockManager.cancelAlarm(context, alarm.id);

        } else if (timevalue > 0) {
            AlarmManager alarmService = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            Calendar instance = Calendar.getInstance();
            instance.set(instance.get(Calendar.YEAR), instance.get(Calendar.MONTH), instance.get
                    (Calendar.DAY_OF_MONTH), alarm.hour, alarm.minutes, 10);
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            alarmIntent.putExtra("realtime", instance.getTimeInMillis());
            alarmIntent.putExtra("id",alarm.id);
            PendingIntent broadcast = PendingIntent.getBroadcast(context, alarm.id, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmService.setRepeating(AlarmManager.RTC_WAKEUP, instance.getTimeInMillis(), INTERVAL, broadcast);
        }

    }

    public static void updateOldAlarm(Context context, ContentValues values, int alarmId,Alarm alarm) {

        Uri uri = ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, alarmId);
        context.getContentResolver().update(uri, values, null, null);
        Log.e(TAG, "修改了一条闹钟  alarmId" + alarmId);

           AlarmManager alarmService = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            Calendar instance = Calendar.getInstance();
            instance.set(instance.get(Calendar.YEAR), instance.get(Calendar.MONTH), instance.get
                    (Calendar.DAY_OF_MONTH), alarm.hour, alarm.minutes, 10);
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            alarmIntent.putExtra("realtime", instance.getTimeInMillis());
            alarmIntent.putExtra("id",alarm.id);
            PendingIntent broadcast = PendingIntent.getBroadcast(context, alarm.id, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmService.setRepeating(AlarmManager.RTC_WAKEUP, instance.getTimeInMillis(), INTERVAL, broadcast);


    }

    // 根据ID号获得闹钟的信息
    public static Alarm getAlarm(Context context, int alarmId) {
        Uri uri = ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, alarmId);
        Cursor cursor = context.getContentResolver().query(uri, Alarm.Columns.ALARM_QUERY_COLUMNS, null, null, null);
        Alarm alarm = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                alarm = new Alarm(cursor);
            }
            cursor.close();
        }
        return alarm;
    }

    static Alarm getNextAlarm(Context context) {
        Cursor cursor = context.getContentResolver().query(Alarm.Columns.CONTENT_URI, Alarm.Columns.ALARM_QUERY_COLUMNS,
                Alarm.Columns.ENABLED_WHER, null, Alarm.Columns.ENABLED_SORT_ORDER);
        Alarm alarm = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                alarm = new Alarm(cursor);
            }
            cursor.close();
        }
        return alarm;
    }

    // 获得所有闹钟
    public static ArrayList<Alarm> getAlarms(Context context) {
        ArrayList<Alarm> alarmList = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(Alarm.Columns.CONTENT_URI, Alarm.Columns.ALARM_QUERY_COLUMNS,
                null, null, Alarm.Columns.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                while (cursor.moveToNext()) {
                    alarmList.add(new Alarm(cursor));
                }
                cursor.close();
            }
        }
        return alarmList;
    }

    private static ContentValues alarm2ContentValues(Alarm alarm) {
        ContentValues values = new ContentValues();
        values.put(Alarm.Columns.HOUR, alarm.hour);
        values.put(Alarm.Columns.MINUTES, alarm.minutes);
        values.put(Alarm.Columns.REPEAT, alarm.repeat);
        values.put(Alarm.Columns.BELL, alarm.bell);
        values.put(Alarm.Columns.VIBRATE, alarm.vibrate);
        values.put(Alarm.Columns.LABEL, alarm.label);
        values.put(Alarm.Columns.ENABLED, alarm.enabled);
        Log.e(TAG, "alarm.repeat: "+alarm.repeat);
        Log.e(TAG, "alarm.bell: "+alarm.bell);
        Log.e(TAG, "alarm.vibrate: "+alarm.vibrate);
        Log.e(TAG, "alarm.label: "+alarm.label);
        Log.e(TAG, "alarm.enabled: "+alarm.enabled);
        return values;
    }
}
