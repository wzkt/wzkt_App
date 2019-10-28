package com.Alan.eva.tools.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.Alan.eva.R;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.ui.activity.HomeActivity;

import java.util.Date;
import java.util.Timer;

public class AlarmNotificationManager {

    private static NotificationManager notificationManager;

    /*
     * 显示状态栏通知图标
     */
    static void showNotification(Context context, Alarm alarm) {

        Intent intent = new Intent(context, HomeActivity.class);
        //点击通知进入app不会重新开启一个新的activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        String hourStr = (alarm.hour + "").length() == 1 ? "0" + alarm.hour : alarm.hour + "";
        String minutesStr = (alarm.minutes + "").length() == 1 ? "0" + alarm.minutes : alarm.minutes + "";
        String str = hourStr + ":" + minutesStr + "\t" + alarm.label + "\t" + alarm.repeat;
        Notification notification = new Notification.Builder(context)
                .setContentTitle("吃药提醒")
                .setContentText(str)
                .setSmallIcon(R.mipmap.icon)
                .setSmallIcon(R.mipmap.icon)
                .setContentIntent(pi)
                .build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    /*
     * 显示状态栏通知图标
     */
   public static void showHighTempNotification(Context context, String  wendu) {
        Notification notification = new Notification.Builder(context)
                .setContentTitle("高温提醒:"+wendu)
                .setContentText("高温时间:"+Tools.getRealtime())
                .setSmallIcon(R.mipmap.icon)
                .setSmallIcon(R.mipmap.icon)
                .build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }


    /*
 * 显示状态栏通知图标
 */
    public static void showKickTempNotification(Context context, String  wendu) {
        Notification notification = new Notification.Builder(context)
                .setContentTitle("踢被提醒:"+wendu)
                .setContentText("低温时间:"+Tools.getRealtime())
                .setSmallIcon(R.mipmap.icon)
                .setSmallIcon(R.mipmap.icon)
                .build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }


    /*
     * 取消状态栏通知图标
     */
    public static void cancelNotification() {
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

}
