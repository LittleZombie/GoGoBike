package gogobike.egg.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import gogobike.egg.com.entity.BikeRoute;
import gogobike.egg.com.gogobike.R;
import gogobike.egg.com.gogobike.RecommendedRouteActivity;

/**
 * Created by user on 2016/9/28.
 */

public class RouteListAdapter extends BaseAdapter {

    private List<BikeRoute> bikeRouteList;
    private Context context;

    public RouteListAdapter(List<BikeRoute> bikeRouteList, Context context, int weight, int area, int mode) {
        this.context = context;
        if (mode == 2) {
            this.bikeRouteList = bikeRouteList;
            return;
        }

        this.bikeRouteList = new ArrayList<>();
        for (BikeRoute bikeRoute : bikeRouteList) {
            RecommendedRouteActivity.Area routeArea = bikeRoute.getArea();
            if (bikeRoute.getWeight() <= weight && routeArea != null && (area == RecommendedRouteActivity.Area.All.ordinal() || routeArea.ordinal() == area)) {
                this.bikeRouteList.add(bikeRoute);
            }
        }
    }

    @Override
    public int getCount() {
        return bikeRouteList == null ? 0 : bikeRouteList.size();
    }

    @Override
    public BikeRoute getItem(int i) {
        return bikeRouteList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.route_list_item, null);
            viewHolder = new ViewHolder();

            viewHolder.imageView = (ImageView) view.findViewById(R.id.routeListItem_imageView);
            viewHolder.ratingBar = (RatingBar) view.findViewById(R.id.routeListItem_ratingBar);

            viewHolder.alarmTimeTextView = (TextView) view.findViewById(R.id.routeListItem_alarmTimeTextView);
            viewHolder.routeNameTextView = (TextView) view.findViewById(R.id.routeListItem_routeNameTextView);
            viewHolder.pokemonTextView = (TextView) view.findViewById(R.id.routeListItem_pokemonStationTextView);
            viewHolder.kmTextView = (TextView) view.findViewById(R.id.routeListItem_kmTextView);
            viewHolder.timeTextView = (TextView) view.findViewById(R.id.routeListItem_timeTextView);
            viewHolder.rentalTextView = (TextView) view.findViewById(R.id.routeListItem_rentalTextView);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BikeRoute bikeRoute = getItem(i);

        viewHolder.imageView.setImageResource(bikeRoute.getImageResourceId());
        viewHolder.ratingBar.setRating(bikeRoute.getRatingScore());
        long timeInMillis = bikeRoute.getAlarmTime();
        if (timeInMillis > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeInMillis);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy a hh:mm", Locale.US);
            String time = sdf.format(calendar.getTime());
            viewHolder.alarmTimeTextView.setText(time);
            viewHolder.alarmTimeTextView.setVisibility(View.VISIBLE);
        }
        viewHolder.routeNameTextView.setText(bikeRoute.getRouteName());
        viewHolder.pokemonTextView.setText("Pokemon Station : " + bikeRoute.getPokemonStationNumber());
        viewHolder.kmTextView.setText("Kilometer : " + bikeRoute.getKilometer());
        viewHolder.timeTextView.setText("Time : " + bikeRoute.getTime() + " minute");

        String yesOrNo = bikeRoute.isHasBikeRentalStation() ? "YES" : "No";
        viewHolder.rentalTextView.setText("Bike Rental Station : " + yesOrNo);

        return view;
    }

    public class ViewHolder {

        ImageView imageView;
        RatingBar ratingBar;

        TextView alarmTimeTextView;
        TextView routeNameTextView;
        TextView pokemonTextView;
        TextView kmTextView;
        TextView timeTextView;
        TextView rentalTextView;

    }
}
