package com.lyz.shuangshouproject.ui.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.lyz.shuangshouproject.ExitApplication;
import com.lyz.shuangshouproject.R;
import com.lyz.shuangshouproject.constant.StaticData;
import com.lyz.shuangshouproject.constant.StaticaAdaptive;
import com.lyz.shuangshouproject.httpapi.RetrofitHelper;
import com.lyz.shuangshouproject.ui.activity.CountDetailActivity;
import com.lyz.shuangshouproject.ui.activity.EvaluationSystermActivity;
import com.lyz.shuangshouproject.ui.activity.InformationActivity;
import com.lyz.shuangshouproject.ui.activity.MyShuangShouActivity;
import com.lyz.shuangshouproject.ui.activity.RecycleAddressActivity;
import com.lyz.shuangshouproject.ui.base.BaseActivity;
import com.lyz.shuangshouproject.ui.bean.UserDataBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by gazrey on 2016/4/12.
 * <p>
 * index 个人中心
 */
public class PersonCenterActivity extends BaseActivity {

    @BindView(R.id.imgLeftTab)
    ImageView imgLeftTab;
    @BindView(R.id.tvTitleTab)
    TextView tvTitleTab;
    @BindView(R.id.imgRightTab)
    ImageView imgRightTab;
    @BindView(R.id.imgPersonal)
    SimpleDraweeView imgPersonal;
    @BindView(R.id.tv_PersonalName)
    TextView tvPersonalName;
    @BindView(R.id.tvPersonalSign)
    TextView tvPersonalSign;
    @BindView(R.id.imgPersonalMyShuangShou)
    ImageView imgPersonalMyShuangShou;
    @BindView(R.id.tvPersonalMyShuangShou)
    TextView tvPersonalMyShuangShou;
    @BindView(R.id.linearPersonalMyShuangShou)
    LinearLayout linearPersonalMyShuangShou;
    @BindView(R.id.tvPersonalCountDetail)
    TextView tvPersonalCountDetail;
    @BindView(R.id.linearPersonalCountDetail)
    LinearLayout linearPersonalCountDetail;
    @BindView(R.id.tvPersonalInfor)
    TextView tvPersonalInfor;
    @BindView(R.id.linearPersonalInfor)
    LinearLayout linearPersonalInfor;
    @BindView(R.id.tvPersonalSet)
    TextView tvPersonalSet;
    @BindView(R.id.linearPersonalset)
    LinearLayout linearPersonalset;
    @BindView(R.id.tvPersonalAddress)
    TextView tvPersonalAddress;
    @BindView(R.id.linearPersonalAddress)
    LinearLayout linearPersonalAddress;
    @BindView(R.id.tvPersonalCustomerPhone)
    TextView tvPersonalCustomerPhone;
    @BindView(R.id.linearPersonalCustomerPhone)
    LinearLayout linearPersonalCustomerPhone;
    @BindView(R.id.tvPersonalComments)
    TextView tvPersonalComments;
    @BindView(R.id.linearPersonalComments)
    LinearLayout linearPersonalComments;
    @BindView(R.id.imgPersonalCountDetail)
    ImageView imgPersonalCountDetail;
    @BindView(R.id.imgPersonalInfor)
    ImageView imgPersonalInfor;
    @BindView(R.id.imgPersonalSet)
    ImageView imgPersonalSet;
    @BindView(R.id.imgPersonalAddress)
    ImageView imgPersonalAddress;
    @BindView(R.id.imgPersonalPhone)
    ImageView imgPersonalPhone;
    @BindView(R.id.imgPersonalComments)
    ImageView imgPersonalComments;
    private float index;

