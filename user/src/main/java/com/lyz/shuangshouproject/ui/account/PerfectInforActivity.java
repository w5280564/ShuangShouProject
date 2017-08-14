package com.lyz.shuangshouproject.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.lyz.shuangshouproject.ExitApplication;
import com.lyz.shuangshouproject.R;
import com.lyz.shuangshouproject.constant.StaticData;
import com.lyz.shuangshouproject.constant.StaticaAdaptive;
import com.lyz.shuangshouproject.httpapi.RetrofitHelper;
import com.lyz.shuangshouproject.ui.activity.HomeActivity;
import com.lyz.shuangshouproject.ui.base.BaseActivity;
import com.lyz.shuangshouproject.ui.bean.UserAccountBean;
import com.lyz.shuangshouproject.utils.ShareSaveUtils;
import com.lyz.shuangshouproject.utils.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


/**
 * 完善信息
 */
public class PerfectInforActivity extends BaseActivity {

    @BindView(R.id.imgLeftTab)
    ImageView imgLeftTab;
    @BindView(R.id.tvTitleTab)
    TextView tvTitleTab;
    @BindView(R.id.iconPerfect)
    SimpleDraweeView iconPerfect;
    @BindView(R.id.relPerfectIcon)
    RelativeLayout relPerfectIcon;
    @BindView(R.id.etPerfectName)
    EditText etPerfectName;
    @BindView(R.id.relPerfectName)
    LinearLayout relPerfectName;
    @BindView(R.id.tvPerfectSex)
    TextView tvPerfectSex;
    @BindView(R.id.relPerfectSex)
    LinearLayout relPerfectSex;
    @BindView(R.id.tvPerfectPhone)
    EditText tvPerfectPhone;
    @BindView(R.id.relPerfectPhone)
    LinearLayout relPerfectPhone;
    @BindView(R.id.etPerfectSign)
    EditText etPerfectSign;
    @BindView(R.id.relPerfectDataJob)
    LinearLayout relPerfectDataJob;
    @BindView(R.id.tvPerfectWordsNum)
    TextView tvPerfectWordsNum;
    @BindView(R.id.btnPerfectFinish)
    Button btnPerfectFinish;
    private Uri imguri;
    private Bitmap myBitmap;
    private File file;

    private static final int GET_IMG_FROM_PIC = 1;

    private static final int GET_IMG_TAKE_PHOTO = 2;
    private float index;

    private int sex = 1;

    private String mUserName, mPsw;

    private PackageManager pm;


    private PopupWindow mPopupWindow;
    private View mPopView;

    private PopupWindow mPopupWindowPic;
    private View mPopViewPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticData.setTranslucentStatus(this);
        setContentView(R.layout.activity_perfect);
        ExitApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        DisplayMetrics DisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(DisplayMetrics);
        float width = DisplayMetrics.widthPixels;
        index = width / 750; //以750像素的宽度为标准

        tvTitleTab.setText("完善信息");
        mUserName = getIntent().getStringExtra("username");
        mPsw = getIntent().getStringExtra("psw");

        pm = this.getPackageManager();

        showSexPop();
        showPicPop();

