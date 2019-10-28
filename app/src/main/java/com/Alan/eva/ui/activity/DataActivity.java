package com.Alan.eva.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.Alan.eva.R;
import com.Alan.eva.http.core.IResultHandler;
import com.Alan.eva.http.post.QueryDataListPost;
import com.Alan.eva.model.Axis;
import com.Alan.eva.model.ScheduleDataTag;
import com.Alan.eva.model.UserInfo;
import com.Alan.eva.result.QueryDataRes;
import com.Alan.eva.service.DateUtil;
import com.Alan.eva.tools.LogUtil;
import com.Alan.eva.tools.NetWork;
import com.Alan.eva.tools.PathUtils;
import com.Alan.eva.tools.SPUtils;
import com.Alan.eva.tools.ScheduleDAO;
import com.Alan.eva.ui.EApp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DataActivity extends Activity implements IResultHandler {
	private static final String TAG = DataActivity.class.getSimpleName();
	private String uid;
	private Calendar calendar;
	private LineChartView chartView;
	private ImageView img_back, img_update;
	private RadioGroup rg_date;
	private RadioButton rbt_date, rbt_year;
	private LinearLayout linechart;
	private String[] XForLine1 = new String[240];
	private float[] data1 = new float[240];
	private String[] XMoth = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" };
	private float[] data2 = new float[12];

	private String[] XMoth31 = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18",
			"19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", ""};
	//private float[] data231 = new float[31];

	private String childPhone = null;// 用户登录手机号
	private int year;
	private int month;
	private int day;

	private Axis yAxis;
	private ProgressDialog mProgress = null;
	private  MyHandler tempHandler = new MyHandler(this);
	private TempTimeHandler tempTimeHandler = new TempTimeHandler(this);

	private MonTempTimeHandler monthHand = new MonTempTimeHandler(this);

	String mac;
	String name;
	private final int QUERY_HIS = 0x0010;
	private void refresh(String uid,String mac,String date,String opetemp) {
		QueryDataListPost post = new QueryDataListPost();
		post.code(QUERY_HIS);
		post.handler(this);
		post.setThermometerID(mac);
		post.setPid(uid);
		post.setQuery_date(date);
		post.setOpt_temp(opetemp);
		post.setUsername(name);
		Log.e(TAG, "QueryDataListPost: "+post.toString());
		post.post();
	}
	@Override
	public void handleStart(int code) {

	}



	@Override
	public void handleResult(String result, int code) {
		Log.e("hjs", "handleResult: "+result);

		Gson gson = new Gson();
		String result2= result.replaceAll(" ","");

		ArrayList<QueryDataRes> listdata = new ArrayList<QueryDataRes>();
		Type listType = new TypeToken<List<QueryDataRes>>() {}.getType();
		listdata = gson.fromJson(result2, listType);

		//DataListRes res = Tools.json2Bean(result, DataListRes.class);
		//ArrayList<QueryDataRes> listdata=null;
			//ArrayList<QueryDataRes> listdata=   res.getHistorical_data();
			LogUtil.info("listdata:" + listdata.size());
			if(listdata!=null){
				data2 = new float[listdata.size()];

						if(DataQuerytemp.equalsIgnoreCase("year")){
							for(int i =0;i<listdata.size();i++) {
								try {
									data2[i] = Float.valueOf(listdata.get(i).getCounts());                            //data1[i]+=i;
									if(data2[i]<=0){
										data2[i] = 0;
									}

									String Time = listdata.get(i).getMonth();
									Log.e("hjs", "year: " + Time);
									data1[i] = Float.valueOf(Time);
								} catch (Exception e) {
								}
							}
						}else if (DataQuerytemp.equalsIgnoreCase("month")){
								for(int i =0;i<listdata.size();i++) {
									try {
										data2[i] = Float.valueOf(listdata.get(i).getCounts());

										if(data2[i]==-1){
											data2[i] = 0;
										}else if(data2[i]==0){
											data2[i] = 10;
										}else if(data2[i]==1){
											data2[i] = 20;
										}

										String Time = listdata.get(i).getDay();
										Log.e("hjs", "data1[i] : " + data1[i] );
										Log.e("hjs", "month: " + Time);
										data1[i] = Float.valueOf(Time);
									} catch (Exception e) {
									}
								}
						}else if (DataQuerytemp.equalsIgnoreCase("day")) {
									for (int i = 0; i < listdata.size(); i++) {
										try {
											data1[i] = Float.valueOf(listdata.get(i).getTemp());
											if(data1[i]<=0){
												data1[i] = 0;
											}

											String Time = listdata.get(i).getTime();
											int ind = Time.indexOf("T");
											Time = Time.substring(ind + 1);
											Log.e("hjs", "Time: " + Time);

											data2[i] = Float.valueOf(Time);
											//tempHandler.sendEmptyMessage(0);
										} catch (Exception e) {
										}
									}

								}

				}



				if(DataQuerytemp.equalsIgnoreCase("year")) {
					tempTimeHandler.sendEmptyMessage(0);
				}else if (DataQuerytemp.equalsIgnoreCase("month")){
					monthHand.sendEmptyMessage(0);
				}
				else if (DataQuerytemp.equalsIgnoreCase("day"))	{
					tempHandler.sendEmptyMessage(0);
				}
				//tempTimeHandler.sendEmptyMessage(0);
			}


	@Override
	public void handleFinish(int code) {

	}

	@Override
	public void handleError(int code) {

	}

	class MyHandler extends Handler {
		WeakReference<Activity> mActivityReference;
		MyHandler(Activity activity) {
			mActivityReference = new WeakReference<Activity>(activity);
		}
		@Override
		public void handleMessage(Message msg) {
			final Activity activity = mActivityReference.get();
			if (activity != null) {

//			}
//
//	private  Handler tempHandler = new Handler() {
//
//		WeakReference<Activity > mActivityReference;
//
//		MyHandler(Activity activity) {
//			mActivityReference= new WeakReference<Activity>(activity);
//		}
//
//		public void handleMessage(android.os.Message msg) {
				closeMprogress();
				chartView = new LineChartView(getApplicationContext());
				chartView.setCountY(5);
				chartView.setUnitX("小时");
				chartView.setUnitY("");
				if (data1 != null) {
					chartView.addLine("", XForLine1, data1);
					chartView.invalidate();
					linechart.removeAllViews();
					linechart.addView(chartView);
				} else {
					Toast.makeText(DataActivity.this, "数据错误", Toast.LENGTH_SHORT).show();
				}

			}
			;
		}

	}

