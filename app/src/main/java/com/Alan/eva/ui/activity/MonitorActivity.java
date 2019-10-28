package com.Alan.eva.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.UserHandle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.Alan.eva.R;
import com.Alan.eva.config.BLEConfig;
import com.Alan.eva.http.core.IResultHandler;
import com.Alan.eva.http.get.MonitorGet;
import com.Alan.eva.http.post.QueryMonitorPost;
import com.Alan.eva.model.ChildSummary;
import com.Alan.eva.model.UserInfo;
import com.Alan.eva.result.MonitorRes;
import com.Alan.eva.result.QueryMonitorRes;
import com.Alan.eva.tools.LogUtil;
import com.Alan.eva.tools.SPUtils;
import com.Alan.eva.tools.SpTools;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.tools.alarm.AlarmNotificationManager;
import com.Alan.eva.ui.EApp;
import com.Alan.eva.ui.core.AbsActivity;
import com.Alan.eva.ui.dialog.MacInputDialog;
import com.Alan.eva.ui.dialog.MyAutoDialogUtil;

import org.xutils.ex.DbException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by CW on 2017/3/20.
 * 监测页面
 */
public class MonitorActivity extends AbsActivity implements IResultHandler, Callback,View.OnClickListener{
    private AppCompatTextView tv_monitor_temp_data;
    private AppCompatTextView tv_monitor_temp_tips;
    /*循环请求机*/
    private static ScheduledExecutorService scheduledExecutor;
    private final int LOOPER_CODE = 0x0099;
    private String monitorId;

    private String uid,mac,username;
    private AppCompatTextView tv_monitor_temp_room,tv_monitor_temp_power;
    private AppCompatButton tv_monitor_add,tv_monitor_his;

    Context homecontext;
    HomeActivity homecext;

    private float maxTemperature,minTemperaTure,courntTempera,emvcourntTempera;

    @Override
    public Activity getCurrActivity() {
        return this;
    }

    @Override
    public int getRootViewId() {
        return R.layout.ac_monitor_temp;
    }

    @Override
    public void findView(View rootView) {
        Toolbar tool_bar_home_title = (Toolbar) getView(R.id.tool_bar_monitor_title);
        tool_bar_home_title.setTitleTextColor(Tools.getColor(getCurrActivity(), R.color.white));
        tool_bar_home_title.setTitle(R.string.monitor);
        setSupportActionBar(tool_bar_home_title);
        tool_bar_home_title.setNavigationIcon(R.mipmap.ic_flag_back);
        tool_bar_home_title.setNavigationOnClickListener((View v) -> currFinish());
        tv_monitor_temp_data = (AppCompatTextView) getView(R.id.tv_monitor_temp_data);
        tv_monitor_temp_tips = (AppCompatTextView) getView(R.id.tv_monitor_temp_tips);
        tv_monitor_temp_room = (AppCompatTextView) getView(R.id.tv_monitor_temp_room);
        tv_monitor_temp_power = (AppCompatTextView) getView(R.id.tv_monitor_temp_power);
        tv_monitor_add= (AppCompatButton) getView(R.id.tv_monitor_add);
        tv_monitor_his= (AppCompatButton) getView(R.id.tv_monitor_his);
        tv_monitor_add.setOnClickListener(this);
        tv_monitor_his.setOnClickListener(this);
        homecontext =this;

        homecext = new HomeActivity();

//        String result = "{\"Message\":{\"count\":1,\"room_temperature\":40,\"body_temperature\":40.0,\"power\":95},\"Code\":2}";
//        MonitorRes res = Tools.json2Bean(result, MonitorRes.class);
        //tv_monitor_temp_data.setText("32");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            mac="";
        UserInfo info = EApp.getApp().getUserInfo(getCurrActivity());
       if(info!=null) {
           uid = info.getUid();
           username =  info.getUsername();
           if(TextUtils.isEmpty(username)){
               username="defalt";
           }
           LogUtil.info("info.getUid()=" + info.getUid());
           LogUtil.info("info.getMac()=" + info.getMac());
           mac = info.getMac();
           Intent intent = getIntent();
           LogUtil.info("intent=" + (intent != null));
           if (intent != null) {
               monitorId = intent.getStringExtra("cid");
           }
           LogUtil.info("monitorId=" + monitorId);

           dialogshow(uid);

//           if (!TextUtils.isEmpty(monitorId)) {
//               startMonitor();
//           } else {
//               LogUtil.info("info.getMac()=" + info.getMac());
//               if (TextUtils.isEmpty(info.getMac())) {
//                   dialogshow(uid);
//               } else {
//                   mac = info.getMac();
//                   if ((mac != null) && (TextUtils.isEmpty(mac))) {
//                       startMonitor();
//                   }
//               }
//           }

//           if ((!TextUtils.isEmpty(uid)) && (!TextUtils.isEmpty(mac))) {
//               startMonitor();
//           }
       }


//        String  result = "{\"Message\":{\"count\":1,\"room_temperature\":32,\"body_temperature\":32.0,\"power\":29},\"Code\":1}";
//        MonitorRes res = Tools.json2Bean(result, MonitorRes.class);
//        LogUtil.info("res==="+res.getMessage().getBody_temperature());
    }

