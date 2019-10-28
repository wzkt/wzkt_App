package com.Alan.eva.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;

import com.Alan.eva.R;
import com.Alan.eva.tools.SPUtils;

public class KickPlayingActivity extends Activity {
	public Vibrator mVibrator;
	private MediaPlayer mMediaPlayer = null;
	private SharedPreferences pre_VibrationSwitch;
	private SharedPreferences pre_BellPath;
	private String kickBellPath = "kickBellPath";// 蹬被的铃声路径的key
	private String kickIsVibration = "kickIsVibration";// 震动的key

	public static boolean kickremue= false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_kick_playing);
		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		if ((boolean) SPUtils.get(this, kickIsVibration, false)) {
			mVibrator.vibrate(new long[] { 500, 500 }, 0);
		} else {
			play();
		}

	}

	public void play() {
		SPUtils.put(this, "dengbei", true);
		// 播放前先停止ֹ
		stop();

		try {
			String path = (String) SPUtils.get(this, kickBellPath, "null");
			if (path.equals("null")) {
				mMediaPlayer = MediaPlayer.create(this, R.raw.alarm);
			} else {
				mMediaPlayer = MediaPlayer.create(this, Uri.parse(path));
			}

			mMediaPlayer.setLooping(true);
			mMediaPlayer.start();

		} catch (IllegalArgumentException | SecurityException | IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mMediaPlayer.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				mp.stop();
				mp.reset();
				mp.release();
				mMediaPlayer = null;
				return true;
			}
		});
	}

	// 停止播放
	public void stop() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}

		// 停止震动
		mVibrator.cancel();
	}

	@Override
	protected void onResume() {
		super.onResume();
		kickremue =true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		kickremue =false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stop();
	}

	public void clickButton(View v) {
		switch (v.getId()) {
		case R.id.kickPlay_btn_cancel:
			stop();
			finish();
			break;

		default:
			break;
		}
	}
}