//	private  tempTimeHandler = new Handler() {
//		public void handleMessage(android.os.Message msg) {
/*
	chartView = new LineChartView(getApplicationContext());
		chartView.setCountY(6);
		chartView.setUnitX("小时");
		chartView.setUnitY("温度");
		if (data1 != null) {
		chartView.addLine("", XForLine1, data1);
		chartView.invalidate();
		linechart.removeAllViews();
		linechart.addView(chartView);
		*/

	class TempTimeHandler extends Handler {
		WeakReference<Activity> mActivityReference;

		TempTimeHandler(Activity activity) {
			mActivityReference = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			final Activity activity = mActivityReference.get();
			if (activity != null) {
				closeMprogress();
				yAxis.setVisibility(View.INVISIBLE);
				linechart.removeAllViews();
				LineCharViewY lineY = new LineCharViewY(getApplicationContext());
				lineY.setisshow(true);
				lineY.setCountY(4);
				lineY.setUnitX("月份");
				lineY.setUnitY("");
				if (data2 != null) {
					lineY.addLine("", XMoth, data2);
					lineY.invalidate();
					linechart.removeAllViews();
					linechart.addView(lineY);

				} else {
					Toast.makeText(DataActivity.this, "数据错误", Toast.LENGTH_SHORT).show();
				}
			}
			;
		}
		;
	}



	class MonTempTimeHandler extends Handler {
		WeakReference<Activity> mActivityReference;

		MonTempTimeHandler(Activity activity) {
			mActivityReference = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			final Activity activity = mActivityReference.get();
			if (activity != null) {
				closeMprogress();
				yAxis.setVisibility(View.INVISIBLE);
				linechart.removeAllViews();
				LineCharViewY lineY = new LineCharViewY(getApplicationContext());
//				LineChartView lineY = new LineChartView(getApplicationContext());
				Log.e("hjs ","lineY.setisshow(false);==");
				lineY.setisshow(false);

				//lineY.setCountY(4);
				//lineY.setCountX(2);
				lineY.setyValuse(true);
				lineY.setUnitX("天");
				lineY.setUnitY("");
//				data2[0] = 10;
//				data2[1] = 40;data2[2] = 20;
//				;data2[3] = 30;

				if (data2 != null) {
					lineY.addLine("", XMoth31, data2);
					lineY.invalidate();
					linechart.removeAllViews();
					linechart.addView(lineY);
				} else {
					Toast.makeText(DataActivity.this, "数据错误", Toast.LENGTH_SHORT).show();
				}
				//lineY.setisshow(true);
			}			;
		}
		;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_data);
		iniView();
		initListener();
		rbt_date.setChecked(true);
		Log.e("hjs", "onCreate:onCreateData ");
