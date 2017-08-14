package com.lyz.shuangshouproject.httpapi;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.lyz.shuangshouproject.ExitApplication;

import java.io.IOException;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by lion on 16/9/13.
 * 邮箱：18817834165@163.com
 */
public class AddCookiesInterceptor implements Interceptor {
    private Context context;

    public AddCookiesInterceptor(Context context) {
        super();
        this.context = context;

    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        final Request.Builder builder = chain.request().newBuilder();

        try {

            SharedPreferences sharedPreferences = ExitApplication.getSP();
            String JSESSIONID = sharedPreferences.getString("JSESSIONID","");

            Log.e("TAG","-JSESSIONID-"+JSESSIONID);

            Flowable.just(JSESSIONID)
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String cookie) throws Exception {
                            //添加cookie
//                        builder.addHeader("Cookie", cookie);
                            builder.header("Cookie", cookie);

//                            Log.e("AddCookies",cookie);

                        }
                    });

        }catch (Exception e){
//            Log.e("TAG--AddCookies","--"+e.getMessage());
        }

        return chain.proceed(builder.build());
    }
}
