package ca.bcit.comp7082.zilong.photogallery.models;

import java.io.Serializable;
import java.util.Date;

public class QueryPicture implements Serializable {

    private String title;

    private Date fromDate;

    private Date toDate;

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
}
