package com.example.kdeuser.testproject.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker  implements ClusterItem {
    private LatLng position;
    private String title;
    private String snippet;
    private int iconPicture;
    //user refrence


    public ClusterMarker(LatLng position, String title, String snippet, int iconPicture) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPicture = iconPicture;
    }

    public ClusterMarker() {
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public void setIconPicture(int iconPicture) {
        this.iconPicture = iconPicture;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public int getIconPicture() {
        return iconPicture;
    }
}
