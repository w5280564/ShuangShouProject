package com.lyz.shuangshoudeliverer.ui.service;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.lyz.shuangshoudeliverer.httpapi.RetrofitHelper;
import com.lyz.shuangshoudeliverer.ui.bean.UserAccountBean;
import com.lyz.shuangshoudeliverer.utils.ToastUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Admin on 2017/7/24.
 *
 */

public class GpsLocationListener implements LocationListener {

    public int GPSCurrentStatus;
    private static final float minAccuracyMeters = 35;
    private static final int duration = 10;

    @Override
    public void onLocationChanged(Location location) {
        Log.e("Service","---定位失败");
        if (null != location) {
            if (location.hasAccuracy() && location.getAccuracy() <= minAccuracyMeters) {
                Log.e("Service","---定位成功");
                ToastUtils.getInstance().showToast("发送位置");
                upLoadUserPlace(location.getLongitude(),location.getLatitude());
            }

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        GPSCurrentStatus = status;
        Log.e("Service","-onStatusChanged--定位成功");
    }


    @Override
    public void onProviderEnabled(String provider) {
        Log.e("Service","-onProviderEnabled--定位成功");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.e("Service","-onProviderDisabled--定位成功");
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

                        ToastUtils.getInstance().showToast("发送位置成功");
                        Log.e("----Service", "" + value.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException) { // 网络错误
                            HttpException httpEx = (HttpException) e;
                            String responseMsg = httpEx.getMessage();
                            Log.e("----TAG网络错误", "" + responseMsg);
                            // ...
                        } else { // 其他错误
                            Log.e("----TAG其他错误", "" + e.getMessage());
                            // ...
                        }

                        Log.e("----Service", "" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

}
