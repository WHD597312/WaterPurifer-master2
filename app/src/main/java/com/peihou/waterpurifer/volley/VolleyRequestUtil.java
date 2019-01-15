package com.peihou.waterpurifer.volley;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.peihou.waterpurifer.base.MyApplication;
import com.peihou.waterpurifer.util.ConstUtils;
import com.peihou.waterpurifer.util.SharedPreferencesHelper;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class VolleyRequestUtil {

    public static JsonObjectRequest jsonObjectRequest;
    public static Context context;
    public static int sTimeOut = 20000;

    /*
     * 获取GET请求内容
     * 参数：
     * context：当前上下文；
     * url：请求的url地址；
     * tag：当前请求的标签；
     * volleyListenerInterface：VolleyListenerInterface接口；
     * timeOutDefaultFlg：是否使用Volley默认连接超时；
     * */
    public static void RequestGet(Context context, String url, final String tag,
                                  VolleyListenerInterface volleyListenerInterface,
                                  boolean timeOutDefaultFlg) {
        // 清除请求队列中的tag标记请求
        MyApplication.getQueue().cancelAll(tag);
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context, "my");
        final String token = (String) sharedPreferencesHelper.getSharedPreference("token", "token");
        // 创建当前的请求，获取字符串内容
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ConstUtils.BASEURL + url,
                volleyListenerInterface.responseListener(), volleyListenerInterface.errorListener()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                //new 一个Map  参数放到Map中
                map.put("client", "android-xr");
//                map.put("Content-Type", "application/json");
//                if (!tag.equals("login")) {
//                    map.put("authorization", token);
//                }
                return map;
            }
        };
        // 为当前请求添加标记
        jsonObjectRequest.setTag(tag);
        // 默认超时时间以及重连次数
        int myTimeOut = timeOutDefaultFlg ? DefaultRetryPolicy.DEFAULT_TIMEOUT_MS : sTimeOut;
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(myTimeOut,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // 将当前请求添加到请求队列中
        MyApplication.getQueue().add(jsonObjectRequest);
        // 重启当前请求队列
        MyApplication.getQueue().start();
    }

    /*
     * 获取POST请求内容（请求的代码为Map）
     * 参数：
     * context：当前上下文；
     * url：请求的url地址；
     * tag：当前请求的标签；
     * params：POST请求内容；
     * volleyListenerInterface：VolleyListenerInterface接口；
     * timeOutDefaultFlg：是否使用Volley默认连接超时；
     * */
    public static void RequestPost(final Context context, String url, final String tag,
                                   final Map<String, String> params,
                                   VolleyListenerInterface volleyListenerInterface,
                                   boolean timeOutDefaultFlg) {
        // 清除请求队列中的tag标记请求
        MyApplication.getQueue().cancelAll(tag);
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context, "my");
        final String token = (String) sharedPreferencesHelper.getSharedPreference("token", "token");
        JSONObject jsonObject = new JSONObject();
        try {

            for (Map.Entry<String, String> param : params.entrySet()) {
                jsonObject.put(param.getKey(), param.getValue());
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        // 创建当前的POST请求，并将请求内容写入Map中
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ConstUtils.BASEURL + url,jsonObject,
                volleyListenerInterface.responseListener(), volleyListenerInterface.errorListener()) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                //new 一个Map  参数放到Map中
                map.put("client", "android-xr");
                map.put("Content-Type", "application/json");
                if (!tag.equals("login")) {
                    map.put("authorization", token);
                }
                return map;

            }
        };
        // 为当前请求添加标记
        jsonObjectRequest.setTag(tag);
        // 默认超时时间以及重连次数
        int myTimeOut = timeOutDefaultFlg ? DefaultRetryPolicy.DEFAULT_TIMEOUT_MS : sTimeOut;
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(myTimeOut,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // 将当前请求添加到请求队列中
        MyApplication.getQueue().add(jsonObjectRequest);
        // 重启当前请求队列
        MyApplication.getQueue().start();
    }


}
