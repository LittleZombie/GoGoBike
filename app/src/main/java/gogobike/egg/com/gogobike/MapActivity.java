package gogobike.egg.com.gogobike;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import gogobike.egg.com.entity.BikeRoute;
import gogobike.egg.com.util.PermissionUtils;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, DialogInterface.OnDismissListener {

    public static final String SERIALIZABLE_BIKE_ROUTE_DATA = "SERIALIZABLE_BIKE_ROUTE_DATA";
    public static final String INTENT_MAP_MODE = "INTENT_MAP_MODE";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int ALARM_SETTING_REQUEST_CODE = 111;

    private static String urlRequest = "https://maps.googleapis.com/maps/api/directions/xml?";
    private static String SERVER_KEY;

    private BikeRoute bikeRoute;
    private GoogleMap map;
    private int mode;
    private boolean isRidingStart = false;
    private boolean mShowPermissionDeniedDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        getExtras();

        initActionbar();
        initGoogleMapApiKey();
        initMapFragment();
        layoutRoute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ALARM_SETTING_REQUEST_CODE && resultCode == RESULT_OK) {
            onAlarmSettingActivityResult(data);
        }
    }

    private void onAlarmSettingActivityResult(Intent data) {
        SharedPreferences sharedPreferences = getSharedPreferences(RouteListActivity.ROUTE_RECORD_SHARED_PREFERENCE, MODE_PRIVATE);
        String bikeRouteJsonString = sharedPreferences.getString(RouteListActivity.ROUTE_RECORD_LIST, null);
        List<BikeRoute> bikeRoutes;
        if (bikeRouteJsonString == null) {
            bikeRoutes = new LinkedList<>();
        } else {
            bikeRoutes = new Gson().fromJson(bikeRouteJsonString, new TypeToken<LinkedList<BikeRoute>>() {}.getType());

        }
        bikeRoute.setAlarmTime(data.getLongExtra(AlarmSettingActivity.INTENT_LONG_CALENDAR_MILLIS, 0));
        bikeRoutes.add(bikeRoute);
        sharedPreferences.edit().putString(RouteListActivity.ROUTE_RECORD_LIST, new Gson().toJson(bikeRoutes)).apply();
        startHomeActivity();
    }

    private void getExtras() {
        Intent intent = getIntent();
        if(intent == null){
            return;
        }
        mode = intent.getIntExtra(INTENT_MAP_MODE, 0);

        Bundle bundle = intent.getExtras();
        if(bundle == null){
            return;
        }

        bikeRoute = (BikeRoute) bundle.getSerializable(SERIALIZABLE_BIKE_ROUTE_DATA);
    }

    private void initActionbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (mode < 2) {
                actionBar.setTitle(R.string.title_activity_recommended_route);
            } else if (mode == 2) {
                actionBar.setTitle(R.string.title_activity_route_record);
            }
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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

    private void layoutRoute() {
        if (bikeRoute == null) {
            return;
        }

        ((TextView) findViewById(R.id.mapActivity_routeNameTextView)).setText(bikeRoute.getRouteName());
        ((TextView) findViewById(R.id.mapActivity_routeLengthTextView)).setText("(" + bikeRoute.getKilometer() + "KM)");
        LinearLayout pokemonLinearLayout = (LinearLayout) findViewById(R.id.mapActivity_pokemonLinearLayout);
        pokemonLinearLayout.setBackgroundResource(bikeRoute.getImageResourceId());
        if (bikeRoute.getPokemonImageResourceId() == null) {
            return;
        }

        for (int imageId : bikeRoute.getPokemonImageResourceId()) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(imageId);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(16, 0, 0, 16);
            imageView.setLayoutParams(layoutParams);
            pokemonLinearLayout.addView(imageView);
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

        //add poke stop
        MarkerOptions pokeMarker = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.pokestop));
        map.addMarker(pokeMarker.position(new LatLng(latitudeList.get(5), longitudeList.get(5))));
        map.addMarker(pokeMarker.position(new LatLng(latitudeList.get(15), longitudeList.get(15))));
        map.addMarker(pokeMarker.position(new LatLng(latitudeList.get(20), longitudeList.get(20))));

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

    public void onBlueBikeImageButtonClick(View view) {
        if (mode < 2) {
            new AlertDialog.Builder(this).setMessage(R.string.alarm_dialog_message)
                    .setNegativeButton("No", null)
                    .setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startAlarmSettingActivity();
                        }
                    }).show();
        } else if (mode == 2 && !isRidingStart) {
            isRidingStart = true;
            findViewById(R.id.mapActivity_pokemonLinearLayout).setVisibility(View.GONE);
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
            LatLng centerPoint = new LatLng(bikeRoute.getLatitudeList().get(0), bikeRoute.getLongitudeList().get(0));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(centerPoint, 18));
        } else if (mode == 2) {
            new AlertDialog.Builder(this).setMessage(R.string.end_riding_dialog_message)
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startRouteRatingDialog();
                        }
                    }).show();
        }
    }

    private void startAlarmSettingActivity() {
        Intent intent = new Intent(this, AlarmSettingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(SERIALIZABLE_BIKE_ROUTE_DATA, bikeRoute);
        intent.putExtras(bundle);
        startActivityForResult(intent, ALARM_SETTING_REQUEST_CODE);
    }

    private void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void startRouteRatingDialog() {
        new AlertDialog.Builder(this)
                .setView(R.layout.rating_dialog)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Send", null)
                .setOnDismissListener(this).show();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

        SharedPreferences sharedPreferences = getSharedPreferences(RouteListActivity.ROUTE_RECORD_SHARED_PREFERENCE, MODE_PRIVATE);
        String bikeRouteJsonString = sharedPreferences.getString(RouteListActivity.ROUTE_RECORD_LIST, "");
        List<BikeRoute> bikeRouteList = new Gson().fromJson(bikeRouteJsonString, new TypeToken<LinkedList<BikeRoute>>() {}.getType());
        List<BikeRoute> newBikeRouteList = new LinkedList<>();
        for (BikeRoute bikeRoute : bikeRouteList) {
            if(bikeRoute.getAlarmTime() != this.bikeRoute.getAlarmTime()) {
                newBikeRouteList.add(bikeRoute);
            }
        }
        sharedPreferences.edit().putString(RouteListActivity.ROUTE_RECORD_LIST, new Gson().toJson(newBikeRouteList)).apply();

        startHomeActivity();
    }
}