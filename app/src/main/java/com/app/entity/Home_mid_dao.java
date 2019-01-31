package com.app.entity;

public class Home_mid_dao {
    /**
     * API 提示：发现精选
     * 名称   属性
     * name :String     精选标题名字
     * iamge: Integer   精选对应图片
     * @return
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    private String name;
    private Integer image;

}