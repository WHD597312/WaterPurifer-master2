package com.peihou.waterpurifer.volley;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import org.json.JSONObject;

public abstract class VolleyListenerInterface implements Response.Listener<JSONObject> {
    public Context mContext;
    public static Response.Listener<JSONObject > mListener;
    public static Response.ErrorListener mErrorListener;

    public VolleyListenerInterface(Context context, Response.Listener<JSONObject > listener,
                                   Response.ErrorListener errorListener) {
        this.mContext = context;
        this.mErrorListener = errorListener;
        this.mListener = listener;
    }

    // 请求成功时的回调函数
    public abstract void onMySuccess(JSONObject result);

    // 请求失败时的回调函数
    public abstract void onMyError(VolleyError error);

    // 创建请求的事件监听
    public Response.Listener<JSONObject> responseListener() {
        mListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject  s) {
                Log.e("Volley Response", "response == " + s);

                onMySuccess(s);
            }
        };
        return mListener;
    }

    // 创建请求失败的事件监听
    public Response.ErrorListener errorListener() {
        mErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onMyError(volleyError);

                if(volleyError!=null){
                    if(volleyError instanceof TimeoutError){
                        Toast.makeText(mContext,"网络请求超时，请重试！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(volleyError instanceof ServerError) {
                        Toast.makeText(mContext,"服务器异常",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(volleyError instanceof NetworkError) {
                        Toast.makeText(mContext,"请检查网络",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(volleyError instanceof ParseError) {
                        Toast.makeText(mContext,"数据格式错误",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(mContext,volleyError.getMessage(),Toast.LENGTH_SHORT).show();
                    int erroorCode = volleyError.networkResponse.statusCode;
                    switch (erroorCode) {
                        case 301:
                        case 302:
                            Log.e("-------->", "301  302--");
                            //TODO 相应的处理
                            break;
                        case 401:

                            break;
                        case 405:
                            break;
                        default:
                    }
                }
            }
        };
        return mErrorListener;
    }
}

