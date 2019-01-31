package com.app.Util;

import android.content.Context;
import android.util.Log;

import com.app.entity.Province;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CityUtil {
    public static List<Province> provinceList = null;
    private static String json = null;

    public static ArrayList<String> getProvinceBeanList() {
        return provinceBeanList;
    }

    public static ArrayList<List<String>> getCityList() {
        return cityList;
    }

    public static ArrayList<List<List<String>>> getDistrictList() {
        return districtList;
    }

    public static ArrayList<String> provinceBeanList = null;
    public static ArrayList<String> cities;
    public static ArrayList<List<String>> cityList= null;
    public static ArrayList<String> district;
    public static ArrayList<List<String>> districts;
    public static ArrayList<List<List<String>>> districtList= null;

    ///====================省数据
    private static void getCityin(Context context) {
        if (provinceList == null) {
            String s = "null";
            InputStream is = null;

            try {
                is = context.getAssets().open("city.txt");

                int lenght = is.available();

                byte[] buffer = new byte[lenght];

                is.read(buffer);

                s = new String(buffer, "utf8");
                json = s;

            } catch (
                    IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Type listType = new TypeToken<List<Province>>() {
            }.getType();
            //得到这个对象，每个省和对应的ity
            provinceList = new Gson().fromJson(s, listType);

        } else {
            Log.e("c", "重复了，不执行读取json");
        }

    }

    /**
     *设置三级联动效果
     *pvOptions.setPicker(provinceBeanList,cityList,districtList,true);
     */

    //  省份    ArrayList<ProvinceBean> provinceBeanList = new ArrayList<>();
    //  城市    ArrayList<String> cities;
    //                      ArrayList<List<String>> cityList = new ArrayList<>();
    //  区/县   ArrayList<String> district;
    //                      ArrayList<List<String>> districts;
    //                      ArrayList<List<List<String>>> districtList = new ArrayList<>();


   public static void init(Context context){
       if(provinceBeanList == null||cityList == null||districtList ==null){
           provinceBeanList = null;
           cityList = null;
           districtList = null;
           provinceBeanList = new ArrayList<>();
           cityList = new ArrayList<>();
           districtList = new ArrayList<>();

           getCityin(context);
           try {
               //  获取json中的数组
               JSONArray jsonArray = new JSONArray(json);
               //  遍历数据组
               for (int i = 0; i < jsonArray.length(); i++) {
                   //  获取省份的对象
                   JSONObject provinceObject = jsonArray.optJSONObject(i);
                   //  获取省份名称放入集合
                   String provinceName = provinceObject.getString("name");
                   provinceBeanList.add(provinceName);
                   //  获取城市数组
                   JSONArray cityArray = provinceObject.optJSONArray("city");
                   cities = new ArrayList<>();//   声明存放城市的集合
                   districts = new ArrayList<>();//声明存放区县集合的集合
                   //  遍历城市数组
                   for (int j = 0; j < cityArray.length(); j++) {
                       //  获取城市对象
                       JSONObject cityObject = cityArray.optJSONObject(j);
                       //  将城市放入集合
                       String cityName = cityObject.optString("name");
                       cities.add(cityName);
                       district = new ArrayList<>();// 声明存放区县的集合
                       //  获取区县的数组
                       JSONArray areaArray = cityObject.optJSONArray("area");
                       //  遍历区县数组，获取到区县名称并放入集合
                       for (int k = 0; k < areaArray.length(); k++) {
                           String areaName = areaArray.getString(k);
                           district.add(areaName);
                       }
                       //  将区县的集合放入集合
                       districts.add(district);
                   }
                   //  将存放区县集合的集合放入集合
                   districtList.add(districts);
                   //  将存放城市的集合放入集合
                   cityList.add(cities);
               }
               Log.e("provinceBeanList",provinceBeanList.size() + "null");
               Log.e("cityList",cityList.size() + "null");
               Log.e("districts",districtList.size() + "null");
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }

    }




}

