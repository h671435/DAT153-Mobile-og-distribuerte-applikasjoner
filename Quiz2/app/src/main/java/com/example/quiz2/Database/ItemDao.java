package com.example.quiz2.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quiz2.Entity.Item;

import java.util.List;

@Dao
public interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItem(Item item);

    @Query("SELECT * FROM items WHERE id = :name")
    List<Item> getItemByName(String name);

    @Query("DELETE FROM items WHERE id = :name")
    void deleteItemByName(String name);

    @Query("SELECT * FROM items")
    LiveData<List<Item>> getAllItems();

    @Update
    void update(Item item);
}
