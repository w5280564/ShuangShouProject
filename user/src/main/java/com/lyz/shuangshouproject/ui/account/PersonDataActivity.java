package com.lyz.shuangshouproject.ui.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.lyz.shuangshouproject.ui.adapter.SelectAdapter;
import com.lyz.shuangshouproject.ui.base.BaseActivity;
import com.lyz.shuangshouproject.ui.bean.UserAccountBean;
import com.lyz.shuangshouproject.ui.bean.UserDataBean;
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
 * 个人信息
 */
public class PersonDataActivity extends BaseActivity {

    @BindView(R.id.iconPersonData)
    SimpleDraweeView iconPersonData;
    @BindView(R.id.relPersonDataIcon)
    RelativeLayout relPersonDataIcon;
    @BindView(R.id.etPersonDataName)
    EditText etPersonDataName;
    @BindView(R.id.relPersonDataName)
    RelativeLayout relPersonDataName;
    @BindView(R.id.tvPersonDataSex)
    TextView tvPersonDataSex;
    @BindView(R.id.relPersonDataSex)
    RelativeLayout relPersonDataSex;
    @BindView(R.id.relPersonDataJob)
    LinearLayout relPersonDataJob;
    @BindView(R.id.btnPerInforFinish)
    Button btnPerInforFinish;
    @BindView(R.id.activity_person_data)
    LinearLayout activityPersonData;
    @BindView(R.id.imgLeftTab)
    ImageView imgLeftTab;
    @BindView(R.id.tvTitleTab)
    TextView tvTitleTab;
    @BindView(R.id.imgRightTab)
    ImageView imgRightTab;
    @BindView(R.id.tvPersonDataPhone)
    EditText tvPersonDataPhone;
    @BindView(R.id.relPersonDataPhone)
    RelativeLayout relPersonDataPhone;
    @BindView(R.id.etPersonDataSign)
    EditText etPersonDataSign;


    private Uri imguri;
    private Bitmap myBitmap;
    private File file;

    private static final int GET_IMG_FROM_PIC = 1;

    private static final int GET_IMG_TAKE_PHOTO = 2;

    private float index;
    private SexWindow sexWindow;
    private String[] sexarr = {"男", "女"};

    private PackageManager pm;

    private int sex = 1;

    private PicWindow picWindow;
    private String[] picarr = {"拍照", "从相册中选择"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticData.setTranslucentStatus(this);
        setContentView(R.layout.activity_person_data);
        ExitApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);

