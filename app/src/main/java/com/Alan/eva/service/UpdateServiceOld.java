//package com.Alan.eva.service;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.util.Log;
//import android.widget.RemoteViews;
//import android.widget.Toast;
//
//import com.Alan.eva.R;
//
//import org.xutils.ex.HttpException;
//import org.xutils.x;
//
//import java.io.File;
//import java.io.IOException;
//
//public class UpdateService extends Service {
//	private static final String TAG = "UpdateService";
//	/* 下载包安装路径 */
//	public String savePath = "EVE安装包";
//	// public String saveFileName = savePath + "EVE_APP";
//	// 文件存储
//	private File updateDir = null;
//	private File updateFile = null;
//	// 下载状态
//	private final static int DOWNLOAD_COMPLETE = 0;
//	private final static int DOWNLOAD_FAIL = 1;
//	// 通知栏
//	private Notification notification;
//	private NotificationManager updateNotificationManager = null;
//	// 通知栏跳转Intent
//	private Intent updateIntent = null;
//	private PendingIntent updatePendingIntent = null;
//	private String app_name;// app名称
//	private String app_version;// app版本号
//	private String loadPath;// app下载地址
//
//	/***
//	 * 创建通知栏
//	 */
//	RemoteViews contentView;
//	int down_step = 5;// 提示step
//	int downloadCount = 0;// 实时下载的文件大小
//
//	@Override
//	public IBinder onBind(Intent intent) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	// 判断sdk是否可用
//	private boolean hasSdcard() {
//		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		// TODO Auto-generated method stub
//		app_name = getResources().getString(R.string.app_name);
//		Bundle bundle = intent.getExtras();
//		app_version = bundle.getString("app_version");
//		loadPath = bundle.getString("loadPath");
////		createNotification();
//		// 创建文件
//		// MEDIA_MOUNTED 当外存储是可以读，也可以写的时候
//		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//			updateDir = new File(Environment.getExternalStorageDirectory(), savePath);
//			Log.e(TAG, updateDir.getPath());
//			updateFile = new File(updateDir.getPath(), app_version);
//		} else {
//			Toast.makeText(this, "存储卡不可用", Toast.LENGTH_SHORT).show();
//		}
//
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//
//				if (!updateDir.exists()) {
//					updateDir.mkdirs();
//				}
//				if (!updateFile.exists()) {
//					try {
//						updateFile.createNewFile();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				downloadUpdateFile(loadPath
//						// VersionUtils.getVerCode(getApplicationContext()))
//						, updateFile.toString());
////				Log.e(TAG, loadPath);
//			}
//		}).start();
//
//		return super.onStartCommand(intent, flags, startId);
//	}
//
//	private Handler updateHandler = new Handler() {
//		public void handleMessage(android.os.Message msg) {
//			switch (msg.what) {
//			case DOWNLOAD_COMPLETE:
//				Uri uri = Uri.fromFile(updateFile);
//				Intent intent = new Intent(Intent.ACTION_VIEW);
//				intent.setDataAndType(uri, "application/vnd.android.package-archive");
//				updatePendingIntent = PendingIntent.getActivity(UpdateService.this, 0, intent, 0);
//				//notification(UpdateService.this, app_name, "下载成功，点击安装", updatePendingIntent);
//				updateNotificationManager.notify(0, notification);
//				stopSelf();
//				break;
//			case DOWNLOAD_FAIL:
//				//notification.setLatestEventInfo(UpdateService.this, app_name, "下载失败", updatePendingIntent);
//				break;
//
//			default:
//				stopSelf();
//				break;
//			}
//
//		};
//	};
//
////	private void createNotification() {
////		// TODO Auto-generated method stub
////		Log.e(TAG, "提醒开始");
////		updateNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
////		notification = new Notification();
////		notification.icon = R.drawable.icon;
////		notification.tickerText = "开始下载";
////		contentView = new RemoteViews(getPackageName(), R.layout.update_item);
////		contentView.setTextViewText(R.id.update_item_textTitle, "正在下载");
////		contentView.setTextViewText(R.id.update_item_textContent, "0%");
////		contentView.setProgressBar(R.id.update_item_progressbar, 100, 0, false);
////		notification.contentView = contentView;
////		updateIntent = new Intent(this, MainActivity.class);
////		updateIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
////		updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
////		notification.contentIntent = updatePendingIntent;
////		updateNotificationManager.notify(0, notification);
////
////	}
////
//	public void downloadUpdateFile(String downloadUrl, String saveFile) {
//		final Message message = updateHandler.obtainMessage();
//
//		x.http().download(downloadUrl, saveFile, true, true, new RequestCallBack<File>() {
//			@Override
//			public void onStart() {
//				// TODO Auto-generated method stub
//				super.onStart();
//			}
//
//			@Override
//			public void onFailure(HttpException arg0, String arg1) {
//				// TODO Auto-generated method stub
//				message.what = DOWNLOAD_FAIL;
//				updateHandler.sendMessage(message);
//			}
//
//			@Override
//			public void onSuccess(ResponseInfo<File> arg0) {
//				// TODO Auto-generated method stub
//				message.what = DOWNLOAD_COMPLETE;
//				updateHandler.sendMessage(message);
//			}
//
//			@Override
//			public void onLoading(long total, long current, boolean isUploading) {
//				// TODO Auto-generated method stub
//				super.onLoading(total, current, isUploading);
//				/*
//				 * if (downloadCount == 0 || (current * 100 / total - down_step)
//				 * >= downloadCount) { downloadCount += down_step;
//				 * contentView.setTextViewText(R.id.update_item_textContent,
//				 * downloadCount + "%");
//				 * contentView.setProgressBar(R.id.update_item_progressbar, 100,
//				 * downloadCount, false); updateNotificationManager.notify(0,
//				 * notification); }
//				 */
//				int a = ((int) (current * 100 / total));
//				contentView.setTextViewText(R.id.update_item_textContent, a + "%");
//				contentView.setProgressBar(R.id.update_item_progressbar, 100, a, false);
//				updateNotificationManager.notify(0, notification);
//			}
//
//		});
//
//	}
//
//}
