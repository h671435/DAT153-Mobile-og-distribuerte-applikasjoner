package no.dat153.thequizapp;

public class GalleryBilde {
    private final int bildeId;
    private final String navn;

    public GalleryBilde(int bildeId, String navn) {
        this.bildeId = bildeId;
        this.navn = navn;
    }

    public int getBildeId() {
        return bildeId;
    }

    public String getNavn() {
        return navn;
    }
}
