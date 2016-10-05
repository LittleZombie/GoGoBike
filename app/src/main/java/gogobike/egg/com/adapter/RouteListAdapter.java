package gogobike.egg.com.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import gogobike.egg.com.entity.BikeRoute;
import gogobike.egg.com.gogobike.R;

/**
 * Created by user on 2016/9/28.
 */

public class RouteListAdapter extends BaseAdapter {

    private List<BikeRoute> bikeRouteList;
    private Context context;

    public RouteListAdapter(List<BikeRoute> bikeRouteList, Context context) {
        this.bikeRouteList = bikeRouteList;
        this.context = context;
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

        TextView routeNameTextView;
        TextView pokemonTextView;
        TextView kmTextView;
        TextView timeTextView;
        TextView rentalTextView;

    }
}
