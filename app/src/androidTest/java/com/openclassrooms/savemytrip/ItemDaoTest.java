package com.openclassrooms.savemytrip;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.openclassrooms.savemytrip.Models.Item;
import com.openclassrooms.savemytrip.Models.User;
import com.openclassrooms.savemytrip.database.SaveMyTripDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ItemDaoTest {

    private SaveMyTripDatabase database;

    // Data set for test
    private static long USER_ID = 1;
    private static User USER_TEST = new User(USER_ID, "Jeremy", "google.com");
    private static Item ITEM_1 = new Item("test 1", 0, USER_ID);
    private static Item ITEM_2 = new Item("test 2", 1, USER_ID);
    private static Item ITEM_3 = new Item("test 3", 2, USER_ID);


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

        User user = LiveDataTestUtil.getValue(database.userDao().getUser(USER_ID));

        assertTrue(user.getUserName().equals(USER_TEST.getUserName()) && user.getId() == USER_TEST.getId());
    }

    @Test
    public void insertAndGetItem() throws InterruptedException {
        database.userDao().createUser(USER_TEST);
        database.itemDao().insertItem(ITEM_1);
        database.itemDao().insertItem(ITEM_2);
        database.itemDao().insertItem(ITEM_3);

        List<Item> items = LiveDataTestUtil.getValue(database.itemDao().getItems(USER_ID));

        assertEquals(3, items.size());
        assertEquals(ITEM_1.getText(), items.get(0).getText());
    }

    @Test
    public void insertAndUpdateItem() throws InterruptedException {
        database.userDao().createUser(USER_TEST);
        database.itemDao().insertItem(ITEM_1);

        Item item = LiveDataTestUtil.getValue(database.itemDao().getItems(USER_ID)).get(0);
        assertFalse(item.isSelected());
        item.setSelected(true);

        database.itemDao().updateItem(item);

        List<Item> items = LiveDataTestUtil.getValue(database.itemDao().getItems(USER_ID));

        assertTrue(items.get(0).isSelected());
    }

    @Test
    public void insertAndDeleteItem() throws InterruptedException {
        database.userDao().createUser(USER_TEST);
        database.itemDao().insertItem(ITEM_1);

        Item item = LiveDataTestUtil.getValue(database.itemDao().getItems(USER_ID)).get(0);
        database.itemDao().deleteItem(item.getId());

        List<Item> items = LiveDataTestUtil.getValue(database.itemDao().getItems(USER_ID));

        assertTrue(items.isEmpty());
    }


}
