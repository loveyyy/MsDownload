package com.morningsun.download.http;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Create By morningsun  on 2020-08-05
 */
public class HttpClient {
    public static HttpClient Instance =new HttpClient();

    private   OkHttpClient client;
    private HttpClient(){
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger()
        {
            @Override
            public void log(String message)
            {
                Log.d("Http", message+"");
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS).
                writeTimeout(20, TimeUnit.SECONDS);
        client= builder.build();
    }

    public Call getCall(String url,String header){
        Request request= new Request
                .Builder()
                .url(url)
                .addHeader("RANGE",header)
                .build();
        return client.newCall(request);
    }




}
