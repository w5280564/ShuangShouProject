package com.lyz.shuangshoudeliverer.httpapi;

import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.lyz.shuangshoudeliverer.BuildConfig;
import com.lyz.shuangshoudeliverer.ExitApplication;
import com.lyz.shuangshoudeliverer.constant.StaticData;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitHelper {

    public static String BaseUrl = "http://121.40.211.134:8090/";
    public static String ImgUrl = "http://121.40.211.134:8090/media/";

    private static OkHttpClient okHttpClient = null;
    private static UrlApi networkService;

    public static UrlApi getNetworkService() {
        initOkHttp();

        Gson gson = null;
        if (gson == null) {
            gson = new GsonBuilder().setLenient().create();
        }
        if (networkService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(BaseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            networkService = retrofit.create(UrlApi.class);
        }
        return networkService;
    }

    private static void initOkHttp() {
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(loggingInterceptor);
            }

            builder.interceptors().add(new ReceivedCookiesInterceptor(ExitApplication.getInstance()));
            builder.interceptors().add(new AddCookiesInterceptor(ExitApplication.getInstance()));

            File cacheFile = new File(Environment.getExternalStorageDirectory(), File.separator + StaticData.PATH_CACHE);
            Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);

            //设置缓存
            builder.cache(cache);
            //设置超时
            builder.connectTimeout(10, TimeUnit.SECONDS);
            builder.readTimeout(20, TimeUnit.SECONDS);
            builder.writeTimeout(20, TimeUnit.SECONDS);
            //错误重连
            builder.retryOnConnectionFailure(true);
            okHttpClient = builder.build();
        }
    }


}
