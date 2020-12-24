package com.openclassrooms.savemytrip.todolist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.openclassrooms.savemytrip.models.Item;
import com.openclassrooms.savemytrip.models.User;
import com.openclassrooms.savemytrip.repoositoies.ItemDataRepository;
import com.openclassrooms.savemytrip.repoositoies.UserDataRepository;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;

import static com.openclassrooms.savemytrip.utils.StorageUtils.getFileFromStorage;
import static com.openclassrooms.savemytrip.utils.StorageUtils.saveFile;

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
    public void deleteItem(Context context, Item item){

        Uri uri = Uri.parse(item.getPictureUri());
        String fileName = new File(uri.toString()).getName();
        File dest = context.getFilesDir();

        File image = getFileFromStorage(dest,context, fileName, FOLDER);
        image.delete();

        executor.execute(() -> itemDataSource.deleteItem(item.getId()));
    }

    public void insertItem(Context context, String text, int categoryId, int userId, String pictureUri) {
        Item item = new Item(text, categoryId, userId);

        if(pictureUri!=null) {
            Log.d("Debug", "pictureUri!=null");

            Uri uri = Uri.parse(pictureUri);
            String fileName = new File(uri.toString()).getName()+".jpg";
            File dest = context.getFilesDir();
            String absolutePath = dest.toString()+File.separator+FOLDER;

            saveFile(context, fileName, uri, absolutePath, fileName);
            File image = getFileFromStorage(dest, context, fileName, FOLDER);

            item.setPictureUri(image.getAbsolutePath());
        }
        insertItem(item);
    }
}
