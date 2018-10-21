package ca.bcit.comp7082.zilong.photogallery;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ca.bcit.comp7082.zilong.photogallery.models.Picture;
import ca.bcit.comp7082.zilong.photogallery.models.QueryPicture;
import ca.bcit.comp7082.zilong.photogallery.services.DatabaseService;
import ca.bcit.comp7082.zilong.photogallery.services.PhotoService;
import ca.bcit.comp7082.zilong.photogallery.utils.BitmapUtil;

public class MainActivity extends AppCompatActivity {

    /**
     * take photo
     */
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_SEARCH_ACTIVITY = 2;

    private PhotoService photoService;

    // save each uri before start camera.
    private Uri mCurrentPhotoPath;

    // pictures show in image view
    private List<Picture> pictureList;

    private int currentPictureIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseService.initDatabaseService(this);

        photoService = new PhotoService(this);

        initView();

    }

    protected void initView() {
        pictureList = photoService.getPictures(0);
        if (pictureList.size() > 0) {
            this.loadPictureView(pictureList.get(0));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        final MainActivity activity = this;
        MenuItem searchItem = menu.findItem(R.id.menu_search);

        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(activity, SearchActivity.class);
                startActivityForResult(intent, REQUEST_SEARCH_ACTIVITY);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_left);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * snap button click listener: start camera
     *
     * @param view
     */
    public void onSnapClick(View view) {
        Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  // device camera Intent
        // Ensure that there's a camera activity to handle the intent
        if (imageCaptureIntent.resolveActivity(getPackageManager()) != null) {

            Uri photoFileURI = photoService.createImageFile(imageCaptureIntent); // Create the File where the photo should go
            if (photoFileURI != null) {

                // when set uri for output, the Intent in onActivityResult will be null. Image can only be get by path
                // if not set URI, picture will save to default device picture folder.
                imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileURI);

                mCurrentPhotoPath = photoFileURI;

                startActivityForResult(imageCaptureIntent, REQUEST_TAKE_PHOTO);     // start device camera activity for result
            }
        }
    }

    /**
     * left button click: show previous picture
     */
    public void onLeftClick(View view) {
        if (currentPictureIndex > 0 && pictureList.size() > 0) {
            if (currentPictureIndex > pictureList.size()) {
                currentPictureIndex = 1; // if enter here, Error Index, need debug
            }
            this.loadPictureView(pictureList.get(--currentPictureIndex));
        }
    }

    /**
     * right button click: show next picture
     */
    public void onRightClick(View view) {
        if (pictureList.size() > currentPictureIndex + 1) {
            this.loadPictureView(pictureList.get(++currentPictureIndex));
        }
    }

    public void onEditClick(View view) {
        findViewById(R.id.save_button).setVisibility(View.VISIBLE);
        findViewById(R.id.edit_button).setVisibility(View.GONE);

        TextView photoName = findViewById(R.id.photo_name_text);
        TextView photoNameInput = findViewById(R.id.photo_name_text_input);

        photoNameInput.setText(photoName.getText());
        photoNameInput.setVisibility(View.VISIBLE);
        photoName.setVisibility(View.GONE);
    }

    /**
     * save button click: save name change
     */
    public void onSaveClick(View view) {

        TextView photoName = findViewById(R.id.photo_name_text);
        TextView photoNameInput = findViewById(R.id.photo_name_text_input);

        photoName.setText(photoNameInput.getText());

        Picture picture = pictureList.get(currentPictureIndex);
        picture.setTitle(photoNameInput.getText().toString());
        photoService.updatePicture(picture);

        findViewById(R.id.save_button).setVisibility(View.GONE);
        findViewById(R.id.edit_button).setVisibility(View.VISIBLE);
        photoNameInput.setVisibility(View.GONE);
        photoName.setVisibility(View.VISIBLE);
    }

    /**
     * Call after take a photo in Camera
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            onActivityReturnOK(requestCode, data);
        }
    }

    protected void onActivityReturnOK(int requestCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            onCameraReturn(); // get image result from camera activity
        } else if (requestCode == REQUEST_SEARCH_ACTIVITY) {
            onSearchActivityReturn(data); // do search with parameters from search activity
        }
    }

    protected void onCameraReturn() {
        Picture picture = photoService.savePictureToDB(mCurrentPhotoPath, MapsActivity.getCurrentLocation(this));
        try {
            Bitmap bitmap = BitmapUtil.decodeBitmapFromURI(this, mCurrentPhotoPath);
            picture.setPictureBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // put new picture at head
        pictureList.add(0, picture);
        currentPictureIndex = 0;

        loadPictureView(picture);
    }

    protected void onSearchActivityReturn(Intent intent) {
        Bundle b = intent.getExtras();
        if (b != null) {
            QueryPicture query = (QueryPicture) b.getSerializable("queryData");

            pictureList = photoService.queryPictures(0, query);
            currentPictureIndex = 0;
            if (pictureList.size() > 0) {
                this.loadPictureView(pictureList.get(0));
            } else {
                this.loadPictureView(null);
            }
        }
    }

    protected void loadPictureView(Picture picture) {

        ImageView mImageView = findViewById(R.id.image_view);
        TextView photoName = findViewById(R.id.photo_name_text);
        TextView photoDate = findViewById(R.id.photo_date_text);
        if (picture == null) {
            mImageView.setImageBitmap(null);
            photoName.setText("No Result");
            photoDate.setText("");
            return;
        }

        Bitmap bitmap = picture.getPictureBitmap();
        if (bitmap == null) {
            bitmap = photoService.getBitmap(picture);
        }
        mImageView.setImageBitmap(bitmap);
        photoName.setText(picture.getTitle());
        String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Date(picture.getTime()));
        photoDate.setText(timeStamp);
    }

}


//
// Code for reference
//
//    public void initEvents() {
//
//        Button snapButtonBtn = (Button) findViewById(R.id.snap_button);
//        snapButtonBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(TakePhotoActivity.this, "SNAP", Toast.LENGTH_SHORT).show();
//            }
//
//        });
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.left_button:
//                Toast.makeText(MainActivity.this, "Left", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.right_button:
//                Toast.makeText(MainActivity.this, "Right", Toast.LENGTH_SHORT).show();
//                break;
//            default:
//                break;
//        }
//    }
