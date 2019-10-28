package com.Alan.eva.ui.core;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.Alan.eva.tools.LogUtil;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.ui.dialog.LoadingDialog;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象Activity
 *
 * @author wei19
 */
public abstract class AbsActivity extends AppCompatActivity {
    /**
     * 获取实现类的上下文环境
     *
     * @return 当前页面
     */
    public abstract Activity getCurrActivity();


    private LayoutInflater mInflater;
    private View rootView;

    public abstract int getRootViewId();

    public abstract void findView(View rootView);

    private Intent mIntent;
    private boolean inActive = false;
    private RequestPermissionCallBack mRequestPermissionCallBack;
    private final int mRequestCode = 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        super.onCreate(savedInstanceState);
        mInflater = LayoutInflater.from(getCurrActivity());
        rootView = mInflater.inflate(getRootViewId(), null);
        setContentView(rootView);
        findView(rootView);
    }

    @Override
    protected void onResume() {
        LogUtil.info(getCurrActivity().getLocalClassName() + ".onResume()");
        super.onResume();
        inActive = true;
        //checkPermissions();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStart() {
        LogUtil.info(getCurrActivity().getLocalClassName() + ".onStart()");
        super.onStart();
    }

    @Override
    protected void onPause() {
        LogUtil.info(getCurrActivity().getLocalClassName() + ".onPause()");
        super.onPause();
        inActive = false;
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        LogUtil.info(getCurrActivity().getLocalClassName() + ".onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LogUtil.info(getCurrActivity().getLocalClassName() + ".onDestroy()");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        LogUtil.info(getCurrActivity().getLocalClassName() + ".onRestart()");
        super.onRestart();
    }

    /**
     * 获取目标意图实例，并且已经添加好对象跳转
     *
     * @param clazz 类
     * @return 意图
     */
    public <T> Intent getIntent(Class<T> clazz) {
        if (mIntent == null) {
            mIntent = new Intent(getCurrActivity(), clazz);
        } else {
            mIntent.setClass(getCurrActivity(), clazz);
        }
        return mIntent;
    }

    protected <T> void gotoActivity(Class<T> clazz) {
        if (mIntent == null) {
            mIntent = new Intent();
        }
        mIntent.setClass(getCurrActivity(), clazz);
        getCurrActivity().startActivity(mIntent);
    }

    /**
     * @return 获取跟布局的实例对象
     */
    public View getRootView() {
        return rootView;
    }

    /**
     * 结束当前activity
     */
    public void currFinish() {
        getCurrActivity().finish();
    }

    /**
     * 获取具体视图
     *
     * @param id 布局id
     * @return 视图布局
     */
    protected View getView(int id) {
        return getRootView().findViewById(id);
    }

    public void showTips(String str) {
        LogUtil.info(""+str);
        if (inActive) {
            Tools.showTips(getCurrActivity(), str);
        }
    }

    protected LayoutInflater getMInflater() {
        return mInflater;
    }

    private LoadingDialog loadingDialog;

    protected void loading() {
        loading("加载中...");
    }

    protected void loading(String tips) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(getCurrActivity());
            loadingDialog.tips(tips);
            loadingDialog.create();
        }
        loadingDialog.show();
    }

    protected void hide() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    private void checkPermissions() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "打开蓝牙", Toast.LENGTH_LONG).show();
           // return;
        }

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE_PERMISSION_LOCATION);
        }
    }
    public void goactivity(){

    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                    new AlertDialog.Builder(this)
                            .setTitle("GPS设置")
                            .setMessage("提示")
                            .setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                            .setPositiveButton("设置",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
                                        }
                                    })

                            .setCancelable(false)
                            .show();
                } else {

                }
                break;
        }
    }

    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }
    private static final int REQUEST_CODE_OPEN_GPS = 1;
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_GPS) {
            if (checkGPSIsOpen()) {

            }
        }
    }
    /**
     * 权限请求结果回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllGranted = true;
        StringBuilder permissionName = new StringBuilder();
        for (String s: permissions) {
            permissionName = permissionName.append(s + "\r\n");
        }
        switch (requestCode) {
            case mRequestCode: {
                for (int i = 0; i < grantResults.length; ++i) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        hasAllGranted = false;
                        //在用户已经拒绝授权的情况下，如果shouldShowRequestPermissionRationale返回false则
                        // 可以推断出用户选择了“不在提示”选项，在这种情况下需要引导用户至设置页手动授权
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                            new android.support.v7.app.AlertDialog.Builder(this)
                                    .setMessage("【用户选择了不在提示按钮，或者系统默认不在提示（如MIUI）。" +
                                            "引导用户到应用设置页去手动授权,注意提示用户具体需要哪些权限】\r\n" +
                                            "获取相关权限失败:\r\n" +
                                            permissionName +
                                            "将导致部分功能无法正常使用，需要到设置页面手动授权")
                                    .setPositiveButton("去授权", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                                            intent.setData(uri);
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mRequestPermissionCallBack.denied();
                                        }
                                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    mRequestPermissionCallBack.denied();
                                }
                            }).show();

                        } else {
                            //用户拒绝权限请求，但未选中“不再提示”选项
                            mRequestPermissionCallBack.denied();
                        }
                        break;
                    }
                }
                if (hasAllGranted) {
                    mRequestPermissionCallBack.granted();
                }
            }
        }
    }

    /**
     * 发起权限请求
     *
     * @param context
     * @param permissions
     * @param callback
     */
    public void requestPermissions(final Context context, final String[] permissions,
                                   RequestPermissionCallBack callback) {
        this.mRequestPermissionCallBack = callback;
        StringBuilder permissionNames = new StringBuilder();
        for(String s : permissions){
            permissionNames = permissionNames.append(s + "\r\n");
        }
        //如果所有权限都已授权，则直接返回授权成功,只要有一项未授权，则发起权限请求
        boolean isAllGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                isAllGranted = false;

                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                    new android.support.v7.app.AlertDialog.Builder(context)
                            .setMessage("用户曾经拒绝过你的请求\r\n" +
                                    "您好，需要如下权限：\r\n" +
                                    permissionNames+
                                    " 请允许，否则将影响部分功能的正常使用。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(((Activity) context), permissions, mRequestCode);
                                }
                            }).show();
                } else {
                    ActivityCompat.requestPermissions(((Activity) context), permissions, mRequestCode);
                }

                break;
            }
        }
        if (isAllGranted) {
            mRequestPermissionCallBack.granted();
            return;
        }
    }

    /**
     * 权限请求结果回调接口
     */
  public   interface RequestPermissionCallBack {
        /**
         * 同意授权
         */
        public void granted();

        /**
         * 取消授权
         */
        public void denied();
    }



}
