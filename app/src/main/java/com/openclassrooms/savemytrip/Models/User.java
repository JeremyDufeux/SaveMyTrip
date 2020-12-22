package com.openclassrooms.savemytrip.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey
    private long id;
    private String userName;
    private String urlPicture;

    public User(long id, String userName, String urlPicture) {
        this.id = id;
        this.userName = userName;
        this.urlPicture = urlPicture;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }
}
