package com.example.musicplayer;

import java.io.Serializable;

public class AudioModel implements Serializable {
    String aPath;
    String aName;
    String aAlbum;
    String aArtist;
    long aDuration;
    int id, year;
    String trackNbr;
    String totalTracks;
    String favorite;

    public AudioModel(String path, String name, String album, String artist, int id, long duration, int yr) {
        aPath = path;
        aName = name;
        aAlbum = album;
        aArtist = artist;
        this.id = id;
        aDuration = duration;
        year = yr;
    }

    public AudioModel(String path, String name, String album, String artist, int id, long duration, int yr, String trackNbr, String totalTracks) {
        aPath = path;
        aName = name;
        aAlbum = album;
        aArtist = artist;
        this.id = id;
        aDuration = duration;
        year = yr;
        this.trackNbr = trackNbr;
        this.totalTracks = totalTracks;
    }

    public AudioModel(String path, String name, String album, String artist, int id, long duration, int yr, String trackNbr, String totalTracks, String fav) {
        aPath = path;
        aName = name;
        aAlbum = album;
        aArtist = artist;
        this.id = id;
        aDuration = duration;
        year = yr;
        this.trackNbr = trackNbr;
        this.totalTracks = totalTracks;
        favorite = fav;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getTotalTracks() {
        return totalTracks;
    }

    public void setTotalTracks(String totalTracks) {
        this.totalTracks = totalTracks;
    }

    public String getTrackNbr() {
        return trackNbr;
    }

    public void setTrackNbr(String trackNbr) {
        this.trackNbr = trackNbr;
    }

    public long getaDuration() {
        return aDuration;
    }

    public void setaDuration(long aDuration) {
        this.aDuration = aDuration;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AudioModel(String album)
    {
        aAlbum = album;
    }

    public AudioModel () { }

    public String getaPath() {
        return aPath;
    }

    public void setaPath(String aPath) {
        this.aPath = aPath;
    }

    public String getaName() {
        return aName;
    }

    public void setaName(String aName) {
        this.aName = aName;
    }

    public String getaAlbum() {
        return aAlbum;
    }

    public void setaAlbum(String aAlbum) {
        this.aAlbum = aAlbum;
    }

    public String getaArtist() {
        return aArtist;
    }

    public void setaArtist(String aArtist) {
        this.aArtist = aArtist;
    }

    public int compareTo(Object o) {
        AudioModel compare = (AudioModel) o;

        if (compare.id == this.id && compare.aName.equals(this.aName) && compare.aArtist.equals(this.aArtist)) {
            return 0;
        }
        return 1;
    }
}
