package com.openclassrooms.savemytrip.repoositoies;

import android.arch.lifecycle.LiveData;

import com.openclassrooms.savemytrip.database.dao.UserDao;
import com.openclassrooms.savemytrip.models.User;

public class UserDataRepository {
    private final UserDao userDao;

    public UserDataRepository(UserDao userDao){
        this.userDao = userDao;
    }

    public LiveData<User> getUser(long userId){
        return this.userDao.getUser(userId);
    }
}
