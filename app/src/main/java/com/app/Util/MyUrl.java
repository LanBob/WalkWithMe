package com.app.Util;


public class MyUrl {

//    服务端

//    private static final String ip = "119.29.104.193";
//    private static final String url = "http://"+ip+":8080/walk/";
//    private static final String wsurl = "ws://"+ip+":8080/walk/server/";
//    private static final String image_path = "http://"+ip+":8080/upload/";
//    private static final String kefu = "13724158682";


//    本地Tomcat
    private static final String ip = "192.168.135.1";
    private static final String url = "http://" + ip + ":8080/";
    private static final String wsurl = "ws://" + ip + ":8080/server/";
    private static final String image_path = "http://" + ip + ":8080/upload/";
    private static final String kefu = "13724158682";

    public static String getKefu() {
        return kefu;
    }

    public static String getUrl() {
        return url;
    }

    public static String add_end(String end) {
        return url + end;
    }


    public static String getImage_path() {
        return image_path;
    }


    public static String getWsurl() {
        return wsurl;
    }

    public static String add_Wsurl(String end) {
        return wsurl + end;
    }

    public static String add_Path(String end) {
        return image_path + end;
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