package gogobike.egg.com.gogobike;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void onRecommendedRouteImageViewClick(View view) {
        Intent intent = new Intent(this, RecommendedRouteActivity.class);
        startActivity(intent);
    }

    public void onRouteRecordImageViewClick(View view) {
        Intent intent = new Intent(this, RouteListActivity.class);
        intent.putExtra(RouteListActivity.INTENT_INT_ROUTE_LIST_MODE, 2);
        startActivity(intent);
    }
}
