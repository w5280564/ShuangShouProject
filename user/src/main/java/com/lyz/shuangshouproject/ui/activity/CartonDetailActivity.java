package com.lyz.shuangshouproject.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.lyz.shuangshouproject.ExitApplication;
import com.lyz.shuangshouproject.R;
import com.lyz.shuangshouproject.constant.StaticData;
import com.lyz.shuangshouproject.httpapi.RetrofitHelper;
import com.lyz.shuangshouproject.ui.base.BaseActivity;
import com.lyz.shuangshouproject.ui.bean.BoxDetailBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * 纸箱信息
 */
public class CartonDetailActivity extends BaseActivity {


    @BindView(R.id.imgLeftTab)
    ImageView imgLeftTab;
    @BindView(R.id.tvTitleTab)
    TextView tvTitleTab;
    @BindView(R.id.imgCartonDetail)
    ImageView imgCartonDetail;
    @BindView(R.id.tvSizeCartonDetail)
    TextView tvSizeCartonDetail;
    @BindView(R.id.tvMaterialCartonDetail)
    TextView tvMaterialCartonDetail;
    @BindView(R.id.tvCountCartonDetail)
    TextView tvCountCartonDetail;
    @BindView(R.id.tvFromWhereCartonDetail)
    TextView tvFromWhereCartonDetail;

    private int mCartonId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticData.setTranslucentStatus(this);
        setContentView(R.layout.activity_carton_detail);
        ExitApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        tvTitleTab.setText("6号纸箱");
        mCartonId = getIntent().getIntExtra("cartonId",0);
        getCartonDetailFormId(String.valueOf(mCartonId));

    }

    /**
     * 获取纸箱信息
     */
    private void getCartonDetailFormId(String cartonId) {
        RetrofitHelper.getNetworkService()
                .getCartonDetailFormId(cartonId)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<BoxDetailBean>() {
                    @Override
                    public void onNext(BoxDetailBean value) {
//                        {"name":"11","point":0,"area":"","user__realname":null,"quality":"","id":1,"size":""}

                        tvTitleTab.setText(value.getName());
                        tvSizeCartonDetail.setText("尺寸："+value.getSize());
                        tvMaterialCartonDetail.setText("材质："+value.getQuality());
                        tvCountCartonDetail.setText("积分："+value.getPoint()+"点");
                        tvFromWhereCartonDetail.setText("产自："+value.getUser__realname()+"("+value.getArea()+")");
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


    @OnClick(R.id.imgLeftTab)
    public void onClick() {
        finish();
    }
}
