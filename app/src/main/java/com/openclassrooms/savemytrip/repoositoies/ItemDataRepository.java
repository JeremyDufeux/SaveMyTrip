package com.openclassrooms.savemytrip.repoositoies;

import android.arch.lifecycle.LiveData;

import com.openclassrooms.savemytrip.database.dao.ItemDao;
import com.openclassrooms.savemytrip.models.Item;

import java.util.List;

public class ItemDataRepository {
    private final ItemDao itemDao;

    public ItemDataRepository(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    public LiveData<List<Item>> getItems(long userId) {
        return this.itemDao.getItems(userId);
    }

    public void createItem(Item item){
        this.itemDao.insertItem(item);
    }

    public void updateItem(Item item){
        this.itemDao.updateItem(item);
    }

    public void deleteItem(long itemId){
        this.itemDao.deleteItem(itemId);
    }
}
