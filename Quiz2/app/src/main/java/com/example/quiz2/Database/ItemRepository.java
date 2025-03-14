package com.example.quiz2.Database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.quiz2.Entity.Item;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ItemRepository {
    private final ItemDao itemDao;
    private final LiveData<List<Item>> allItems;
    private ExecutorService executorService;


    public ItemRepository(Application application) {
        ItemRoomDatabase db = ItemRoomDatabase.getDatabase(application);
        itemDao = db.itemDao();
        allItems = itemDao.getAllItems();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Item>> getAllItems() {
        return allItems;
    }

    public void deleteAllItems() {
        executorService.execute(itemDao::deleteAllItems);
    }

    public void insert(Item item) {
        executorService.execute(() -> itemDao.insertItem(item));
    }

    public void update(Item item) {
        // Implementation here
        executorService.execute(() -> itemDao.update(item));
    }

    public void delete(Item item) {
        // Implementation here
        executorService.execute(() -> itemDao.deleteItemByName(item.getName()));
    }
}