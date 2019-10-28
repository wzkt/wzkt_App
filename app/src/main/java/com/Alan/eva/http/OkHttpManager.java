package com.Alan.eva.http;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.Alan.eva.tools.LogUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpManager {
    private static final String TAG = "OkHttpManager";
    private OkHttpClient okHttpClient;
    private volatile static OkHttpManager manager;
    private Handler handler;
    //提交json数据
    private static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    //提交字符串
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown;charset=utf-8");

    public OkHttpManager() {
        okHttpClient = getUnsafeOkHttpClient();
//        okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
//                .readTimeout(10000L, TimeUnit.MILLISECONDS)
//                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
//                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
//                .build();
        handler = new Handler(Looper.getMainLooper());
    }



    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.connectTimeout(3, TimeUnit.MINUTES);
            builder.readTimeout(5, TimeUnit.MINUTES);
            builder.writeTimeout(3,TimeUnit.MINUTES);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static OkHttpManager getInstance() {
        if (manager == null) {
            synchronized (OkHttpManager.class) {
                if (manager == null) {
                    manager = new OkHttpManager();
                }
            }
        }
        return manager;
    }

    /**
     * 向服务器提交String请求
     * Post
     *
     * @param url
     * @param paramsMap 参数
     * @param callBack
     */
    public void sendStringByPostMethod(String url, HashMap<String, String> paramsMap, final StringCallBack callBack) {
        try {
            //请求
      String requestUrl = url;
     LogUtil.inf( "requestUrl ----"+requestUrl);
      //Log.e( "hjs","requestUrl ----"+requestUrl);

    JSONObject jsonObj = new JSONObject(paramsMap);
      RequestBody body = RequestBody.create(JSON, jsonObj.toString());
    try{
    if(jsonObj!=null)LogUtil.inf(("hjssendjson"+jsonObj.toString()));
    }catch (Exception e){
    }
    //Log.e("json", jsonObj.toString());
    Request request = addHeaders().url(requestUrl).post(body).build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callBack.onFailed();
                    try {
                        LogUtil.inf(("response ----->onFailed" + e.toString()));
                    }catch (Exception ee){
                        LogUtil.inf(("response --ee--->onFailed"));
                    }
                   // e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response){
                    try {
                        //LogUtil.inf("sendjson", jsonObj.toString());
                        LogUtil.inf("response onResponse----->" + response.body().toString());
                    }catch (Exception e){

                    }
                    //Log.e("TAG", "response.isSuccessful())"+response.code());
//                     if (response != null && response.isSuccessful()) {
                    if (response != null) {
                        try {
                            onSuccessJsonString(response.body().string(), callBack);
                        } catch (Exception e) {
                            e.printStackTrace();
                            callBack.onFailed();
                        }
                    }
                }
            }
            );
        } catch (Exception e) {
            try {
                LogUtil.inf(("okHttpClient.newCal ----->onFailed" + System.err.toString()));
            }catch (Exception ee){
                LogUtil.inf(("okHttpClient.newCal --ee--->onFailed"));
            }
            e.printStackTrace();
            callBack.onFailed();
        }
    }

    /**
     * 向服务器提交String请求
     * Post
     *
     * @param url
     * @param paramsMap 参数
     * @param callBack
     */
    public void sendStringByGETMethod(String url, HashMap<String, String> paramsMap, final StringCallBack callBack) {
        StringBuilder tempParams = new StringBuilder();
        try {
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            String requestUrl = String.format("%s/%s?%s", url,tempParams.toString());

            LogUtil.inf(( "requestUrl ----"+url+" ="+tempParams.toString()));

            final Request request = addHeaders().url(requestUrl).build();
//            final Call call = mOkHttpClient.newCall(request);
//
//
//            //请求
//            String requestUrl = url;
//            LogUtil.inf( "requestUrl ----"+requestUrl);
//
//            JSONObject jsonObj = new JSONObject(paramsMap);
//            RequestBody body = RequestBody.create(JSON, jsonObj.toString());
//
//            Log.e("json", jsonObj.toString());
//            Request request = addHeaders().url(requestUrl).get()..build();
//
//            okHttpClient.newBuilder().addParam("cat_id", 14137)
//                    .addParam("cur_page", 1)
//                    .addParam("size", 10)
//                    .get()
//                    .url(url)
//                    .build();


            okHttpClient.newCall(request).enqueue(new Callback() {
                                                      @Override
                                                      public void onFailure(Call call, IOException e) {
                                                          e.printStackTrace();
                                                          callBack.onFailed();
                                                          LogUtil.inf(( "response ----->onFailed" ));
                                                      }

                                                      @Override
                                                      public void onResponse(Call call, Response response){
                                                          LogUtil.inf(( "response onResponse----->" +response.body().toString()));
                                                          if (response != null && response.isSuccessful()) {
                                                              try {
                                                                  onSuccessJsonString(response.body().string(), callBack);
                                                              } catch (Exception e) {
                                                                  e.printStackTrace();
                                                                  callBack.onFailed();
                                                              }
                                                          }
                                                      }
                                                  }
            );
        } catch (Exception e) {
            e.printStackTrace();
            callBack.onFailed();
        }
    }


    /**
     * 向服务器提交String请求
     * Post
     *
     * @param url
     * @param
     * @param callBack
     */
    public void sendStringByGETMethod(String url, final StringCallBack callBack) {
        try {
            String requestUrl = String.format(url);

            final Request request = addHeaders().url(requestUrl).build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                                                      @Override
                                                      public void onFailure(Call call, IOException e) {
                                                          e.printStackTrace();
                                                          callBack.onFailed();
                                                          LogUtil.inf( "response ----->onFailed" );
                                                      }

                                                      @Override
                                                      public void onResponse(Call call, Response response){
                                                          LogUtil.inf( "response onResponse----->" +response.body().toString());
                                                          if (response != null && response.isSuccessful()) {
                                                              try {
                                                                  onSuccessJsonString(response.body().string(), callBack);
                                                              } catch (Exception e) {
                                                                  e.printStackTrace();
                                                                  callBack.onFailed();
                                                              }
                                                          }
                                                      }
                                                  }
            );
        } catch (Exception e) {
            e.printStackTrace();
            callBack.onFailed();
        }
    }

