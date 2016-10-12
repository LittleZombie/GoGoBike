package gogobike.egg.com.gogobike;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmSetting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setting);

        initActionBar();
        layoutDate();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_activity_alarm_setting);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void layoutDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("mm dd, yyyy");
        ((TextView) findViewById(R.id.alarmSetting_dateTextView)).setText(sdf.format(new Date()));
    }

    public void onDateTextViewClick(View view) {

    }

    public void onTimeTextViewClick(View view) {
    }
}
