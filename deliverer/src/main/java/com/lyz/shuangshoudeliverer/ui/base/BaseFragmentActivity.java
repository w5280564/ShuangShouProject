package com.lyz.shuangshoudeliverer.ui.base;

import android.support.v4.app.FragmentActivity;

import com.lyz.shuangshoudeliverer.R;


public abstract class BaseFragmentActivity extends FragmentActivity {
    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        setContentView(R.layout.empty);
        System.gc();
        super.onDestroy();
    }

}