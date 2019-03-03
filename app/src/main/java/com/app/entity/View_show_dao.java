package com.app.entity;

import java.math.BigDecimal;

public class View_show_dao {
    private String title;
    private String introduce;
    private String city;
    private Long user_id;
    private int type;
    private BigDecimal money;
    private String defaultpath;
    private Long id;
    private String route;
    private String friendlyToEat;
    private String firendlyToLive;
    private String detailAddress;
    private int score;
    private String myTime;

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getFriendlyToEat() {
        return friendlyToEat;
    }

    public void setFriendlyToEat(String friendlyToEat) {
        this.friendlyToEat = friendlyToEat;
    }

    public String getFirendlyToLive() {
        return firendlyToLive;
    }

    public void setFirendlyToLive(String firendlyToLive) {
        this.firendlyToLive = firendlyToLive;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getMyTime() {
        return myTime;
    }

    public void setMyTime(String myTime) {
        this.myTime = myTime;
    }



//    private int star;
//    private int collection;
//    private int interest;

    public String getDefaultpath() {
        return defaultpath;
    }

    public void setDefaultpath(String defaultpath) {
        this.defaultpath = defaultpath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public int getStar() {
//        return star;
//    }
//
//    public void setStar(int star) {
//        this.star = star;
//    }
//
//    public int getCollection() {
//        return collection;
//    }
//
//    public void setCollection(int collection) {
//        this.collection = collection;
//    }
//
//    public int getInterest() {
//        return interest;
//    }
//
//    public void setInterest(int interest) {
//        this.interest = interest;
//    }





    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}
