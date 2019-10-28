package com.example.nilif.wifilist;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Created by nilif on 2016/7/8.
 */
public class WifiClass {
    // 定义一个WifiManager对象
    private WifiManager wifiManager;
    // 定义一个WifiInfo对象
    private WifiInfo wifiInfo;
    // 扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    // 网络连接列表
    private List<WifiConfiguration> wifiConfigurList;

    public  boolean isConnect(ScanResult scanResult) {
        if (scanResult == null) {
            return false;
        }

        wifiInfo = wifiManager.getConnectionInfo();
        String g2 = "\"" + scanResult.SSID + "\"";
        if (wifiInfo.getSSID() != null && wifiInfo.getSSID().endsWith(g2)) {
            return true;
        }
        return false;
    }

    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    public WifiClass(Context context){
        // 取得WifiManager对象
        wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        wifiInfo = wifiManager.getConnectionInfo();
    }

    // 连接
    public boolean connect(String SSID, String password,WifiCipherType type){
        if (!this.openWifi()){
            return false;
        }

        //开启wifi功能，设定延时
        while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING){
            try{
                Thread.currentThread();
                Thread.sleep(100);
            }catch (InterruptedException ie){
                ie.printStackTrace();
            }
        }
        WifiConfiguration wifiConfig = this.createInfo(SSID,password,type);
        if (wifiConfig == null){
            return false;
        }
        WifiConfiguration tempConfig = this.isExits(SSID);
        if (tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        }

        int netID = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();

        boolean bRet = wifiManager.enableNetwork(netID, true);
        wifiManager.reconnect();
        return bRet;
    }

    public void wifiStartScan(){
        // 开始扫描
        wifiManager.startScan();
        // 获取扫描结果
        mWifiList = wifiManager.getScanResults();
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



    private WifiConfiguration createInfo(String SSID, String password,WifiCipherType type){
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.allowedAuthAlgorithms.clear();
        wifiConfig.allowedGroupCiphers.clear();
        wifiConfig.allowedKeyManagement.clear();
        wifiConfig.allowedPairwiseCiphers.clear();
        wifiConfig.allowedProtocols.clear();
        wifiConfig.SSID = "\""+SSID+"\"";
        // 没有加密
        if (type == WifiCipherType.WIFICIPHER_NOPASS){
            wifiConfig.wepKeys[0] = "";
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE); // 设置成没有加密
            wifiConfig.wepTxKeyIndex = 0;
        }
        // WEP 加密
        if (type == WifiCipherType.WIFICIPHER_WEP){
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
        if (type == WifiCipherType.WIFICIPHER_WPA){
            wifiConfig.preSharedKey = "\""+password+"\"";
            wifiConfig.hiddenSSID = true;
            wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wifiConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
        }else {
            return null;
        }

        return wifiConfig;


    }
    private boolean openWifi() {
        if (!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
        return true;
    }

}
