package com.Alan.eva.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.Alan.eva.R;
import com.Alan.eva.service.UpdateService;
import com.Alan.eva.tools.PathUtils;
import com.Alan.eva.tools.PixelUtils;
import com.Alan.eva.tools.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.HttpException;
import org.xutils.http.HttpMethod;


public class FifthFragment extends FragmentActivity implements OnClickListener {
	private static final String TAG = "FifthFragment";
	private ImageView img_back;
	private RelativeLayout warnSwitch, suggest, layoutUpdate, layoutStemp, layoutSkick;
	private LinearLayout layoutVir, layoutBell, layoutCancel;
	private PopupWindow tempPopu;
	private PopupWindow wayPopu;
	private CheckBox cbTemp, cbKick;
	private LinearLayout layoutLogout;
	boolean isChecked = true;
	private String appName;// app版本号名称
	private String loadPath;// app下载地址
	private String des;// 更新内容描述
	private int who = 0;// 0表示选择温度1表示蹬被
	private String kickIsVibration = "kickIsVibration";// 开启蹬被预警震动的Key
	private String TempIsVibration = "TempIsVibration";
	private String kickBellName = "kickBellName";// 蹬被的铃声名的key
	private String kickBellPath = "kickBellPath";// 蹬被的铃声路径的key
	private String bellNameKey = "bellNameKey";// 高温的铃声名的Key
	private String bellPathKey = "TempBellPathKey";
	private String BellPath = "BellPath";
//	private Handler mHandler = new Handler() {
//	}

	CheckBox supertem,lowpower;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_fragment);
		img_back = (ImageView)findViewById(R.id.setting_img_back);
		warnSwitch = (RelativeLayout) findViewById(R.id.setF_layout_warnswitch);
		suggest = (RelativeLayout) findViewById(R.id.setF_layout_suggest);
		layoutLogout = (LinearLayout) findViewById(R.id.setF_layout_logout);
		layoutUpdate = (RelativeLayout) findViewById(R.id.setF_layout_update);
		layoutStemp = (RelativeLayout) findViewById(R.id.setF_layout_selectTemp);
		layoutSkick = (RelativeLayout) findViewById(R.id.setF_layout_selectkick);
		img_back.setOnClickListener(this);
		layoutLogout.setOnClickListener(this);
		warnSwitch.setOnClickListener(this);
		suggest.setOnClickListener(this);
		layoutUpdate.setOnClickListener(this);
		layoutStemp.setOnClickListener(this);
		layoutSkick.setOnClickListener(this);
		cbTemp = (CheckBox) findViewById(R.id.setF_cb_tempswitch);
		cbKick = (CheckBox) findViewById(R.id.setF_cb_kickswitch);
		cbTemp.setOnClickListener(this);
		cbKick.setOnClickListener(this);

		cbTemp.setChecked(getTempIsSwitchswtich());
		cbKick.setChecked(getkickalarmswtich());

		supertem = (CheckBox) findViewById(R.id.setF_super_tempswitch);
		lowpower = (CheckBox) findViewById(R.id.setF_lowpower_tempswitch);
//		supertem.setOnClickListener(this);
		lowpower.setOnClickListener(this);

		supertem.setChecked(true);
		supertem.setClickable(false);
		lowpower.setChecked(getlowerTempIsSwitchswtich());

//		layoutVir = (LinearLayout) findViewById(R.id.fifthF_layout_vir);
//		layoutBell = (LinearLayout) findViewById(R.id.fifthF_layout_bell);
//		layoutCancel = (LinearLayout) findViewById(R.id.fifthF_layout_cancel);
//		layoutVir.setOnClickListener(this);
//		layoutBell.setOnClickListener(this);
//		layoutCancel.setOnClickListener(this);
	}

	private boolean getSuperTempIsSwitchswtich(){
		boolean tem  =false;
		try{
			tem =(boolean)SPUtils.get(this, "Supertem_Switch", true);
			Log.e("hjs","Supertem_Switch=="+tem);
		}catch (Exception e){
			tem =false;
		}
		return tem;
	}
	private boolean getlowerTempIsSwitchswtich(){
		boolean tem  =false;
		try{
			tem =(boolean)SPUtils.get(this, "lowpower_Switch", false);
			Log.e("hjs","lowpower_Switch=="+tem);
		}catch (Exception e){
			tem =false;
		}
		return tem;
	}


	private boolean getTempIsSwitchswtich(){
		boolean tem  =false;
		try{
			tem =(boolean)SPUtils.get(this, "TempIsSwitch", false);
			Log.e("hjs","getTempIsSwitchswtich=="+tem);
		}catch (Exception e){
			tem =false;
		}
		return tem;
	}

	private boolean getkickalarmswtich(){
		boolean tem  =false;
		try{

			SharedPreferences pres = this.getSharedPreferences("kickalarm", Context.MODE_PRIVATE);
			tem =  pres.getBoolean("kickIsSwitch", false);
			Log.e("hjs","kickIsSwitch=="+tem);

		}catch (Exception e){
			tem =false;
		}
		return tem;
	}

