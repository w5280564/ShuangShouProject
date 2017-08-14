package com.lyz.shuangshouproject.ui.base;

import android.app.Activity;

import com.lyz.shuangshouproject.R;


public abstract class BaseActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
//        MobclickAgent.onPageStart(getClass().getName());
//        //session的统计
//        MobclickAgent.onResume(this);  //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd(getClass().getName());
//        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        setContentView(R.layout.empty);
        System.gc();
        super.onDestroy();
    }

}