    String phonemac;
    private void dialogshow(String uid){
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
                mac = phonemac;
                UserInfo info = EApp.getApp().getUserInfo(getCurrActivity());
                info.setMac(mac);
                EApp.getApp().setUserInfo(info,this);
                //addMonitor(uid, phonemac);
                stopMonitor();
                startMonitor();
            });
            inputDialog.setOnCancel(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //currFinish();
                    inputDialog.dismiss();
                }
            });

            int wid = getCurrActivity().getResources().getDimensionPixelOffset(R.dimen.size_300);
            inputDialog.create(wid, ViewGroup.LayoutParams.WRAP_CONTENT);
            inputDialog.show();
        if(!TextUtils.isEmpty(mac))inputDialog.setContent(""+mac);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stopMonitor();
    }

    /**
     * 启动监护
     */
    private void startMonitor() {
        if (scheduledExecutor == null) {
            scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        }

            Handler monitorHandler = new Handler(this);
            scheduledExecutor.scheduleWithFixedDelay(() ->
                    monitorHandler.sendEmptyMessage(ADD_MONITOR), 0, 30, TimeUnit.SECONDS);

    }

    /**
     * 停止监护
     */
    public static void stopMonitor() {
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdown();
        }
        scheduledExecutor = null;
    }

    private MonitorGet monitorGet;
    private final int MONITOR_CODE = 0x0088;

    @Override
    public void handleStart(int code) {
        if (code == MONITOR_CODE) {
            LogUtil.info("异地监测开始了");
        }
    }

    @Override
    public void handleResult(String result, int code) {
        if (code == MONITOR_CODE) {
            LogUtil.info("异地监测开始了");
            MonitorRes res = Tools.json2Bean(result, MonitorRes.class);
            if (res.isOk()) {
                String temp = res.getData();
//                if(!TextUtils.isEmpty(temp)){

//                   String textmon =   tv_monitor_temp_data.getText().toString();
//                   int oldwendu = Integer.valueOf(textmon);
                  // int newwendu =   Integer.valueOf(temp);
                   //if(newwendu>0) {
                       tv_monitor_temp_data.setText(temp);
//                   }else {
//                       tv_monitor_temp_data.setText("32");
//                   }
//                }else {
//                    tv_monitor_temp_data.setText("32");
//                }

                tv_monitor_temp_tips.setText(username+"体温监测中\n"+mac);
            } else {
                String msg = res.getMessage().msg();
                tv_monitor_temp_data.setText("--");
                tv_monitor_temp_tips.setText(msg);
            }
        } else if (code == ADD_MONITOR) {
            try {
                //result = "{\"Message\":{\"count\":1,\"room_temperature\":21,\"body_temperature\":40.0,\"power\":95},\"Code\":2}";
                MonitorRes res = Tools.json2Bean(result, MonitorRes.class);
                LogUtil.info("res===" + res.getMessage().getBody_temperature());
                //LogUtil.info("resugetQuerylt==="+res.getQuery().getBody_temperature());
                //QueryMonitorRes     queres = Tools.json2Bean(res.msg(), QueryMonitorRes.class);

//            if (res.isOk()) {
//                String temp = "";
                QueryMonitorRes queryres = res.getMessage();
                if (queryres != null) {
                    String temp = queryres.getBody_temperature();
                    String room = queryres.getRoom_temperature();


                    LogUtil.info("getBody_temperature" + temp);

                    if (!TextUtils.isEmpty(temp)) {
                        UserInfo userInfo = EApp.getApp().getUserInfo(getCurrActivity());
                        if (!TextUtils.isEmpty(phonemac)) {
                            userInfo.setMac(phonemac);
                            EApp.getApp().setUserInfo(userInfo, getCurrActivity());
                        }
                        //userInfo.setCid(""+et_login_phone.getText().toString());

                        LogUtil.info("setUserInfo:" + phonemac);
                        if(temp.startsWith("0.")){
                            return;
                        }

                        tv_monitor_temp_data.setText(temp);

                        tv_monitor_temp_tips.setText("体温监测中\n" + mac);

                        homecext.maxMinTemperature(getCurrActivity(), temp + "℃");
                        //homecext.Mackicktemo(getCurrActivity(),room);

                        if (!TextUtils.isEmpty(queryres.getRoom_temperature())) {
                            tv_monitor_temp_room.setText(queryres.getRoom_temperature() + "℃");
                        }

                        //showLowBatteryWarning();

                        if (!TextUtils.isEmpty(queryres.getPower())) {
                            tv_monitor_temp_power.setText(queryres.getPower() + "%");

                            try {
                                int power = Integer.valueOf(queryres.getPower());


                                if (power <= 20) {
                                    if (getlowerTempIsSwitchswtich()) {
                                        if (!showpower) {
                                            showLowBatteryWarning();
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        Thread.sleep(1000 * 60 * 10);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                    showpower = false;
                                                }
                                            }).start();
                                        }
                                    }
                                }
                            } catch (Exception e) {

                            }
                        }

                    }
                }
            }catch (Exception e){

            }
