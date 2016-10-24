package gogobike.egg.com.gogobike;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

public class RecommendedRouteActivity extends AppCompatActivity {

    private enum ChallengeLevel {Relaxed, General, Challenge}
    public enum Area {All, Taipei, Taichung, Kaohsiung}
    private int selectLevel = 0;
    private final String BODY_DATA_SHARED_PREFERENCE = "BODY_DATA_SHARED_PREFERENCE";
    private final String ISBOY = "ISBOY";
    private final String HEIGHT = "HEIGHT";
    private final String WEIGHT = "WEIGHT";
    private final String AREA = "AREA";
    private final String BMI = "BMI";
    private final String ISRECORDPROFILE = "ISRECORDPROFILE";
    private RecommendRouteViewHolder viewHolder;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(BODY_DATA_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        if (!sharedPreferences.getBoolean(ISRECORDPROFILE, false)) {
            setContentView(R.layout.activity_recommended_route);
            viewHolder = new RecommendRouteViewHolder();
            layoutPersonalProfile();
        } else {
            setContentView(R.layout.activity_recommended_route_no_type_area);
        }
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.recommendedRouteActivity_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_activity_recommended_route);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private void layoutPersonalProfile() {
        if (sharedPreferences.getBoolean(ISRECORDPROFILE, false)) {
            if (sharedPreferences.getBoolean(ISBOY, true)) {
                viewHolder.boyRadioButton.setChecked(true);
            } else {
                viewHolder.girlRadioButton.setChecked(true);
            }

            viewHolder.heightEditText.setText("" + sharedPreferences.getFloat(HEIGHT, 0));
            viewHolder.weightEditText.setText("" + sharedPreferences.getFloat(WEIGHT, 0));
            viewHolder.areaSpinner.setSelection(sharedPreferences.getInt(AREA, 0));
        }
    }

    public void onRelaxedButtonClick(View view) {
        selectLevel = ChallengeLevel.Relaxed.ordinal() + 1;
        viewHolder.relaxedImageView.setImageResource(R.drawable.bike_blue);
        viewHolder.generalImageView.setImageResource(R.drawable.general_button);
        viewHolder.challengeImageView.setImageResource(R.drawable.challenge_button);
    }

    public void onGeneralButtonClick(View view) {
        selectLevel = ChallengeLevel.General.ordinal() + 1;
        viewHolder.relaxedImageView.setImageResource(R.drawable.relaxed_button);
        viewHolder.generalImageView.setImageResource(R.drawable.bike_blue);
        viewHolder.challengeImageView.setImageResource(R.drawable.challenge_button);
    }

    public void onChallengeButtonClick(View view) {
        selectLevel = ChallengeLevel.Challenge.ordinal() + 1;
        viewHolder.relaxedImageView.setImageResource(R.drawable.relaxed_button);
        viewHolder.generalImageView.setImageResource(R.drawable.general_button);
        viewHolder.challengeImageView.setImageResource(R.drawable.bike_blue);
    }

    public void onEditButtonClick(View view) {
        setContentView(R.layout.activity_recommended_route);
        viewHolder = new RecommendRouteViewHolder();
        layoutPersonalProfile();
        initToolbar();
    }

    public void onRelaxedImageViewClick(View view) {
        startRouteListActivity(sharedPreferences.getFloat(BMI, 0),
                ChallengeLevel.Relaxed.ordinal() + 1, sharedPreferences.getInt(AREA, 0));
    }

    public void onGeneralImageViewClick(View view) {
        startRouteListActivity(sharedPreferences.getFloat(BMI, 0),
                ChallengeLevel.General.ordinal() + 1, sharedPreferences.getInt(AREA, 0));
    }

    public void onChallengeImageViewClick(View view) {
        startRouteListActivity(sharedPreferences.getFloat(BMI, 0),
                ChallengeLevel.Challenge.ordinal() + 1, sharedPreferences.getInt(AREA, 0));
    }

    public void onChangeFloatingActionButtonClick(View view) {
        setContentView(R.layout.activity_recommended_route_no_type_area);
        initToolbar();
    }

