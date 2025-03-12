package com.example.quiz2.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.quiz2.Database.ItemRepository;
import com.example.quiz2.Entity.Item;

import java.util.List;

public class GalleryViewModel extends AndroidViewModel {
    final private ItemRepository repository;
    final private LiveData<List<Item>> allItems;

    public GalleryViewModel(Application application) {
        super(application);
        repository = new ItemRepository(application);
        allItems = repository.getAllItems();
    }

    public LiveData<List<Item>> getAllItems() {
        return allItems;
    }

    public void add(Item item) {
        repository.insert(item);
    }

    public void delete(Item item) {
        repository.delete(item);
    }

    public void update(Item item) {
        repository.update(item);
    }
}
