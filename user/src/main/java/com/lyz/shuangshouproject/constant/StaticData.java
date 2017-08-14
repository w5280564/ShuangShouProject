package com.lyz.shuangshouproject.constant;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Admin on 2016/7/27.
 */
public class StaticData {

    /**
     * state（0：待接单，1：已接单，2：已回收，3：已完成，4：已撤回）
     */
    public static final String ORDER_STATE_ORDERS = "0";
    public static final String ORDER_STATE_ORDERED = "1";
    public static final String ORDER_STATE_RECYCLED = "2";
    public static final String ORDER_STATE_FINISHED = "3";

    public static final boolean FLAG_USER = true; //用户类型 true 普通用户，false 快递员

    public static final String PACKAGE_NAME = "com.lyz.shuangshouproject";
    public static final String PATH_CACHE = "cache";


    // 使用系统当前日期加以调整作为照片的名称
    public static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("'PNG'_yyyyMMdd_HHmmss");
        return sdf.format(date) + ".png";
    }

    // 使用系统当前日期加以调整作为照片的名称
    public static String getVideoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("'VIDEO'_yyyyMMdd_HHmmss");
        return sdf.format(date) + ".mp4";
    }

    // 使用系统当前日期加以调整作为照片的名称
    public static String getCurrentTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("'VIDEO'_yyyyMMdd_HHmmss");
        return sdf.format(date) + ".mp4";
    }

    /**
     * 判断是否有网true有网
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    //设置沉浸式状态栏
    public static void setTranslucentStatus(Activity activity) {

        activity.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = activity.getWindow();

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS

                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//隐藏状态栏或者导航栏

            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);//设置可见的UI，DecorView控制了最顶层的View

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(Color.parseColor("#6c6c6c"));

            window.setNavigationBarColor(Color.TRANSPARENT);//设置了导航栏的透明效果

        }

    }


    public static PipelineDraweeController loadFrescoImg(Uri imguri, float index, int wight, int height, SimpleDraweeView img) {

        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(imguri).
                setResizeOptions(new ResizeOptions(StaticaAdaptive.translate(wight, index), StaticaAdaptive.translate(height, index))).build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder().
                setOldController(img.getController()).setImageRequest(imageRequest).build();

        return controller;
    }

    public static PipelineDraweeController loadFrescoImgRes(int resId, float index, int wight, int height, SimpleDraweeView img) {

        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(resId).
                setResizeOptions(new ResizeOptions(StaticaAdaptive.translate(wight, index), StaticaAdaptive.translate(height, index))).build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder().
                setOldController(img.getController()).setImageRequest(imageRequest).build();

        return controller;
    }

    /**
     * 分割时间字符串
     *
     * @param GTMDate
     * @return
     */
    public static String GTMToLocal(String GTMDate) {

        try {
            if (null == GTMDate) {
                return GTMDate;
            }

            if (GTMDate.contains("T")) {
                int tIndex = GTMDate.indexOf("T");
                String dateTemp = GTMDate.substring(0, tIndex);
                String timeTemp = GTMDate.substring(tIndex + 1, GTMDate.length());
                if (timeTemp.contains(".")) {
                    int tIndexTime = timeTemp.indexOf(".");
                    timeTemp = timeTemp.substring(0, tIndexTime);
                }
                return dateTemp+" "+timeTemp;
            }

//            int tIndex = GTMDate.indexOf("T");
//            String dateTemp = GTMDate.substring(0, tIndex);
//            String timeTemp = GTMDate.substring(tIndex + 1, GTMDate.length() - 6);
//            String convertString = dateTemp + " " + timeTemp;
//
//
//            SimpleDateFormat format;
//            format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
//            Date result_date;
//            long result_time = 0;
//            //GMT00:00  将0时区的时间转换为中国8时区的时间
//            //GMT+:08:00 获取中国时区的时间
//            format.setTimeZone(TimeZone.getTimeZone("GMT00:00"));
//            result_date = format.parse(convertString);
//            result_time = result_date.getTime();
//            format.setTimeZone(TimeZone.getDefault());
//
//            return format.format(result_time);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return GTMDate;

    }
//2017-07-24T16:22:59.473657

    /**
     * 解决三星手机调用相册加载图片 角度变换的问题
     *
     * @param context
     * @param imagePath
     * @param width
     * @return
     */
    public static Bitmap loadingImageBitmap(Context context, String imagePath, int width) {

        /**
         * 通过设置optios来只加载大图的尺寸
         */
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = null;
        int degree = 0;

        try {
            bitmap = BitmapFactory.decodeFile(imagePath, options);

            ExifInterface exifInterface = new ExifInterface(imagePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
            /**
             * 计算手机宽高与显示大图的宽高，然后确定缩放有比例
             */
            int widthRaio = (int) Math.ceil(options.outWidth / (float) width);
            if (widthRaio > 1 && widthRaio > 1) {
                options.inSampleSize = widthRaio;
            }
            /**
             * 设置加载缩放后的图片
             */
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(imagePath, options);
            Matrix matrix = new Matrix();
            matrix.postRotate(degree); /*翻转90度*/
            width = bitmap.getWidth();
            int height = bitmap.getHeight();
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param context
     * @param imageUri
     */

    @TargetApi(19)
    public static String getImageAbsolutePath(Activity context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());

    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     * 绝对路径转Uri的那个方法 目前是图片文件的转换 转其他文件 只要把content后面的目录换成对应文件的归属目录就行了。
     *
     * @param context
     * @return content Uri
     */
    public static Uri getImageContentUri(Context context, String filePath) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, filePath);
            return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
    }

    public static Bitmap getimage(String srcPath, Bitmap bitmap) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 比较节约内存，加载本地大图时。
     * <p>
     * Android中图片有四种属性，分别是：
     * ALPHA_8：每个像素占用1byte内存
     * ARGB_4444：每个像素占用2byte内存
     * ARGB_8888：每个像素占用4byte内存 （默认）
     * RGB_565：每个像素占用2byte内存
     */
    public static Bitmap readBitMap(Context context, int resId) {

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static boolean getPhotoPermission(Context context) {
        PackageManager pm = context.getPackageManager();
        //三星手机需要判断权限，让用户手动打开权限
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.CAMERA", "com.lyz.anxuquestionnaire"));
        return permission;

    }

    public static boolean getFileReadPermission(Context context) {
        PackageManager pm = context.getPackageManager();
        //三星手机需要判断权限，让用户手动打开权限
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.READ_EXTERNAL_STORAGE", "com.lyz.anxuquestionnaire"));
        return permission;

    }

    public static boolean getFileWritePermission(Context context) {
        PackageManager pm = context.getPackageManager();
        //三星手机需要判断权限，让用户手动打开权限
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", "com.lyz.anxuquestionnaire"));
        return permission;

    }


    public static boolean getRecordPermission(Context context) {
        PackageManager pm = context.getPackageManager();
        //三星手机需要判断权限，让用户手动打开权限
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.RECORD_AUDIO", "com.lyz.anxuquestionnaire"));
        return permission;

    }


    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha 屏幕透明度0.0-1.0 1表示完全不透明
     */
    public static void setBackgroundAlpha(float bgAlpha, Context mContext) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) mContext).getWindow().setAttributes(lp);
    }
}



