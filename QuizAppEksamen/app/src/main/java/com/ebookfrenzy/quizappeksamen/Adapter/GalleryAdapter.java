package com.ebookfrenzy.quizappeksamen.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ebookfrenzy.quizappeksamen.Database.QuizItem;
import com.ebookfrenzy.quizappeksamen.R;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {
    Context context;
    ArrayList<QuizItem> quizItems;

    public GalleryAdapter(Context context, ArrayList<QuizItem> quizItems) {
        this.context = context;
        this.quizItems = quizItems;
    }

    @NonNull
    @Override
    public GalleryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Her inflater vi layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.gallery_card_row, parent, false);

        return new GalleryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.MyViewHolder holder, int position) {
        // Gir verdier til hver rad når de kommer på siden
        // Basert på positionen til recycler view
        holder.imageNameText.setText(quizItems.get(position).getImageName());
        try {
            Glide.with(context)
                    .load(Uri.parse(quizItems.get(position).getImageUri()))
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.imageView);
        } catch (Exception e) {
            Log.d("lol", "Idk ka en log e");
        }

//        holder.imageView.setImageURI(Uri.parse(quizItems.get(position).getImageUri()));
    }

    @Override
    public int getItemCount() {
        // Kor mange ting som skal vises
        return quizItems.size();
    }

    // Henter alle views fra radene fra layout filen
    // nesten som onCreate method
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView imageNameText;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.recyclerImageView);
            imageNameText = itemView.findViewById(R.id.recyclerTextView);
        }
    }
}
