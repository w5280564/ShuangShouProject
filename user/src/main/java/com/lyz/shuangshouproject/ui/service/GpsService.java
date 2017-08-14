package com.lyz.shuangshouproject.ui.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.lyz.shuangshouproject.httpapi.RetrofitHelper;
import com.lyz.shuangshouproject.ui.bean.UserAccountBean;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Admin on 2017/7/24.
 *
 */

public class GpsService extends Service {

    //定位点信息
    public LatLng latlng;
    private LocationClient mLocationClient =null;//定位客户端
    public MyLocationListener mMyLocationListener = new MyLocationListener();;
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private boolean isStop = false;


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.setLocOption(setLocationClientOption());
        mLocationClient.registerLocationListener(mMyLocationListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 触发定时器
        if (!isStop) {
            Log.i("tag", "定时器启动");
            startTimer();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        if (mLocationClient!=null) {
            mLocationClient.stop();
        }
        super.onDestroy();
        // 停止定时器
        if (isStop) {
            Log.i("tag", "定时器服务停止");
            stopTimer();
        }
    }
    /**
     * 定时器 每隔一段时间执行一次
     */
    private void startTimer() {
        isStop = true;//定时器启动后，修改标识，关闭定时器的开关
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {

                @Override
                public void run() {
                    do {
                        try {
                            Log.d("tag", "isStop="+isStop);
                            Log.d("tag", "mMyLocationListener="+mMyLocationListener);
                            mLocationClient.start();
                            Log.d("tag", "mLocationClient.start()");
                            Log.d("tag", "mLocationClient=="+mLocationClient);
                            Thread.sleep(1000*60*10);//10分钟定位一次后再次执行
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } while (isStop);

                }
            };
        }

        if (mTimer != null && mTimerTask != null) {
            Log.d("tag", "mTimer.schedule(mTimerTask, delay)");
            mTimer.schedule(mTimerTask, 0);//执行定时器中的任务
        }
    }
    /**
     * 停止定时器，初始化定时器开关
     */
    private void stopTimer() {

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        isStop = false;//重新打开定时器开关
        Log.d("tag", "isStop="+isStop);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 定位客户端参数设定，更多参数设置，查看百度官方文档
     * @return
     */
    private LocationClientOption setLocationClientOption() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setScanSpan(1000);//每隔1秒发起一次定位
        option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
        option.setOpenGps(true);//是否打开gps
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到该描述，不设置则在4G情况下会默认定位到“天安门广场”
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要，不设置则拿不到定位点的省市区信息
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        /*可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        该参数若不设置，则在4G状态下，会出现定位失败，将直接定位到天安门广场
         */
        return option;
    }
    /**
     * 定位监听器
     * @author User
     *
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location==null) {
                return;
            }
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            latlng = new LatLng(lat, lng);

            upLoadUserPlace(lng,lat);

            // 到此定位成功，没有必要反复定位
            // 应该停止客户端再发送定位请求
            if (mLocationClient.isStarted()) {
                Log.d("tag", "mLocationClient.isStarted()==>mLocationClient.stop()");
                mLocationClient.stop();

            }

        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }

    }

    /**
     * 向服务器发送位置
     */
    private void upLoadUserPlace(double lng,double lat) {
        RetrofitHelper.getNetworkService()
                .upLoadUserPlace(lng,lat)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<UserAccountBean>() {
                    @Override
                    public void onNext(UserAccountBean value) {
                        Log.e("----tag", "" + value.isSuccess());
                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.e("----tag", "" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
