package no.dat153.thequizapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.io.File;
import java.util.HashMap;
import java.util.stream.IntStream;

import android.os.Environment;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GalleryActivity extends AppCompatActivity {

    ArrayList<GalleryBilde> bilder = new ArrayList<>();
    private Uri bildeUri;

    private GalleryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RecyclerView recyclerView;
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gallery);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.min_recycler_view);

        loadSavedImages();
        if (bilder.isEmpty()) {
            setUpGalleryBilder();
        }

        adapter = new GalleryAdapter(this, bilder);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sorter listen
        findViewById(R.id.button_sort).setOnClickListener(v -> sorterBilder());
        findViewById(R.id.button_add_image).setOnClickListener(addImage());
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveImages();
    }

    void removeImage(int position) {
        if (position >= 0 && position < bilder.size()) {
            bilder.remove(position);
            adapter.notifyItemRemoved(position);
            saveImages();
        }
    }

    private void sorterBilder() {
        boolean isSorted = true;
        for (int i = 0; i < bilder.size() - 1; i++) {
            if (bilder.get(i).getNavn().compareTo(bilder.get(i + 1).getNavn()) > 0) {
                isSorted = false;
                break;
            }
        }

        if (isSorted) {
            bilder.sort((b1, b2) -> b2.getNavn().compareTo(b1.getNavn()));
        } else {
            bilder.sort(Comparator.comparing(GalleryBilde::getNavn));
        }

        adapter.notifyItemRangeChanged(0, bilder.size());
    }

    private void saveImages() {
        SharedPreferences sharedPreferences = getSharedPreferences("BildeData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        ArrayList<HashMap<String, String>> bildeURIer = new ArrayList<>();
        for (GalleryBilde bilde : bilder) {
            HashMap<String, String> bildeData = new HashMap<>();
            bildeData.put("uri", bilde.getBildeUri().toString());
            bildeData.put("navn", bilde.getNavn());
            bildeURIer.add(bildeData);
        }

        editor.putString("bildeURIer", new Gson().toJson(bildeURIer));
        editor.apply();
    }

    private void loadSavedImages() {
        SharedPreferences sharedPreferences = getSharedPreferences("BildeData", MODE_PRIVATE);
        String savedImagesJson = sharedPreferences.getString("bildeURIer", null);

        if (savedImagesJson != null) {
            ArrayList<HashMap<String, String>> bildeURIer = new Gson().fromJson(savedImagesJson, new TypeToken<ArrayList<HashMap<String, String>>>() {
            }.getType());

            if (bildeURIer != null) {
                for (HashMap<String, String> bildeData : bildeURIer) {
                    String uriString = bildeData.get("uri");
                    String navn = bildeData.get("navn");

                    bilder.add(new GalleryBilde(Uri.parse(uriString), navn));
                }
            }
        }

    }

    private void setUpGalleryBilder() {
        bilder.add(new GalleryBilde(getResourceUri(R.drawable.katt), "Katt"));
        bilder.add(new GalleryBilde(getResourceUri(R.drawable.hund), "Hund"));
        bilder.add(new GalleryBilde(getResourceUri(R.drawable.snow_leopard), "Snø-Hest"));
    }

    // Hjelpemetode for å konvertere R.drawable til Uri
    private Uri getResourceUri(int resId) {
        return Uri.parse("android.resource://" + getPackageName() + "/" + resId);
    }


    private View.OnClickListener addImage() {
        return v -> visBildeValgDialog();
    }

    private void visBildeValgDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Velg bilde kamera eller bilde mobil")
                .setMessage("Velg en handling")
                .setPositiveButton("Ta et bilde", (dialog, which) -> openCamera())
                .setNegativeButton("Velg fra galleri", (dialog, which) -> openBilder())
                .setNeutralButton("Avbryt", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Sjekker om det finnes en app som kan ta bilder
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File bildeFil = createImageFile();
            if (bildeFil != null) {
                bildeUri = FileProvider.getUriForFile(this, "no.dat153.thequizapp.fileprovider", bildeFil);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, bildeUri);
                startActivityForResult(cameraIntent, 1);
            }
        }
    }

    private File createImageFile() {
        String tidspunkt = new SimpleDateFormat("yyyyMMdd_HHmmss").format(System.currentTimeMillis());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            return File.createTempFile("JPEG_" + tidspunkt + "_", ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) { // Camera
                leggVedNavnPaaBildeSomBlirTatt(bildeUri);
            } else if (requestCode == 2 && data != null) {
                    Uri selectedImageUri = data.getData(); // Henter URI til valgt bilde
                    leggVedNavnPaaBildeSomBlirTatt(selectedImageUri);
                }

        }
    }
    private void leggVedNavnPaaBildeSomBlirTatt(Uri bildeUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Skriv inn hva du har tatt bilde av");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Ok", (dialog, which) -> {
            String bildeNavn = input.getText().toString();
            if (!bildeNavn.isEmpty()) {
                bilder.add(new GalleryBilde(bildeUri, bildeNavn));
                adapter.notifyItemInserted(bilder.size() - 1);
            }
        });

        builder.setNegativeButton("Avbryt", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openBilder() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 2);
    }
}