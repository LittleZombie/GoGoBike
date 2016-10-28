package gogobike.egg.com.gogobike;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import gogobike.egg.com.entity.BikeRoute;
import gogobike.egg.com.util.GeofenceUtils;
import gogobike.egg.com.util.PermissionUtils;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, DialogInterface.OnDismissListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String SERIALIZABLE_BIKE_ROUTE_DATA = "SERIALIZABLE_BIKE_ROUTE_DATA";
    public static final String INTENT_MAP_MODE = "INTENT_MAP_MODE";
    public static final String USER_NAME = "USER_NAME";
    private static final String speakingMark = ":";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int ALARM_SETTING_REQUEST_CODE = 111;

    private static String urlRequest = "https://maps.googleapis.com/maps/api/directions/xml?";
    private static String SERVER_KEY;

    private BroadcastReceiver broadcastReceiver;
    private BikeRoute bikeRoute;
    private ViewHolder viewHolder;
    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private Marker currentMarker;
    private LatLng currentLatLng;

    public static String userName;
    private int mode;
    private boolean mShowPermissionDeniedDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        getExtras();

        viewHolder = new ViewHolder();
        initActionbar();
        initUserName();
        initBroadcastReceiver();
        initGoogleApiClient();
        initGoogleMapApiKey();
        initMapFragment();
        layoutRoute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
        registerReceiver(broadcastReceiver, new IntentFilter(GeofenceTransitionsIntentService.BROADCAST_ACTION_GEOFENCE_TRIGGER));
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
            bikeRoutes = new Gson().fromJson(bikeRouteJsonString, new TypeToken<LinkedList<BikeRoute>>() {
            }.getType());

        }
        bikeRoute.setAlarmTime(data.getLongExtra(AlarmSettingActivity.INTENT_LONG_CALENDAR_MILLIS, 0));
        bikeRoutes.add(bikeRoute);
        sharedPreferences.edit().putString(RouteListActivity.ROUTE_RECORD_LIST, new Gson().toJson(bikeRoutes)).apply();
        startHomeActivity();
    }

    private void getExtras() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mode = intent.getIntExtra(INTENT_MAP_MODE, 0);

        Bundle bundle = intent.getExtras();
        if (bundle == null) {
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

    private void initUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences(RouteListActivity.ROUTE_RECORD_SHARED_PREFERENCE, MODE_PRIVATE);
        userName = sharedPreferences.getString(USER_NAME, "");
    }

    private void initBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String jsonString = intent.getStringExtra(GeofenceTransitionsIntentService.INTENT_GEOFENCE_ID_LIST);
                List<String> ids = new Gson().fromJson(jsonString, new TypeToken<LinkedList<String>>() {}.getType());
                for (String id : ids) {
                    new AlertDialog.Builder(context)
                            .setMessage(id)
                            .setNegativeButton("No", null)
                            .show();
                }

            }
        };
    }

    private void initGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        viewHolder.messageEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (userName.isEmpty()) {
                    showTypeUserNameDialog(v.getContext());
                }
                if (hasFocus) {
                    viewHolder.messageLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.messageLinearLayout.setVisibility(View.GONE);
                }
            }
        });
        viewHolder.messageEditText.setEnabled(true);
        viewHolder.sendMessageButton.setEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
            return;
        }
        LocationRequest request = new LocationRequest();
        request.setInterval(5000);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                request,  this);
    }

    private void showTypeUserNameDialog(Context context) {
        final EditText nameEditText = new EditText(context);
        new AlertDialog.Builder(context)
                .setMessage("You haven't set your user name yet. Please type your user name.")
                .setView(nameEditText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = String.valueOf(nameEditText.getText());
                        if (name.isEmpty()) {
                            return;
                        }
                        userName = name;
                        SharedPreferences sharedPreferences = getSharedPreferences(RouteListActivity.ROUTE_RECORD_SHARED_PREFERENCE, MODE_PRIVATE);
                        sharedPreferences.edit().putString(USER_NAME, userName).apply();
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (currentMarker != null) {
            currentMarker.remove();
        }
        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        currentMarker = map.addMarker(new MarkerOptions().position(currentLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.user_bicycle)));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17));
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

        viewHolder.routeNameTextView.setText(bikeRoute.getRouteName());
        viewHolder.routeLengthTextView.setText("(" + bikeRoute.getKilometer() + "KM)");
        viewHolder.pokemonLinearLayout.setBackgroundResource(bikeRoute.getImageResourceId());
        if (bikeRoute.getPokemonImageResourceId() == null) {
            return;
        }

        for (int imageId : bikeRoute.getPokemonImageResourceId()) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(imageId);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(16, 0, 0, 16);
            imageView.setLayoutParams(layoutParams);
            viewHolder.pokemonLinearLayout.addView(imageView);
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
        for (int i = 0; i < routeSize; i++) {
            LatLng latLng = new LatLng(latitudeList.get(i), longitudeList.get(i));
            latLngs.add(latLng);
        }
        addPolylineTo(latLngs);

        //add poke stop
        MarkerOptions pokeMarker = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.pokestop));
        map.addMarker(pokeMarker.position(new LatLng(latitudeList.get(5), longitudeList.get(5))));
        map.addMarker(pokeMarker.position(new LatLng(latitudeList.get(15), longitudeList.get(15))));
        map.addMarker(pokeMarker.position(new LatLng(latitudeList.get(20), longitudeList.get(20))));

        LatLng centerPoint = new LatLng(latitudeList.get(routeSize / 2), longitudeList.get(routeSize / 2));
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
        } else if (mode == 2) {
            viewHolder.pokemonLinearLayout.setVisibility(View.GONE);
            viewHolder.routeNameTextView.setVisibility(View.GONE);
            viewHolder.routeLengthTextView.setVisibility(View.GONE);
            viewHolder.blueBikeImageButton.setVisibility(View.GONE);
            viewHolder.racingEndImageButton.setVisibility(View.VISIBLE);
            viewHolder.messageEditText.setVisibility(View.VISIBLE);
            viewHolder.sendMessageButton.setVisibility(View.VISIBLE);
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
        }
    }

    public void onRacingEndImageButtonClick(View view) {
        new AlertDialog.Builder(this).setMessage(R.string.end_riding_dialog_message)
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRouteRatingDialog();
                    }
                }).show();
    }

    public void onSendMessageImageButtonClick(View view) {
        String message = String.valueOf(viewHolder.messageEditText.getText());
        if (message.isEmpty()) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
            return;
        }
        viewHolder.messageEditText.setText("");
        LocationServices.GeofencingApi.addGeofences(
                googleApiClient,
                GeofenceUtils.getGeofencingRequest(GeofenceUtils.getGeofence(userName + speakingMark + message, currentLatLng)),
        GeofenceUtils.getGeofencePendingIntent(this));
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
        List<BikeRoute> bikeRouteList = new Gson().fromJson(bikeRouteJsonString, new TypeToken<LinkedList<BikeRoute>>() {

        }.getType());
        List<BikeRoute> newBikeRouteList = new LinkedList<>();
        for (BikeRoute bikeRoute : bikeRouteList) {
            if (bikeRoute.getAlarmTime() != this.bikeRoute.getAlarmTime()) {
                newBikeRouteList.add(bikeRoute);
            }
        }
        sharedPreferences.edit().putString(RouteListActivity.ROUTE_RECORD_LIST, new Gson().toJson(newBikeRouteList)).apply();

        startHomeActivity();
    }

    private class ViewHolder {
        private LinearLayout pokemonLinearLayout;
        private LinearLayout messageLinearLayout;
        private ImageButton racingEndImageButton;
        private ImageButton blueBikeImageButton;
        private TextView routeNameTextView;
        private TextView routeLengthTextView;
        private EditText messageEditText;
        private ImageButton sendMessageButton;

        ViewHolder() {
            pokemonLinearLayout = (LinearLayout) findViewById(R.id.mapActivity_pokemonLinearLayout);
            messageLinearLayout = (LinearLayout) findViewById(R.id.mapActivity_messageLinearLayout);
            racingEndImageButton = (ImageButton) findViewById(R.id.mapActivity_racingEndImageButton);
            blueBikeImageButton = (ImageButton) findViewById(R.id.mapActivity_blueBikeImageButton);
            routeNameTextView = (TextView) findViewById(R.id.mapActivity_routeNameTextView);
            routeLengthTextView = (TextView) findViewById(R.id.mapActivity_routeLengthTextView);
            messageEditText = (EditText) findViewById(R.id.mapActivity_messageEditText);
            sendMessageButton = (ImageButton) findViewById(R.id.mapActivity_sendMessageImageButton);
        }
    }
}