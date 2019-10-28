package com.Alan.eva.http.core;

import org.xutils.http.RequestParams;

import java.io.File;

/**
 * Created by qc on 2016/4/28.
 * 目的是将不同参数名称规整统一
 */
public class ReqParam extends RequestParams {
    public ReqParam(String url) {
        super(url);
    }

    /**
     * 添加参数至Body
     *
     * @param name name
     * @param value value
     */
    public void put(String name, String value) {
        super.addBodyParameter(name, value);
    }

    /**
     * 添加body参数
     */
    public void put(String name, File value) {
        super.addBodyParameter(name, value);
    }

    /**
     * 添加body参数
     *
     * @param name        参数名
     * @param value       可以是String, File, InputStream 或 byte[]
     * @param contentType 可为null
     */
    public void put(String name, Object value, String contentType) {
        super.addBodyParameter(name, value, contentType);
    }

    /**
     * 添加body参数
     *
     * @param name        参数名
     * @param value       可以是String, File, InputStream 或 byte[]
     * @param contentType 可为null
     * @param fileName    服务端看到的文件名
     */
    public void put(String name, Object value, String contentType, String fileName) {
        super.addBodyParameter(name, value, contentType, fileName);
    }
}
