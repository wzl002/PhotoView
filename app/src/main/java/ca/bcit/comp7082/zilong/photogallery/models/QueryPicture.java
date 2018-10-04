package ca.bcit.comp7082.zilong.photogallery.models;

import java.io.Serializable;
import java.util.Date;

public class QueryPicture implements Serializable {

    private String title;

    private Date fromDate;

    private Date toDate;

    private boolean searchByArea;

    private double northeastLat;

    private double northeastLong;

    private double southwestLat;

    private double southwestLong;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public boolean isSearchByArea() {
        return searchByArea;
    }

    public void setSearchByArea(boolean searchByArea) {
        this.searchByArea = searchByArea;
    }

    public double getNortheastLat() {
        return northeastLat;
    }

    public void setNortheastLat(double northeastLat) {
        this.northeastLat = northeastLat;
    }

    public double getNortheastLong() {
        return northeastLong;
    }

    public void setNortheastLong(double northeastLong) {
        this.northeastLong = northeastLong;
    }

    public double getSouthwestLat() {
        return southwestLat;
    }

    public void setSouthwestLat(double southwestLat) {
        this.southwestLat = southwestLat;
    }

    public double getSouthwestLong() {
        return southwestLong;
    }

    public void setSouthwestLong(double southwestLong) {
        this.southwestLong = southwestLong;
    }
}
