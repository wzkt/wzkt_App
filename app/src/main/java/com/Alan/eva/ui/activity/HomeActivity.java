package com.Alan.eva.ui.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.Alan.eva.BuildConfig;
import com.Alan.eva.R;
import com.Alan.eva.config.BLEConfig;
import com.Alan.eva.config.BleEvent;
import com.Alan.eva.config.DownloadConfig;
import com.Alan.eva.config.URlConfig;
import com.Alan.eva.foreground.DaemonService;
import com.Alan.eva.http.OkHttpManager;
import com.Alan.eva.http.core.IResultHandler;
import com.Alan.eva.http.get.CheckVersionGet;
import com.Alan.eva.http.get.ChildSummaryGet;
import com.Alan.eva.http.post.CreateChildPost;
import com.Alan.eva.http.post.CreateThermometerRecord;
import com.Alan.eva.http.post.ForgetGetPwdPost;
import com.Alan.eva.http.post.QueryMonitorPost;
import com.Alan.eva.model.ChildSummary;
import com.Alan.eva.model.UserInfo;
import com.Alan.eva.model.VersionData;
import com.Alan.eva.result.ChildSummaryRes;
import com.Alan.eva.result.MonitorRes;
import com.Alan.eva.result.QueryMonitorRes;
import com.Alan.eva.result.VersionRes;
import com.Alan.eva.service.BleService;
import com.Alan.eva.service.ServiceUtils;
import com.Alan.eva.service.ToastUtil;
import com.Alan.eva.service.UpdateService;
import com.Alan.eva.tools.LogUtil;
import com.Alan.eva.tools.PathUtils;
import com.Alan.eva.tools.SPUtils;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.tools.alarm.AlarmNotificationManager;
import com.Alan.eva.ui.EApp;
import com.Alan.eva.ui.core.AbsActivity;
import com.Alan.eva.ui.dialog.GenderDialog;
import com.Alan.eva.ui.dialog.KickDialog;
import com.Alan.eva.ui.dialog.MacInputDialog;
import com.Alan.eva.ui.dialog.OperateDialog;
import com.Alan.eva.ui.dialog.PopuDialog;
import com.Alan.eva.ui.widget.CircleImageView;
import com.Alan.eva.ui.widget.PickerView;
import com.Alan.eva.ui.widget.TempCircleView;
import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.ex.DbException;
import org.xutils.ex.HttpException;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.Alan.eva.config.BLEConfig.CMD_EXTRA;
import static com.Alan.eva.ui.EApp.getApp;

/**
 * Created by CW on 2017/2/21.
 * 新首页
 */
public class HomeActivity extends AbsActivity implements View.OnClickListener, IResultHandler {
    private AppCompatTextView tv_home_check_new_version;
    private AppCompatTextView tv_home_log_out;
    private DrawerLayout drawer_home_holder;
    private AppCompatTextView tv_home_temp_operator;
    private AppCompatImageView iv_home_indicator_bg;
    private TempCircleView circle_view_home_temp_indicator;
    private AppCompatTextView tv_home_temp_tips_shower;

    private CircleImageView circle_home_child_portrait;
    private AppCompatTextView tv_home_child_name;
    private AppCompatTextView tv_home_child_height;
    private AppCompatTextView tv_home_child_weight;
    private AppCompatTextView tv_home_child_age;
    private AppCompatTextView tv_home_child_gender;

    private AppCompatTextView tv_home_child_max;
    private AppCompatTextView tv_home_child_min;
    private AppCompatTextView tv_home_child_count;
    private AppCompatTextView tv_home_child_tips;

    private BleEvent bleEvent;

    private BluetoothDevice device;
    private ImageView img_home_drug, img_home_temperature, img_home_kicked, img_home_data, img_home_alrmIcon;

    private double maxTemperature, minTemperaTure, courntTempera, emvcourntTempera;

    private double highTemp = 38;
    static MyHandler myhomehan;

    public static Context homecontext;

    @Override
    public Activity getCurrActivity() {
        return this;
    }

    @Override
    public int getRootViewId() {
        return R.layout.ac_home;
    }

    @Override
    public void findView(View rootView) {
        initTitleBar();
        tv_home_temp_operator = (AppCompatTextView) getView(R.id.tv_home_temp_operator);
        iv_home_indicator_bg = (AppCompatImageView) getView(R.id.iv_home_indicator_bg);
        circle_view_home_temp_indicator = (TempCircleView) getView(R.id.circle_view_home_temp_indicator);
        tv_home_temp_tips_shower = (AppCompatTextView) getView(R.id.tv_home_temp_tips_shower);

        AppCompatTextView tv_home_about_us = (AppCompatTextView) getView(R.id.tv_home_about_us);
        AppCompatTextView tv_home_user_helper = (AppCompatTextView) getView(R.id.tv_home_user_helper);
        AppCompatTextView tv_home_medicine_remind = (AppCompatTextView) getView(R.id.tv_home_medicine_remind);
        AppCompatTextView tv_home_seggestion = (AppCompatTextView) getView(R.id.tv_home_seggestion);
        AppCompatTextView tv_home_version_name = (AppCompatTextView) getView(R.id.tv_home_version_name);
        tv_home_version_name.setText(String.valueOf("当前版本：V" + BuildConfig.VERSION_NAME));

        AppCompatTextView tv_home_change_pwd = (AppCompatTextView) getView(R.id.tv_changpwd_setting);
        AppCompatTextView tv_wifi_setting = (AppCompatTextView) getView(R.id.tv_wifi_setting);
        AppCompatTextView input_mac = (AppCompatTextView) getView(R.id.tv_mac_input);

        AppCompatTextView tv_monitor_setting = (AppCompatTextView) getView(R.id.tv_monitor_setting);

        tv_home_check_new_version = (AppCompatTextView) getView(R.id.tv_home_check_new_version);
        tv_home_log_out = (AppCompatTextView) getView(R.id.tv_home_log_out);

        circle_home_child_portrait = (CircleImageView) getView(R.id.circle_home_child_portrait);
        tv_home_child_name = (AppCompatTextView) getView(R.id.tv_home_child_name);
        tv_home_child_height = (AppCompatTextView) getView(R.id.tv_home_child_height);
        tv_home_child_weight = (AppCompatTextView) getView(R.id.tv_home_child_weight);
        tv_home_child_age = (AppCompatTextView) getView(R.id.tv_home_child_age);
        tv_home_child_gender = (AppCompatTextView) getView(R.id.tv_home_child_gender);

        tv_home_child_max = (AppCompatTextView) getView(R.id.tv_home_child_max);
        tv_home_child_min = (AppCompatTextView) getView(R.id.tv_home_child_min);
        tv_home_child_count = (AppCompatTextView) getView(R.id.tv_home_child_count);
        tv_home_child_tips = (AppCompatTextView) getView(R.id.tv_home_child_tips);

        img_home_drug = (ImageView) getView(R.id.img_home_drug);
        img_home_temperature = (ImageView) getView(R.id.img_home_temperature);
        img_home_kicked = (ImageView) getView(R.id.img_home_quilt);
        img_home_data = (ImageView) getView(R.id.img_home_data);
        Button wifibaijianbt = (Button) getView(R.id.wifibaijianbt);
        wifibaijianbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserInfo userInfo = getApp().getUserInfo(getCurrActivity());
                if (userInfo != null) {
                    String uid = userInfo.getUid();
                    if (TextUtils.isEmpty(uid)) {
                        showTips("请先登录");
                        login();
                        return;
                    }
                } else {
                    showTips("请先登录");
                    login();
                    return;
                }

                Intent intentstartMonitor = new Intent(getCurrActivity(), MonitorActivity.class);
                startActivity(intentstartMonitor);
            }
        });

        resetOperate("扫描体温计", "请开始扫描体温计");
        tv_home_about_us.setOnClickListener(this);
        tv_home_user_helper.setOnClickListener(this);
        tv_home_medicine_remind.setOnClickListener(this);
        tv_home_seggestion.setOnClickListener(this);
        tv_wifi_setting.setOnClickListener(this);
        tv_monitor_setting.setOnClickListener(this);
        tv_home_change_pwd.setOnClickListener(this);
        tv_home_check_new_version.setOnClickListener(this);
        tv_home_log_out.setOnClickListener(this);
        input_mac.setOnClickListener(this);
        setlistenner();
        connecttime = 0;

        homecontext = this;
        myhomehan = new MyHandler(getCurrActivity());

        remeber_mac = (TextView) getView(R.id.remeber_mac);
        Switch remeber_switch =(Switch) getView(R.id.switch_mac_onoff);
      String mac= getremebermac();

        remeber_mac.setText(mac);
        Log.d("hjs", " mac:" + mac);
        if ((mac.length() == 12)) {
            StringBuffer sb = new StringBuffer(mac);
            for (int i = 2; i < sb.length(); i += 3) {
                sb.insert(i, ":");
            }
            mac = (sb.toString().toUpperCase());
            phonemac = mac;
            Log.d("hjs", " mac:" + phonemac);
        }

        remeber_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                remeberswitch(isChecked);
                getswithc = isChecked;
                LogUtil.inf("getswithc:"+getswithc);
            }
        });

        remeber_switch.setChecked(getswitch());
        getswithc = getswitch();
        LogUtil.inf("getswithc:"+getswithc);