//
//    /**
//     * okHttp get异步请求
//     * @param actionUrl 接口地址
//     * @param paramsMap 请求参数
//     * @param callBack 请求返回数据回调
//     * @param <T> 数据泛型
//     * @return
//     */
//    private <T> Call requestGetByAsyn(String actionUrl, HashMap<String, String> paramsMap, final ReqCallBack<T> callBack) {
//        StringBuilder tempParams = new StringBuilder();
//        try {
//            int pos = 0;
//            for (String key : paramsMap.keySet()) {
//                if (pos > 0) {
//                    tempParams.append("&");
//                }
//                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
//                pos++;
//            }
//            String requestUrl = String.format("%s/%s?%s", BASE_URL, actionUrl, tempParams.toString());
//            final Request request = addHeaders().url(requestUrl).build();
//            final Call call = mOkHttpClient.newCall(request);
//            call.enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    failedCallBack("访问失败", callBack);
//                    Log.e(TAG, e.toString());
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    if (response.isSuccessful()) {
//                        String string = response.body().string();
//                        Log.e(TAG, "response ----->" + string);
//                        successCallBack((T) string, callBack);
//                    } else {
//                        failedCallBack("服务器错误", callBack);
//                    }
//                }
//            });
//            return call;
//        } catch (Exception e) {
//            Log.e(TAG, e.toString());
//        }
//        return null;
//    }

    /**
     * 统一为请求添加头信息
     * @return
     */
    private Request.Builder addHeaders() {
        Request.Builder builder = new Request.Builder()
                .addHeader("Connection", "keep-alive")
                .addHeader("platform", "2")
                .addHeader("phoneModel", Build.MODEL)
                .addHeader("systemVersion", Build.VERSION.RELEASE)
                .addHeader("appVersion", "3.2.0");
        return builder;
    }


    /**
     * 请求指定的url返回的结果是json字符串
     * Get
     *
     * @param url
     * @param callBack
     */
    public void asyncJsonStringByURL(String url, final StringCallBack callBack) {
        final Request request = new Request.Builder().url(url).build();
        Log.d(TAG, "asyncJsonStringByURL: " + url);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d(TAG, "onFailure: " + e.getMessage());
                callBack.onFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    Log.d(TAG, "onResponse: " + response.body().toString());
                    onSuccessJsonString(response.body().string(), callBack);
                }
            }
        });
    }

    /**
     * 请求返回的是json对象
     *
     * @param url
     * @param callBack
     */
    public void asyncJsonObjectByURL(String url, final JSONObjectCallBack callBack) {
        final Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    onSuccessJsonObject(response.body().string(), callBack);
                }
            }
        });
    }


    /**
     * 请求返回的结果是json字符串
     *
     * @param json
     * @param callBack
     */
    private void onSuccessJsonString(final String json, final StringCallBack callBack) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    try {
                        callBack.onResponse(json);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callBack.onFailed();
                    }

                }
            }
        });
    }

    /**
     * 请求返回的结果是json对象
     *
     * @param json
     * @param callBack
     */
    private void onSuccessJsonObject(final String json, final JSONObjectCallBack callBack) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    try {
                        callBack.onResponse(new JSONObject(json));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public interface StringCallBack {
        void onResponse(String result);

        void onFailed();
    }

//    interface ByteArrayCallBack {
//        void onResponse(byte[] result);
//    }

    public interface BitmapCallBack {
        void onResponse(Bitmap bitmap);
    }

    public interface JSONObjectCallBack {
        void onResponse(JSONObject jsonObject);
    }

}
