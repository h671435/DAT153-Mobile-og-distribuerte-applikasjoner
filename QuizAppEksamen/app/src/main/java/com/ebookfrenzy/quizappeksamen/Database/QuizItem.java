package com.ebookfrenzy.quizappeksamen.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "QuizItems")
public class QuizItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String imageUri;
    private String imageName;

    public QuizItem(String imageUri, String imageName) {
        this.imageUri = imageUri;
        this.imageName = imageName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
