package com.lyz.shuangshoudeliverer.ui.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lyz.shuangshoudeliverer.ExitApplication;
import com.lyz.shuangshoudeliverer.R;
import com.lyz.shuangshoudeliverer.constant.StaticData;
import com.lyz.shuangshoudeliverer.constant.StaticaAdaptive;
import com.lyz.shuangshoudeliverer.ui.base.BaseActivity;
import com.lyz.shuangshoudeliverer.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {


    @BindView(R.id.imgLeftTab)
    ImageView imgLeftTab;
    @BindView(R.id.tvTitleTab)
    TextView tvTitleTab;
    @BindView(R.id.etAccountRegister)
    EditText etAccountRegister;
    @BindView(R.id.imgAccountRegister)
    ImageView imgAccountRegister;
    @BindView(R.id.etPswRegister)
    EditText etPswRegister;
    @BindView(R.id.imgPswRegister)
    ImageView imgPswRegister;
    @BindView(R.id.tvNextRegister)
    TextView tvNextRegister;
    @BindView(R.id.activity_login)
    LinearLayout activityLogin;

    private boolean mFlagPswVisiable = false; //密码不可见

    private String mState = "1"; //人员类型：state（0：普通用户，1：快递员，2：管理员，3：商家）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticData.setTranslucentStatus(this);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        ExitApplication.getInstance().addActivity(this);

        initView();
    }

    private void initView() {
        SharedPreferences s = RegisterActivity.this.getSharedPreferences("index", Context.MODE_PRIVATE);
        float index = s.getFloat("index", 0);
        StaticaAdaptive.adaptiveView(imgAccountRegister,40,40,index);
        StaticaAdaptive.adaptiveView(imgPswRegister,40,40,index);
        tvTitleTab.setText("注册");
    }

//    private void register() {
//        RetrofitHelper.getNetworkService()
//                .register(etAccountRegister.getText().toString().trim(), etPswRegister.getText().toString().trim(), mState)
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new DefaultObserver<UserAccountBean>() {
//                    @Override
//                    public void onNext(UserAccountBean value) {
//
//                        if (value.isSuccess()) {
//                            //注册成功，先调用登录的接口，获取session
//                            login();
//
//                        } else {
//                            int err_code = value.getErr_code();
//                            if (err_code == 1001) {
//                                ToastUtils.getInstance().showToast("密码错误");
//                            } else if (err_code == 1002) {
//                                ToastUtils.getInstance().showToast("用户名已被使用");
//                            } else if (err_code == 1003) {
//                                ToastUtils.getInstance().showToast("手机号或密码为空");
//                            } else if (err_code == 1004) {
//                                ToastUtils.getInstance().showToast("用户被禁止");
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        ToastUtils.getInstance().showToast("注册失败");
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//
//    }


    @OnClick({R.id.imgLeftTab, R.id.imgAccountRegister, R.id.imgPswRegister, R.id.tvNextRegister})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgLeftTab:
                finish();
                break;
            case R.id.imgAccountRegister:
                etAccountRegister.setText("");
                etAccountRegister.setHint("请设置新账号");
                break;
            case R.id.imgPswRegister:

                if (mFlagPswVisiable) { //设置不密码可见
                    etPswRegister.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                    mFlagPswVisiable = false;
                    imgPswRegister.setImageResource(R.drawable.psw_invisiable);
                } else { //设置密码可见
                    etPswRegister.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    imgPswRegister.setImageResource(R.drawable.psw_visiable);
                    mFlagPswVisiable = true;
                }

                break;
            case R.id.tvNextRegister:

                if (TextUtils.isEmpty(etAccountRegister.getText().toString())) {
                    ToastUtils.getInstance().showToast("请输入用户名");
                    return;
                }
                if (TextUtils.isEmpty(etPswRegister.getText().toString())) {
                    ToastUtils.getInstance().showToast("请输入密码");
                    return;
                }

                if (StaticData.FLAG_USER){
                    mState = "0";
                }else {
                    mState = "1";
                }

                Intent intent = new Intent(RegisterActivity.this, PerfectInforActivity.class);
                intent.putExtra("username", etAccountRegister.getText().toString().trim());
                intent.putExtra("psw", etPswRegister.getText().toString().trim());
                startActivity(intent);
                finish();

//                register();

                break;

        }
    }



}
