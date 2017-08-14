package com.lyz.shuangshouproject;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;

import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.lyz.shuangshouproject.httpapi.ImagePipelineConfigUtils;
import com.lyz.shuangshouproject.utils.LocationService;
import com.lyz.shuangshouproject.utils.ShareSaveUtils;
import com.lyz.shuangshouproject.utils.ToastUtils;

import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Administrator on 2016/5/5.
 * 添加activity到栈中
 */
public class ExitApplication extends Application {

    private List<Activity> activityList = new LinkedList();
    private static ExitApplication instance;
    public LocationService locationService;

    public Vibrator mVibrator;

    private static Context context;
    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;


    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        sp = this.getSharedPreferences(ShareSaveUtils.SHARED_MAIN, Context.MODE_PRIVATE);
        editor = sp.edit();

        ToastUtils.init(this);
        //初始化Fresco
        Fresco.initialize(this, ImagePipelineConfigUtils.getDefaultImagePipelineConfig(this));

        /***
         * 初始化定位sdk，建议在Application中创建
         */
        SDKInitializer.initialize(this);
        locationService = new LocationService(getApplicationContext());
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);

        initRealm();
    }

    public static Context getContext() {
        return context;
    }

    public static SharedPreferences getSP() {
        return sp;
    }

    public static SharedPreferences.Editor getSpEditor() {
        return editor;
    }

    //单例模式中获取唯一的ExitApplication实例
    public static ExitApplication getInstance() {
        if (null == instance) {
            instance = new ExitApplication();
        }
        return instance;
    }

    //添加Activity到容器中
    public void addActivity(Activity activity) {

        activityList.add(activity);
    }
    //遍历所有Activity并finish

    public void exit() {

        for (Activity activity : activityList) {
            activity.finish();
        }

        System.exit(0);

    }

    private void initRealm() {

        //初始化 realm 数据存储的框架
        Realm.init(getApplicationContext());
//        Migration migration = new Migration();
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("newCarton.realm") //文件名
                .schemaVersion(1) //版本号
//                .migration(migration)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.getInstance(config);

    }


}


