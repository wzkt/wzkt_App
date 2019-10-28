package com.Alan.eva.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.Alan.eva.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TempBellActivity extends Activity implements OnClickListener {
	private ListView seclectBellList;
	private ImageView back;
	private TextView save;
	private Context context;
	private List<Map<String, Object>> bells;
	private MediaPlayer player;
	private MusicAdapter adapter;
	private Map<String, Boolean> RBStates;
	private String name;
	private String path;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_temp_bell);
		context = this;
		initView();
		initList();
		adapter = new MusicAdapter(bells);
		resetPlayer();
		if (adapter != null && RBStates != null) {
			RBStates.clear();
			adapter.notifyDataSetChanged();
			seclectBellList.setAdapter(adapter);
		}

	}

	private void initList() {
		// TODO Auto-generated method stub
		// 系统铃声查询
		Map<String, Object> mapOne = new HashMap<String, Object>();
		mapOne.put("name", "default");
		mapOne.put("path", "null");
		bells.add(mapOne);
		Cursor bellcCursor = getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, null, null, null,
				null);
		if (bellcCursor != null) {
			while (bellcCursor.moveToNext()) {
				Map<String, Object> map = new HashMap<String, Object>();
				// 名字
				String bellName = bellcCursor
						.getString(bellcCursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
				// 路径
				String bellPath = bellcCursor.getString(bellcCursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
				map.put("name", bellName);
				map.put("path", bellPath);
				bells.add(map);
			}
			bellcCursor.close();
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		seclectBellList = (ListView) findViewById(R.id.tempBellList);
		back = (ImageView) findViewById(R.id.tempB_img_back);
		save = (TextView) findViewById(R.id.tempB_tv_save);
		bells = new ArrayList<Map<String, Object>>();
		RBStates = new HashMap<String, Boolean>();
		back.setOnClickListener(this);
		save.setOnClickListener(this);
	}

	private void resetPlayer() {
		if (player != null) {
			player.stop();
			player.release();
			player = null;
		}

	}

	// 播放选择的铃声
	private void playMusic(String path) {
		resetPlayer();
		player = MediaPlayer.create(context, Uri.parse(path));
		// 只播放一次
		player.setLooping(false);
		player.start();
	}

	// 播放本地铃声
	private void playLocalMusic() {
		resetPlayer();
		player = MediaPlayer.create(context, R.raw.alarm);
		// 只播放一次
		player.setLooping(false);
		player.start();
	}

	class MusicAdapter extends BaseAdapter {
		private List<Map<String, Object>> lists;

		MusicAdapter(List<Map<String, Object>> lists) {
			this.lists = lists;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lists.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return lists.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Holder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.bell_item, null);
				holder = new Holder();
				holder.tv_music = (TextView) convertView.findViewById(R.id.tv_music);
				holder.rb_check = (RadioButton) convertView.findViewById(R.id.rb_check);
				holder.ll_item = (LinearLayout) convertView.findViewById(R.id.ll_item);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			holder.tv_music.setText(lists.get(position).get("name").toString());
			holder.ll_item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					for (String key : RBStates.keySet()) {
						RBStates.put(key, false);
					}
					RBStates.put(String.valueOf(position), true);
					notifyDataSetChanged();
					if (position == 0) {
						playLocalMusic();
						name = "default";
						path = "null";
					} else {
						playMusic(lists.get(position).get("path").toString());
						name = lists.get(position).get("name").toString();
						path = lists.get(position).get("path").toString();
					}

				}
			});
			boolean res = false;
			if (RBStates.get(String.valueOf(position)) == null || RBStates.get(String.valueOf(position)) == false) {
				res = false;
			} else {
				res = true;
			}
			holder.rb_check.setChecked(res);
			return convertView;
		}

		class Holder {
			LinearLayout ll_item;
			TextView tv_music;
			RadioButton rb_check;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tempB_img_back:
			finish();
			break;
		case R.id.tempB_tv_save:
			Intent intent = new Intent();
			intent.putExtra("name", name);
			intent.putExtra("path", path);
			setResult(200, intent);
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		resetPlayer();
	}
}
