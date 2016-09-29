package gogobike.egg.com.gogobike;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import gogobike.egg.com.entity.BikeRoute;
import gogobike.egg.com.util.PermissionUtils;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

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
    }

    private void getExtras() {
        Intent intent = getIntent();
        if(intent == null){
            return;
        }

        Bundle bundle = intent.getExtras();
        if(bundle == null){
            return;
        }

        bikeRoute = (BikeRoute) bundle.getSerializable(SERIALIZABLE_BIKE_ROUTE_DATA);
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
        updateMap(bikeRoute);

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


    private void enableMyLocation() {
        //noinspection MissingPermission
        map.setMyLocationEnabled(true);

    }

    public void updateMap(BikeRoute bikeRoute) {
        if (bikeRoute == null) {
            return;
        }

        int routeSize = bikeRoute.getLatitudeList().size();
        List<Double> latitudeList = bikeRoute.getLatitudeList();
        List<Double> longitudeList = bikeRoute.getLongitudeList();

        LatLng startPoint = new LatLng(latitudeList.get(0), longitudeList.get(0));
        LatLng endPoint = new LatLng(latitudeList.get(routeSize - 1), longitudeList.get(routeSize - 1));

        // add start and end marker
        map.addMarker(new MarkerOptions().position(startPoint).title(bikeRoute.getOrigin()));
        map.addMarker(new MarkerOptions().position(endPoint).title(bikeRoute.getDestination()));

        // add polyline using points from directions
//        map.addPolyline(new PolylineOptions()
//                .addAll(directions.latLngs)
//                .color(Color.BLUE));

        List<LatLng> latLngs = new ArrayList<>();
        for(int i = 0; i < routeSize;i++){
            LatLng latLng = new LatLng(latitudeList.get(i), longitudeList.get(i));
            latLngs.add(latLng);
        }
        addPolylineTo(latLngs);

        LatLng centerPoint = new LatLng(latitudeList.get(routeSize/2), longitudeList.get(routeSize/2));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(centerPoint, 15));
    }

    private static final float POLYLINE_WIDTH_IN_PIXELS = 10;
    private static final float POLYLINE_STROKE_WIDTH_IN_PIXELS = 2;

    public void addPolylineTo(List<LatLng> latLngs) {
        int fillColor = ContextCompat.getColor(this, R.color.polylineFillColor);
        int strokeColor = ContextCompat.getColor(this, R.color.polylineStrokeColor);

        PolylineOptions fill = new PolylineOptions();
        fill.color(fillColor).width(POLYLINE_WIDTH_IN_PIXELS).addAll(latLngs);


        map.addPolyline(fill);
//        googleMap.addPolyline(fill);
    }

}