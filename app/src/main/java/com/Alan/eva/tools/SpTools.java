package com.Alan.eva.tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 保存用户偏好的设置
 *
 * @author xiaoyunfei
 *         createTime：2015年3月16日 下午3:23:15
 */
public class SpTools {
    private static final String FILE_NAME = "EVE";
    private static SharedPreferences sp;
    private static SpTools save;
    /**
     * 实际保存数据时需要的键，使用衍生方法保存数据时不能缺少这个
     */
    private final String FIRST_START = "first_start";
    private final String USER_INFO = "user_info";
    private final String BLE_NAME = "ble_name";
    private final String MAC_ADDRESS = "mac_address";

    public static SpTools getInstance(Context context) {
        if (save == null) {
            save = new SpTools(context);
        }
        return save;
    }

    private SpTools(Context context) {
        sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 保存 第一次进入 App 的标志数据
     */
    public void putFirstStart(boolean value) {
        saveBoolean(FIRST_START, value);
    }

    /**
     * 获取第一次进入 App 的标志数据
     */
    public boolean isFirstStart() {
        return getBoolean(FIRST_START, true);
    }

    public void setUserInfo(String userInfo) {
        this.saveString(USER_INFO, userInfo);
    }

    public String getUserInfo() {
        return this.getString(USER_INFO, "");
    }

    public void saveMac(String name, String address) {
        saveString(BLE_NAME, name);
        saveString(MAC_ADDRESS, address);
    }

    public String getBleName() {
        return getString(BLE_NAME, "");
    }

    public String getMacAddress() {
        return getString(MAC_ADDRESS, "");
    }

    /**
     * 保存String型数据
     */
    private void saveString(String key, String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 从sp中得到数据 默认值为 ""
     */
    public String getString(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    /**
     * 保存int型数据
     */
    private void saveInt(String key, int value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * 从sp中得到数据 默认值为0
     */
    private int getInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    /**
     * 保存long型数据
     */
    private void saveLong(String key, long value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * 从sp中得到数据 默认值为0L
     */
    private long getLong(String key, long defaultValue) {
        return sp.getLong(key, defaultValue);
    }

    /**
     * 保存float型数据
     */
    private void saveFloat(String key, float value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    /**
     * 从sp中得到数据 默认值为0.0f
     */
    private float getFloat(String key, float defaultValue) {
        return sp.getFloat(key, defaultValue);
    }

    /**
     * 保存Boolean型数据
     */
    private void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * 从sp中得到数据 默认值为 false
     */
    private boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }
}