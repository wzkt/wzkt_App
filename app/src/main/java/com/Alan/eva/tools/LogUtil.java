package com.Alan.eva.tools;

import android.util.Log;


/**
 * 修改false,其他时候全部修改为true
 *
 * @author 洋
 */
public class LogUtil {
    private static String defaultTag = "hjs";

    /**
     * 打印调试日志
     *
     * @param log 信息
     */
    public static void debug(String log) {
        debug(defaultTag, log);
    }

    /**
     * 打印信息
     *
     * @param log 信息
     */
    public static void info(String log) {
       info(defaultTag, log);
    }



    /**
     * 调试信息，可以自己加标签
     *
     * @param tag tag
     * @param log 信息
     */
    public static void debug(String tag, String log) {
        Log.d(tag, log);
    }
    /**
     * 打印信息
     *
     * @param log 信息
     */
    public static void inf(String log) {
        info("hjs", log);
    }

    /**
     * 打印信息，可以自己加标签
     *
     * @param tag tag
     * @param log 信息
     */
    public static void info(String tag, String log) {
        Log.e(tag, log);
    }
}
