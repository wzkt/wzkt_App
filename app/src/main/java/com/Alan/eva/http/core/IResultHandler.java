package com.Alan.eva.http.core;

/**
 * Created by wei19 on 2016/9/16.
 * 请求回传接口
 */
public interface IResultHandler {
    /**
     * 请求开始
     *
     * @param code code
     */
    void handleStart(int code);

    /**
     * 请求成功
     *
     * @param result 结果
     * @param code   code
     */
    void handleResult(String result, int code);

    /**
     * 请求结束
     *
     * @param code code
     */
    void handleFinish(int code);

    /**
     * 请求错误
     *
     * @param code code
     */
    void handleError(int code);
}
