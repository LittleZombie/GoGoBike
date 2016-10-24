package gogobike.egg.com.gogobike;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class AlarmSettingActivity extends AppCompatActivity {
    public static final String INTENT_LONG_CALENDAR_MILLIS = "INTENT_LONG_CALENDAR_MILLIS";
    public static final int NOTIFICATION_ID = 998171;
    public static final String NOTIFICATION_NAME = "GOGOBIKE";
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private TextView dateTextView;
    private TextView timeTextView;
    private TextView amPmTextView;
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setting);

        initActionBar();
        initView();
        initDialog();
        layoutDate();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_activity_alarm_setting);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initView() {
        dateTextView = (TextView) findViewById(R.id.alarmSetting_dateTextView);
        timeTextView = (TextView) findViewById(R.id.alarmSetting_timeTextView);
        amPmTextView = (TextView) findViewById(R.id.alarmSetting_amPmTextView);
    }

    private void initDialog() {
        final GregorianCalendar calendar = new GregorianCalendar();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateTextView.setText(dayOfMonth + " " + (month + 1) + ", " + year);
                calendar.set(year, month, dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                String hour, minutes, amPm;
                if (hourOfDay < 12) {
                    amPm = "AM";
                } else {
                    hourOfDay -= 12;
                    amPm = "PM";
                }

                if (hourOfDay == 0) {
                    hour = "12";
                } else if (hourOfDay < 10) {
                    hour = "0" + hourOfDay;
                } else {
                    hour = "" + hourOfDay;
                }

                minutes = minute < 10 ? "0" + minute : "" + minute;
                timeTextView.setText(hour + ":" + minutes);
                amPmTextView.setText(amPm);
                Log.e(getClass().getSimpleName(), ""+calendar.getTime());
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
    }

    private void layoutDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM dd, yyyy|hh:mm a", Locale.ENGLISH);
        String date = sdf.format(new Date());
        dateTextView.setText(date.substring(0, 11));
        timeTextView.setText(date.substring(12, 17));
        amPmTextView.setText(date.substring(18));
        calendar.setTime(new Date());
//        calendar.set(date.substring(7, 11), date.substring(0, 2), date.substring(3, 5), date.substring(12, 14), date.substring(15, 17));
    }

    public void onDateTextViewClick(View view) {
        datePickerDialog.show();
    }

    public void onTimeTextViewClick(View view) {
        timePickerDialog.show();
    }

    public void onTimeSettingImageButtonClick(View view) {
        long timeInMillis = calendar.getTimeInMillis();
        Intent intent = new Intent();
        intent.setClass(this, RouteListActivity.class);
        intent.putExtra(INTENT_LONG_CALENDAR_MILLIS, timeInMillis);
        intent.putExtra(RouteListActivity.INTENT_INT_ROUTE_LIST_MODE, 2);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification  = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setWhen(timeInMillis)
                .setContentTitle("GoGoBike")
                .setContentText("即將到達預定騎乘時間")
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND).build();
        manager.notify(NOTIFICATION_NAME, NOTIFICATION_ID, notification);

        setResult(RESULT_OK, new Intent().putExtra(INTENT_LONG_CALENDAR_MILLIS, timeInMillis));
        finish();
    }
}