        etPerfectSign.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !TextUtils.isEmpty(s.toString())) {
                    tvPerfectWordsNum.setText(s.toString().length() + "/50");

                    if (s.toString().length() > 50) {
                        ToastUtils.getInstance().showToast("个性签名最多输入50字");
                    }

                } else {
                    tvPerfectWordsNum.setText("0/50");
                }
            }
        });
    }

    @OnClick({R.id.imgLeftTab, R.id.iconPerfect, R.id.relPerfectSex, R.id.btnPerfectFinish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgLeftTab:
                finish();
                break;

            case R.id.iconPerfect:
//                三星手机需要判断权限，让用户手动打开权限
                boolean permission = (PackageManager.PERMISSION_GRANTED ==
                        pm.checkPermission("android.permission.CAMERA", StaticData.PACKAGE_NAME));
                if (!permission) {
                    Toast.makeText(this, "未获得手机相机权限", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean permission1 = (PackageManager.PERMISSION_GRANTED ==
                        pm.checkPermission("android.permission.READ_EXTERNAL_STORAGE", StaticData.PACKAGE_NAME));
                if (!permission1) {
                    Toast.makeText(this, "未获得手机读取文件的权限", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean permission2 = (PackageManager.PERMISSION_GRANTED ==
                        pm.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", StaticData.PACKAGE_NAME));
                if (!permission2) {
                    Toast.makeText(this, "未获得手机读取文件的权限", Toast.LENGTH_SHORT).show();
                    return;
                }

                StaticaAdaptive.HideKeyboard(iconPerfect);
                mPopupWindowPic.showAtLocation(tvTitleTab, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.relPerfectSex:

                StaticaAdaptive.HideKeyboard(relPerfectSex);
                mPopupWindow.showAtLocation(tvTitleTab, Gravity.BOTTOM, 0, 0);

                break;
            case R.id.btnPerfectFinish:

                if (TextUtils.isEmpty(etPerfectName.getText().toString().trim())) {
                    ToastUtils.getInstance().showToast("请输入姓名");
                    return;
                }
                if (TextUtils.isEmpty(tvPerfectPhone.getText().toString().trim())) {
                    ToastUtils.getInstance().showToast("请输入手机号");
                    return;
                }
                if (TextUtils.isEmpty(etPerfectSign.getText().toString().trim())) {
                    ToastUtils.getInstance().showToast("请输入个性签名");
                    return;
                }

                saveData();

                break;
        }
    }


    private void saveData() {

        MultipartBody.Part body = null;

        if (file == null) {
            ToastUtils.getInstance().showToast("请选择头像");
            return;
        }

        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        body = MultipartBody.Part.createFormData("file", "ShuangShou" + StaticaAdaptive.getStringRandom(6) + ".png", requestFile);

        RequestBody nameBody = RequestBody.create(MediaType.parse("multipart/form-data"), etPerfectName.getText().toString().trim());
        RequestBody phoneBody = RequestBody.create(MediaType.parse("multipart/form-data"), tvPerfectPhone.getText().toString().trim());
        RequestBody sexBody = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(sex));
        RequestBody signBody = RequestBody.create(MediaType.parse("multipart/form-data"), etPerfectSign.getText().toString().trim());

        RetrofitHelper.getNetworkService()
                .perfectUserData(nameBody, phoneBody, sexBody, signBody, body)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<UserAccountBean>() {
                    @Override
                    public void onNext(UserAccountBean value) {
                        if (value.isSuccess()) {
                            ToastUtils.getInstance().showToast("注册成功");
                            ShareSaveUtils.saveShareData("username", mUserName, PerfectInforActivity.this);
                            Intent intent = new Intent(PerfectInforActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                case GET_IMG_FROM_PIC: //从相册中选择

                    imguri = data.getData();
                    try {

                        String path = StaticData.getImageAbsolutePath(PerfectInforActivity.this, imguri);
                        myBitmap = StaticData.loadingImageBitmap(PerfectInforActivity.this, path, 480);
                        file = new File(path);
                        FileOutputStream out = new FileOutputStream(file);
                        myBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                    } catch (Exception e) {

                    }

                    iconPerfect.setController(StaticData.loadFrescoImg(imguri, index, 100, 100, iconPerfect));

                    if (myBitmap != null && !myBitmap.isRecycled()) {
                        myBitmap.recycle();
                        myBitmap = null;
                    }

                    break;
                case GET_IMG_TAKE_PHOTO: //拍照

                    File tempFile = new File(Environment.getExternalStorageDirectory(), "IMGface");
                    Uri _imguri = Uri.parse("file://" + tempFile.getPath());

                    try {

                        myBitmap = StaticData.loadingImageBitmap(this, StaticaAdaptive.getRealFilePath(this, _imguri), 480);
                        file = new File(Environment.getExternalStorageDirectory(), "AnXu" + StaticaAdaptive.getStringRandom(6));
                        FileOutputStream out = new FileOutputStream(file);
                        myBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();

                        Uri nowuri = Uri.parse("file://" + file.getPath());

                        iconPerfect.setController(StaticData.loadFrescoImg(nowuri, index, 100, 100, iconPerfect));

                        if (myBitmap != null && !myBitmap.isRecycled()) {
                            myBitmap.recycle();
                            myBitmap = null;
                        }

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }


                    break;
            }
        }
    }

    private void showSexPop() {
        mPopView = getLayoutInflater().inflate(R.layout.pop_select_item, null);

        final TextView tvPopItem1 = (TextView) mPopView.findViewById(R.id.tvPopItem1);
        final TextView tvPopItem2 = (TextView) mPopView.findViewById(R.id.tvPopItem2);
        final TextView tvPopItemCancle = (TextView) mPopView.findViewById(R.id.tvPopItemCancle);

        StaticaAdaptive.adaptiveView(tvPopItem1, 713, 120, index);
        StaticaAdaptive.adaptiveView(tvPopItem2, 713, 120, index);
        StaticaAdaptive.adaptiveView(tvPopItemCancle, 713, 120, index);
        tvPopItem1.setText("男");
        tvPopItem2.setText("女");
        LinearLayout linearPopSort = (LinearLayout) mPopView.findViewById(R.id.linearPopSelect);
        linearPopSort.setAlpha(1);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linearPopSort.getLayoutParams();
        params.topMargin = (int) (160 * index);
        linearPopSort.setLayoutParams(params);

        mPopupWindow = new PopupWindow(mPopView, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        mPopupWindow.setBackgroundDrawable(dw);

        mPopView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int y = (int) motionEvent.getY();
                int height = mPopView.findViewById(R.id.linearPopSelect).getTop();
                int bottom_h = mPopView.findViewById(R.id.linearPopSelect).getBottom();

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height || y > bottom_h) {
                        mPopupWindow.dismiss();
                    }
                }
                return true;
            }
        });

        tvPopItem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sex = 1;

                tvPerfectSex.setText("男");
                mPopupWindow.dismiss();
            }
        });

        tvPopItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sex = 2;

                tvPerfectSex.setText("女");
                mPopupWindow.dismiss();
            }
        });
        tvPopItemCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPopupWindow.dismiss();
            }
        });

    }

    private void showPicPop() {
        mPopViewPic = getLayoutInflater().inflate(R.layout.pop_select_item, null);

        final TextView tvPopItem1 = (TextView) mPopViewPic.findViewById(R.id.tvPopItem1);
        final TextView tvPopItem2 = (TextView) mPopViewPic.findViewById(R.id.tvPopItem2);
        final TextView tvPopItemCancle = (TextView) mPopViewPic.findViewById(R.id.tvPopItemCancle);

        StaticaAdaptive.adaptiveView(tvPopItem1, 713, 120, index);
        StaticaAdaptive.adaptiveView(tvPopItem2, 713, 120, index);
        StaticaAdaptive.adaptiveView(tvPopItemCancle, 713, 120, index);
        tvPopItem1.setText("拍照");
        tvPopItem2.setText("从相册中选择");
        LinearLayout linearPopSort = (LinearLayout) mPopViewPic.findViewById(R.id.linearPopSelect);
        linearPopSort.setAlpha(1);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linearPopSort.getLayoutParams();
        params.topMargin = (int) (160 * index);
        linearPopSort.setLayoutParams(params);

        mPopupWindowPic = new PopupWindow(mPopViewPic, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, true);
        mPopupWindowPic.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        mPopupWindowPic.setBackgroundDrawable(dw);

        mPopViewPic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int y = (int) motionEvent.getY();
                int height = mPopViewPic.findViewById(R.id.linearPopSelect).getTop();
                int bottom_h = mPopViewPic.findViewById(R.id.linearPopSelect).getBottom();

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height || y > bottom_h) {
                        mPopupWindowPic.dismiss();
                    }
                }
                return true;
            }
        });

        tvPopItem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                // 判断存储卡是否可以用，可用进行存储
                if (StaticaAdaptive.hasSdcard()) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "IMGface")));
                }
                startActivityForResult(intent, GET_IMG_TAKE_PHOTO);

                mPopupWindowPic.dismiss();
            }
        });

        tvPopItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_load = new Intent(Intent.ACTION_PICK, null);
                intent_load.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent_load, GET_IMG_FROM_PIC);

                mPopupWindowPic.dismiss();
            }
        });
        tvPopItemCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPopupWindowPic.dismiss();
            }
        });

    }
}
