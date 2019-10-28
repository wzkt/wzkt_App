package com.Alan.eva.ui.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.Alan.eva.R;
import com.Alan.eva.tools.alarm.Alarm;
import com.Alan.eva.tools.alarm.AlarmClockManager;
import com.Alan.eva.tools.alarm.AlarmHandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlarmActivity extends Activity implements OnClickListener {
	private Context context;
	private ListView listAlarm;
	private Alarm alarm;
	private List<Alarm> alarms;
	private AlarmAdapter adapter;
	private ImageView imgBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		context = this;
		setContentView(R.layout.activity_alarm);
		//AlarmClockManager.setNextAlarm(context);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		findViewById(R.id.alarmA_lv_addlist).setOnClickListener(this);
		listAlarm = (ListView) findViewById(R.id.alarmA_lv_alarmlist);
		imgBack = (ImageView) findViewById(R.id.alarmA_img_back);
		imgBack.setOnClickListener(this);
		getAlarms(this);
		adapter = new AlarmAdapter();
		listAlarm.setAdapter(adapter);
		TextView system_clock = (TextView) findViewById(R.id.system_clock);
		system_clock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent alarms = new Intent(AlarmClock.ACTION_SET_ALARM);
				startActivity(alarms);

//				listPackages();
//				Log.d("mxt", "paglist的大小：" + pagList.size());
//				for (int i = 0; i < pagList.size(); i++) {
//					Log.d("mxt", pagList.get(i));
//				}
//				PackageManager pm = getPackageManager();
//				Intent i = pm.getLaunchIntentForPackage(pagList.get(0));
//				// 如果该程序不可启动（像系统自带的包，有很多是没有入口的）会返回NULL
//				if (i != null) {
//					AlarmActivity.this.startActivity(i);
//				} else {
//					Intent i2 = new Intent(Settings.ACTION_DATE_SETTINGS);
//					AlarmActivity.this.startActivity(i);
//				}
			}
		});


	}

	Map<String, Object> item;
	private ArrayList<String> pagList;
	class PInfo {
		private String appname = "";
		private String pname = "";
		private String versionName = "";
		private int versionCode = 0;
		private Drawable icon;
		private void prettyPrint() {
			Log.i("taskmanger", appname + "\t" + pname + "\t" + versionName
					+ "\t" + versionCode + "\t");
		}
	}
	private void listPackages() {
		ArrayList<PInfo> apps = getInstalledApps(false);
		final int max = apps.size();
		for (int i = 0; i < max; i++) {
			apps.get(i).prettyPrint();
			item = new HashMap<String, Object>();

			int aa = apps.get(i).pname.length();
			if (aa > 11) {
				if (apps.get(i).pname.contains("clock") ) {
					if (!(apps.get(i).pname.contains("widget"))) {
						try {
							PackageInfo pInfo = getPackageManager()
									.getPackageInfo(apps.get(i).pname, 0);
							if (isSystemApp(pInfo) || isSystemUpdateApp(pInfo)) {
								Log.d("mxt", "是系统自带的");
								Log.d("mxt",					"找到了"
												+ apps.get(i).pname
												.substring(apps.get(i).pname
														.length() - 5)
												+ "  全名：" + apps.get(i).pname
												+ " " + apps.get(i).appname);
								item.put("pname", apps.get(i).pname);
								item.put("appname", apps.get(i).appname);
								pagList.add(apps.get(i).pname);
							}
						} catch (Exception e) {
							// TODO: handle exception
						}

					}
				}
			}

		}
	}

	private ArrayList<PInfo> getInstalledApps(boolean getSysPackages) {
		ArrayList<PInfo> res = new ArrayList<PInfo>();
		List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
		for (int i = 0; i < packs.size(); i++) {
			PackageInfo p = packs.get(i);
			if ((!getSysPackages) && (p.versionName == null)) {
				continue;
			}
			PInfo newInfo = new PInfo();
			newInfo.appname = p.applicationInfo.loadLabel(getPackageManager())
					.toString();
			newInfo.pname = p.packageName;
			newInfo.versionName = p.versionName;
			newInfo.versionCode = p.versionCode;
			newInfo.icon = p.applicationInfo.loadIcon(getPackageManager());
			res.add(newInfo);
		}
		return res;
	}
	public boolean isSystemApp(PackageInfo pInfo) {
		return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
	}
	public boolean isSystemUpdateApp(PackageInfo pInfo) {
		return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (v.getId()) {
		case R.id.alarmA_lv_addlist:

			Intent alarms = new Intent(AlarmClock.ACTION_SET_ALARM);
			startActivity(alarms);

//			intent = new Intent();
//			intent.setClass(this, SetTimeActivity.class);
//			startActivityForResult(intent, 10);
			break;
		case R.id.alarmA_img_back:
			finish();
			break;
		default:
			break;
		}

	}

	private void getAlarms(Context context) {

		alarms = AlarmHandle.getAlarms(context);
	}

	class AlarmAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (alarms != null) {
				return alarms.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return alarms.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Holder holder;
			if (convertView == null) {
				holder = new Holder();
				convertView = LayoutInflater.from(AlarmActivity.this).inflate(R.layout.alarmactivtiy_alarmlist_layout,
						null);
				holder.layoutSwitch = (LinearLayout) convertView.findViewById(R.id.alarmA_layout_switch);
				holder.tv_repeat = (TextView) convertView.findViewById(R.id.alarmA_tv_alarmway);
				holder.tv_time = (TextView) convertView.findViewById(R.id.alarmA_tv_alarmtime);
				holder.cb_switch = (CheckBox) convertView.findViewById(R.id.alarmA_cb_alarmswitch);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			final Alarm alarm = alarms.get(position);
			holder.layoutSwitch.setTag(alarm.id);
			holder.layoutSwitch.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.putExtra("alarm", alarms.get(position));
					intent.setClass(context, SetTimeActivity.class);
					startActivityForResult(intent, 10);
				}
			});
			String hourStr = (alarm.hour + "").length() == 1 ? "0" + alarm.hour : alarm.hour + "";
			String minutesStr = (alarm.minutes + "").length() == 1 ? "0" + alarm.minutes : alarm.minutes + "";
			holder.tv_time.setText(hourStr + ":" + minutesStr);
			holder.tv_repeat.setText(alarm.repeat);
			Log.e("hjs", "alarm.enabled: "+alarm.enabled);
			holder.cb_switch.setChecked(alarm.enabled == 1 ? true : false);
			holder.cb_switch.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ContentValues values = new ContentValues();
					boolean isChecked = true;
					if (((CheckBox) v).isChecked()) {
						isChecked = true;
					} else {
						isChecked = false;
					}
					values.put(Alarm.Columns.ENABLED, isChecked ? 1 : 0);
					AlarmHandle.updateAlarm(context, values, alarm.id,alarm);
					alarms.get(position).enabled = isChecked ? 1 : 0;
					if (isChecked) {
						AlarmClockManager.setAlarm(context, alarm);
					} else {
						AlarmClockManager.cancelAlarm(context, alarm.id);
					}
				}
			});
			return convertView;
		}

		class Holder {
			LinearLayout layoutSwitch;
			TextView tv_time;
			TextView tv_repeat;
			CheckBox cb_switch;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case Alarm.UPDATE_ALARM:
			if (adapter != null) {
				getAlarms(context);
				adapter.notifyDataSetChanged();
			}
			Alarm alarm = (Alarm) data.getSerializableExtra("alarm");
			if (alarm != null) {
				AlarmClockManager.setAlarm(context, alarm);
			}
			break;
		case Alarm.DELETE_ALARM:
			if (adapter != null) {
				getAlarms(context);
				adapter.notifyDataSetChanged();
			}
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (adapter != null) {
			getAlarms(context);
			adapter.notifyDataSetChanged();
		}
	}
}