//		initData();
//
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		rbt_date.setChecked(true);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		closeMprogress();
//		data1[0] =0;
//		data1[1] =1;data1[2] =0;data1[3] =4;data1[4] =4;
//
//		data2[0] = 2018-6-28;
//		data2[1] = 2018745;
//		tempHandler.sendEmptyMessage(0);
//		tempTimeHandler.sendEmptyMessage(0);

	}

	/**
	 * 如果有网，从网络获取，如果没有，从本地数库获取
	 */
	private void initData(String datepara) {
		// TODO Auto-generated method stub
		linechart.removeAllViews();
		yAxis.setVisibility(View.VISIBLE);
		if (NetWork.isNetworkAvailable(DataActivity.this)) {
			showMprogress();
//			getUrlTemps();

			UserInfo info = EApp.getApp().getUserInfo(this);
			if(info!=null) {
				uid = info.getUid();
				mac = info.getMac();
				name=info.getUsername();

				if(TextUtils.isEmpty(mac)){
					//mac= "11113c4d5e6f";
				}

				LogUtil.info("info.getUid()=" + info.getUid());
				LogUtil.info("info.getMac()=" + info.getMac());
				if(TextUtils.isEmpty(uid))return;
				if(TextUtils.isEmpty(mac))return;
				String date = DateUtil.getCurDateStr_N();
				String gettemp ="";
				SharedPreferences pre_tempValues = getSharedPreferences("sp_da", Context.MODE_PRIVATE);
				float h = pre_tempValues.getFloat("temperature", (float) 0);

				if(h>0){
					gettemp =String.valueOf(h);
				}else{
					gettemp = "37.5";
				}

				if(datepara!=null){
					refresh(uid, mac, datepara, gettemp);
				}else {
					refresh(uid, mac, date, gettemp);
				}
			}

		} else {
			getLocalTemps();
		}

		/*
		 * mUtils.send(HttpMethod.GET, PathUtils.getStatpath(uid, phone, date),
		 * new RequestCallBack<String>() {
		 * 
		 * @Override public void onFailure(HttpException arg0, String arg1) { //
		 * TODO Auto-generated method stub
		 * 
		 * }
		 * 
		 * @Override public void onSuccess(ResponseInfo<String> arg0) { // TODO
		 * Auto-generated method stub String result = arg0.result; try {
		 * JSONObject jsonObject = new JSONObject(result); int code =
		 * jsonObject.getInt("code"); String msg = jsonObject.getString("msg");
		 * Log.e(TAG, "统计温度" + code + msg); JSONArray array =
		 * jsonObject.getJSONArray("data"); for (int i = 0; i < array.length();
		 * i++) { data1[i] = Float.parseFloat(array.getString(i)); } } catch
		 * (JSONException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } chartView = new
		 * LineChartView(getApplicationContext()); chartView.setCountY(6);
		 * chartView.setUnitX("小时"); chartView.setUnitY("温度");
		 * linechart.removeAllViews(); if (data1 != null) {
		 * chartView.addLine("", XForLine1, data1);
		 * linechart.addView(chartView); } else {
		 * Toast.makeText(getApplicationContext(), "数据请求错误",
		 * Toast.LENGTH_SHORT).show(); }
		 * 
		 * } });
		 */

	}

	@SuppressLint("ClickableViewAccessibility")
	private void initListener() {
		// TODO Auto-generated method stub
		/*
		 * img_back.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * 
		 * finish(); } });
		 */
		img_back.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ResourceAsColor")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					finish();
				}
				return false;
			}
		});
		img_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (rbt_date.isChecked()) {
					initData(null);
				} else if (rbt_year.isChecked()) {
					getLineChartY();
				}

			}
		});
		rg_date.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.data_rbtn_date:
					Log.e("hjs", "data_rbtn_date");
					DataQuerytemp ="day";
					initData(null);
					break;
				case R.id.data_rbtn_year:
					DataQuerytemp ="year";
					getLineChartY();
					break;
				case R.id.data_rbtn_month:


					DataQuerytemp ="month";
					String month = DateUtil.getCurMonthDateStr_N();
					Log.e("hjs", "month:"+month);
					initData(month);
