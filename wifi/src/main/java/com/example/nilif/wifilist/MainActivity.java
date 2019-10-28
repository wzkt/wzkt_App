package com.example.nilif.wifilist;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pUpnpServiceRequest;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.kingcomchina.www.tcptool.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends ListActivity {

    private WifiManager wifiManager;
    private List<ScanResult> listResult;
    private List<ScanResult> tmpList = new ArrayList<ScanResult>();
    private List<WifiInfo> wifiArray = new ArrayList<WifiInfo>();
    private StringBuffer stringBuffer = new StringBuffer();
    private ScanResult scanResult;
    private List<WifiConfiguration> wifiConfigurList; // 配置好网络的列表
    private TextView tv;
    private Button button;
    private WifiInfo mWifiInfo;
    private ListView listView;
    private List<Map<String,Object>> list;
    private final String TAG = "sda";
    private BroadcastReceiver mRecevicer;
    private List<ScanResult> wifiList;
    private SimpleAdapter adapter;
    private WifiClass wifiClass;
    // 定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainn);
        //tv = (TextView) findViewById(R.id.tv);
        list = new ArrayList<Map<String, Object>>();
        button = (Button) findViewById(R.id.btn_scan);
        wifiInitial();
        //openWifi();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanWifi();
            }
        });

    }

    private OnNetworkChangeListener mOnNetworkChangeListener = new OnNetworkChangeListener() {

       /* @Override
        public void onNetWorkDisConnect() {
            getWifiListInfo();
            mAdapter.setDatas(list);
            mAdapter.notifyDataSetChanged();
        }*/

        @Override
        public void onNetWorkConnect() {
            getWifiListInfo();
            setDatas(listResult);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
        }
    };
    private WifiInfo connInfo;
    private void setDatas(List<ScanResult> listResult) {
        this.listResult = listResult;
        connInfo = wifiManager.getConnectionInfo();
    }


    public void getWifiListInfo(){
        System.out.println("WifiListActivity#getWifiListInfo");
        wifiStartScan();
        List<ScanResult> tmpList = getWifiList();
        if (tmpList == null) {
            list.clear();
        } else {
            listResult = tmpList;
        }
    }
    public List<ScanResult> getWifiList(){
        return wifiList;
    }
    private void wifiInitial() {
        // 获取wifi服务
        wifiClass = new WifiClass(MainActivity.this);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        openWifi();
        wifiConfigurList = wifiManager.getConfiguredNetworks(); // 获取以前配置好的wifi列表
    }

    /**
     * 打开wifi
     * */
    private boolean openWifi() {
        if (!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
        return true;
    }

    /**
     * 关闭wifi
     * */
    private void closeWifi(){
        if (wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(false);
        }
    }
    /**
     * 扫描周围wifi
     * */
    public void scanWifi() {
        wifiStartScan();

        if (listResult == null){
            Toast.makeText(MainActivity.this, "周围没有wifi服务，请重试", Toast.LENGTH_SHORT).show();
        }

        for (ScanResult scanResult:listResult) {
        Map<String,Object> map = new HashMap<String,Object>();
            map.put("SSID", scanResult.SSID);
            map.put("BSSID", scanResult.BSSID);
            map.put("RSSI", scanResult.level);
            tmpList.add(scanResult);
            list.add(map);
            Log.e(TAG, "scanWifi: "+scanResult.SSID );
        }
//        for (WifiConfiguration wifiConfig:wifiConfigurList){
//            Map<String,Object> map = new HashMap<String,Object>();
//            map.put("SSID", wifiConfig.SSID);
//            map.put("BSSID", wifiConfig.BSSID);
////            map.put("RSSI", wifiConfig.level);
//            list.add(map);
//        }
//        getListView().setAdapter(new SimpleAdapter(this,list,R.layout.listview_item_layout
//                ,new String[]{"SSID","BSSID"}
//                ,new int[]{R.id.SSID,R.id.BSSID}));
        adapter = new SimpleAdapter(this,list,R.layout.listview_item_layout
                ,new String[]{"SSID","BSSID","RSSI"}
                ,new int[]{R.id.SSID,R.id.BSSID,R.id.RSSI});
        setListAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            String wifiItemSSID = null;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object ssid = list.get(position).get("SSID");//获取的ssid
                Log.e(TAG, "onItemClick:"+ssid );
                ScanResult scanResult = tmpList.get(position);
//                int pos = position - 1;
//                ScanResult scanResult = tmpList.get(pos);
//                // TODO: 2016/7/8 create a dialog
//                new AlertDialog.Builder(getApplicationContext()).setTitle("请输入密码").setView(new EditText(getApplicationContext())).setPositiveButton("确定",null)
//                        .setNegativeButton("取消",null).show();
//                connectConfiguration(position);
                isConnect(scanResult);

            }
        });

    }

    private void isConnect(ScanResult scanResult) {
        if (wifiClass.isConnect(scanResult)){
            Toast.makeText(MainActivity.this, "已连接该网络", Toast.LENGTH_SHORT).show();
        }else {
            WifiConnDialog mDialog = new WifiConnDialog(
                    MainActivity.this, R.style.PopDialog,
                    scanResult, mOnNetworkChangeListener);
            // WifiConnDialog mDialog = new WifiConnDialog(
            // WifiListActivity.this, R.style.PopDialog, wifiName,
            // singlStrength, secuString);
            mDialog.show();
        }
    }

    private void buildDialog() {
        new AlertDialog.Builder(this).setTitle("请输入密码").setView(new EditText(this)).setPositiveButton("确定",null)
                .setNegativeButton("取消",null).show();
    }

    public boolean ifConnect(ScanResult result) {
        if (result == null) {
            return false;
        }

        mWifiInfo = wifiManager.getConnectionInfo();
        String g2 = "\"" + result.SSID + "\"";
        if (mWifiInfo.getSSID() != null && mWifiInfo.getSSID().endsWith(g2)) {
            return true;
        }
        return false;
    }

    // 获取配置好的网络的列表
    public List<WifiConfiguration> getWifiConfigurList(){
        return wifiConfigurList;
    }
    // 连接配置好的网络
    public void connectConfiguration(int index){
        if (index > wifiConfigurList.size()){
            Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
            return;
        }
        wifiManager.enableNetwork(wifiConfigurList.get(index).networkId,true);
        Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
    }


    public void wifiStartScan(){
        // 开始扫描
        wifiManager.startScan();
        // 获取扫描结果
        listResult = wifiManager.getScanResults();
        // 获取配置好的网络连接
        wifiConfigurList = wifiManager.getConfiguredNetworks();
    }

    // 添加一个网络并连接
    public void addNetWork(WifiConfiguration wifiConfiguration){
        // 获取网络的netId
        int wcgId = wifiManager.addNetwork(wifiConfiguration);

        wifiManager.enableNetwork(wcgId,true);
    }

    // 断开连接
    public void disconnectNetWork(int netId){
        wifiManager.disableNetwork(netId);
        wifiManager.disconnect();
    }

    // 判断某一个网络是否配置过
    private WifiConfiguration isExits(String SSID){
        List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configs){
            if (config.SSID.equals("\"" + SSID + "\"")){
                return config;
            }
        }
        return null;
    }



    private WifiConfiguration createInfo(String SSID, String password,WifiClass.WifiCipherType type){
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.allowedAuthAlgorithms.clear();
        wifiConfig.allowedGroupCiphers.clear();
        wifiConfig.allowedKeyManagement.clear();
        wifiConfig.allowedPairwiseCiphers.clear();
        wifiConfig.allowedProtocols.clear();
        wifiConfig.SSID = "\""+SSID+"\"";
        // 没有加密
        if (type == WifiClass.WifiCipherType.WIFICIPHER_NOPASS){
            wifiConfig.wepKeys[0] = "";
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE); // 设置成没有加密
            wifiConfig.wepTxKeyIndex = 0;
        }
        // WEP 加密
        if (type == WifiClass.WifiCipherType.WIFICIPHER_WEP){
            wifiConfig.preSharedKey = "\""+password+"\"";
            wifiConfig.hiddenSSID = true;
            wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfig.wepTxKeyIndex = 0;
        }
        // WPA加密
        if (type == WifiClass.WifiCipherType.WIFICIPHER_WPA){
            wifiConfig.preSharedKey = "\""+password+"\"";
            wifiConfig.hiddenSSID = true;
            wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wifiConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfig.status = WifiConfiguration.Status.ENABLED;
        }else {
            return null;
        }

        return wifiConfig;
    }





}
