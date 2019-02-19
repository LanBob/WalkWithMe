package com.app.modle;


import com.app.Util.MyUrl;
import com.app.entity.Person_setting;
import com.google.gson.Gson;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpMethods {

    public static final String BASE_URL = MyUrl.getUrl();

    private static final int DEFAULT_TIMEOUT = 5000;
    private Retrofit retrofit;
    private Service service;

    //构造方法私有
    private HttpMethods() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        retrofit = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        service = retrofit.create(Service.class);
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final HttpMethods INSTANCE = new HttpMethods();
    }

    //获取单例
    public static HttpMethods getInstance() {
        return SingletonHolder.INSTANCE;
    }

    //对外的方法=====================================================================================================


    /**
     * 通过Id获取主页的信息
     * 只需要进行重写Observer方法即可
     *
     * @param observer
     */
    public void getPerson(Long id, Observer observer) {
        service.getPerson(id)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 通过id获取旅游信息页面
     * @param id
     * @param observer
     */
    public void getView_show_dao(Long id,Observer observer){
        service.getViewShowDao(id)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void searchByKeyWord(String keyWord,Observer observer){
        service.searchByKeyWord(keyWord)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * findViewShow，通过type
     * @param type
     * @param observer
     */
    public void getFind_item(int type,Observer observer){
        service.getFindItem(type)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getFollow(String userId,Observer observer){
        service.getFollow(userId,3)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getStar(String userId,Observer observer){
        service.getStar(userId,0)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    public void getCollection(String userId,Observer observer){
        service.getCollection(userId,1)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    public void getStarColllection(String view_show_id,Observer observer){
        service.getStarCollection(view_show_id,4)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    /**
     *
     * @param first view_show_id 或者 follower
     * @param second who_star or who_collection or followed
     * @param code
     */
    public void star_collection_follow(String first,String second,int code,Observer observer){
        service.star_collection_follow(first,second,code)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }




    /**
     * insert:Parameters: who_star、view_show_id code = 1
     */
    public void add_Star(String view_show_id ,String who_star,int code,Observer observer){
        service.add_Star(view_show_id,who_star,code)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     *
     * @param view_show_id
     * @param who_collection
     * @param code
     * @param observer
     */
    public void add_Collection(String view_show_id,String who_collection,int code,Observer observer){
        service.add_Collection(view_show_id,who_collection,code)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void add_Follow(String follower,String followed,int code,Observer observer){
        service.add_Follow(follower,followed,code)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }



    /**
     * 上传图片，参数为List集合和Observer对象
     * @param fileList
     * @param observer
     */
    public void uploadFileList(List<File> fileList, Observer observer){

        MultipartBody body = filesToMultipartBody(fileList);

        service.uploadFileList(body)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 请求文体，请求文件集合，observer
     * @param requestBody
     * @param fileList
     * @param observer
     */
    public void uploadEditText(RequestBody requestBody, List<File> fileList, Observer observer){

        MultipartBody.Part[] partList = filesMultipartBody(fileList);

        service.uploadFindview(requestBody,partList)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    /**
     * 注册操作
     * @param map
     * @param observer
     */
    public void Login_check(Map map,Observer observer){
        //注册，直接传一个map进来
        String json  = new Gson().toJson(map);
        service.login_check(json,0)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 登录
     * @param map
     * @param observer
     */
    public void login_check(Map map, Observer observer){
        String json  = new Gson().toJson(map);
        service.login_check(json,1)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 登录
     * @param map
     * @param observer
     */
    public void loginByMessageCode(Map map, Observer observer){
        String json  = new Gson().toJson(map);
        service.login_check(json,2)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }



    /**
     * 上传setting
     * @param setting
     * @param observer
     */
    public void Person_settting(Person_setting setting, Observer observer){
        String json = new Gson().toJson(setting);
         service.setPersonSettiong(json)
                 .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    //对外的方法end=====================================================================================================



    public static MultipartBody.Part[] filesMultipartBody(List<File> fileList) {
        MultipartBody.Part[] partArray = new MultipartBody.Part[fileList.size()];
        for(int i=0;i<fileList.size();++i){
            File file = fileList.get(i);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file",
                    file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
            partArray[i] = filePart;
        }
        return partArray;
    }



    /**
     * 图片，构造MultipartBody
     * @param fileList
     * @return
     */
    public static MultipartBody filesToMultipartBody(List<File> fileList) {

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        for (File file : fileList) {
            //这里自动设置了图片的名称
            String fileName = file.getName();
            // TODO: 16-4-2  这里为了简单起见，没有判断file的类型
            RequestBody requestBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
            //TODO 根据文件名设置contentType
            builder.addPart(Headers.of("Content-Disposition",
                    "form-data; name=\"image\"; filename=\"" + fileName + "\""),
                    requestBody);
        }
        MultipartBody multipartBody = builder.build();
        return multipartBody;
    }

    //判断文件类型
    private static String guessMimeType(String path)
    {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null)
        {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }


}
