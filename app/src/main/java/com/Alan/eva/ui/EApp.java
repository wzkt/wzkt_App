package com.Alan.eva.ui;


import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.Alan.eva.BuildConfig;
import com.Alan.eva.config.BLEConfig;
import com.Alan.eva.model.UserInfo;
import com.Alan.eva.service.BleService;
import com.Alan.eva.service.ServiceUtils;
import com.Alan.eva.tools.LogUtil;
import com.Alan.eva.tools.SpTools;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.ui.activity.MonitorActivity;
import com.clj.fastble.BleManager;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.xutils.DbManager;
import org.xutils.db.table.TableEntity;
import org.xutils.x;

import java.io.File;

public class EApp extends Application {
    private static EApp app;
    private UserInfo userInfo;
    private int screenWidth;
    private int screenHeight;
   public DbManager db;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG_MODE);
        MobclickAgent.setDebugMode(BuildConfig.DEBUG_MODE);
        MobclickAgent.setCatchUncaughtExceptions(true);

        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                //设置数据库名，默认xutils.db
                .setDbName("eva.db")
                .setDbDir(new File(this.getCacheDir().toString()+"/db"))
                .setDbVersion(1)
                //设置数据库更新的监听
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                    }
                });


        db = x.getDb(daoConfig);
    }



    public static EApp getApp() {
        return app;
    }




    @Override
    public void onTerminate() {
        try {
            MonitorActivity.stopMonitor();
            EventBus.getDefault().unregister(app);
        }catch (Exception e){
        }


        BleManager.getInstance().disableBluetooth();
        if(ServiceUtils.isServiceRunning(this,"BleService")){
            BleService bleserver =  new BleService();
            bleserver.stopSelf();
        }
        super.onTerminate();




        LogUtil.info("应用挂了~~~~");
    }


    /**
     * 获取用户信息内容
     *
     * @param activity 上下文
     * @return 用户信息
     */
    public UserInfo getUserInfo(Activity activity) {
        if (userInfo == null) {
            String userStr = SpTools.getInstance(activity).getUserInfo();
            if (TextUtils.isEmpty(userStr)) {
                return null;
            }
            userInfo = Tools.json2Bean(userStr, UserInfo.class);
        }
        return userInfo;
    }

    /**
     * 保存用户信息
     *
     * @param userInfo 用户信息
     */
    public void setUserInfo(UserInfo userInfo, Activity activity) {
        LogUtil.info("userInfo:"+userInfo);

        this.userInfo = userInfo;
        if (userInfo != null) {
            String userStr = Tools.bean2Json(userInfo);
            LogUtil.info("userStr:"+userStr);
            SpTools.getInstance(activity).setUserInfo(userStr);
        } else {
            SpTools.getInstance(activity).setUserInfo("");
        }
    }

    /**
     * 获取全局屏幕宽度
     *
     * @return 屏幕宽度
     */
    public int getScreenWidth() {
        return screenWidth;
    }

    /**
     * 在app进入主界面的时候调用一次set方法， 将屏幕宽高保存到application里面，这样全局都可以使用
     */
    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    /**
     * 获取全局屏幕高度
     *
     * @return 屏幕高度
     */
    public int getScreenHeight() {
        return screenHeight;
    }

    /**
     * 保存高度，在app首页中调用一次即可，和set宽一样
     *
     * @param screenHeight 屏幕高度
     */
    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }
}