//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		View view = inflater.inflate(R.layout.setting_fragment, container, false);
//
//		img_back = (ImageView) view.findViewById(R.id.setting_img_back);
//		warnSwitch = (RelativeLayout) view.findViewById(R.id.setF_layout_warnswitch);
//		suggest = (RelativeLayout) view.findViewById(R.id.setF_layout_suggest);
//		layoutLogout = (LinearLayout) view.findViewById(R.id.setF_layout_logout);
//		layoutUpdate = (RelativeLayout) view.findViewById(R.id.setF_layout_update);
//		layoutStemp = (RelativeLayout) view.findViewById(R.id.setF_layout_selectTemp);
//		layoutSkick = (RelativeLayout) view.findViewById(R.id.setF_layout_selectkick);
//		layoutLogout.setOnClickListener(this);
//		warnSwitch.setOnClickListener(this);
//		suggest.setOnClickListener(this);
//		layoutUpdate.setOnClickListener(this);
//		layoutStemp.setOnClickListener(this);
//		layoutSkick.setOnClickListener(this);
//
//		return view;
//	}


	@Override
	protected void onResume() {
		super.onResume();

	new Thread(new Runnable() {
	@Override
	public void run() {
		try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				runOnUiThread(new Runnable() {
			@Override
			public void run() {

				startBellPopuwindow();
			}
		});
	}
}).start();




	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.setting_img_back:
				this.finish();
				break;
		case R.id.setF_layout_warnswitch:
			//startSwitchPopuwindow();
			break;
		case R.id.setF_layout_suggest:
			Intent intent = new Intent();
			intent.setClass(this, SuggestActivity.class);
			startActivity(intent);
			break;
		case R.id.setF_cb_tempswitch:
			if (cbTemp.isChecked()) {
				SPUtils.put(this, "TempIsSwitch", true);
			} else {
				SPUtils.put(this, "TempIsSwitch", false);
			}
			break;
		case R.id.setF_cb_kickswitch:
			SharedPreferences pres = this.getSharedPreferences("kickalarm", Context.MODE_PRIVATE);
			if (cbKick.isChecked()) {
				SharedPreferences.Editor editor = pres.edit();
				editor.putBoolean("kickIsSwitch", true);
				editor.commit();
			} else {
				SharedPreferences.Editor editor = pres.edit();
				editor.putBoolean("kickIsSwitch", false);
				editor.commit();
			}
			break;
			case R.id.setF_super_tempswitch:
				if (supertem.isChecked()) {
					SPUtils.put(this, "Supertem_Switch", true);
				} else {
					SPUtils.put(this, "Supertem_Switch", false);
				}
			break;
			case R.id.setF_lowpower_tempswitch:
				if (lowpower.isChecked()) {
					SPUtils.put(this, "lowpower_Switch", true);
				} else {
					SPUtils.put(this, "lowpower_Switch", false);
				}
			break;
		case R.id.setF_layout_logout:
			//SPUtils.put(this, Keys.ISLOGIN, false);
			Intent intentLogin = new Intent();
			intentLogin.setClass(this, LoginActivity.class);
			startActivity(intentLogin);
			this.finish();
			break;
		case R.id.setF_layout_update:
			checkVersion();
			break;
		case R.id.setF_layout_selectTemp:
			who = 0;
			//startBellPopuwindow();
			break;
		case R.id.setF_layout_selectkick:
			who = 1;
			//startBellPopuwindow();
			break;
			case R.id.fifthF_layout_vir:
				if (who == 0) {
					SPUtils.put(this, kickIsVibration, true);
					if(wayPopu.isShowing())wayPopu.dismiss();

				} else if (who == 1) {
					SPUtils.put(this, TempIsVibration, true);
					if(wayPopu.isShowing())wayPopu.dismiss();
				}
				break;
			case R.id.fifthF_layout_bell:

				if (who == 1) {
					SPUtils.put(this, kickIsVibration, false);
					Intent bellIntent = new Intent(this, TempBellActivity.class);
					startActivityForResult(bellIntent, 100);
					if(wayPopu.isShowing())wayPopu.dismiss();
				} else if (who == 0) {
					SPUtils.put(this, TempIsVibration, false);
					Intent bellIntent = new Intent(this, TempBellActivity.class);
					startActivityForResult(bellIntent, 200);
					if(wayPopu.isShowing())wayPopu.dismiss();
				}
				break;
			case R.id.fifthF_layout_cancel:
				if(wayPopu.isShowing())wayPopu.dismiss();
				break;
		default:
			break;
		}
	}

	private void checkVersion() {
//		// TODO Auto-generated method stub
//		// 网络访问得到版本号，下载地址
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//
//				x.http().get()
//				mUtils.send(HttpMethod.GET, PathUtils.update(VersionUtils.getVerCode(getActivity())),
//						new RequestCallBack<String>() {
//
//							@Override
//							public void onFailure(HttpException arg0, String arg1) {
//								// TODO Auto-generated method stub
//								Log.e(TAG, "下载失败");
//							}
//
//							@Override
//							public void onSuccess(ResponseInfo<String> arg0) {
//								// TODO Auto-generated method stub
//
//								String result = arg0.result;
//								Log.e(TAG, result.toString());
//								try {
//									JSONObject object = new JSONObject(result);
//									int code = object.getInt("code");
//									Log.e(TAG, "" + code);
//									if (code == 100) {
//										JSONObject objContent = object.getJSONObject("data");
//										appName = objContent.getString("name");
//										loadPath = objContent.getString("path");
//										des = objContent.getString("des");
//										mHandler.postDelayed(new Runnable() {
//
//											@Override
//											public void run() {
//												// TODO Auto-generated method stub
//												createDialog(appName, loadPath, des);
//											}
//										}, 3000);
//
//									} else {
//										Toast.makeText(getApplicationContext(), "已是最新版本", Toast.LENGTH_SHORT).show();
//									}
//								} catch (JSONException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//
//							}
//						});
//			}
//		}).start();
//
	}

	public void createDialog(final String appName, final String loadPath, String des) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("软件升级").setMessage("发现新版本,建议立即更新使用.")
				.setPositiveButton("更新", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getApplicationContext(), com.Alan.eva.service.UpdateService.class);
						Bundle bundle = new Bundle();
						bundle.putString("app_version", appName);
						bundle.putString("loadPath", loadPath);
						intent.putExtras(bundle);
						startService(intent);
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}

		});
		alert.create().show();
	}

	public void startSwitchPopuwindow() {
		View view = LayoutInflater.from(this).inflate(R.layout.set_warnswitch_popwindow, null);
		cbTemp = (CheckBox) view.findViewById(R.id.setF_cb_tempswitch);
		cbKick = (CheckBox) view.findViewById(R.id.setF_cb_kickswitch);
		cbTemp.setOnClickListener(this);
		cbKick.setOnClickListener(this);
		if ((boolean) SPUtils.get(this, "TempIsSwitch", false)) {
			cbTemp.setChecked(true);
		} else {
			cbTemp.setChecked(false);
		}
		SharedPreferences pres = this.getSharedPreferences("kickalarm", Context.MODE_PRIVATE);
		if (pres.getBoolean("kickIsSwitch", false)) {
			cbKick.setChecked(true);
		} else {
			cbKick.setChecked(false);
		}
		int width = this.getWindowManager().getDefaultDisplay().getWidth();
		int height = this.getWindowManager().getDefaultDisplay().getHeight();
		Rect frame = new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		int contentTop = this.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
		int titleBarHeight = contentTop - statusBarHeight;
		int popWindowHeight = height / 3;
		tempPopu = new PopupWindow(view, width, PixelUtils.dipToPx(this, 102), true);
		// 设置弹窗出来的动画,选择一个渐变的动画
		tempPopu.setAnimationStyle(android.R.style.Animation_Translucent);
		// 设置一个弹窗的背景，控制弹窗消失时不许有这个属性
		tempPopu.setBackgroundDrawable(new ColorDrawable());
		View parent = this.getWindow().getDecorView();
		// 第一个参数父容器，第二参数显示的位置，第3,4参数是坐标点x便宜,y偏移
		Log.e("_______", titleBarHeight + "");
		int a = PixelUtils.dipToPx(this, 100);
		// int a = dip2px(this, 100);
		tempPopu.showAtLocation(parent, Gravity.TOP, 0, Math.abs(titleBarHeight) + a);
	}

	public void startBellPopuwindow() {
		View view = LayoutInflater.from(this).inflate(R.layout.fifthf_seclectbell, null);
		layoutVir = (LinearLayout) view.findViewById(R.id.fifthF_layout_vir);
		layoutBell = (LinearLayout) view.findViewById(R.id.fifthF_layout_bell);
		layoutCancel = (LinearLayout) view.findViewById(R.id.fifthF_layout_cancel);
		layoutVir.setOnClickListener(this);
		layoutBell.setOnClickListener(this);
		layoutCancel.setOnClickListener(this);

		int width = this.getWindowManager().getDefaultDisplay().getWidth();
		int height = this.getWindowManager().getDefaultDisplay().getHeight();
		wayPopu = new PopupWindow(view, width, PixelUtils.dipToPx(this, 131), true);
		wayPopu.setAnimationStyle(android.R.style.Animation_InputMethod);
		// 设置一个弹窗的背景，控制弹窗消失时不许有这个属性
		wayPopu.setBackgroundDrawable(new ColorDrawable());
		View parent = this.getWindow().getDecorView();
		wayPopu.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100) {
			if (resultCode == 200) {
				String name = data.getStringExtra("name");
				String path = data.getStringExtra("path");
				SPUtils.put(this, kickBellName, name);
				SPUtils.put(this, kickBellPath, path);
			}

		} else if (requestCode == 200) {
			if (resultCode == 200) {
				String name = data.getStringExtra("name");
				String path = data.getStringExtra("path");
				if((name !=null)&&(path!=null)){
					SPUtils.put(this, bellNameKey, name);

				SPUtils.put(this, bellPathKey, path);
				}
			}

		}

	}

}
