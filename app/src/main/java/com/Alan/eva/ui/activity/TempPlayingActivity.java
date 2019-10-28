package com.Alan.eva.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.Alan.eva.R;
import com.Alan.eva.tools.SPUtils;

public class TempPlayingActivity extends Activity {
	private static final String TAG = "TempPlayingActivity";
	private Context context;
	public Vibrator mVibrator;
	private MediaPlayer mMediaPlayer;
	private boolean isRing = false;

	public static boolean isRingremue = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_temp_playing);
		context = this;
		// 初始化振动器
		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		if ((boolean) SPUtils.get(getApplicationContext(), "TempIsVibration", false)) {
			mVibrator.vibrate(new long[] { 500, 500 }, 0);
		} else {
			play();
		}

	}

	public void play() {
		// 警报以后，将isring改为true
		SPUtils.put(this, "isRing", true);
		// 播放前先停止ֹ
		stop();
		try {
			String path = (String) SPUtils.get(getApplicationContext(), "TempBellPathKey", "null");
			Log.e(TAG, path);
			if (path.equals("null")) {
				mMediaPlayer = MediaPlayer.create(context, R.raw.alarm);
				Log.e(TAG, "瞩目鸟声初始化成功");
			} else {
				mMediaPlayer = MediaPlayer.create(context, Uri.parse(path));
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

	public void clickButton(View v) {
		switch (v.getId()) {
		case R.id.tempPlay_btn_stop:
			stop();
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		isRingremue = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		isRingremue =false;
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isRingremue =false;
		stop();
	}
}
