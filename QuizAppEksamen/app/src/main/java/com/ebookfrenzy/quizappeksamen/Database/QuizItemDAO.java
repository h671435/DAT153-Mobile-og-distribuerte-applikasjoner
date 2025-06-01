package com.ebookfrenzy.quizappeksamen.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface QuizItemDAO {
    @Query("SELECT * FROM quizitems")
    LiveData<List<QuizItem>> getAll();

    @Query("SELECT * FROM quizitems WHERE id = :id")
    LiveData<QuizItem> getQuizItem(int id);

    @Insert
    void insert(QuizItem item);

    @Insert
    void insertAll(QuizItem... items);

    @Update
    void update(QuizItem item);

    @Delete
    void delete(QuizItem item);

    @Query("DELETE FROM quizitems")
    void deleteAll();
}
