package com.ebookfrenzy.quizappeksamen.Database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.ebookfrenzy.quizappeksamen.R;

import java.util.concurrent.Executors;

@Database(entities = {QuizItem.class}, version = 1, exportSchema = false)
public abstract class QuizItemDatabase extends RoomDatabase {

    private static final String TAG = "QuizItemDatabase";
    private static volatile QuizItemDatabase INSTANCE;

    public static QuizItemDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (QuizItemDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    QuizItemDatabase.class, "QuizItemDatabase")
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        QuizItem[] defaultItems = new QuizItem[] {
                                                new QuizItem("android.resource://" + context.getPackageName() + "/" + R.drawable.ulv, "Ulvvv"),
                                                new QuizItem("android.resource://" + context.getPackageName() + "/" + R.drawable._ec0ce0c889800a9298367fe942471c9, "Sjef ulv")
                                        };

                                        Log.d(TAG, "Legger til standarddata i databasen...");
                                        getDatabase(context).quizItemDAO().insertAll(defaultItems);
                                        Log.d(TAG, "Lagt til " + defaultItems.length + " bilder i databasen!");
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract QuizItemDAO quizItemDAO();
}