//            } else {
//                //showTips(getcodeStr(res.code()));
//            }
        }
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

    boolean showpower = false;
    void showLowBatteryWarning() {
        try {
//        MyAutoDialogUtil.showScanNumberDialog(getApplicationContext(),"体温计电量低",R.mipmap.battery);
            AlertDialog dialog = new AlertDialog.Builder(getCurrActivity())
                    .setIcon(R.mipmap.battery)//设置标题的图片
                    .setTitle("体温计电量低")//设置对话框的标题
                    .setMessage("体温计电量低、请充电")//设置对话框的内容
                    //设置对话框的按钮
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
            dialog.show();
            showpower = true;
        }catch (Exception e){

        }
    }



    private String  getcodeStr(int i){
        String info;
      switch (i) {
          case 2:
              info ="手机号正确,但是未绑定体温计,使用体温计MAC地址查询数据";
              break;
          case 3:
              info ="手机号不正确,使用体温计MAC地址查询数据";
              break;
          case -1:
              info ="手机号正确,但是未绑定体温计,体温计MAC地址不正确";
              break;
          case -2:
              info ="手机号和体温计MAC地址均不正确";
              break;
          case -3:
              info ="Request的数据格式不正确";
              break;
          case -4:
              info ="HTTP方法错误";
              break;

      }

      return  "";
    };

    @Override
    public void handleFinish(int code) {
    }

    @Override
    public void handleError(int code) {
        if (code == MONITOR_CODE) {
            showTips("监测失败，请检查网络配置");
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        LogUtil.info("msg.what"+msg.what);
        if(msg.what==ADD_MONITOR){
            LogUtil.info("uid"+uid);
            LogUtil.info("mac"+mac);
            addMonitor(uid,mac,username);
        }else {
            if (monitorGet == null) {
                monitorGet = new MonitorGet();
                monitorGet.setCid(monitorId);
                monitorGet.code(MONITOR_CODE);
                monitorGet.handler(this);
            }
            monitorGet.get();
        }


        return false;
    }

    private final int ADD_MONITOR = 0x0046;

    private void addMonitor(String uid, String mac,String username) {
        QueryMonitorPost post = new QueryMonitorPost();
        post.code(ADD_MONITOR);
        post.handler(this);
        post.setThermometerID(mac);
        post.setPid(uid);
        //post.setUsername(username);
        post.post();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_monitor_add:
                dialogshow(uid);
                break;
            case R.id.tv_monitor_his:
                if(!TextUtils.isEmpty(mac)) {
                    Intent intent = new Intent(this, MonHisListActivity.class);
                    intent.putExtra("mac", mac);
                    intent.putExtra("uid", uid);
                    intent.putExtra("username", username);

                    startActivity(intent);
                }
                break;
        }
    }






}
