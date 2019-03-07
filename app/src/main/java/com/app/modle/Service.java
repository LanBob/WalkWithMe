package com.app.modle;

import com.app.entity.CommentDao;
import com.app.entity.Find_item_dao;
import com.app.entity.HeadImage;
import com.app.entity.IsGoodMan;
import com.app.entity.Person_dao;
import com.app.entity.Person_setting;
import com.app.entity.Star_collection;
import com.app.entity.View_show_dao;


import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Service {

    /**
     * GET请求
     * 获取主页信息
     */
    @GET("app/person_setting")
    Observable<ResponseResult<Person_setting>> getPerson(@Query("id") Long id);

    @GET("get/view_show")
    Observable<ResponseResult<View_show_dao>> getViewShowDao(@Query("id") Long id);

    @GET("get/findItem")
    Observable<ResponseResult<List<Find_item_dao>>> getViewShowDaoByUserId(@Query("userId") String userId);

    @GET("get/search")
    Observable<ResponseResult<List<View_show_dao>>> searchByKeyWord(@Query("keyWord") String keyWord);


    @GET("get/findItem")
    Observable<ResponseResult<List<Find_item_dao>>> getFindItem(@Query("type") int type);

    @GET("app/follow_collection")
    Observable<ResponseResult<List<Person_dao>>> getFollow(@Query("userId") String userId, @Query("code") int code);

    @GET("app/follow_collection")
    Observable<ResponseResult<List<Find_item_dao>>> getStar(@Query("userId") String userId, @Query("code") int code);

    @GET("app/follow_collection")
    Observable<ResponseResult<List<Find_item_dao>>> getCollection(@Query("userId") String userId, @Query("code") int code);

    @GET("app/follow_collection")
    Observable<ResponseResult<Star_collection>> getStarCollection(@Query("view_show_id") String view_show_id, @Query("code") int code);


    @GET("get/who_star_collection")
    Observable<ResponseResult<Integer>> star_collection_follow(@Query("first") String first, @Query("second") String second, @Query("code") int code);


    @GET("get/messageCode")
    Observable<ResponseResult<String>> getMessageCode(@Query("phone") String userId);

    @GET("app/uploadHeadImage")
    Observable<ResponseResult<HeadImage>> getHeadImage(@Query("phone") String userId);

    @GET("app/comment")
    Observable<ResponseResult<List<CommentDao>>> getCommentByViewShowId(@Query("viewShowId") String viewShowId);

    @GET("app/isgoodman")
    Observable<ResponseResult<List<IsGoodMan>>> getIsGoodMan(@Query("code") String code);

    @GET("app/isgoodman")
    Observable<ResponseResult<IsGoodMan>> getScoreByUserId(@Query("code") String code, @Query("userId") String userId);


    /**
     * 通过json传输Person_setting 的内容
     *
     * @param json
     * @return
     */
    @FormUrlEncoded
    @POST("app/person_setting")
    Observable<ResponseResult<String>> setPersonSettiong(@Field("person_setting") String json);

    //    管理员模块
    @FormUrlEncoded
    @POST("app/manager")
    Observable<ResponseResult<String>> managerUploadScore(@Field("userId") String userId, @Field("score") int score);


    //    查询要进行评分的ViewShow
    @GET("app/up_view_show")
    Observable<ResponseResult<List<View_show_dao>>> managerNeededToScore(@Query("code") String code);

    //    消息中查询要进行评分的ViewShow
    @GET("app/interScore")
    Observable<ResponseResult<View_show_dao>> messageGetViewShowToBeScore(@Query("code") String code,@Query("userId")String userId);

    @FormUrlEncoded
    @POST("app/interScore")
    Observable<ResponseResult<String>>
        messageUpScore(@Field("code") String code,
                   @Field("viewShowId") String viewShowId,@Field("userId")String userId,@Field("score") int score);


    @GET("app/manager")
    Observable<ResponseResult<String>> managerUpScore(@Query("viewShowId") String viewShowId,@Query("score")int score);

    //    管理员模块

//    个人中心进行查询
    @GET("app/up_view_show")
    Observable<ResponseResult<List<View_show_dao>>> getViewShowByUserId(@Query("code") String code, @Query("userId") String userId);

    @FormUrlEncoded
    @POST("get/view_show")
    Observable<ResponseResult<String>> deleteViewShowById(@Field("code") String code,@Field("viewShowId")String viewShowId);



    /**
     * 通过 MultipartBody和@body作为参数来上传，仅仅是用来进行消息的缓存的。
     *
     * @param multipartBody MultipartBody包含多个Part
     * @return 状态信息
     */
    @FormUrlEncoded
    @POST("app/message")
    Observable<ResponseResult<String>> uploadFileList(@Body MultipartBody multipartBody);

    @Multipart
    @POST("app/up_view_show")
    Observable<ResponseResult<String>> uploadFindview(@Part("data") RequestBody description, @Part MultipartBody.Part... multipartBody);

    @Multipart
    @POST("app/isgoodman")
    Observable<ResponseResult<String>> isgoodman(@Part("data") RequestBody description, @Part MultipartBody.Part... multipartBody);

    @Multipart
    @POST("app/uploadHeadImage")
    Observable<ResponseResult<String>> uploadHeadImage(@Part("id") RequestBody description, @Part MultipartBody.Part... multipartBody);

//    /**
//     * 登录操作
//     * @param username
//     * @param password
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("login")
//    Observable<ResponseResult<String>> login_check(@Field("username") String username,@Field("password")String password);


    /**
     * 注册
     *
     * @param userJson
     * @return
     */
    @FormUrlEncoded
    @POST("login")
    Observable<ResponseResult<String>> login_check(@Field("data") String userJson, @Field("code") int code);




    /**
     * 点赞操作 insert:Parameters: who_star、view_show_id code = 1
     *
     * @param view_show_id
     * @param who_star
     * @param code         为0删除，为1添加
     * @return
     */
    @FormUrlEncoded
    @POST("app/follow_collection")
    Observable<ResponseResult<String>> add_Star(@Field("view_show_id") String view_show_id,
                                                @Field("who_star") String who_star, @Field("code") int code);


    /**
     * 收藏操作 insert :Parameters:view_show_id,who_cellection,code == 1
     *
     * @param view_show_id
     * @param code
     * @param code         为0删除，为1添加
     * @return
     */
    @FormUrlEncoded
    @POST("app/follow_collection")
    Observable<ResponseResult<String>> add_Collection(@Field("view_show_id") String view_show_id,
                                                      @Field("who_collection") String who_cellection, @Field("code") int code);

    /**
     * 关注操作 insert:parameters:follower，followed，code ==1
     *
     * @param follower
     * @param followed
     * @param code     为0删除，为1添加
     * @return
     */
    @FormUrlEncoded
    @POST("app/follow_collection")
    Observable<ResponseResult<String>> add_Follow(@Field("follower") String follower,
                                                  @Field("followed") String followed, @Field("code") int code);


    @FormUrlEncoded
    @POST("app/comment")
    Observable<ResponseResult<String>> comment(@Field("userId") String userId,
                                               @Field("viewShowId") String viewShowId, @Field("comment") String comment);

    @FormUrlEncoded
    @POST("app/feedBack")
    Observable<ResponseResult<String>> feedBack(@Field("userId") String userId,
                                                @Field("title") String title, @Field("content") String content);


}
