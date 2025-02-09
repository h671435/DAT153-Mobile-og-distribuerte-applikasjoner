package no.dat153.thequizapp;

import android.app.Application;

import java.util.ArrayList;

public class QuizApp extends Application {
    private static QuizApp instance;
    private ArrayList<GalleryBilde> bilder;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        bilder = new ArrayList<>();
    }

    public static QuizApp getInstance() {
        return instance;
    }

    public ArrayList<GalleryBilde> getBilder() {
        return bilder;
    }
}