//					Intent intent = new Intent();
//					intent.setClass(getApplicationContext(), MyCalendar.class);
//					startActivityForResult(intent,100);
					break;
				default:
					break;
				}
			}

		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e("hjs","requestCode"+requestCode);

		Log.e("hjs","resultCode"+resultCode);
	}



	/**
	 * 如果有网络，从网络获取发烧次数数组，如果没有，从本地数据库获取
	 */
	private void getLineChartY() {
		// TODO Auto-generated method stub
		yAxis.setVisibility(View.GONE);
		if (NetWork.isNetworkAvailable(DataActivity.this)) {
			//showMprogress();
			getTempTime();
		} else {
			getLocalTempTime();
		}

		/*
		 * mUtils.send(HttpMethod.GET, PathUtils.getLineChartData(uid, phone,
		 * year), new RequestCallBack<String>() {
		 * 
		 * @Override public void onFailure(HttpException arg0, String arg1) { //
		 * TODO Auto-generated method stub
		 * 
		 * }
		 * 
		 * @Override public void onSuccess(ResponseInfo<String> arg0) { // TODO
		 * Auto-generated method stub String result = arg0.result; try {
		 * JSONObject jsonObject = new JSONObject(result); int code =
		 * jsonObject.getInt("code"); String msg = jsonObject.getString("msg");
		 * Log.e(TAG, "按年统计次数的code" + code + msg); JSONArray array =
		 * jsonObject.getJSONArray("data"); for (int i = 0; i < array.length();
		 * i++) { data2[i] = Float.parseFloat(array.getString(i)); }
		 * linechart.removeAllViews(); LineCharViewY lineY = new
		 * LineCharViewY(getApplicationContext()); lineY.setUnitX("月份");
		 * lineY.setUnitY("次数"); if (data2 != null) { lineY.addLine("", XMoth,
		 * data2); linechart.addView(lineY); } else {
		 * Toast.makeText(getApplicationContext(), "数据请求错误",
		 * Toast.LENGTH_SHORT).show(); }
		 * 
		 * } catch (JSONException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } } });
		 */
	}

	private void iniView() {
		// TODO Auto-generated method stub
		img_back = (ImageView) findViewById(R.id.data_img_back);
		rg_date = (RadioGroup) findViewById(R.id.data_rg_date);
		img_update = (ImageView) findViewById(R.id.data_img_flash);
		linechart = (LinearLayout) findViewById(R.id.data_layout_chartLine);
		rbt_date = (RadioButton) findViewById(R.id.data_rbtn_date);
		rbt_year = (RadioButton) findViewById(R.id.data_rbtn_year);
		yAxis = (Axis) findViewById(R.id.data_layout_yAxis);
		for (int i = 0; i < 240; i++) {
			XForLine1[i] = i + "";
		}
		calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;
		day = calendar.get(Calendar.DAY_OF_MONTH);
		childPhone = (String) SPUtils.get(this, "userName", "0");

	}

	/**
	 * 从本地数据库得到发烧次数数组
	 */
	private void getLocalTempTime() {
		ScheduleDAO dao = new ScheduleDAO(this);
		for (int i = 1; i <= data2.length; i++) {
			ArrayList<ScheduleDataTag> dateTagList = dao.getTagDate(year, i);
			if (dateTagList != null && dateTagList.size() > 0) {
				data2[i - 1] = Float.parseFloat(dateTagList.size() + "");
			} else {
				data2[i - 1] = Float.parseFloat("0");
			}
			closeMprogress();

		}
		linechart.removeAllViews();
		LineCharViewY lineY = new LineCharViewY(getApplicationContext());
		lineY.setisshow(true);
		lineY.setCountY(5);
		lineY.setUnitX("月份");
		lineY.setUnitY("次数");
		if (data2 != null) {
			lineY.addLine("", XMoth, data2);
			lineY.invalidate();
			linechart.addView(lineY);

		} else {
			Toast.makeText(DataActivity.this, "数据错误", Toast.LENGTH_SHORT).show();
		}
	}

	private String DataQuerytemp;
	/**
	 * 从网络得到发烧次数数组
	 */
	private void getTempTime() {
		calendar.setTime(new Date());
		String year = String.valueOf(calendar.get(Calendar.YEAR));
		Log.e("hjs","year="+year);
		/**
		 * 如果有网，从网络获取，如果没有，从本地数库获取
		 */
			// TODO Auto-generated method stub
			linechart.removeAllViews();
			yAxis.setVisibility(View.INVISIBLE);
			if (NetWork.isNetworkAvailable(DataActivity.this)) {
				showMprogress();
//			getUrlTemps();

				UserInfo info = EApp.getApp().getUserInfo(this);
				if(info!=null) {
					uid = info.getUid();
					mac = info.getMac();


					if(TextUtils.isEmpty(mac)){
						//mac= "11113c4d5e6f";
						return;
					}

					LogUtil.info("info.getUid()=" + info.getUid());
					LogUtil.info("info.getMac()=" + info.getMac());
					if(TextUtils.isEmpty(uid))return;
					if(TextUtils.isEmpty(mac))return;
					String gettemp ="";
					SharedPreferences pre_tempValues = getSharedPreferences("sp_da", Context.MODE_PRIVATE);
					float h = pre_tempValues.getFloat("temperature", (float) 0);

					if(h>0){
						gettemp =String.valueOf(h);
					}else{
						gettemp = "37.5";
					}

					if(year!=null){
						refresh(uid, mac, year, gettemp);
					}
				}

			} else {
				getLocalTemps();
			}

//
//		RequestParams params = new RequestParams(PathUtils.getTime());
//		params.addQueryStringParameter("phone", childPhone);
//		params.addQueryStringParameter("y", year);
//		Log.e(TAG, childPhone + year);
//		x.http().get(params, new CommonCallback<String>() {
//
//			@Override
//			public void onCancelled(CancelledException arg0) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onError(Throwable arg0, boolean arg1) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onFinished() {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onSuccess(String arg0) {
//				// TODO Auto-generated method stub
//				Log.e(TAG, arg0.toString());
//				try {
//					JSONObject json = new JSONObject(arg0);
//					int code = json.getInt("code");
//					String msg = json.getString("msg");
//					if (code == 100) {
//						JSONArray arr = json.getJSONArray("data");
//						for (int i = 0; i < arr.length(); i++) {
//							data2[i] = arr.getInt(i);
//						}
//						tempTimeHandler.sendEmptyMessage(0);
//					} else {
//						getLocalTempTime();
//					}
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		});

	}

	/**
	 * 从本地数据库得到体温数组
	 */
	private void getLocalTemps() {
		ScheduleDAO dao = new ScheduleDAO(this);
		String[] temps = dao.getTemps();

//		String[]	temps2 = {"10","20","38","40","41","42","42","42","42"};
//		temps= temps2;
		if(temps==null) return;


		for (int i = 0; i < temps.length; i++) {
			data1[i] = Float.parseFloat(temps[i]);
			Log.e("折线图", temps[i]);
		}
		chartView = new LineChartView(getApplicationContext());
		chartView.setCountY(5);
		chartView.setUnitX("小时");
		chartView.setUnitY("温度");
		if (data1 != null) {
			chartView.addLine("", XForLine1, data1);
			chartView.invalidate();
			linechart.removeAllViews();
			linechart.addView(chartView);
		} else {
			Toast.makeText(DataActivity.this, "数据错误", Toast.LENGTH_SHORT).show();
		}
		closeMprogress();
//		data2[0] = 20;
//		data2[1] = 20;
//		data2[2] = 20;
//		data2[3] =0;
//		data2[4] = 20;



//		monthHand.sendEmptyMessage(0);
	}

	/**
	 * 得到网络后台体温数组
	 */
	private void getUrlTemps() {
		RequestParams params = new RequestParams(PathUtils.getTemps(childPhone, year + "", month + "", day + ""));
		Log.e(TAG, PathUtils.getTemps(childPhone, year + "", month + "", day + ""));
		x.http().get(params, new CommonCallback<String>() {

			@Override
			public void onCancelled(CancelledException arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(String arg0) {
				// TODO Auto-generated method stub
				Log.e(TAG, arg0.toString());

				try {
					JSONObject jsonObject = new JSONObject(arg0);
					int code = jsonObject.getInt("code");
					if (code == 100) {
						Log.e(TAG, "in");
						JSONArray arr = jsonObject.getJSONArray("data");
						for (int i = 0; i < arr.length() - 10; i++) {
							data1[i] = Float.parseFloat(arr.getString(i));
						}
						tempHandler.sendEmptyMessage(0);
					} else {
						closeMprogress();
						getLocalTemps();

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	// 显示加载框
	private void showMprogress() {
		if (mProgress == null) {
			mProgress = ProgressDialog.show(DataActivity.this, "", "正在加载数据");
			mProgress.setCancelable(false);
			mProgress.show();
		}
		mProgress.setOnKeyListener(onKeyListener);
	}

	private void closeMprogress() {
		if (isFinishing()) {
			return;
		}
		if (mProgress != null && mProgress.isShowing()) {
			mProgress.dismiss();
			mProgress = null;
		}
	}

	private OnKeyListener onKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
				closeMprogress();

			}
			return false;
		}
	};

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (mProgress != null && mProgress.isShowing()) {
			mProgress.dismiss();
			mProgress = null;
		} else {
			super.onBackPressed();
		}
	}

	public static void main(String args[]){

		StringBuffer sb = new StringBuffer("112233445566");
		for(int i = 2; i < sb.length(); i+=3){
			sb.insert(i,":");
		}
		System.out.print(sb.toString());

//		Gson gson = new Gson();
//		String result2= result;
//
//		ArrayList<QueryDataRes> list = new ArrayList<QueryDataRes>();
//		Type listType = new TypeToken<List<QueryDataRes>>() {}.getType();
//		list = gson.fromJson(result2, listType);
//		System.out.println(list.size());


	}


}
