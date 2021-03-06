package com.openclassrooms.savemytrip.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.openclassrooms.savemytrip.models.Item;

import java.util.List;

@Dao
public interface ItemDao {

    @Query("SELECT * FROM Item WHERE userId = :userId")
    LiveData<List<Item>> getItems(long userId);

    @Insert
    long insertItem(Item item);

    @Query("UPDATE Item SET isSelected = :selection WHERE id = :itemId")
    int updateItemSelection(long itemId, boolean selection);

    @Query("DELETE FROM Item WHERE id = :itemId")
    int deleteItem(long itemId);
}
