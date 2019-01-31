package com.app.entity;

import java.math.BigDecimal;

public class Find_item_dao {
    private Long id;
    private int type;
    private String city;
    private String title;
    private Long user_id;
    private BigDecimal money;
    private int star;
    private String defaultpath;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getDefaultpath() {
        return defaultpath;
    }

    public void setDefaultpath(String defaultpath) {
        this.defaultpath = defaultpath;
    }
}



















