package com.Alan.eva.config;

import android.os.Environment;


/**
 * Created by CW on 2017/5/15.
 * 下载文件服务的配置文件
 */
public class DownloadConfig {
    public static final int DOWN_LOAD_START_CMD = 0x00077;

    public static final int DOWN_LOAD_STARTED = 0x00080;
    public static final int DOWN_LOAD_FAILED = 0x00081;
    public static final int DOWN_LOAD_SUCCESS = 0x00082;

    private static final String ROOT_PATH = Environment.getExternalStorageDirectory().getPath() + "/wzkt/";

    public static final String FILE_PATH = ROOT_PATH + "file/";

}
