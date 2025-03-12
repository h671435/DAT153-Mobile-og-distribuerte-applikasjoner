package com.example.quiz2.Entity;

import android.net.Uri;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.net.URI;

@Entity(tableName = "items")
public class Item {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String image;  // Store URI as a String

    // Constructor with URI
    public Item(String name, URI image) {
        this.name = name;
        this.image = image.toString();  // Convert URI to String
    }

    // Second constructor with String
    public Item(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public Uri getImageAsUri() {
        return Uri.parse(image);  // Convert back to URI
    }

    public String getImage() {
        return image;  // Get raw string
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setImage(URI image) {
        this.image = image.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
