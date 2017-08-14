package com.lyz.shuangshouproject.ui.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.lyz.shuangshouproject.ExitApplication;
import com.lyz.shuangshouproject.R;
import com.lyz.shuangshouproject.constant.StaticData;
import com.lyz.shuangshouproject.httpapi.RetrofitHelper;
import com.lyz.shuangshouproject.ui.base.BaseActivity;
import com.lyz.shuangshouproject.ui.bean.UserAccountBean;
import com.lyz.shuangshouproject.utils.CleanCacheUtils;
import com.lyz.shuangshouproject.utils.ShareSaveUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * 个人设置
 */
public class PersonSetActivity extends BaseActivity {


    @BindView(R.id.imgLeftTab)
    ImageView imgLeftTab;
    @BindView(R.id.tvTitleTab)
    TextView tvTitleTab;
    @BindView(R.id.tvVersionNumName)
    TextView tvVersionNumName;
    @BindView(R.id.tvCacheSet)
    TextView tvCacheSet;
    @BindView(R.id.relClearCacheSet)
    RelativeLayout relClearCacheSet;
    @BindView(R.id.relAboutSet)
    RelativeLayout relAboutSet;
    @BindView(R.id.relFAQSet)
    RelativeLayout relFAQSet;
    @BindView(R.id.btnClearAccountSet)
    Button btnClearAccountSet;
    @BindView(R.id.activity_person_data)
    LinearLayout activityPersonData;
    private float index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticData.setTranslucentStatus(this);
        setContentView(R.layout.activity_person_set);
        ExitApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        SharedPreferences s = PersonSetActivity.this.getSharedPreferences("index", Context.MODE_PRIVATE);
        index = s.getFloat("index", 0);


        tvTitleTab.setText("个人设置");
        try {
            tvCacheSet.setText(CleanCacheUtils.getTotalCacheSize(PersonSetActivity.this));
        } catch (Exception e) {

        }
    }


    @OnClick({R.id.imgLeftTab, R.id.relClearCacheSet, R.id.relAboutSet, R.id.relFAQSet, R.id.btnClearAccountSet})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgLeftTab:
                finish();
                break;
            case R.id.relClearCacheSet:
                try {

                    CleanCacheUtils.clearAllCache(PersonSetActivity.this);
                    tvCacheSet.setText("0");

                } catch (Exception e) {

                }

                break;
            case R.id.relAboutSet:
                break;
            case R.id.relFAQSet:
                break;
            case R.id.btnClearAccountSet:
                logout();
                ShareSaveUtils.clearShareData("JSESSIONID", this);
                ShareSaveUtils.clearShareData("username", this);
                ShareSaveUtils.clearShareData("Address", this);
                Intent intent = new Intent(PersonSetActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();


                break;
        }
    }

    private void logout() {
        RetrofitHelper.getNetworkService()
                .logout()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<UserAccountBean>() {
                    @Override
                    public void onNext(UserAccountBean value) {

                        if (value.isSuccess()) {

                        }
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
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    /**
     * 2  * 获取版本号
     * 3  * @return 当前应用的版本号
     * 4
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return this.getString(R.string.version_name) + version;

        } catch (Exception e) {
            e.printStackTrace();
            return this.getString(R.string.version_name);
        }

    }

}
