package com.app.Util;


public class MyUrl {


    public static String getKefu() {
        return kefu;
    }

    public static String getUrl() {
        return url;
    }

    public static String add_end(String end){
        return url + end;
    }


    public static String getImage_path() {
        return image_path;
    }


    public static String getWsurl() {
        return wsurl;
    }
    public static String add_Wsurl(String end){
        return wsurl+end;
    }
    public static String add_Path(String end){
        return image_path+end;
    }

}
/*





    ====
     public static String getUrl() {
        return url;
    }

    public static String add_end(String end){
        return url + end;
    }

    private static final String url = "http://119.29.104.193:8080/Final/";

    public static String getImage_path() {
        return image_path;
    }

    private static final String wsurl = "ws://119.29.104.193:8080/Final/server";
    private static final String image_path = "http://119.29.104.193:8080/upload/";

    public static String getWsurl() {
        return wsurl;
    }
    public static String add_Wsurl(String end){
        return wsurl+end;
    }
    public static String add_Path(String end){
        return image_path+end;
    }
 */