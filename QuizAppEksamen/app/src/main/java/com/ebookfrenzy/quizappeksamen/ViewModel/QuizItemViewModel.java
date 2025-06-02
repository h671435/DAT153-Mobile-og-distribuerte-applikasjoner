package com.ebookfrenzy.quizappeksamen.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ebookfrenzy.quizappeksamen.Database.QuizItem;
import com.ebookfrenzy.quizappeksamen.Database.QuizRepo;

import java.util.List;

public class QuizItemViewModel extends AndroidViewModel {
    private final QuizRepo repository;
    private final LiveData<List<QuizItem>> allQuizItems;
    private MutableLiveData<Integer> correctGuesses = new MutableLiveData<>(0);
    private MutableLiveData<Integer> totalGuesses = new MutableLiveData<>(0);

    public QuizItemViewModel(@NonNull Application application) {
        super(application);
        repository = new QuizRepo(application);
        allQuizItems = repository.getAllQuizItems();
    }

    // Get all quiz items
    public LiveData<List<QuizItem>> getAllQuizItems() {
        return allQuizItems;
    }

    // Insert a quiz item
    public void insert(QuizItem quizItem) {
        repository.insert(quizItem);
    }

    // Update a quiz item
    public void update(QuizItem quizItem) {
        repository.update(quizItem);
    }

    // Delete a quiz item
    public void delete(QuizItem quizItem) {
        repository.delete(quizItem);
    }

    // Delete all quiz items
    public void deleteAll() {
        repository.deleteAll();
    }

    public MutableLiveData<Integer> getCorrectGuesses() {
        return correctGuesses;
    }

    public void setCorrectGuesses(MutableLiveData<Integer> correctGuesses) {
        this.correctGuesses = correctGuesses;
    }

    public MutableLiveData<Integer> getTotalGuesses() {
        return totalGuesses;
    }

    public void setTotalGuesses(MutableLiveData<Integer> totalGuesses) {
        this.totalGuesses = totalGuesses;
    }

    public void incrementTotalGuesses() {
        totalGuesses.setValue(totalGuesses.getValue() + 1);
    }

    public void incrementCorrectGuesses() {
        correctGuesses.setValue(correctGuesses.getValue() + 1);
    }
}