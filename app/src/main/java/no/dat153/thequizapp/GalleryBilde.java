package no.dat153.thequizapp;

import android.net.Uri;

public class GalleryBilde {
    private final Uri bildeUri;
    private final String navn;

    public GalleryBilde(Uri bildeUri, String navn) {
        this.bildeUri = bildeUri;
        this.navn = navn;
    }

    public Uri getBildeUri() {
        return bildeUri;
    }

    public String getNavn() {
        return navn;
    }
}
