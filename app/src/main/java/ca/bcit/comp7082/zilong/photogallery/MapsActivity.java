package ca.bcit.comp7082.zilong.photogallery;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

public abstract class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        // mMap.setOnMyLocationClickListener(onMyLocationClickListener);
        // mMap.setOnMyLocationClickListener(showMyLocationMarker);

        enableMyLocationIfPermitted();

        // mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMinZoomPreference(15);

        mMap.setOnCameraMoveListener(getScreenArea);
    }

    protected void enableMyLocationIfPermitted() {
        // check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            LatLng location = getCurrentLocation(this);
            // mMap.addMarker(new MarkerOptions().position(location).title("YOU ARE HERE"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
        }
    }

    public static LatLng getCurrentLocation(Context context) {
        // check permission
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return getDefaultLocation();
        }

        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria mCriteria = new Criteria();
        String bestProvider = String.valueOf(manager.getBestProvider(mCriteria, true));
        Location mLocation = manager.getLastKnownLocation(bestProvider);

        if (mLocation != null) {
            Log.d("MapsActivity", "GPS is on");
            final double currentLatitude = mLocation.getLatitude();
            final double currentLongitude = mLocation.getLongitude();
            return new LatLng(currentLatitude, currentLongitude);
        } else {
            Log.e("MapsActivity", "Location Permission not granted");
            return getDefaultLocation();
        }
    }

    public static LatLng getDefaultLocation() {
        LatLng vancouver = new LatLng(49.246292, -123.116226);
        return vancouver;
    }


    protected void showDefaultLocation() {
        Toast.makeText(this, "Location permission not granted, showing default location", Toast.LENGTH_SHORT).show();
        LatLng vancouver = getDefaultLocation();
        mMap.addMarker(new MarkerOptions().position(vancouver).title("You May Be Here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(vancouver));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocationIfPermitted();
                } else {
                    showDefaultLocation();
                }
            }

        }
    }

    protected GoogleMap.OnCameraMoveListener getScreenArea =
            new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {
                    VisibleRegion screen = mMap.getProjection().getVisibleRegion();

                    // mMap.addMarker(new MarkerOptions().position(screen.latLngBounds.northeast).title("northeast"));
                    // mMap.addMarker(new MarkerOptions().position(screen.latLngBounds.southwest).title("southwest"));

                    // Log.d("log#################", screen.latLngBounds.northeast.latitude+"");

                    setScreenRegion(screen.latLngBounds);
                }
            };

    protected abstract void setScreenRegion(LatLngBounds latLngBounds);


    protected GoogleMap.OnMyLocationClickListener showMyLocationMarker =
            new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),
                            location.getLongitude())).title("YOU ARE HERE"));
                }
            };

    protected GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    mMap.setMinZoomPreference(15);
                    return false;
                }
            };

    protected GoogleMap.OnMyLocationClickListener onMyLocationClickListener =
            new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {

                    mMap.setMinZoomPreference(12);

                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(new LatLng(location.getLatitude(),
                            location.getLongitude()));

                    circleOptions.radius(200);
                    circleOptions.fillColor(Color.RED);
                    circleOptions.strokeWidth(6);

                    mMap.addCircle(circleOptions);
                }
            };
}
