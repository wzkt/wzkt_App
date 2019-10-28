package com.Alan.eva.tools.alarm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.io.IOException;

public class AlarmService extends Service {
	public Vibrator mVibrator;
	private MediaPlayer mMediaPlayer;
	private Alarm mCurrentAlarm;
	private TelephonyManager mTelephonyManager;

	private IBinder binder = new MyBinder();

	public IBinder onBind(Intent intent) {
		if (intent != null) {

			mCurrentAlarm = (Alarm) intent.getSerializableExtra("alarm");
			if (mCurrentAlarm != null) {
				AudioManager volMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				// 获取系统设置的铃声模式
				switch (volMgr.getRingerMode()) {
				// 静音模式，值为0，震动
				case AudioManager.RINGER_MODE_SILENT:
					openVibrator();
					break;
				case AudioManager.RINGER_MODE_VIBRATE:
					// 系统是震动模式 ，只震动 不响铃
					openVibrator();
					break;
				case AudioManager.RINGER_MODE_NORMAL:
					// 系统是正常模式
					play(mCurrentAlarm);
					break;
				}
			} else {
				stopSelf();
			}
		}
		return binder;
	}

	// 开启震动
	private void openVibrator() {
		if (mCurrentAlarm != null && mVibrator != null && mCurrentAlarm.vibrate == 1) {
			mVibrator.vibrate(new long[] { 500, 500 }, 0);
		}
	}

	private void play(Alarm alarm) {
		// 播放前先停止ֹ
		stop();
		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
			//mMediaPlayer.setDataSource(alarm.bell);
			mMediaPlayer.prepare();
			mMediaPlayer.setLooping(true);
			mMediaPlayer.start();
			// 震动
			openVibrator();
		} catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
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

	@Override
	public void onCreate() {
		super.onCreate();
		// 初始化震动器
		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		// 监听来电
		mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
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

	public void onDestroy() {
		super.onDestroy();
		stop();
		mTelephonyManager.listen(mPhoneStateListener, 0);
	}

	private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String ignored) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:// 无任何状态״̬
				System.out.println("没电话");
				if (mCurrentAlarm != null && mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
					play(mCurrentAlarm);
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:// 接起电话时
				System.out.println("接电话");
				stop();
				break;
			case TelephonyManager.CALL_STATE_RINGING:// 电话进来时
				System.out.println("电话" + ignored);
				stop();
				break;
			}
		}
	};

	public class MyBinder extends Binder {
		public AlarmService getService() {
			return AlarmService.this;
		}
	}
}
