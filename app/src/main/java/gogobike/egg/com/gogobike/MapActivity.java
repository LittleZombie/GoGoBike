package gogobike.egg.com.gogobike;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.net.MalformedURLException;
import java.net.URL;

import gogobike.egg.com.entity.BikeRoute;
import gogobike.egg.com.service.Directions;
import gogobike.egg.com.service.DownloadDirectionsTask;
import gogobike.egg.com.util.PermissionUtils;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, DownloadDirectionsTask.BikeRouteListener {

    public static final String SERIALIZABLE_BIKE_ROUTE_DATA = "SERIALIZABLE_BIKE_ROUTE_DATA";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static String urlRequest = "https://maps.googleapis.com/maps/api/directions/xml?";
    private static String SERVER_KEY;

    private BikeRoute bikeRoute;
    private GoogleMap map;
    private boolean mShowPermissionDeniedDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        getExtras();

        initGoogleMapApiKey();
        initMapFragment();

        requestBikeRoute();
    }

    private void getExtras() {
        bikeRoute = (BikeRoute) getIntent().getSerializableExtra(SERIALIZABLE_BIKE_ROUTE_DATA);
    }

    private void initGoogleMapApiKey() {
        SERVER_KEY = getResources().getString(R.string.google_maps_key);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mShowPermissionDeniedDialog) {
            PermissionUtils.PermissionDeniedDialog
                    .newInstance(false).show(getSupportFragmentManager(), "dialog");
            mShowPermissionDeniedDialog = false;
        }
    }

    private void initMapFragment() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        updateMyLocation();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, results, Manifest.permission.ACCESS_FINE_LOCATION)) {
            enableMyLocation();
        } else {
            mShowPermissionDeniedDialog = true;
        }
    }

    private void updateMyLocation() {
        // Enable the location layer. Request the location permission if needed.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        } else {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        }
    }


    private void enableMyLocation(){
        //noinspection MissingPermission
        map.setMyLocationEnabled(true);

    }

    private void requestBikeRoute(){
        if(bikeRoute == null){
            return;
        }

        String toUrl = urlRequest + "origin=" + bikeRoute.getOrigin() + "&"
                + "destination=" + bikeRoute.getDestination() + "&"
                + "avoid=ferries" + "&"
                + "key=" + SERVER_KEY;

        Log.e("googlemap", "url : " + toUrl);
        try {
            URL url = new URL(toUrl);
            // start task
            new DownloadDirectionsTask(this, MapActivity.this).execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void updateMap(String start, String end, Directions directions)
    {
        if (directions.directions.size() == 0)
            return;
        // very first and last latlng in array contains starting and ending point
        int size = directions.latLngs.size();
        LatLng startPoint = directions.latLngs.get(0);
        LatLng endPoint = directions.latLngs.get(size-1);
        // add start and end marker
        map.addMarker(new MarkerOptions().position(startPoint).title(start));
        map.addMarker(new MarkerOptions().position(endPoint).title(end));
        // add polyline using points from directions
        map.addPolyline(new PolylineOptions()
                .addAll(directions.latLngs)
                .color(Color.BLUE));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 13));
    }

    @Override
    public void BikeRouteSuccess(Directions directions) {
        updateMap(bikeRoute.getOrigin(), bikeRoute.getDestination(), directions);
    }

    @Override
    public void BikeRouteFailed() {
        Toast.makeText(this, "Error, Try again later", Toast.LENGTH_SHORT).show();
    }
}