package com.lyz.shuangshouproject.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.lyz.shuangshouproject.ExitApplication;
import com.lyz.shuangshouproject.R;
import com.lyz.shuangshouproject.constant.StaticData;
import com.lyz.shuangshouproject.httpapi.RetrofitHelper;
import com.lyz.shuangshouproject.ui.activity.HomeActivity;
import com.lyz.shuangshouproject.ui.base.BaseActivity;
import com.lyz.shuangshouproject.ui.bean.UserAccountBean;
import com.lyz.shuangshouproject.utils.ShareSaveUtils;
import com.lyz.shuangshouproject.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.etLoginPhone)
    EditText etLoginPhone;
    @BindView(R.id.linearLoginPhone)
    LinearLayout linearLoginPhone;
    @BindView(R.id.etLoginPassword)
    EditText etLoginPassword;
    @BindView(R.id.linearLoginPassword)
    LinearLayout linearLoginPassword;
    @BindView(R.id.tvLoginForget)
    TextView tvLoginForget;
    @BindView(R.id.tvLoginRegister)
    TextView tvLoginRegister;
    @BindView(R.id.activity_login)
    LinearLayout activityLogin;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.tvRegisterLogin)
    TextView tvRegisterLogin;

    private long secondTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticData.setTranslucentStatus(this);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        ExitApplication.getInstance().addActivity(this);

        String username = ShareSaveUtils.getShareData("username", this);

        if (!TextUtils.isEmpty(username)) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }
    }

    @OnClick({R.id.linearLoginPhone, R.id.linearLoginPassword, R.id.tvLoginForget, R.id.tvRegisterLogin,
            R.id.btnLogin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linearLoginPhone:
                break;
            case R.id.linearLoginPassword:
                break;
            case R.id.tvLoginForget:
//                Intent intent = new Intent(LoginActivity.this, ForgetPwActivity.class);
//                startActivity(intent);
                break;
            case R.id.tvRegisterLogin:
                Intent intent1 = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent1);
                break;
            case R.id.btnLogin:

                if (TextUtils.isEmpty(etLoginPhone.getText().toString())) {
                    ToastUtils.getInstance().showToast(getString(R.string.login_phone));
                    return;
                }
                if (TextUtils.isEmpty(etLoginPassword.getText().toString())) {
                    ToastUtils.getInstance().showToast(getString(R.string.login_password));
                    return;
                }


                login();

                break;
        }
    }

    private void login() {
        RetrofitHelper.getNetworkService()
                .login(etLoginPhone.getText().toString().trim(), etLoginPassword.getText().toString().trim())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<UserAccountBean>() {
                    @Override
                    public void onNext(UserAccountBean value) {

//                        {"type":0,"success":true}

                        if (value.isSuccess()) {
                            ShareSaveUtils.saveShareData("username", etLoginPhone.getText().toString(), LoginActivity.this);
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            int err_code = value.getErr_code();
                            if (err_code == 1001) {
                                ToastUtils.getInstance().showToast("密码或者用户名输入错误");
                            } else if (err_code == 1002) {
                                ToastUtils.getInstance().showToast("您的账号还未注册");
                            } else if (err_code == 1003) {
                                ToastUtils.getInstance().showToast("手机号或密码为空");
                            } else if (err_code == 1004) {
                                ToastUtils.getInstance().showToast("该账号被禁止");
                            }
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - secondTime > 2000) {
                secondTime = System.currentTimeMillis();
                ToastUtils.getInstance().showToast(getResources().getString(R.string.app_exit_sure));
            } else {
                finish();
//                System.exit(0);
                ExitApplication.getInstance().exit();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
