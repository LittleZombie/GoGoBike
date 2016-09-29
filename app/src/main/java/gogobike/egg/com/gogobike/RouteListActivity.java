package gogobike.egg.com.gogobike;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import gogobike.egg.com.adapter.RouteListAdapter;
import gogobike.egg.com.entity.BikeRoute;
import gogobike.egg.com.route.TamsuiRoutes;

public class RouteListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private List<BikeRoute> bikeRouteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_list_activity);

        generateRouteList();
        layoutListView();
    }

    private void generateRouteList() {
        BikeRoute bikeRoute1 = new BikeRoute();
        bikeRoute1.setRouteName("北投貴子坑");
        bikeRoute1.setOrigin("北投田心仔");
        bikeRoute1.setDestination("貴子坑");
        bikeRoute1.setPokemonStationNumber(15);
        bikeRoute1.setKilometer(6.4f);
        bikeRoute1.setTime(90);
        bikeRoute1.setHasBikeRentalStation(true);

        BikeRoute bikeRoute2 = new BikeRoute();
        bikeRoute2.setRouteName("淡水紅樹林");
        bikeRoute2.setOrigin("淡水");
        bikeRoute2.setDestination("紅樹林");
        bikeRoute2.setPokemonStationNumber(8);
        bikeRoute2.setKilometer(4.3f);
        bikeRoute2.setTime(60);
        bikeRoute2.setHasBikeRentalStation(true);
        bikeRoute2.setLatitudeList(TamsuiRoutes.generateLatitude());
        bikeRoute2.setLongitudeList(TamsuiRoutes.generateLongitude());

        bikeRouteList.add(bikeRoute1);
        bikeRouteList.add(bikeRoute2);
    }

    private void layoutListView() {
        RouteListAdapter adapter = new RouteListAdapter(bikeRouteList, this);
        ListView listView = (ListView) findViewById(R.id.routeListActivity_listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
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