    public void onRecommendImageViewClick(View view) {
        if (checkError()) {
            return;
        }

        boolean isBoy = viewHolder.boyRadioButton.isChecked();
        float height = Float.valueOf(viewHolder.heightEditText.getText().toString());
        float weight = Float.valueOf(viewHolder.weightEditText.getText().toString());
        int selectedArea = viewHolder.areaSpinner.getSelectedItemPosition();
        double BMIValue = weight / Math.pow(height / 100, 2);
        sharedPreferences.edit()
                .putBoolean(ISBOY, isBoy)
                .putFloat(HEIGHT, height)
                .putFloat(WEIGHT, weight)
                .putInt(AREA, selectedArea)
                .putFloat(BMI, (float) BMIValue)
                .putBoolean(ISRECORDPROFILE, true)
                .apply();
        startRouteListActivity(BMIValue,  selectLevel, selectedArea);
    }

    private boolean checkError() {
        boolean isError = false;
        if (!viewHolder.boyRadioButton.isChecked() && !viewHolder.girlRadioButton.isChecked()) {
            viewHolder.genderErrorTextView.setVisibility(View.VISIBLE);
            isError = true;
        } else {
            viewHolder.genderErrorTextView.setVisibility(View.GONE);
        }

        if (viewHolder.heightEditText.getText().toString().isEmpty()) {
            viewHolder.heightErrorTextView.setVisibility(View.VISIBLE);
            isError = true;
        } else {
            viewHolder.heightErrorTextView.setVisibility(View.GONE);
        }

        if (viewHolder.weightEditText.getText().toString().isEmpty()) {
            viewHolder.weightErrorTextView.setVisibility(View.VISIBLE);
            isError = true;
        } else {
            viewHolder.weightErrorTextView.setVisibility(View.GONE);
        }

        if (selectLevel == 0) {
            viewHolder.typeErrorTextView.setVisibility(View.VISIBLE);
            isError = true;
        } else {
            viewHolder.typeErrorTextView.setVisibility(View.INVISIBLE);
        }

        return isError;
    }

    private void startRouteListActivity(double BMIValue, int selectLevel, int selectedArea) {
        int routeWeight = getRouteWeight(BMIValue, selectLevel);
        Log.i(getClass().getSimpleName(), "Weight:" + routeWeight);
        Intent intent = new Intent(this, RouteListActivity.class);
        intent.putExtra(RouteListActivity.INTENT_INT_ROUTE_LIST_MODE, 1);
        intent.putExtra(RouteListActivity.INTENT_INT_ROUTE_WEIGHT, routeWeight);
        intent.putExtra(RouteListActivity.INTENT_INT_AREA, selectedArea);
        startActivity(intent);
    }

    private int getRouteWeight(double BMIValue, int selectLevel) {
        if (BMIValue < 18.5) {
            return 2 * selectLevel;
        } else if (BMIValue < 24) {
            return 3 * selectLevel;
        } else {
            return selectLevel;
        }
    }

    private class RecommendRouteViewHolder {
        RadioButton boyRadioButton;
        RadioButton girlRadioButton;
        EditText heightEditText;
        EditText weightEditText;
        Spinner areaSpinner;
        ImageView relaxedImageView;
        ImageView generalImageView;
        ImageView challengeImageView;
        TextView genderErrorTextView;
        TextView heightErrorTextView;
        TextView weightErrorTextView;
        TextView typeErrorTextView;

        public RecommendRouteViewHolder() {
            boyRadioButton = (RadioButton) findViewById(R.id.recommendedRouteActivity_boyRadioButton);
            girlRadioButton = (RadioButton) findViewById(R.id.recommendedRouteActivity_girlRadioButton);
            heightEditText = (EditText) findViewById(R.id.recommendedRouteActivity_heightEditText);
            weightEditText = (EditText) findViewById(R.id.recommendedRouteActivity_weightEditText);
            areaSpinner = (Spinner) findViewById(R.id.recommendedRouteActivity_areaSpinner);
            relaxedImageView = (ImageView) findViewById(R.id.recommendedRouteActivity_relaxedImageView);
            generalImageView = (ImageView) findViewById(R.id.recommendedRouteActivity_generalImageView);
            challengeImageView = (ImageView) findViewById(R.id.recommendedRouteActivity_challengeImageView);
            genderErrorTextView = (TextView) findViewById(R.id.recommendedRouteActivity_genderErrorTextView);
            heightErrorTextView = (TextView) findViewById(R.id.recommendedRouteActivity_heightErrorTextView);
            weightErrorTextView = (TextView) findViewById(R.id.recommendedRouteActivity_weightErrorTextView);
            typeErrorTextView = (TextView) findViewById(R.id.recommendedRouteActivity_typeErrorTextView);
        }
    }
}
