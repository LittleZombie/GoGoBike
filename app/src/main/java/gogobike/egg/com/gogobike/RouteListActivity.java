package gogobike.egg.com.gogobike;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import gogobike.egg.com.adapter.RouteListAdapter;
import gogobike.egg.com.entity.BikeRoute;
import gogobike.egg.com.route.BaliRoutes;
import gogobike.egg.com.route.CijinRoutes;
import gogobike.egg.com.route.HouliRoutes;
import gogobike.egg.com.route.KaohsiungRoutes;
import gogobike.egg.com.route.TamsuiRoutes;
import gogobike.egg.com.route.TouBianRoutes;

public class RouteListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final String ROUTE_RECORD_SHARED_PREFERENCE = "ROUTE_RECORD_SHARED_PREFERENCE";
    public static final String ROUTE_RECORD_LIST = "ROUTE_RECORD_LIST";
    public static final String INTENT_INT_ROUTE_LIST_MODE = "INTENT_INT_ROUTE_LIST_MODE";
    public static final String INTENT_INT_ROUTE_WEIGHT = "INTENT_INT_ROUTE_WEIGHT";
    public static final String INTENT_INT_AREA = "INTENT_INT_AREA";
    private List<BikeRoute> bikeRouteList = new ArrayList<>();
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_list_activity);

        mode = getExtra();
        layoutActionBar();
        if (mode < 2) {
            generateRouteList();
        } else {
            getRouteListFromSharedPreference();
        }
        layoutListView();
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

    public int getExtra() {
        Intent intent = getIntent();
        if(intent == null){
            return 0;
        }

        return intent.getIntExtra(INTENT_INT_ROUTE_LIST_MODE, 0);
    }

    private void layoutActionBar() {
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        if(ab != null) {
            if (mode == 2) {
                ab.setTitle("Route Record");
            } else {
                ab.setTitle("Recommend Route");
            }
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void generateRouteList() {
        BikeRoute bikeRoute1 = new BikeRoute();
        bikeRoute1.setRouteName("八里-媽媽嘴");
        bikeRoute1.setOrigin("八里渡船頭");
        bikeRoute1.setDestination("媽媽嘴咖啡");
        bikeRoute1.setPokemonStationNumber(15);
        bikeRoute1.setKilometer(6.4f);
        bikeRoute1.setTime(90);
        bikeRoute1.setRatingScore(2.5f);
        bikeRoute1.setImageResourceId(R.drawable.bali);
        bikeRoute1.setHasBikeRentalStation(true);
        bikeRoute1.setLatitudeList(BaliRoutes.generateLatitude());
        bikeRoute1.setLongitudeList(BaliRoutes.generateLongitude());
        bikeRoute1.setWeight(7);
        bikeRoute1.setArea(RecommendedRouteActivity.Area.Taipei);
        bikeRoute1.setPokemonImageResourceId(new int[]{R.drawable.pokemon_4, R.drawable.pokemon_5});

        BikeRoute bikeRoute2 = new BikeRoute();
        bikeRoute2.setRouteName("淡水-紅樹林");
        bikeRoute2.setOrigin("淡水");
        bikeRoute2.setDestination("紅樹林");
        bikeRoute2.setPokemonStationNumber(8);
        bikeRoute2.setKilometer(4.3f);
        bikeRoute2.setTime(60);
        bikeRoute2.setRatingScore(4);
        bikeRoute2.setImageResourceId(R.drawable.tamsui);
        bikeRoute2.setHasBikeRentalStation(true);
        bikeRoute2.setLatitudeList(TamsuiRoutes.generateLatitude());
        bikeRoute2.setLongitudeList(TamsuiRoutes.generateLongitude());
        bikeRoute2.setWeight(3);
        bikeRoute2.setArea(RecommendedRouteActivity.Area.Taipei);
        bikeRoute2.setPokemonImageResourceId(new int[]{R.drawable.pokemon_1, R.drawable.pokemon_2, R.drawable.pokemon_3});

        BikeRoute bikeRoute3 = new BikeRoute();
        bikeRoute3.setRouteName("后里馬場-酒莊");
        bikeRoute3.setOrigin("后里馬場");
        bikeRoute3.setDestination("鐵道之鄉酒莊");
        bikeRoute3.setPokemonStationNumber(12);
        bikeRoute3.setKilometer(5.6f);
        bikeRoute3.setTime(40);
        bikeRoute3.setRatingScore(5);
        bikeRoute3.setImageResourceId(R.drawable.houli);
        bikeRoute3.setHasBikeRentalStation(false);
        bikeRoute3.setLatitudeList(HouliRoutes.generateLatitude());
        bikeRoute3.setLongitudeList(HouliRoutes.generateLongitude());
        bikeRoute3.setWeight(5);
        bikeRoute3.setArea(RecommendedRouteActivity.Area.Taichung);
        bikeRoute3.setPokemonImageResourceId(new int[]{R.drawable.pokemon_1, R.drawable.pokemon_2, R.drawable.pokemon_3, R.drawable.pokemon_4, R.drawable.pokemon_5, R.drawable.pokemon_6});

        BikeRoute bikeRoute4 = new BikeRoute();
        bikeRoute4.setRouteName("磨仔墩-蝙蝠洞");
        bikeRoute4.setOrigin("磨仔墩故事島");
        bikeRoute4.setDestination("頭汴蝙蝠洞風景區");
        bikeRoute4.setPokemonStationNumber(15);
        bikeRoute4.setKilometer(6.4f);
        bikeRoute4.setTime(90);
        bikeRoute4.setRatingScore(2.5f);
        bikeRoute4.setImageResourceId(R.drawable.toubain);
        bikeRoute4.setHasBikeRentalStation(false);
        bikeRoute4.setLatitudeList(TouBianRoutes.generateLatitude());
        bikeRoute4.setLongitudeList(TouBianRoutes.generateLongitude());
        bikeRoute4.setWeight(7);
        bikeRoute4.setArea(RecommendedRouteActivity.Area.Taichung);

        BikeRoute bikeRoute5 = new BikeRoute();
        bikeRoute5.setRouteName("旗津渡輪-舊高字塔");
        bikeRoute5.setOrigin("旗津渡輪站");
        bikeRoute5.setDestination("舊高字塔");
        bikeRoute5.setPokemonStationNumber(8);
        bikeRoute5.setKilometer(4.3f);
        bikeRoute5.setTime(60);
        bikeRoute5.setRatingScore(4);
        bikeRoute5.setImageResourceId(R.drawable.cijin);
        bikeRoute5.setHasBikeRentalStation(true);
        bikeRoute5.setLatitudeList(CijinRoutes.generateLatitude());
        bikeRoute5.setLongitudeList(CijinRoutes.generateLongitude());
        bikeRoute5.setWeight(3);
        bikeRoute5.setArea(RecommendedRouteActivity.Area.Kaohsiung);

        BikeRoute bikeRoute6 = new BikeRoute();
        bikeRoute6.setRouteName("愛河之心-光之塔");
        bikeRoute6.setOrigin("愛河之心");
        bikeRoute6.setDestination("光之塔");
        bikeRoute6.setPokemonStationNumber(12);
        bikeRoute6.setKilometer(5.6f);
        bikeRoute6.setTime(40);
        bikeRoute6.setRatingScore(5);
        bikeRoute6.setImageResourceId(R.drawable.kaohsiung);
        bikeRoute6.setHasBikeRentalStation(true);
        bikeRoute6.setLatitudeList(KaohsiungRoutes.generateLatitude());
        bikeRoute6.setLongitudeList(KaohsiungRoutes.generateLongitude());
        bikeRoute6.setWeight(5);
        bikeRoute6.setArea(RecommendedRouteActivity.Area.Kaohsiung);

        bikeRouteList.add(bikeRoute1);
        bikeRouteList.add(bikeRoute2);
        bikeRouteList.add(bikeRoute3);
        bikeRouteList.add(bikeRoute4);
        bikeRouteList.add(bikeRoute5);
        bikeRouteList.add(bikeRoute6);
    }

    public void getRouteListFromSharedPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences(RouteListActivity.ROUTE_RECORD_SHARED_PREFERENCE, MODE_PRIVATE);
        String bikeRouteJsonString = sharedPreferences.getString(RouteListActivity.ROUTE_RECORD_LIST, "");
        bikeRouteList = new Gson().fromJson(bikeRouteJsonString, new TypeToken<LinkedList<BikeRoute>>() {}.getType());
    }

    private void layoutListView() {
        RouteListAdapter adapter = new RouteListAdapter(bikeRouteList, this, getWeight(), getArea(), mode);
        ListView listView = (ListView) findViewById(R.id.routeListActivity_listView);
        if (adapter.getCount() > 0) {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
        } else {
            listView.setVisibility(View.GONE);
            findViewById(R.id.routeListActivity_noRouteLinearLayout).setVisibility(View.VISIBLE);
        }
    }

    private int getWeight() {
        return getIntent().getIntExtra(INTENT_INT_ROUTE_WEIGHT, 1);
    }

    private int getArea() {
        return getIntent().getIntExtra(INTENT_INT_AREA, 0);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        BikeRoute bikeRoute = ((RouteListAdapter) adapterView.getAdapter()).getItem(i);
        startMapActivity(bikeRoute);
    }

    public void startMapActivity(BikeRoute bikeRoute) {
        Intent intent = new Intent(this, MapActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable(MapActivity.SERIALIZABLE_BIKE_ROUTE_DATA, bikeRoute);

        intent.putExtras(bundle);
        intent.putExtra(MapActivity.INTENT_MAP_MODE, mode);
        startActivity(intent);
    }

}
