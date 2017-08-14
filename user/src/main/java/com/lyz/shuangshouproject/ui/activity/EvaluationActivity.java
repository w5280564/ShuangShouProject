package com.lyz.shuangshouproject.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.lyz.shuangshouproject.ExitApplication;
import com.lyz.shuangshouproject.R;
import com.lyz.shuangshouproject.constant.StaticData;
import com.lyz.shuangshouproject.httpapi.RetrofitHelper;
import com.lyz.shuangshouproject.ui.base.BaseActivity;
import com.lyz.shuangshouproject.ui.bean.UserAccountBean;
import com.lyz.shuangshouproject.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;

public class EvaluationActivity extends BaseActivity {


    @BindView(R.id.imgLeftTab)
    ImageView imgLeftTab;
    @BindView(R.id.tvTitleTab)
    TextView tvTitleTab;
    @BindView(R.id.etEvaluation)
    EditText etEvaluation;
    @BindView(R.id.tvSubmitEvaluation)
    TextView tvSubmitEvaluation;
    @BindView(R.id.tvWordNumEvaluation)
    TextView tvWordNumEvaluation;

    private int mOrderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticData.setTranslucentStatus(this);
        setContentView(R.layout.activity_evaluation);
        ExitApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        tvTitleTab.setText("评价");

        mOrderId = getIntent().getIntExtra("orderId", 0);
        String content = getIntent().getStringExtra("content");
        if (!TextUtils.isEmpty(content)){
            etEvaluation.setText(content);
            tvWordNumEvaluation.setText(content.length() + "/200");
        }


        etEvaluation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !TextUtils.isEmpty(s.toString())) {
                    tvWordNumEvaluation.setText(s.toString().length() + "/200");

                    if (s.toString().length() > 200) {
                        ToastUtils.getInstance().showToast("评价内容最多输入200字");
                    }

                } else {
                    tvWordNumEvaluation.setText("0/200");
                }
            }
        });
    }

    @OnClick({R.id.imgLeftTab, R.id.tvSubmitEvaluation})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgLeftTab:
                finish();
                break;
            case R.id.tvSubmitEvaluation:
                if (TextUtils.isEmpty(etEvaluation.getText().toString())) {
                    ToastUtils.getInstance().showToast("请输入评价内容");
                    return;
                }
                if (etEvaluation.getText().toString().length() > 200) {
                    ToastUtils.getInstance().showToast("评价内容最多输入200字");
                    return;
                }

                commentOrder(mOrderId, etEvaluation.getText().toString());

                break;
        }
    }

    private void commentOrder(int orderId, String content) {
        RetrofitHelper.getNetworkService()
                .commentOrder(orderId, content)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<UserAccountBean>() {
                    @Override
                    public void onNext(UserAccountBean value) {
                        if (value.isSuccess()) {
                            ToastUtils.getInstance().showToast("评价成功");
                            setResult(Activity.RESULT_OK);
                            finish();
                        } else {
                            ToastUtils.getInstance().showToast("评价失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.getInstance().showToast("评价失败");
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
}
