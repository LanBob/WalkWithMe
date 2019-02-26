package com.app.entity;


/**
 * Created by ownlove on 2019/2/25.
 */
public class CommentDao {
    private String userId;
    private String comment;
    private String viewShowId;
    private String defaultImage;
    private String userName;
    private String mytime;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getViewShowId() {
        return viewShowId;
    }

    public void setViewShowId(String viewShowId) {
        this.viewShowId = viewShowId;
    }

    public String getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(String defaultImage) {
        this.defaultImage = defaultImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMytime() {
        return mytime;
    }

    public void setMytime(String mytime) {
        this.mytime = mytime;
    }
}
