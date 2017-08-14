package com.lyz.shuangshouproject.httpapi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.lyz.shuangshouproject.ExitApplication;
import com.lyz.shuangshouproject.ui.account.LoginActivity;
import com.lyz.shuangshouproject.utils.ToastUtils;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.Interceptor;
import okhttp3.Response;

public class ReceivedCookiesInterceptor implements Interceptor {
    private Context context;

    public ReceivedCookiesInterceptor(Context context) {
        super();
        this.context = context;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {

        Response originalResponse = chain.proceed(chain.request());

        if (originalResponse.code()==403){//sesscion过期，重新登录

            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            ToastUtils.getInstance().showToast("账号过期，请重新登录");
        }else {

            //这里获取请求返回的cookie
            if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                final StringBuffer cookieBuffer = new StringBuffer();

                //保存cookie数据
                Observable.fromIterable(originalResponse.headers("Set-Cookie"))
                        .map(new Function<String, String>() {
                            @Override
                            public String apply(String s) throws Exception {

                                String[] cookieArray = s.split(";");

                                SharedPreferences.Editor editor = ExitApplication.getSpEditor();
                                editor.putString("JSESSIONID", cookieArray[0]);
                                /**put完毕必需要commit()否则无法保存**/
                                editor.commit();
//                            String JSESSIONID =cookieArray[0] ;
                                return cookieArray[0];
                            }
                        })
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String cookie) throws Exception {
                                cookieBuffer.append(cookie);

                            }
                        });
            }

        }

        return originalResponse;
    }
}