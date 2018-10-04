package ca.bcit.comp7082.zilong.photogallery.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;

@Entity(tableName = "picture")
public class Picture {

    // @PrimaryKey's autoGenerate property.
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "time")
    private long time;

    @ColumnInfo(name = "uri")
    private String uri;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @Ignore
    Bitmap pictureBitmap;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Bitmap getPictureBitmap() {
        return pictureBitmap;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setPictureBitmap(Bitmap pictureBitmap) {
        this.pictureBitmap = pictureBitmap;
    }
}
