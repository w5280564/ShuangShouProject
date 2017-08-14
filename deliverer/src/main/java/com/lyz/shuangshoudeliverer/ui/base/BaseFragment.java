package com.lyz.shuangshoudeliverer.ui.base;

import android.support.v4.app.Fragment;


public abstract class BaseFragment extends Fragment {
    @Override
    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart(getClass().getName());
//        Log.e("name", getClass().getName().replaceAll("com.lyz.anxuquestionnaire.", ""));
    }


    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd(getClass().getName());
//        Log.e("name", getClass().getName().replaceAll("com.lyz.anxuquestionnaire.", ""));
    }
}
