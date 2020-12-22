package com.openclassrooms.savemytrip;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.openclassrooms.savemytrip.Models.User;
import com.openclassrooms.savemytrip.database.SaveMyTripDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ItemDaoTest {

    private SaveMyTripDatabase database;

    // Data set for test
    private static long USER_ID = 1;
    private static User USER_TEST = new User(USER_ID, "Jeremy", "google.com");



    @Rule
    public InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void initDb() {
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                SaveMyTripDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void closeDb() {
        database.close();
    }

    @Test
    public void insertAndGetUser() throws InterruptedException {
        database.userDao().createUser(USER_TEST);

        User user = LiveDataTestUtil.getValue(this.database.userDao().getUser(USER_ID));

        assertTrue(user.getUserName().equals(USER_TEST.getUserName()) && user.getId() == USER_TEST.getId());
    }

}
