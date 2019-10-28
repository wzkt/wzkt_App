package com.Alan.eva.http.core;

import android.text.TextUtils;

import com.Alan.eva.BuildConfig;
import com.Alan.eva.tools.LogUtil;

import org.xutils.common.Callback;
import org.xutils.x;

/**
 * Created by wei19 on 2016/9/16.
 * 抽象请求工具
 */
public abstract class AbsHttp {

    private int httpCode;
    private IResultHandler handler;

    /**
     * 获取片段url
     *
     * @return domain
     */
    protected abstract String domain();

    /**
     * 填充参数
     *
     * @return 参数列表
     */
    protected abstract ReqParam setParams(ReqParam builder);

    /**
     * 添加文件，如果有返回true 没有默认返回false即可
     *
     * @param params 请求体
     * @return 判断是否有文件
     */
    protected abstract boolean addFile(ReqParam params);

    public AbsHttp() {

    }

    public void code(int code) {
        this.httpCode = code;
    }

    public void handler(IResultHandler handler) {
        this.handler = handler;
    }

    public AbsHttp(IResultHandler handler, int httpCode) {
        this.httpCode = httpCode;
        this.handler = handler;
    }

    public void get() {
        x.http().get(getParam(), getCallBack());
    }

    public void post() {
        ReqParam request = getParam();
        boolean hasFile = addFile(request);
        if (hasFile) {
            request.setMultipart(true);
        }
        x.http().post(request, getCallBack());
    }

    private ReqParam getParam() {
        String domainurl = domain();
        String url="";
        if(!TextUtils.isEmpty(domainurl)&&(domainurl.startsWith("http://"))) {
            url = domainurl;
        }else{
            url = BuildConfig.SERVER_URL+domainurl;
        }
        ReqParam request = new ReqParam(url);
        request = setParams(request);
        LogUtil.info("request=" + request.toString());
        if (handler != null) {
            handler.handleStart(httpCode);
        }
        return request;
    }

    private Callback.CommonCallback<String> callback;

    private Callback.CommonCallback<String> getCallBack() {
        if (callback == null) {
            callback = new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    LogUtil.info("result=" + result);
                    if (handler != null) {
                        handler.handleResult(result, httpCode);
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    LogUtil.info("ex=" + ex);
                    if (handler != null) {
                        handler.handleError(httpCode);
                    }
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {
                    if (handler != null) {
                        handler.handleFinish(httpCode);
                    }
                }
            };
        }
        return callback;
    }

}