        initView();
        getUserData();

    }

    private void initView() {
        SharedPreferences s = PersonDataActivity.this.getSharedPreferences("index", Context.MODE_PRIVATE);
        index = s.getFloat("index", 0);

        imgRightTab.setVisibility(View.GONE);
        tvTitleTab.setText(getResources().getString(R.string.personData));

        pm = this.getPackageManager();


    }

    @OnClick({R.id.imgLeftTab, R.id.relPersonDataIcon, R.id.relPersonDataName, R.id.relPersonDataSex, R.id.btnPerInforFinish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgLeftTab:
                finish();
                break;
            case R.id.relPersonDataIcon:

                //三星手机需要判断权限，让用户手动打开权限
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

                StaticaAdaptive.HideKeyboard(relPersonDataIcon);
                if (picWindow != null) {
                    picWindow.dismiss();
                }
                picWindow = new PicWindow(this, relPersonDataIcon);


                break;

            case R.id.relPersonDataSex:
                StaticaAdaptive.HideKeyboard(relPersonDataSex);
                if (sexWindow != null) {
                    sexWindow.dismiss();
                }
                sexWindow = new SexWindow(this, relPersonDataSex);
                break;

            case R.id.btnPerInforFinish:

                if (TextUtils.isEmpty(etPersonDataName.getText().toString().trim())) {
                    ToastUtils.getInstance().showToast("请输入姓名");
                    return;
                }
                if (TextUtils.isEmpty(tvPersonDataPhone.getText().toString().trim())) {
                    ToastUtils.getInstance().showToast("请输入手机号");
                    return;
                }
                if (TextUtils.isEmpty(etPersonDataSign.getText().toString().trim())) {
                    ToastUtils.getInstance().showToast("请输入个性签名");
                    return;
                }

                saveData();

                break;
        }
    }


    private void saveData() {

        MultipartBody.Part body = null;

        if (file != null) {
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body = MultipartBody.Part.createFormData("file", "ShuangShou" + StaticaAdaptive.getStringRandom(6) + ".png", requestFile);
        }

        RequestBody nameBody = RequestBody.create(MediaType.parse("multipart/form-data"), etPersonDataName.getText().toString().trim());
        RequestBody phoneBody = RequestBody.create(MediaType.parse("multipart/form-data"), tvPersonDataPhone.getText().toString().trim());
        RequestBody sexBody = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(sex));
        RequestBody signBody = RequestBody.create(MediaType.parse("multipart/form-data"), etPersonDataSign.getText().toString().trim());

        RetrofitHelper.getNetworkService()
                .perfectUserData(nameBody, phoneBody, sexBody, signBody, body)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<UserAccountBean>() {
                    @Override
                    public void onNext(UserAccountBean value) {
                        if (value.isSuccess()) {

                            //完成之后 直接调登录的接口
                            setResult(Activity.RESULT_OK);
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

                case GET_IMG_FROM_PIC:

                    imguri = data.getData();
                    try {

                        String path = StaticData.getImageAbsolutePath(PersonDataActivity.this, imguri);
                        myBitmap = StaticData.loadingImageBitmap(PersonDataActivity.this, path, 480);
                        file = new File(path);
                        FileOutputStream out = new FileOutputStream(file);
                        myBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                    } catch (Exception e) {

                    }

                    iconPersonData.setController(StaticData.loadFrescoImg(imguri, index, 100, 100, iconPersonData));

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

                        iconPersonData.setController(StaticData.loadFrescoImg(nowuri, index, 100, 100, iconPersonData));

                        if (myBitmap != null && !myBitmap.isRecycled()) {
                            myBitmap.recycle();
                            myBitmap = null;
                        }

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

            }
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

                        etPersonDataName.setText(value.getRealname());
                        etPersonDataName.setSelection(etPersonDataName.getText().length());
                        etPersonDataSign.setText(value.getNote());
                        etPersonDataSign.setSelection(etPersonDataSign.getText().length());

                        tvPersonDataPhone.setText(value.getPhone());
                        iconPersonData.setController(StaticData.loadFrescoImg(Uri.parse(RetrofitHelper.ImgUrl + value.getImg()),
                                index, 120, 120, iconPersonData));

                        if (value.getSex() == 1) {
                            tvPersonDataSex.setText("男");
                        } else if (value.getSex() == 2) {
                            tvPersonDataSex.setText("女");
                        } else if (value.getSex() == 3) {
                            tvPersonDataSex.setText("未知");
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


    //选择照片弹出框
    public class PicWindow extends PopupWindow {
        private ImageView popbackimg;
        private Button cancelbtn;
        private ImageView cancelline;
        private ListView selectlist;
        private SelectAdapter adapter;

        public PicWindow(final Context mContext, View parent) {
            super(parent);
            View view = View.inflate(mContext, R.layout.select_pop_window, null);
            setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
            setFocusable(true);
            setOutsideTouchable(false);
            setContentView(view);
            showAtLocation(parent, Gravity.TOP, 0, 0);
            // showAsDropDown(parent);
            update();

            popbackimg = (ImageView) view.findViewById(R.id.popbackimg);
            cancelbtn = (Button) view.findViewById(R.id.cancelbtn);
            cancelline = (ImageView) view.findViewById(R.id.cancelline);
            selectlist = (ListView) view.findViewById(R.id.selectlist);
            StaticaAdaptive.adaptiveView(cancelline, 713, 0, index);
            StaticaAdaptive.adaptiveView(cancelbtn, 713, 120, index);
            StaticaAdaptive.adaptiveView(selectlist, 713, 0, index);
            popbackimg.setImageBitmap(StaticData.readBitMap(mContext, R.drawable.popback));

            adapter = new SelectAdapter(PersonDataActivity.this, picarr);
            selectlist.setAdapter(adapter);
            selectlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    if (i == 0) { //拍照
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        // 判断存储卡是否可以用，可用进行存储
                        if (StaticaAdaptive.hasSdcard()) {
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "IMGface")));
                        }
                        startActivityForResult(intent, GET_IMG_TAKE_PHOTO);
                        dismiss();

                    } else {

                        Intent intent_load = new Intent(Intent.ACTION_PICK, null);
                        intent_load.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent_load, GET_IMG_FROM_PIC);

                    }

                    dismiss();

                }
            });
            cancelbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }


    //选择性别弹出框
    public class SexWindow extends PopupWindow {
        private ImageView popbackimg;
        private Button cancelbtn;
        private ImageView cancelline;
        private ListView selectlist;
        private SelectAdapter adapter;

        public SexWindow(final Context mContext, View parent) {
            super(parent);
            View view = View.inflate(mContext, R.layout.select_pop_window, null);
            setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
            setFocusable(true);
            setOutsideTouchable(false);
            setContentView(view);
            showAtLocation(parent, Gravity.TOP, 0, 0);
            // showAsDropDown(parent);
            update();

            popbackimg = (ImageView) view.findViewById(R.id.popbackimg);
            cancelbtn = (Button) view.findViewById(R.id.cancelbtn);
            cancelline = (ImageView) view.findViewById(R.id.cancelline);
            selectlist = (ListView) view.findViewById(R.id.selectlist);
            StaticaAdaptive.adaptiveView(cancelline, 713, 0, index);
            StaticaAdaptive.adaptiveView(cancelbtn, 713, 120, index);
            StaticaAdaptive.adaptiveView(selectlist, 713, 0, index);
            popbackimg.setImageBitmap(StaticData.readBitMap(mContext, R.drawable.popback));

            adapter = new SelectAdapter(PersonDataActivity.this, sexarr);
            selectlist.setAdapter(adapter);
            selectlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    sex = i + 1;
                    tvPersonDataSex.setText(sexarr[i]);
                    dismiss();

                }
            });
            cancelbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }


}
