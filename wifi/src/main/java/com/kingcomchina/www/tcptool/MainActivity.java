package com.kingcomchina.www.tcptool;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 0x02;
    private TextView tv_ssid;
    private List<ScanResult> list;
    private String mSsid="";
    private WifiManager manager;
    private String password="123456789";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this,com.example.nilif.wifilist.MainActivity.class));
        //startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));

        tv_ssid = (TextView) findViewById(R.id.tv_ssid);
        manager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission_group.LOCATION)!=PackageManager.PERMISSION_GRANTED){
        // 获取wifi连接需要定位权限,没有获取权限
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
            },PERMISSION_REQUEST_CODE);

        }
        tv_ssid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"点击");
                startActivity(new Intent(MainActivity.this,ControlActivity.class));
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //
        if(requestCode==PERMISSION_REQUEST_CODE){
            Log.d(TAG,"请求吗验证通过");
            if(grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED){
               updateWifi();

            }else{
                Log.d(TAG,"不能继续操作");
            }

        }
    }

    private void updateWifi() {
        String curSsid=WifiAutoConnectManager.getCurentWifiSSID(MainActivity.this);
        Log.d(TAG,"当前wifi ssid:"+curSsid);
        if(!curSsid.contains("ESP") ){
            Toast.makeText(MainActivity.this,"请把wifi切换到KC_ESP...网络上面",Toast.LENGTH_LONG).show();
        }else{
            mSsid=curSsid;
            tv_ssid.setText(mSsid);

        }
    }

    public static List<ScanResult> getCurrentWifiScanResult(Context c) {
        WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        return wifiManager.getScanResults();
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateWifi();

        list = getCurrentWifiScanResult(getApplication());
        for(int i=0;i<list.size();i++){
            Log.d(TAG," list:BSSID:"+list.get(i).BSSID);
            Log.d(TAG," list:SSID:"+list.get(i).SSID);
            Log.d(TAG," list toString:"+list.get(i).toString());
            Log.d(TAG," list capabilities:"+list.get(i).capabilities);
        }

    }


}
