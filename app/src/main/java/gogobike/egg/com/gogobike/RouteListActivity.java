package gogobike.egg.com.gogobike;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import gogobike.egg.com.adapter.RouteListAdapter;
import gogobike.egg.com.entity.BikeRoute;
import gogobike.egg.com.route.TamsuiRoutes;

public class RouteListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final String INTENT_INT_ROUTE_WEIGHT = "INTENT_INT_ROUTE_WEIGHT";
    public static final String INTENT_INT_AREA = "INTENT_INT_AREA";
    private List<BikeRoute> bikeRouteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_list_activity);

        layoutActionBar();
        generateRouteList();
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

    private void layoutActionBar() {
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setTitle("Recommend Route");
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void generateRouteList() {
        BikeRoute bikeRoute1 = new BikeRoute();
        bikeRoute1.setRouteName("北投-貴子坑");
        bikeRoute1.setOrigin("北投田心仔");
        bikeRoute1.setDestination("貴子坑");
        bikeRoute1.setPokemonStationNumber(15);
        bikeRoute1.setKilometer(6.4f);
        bikeRoute1.setTime(90);
        bikeRoute1.setRatingScore(2.5f);
        bikeRoute1.setImageResourceId(R.drawable.tamsui_route_2);
        bikeRoute1.setHasBikeRentalStation(true);
        bikeRoute1.setLatitudeList(TamsuiRoutes.generateLatitude());
        bikeRoute1.setLongitudeList(TamsuiRoutes.generateLongitude());
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
        bikeRoute2.setImageResourceId(R.drawable.tamsui_route_1);
        bikeRoute2.setHasBikeRentalStation(true);
        bikeRoute2.setLatitudeList(TamsuiRoutes.generateLatitude());
        bikeRoute2.setLongitudeList(TamsuiRoutes.generateLongitude());
        bikeRoute2.setWeight(3);
        bikeRoute2.setArea(RecommendedRouteActivity.Area.Taipei);
        bikeRoute2.setPokemonImageResourceId(new int[]{R.drawable.pokemon_1, R.drawable.pokemon_2, R.drawable.pokemon_3});

        BikeRoute bikeRoute3 = new BikeRoute();
        bikeRoute3.setRouteName("竹圍-關渡");
        bikeRoute3.setOrigin("竹圍");
        bikeRoute3.setDestination("關渡");
        bikeRoute3.setPokemonStationNumber(12);
        bikeRoute3.setKilometer(5.6f);
        bikeRoute3.setTime(40);
        bikeRoute3.setRatingScore(5);
        bikeRoute3.setImageResourceId(R.drawable.tamsui_route_3);
        bikeRoute3.setHasBikeRentalStation(true);
        bikeRoute3.setLatitudeList(TamsuiRoutes.generateLatitude());
        bikeRoute3.setLongitudeList(TamsuiRoutes.generateLongitude());
        bikeRoute3.setWeight(5);
        bikeRoute3.setArea(RecommendedRouteActivity.Area.Taipei);
        bikeRoute3.setPokemonImageResourceId(new int[]{R.drawable.pokemon_1, R.drawable.pokemon_2, R.drawable.pokemon_3, R.drawable.pokemon_4, R.drawable.pokemon_5, R.drawable.pokemon_6});

        BikeRoute bikeRoute4 = new BikeRoute();
        bikeRoute4.setRouteName("北投-貴子坑");
        bikeRoute4.setOrigin("北投田心仔");
        bikeRoute4.setDestination("貴子坑");
        bikeRoute4.setPokemonStationNumber(15);
        bikeRoute4.setKilometer(6.4f);
        bikeRoute4.setTime(90);
        bikeRoute4.setRatingScore(2.5f);
        bikeRoute4.setImageResourceId(R.drawable.tamsui_route_2);
        bikeRoute4.setHasBikeRentalStation(true);
        bikeRoute4.setLatitudeList(TamsuiRoutes.generateLatitude());
        bikeRoute4.setLongitudeList(TamsuiRoutes.generateLongitude());
        bikeRoute4.setWeight(7);
        bikeRoute4.setArea(RecommendedRouteActivity.Area.Taipei);

        BikeRoute bikeRoute5 = new BikeRoute();
        bikeRoute5.setRouteName("淡水-紅樹林");
        bikeRoute5.setOrigin("淡水");
        bikeRoute5.setDestination("紅樹林");
        bikeRoute5.setPokemonStationNumber(8);
        bikeRoute5.setKilometer(4.3f);
        bikeRoute5.setTime(60);
        bikeRoute5.setRatingScore(4);
        bikeRoute5.setImageResourceId(R.drawable.tamsui_route_1);
        bikeRoute5.setHasBikeRentalStation(true);
        bikeRoute5.setLatitudeList(TamsuiRoutes.generateLatitude());
        bikeRoute5.setLongitudeList(TamsuiRoutes.generateLongitude());
        bikeRoute5.setWeight(3);
        bikeRoute5.setArea(RecommendedRouteActivity.Area.Taipei);

        BikeRoute bikeRoute6 = new BikeRoute();
        bikeRoute6.setRouteName("竹圍-關渡");
        bikeRoute6.setOrigin("竹圍");
        bikeRoute6.setDestination("關渡");
        bikeRoute6.setPokemonStationNumber(12);
        bikeRoute6.setKilometer(5.6f);
        bikeRoute6.setTime(40);
        bikeRoute6.setRatingScore(5);
        bikeRoute6.setImageResourceId(R.drawable.tamsui_route_3);
        bikeRoute6.setHasBikeRentalStation(true);
        bikeRoute6.setLatitudeList(TamsuiRoutes.generateLatitude());
        bikeRoute6.setLongitudeList(TamsuiRoutes.generateLongitude());
        bikeRoute6.setWeight(5);
        bikeRoute6.setArea(RecommendedRouteActivity.Area.Taipei);

        bikeRouteList.add(bikeRoute1);
        bikeRouteList.add(bikeRoute2);
        bikeRouteList.add(bikeRoute3);
        bikeRouteList.add(bikeRoute4);
        bikeRouteList.add(bikeRoute5);
        bikeRouteList.add(bikeRoute6);
    }

    private void layoutListView() {
        RouteListAdapter adapter = new RouteListAdapter(bikeRouteList, this, getWeight(), getArea());
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
        startActivity(intent);
    }

}
