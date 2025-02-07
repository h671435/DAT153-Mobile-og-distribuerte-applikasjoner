package no.dat153.thequizapp;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {
    Context context;
    ArrayList<GalleryBilde> bilder;

    public GalleryAdapter(Context context, ArrayList<GalleryBilde> bilder) {
        this.context = context;
        this.bilder = bilder;
    }

    @NonNull
    @Override
    public GalleryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout for adapter. Gjør at radene får riktig layout??
        LayoutInflater inflation = LayoutInflater.from(context);
        View view = inflation.inflate(R.layout.recycler_view_rad, parent, false);

        return new GalleryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.MyViewHolder holder, int position) {
        // Gir verdier til hver verdi som kommer inn i viewet, basert på posisjonen i listen
        holder.textView.setText(bilder.get(position).getNavn());

        Uri bildeUri = bilder.get(position).getBildeUri();

        Glide.with(context)
                .load(bildeUri) // Load image from Uri (works for both resources and external URIs)
                .into(holder.imageView); // Set the loaded image into ImageView
    }

    @Override
    public int getItemCount() {
        // Hvor mange items blir displayet
        return bilder.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // Henter views fra recycler_view_rad. Litt som oncreate

        ImageView imageView;
        TextView textView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);

            // Sletting av bilde
            itemView.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                builder.setTitle("Slett bilde")
                        .setMessage("Er du sikker på at du vil slette dette bildet?")
                        .setNegativeButton("Ja", (dialog, which) -> {
                            ((GalleryActivity) itemView.getContext()).removeImage(getAdapterPosition());
                        })
                        .setPositiveButton("Nei", (dialog, which) -> dialog.dismiss())
                        .show();
                return true;
            });
        }
    }
}
