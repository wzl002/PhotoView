package ca.bcit.comp7082.zilong.photogallery;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ca.bcit.comp7082.zilong.photogallery.models.Picture;
import ca.bcit.comp7082.zilong.photogallery.models.PictureDao;
import ca.bcit.comp7082.zilong.photogallery.models.QueryPicture;
import ca.bcit.comp7082.zilong.photogallery.utils.BitmapUtil;

public class PhotoService {

    private Context context;

    private String defaultPhotoName = "new photo";

    public PhotoService(Context context) {
        this.context = context;
    }

    // delete all in Picture table, for test purpose. todo: clear folder after clear table
    public void resetDatabase() {
        PictureDao dao = DatabaseService.getPictureDao();
        for (Picture p : dao.getAll()) {
            dao.delete(p);
        }
    }

    public List<Picture> getPictures(int start) {
        return DatabaseService.getPictureDao().getAll(); //TODO page limit
    }

    public List<Picture> queryPictures(int start, QueryPicture queryPicture) {
        String title = queryPicture.getTitle();
        long fromDate = queryPicture.getFromDate() != null ? queryPicture.getFromDate().getTime() : 0; // fromDate default from 0
        long toDate = queryPicture.getFromDate() != null ? queryPicture.getToDate().getTime() : (new Date()).getTime(); // toDate default to now

        if (title == null || title.trim().length() == 0) {
            title = "%";
        }
        if (queryPicture.isSearchByArea()) {
            double NELat = queryPicture.getNortheastLat();
            double NELong = queryPicture.getNortheastLong();
            double SWLat = queryPicture.getSouthwestLat();
            double SWLong = queryPicture.getSouthwestLong();
            return DatabaseService.getPictureDao().findByNameAndTimeAndLocation("%" + title + "%", fromDate, toDate, NELat, SWLat, NELong, SWLong);
        } else {
            return DatabaseService.getPictureDao().findByNameAndTime("%" + title + "%", fromDate, toDate);
        }
    }

    public void updatePicture(Picture picture) {
        DatabaseService.getPictureDao().updatePictures(picture);
    }

    protected Bitmap getBitmap(Picture picture) {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String fileName = picture.getUri();
        File pictureFile = new File(storageDir, fileName);

        Uri uri = FileProvider.getUriForFile(
                context,
                "ca.bcit.comp7082.zilong.photogallery.fileprovider",
                pictureFile);

        Bitmap bitmap = null;
        try {
            bitmap = BitmapUtil.decodeBitmapFromFile(pictureFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * create a new picture and insert into database
     *
     * @param fileUri
     * @param currentLocation
     * @return
     */
    public Picture savePictureToDB(Uri fileUri, LatLng currentLocation) {
        PictureDao dao = DatabaseService.getPictureDao();
        Picture picture = new Picture();
        picture.setTime((new Date().getTime()));
        picture.setTitle(defaultPhotoName);

        String fileName = new File(fileUri.getPath()).getName();
        picture.setUri(fileName);

        if (currentLocation != null) {
            picture.setLatitude(currentLocation.latitude);
            picture.setLongitude(currentLocation.longitude);
        }

        dao.insertAll(picture);
        return picture;
    }


    /**
     * create a jpg file and return uri
     * Camera Intent is used to do deal with Security Exception
     *
     * @param imageCaptureIntent
     */
    public Uri createImageFile(Intent imageCaptureIntent) {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // automatically save file into gallery:
        // File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        File jpgFile = null;
        try {
            jpgFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            // jpgFile looks like /storage/emulated/0/Android/data/ca.bcit.comp7082.zilong.photogallery/files/Pictures/JPEG_20180916_023622_265931378.jpg
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (jpgFile != null) {
            Uri photoURI = FileProvider.getUriForFile(
                    context,
                    "ca.bcit.comp7082.zilong.photogallery.fileprovider",
                    jpgFile);

            this.dealWithSecurityException(imageCaptureIntent, photoURI);

            return photoURI;
        }
        return null;
    }

    /**
     * // Without it, you may get Security Exceptions.
     * // Taken from: https://github.com/commonsguy/cw-omnibus/blob/master/Camera/FileProvider/app/src/main/java/com/commonsware/android/camcon/MainActivity.java
     *
     * @param intent
     * @param imageUri
     */
    private void dealWithSecurityException(Intent intent, Uri imageUri) {
        if (intent == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ClipData clip = ClipData.newUri(context.getContentResolver(), "photo", imageUri);

            intent.setClipData(clip);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            List<ResolveInfo> resInfoList =
                    context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                context.grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        }
    }

}
