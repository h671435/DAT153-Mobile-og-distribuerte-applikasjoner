package com.ebookfrenzy.quizappeksamen.Database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizRepo {
    private QuizItemDAO dao;
    private LiveData<List<QuizItem>> allQuizItems;
    private ExecutorService executorService;

    public QuizRepo(Application application) {
        QuizItemDatabase database = QuizItemDatabase.getDatabase(application);
        dao = database.quizItemDAO();
        allQuizItems = dao.getAll();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Get all quiz items
    public LiveData<List<QuizItem>> getAllQuizItems() {
        return allQuizItems;
    }

    // Insert a quiz item
    public void insert(QuizItem quizItem) {
        executorService.execute(() -> dao.insert(quizItem));
    }

    // Update a quiz item
    public void update(QuizItem quizItem) {
        executorService.execute(() -> dao.update(quizItem));
    }

    // Delete a quiz item
    public void delete(QuizItem quizItem) {
        executorService.execute(() -> dao.delete(quizItem));
    }

    // Delete all quiz items
    public void deleteAll() {
        executorService.execute(dao::deleteAll);
    }
}