    private final int INTENT_FLAG_TO_PERSON = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticData.setTranslucentStatus(this);
        setContentView(R.layout.activity_person_center);
        ExitApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);

        initView();
    }


    public void initView() {
        SharedPreferences s = getSharedPreferences("index", Context.MODE_PRIVATE);
        index = s.getFloat("index", 0);

        tvTitleTab.setText("个人中心");
        StaticaAdaptive.adaptiveView(imgPersonalMyShuangShou, 40, 40, index);
        StaticaAdaptive.adaptiveView(imgPersonalCountDetail, 40, 40, index);
        StaticaAdaptive.adaptiveView(imgPersonalInfor, 40, 40, index);
        StaticaAdaptive.adaptiveView(imgPersonalSet, 40, 40, index);
        StaticaAdaptive.adaptiveView(imgPersonalAddress, 40, 40, index);
        StaticaAdaptive.adaptiveView(imgPersonalPhone, 40, 40, index);
        StaticaAdaptive.adaptiveView(imgPersonalComments, 40, 40, index);

        getUserData();


    }


    @OnClick({R.id.imgLeftTab, R.id.imgPersonal, R.id.linearPersonalMyShuangShou, R.id.linearPersonalCountDetail, R.id.linearPersonalInfor, R.id.linearPersonalset, R.id.linearPersonalAddress, R.id.linearPersonalCustomerPhone, R.id.linearPersonalComments})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.imgLeftTab:
                finish();
                break;
            case R.id.imgPersonal:
                Intent intent1 = new Intent(this, PersonDataActivity.class);
                startActivityForResult(intent1, INTENT_FLAG_TO_PERSON);
                break;
            case R.id.linearPersonalMyShuangShou:
                Intent intent6 = new Intent(this, MyShuangShouActivity.class);
                startActivity(intent6);
                break;
            case R.id.linearPersonalCountDetail:
                Intent intent4 = new Intent(this, CountDetailActivity.class);
                startActivity(intent4);
                break;
            case R.id.linearPersonalInfor:
                Intent intent7 = new Intent(this, InformationActivity.class);
                startActivity(intent7);
                break;
            case R.id.linearPersonalset:
                Intent intent2 = new Intent(this, PersonSetActivity.class);
                startActivity(intent2);
                break;
            case R.id.linearPersonalAddress:
                Intent intent3 = new Intent(this, RecycleAddressActivity.class);
                startActivity(intent3);
                break;
            case R.id.linearPersonalCustomerPhone:
                showCallPop();
                break;
            case R.id.linearPersonalComments:
//                Intent intent5 = new Intent(this,EvaluationActivity.class);
//                startActivity(intent5);
                Intent intent5 = new Intent(this, EvaluationSystermActivity.class);
                startActivity(intent5);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_FLAG_TO_PERSON && resultCode == Activity.RESULT_OK) {
            getUserData();
        }
    }

    private void getUserData() {
        RetrofitHelper.getNetworkService()
                .getUserData()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<UserDataBean>() {
                    @Override
                    public void onNext(UserDataBean value) {

                        tvPersonalName.setText(value.getRealname());
                        tvPersonalSign.setText(value.getNote());

                        imgPersonal.setController(StaticData.loadFrescoImg(Uri.parse(RetrofitHelper.ImgUrl + value.getImg()),
                                index, 120, 120, imgPersonal));

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

    private PopupWindow mPopupWindow;
    private View mPopView;

    private void showCallPop() {
        mPopView = getLayoutInflater().inflate(R.layout.pop_call_phone, null);
        final TextView tvPhone = (TextView) mPopView.findViewById(R.id.tvPhoneNumPerson);
        TextView tvCallPerson = (TextView) mPopView.findViewById(R.id.tvCallPerson);
        TextView tvPoneCallPerson = (TextView) mPopView.findViewById(R.id.tvPoneCallPerson);
        LinearLayout linearPopCall = (LinearLayout) mPopView.findViewById(R.id.linearPopCall);
        linearPopCall.setAlpha(1);

        mPopupWindow = new PopupWindow(mPopView, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        mPopupWindow.setBackgroundDrawable(dw);

        mPopView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int y = (int) motionEvent.getY();
                int height = mPopView.findViewById(R.id.linearPopCall).getTop();
                int bottom_h = mPopView.findViewById(R.id.linearPopCall).getBottom();

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height || y > bottom_h) {
                        mPopupWindow.dismiss();
                    }
                }
                return true;
            }
        });

        mPopupWindow.showAtLocation(tvTitleTab, Gravity.CENTER, 0, 0);

        tvCallPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tvPhone.getText().toString()));
                startActivity(intent);

            }
        });
        tvPoneCallPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
    }
}
