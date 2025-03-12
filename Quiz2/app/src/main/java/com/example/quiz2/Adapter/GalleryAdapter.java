package com.example.quiz2.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quiz2.Entity.Item;
import com.example.quiz2.R;

import java.net.URI;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {
    private Context context;
    private List<Item> items;

    public GalleryAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout, altså lage et view av layouten
        View view = LayoutInflater.from(context).inflate(R.layout.activity_gallery, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Gir verdier til hver verdi som kommer inn i viewet, basert på posisjonen i listen
        // Setter navn til bilde
        holder.textView.setText(items.get(position).getName());

        Uri bildeUri = items.get(position).getImageAsUri();

        Glide.with(context)
                .load(bildeUri)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
