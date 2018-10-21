package ca.bcit.comp7082.zilong.photogallery.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtil {

    private static int DEFAULT_WIDTH = 500;

    private static int DEFAULT_HEIGHT = 500;

    /**
     * get uncompressed image from URI
     *
     * @param context
     * @param uri
     * @return
     */
    public static Bitmap getRawImage(Context context, Uri uri) throws IOException {
        return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
    }

    /**
     * get compressed image bitmap from URI with default width and height
     *
     * @param context
     * @param uri
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap decodeBitmapFromURI(Context context, Uri uri) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(uri);

        return decodeBitmapFromSteam(input, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * get compressed image bitmap from File with default width and height
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap decodeBitmapFromFile(File file) throws IOException {
        return decodeBitmapFromSteam(new FileInputStream(file), DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * get compressed image bitmap from URI
     *
     * @param context
     * @param uri
     * @param reqWidth
     * @param reqHeight
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap decodeBitmapFromURI(Context context, Uri uri, int reqWidth, int reqHeight) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(uri);

        return decodeBitmapFromSteam(input, reqWidth, reqHeight);
    }

    /**
     * get compressed image bitmap from inputStream
     *
     * @param inputStream
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeBitmapFromSteam(InputStream inputStream, int reqWidth, int reqHeight) throws IOException {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        BufferedInputStream buffer = new BufferedInputStream(inputStream);

        options.inJustDecodeBounds = true;
        buffer.mark(buffer.available());

        BitmapFactory.decodeStream(buffer, null, options);
        buffer.reset(); // Stream must be reset after decode

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(buffer, null, options);
    }

    /**
     * get compressed image bitmap from resource
     *
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeBitmapFromResource(Resources res, int resId,
                                                  int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    protected static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        int inSampleSize = 1;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((options.outHeight / 2 / inSampleSize) >= reqHeight
                && (options.outWidth / 2 / inSampleSize) >= reqWidth) {
            inSampleSize *= 2;
        }

        return inSampleSize;
    }

}
