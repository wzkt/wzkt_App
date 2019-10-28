package com.Alan.eva.tools.alarm;

import com.Alan.eva.ui.activity.AlarmDealActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	public  static  boolean flag  = false;
	@Override
	public void onReceive(Context context, Intent intent) {

		Log.e("hjs", "flag: "+flag);

		if(flag){
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				flag  =true;
				try {
					Thread.sleep(1000*60*2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				flag  =false;
			}
		}).start();

		long realtime = intent.getLongExtra("realtime", 0);
		int id  =intent.getIntExtra("id",0);
		Log.e("hjs", "id: "+id);
		Log.e("hjs", "realtime: "+realtime);
		if(realtime==0){
			Log.e("hjs", "realtime time return");
			return;
		}

		long currtime = System.currentTimeMillis();
		Log.e("hjs", "systime: "+currtime);
		Log.e("hjs", "(--): "+(realtime -currtime) );

		long subtime=((currtime-realtime)&0xff);
		if(Math.abs(subtime)>(120*1000)){
			Log.e("hjs", "erro time return");
			return;
		}


		Log.e("hjs", "AlarmReceiveronReceive: ");
		if((intent==null)|| (intent.getAction()==null)){
			Intent deal = new Intent(context, AlarmDealActivity.class);
			Log.e("hjs", "id== "+id);
			deal.putExtra(Alarm.Columns._ID, intent.getIntExtra(Alarm.Columns._ID, id));
			deal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(deal);
			return;
		}
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Log.e("hjs", "ACTION_BOOT_COMPLETED");
			// Intent service = new Intent(context, AlarmService.class);
			// context.startService(service);
			AlarmClockManager.setNextAlarm(context);
		} else {
			Log.e("hjs", "AlarmDealActivity:id== "+id);
			Intent deal = new Intent(context, AlarmDealActivity.class);
			deal.putExtra(Alarm.Columns._ID, intent.getIntExtra(Alarm.Columns._ID, id));
			deal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(deal);
		}
	}
}
