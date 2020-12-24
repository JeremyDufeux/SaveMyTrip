package com.openclassrooms.savemytrip.todolist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.openclassrooms.savemytrip.models.Item;
import com.openclassrooms.savemytrip.models.User;
import com.openclassrooms.savemytrip.repoositoies.ItemDataRepository;
import com.openclassrooms.savemytrip.repoositoies.UserDataRepository;

import java.util.List;
import java.util.concurrent.Executor;

public class ItemViewModel extends ViewModel {
    private static final String FOLDER = "item_pictures";

    // Repositories
    private final ItemDataRepository itemDataSource;
    private final UserDataRepository userDataSource;
    private final Executor executor;

    // Data
    @Nullable
    private LiveData<User> currentUser;

    public ItemViewModel(ItemDataRepository itemDataSource, UserDataRepository userDataSource, Executor executor) {
        this.itemDataSource = itemDataSource;
        this.userDataSource = userDataSource;
        this.executor = executor;
    }

    public void init(long userId){
        if(this.currentUser != null){
            return;
        }
        currentUser = userDataSource.getUser(userId);
    }

    // ----------
    // For user
    // ----------

    public LiveData<User> getUser(long userId){
        return this.currentUser;
    }

    // ---------
    // For item
    // ---------

    public LiveData<List<Item>> getItems(long userId){
        return itemDataSource.getItems(userId);
    }

    public void insertItem(Item item){
        executor.execute(() -> itemDataSource.createItem(item));
    }
    public void updateItemSelection(long itemId, boolean selection){
        executor.execute(() -> itemDataSource.updateItemSelection(itemId, selection));
    }
    public void deleteItem(long userId){
        executor.execute(() -> itemDataSource.deleteItem(userId));
    }

    public void insertItem(String text, int categoryId, int userId, String pictureUri) {
        Item item = new Item(text, categoryId, userId);

        if(pictureUri!=null) {
            item.setPictureUri(pictureUri);
        }
        insertItem(item);
    }
}