//        SPUtils.put(homecontext, "isRing", false);
//        SPUtils.put(homecontext, "superisRing", false);
//        SPUtils.put(homecontext, "dengbei", false);
    }
    TextView remeber_mac;

    public static boolean getswithc = false;

    private boolean getswitch() {
        SharedPreferences pres = getCurrActivity().getSharedPreferences("switch", Context.MODE_PRIVATE);
        return pres.getBoolean("switch", false);
    }
    private void remeberswitch(boolean flag) {
        SharedPreferences pres = getCurrActivity().getSharedPreferences("switch", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pres.edit();
        editor.putBoolean("switch", flag);
        editor.commit();
    }


    private void setlistenner() {
        img_home_drug.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new Intent();
                intent.setClass(getCurrActivity(), AlarmActivity.class);
                getCurrActivity().startActivity(intent);

            }
        });
        img_home_temperature.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                /*
                 * Intent intent = new Intent(); intent.setClass(getActivity(),
				 * TemperatureActivity.class); startActivity(intent);
				 */

                startSettimePopu();
            }
        });
        img_home_kicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                /*
				 * Intent intent = new Intent(); intent.setClass(getActivity(),
				 * KickedActivity.class); startActivity(intent);
				 */
                startKickSettimePopu();
            }
        });
        img_home_data.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(getCurrActivity(), DataActivity.class);
                startActivity(intent);

            }
        });

    }

    PopuDialog popuDialog;

    private void startSettimePopu() {

        if (popuDialog == null) {
            popuDialog = new PopuDialog(getCurrActivity());
        }
        popuDialog.create(Gravity.BOTTOM, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popuDialog.show();

    }

    KickDialog startKickSet;

    private void startKickSettimePopu() {

        if (startKickSet == null) {
            startKickSet = new KickDialog(getCurrActivity());
        }
        startKickSet.create(Gravity.BOTTOM, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        startKickSet.show();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_home_about_us:  //关于我们
                Intent about = getIntent(CommonWebActivity.class);
                about.putExtra(URlConfig.URL_KEY_TITLE, URlConfig.ABOUT_US);
                about.putExtra(URlConfig.URL_KEY_URL, URlConfig.ABOUT_US_URL);
                startActivity(about);
                break;
            case R.id.tv_home_user_helper:  //使用说明
                Intent helper = getIntent(CommonWebActivity.class);
                helper.putExtra(URlConfig.URL_KEY_TITLE, URlConfig.ABOUT_PRODUCT);
                helper.putExtra(URlConfig.URL_KEY_URL, URlConfig.ABOUT_PRODUCT_URL);
                startActivity(helper);
                break;
            case R.id.tv_home_medicine_remind:  //吃药提醒
                gotoActivity(AlarmListActivity.class);
                break;
            case R.id.tv_home_seggestion:  //反馈建议
                gotoActivity(SuggestActivity.class);
                break;
            case R.id.tv_home_check_new_version:  //检查更新
                checkVersion();
                break;
            case R.id.tv_wifi_setting:
                startActivity(new Intent(this, FifthFragment.class));
//                gotoDeviceDetail();
                break;
            case R.id.tv_monitor_setting:


                UserInfo userInfo = getApp().getUserInfo(getCurrActivity());
                if (userInfo != null) {
                    String uid = userInfo.getUid();
                    if (TextUtils.isEmpty(uid)) {
                        showTips("请先登录");
                        login();
                        return;
                    }
                } else {
                    showTips("请先登录");
                    login();
                    return;
                }

                if (!TextUtils.isEmpty(tempaddress)) {
                    try {
                        // UserInfo userInfo = getApp().getUserInfo(this);
                        if (userInfo == null) {
                            userInfo = new UserInfo();
                        }
                        userInfo.setMac(tempaddress.replaceAll(":", ""));
                        getApp().setUserInfo(userInfo, this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                Intent intentstartMonitor = new Intent(this, MonitorActivity.class);
                startActivity(intentstartMonitor);
                break;
            case R.id.tv_changpwd_setting:
                startActivity(new Intent(this, ChangePassActivity.class));
                break;
            case R.id.tv_mac_input:
                input_dialogshow();
                break;
            case R.id.tv_home_log_out:  //退出登录
                showLogout();
                break;
        }
    }
//    private void getmac(){
//        String mac="00:00:00:1C:7C:0C";
//    //正则校验MAC合法性
//        String patternMac="^[A-F0-9]{2}(-[A-F0-9]{2}){5}$";
//        if(!Pattern.compile(patternMac).matcher(mac).find()){
//            ToastUtil.show(getApplicationContext(), "MAC地址格式或者大小写错误");
//        }
//    }

    private void remebermac(String mac) {
        SharedPreferences pres = getCurrActivity().getSharedPreferences("inputmac", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pres.edit();
        editor.putString("inputmac", mac);
        editor.commit();
    }

    private String getremebermac() {
        SharedPreferences pres = getCurrActivity().getSharedPreferences("inputmac", Context.MODE_PRIVATE);
        return pres.getString("inputmac", "");
    }

    public  static  String phonemac;

    private void input_dialogshow() {
        MacInputDialog inputDialog = new MacInputDialog(getCurrActivity());
        inputDialog.setTitle("请输入要绑定体温计的MAC地址");
        inputDialog.setContentHint("请输入体温计地址");
        inputDialog.setOnOk(v -> {
            phonemac = inputDialog.getContent();
            if (TextUtils.isEmpty(phonemac)) {
                inputDialog.errorAlert("请输入体温计地址");
                return;
            }
            inputDialog.dismiss();
            String mac = phonemac;
            if (mac.length() == 12) {

                StringBuffer sb = new StringBuffer(mac);
                for (int i = 2; i < sb.length(); i += 3) {
                    sb.insert(i, ":");
                }
                mac = (sb.toString().toUpperCase());


                remebermac(mac.replaceAll(":", ""));

                Log.e("hjs", "mac:::" + mac);
                connectmacBle(mac);
            } else {
                ToastUtil.show(getApplicationContext(), "MAC地址格式或者大小写错误");
            }
        });
        inputDialog.setOnCancel(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //currFinish();
                inputDialog.dismiss();
            }
        });

        String remebemac = getremebermac();
        if (!TextUtils.isEmpty(remebemac)) {
            phonemac = remebemac;
        }

        int wid = getCurrActivity().getResources().getDimensionPixelOffset(R.dimen.size_300);
        inputDialog.create(wid, ViewGroup.LayoutParams.WRAP_CONTENT);
        inputDialog.show();
        if (!TextUtils.isEmpty(phonemac)) inputDialog.setContent("" + phonemac);

    }


    private final int CHECK_VERSION = 0x0016;

    private void checkVersion() {
        int versionCode = BuildConfig.VERSION_CODE;
        CheckVersionGet get = new CheckVersionGet();
        get.code(CHECK_VERSION);
        get.handler(this);
        get.setCode(String.valueOf(versionCode));
        get.get();
    }

    /**
     * 显示退出登录对话框
     */
    private void showLogout() {
        OperateDialog dialog = new OperateDialog(getCurrActivity());
        dialog.setContent("是否要退出登录");
        dialog.setOk("退出");
        dialog.setOnOk(v -> {
            dialog.dismiss();
            getApp().setUserInfo(null, getCurrActivity());
            MobclickAgent.onProfileSignOff();
            tv_home_log_out.setVisibility(View.GONE);
            showTips("已退出");
        });
        int wid = getCurrActivity().getResources().getDimensionPixelOffset(R.dimen.size_300);
        dialog.create(wid, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        post_thermometer_record_time = 0;
        Intent startCmd = new Intent(getCurrActivity(), BleService.class);
        startService(startCmd);

        startService(new Intent(getApplicationContext(), DaemonService.class));

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                Intent startCmd = new Intent(getCurrActivity(), BleService.class);
//                startService(startCmd);
//            }
//        }).start();

        EventBus.getDefault().register(homecontext);
        bleEvent = new BleEvent();
        startAperture();

        UserInfo userInfo = getApp().getUserInfo(getCurrActivity());
        if (userInfo != null) {
            String cid = userInfo.getCid();
            if (!TextUtils.isEmpty(cid)) {
                tv_home_log_out.setVisibility(View.VISIBLE);
                sendCmd(BLEConfig.CHILD_ID_CMD, cid);//添加监听对象id
                //summaryGet(cid);
            } else {

//                if(TextUtils.isEmpty(userInfo.getMac())){
//                    dialogshow(userInfo.getUid());
//                }else {
//                    //tv_home_log_out.setVisibility(View.GONE);
//                    //showAddChildDialog();
//                }

//                tv_home_log_out.setVisibility(View.GONE);

                //  dialogshow();

                if (TextUtils.isEmpty(userInfo.getMac())) {
                    showAddChildDialog();
                }
            }
        }
    }


    public static boolean ifwhiletruefailed = true;

    public static boolean ifbleisconnect = false;

    private void dialogshow() {
        OperateDialog dialog = new OperateDialog(getCurrActivity());
        dialog.setContent("是否远程监控");
        dialog.setOk("确定");
        dialog.setOnOk(v -> {
            dialog.dismiss();
            Intent intentstartMonitor = new Intent(homecontext, MonitorActivity.class);
            startActivity(intentstartMonitor);
        });
        dialog.setOnCancel(v -> {
            dialog.dismiss();
        });
        int wid = getCurrActivity().getResources().getDimensionPixelOffset(R.dimen.size_300);
        dialog.create(wid, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();


//        MacInputDialog inputDialog = new MacInputDialog(getCurrActivity());
//        inputDialog.setTitle("是否网络监控温度");
//        inputDialog.setOk("确定");
//        inputDialog.setOnOk(v -> {
//
//        });
//        inputDialog.setOnCancel(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //currFinish();
//                inputDialog.dismiss();
//                showAddChildDialog();
//            }
//        });
//        inputDialog.setCancel("取消");
//
//        int wid = getCurrActivity().getResources().getDimensionPixelOffset(R.dimen.size_300);
//        inputDialog.create(wid, ViewGroup.LayoutParams.WRAP_CONTENT);
//        inputDialog.show();
        //inputDialog.setContent("94e36d9e451d");

    }

    @Override
    protected void onStart() {
        super.onStart();
        getServerVer(getApplicationContext());
    }


    /**
     * 根据蓝牙地址进行连接
     *
     * @param address mac 地址
     */
    public void connectBle(String address) {
        if (BluetoothAdapter.checkBluetoothAddress(address)) {
            sendCmd(BLEConfig.BLE_CONNECT_CMD, address);//连接
        } else {

            Log.e("hjs", "connectBle.============");
            resetOperate("重新扫描", "体温计校验错误，请尝试扫描其他体温计");
        }
    }

    /**
     * 根据蓝牙地址进行连接
     *
     * @param address mac 地址
     */
    public void connectmacBle(String address) {
        if (BluetoothAdapter.checkBluetoothAddress(address)) {
            sendCmd(BLEConfig.MAC_CONNECT_CMD, address);//连接
        } else {
            Log.e("hjs", "connectBle.============");
            resetOperate("重新扫描", "体温计校验错误，请尝试扫描其他体温计");
        }
    }

    /**
     * 重置界面并添加提示信息
     *
     * @param operate 操作提示内容
     */
    private void resetOperate(String operate, String tips) {
        tv_home_temp_operator.setClickable(true);
        tv_home_temp_operator.setText(operate);
        tv_home_temp_operator.setOnClickListener(v -> startScan());
        tv_home_temp_tips_shower.setText(tips);
    }


    private float getsettemp() {
        float values = 0;
        SharedPreferences pre_tempValues = homecontext.getSharedPreferences("sp_da", Context.MODE_PRIVATE);
        values = pre_tempValues.getFloat("temperature", 0);
        return values;
    }

    private float getkickIsSwitchtemp() {
        float values = 0;
        SharedPreferences pre_tempValues = homecontext.getSharedPreferences("kickwendu", Context.MODE_PRIVATE);
        values = pre_tempValues.getInt("kickwendu", 0);
        return values;
    }

    private boolean getkickalarmswtich() {
        boolean tem = false;
        try {

            SharedPreferences pres = homecontext.getSharedPreferences("kickalarm", Context.MODE_PRIVATE);
            tem = pres.getBoolean("kickIsSwitch", false);
            Log.e("hjs", "kickIsSwitch==" + tem);

        } catch (Exception e) {
            tem = false;
        }
        return tem;
    }


    private boolean getTempIsSwitchswtich() {
        boolean tem = false;
        try {
            tem = (boolean) SPUtils.get(homecontext, "TempIsSwitch", false);
            Log.e("hjs", "getTempIsSwitchswtich==" + tem);
        } catch (Exception e) {
            tem = false;
        }
        return tem;
    }

    private static boolean showTemp = false;
    private static boolean showTempflag = false;

    void showTempWarning(String bodytemp) {
        if (!showTemp) {
            AlertDialog dialog = new AlertDialog.Builder(homecontext)
                    .setIcon(R.mipmap.bodytemp)//设置标题的图片
                    .setTitle("高烧提醒")//设置对话框的标题
                    .setMessage("请注意,孩子发烧了,体温温度" + bodytemp)//设置对话框的内容
                    //设置对话框的按钮
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            showTemp = false;
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            showTemp = false;
                        }
                    }).create();
            dialog.show();
            showTemp = true;
        }
    }

    private int isSuperPlay = 0;
    private int isPlay = 0;// 为0时，高温报警执行，为1时，不执行
    private int isKick = 0;// 为0时，蹬被报警执行，为1时，不执行
    private int recordTemp = 0;// 用于判断烧的稳定性
    private static long subtime1min = 1000 * 15;
    private static long subtime10min = 1000 * 60 * 10;

    private static long subtime1min42 = 1000 * 15;
    private static long subtime10min42 = 1000 * 60 * 10;

    private static long kicksubtime1min = 1000 * 15;
    private static long kicksubtime10min = 1000 * 60 * 10;
    private static long adayshow = 1000 * 60 * 60 * 8;

//    private static long kicksubtime10min  = 1000*60*5;
//    private static long adayshow = 1000*60*10;

    private static boolean gaowentemp = false;
    CountDownTimer countDownTimer;

    private void startdismisscount() {
        if (countDownTimer != null) return;
        countDownTimer = new CountDownTimer(adayshow, kicksubtime10min) {
            @Override
            public void onTick(long millisUntilFinished) {
                LogUtil.inf("onTick+" + millisUntilFinished);
                showTempflag = !showTempflag;
                gaowentemp = true;
            }

            @Override
            public void onFinish() {
                gaowentemp = false;
                LogUtil.inf("onFinish+");
            }
        }.start();
    }

    private static double showtemp = 37.5;

    private static long lasttime = 0;
    private static long nowtime = 0;

    private synchronized void tempplaying(Context con, String n) {
        recordTemp++;
        try {
            nowtime = (System.currentTimeMillis());
            int hours = (int) ((nowtime - lasttime) / (1000 * 60));
            System.out.println("两个时间之间的小时差为：" + hours);
            if (hours < 30) {
                gaowentemp = true;
                showTempflag = true;
            } else {
                gaowentemp = false;
            }
        } catch (Exception e) {
            gaowentemp = false;
            showTempflag = true;
        }

        /**
         * 持续超过30秒，才会报警
         */
        if (recordTemp >= 4) {
            recordTemp = 0;
            /**
             * 如果预警开关打开，跳转到闹铃界面
             */
            if ((boolean) SPUtils.get(homecontext, "TempIsSwitch", false)) {
                if ((!TempPlayingActivity.isRingremue) && (!gaowentemp)) {
                    //startdismisscount();
                    // Date lastnowTime = new Date(System.currentTimeMillis());
                    lasttime = (System.currentTimeMillis());
                    //  String retStrFormatNowDate = sdFormatter.format(lastnowTime);

                    SPUtils.put(homecontext, "isRing", true);
                    Intent tempIntent = new Intent();
                    tempIntent.setClass(con, TempPlayingActivity.class);
                    homecontext.startActivity(tempIntent);
                } else {
                    if (showTempflag) {
                        try {
                            double tempera = Double.valueOf(n);
                            if (tempera > showtemp) {
                                showTempWarning(n);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }


    private boolean getSuperTempIsSwitchswtich(Context con) {
        boolean tem = false;
        try {
            tem = (boolean) SPUtils.get(homecontext, "Supertem_Switch", true);
            Log.e("hjs", "Supertem_Switch==" + tem);
        } catch (Exception e) {
            tem = false;
        }
        tem = true;
        return tem;
    }

    private boolean super42 = false;
    CountDownTimer countDownTimer42;
    private boolean show42Tempflag = false;

    private void startdismisscount42() {
        if (countDownTimer42 != null) return;
        countDownTimer42 = new CountDownTimer(adayshow, kicksubtime10min) {
            @Override
            public void onTick(long millisUntilFinished) {
                super42 = true;
                show42Tempflag = !show42Tempflag;
            }

            @Override
            public void onFinish() {
                super42 = false;
            }
        }.start();
    }

    private static long lasttime_42 = 0;
    private static long nowtime_42 = 0;

    private void tempplaying42(Context con, String sheshidu) {
        if (getSuperTempIsSwitchswtich(con)) {

            try {
                nowtime_42 = (System.currentTimeMillis());
                int hours = (int) ((nowtime_42 - lasttime_42) / (1000 * 60));
                System.out.println("两个时间之间的小时差为：" + hours);
                if (hours < 30) {
                    super42 = true;
                    show42Tempflag = true;
                } else {
                    super42 = false;
                }
            } catch (Exception e) {
                super42 = false;
                show42Tempflag = true;
            }

//            if (!(boolean) SPUtils.get(homecontext, "superisRing", false)) {
            if ((!TempPlayingActivity.isRingremue) && (!super42)) {
                // startdismisscount42();

                lasttime_42 = (System.currentTimeMillis());

                SPUtils.put(homecontext, "superisRing", true);
                Intent tempIntent = new Intent();
                tempIntent.setClass(con, TempPlayingActivity.class);
                homecontext.startActivity(tempIntent);
            } else {

                if (show42Tempflag) {

                    showTempWarning(sheshidu);
                }

            }

        }
    }

    private boolean kicktemp = false;

    private void kickstartdismisscount() {
        CountDownTimer countDownTimer = new CountDownTimer(1000 * 60 * 10 + 1050, 1000 * 60) {
            @Override
            public void onTick(long millisUntilFinished) {
                kicktemp = true;
            }

            @Override
            public void onFinish() {
                kicktemp = false;
            }
        }.start();
    }

    private void kickpalying(Context con, float kickValuesInt) {
        SharedPreferences pres = homecontext.getSharedPreferences("kickalarm", Context.MODE_PRIVATE);
        boolean tem = pres.getBoolean("kickIsSwitch", false);
        Log.e("hjs", "kickIsSwitch==" + tem);
        float values = getkickIsSwitchtemp();
        if (tem) {
            if (kickValuesInt < values) {
                if (!(boolean) SPUtils.get(homecontext, "dengbei", false)) {
                    if ((!KickPlayingActivity.kickremue) && (!kicktemp)) {
                        kickstartdismisscount();
                        SPUtils.put(homecontext, "dengbei", true);
                        Intent Kickintent = new Intent();
                        Kickintent.setClass(con, KickPlayingActivity.class);
                        homecontext.startActivity(Kickintent);
                    }
                } else {
                    if (isKick == 0) {
                        isKick = 1;
                        myhomehan.postDelayed(new Runnable() {

                            public void run() {
                                kicksubtime1min = kicksubtime10min;
                                SPUtils.put(homecontext, "dengbei", false);
                                isKick = 0;
                            }

                        }, kicksubtime1min);// 600000十分钟
                    }
                }

            }
        }
    }


    /***
     * 记录孩子温度
     * @param sheshidu
     */
    public void maxMinTemperature(Context con, String sheshidu) {
        if (!TextUtils.isEmpty(sheshidu)) {
            if (sheshidu.contains("℃")) {
                try {
                    String tempare = sheshidu.replaceAll("℃", "");
                    tempare = tempare.replaceAll(" ", "");
                    courntTempera = Double.valueOf(tempare);

                    try {
                        int temphjwd = Integer.valueOf(hjwendu.replaceAll("℃", ""));
                        int barrary = 100;
                        if (!TextUtils.isEmpty(BleService.batteryPower)) {
                            barrary = Integer.valueOf(BleService.batteryPower);
                        } else {
                            if (!semdbattasy) {
                                sendCmd(BLEConfig.READ_DEVISE_POWER_CMD, null);
                                semdbattasy = true;
                            }
                        }
                        if (courntTempera >= 37.5) {
                            post_thermometer_record(true, Float.valueOf(tempare), temphjwd, barrary);
                            tv_home_child_tips.setText(R.string.fever);
                        } else {
                            post_thermometer_record(false, Float.valueOf(tempare), temphjwd, barrary);
                            tv_home_child_tips.setText("您的体温在正常范围 (*^__^*)");
                        }
                    } catch (Exception e) {
                        tv_home_child_tips.setText("您的体温在正常范围 (*^__^*)");
                    }
                    if (courntTempera >= 42) {
                        AlarmNotificationManager.showHighTempNotification(homecontext, sheshidu);
                        tempplaying42(con, sheshidu);
                    } else {


                        float getsettemp = getsettemp();
                        if ((getsettemp > 0 && (courntTempera > getsettemp))) {
                            if (!getTempIsSwitchswtich()) return;

                            //String strbody = String.valueOf(courntTempera);
                            // Float fbody = Float.valueOf(strbody);

                            tempplaying(con, tempare);
                            {
//                                AlarmNotificationManager.showHighTempNotification(homecontext, sheshidu);
//                            MediaPlayer mp = new MediaPlayer();
//                            try {
//                                mp.setDataSource(homecontext, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//                                mp.prepare();
//                                mp.start();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }

                            }
                        }
                        ;
                    }

                    if (courntTempera > maxTemperature) {
                        maxTemperature = courntTempera;
                        if (tv_home_child_max != null) tv_home_child_max.setText(sheshidu);

                        LogUtil.inf("" + maxTemperature);
                        if (maxTemperature >= highTemp) {
                            // AlarmNotificationManager.showHighTempNotification(homecontext,sheshidu);
//                            MediaPlayer mp = new MediaPlayer();
//                            try {
//                                mp.setDataSource(homecontext, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//                                mp.prepare();
//                                mp.start();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
                            ChildSummary childsummer = queryChildSummary();
                            if (childsummer == null) {
                                childsummer = new ChildSummary();
                                childsummer.setMax(sheshidu);
                            } else {
                                childsummer.setMax(sheshidu);
                            }
                            saveorupdate(childsummer);
                        }
                    }

                    if (minTemperaTure == 0.0) {
                        minTemperaTure = courntTempera;
                    }
                    if (courntTempera < minTemperaTure) {
                        minTemperaTure = courntTempera;
                        if (tv_home_child_min != null) tv_home_child_min.setText(sheshidu);

                        ChildSummary childsummer = queryChildSummary();
                        if (childsummer == null) {
                            childsummer = new ChildSummary();
                            childsummer.setMin(sheshidu);
                        } else {
                            childsummer.setMin(sheshidu);
                        }
                        saveorupdate(childsummer);
                    } else if (minTemperaTure == courntTempera) {
                        if (tv_home_child_min != null) tv_home_child_min.setText(sheshidu);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void saveorupdate(ChildSummary child) {
        ChildSummary first = null;
        try {
            first = EApp.getApp().db.findFirst(ChildSummary.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (first != null) {
            child.setId(first.getId());
        }
        try {
            EApp.getApp().db.saveOrUpdate(child);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 有东西输出的时候
     */
    private void onTips(String operate, String tips) {
        //tv_home_temp_operator.setClickable(false);
        LogUtil.inf("==========" + operate + tips);
//        if( unbind ){
//            LogUtil.inf("======unbind=====");
//            tv_home_temp_operator.setText("扫描体温计");
//            unbind =false;
//        }else {
        tv_home_temp_operator.setText(operate);
        tv_home_temp_tips_shower.setText(tips);
//        }
    }

    private static boolean startscan = true;

    /**
     * 扫描体温计
     */
    private void startScan() {
        Log.e("hjs", "homeActivity startScan " + (BluetoothAdapter.getDefaultAdapter().isEnabled()));

//        String mac = getremebermac();
//        if ((mac.length() == 12) && startscan) {
//            StringBuffer sb = new StringBuffer(mac);
//            for (int i = 2; i < sb.length(); i += 3) {
//                sb.insert(i, ":");
//            }
//            mac = (sb.toString().toUpperCase());
//            sendCmd(BLEConfig.MAC_CONNECT_CMD, mac);
//        } else
            {
            if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                if((!TextUtils.isEmpty(phonemac))&&(getswitch())){
                    sendCmd(BLEConfig.MAC_CONNECT_CMD, phonemac);
                    Log.e("hjs", "只扫描 phonemac "+phonemac);
                }else {
                    sendCmd(BLEConfig.BLE_SCAN_CMD, null);//扫描体温计
                }
                tv_home_temp_operator.setText("扫描中...");
                tv_home_temp_operator.setClickable(false);
                startRotateAnim();
            } else {
                Toast.makeText(this, "请先打开蓝牙", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String tempaddress;
    private static int connecttime = 0;

    /**
     * 蓝牙服务事件回调函数
     *
     * @param bleEvent 事件体
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEventMainThread(BleEvent bleEvent) {
        int code = bleEvent.getCode();
        Bundle bundle = bleEvent.getExtra();
        String extra = "";
        //LogUtil.inf("==onMessageEventMainThread====code====="+code);
        if (bundle != null) {
            extra = bundle.getString(BLEConfig.MSG_KEY);
        }
        switch (code) {
            case BLEConfig.BLE_IS_SCANNING: //正在扫描
                LogUtil.inf("==BLE_IS_SCANNING");
                onTips("扫描中", extra);
                break;
            case BLEConfig.BLE_NEW_DEVICE:  //发现体温计
                LogUtil.inf("==BLE_NEW_DEVICE");
                if (bundle != null) {
                    device = bundle.getParcelable(BLEConfig.DEVICE_KEY);
                }
                LogUtil.inf("======BLE_NEW_DEVICE=====");
                if (device != null) {
                    String address = device.getAddress();
                    //connectBle(address);
                    tempaddress = address.replace(":", "").toUpperCase();
                    remebermac(tempaddress);
                    remeber_mac.setText(tempaddress);
                }
                break;
            case BLEConfig.BLE_SCAN_FINISH: //扫描结束
               // LogUtil.inf("==BLE_SCAN_FINISH");
                stopRotateAnim();
//                if (device == null) {
//                    resetOperate("重新扫描", "没有发现可用的体温计，请打开体温计重试");
//                } else {
//                    onTips("连接中...", extra);
//                    if (myhomehan != null) myhomehan.removeMessages(10);
//                    if (myhomehan != null) myhomehan.sendEmptyMessageDelayed(10, 1000 * 60);
//                }
                break;
            case BLEConfig.BLE_CONNECTING: //正在连接
                ifbleisconnect = false;
                //onTips("连接中...", extra);
                // if(myhomehan!=null)myhomehan.sendEmptyMessageDelayed(10,1000*40);
                LogUtil.inf("==BLE_CONNECTING");
                break;
            case BLEConfig.BLE_CONNECT_ERROR:
                LogUtil.inf("BLE_CONNECT_ERROR重新扫描22=====");
                //onTips("重新扫描", extra);
                // resetOperate("重新扫描", extra);
                break;
            case BLEConfig.BLE_CONNECTED://蓝牙服务正常连接了
                LogUtil.inf("BLE_CONNECTED=====");
                onTips("读取中...", extra);
                //if(myhomehan!=null)myhomehan.sendEmptyMessageDelayed(20,1000*40);
                connecttime = 0;
                //if(myhomehan!=null)myhomehan.sendEmptyMessageDelayed(3,1000*60);
                break;
            case BLEConfig.BLE_DEVICE_DISCOVERY:   //体温计服务被发现了..数据准备就绪开始监测体温
                LogUtil.inf("BLE_DEVICE_DISCOVERY=====");
                connecttime = 0;
                try {
                    if (tv_home_temp_operator.getText().toString().equalsIgnoreCase("读取中")) {
                        LogUtil.inf("same===============");
                        return;
                    }
                } catch (Exception e) {
                    return;
                }
                //onTips("读取中", extra);
                //if(myhomehan!=null)myhomehan.sendEmptyMessageDelayed(20,1000*60);
                LogUtil.inf("address==" + tempaddress);
                if (!TextUtils.isEmpty(tempaddress)) {
                    try {
                        UserInfo userInfo = getApp().getUserInfo(this);
                        if (userInfo == null) {
                            userInfo = new UserInfo();
                        }

                        if (userInfo != null) {
                            if (userInfo.getMac() != null) {
                                if (!userInfo.getMac().equalsIgnoreCase(tempaddress.replaceAll(":", ""))) {
                                    try {
                                        String tempmac = tempaddress.replaceAll(":", "");
                                        //String name,String gender,String age,String height,String weight
                                        ChildSummary childsummer = queryChildSummary();
                                        if (childsummer != null) {
                                            createchild(userInfo.getUid(), tempmac, childsummer.getName(), childsummer.getGender(), childsummer.getAge(), childsummer.getHeight(), childsummer.getWeight());
                                            userInfo.setUsername(childsummer.getName());
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        userInfo.setMac(tempaddress.replaceAll(":", ""));
                        getApp().setUserInfo(userInfo, this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
            case BLEConfig.BLE_SERVER_DISCONNECTED:  //蓝牙服务断开连接了
                ifbleisconnect = true;
                startscan = true;
                setauto_falg = true;
                LogUtil.inf("BLE_SERVER_DISCONNECTED=====");
                if (BleService.evebleDevice != null)
                    BleManager.getInstance().disconnect(BleService.evebleDevice);
                BleManager.getInstance().disconnectAllDevice();
                resetOperate("重新扫描", extra);

                if (device == null) {
                    resetOperate("重新扫描", extra);
                } else {
                    tv_home_temp_operator.setClickable(true);
                    tv_home_temp_operator.setText("重新连接");
                    tv_home_temp_tips_shower.setText(extra);
                    tv_home_temp_operator.setOnClickListener(v -> {
                        String macAddress = device.getAddress();
                        connectBle(macAddress);
                    });
                }
                startautoConnect();

                MediaPlayer mp = new MediaPlayer();
                try {
                    mp.setDataSource(homecontext, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    mp.prepare();
                    mp.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case BLEConfig.BLE_TEMP_GET:  //得到温度
                setauto_falg = false;
                startscan = true;
                ifwhiletruefailed = true;
                ifbleisconnect = false;
                LogUtil.inf("BLE_TEMP_GET=====");
                stopRotateAnim();
                onTips(extra, "体温监测中");
                LogUtil.inf("BLE_TEMP_GET" + extra);
                courntTempera = 32;
                maxMinTemperature(this, extra);

                break;
            case BLEConfig.BLE_EVM_TEMP_GET_2:  //体温计环境温度
                setauto_falg = false;
                stopRotateAnim();
                try {
                    LogUtil.inf("BLE_hjs_TEMP_GET_2");
                    hjwendu = extra;
                    //onTips(extra, "体温监测中");
                    //Mackicktemo(getCurrActivity(),extra);
                } catch (Exception e) {
                }

                LogUtil.inf("BLE_EVM_TEMP_GET_2=====");
                break;
            case BLEConfig.BLE_DEVICE_NOT_FOUND: //体温计不可用，需要重新扫描
                startscan = false;
                resetOperate("重新扫描", extra);
                LogUtil.inf("BLE_DEVICE_NOT_FOUND=====");
                break;
            case BLEConfig.BLE_OFF_LINE:
                showBleClosedDialog(extra);
                LogUtil.inf("BLE_DEVICE_NOT_FOUND=====");
                break;
            case BLEConfig.BLE_ON_LINE:
                showTips(extra);
                if (device != null) {
                    tempaddress = device.getAddress();

                    LogUtil.info("重连中 address" + tempaddress);
                    onTips("重连中", "蓝牙已重新打开正常尝试重连");
                    connectBle(tempaddress);
                } else {
                    resetOperate("扫描体温计", "蓝牙已打开，请扫描体温计");
                }

                LogUtil.inf("BLE_DEVICE_NOT_FOUND=====");
                break;
            case BLEConfig.BLE_RELEASE_DEVICE: //体温计已解除
                LogUtil.inf("BLE_RELEASE_DEVICE=====");
                resetOperate("扫描体温计", extra);
                //this.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
                break;
            case DownloadConfig.DOWN_LOAD_STARTED:
                LogUtil.inf("DOWN_LOAD_STARTED=====");
                showTips(extra);
                break;
            case DownloadConfig.DOWN_LOAD_FAILED:
                showTips(extra);
                break;
            case DownloadConfig.DOWN_LOAD_SUCCESS:
                showTips("下载成功，请安装");
                if (!TextUtils.isEmpty(extra)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(extra)), "application/vnd.android.package-archive");
                    getCurrActivity().startActivity(intent);
                }
                break;
        }
    }

    private boolean semdbattasy = false;

    public static String hjwendu = "";

    private void createchild(String pid, String mac, String name, String gender, String age, String height, String weight) {
        CreateChildPost post = new CreateChildPost();
        //post.handler(this);
        post.setPid(pid);
        //post.setPortrait(localPic);
        post.setThermometer_add(mac);
        post.setName(name);
        post.setGender(gender);
        post.setAge(age);
        post.setHeight(height);
        post.setWeight(weight);
        post.post();

    }

    String Thermometer_mac;
    private static int post_thermometer_record_time;///上传记录次数

    private void post_thermometer_record(boolean ifsupertemp, float temperature, int rommtemp, int batt) {
        if ((post_thermometer_record_time < 0) || (post_thermometer_record_time > 100)) {
            post_thermometer_record_time = 0;
        }
        post_thermometer_record_time++;
        if ((post_thermometer_record_time >= 12) || ifsupertemp) {
//        if(true){
            post_thermometer_record_time = 0;
            CreateThermometerRecord post = new CreateThermometerRecord();
            post.code(0x0031);
            post.handler(this);
            post.setBase_station_mac("000000000000000000000000");
            if (device != null) {
                if (TextUtils.isEmpty(Thermometer_mac)) {
                    Thermometer_mac = device.getAddress();
                    Thermometer_mac = Thermometer_mac.replaceAll(":", "");
                }
                post.setThermometer_mac(Thermometer_mac);
            }
            post.setPatient_temperature(temperature);
            post.setRoom_temperature(rommtemp);
            post.setBattery_level(batt);

            post.post();
        }
    }

    /**
     * 显示蓝牙断开连接对话框
     *
     * @param tips 内容
     */
    private void showBleClosedDialog(String tips) {
        OperateDialog dialog = new OperateDialog(getCurrActivity());
        dialog.setContent(tips);
        dialog.setOk("打开蓝牙");
        dialog.setCancel("退出使用");
        dialog.setOnCancel(v -> {
            dialog.dismiss();
            currFinish();
        });
        dialog.setOnOk(v -> {
            dialog.dismiss();
            sendCmd(BLEConfig.BLE_OPEN, "");
        });
        int wid = getCurrActivity().getResources().getDimensionPixelOffset(R.dimen.size_300);
        dialog.create(wid, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    /**
     * 显示蓝牙断开连接对话框
     *
     * @param tips 内容
     */
    private void closeBleClosedDialog(String tips) {
        OperateDialog dialog = new OperateDialog(getCurrActivity());
        dialog.setContent(tips);
        dialog.setOk("退出程序");
        dialog.setCancel("退出使用");
        dialog.setOnCancel(v -> {
            dialog.dismiss();
            currFinish();
        });
        dialog.setOnOk(v -> {
            dialog.dismiss();
            sendCmd(BLEConfig.BLE_OPEN, "");
        });
        int wid = getCurrActivity().getResources().getDimensionPixelOffset(R.dimen.size_300);
        dialog.create(wid, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }


    /**
     * 发送命令
     */
    private void sendCmd(int code, String extra) {
        bleEvent.setCode(code);
        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(extra)) {
            bundle.putString(CMD_EXTRA, extra);
        }
        bleEvent.setExtra(bundle);
        EventBus.getDefault().post(bleEvent);
    }

    /**
     * 初始化标题栏信息
     */
    private void initTitleBar() {
        drawer_home_holder = (DrawerLayout) getView(R.id.drawer_home_holder);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(getCurrActivity(), drawer_home_holder, R.string.open, R.string.close);
        actionBarDrawerToggle.syncState();
        drawer_home_holder.addDrawerListener(actionBarDrawerToggle);
        Toolbar tool_bar_home_title = (Toolbar) getView(R.id.tool_bar_home_title);
        tool_bar_home_title.setTitleTextColor(Tools.getColor(getCurrActivity(), R.color.white));
        tool_bar_home_title.setTitle(R.string.app_name);
        setSupportActionBar(tool_bar_home_title);
        tool_bar_home_title.setNavigationIcon(R.mipmap.ic_home_menu);
        tool_bar_home_title.setNavigationOnClickListener((View v) -> drawer_home_holder.openDrawer(GravityCompat.START));
        tool_bar_home_title.setOnMenuItemClickListener((MenuItem item) -> {
            switch (item.getItemId()) {
                case R.id.title_bar_home_user:
                    UserInfo userInfo = getApp().getUserInfo(getCurrActivity());
                    if (userInfo != null) {
                        String uid = userInfo.getUid();
                        if (TextUtils.isEmpty(uid)) {
                            showTips("请先登录");
                            login();
                        } else {
                            Intent intent = getIntent(ChildListActivity.class);
                            intent.putExtra("uid", uid);
                            startActivity(intent);
                        }
                    } else {
                        showTips("请先登录");
                        login();
                    }
                    break;
                case R.id.title_bar_home_device:
                    gotoDeviceDetail();
                    break;
            }
            return true;
        });
    }

    private final int LOGIN_CODE = 0x0099;

    /**
     * 登录
     */
    private void login() {
        Intent intent = getIntent(LoginActivity.class);
        startActivityForResult(intent, LOGIN_CODE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            String cid = intent.getStringExtra("cid");
            if (!TextUtils.isEmpty(cid)) {
                //summaryGet(cid);
                sendCmd(BLEConfig.CHILD_ID_CMD, cid);//切换孩子
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == DEVICE_DETAIL) { //解除了体温计
                ifbleisconnect = true;
                boolean unbind = data.getBooleanExtra("unbind", false);
                if (unbind) {
                    device = null;
                    LogUtil.info("体温计被解除了");
                    Log.e("hjs_home", "onActivityResult");
                    sendCmd(BLEConfig.BLE_DISCONNECT_CMD, null);//连接
                }
            } else if (requestCode == LOGIN_CODE) {
                UserInfo info = EApp.getApp().getUserInfo(getCurrActivity());
                if (info != null) {
                    String uid = info.getUid();
                    if (!TextUtils.isEmpty(uid)) {
                        tv_home_log_out.setVisibility(View.VISIBLE);
                        if (TextUtils.isEmpty(info.getMac())) {
                            //dialogshow(uid);
                        }
                    } else {
                        tv_home_log_out.setVisibility(View.GONE);
                    }
                } else {
                    tv_home_log_out.setVisibility(View.GONE);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private Animation connectRotaAni = null;

    /**
     * 开始 连接蓝牙旋转动画
     */
    private void startRotateAnim() {
        if (connectRotaAni == null) {
            connectRotaAni = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            connectRotaAni.setDuration(2000); //周期2秒
            connectRotaAni.setInterpolator(new LinearInterpolator());// 匀速
            connectRotaAni.setRepeatCount(-1);
        }
        circle_view_home_temp_indicator.setAnimation(connectRotaAni);
        connectRotaAni.start();
    }

    /**
     * 结束  连接蓝牙旋转动画
     */
    private void stopRotateAnim() {
        if (circle_view_home_temp_indicator != null)
            circle_view_home_temp_indicator.clearAnimation();
    }

    /**
     * 开启光圈动画
     */
    private void startAperture() {
        final ObjectAnimator animatorAlp = ObjectAnimator.ofFloat(iv_home_indicator_bg, "alpha", 1f, 0.2f, 1f);
        animatorAlp.setDuration(3000);
        animatorAlp.setRepeatCount(-1);
        animatorAlp.setInterpolator(new LinearInterpolator());
        animatorAlp.setRepeatMode(ValueAnimator.RESTART);
        animatorAlp.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_toolbar_menu, menu);
        return true;
    }

    /**
     * 显示添加孩子对话框
     */
    private void showAddChildDialog() {
        OperateDialog dialog = new OperateDialog(getCurrActivity());
        dialog.setContent("未添加默认孩子，只能做实时监测，无法进行异地监测和健康分析。现在去设置默认绑定？");
        dialog.setOk("去绑定");
        dialog.setCancel("本地监测");
        dialog.setOnOk(v -> {
            dialog.dismiss();
            gotoActivity(ChildListActivity.class);
        });
        dialog.setCancelable(false);
        int wid = getResources().getDimensionPixelOffset(R.dimen.size_300);
        dialog.create(wid, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        Log.e("hjs", "onDestroy");
        semdbattasy = false;
        super42 = false;
        show42Tempflag = true;
        showTempflag = true;
        gaowentemp = false;

//        device = null;
//        try {
//            MonitorActivity.stopMonitor();
//            EventBus.getDefault().unregister(homecontext);
//            sendCmd(BLEConfig.STOP_SERVICE_CMD, null);//结束服务
//        }catch (Exception e){
//
//        }
        super.onDestroy();
    }

    private void gotoDeviceDetail() {
        if (device == null) {
            showTips("请先选择体温计进行连接");
            return;
        }
        String bleName = device.getName();
        String macAddress = device.getAddress();
        Intent intent = getIntent(DeviceActivity.class);
        intent.putExtra("name", bleName);
        intent.putExtra("mac", macAddress);
        startActivityForResult(intent, DEVICE_DETAIL);
    }

    private final int DEVICE_DETAIL = 0x00081;

    private final int SUMMARY_GET = 0x0018;

    private void summaryGet(String cid) {
        ChildSummaryGet get = new ChildSummaryGet();
        get.code(SUMMARY_GET);
        get.handler(this);
        get.setCid(cid);
        get.get();
    }

    @Override
    public void handleStart(int code) {
        if (code == SUMMARY_GET) {
            LogUtil.info("获取孩子概况开始");
        } else if (code == CHECK_VERSION) {
            tv_home_check_new_version.setClickable(false);
        }
    }

    @Override
    public void handleResult(String result, int code) {
        if (code == SUMMARY_GET) {
            ChildSummaryRes res = Tools.json2Bean(result, ChildSummaryRes.class);
            if (res.isOk()) {
                ChildSummary data = res.getData();
                String name = data.getName();
                String portrait = data.getPortrait();
                String height = data.getHeight();
                String weight = data.getWeight();
                String age = data.getAge();
                String gender = data.getGender();
                String max = data.getMax();
                String min = data.getMin();
                String count = data.getCount();
                String tips = data.getTips();
                Tools.display(circle_home_child_portrait, portrait);
                tv_home_child_name.setText(name);
                tv_home_child_height.setText(height);
                tv_home_child_weight.setText(weight);
                tv_home_child_age.setText(age);
                tv_home_child_gender.setText(gender);
                tv_home_child_max.setText(max);
                tv_home_child_min.setText(min);
                tv_home_child_count.setText(count);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tv_home_child_tips.setText(Html.fromHtml(tips, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    //noinspection deprecation
                    tv_home_child_tips.setText(Html.fromHtml(tips));
                }
            } else {
                showTips(res.msg());
            }
        } else if (code == CHECK_VERSION) {
            VersionRes res = Tools.json2Bean(result, VersionRes.class);
            if (res.isOk()) {//有新版本
                VersionData data = res.getData();
                showUpdate(data);
            } else {
                showTips(res.msg());
            }
        } else if (code == ChECK_MONITOR) {
            try {
                MonitorRes res = Tools.json2Bean(result, MonitorRes.class);
                LogUtil.info("getFever_times===" + res.getMessage().getFever_times());
                String fstime = res.getMessage().getFever_times();
                tv_home_child_count.setText(fstime + "次");
            } catch (Exception e) {

            }

        }
//        else  if(code == ADD_MONITOR){
//            QueryMonitorRes res = Tools.json2Bean(result, QueryMonitorRes.class);
//            if (res.isOk()) {
//                String temp  = res.getBody_temperature();
//                if(!TextUtils.isEmpty(temp)){
//                    sendCmd(BLEConfig.BLE_TEMP_GET, (temp + "℃"));//
//                    UserInfo   userInfo =EApp.getApp().getUserInfo(getCurrActivity());
//                    userInfo.setMac(phonemac);
//                    //userInfo.setCid(""+et_login_phone.getText().toString());
//                    //EApp.getApp().setUserInfo(userInfo, getCurrActivity());
//
//                }
//            }else {
//                showTips(res.msg());
//            }
//        }
    }

    private boolean isFront = false;

    @Override
    protected void onPause() {
        super.onPause();
        isFront = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                finish();
//                System.exit(0);
//                android.os.Process.killProcess(android.os.Process.myPid());
////                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getApplication().getPackageName());
////                LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                startActivity(LaunchIntent);
//            }
//        }, 20000);// 1秒钟后重启应用

        isFront = true;
        ChildSummary childsummer = queryChildSummary();
        Log.e("hjs", "childsummer=" + childsummer);
        if (childsummer != null) {
            if (childsummer.getName() != null) {
                tv_home_child_name.setText(childsummer.getName());

                try {
                    UserInfo userInfo = getApp().getUserInfo(this);
                    if (userInfo == null) {
                        userInfo = new UserInfo();
                    }
                    userInfo.setUsername(childsummer.getName());
                    getApp().setUserInfo(userInfo, this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (childsummer.getHeight() != null)
                tv_home_child_height.setText(childsummer.getHeight());
            if (childsummer.getWeight() != null)
                tv_home_child_weight.setText(childsummer.getWeight());
            if (childsummer.getAge() != null) tv_home_child_age.setText(childsummer.getAge());
            if (childsummer.getGender() != null) {
                if (childsummer.getGender().equalsIgnoreCase("1")) {
                    tv_home_child_gender.setText("男");
                } else {
                    tv_home_child_gender.setText("女");
                }
            }
        } else {
            tv_home_child_height.setText("");
            tv_home_child_weight.setText("");
            tv_home_child_age.setText("");
            tv_home_child_gender.setText("");
        }


        try {
            UserInfo userInfo = getApp().getUserInfo(this);
            if (userInfo == null) {
                userInfo = new UserInfo();
            }
            if ((userInfo.getUid() != null)) {
                addMonitor(userInfo.getUid(), userInfo.getMac());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void showUpdate(VersionData data) {
        String vName = data.getvName();
        String path = data.getPath();
        OperateDialog dialog = new OperateDialog(getCurrActivity());
        dialog.setOk("开始下载");
        dialog.setCancel("稍候再说");
        dialog.setContent("最新版本已更新至" + vName + "，请及时更新。");
        dialog.setOnOk(v -> {
            dialog.dismiss();
            sendCmd(DownloadConfig.DOWN_LOAD_START_CMD, path);
        });
        int wid = getCurrActivity().getResources().getDimensionPixelOffset(R.dimen.size_300);
        dialog.create(wid, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    @Override
    public void handleFinish(int code) {
        if (code == SUMMARY_GET) {
            LogUtil.info("获取孩子概况结束");
        } else if (code == CHECK_VERSION) {
            tv_home_check_new_version.setClickable(true);
        }
    }

    @Override
    public void handleError(int code) {
        if (code == SUMMARY_GET) {
            LogUtil.info("获取孩子概况错误");
        } else if (code == CHECK_VERSION) {
            showTips("检查更新出错，请重试");
        }
    }

    private long exitTime = 0;

    /**
     * 回退
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000) { // System.currentTimeMillis()无论何时调用，肯定大于2000

                Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                //ToastUtil.show(this, "再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                return super.onKeyDown(keyCode, event);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private ChildSummary queryChildSummary() {
        try {
            ChildSummary first = null;
            try {
                first = EApp.getApp().db.findFirst(ChildSummary.class);
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (first != null) LogUtil.inf(first.toString());
            //添加查询条件进行查询
            return first;
        } catch (Exception e) {
            return null;
        }
    }


    public void Mackicktemo(Context ton, String extra) {
        try {
            emvcourntTempera = Double.valueOf(extra.replaceAll("℃", ""));
        } catch (Exception e) {
        }

        if (!getkickalarmswtich()) {
            return;
        }

        float kicktemp = getkickIsSwitchtemp();
        if ((kicktemp > 0 && (emvcourntTempera < kicktemp))) {
            String strroom = String.valueOf(emvcourntTempera);
            Float froom = Float.valueOf(strroom);
            kickpalying(ton, froom);
            AlarmNotificationManager.showKickTempNotification(homecontext, "" + extra);
//            MediaPlayer mp = new MediaPlayer();
//            try {
//                mp.setDataSource(homecontext, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//                mp.prepare();
//                mp.start();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            // myhomehan.sendEmptyMessageDelayed(3,1000*60*10);
        }
    }


    class MyHandler extends Handler {
        WeakReference<Activity> mWeakReference;

        MyHandler(Activity activity) {
            mWeakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final Activity activity = mWeakReference.get();
            Log.e("hjs", "msg.============" + msg.what);
            if (activity != null) {
                if (msg.what == 1) {
                } else if (msg.what == 2) {
                    Log.e("hjs", "msg.what==2==");
//                    if(!getkickalarmswtich()){
//                        return;
//                    }
//
//                    float kicktemp  =getkickIsSwitchtemp();
//                      if((kicktemp>0 && (emvcourntTempera<kicktemp))){
//
//                        kickpalying((float) emvcourntTempera);
//                        AlarmNotificationManager.showKickTempNotification(activity,"");
//                        MediaPlayer mp = new MediaPlayer();
//                        try {
//                            mp.setDataSource(activity, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//                            mp.prepare();
//                            mp.start();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                          myhomehan.sendEmptyMessageDelayed(3,1000*60*10);
//                    }
                } else if (msg.what == 3) {
                    Log.e("hjs", "msg.what==3==");
                    if (!getkickalarmswtich()) {
                        //myhomehan.sendEmptyMessageDelayed(3,1000*60*10);
                        return;
                    }
//                    if(courntTempera>20) {
//                        sendCmd(BLEConfig.BLE_EVM_TEMP_GET, null);
//                    }
                } else if (msg.what == 1111) {
                    try {
                        String textmsg = tv_home_temp_operator.getText().toString();
                        if (textmsg.startsWith("读取中") || (textmsg.startsWith("连接中")) || (courntTempera <= 0)) {
                            if (device != null) {
                                String address = device.getAddress();
                                connectBle(address);
                                tempaddress = address.replace(":", "").toUpperCase();
                                myhomehan.sendEmptyMessageDelayed(20, 1000 * 20);
                            } else {
                                resetOperate("重新扫描", "");
                            }
                            // resetOperate("重新扫描","");
//                        myhomehan.removeMessages(10);
//                        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getApplication().getPackageName());
//                        LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(LaunchIntent);
                        } else {
                            myhomehan.removeMessages(10);
                        }
                    } catch (Exception e) {
                        resetOperate("重新扫描", "");
                    }

                } else if (msg.what == 2222) {
                    try {
                        String textmsg = tv_home_temp_operator.getText().toString();
                        if (textmsg.startsWith("读取中") || (textmsg.startsWith("连接中")) || (courntTempera <= 0)) {
//                            onDestroy();
                            myhomehan.removeMessages(20);
                            resetOperate("重新扫描", "");
//                            Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getApplication().getPackageName());
//                            LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(LaunchIntent);
//                            currFinish();

                        }
                    } catch (Exception e) {
//                        onDestroy();
                        myhomehan.removeMessages(20);
                        resetOperate("重新扫描", "");
//                        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getApplication().getPackageName());
//                        LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(LaunchIntent);
                    }
                } else if (msg.what == 30) {
                    isPlay = 2;
                    SPUtils.put(homecontext, "isRing", true);
                    myhomehan.sendEmptyMessageDelayed(40, 1000 * 60 * 10);
                } else if (msg.what == 40) {
                    isPlay = 0;
                    SPUtils.put(homecontext, "isRing", false);
                } else if (msg.what == 50) {

                    subtime1min42 = subtime10min42;
                    SPUtils.put(homecontext, "superisRing", false);
                    isSuperPlay = 0;
                    Log.e("hjs", "判断isplayrun方法执行");

                }
            }
        }
    }


    private final int ChECK_MONITOR = 0x0146;

    private void addMonitor(String uid, String mac) {
        QueryMonitorPost post = new QueryMonitorPost();
        post.code(ChECK_MONITOR);
        post.handler(this);
        post.setThermometerID(mac);
        post.setPid(uid);
        //post.setUsername(username);
        post.post();
    }

    private boolean setauto_falg = true;
    private void startautoConnect(){

        new Thread(() -> {
            while (setauto_falg) {
                Log.d("hjs", " setauto_falg :"+setauto_falg);
                try {
                    Thread.sleep(1000 * 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Log.e("hjs", "BleService.evebleDevice!=null" + (BleService.evebleDevice!=null));
                    if(BleService.evebleDevice!=null) {
                        if (!BleManager.getInstance().isConnected(BleService.evebleDevice)) {
                            Log.d("hjs", "  String macAddress = device.getAddress();=" + device);
                            device = BleService.evebleDevice.getDevice();
                            String macAddress = device.getAddress();
                            Log.d("hjs", "macAddress:" + macAddress);
                            phonemac = macAddress;
                            remebermac(macAddress.replaceAll(":", ""));
                            connectBle(macAddress);
                            ifwhiletruefailed = true;
                        }
                    }else {
                        Log.d("hjs", " startScan");
                        String mac = getremebermac();
                        Log.d("hjs", " mac:" + mac);
                        if ((mac.length() == 12) && startscan) {
                            StringBuffer sb = new StringBuffer(mac);
                            for (int i = 2; i < sb.length(); i += 3) {
                                sb.insert(i, ":");
                            }
                            mac = (sb.toString().toUpperCase());
                            phonemac = mac;
                            sendCmd(BLEConfig.MAC_CONNECT_CMD, mac);
                        }
                    }
                } catch (Exception e) {
                  e.printStackTrace();
                    Log.d("hjs", " e.printStackTrace()");
                }

                try {
                    Thread.sleep(1000 * 60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    public static  String IP = "www.zhengqidun.com";
    public static final String HOST_Update_APK = "http://"+IP+":8080/csm-web/download/app/wzkt/wzkt.apk";
    public static final String HOST_Update_JSON = "http://"+IP+":8080/csm-web/download/app/wzkt/wzkt.json";


    int newVerCode;
    public void getServerVer(final Context context) {

        OkHttpManager okHttpManager;
        okHttpManager = OkHttpManager.getInstance();
        okHttpManager.asyncJsonStringByURL(HOST_Update_JSON, new OkHttpManager.StringCallBack() {
            @Override
            public void onResponse(String result) {
                //Log.e("hjs","getServerVer="+result);
               LogUtil.inf("getServerVer="+result);
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, Object>>() {}.getType();
                Map<String, Object> datas = gson.fromJson(result.replace("[","").replace("]",""), type);

                Double oo=  (Double)datas.get("verCode");
                newVerCode=oo.intValue();

                int verCode = getVerCode(getApplicationContext());
                if (newVerCode > verCode) {
                    showUpdateDialog();
                } else {
                }
            }
            @Override
            public void onFailed() {
                LogUtil.inf("getServerVer= failed");
            }
        });
    }
    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
           return BuildConfig.VERSION_CODE;
           // verCode = context.getPackageManager().getPackageInfo("com.wzkt.eva", 0).versionCode;
        } catch (Exception e) {
            LogUtil.inf(e.getMessage());
        }
        return verCode;
    }

    /**
     * 有新版本
     */
    private void showUpdateDialog() {
        if(!isServiceRunning(this,"UpdateService")) {
            Intent intent = new Intent(HomeActivity.this, UpdateService.class);
            intent.putExtra("Key_App_Name","wzkt" );
            intent.putExtra("Key_Down_Url", HOST_Update_APK);
            startService(intent);
        };
    }
    /**
     * 校验某个服务是否还存在
     */
    public static boolean isServiceRunning(Context context,String serviceName){
        // 校验服务是否还存在
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : services) {
            // 得到所有正在运行的服务的名称
            String name = info.service.getClassName();
            if (serviceName.equals(name)) {
                return true;
            }
        }
        return false;
    }



//    public void createDialog() {
//        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
//        alert.setTitle("软件升级").setMessage("蓝牙体温计发现新版本,请更新使用.")
//                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // TODO Auto-generated method stub
//                dialog.dismiss();
//            }
//
//        });
//        alert.create().show();
//    }



}