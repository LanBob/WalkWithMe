package com.app.entity;

public class Home_everyDao {
    private String time;
    private Integer image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    @Override
    public String toString() {
        return "Home_everyDao{" +
                "time='" + time + '\'' +
                ", image=" + image +
                ", introduce='" + introduce + '\'' +
                '}';
    }

    private String introduce;

}