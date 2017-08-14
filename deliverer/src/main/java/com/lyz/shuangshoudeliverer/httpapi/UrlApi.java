package com.lyz.shuangshoudeliverer.httpapi;


import com.google.gson.JsonObject;
import com.lyz.shuangshoudeliverer.ui.bean.BoxDetailBean;
import com.lyz.shuangshoudeliverer.ui.bean.InforBean;
import com.lyz.shuangshoudeliverer.ui.bean.LocationBean;
import com.lyz.shuangshoudeliverer.ui.bean.OrderDetailBean;
import com.lyz.shuangshoudeliverer.ui.bean.OrderListBean;
import com.lyz.shuangshoudeliverer.ui.bean.PointBean;
import com.lyz.shuangshoudeliverer.ui.bean.RecycleAddressBean;
import com.lyz.shuangshoudeliverer.ui.bean.UserAccountBean;
import com.lyz.shuangshoudeliverer.ui.bean.UserDataBean;

import java.util.ArrayList;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;


/**
 * Retrofit接口类
 */

public interface UrlApi {
    /**
     * 这里需要稍作说明，@GET注解就表示get请求，
     *
     * @return
     * @Query表示请求参数，将会以key=value的方式拼接在url后面 @QueryMap 传map 把key value丢进去
     * @POST注解就表示post请求，@Field表示请求参数，将会以表单的方式提交 @FieldMap 意思一样
     * 除此之外还有 @Body @Path 添加header等方法
     */

    //登录
    //结合Rajava使用 增加返回值为Oservable<T>的支持,Rersults为实体类

    /**
     * 用户名：username
     * 密码：pwd
     * 人员类型：state（0：普通用户，1：快递员，2：管理员，3：商家）
     * 备注   state不可上传2  不能这样建立管理员
     */
    @FormUrlEncoded //@FormUrlEncoded注明是表单提交 @Field注明表单中的键,方法的参数就是值了噻.
    @POST("user/reg")
    Observable<UserAccountBean> register(@Field("username") String username, @Field("pwd") String pwd,
                                         @Field("state") String state, @Field("idcard") String idcard,
                                         @Field("company_name") String company_name);

    //上传图片
    @Multipart
    @POST("UpdateUserData/")
    Observable<JsonObject> updateUserImg(@Part("img") MultipartBody.Part file, @PartMap() Map<String, RequestBody> partMap);


    //完善个人信息
    @Multipart
    @POST("user/UpdateUserData")
    Observable<UserAccountBean> perfectUserData(@Part("realname") RequestBody realname, @Part("phone") RequestBody phone,
                                                @Part("sex") RequestBody sex, @Part("note") RequestBody note, @Part MultipartBody.Part file);

    //登录
    @FormUrlEncoded
    @POST("user/login")
    Observable<UserAccountBean> login(@Field("username") String username, @Field("pwd") String pwd);

    //获取用户个人信息
    @GET("user/GetUserData")
    Observable<UserDataBean> getUserData();

    //获取个人消息
    @GET("order/GetMessageList")
    Observable<ArrayList<InforBean>> getMessageList(@Query("page") int page);


    //退出登录
    @GET("user/logout")
    Observable<UserAccountBean> logout();

    /**
     * 订单状态：state（0：待接单，1：已接单，2：已回收，3：已完成，4：已撤回）可以填写多个用逗号链接（如：0,1,2,3,4）
     * 页数：page
     */
    //获取订单列表
    @GET("order/GetOrderList")
    Observable<ArrayList<OrderListBean>> getOrderList(@Query("state") String state, @Query("page") int page);


    //获取后台管理所有的维修工坐标
    @GET("user/GetAllDesPlace")
    Observable<ArrayList<LocationBean>> getAllDesPlace();


    //获取所有待接单和已接单的工单的坐标
    @GET("order/GetOrderPlace")
    Observable<ArrayList<LocationBean>> getOrderPlace();


    /**
     * 通过纸箱ID扫描获取纸箱信息
     */
    @GET("order/GetBoxData")
    Observable<BoxDetailBean> getCartonDetailFormId(@Query("id") String id);


    /**
     * 获取用户的回收地址列表
     */
    @GET("order/GetAllAddress")
    Observable<ArrayList<RecycleAddressBean>> getAllAddress();

    /**
     * 获取积分列表
     */
    @GET("order/GetPointList")
    Observable<ArrayList<PointBean>> getPointList(@Query("page") int page);

    /**
     * 删除回收地址
     */
    @FormUrlEncoded
    @POST("order/DeleteAddress")
    Observable<UserAccountBean> deleteAddress(@Field("id") int id);

    /**
     * 创建订单
     */
    @FormUrlEncoded
    @POST("order/MakeOrder")
    Observable<UserAccountBean> makeOrder(@Field("boxeslist") String boxeslist, @Field("contact") String contact,
                                          @Field("phone") String phone, @Field("address") String address,
                                          @Field("lng") String lng, @Field("lat") String lat);

    /**
     * 撤回订单
     * 用户发布订单后，撤回订单，将订单状态修改为5
     */
    @FormUrlEncoded
    @POST("order/DeleteOrder")
    Observable<UserAccountBean> deleteOrder(@Field("id") String id);

    /**
     * 快递员订单
     */
    @FormUrlEncoded
    @POST("order/TakeOrder")
    Observable<UserAccountBean> takeOrder(@Field("id") String id);

    /**
     * 快递员回收订单
     */
    @FormUrlEncoded
    @POST("order/TakeBackOrder")
    Observable<UserAccountBean> takeBackOrder(@Field("id") String id);

    /**
     * 评价订单
     */
    @FormUrlEncoded
    @POST("order/CommentOrder")
    Observable<UserAccountBean> commentOrder(@Field("id") int id, @Field("content") String content);

    /**
     * 获取订单详情
     */
    @GET("order/GetOrderDetail")
    Observable<OrderDetailBean> getOrderDetail(@Query("id") String id);


    /**
     * 新建回收地址
     * 联系人：contact
     * 联系电话：phone
     * 回收地区：area
     * 回收地址：address
     * 是否为默认：isdefault   0 否 1 是
     * 经度：lng
     * 纬度：lat
     */
    @FormUrlEncoded
    @POST("order/MakeAddress")
    Observable<UserAccountBean> makeAddress(@Field("contact") String contact, @Field("phone") String phone,
                                            @Field("area") String area, @Field("address") String address,
                                            @Field("isdefault") int isdefault,
                                            @Field("lng") String lng, @Field("lat") String lat);

    /**
     * 修改回收地址
     * 回收地址ID：id
     * 联系人：contact
     * 联系电话：phone
     * 回收地区：area
     * 回收地址：address
     * 是否为默认：isdefault   0 否 1 是
     * 经度：lng
     * 纬度：lat
     */
    @FormUrlEncoded
    @POST("order/EditAddress")
    Observable<UserAccountBean> editAddress(@Field("id") int id, @Field("contact") String contact,
                                            @Field("phone") String phone,
                                            @Field("area") String area, @Field("address") String address,
                                            @Field("isdefault") int isdefault,
                                            @Field("lng") String lng, @Field("lat") String lat);


    /**
     * 向服务器发送位置
     */
    @FormUrlEncoded
    @POST("user/UpLoadUserPlace")
    Observable<UserAccountBean> upLoadUserPlace(@Field("lng") double lng, @Field("lat") double lat);

}
