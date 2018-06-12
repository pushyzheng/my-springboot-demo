package com.pushy.mongodbdemo.pojo;

import java.util.Date;

public class Comment {

    private String id;

    private Date registrationTime;

    private String usreId;

    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(Date registrationTime) {
        this.registrationTime = registrationTime;
    }

    public String getUsreId() {
        return usreId;
    }

    public void setUsreId(String usreId) {
        this.usreId = usreId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
