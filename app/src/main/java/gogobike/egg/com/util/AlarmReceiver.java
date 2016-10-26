package gogobike.egg.com.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import gogobike.egg.com.gogobike.AlarmSettingActivity;
import gogobike.egg.com.gogobike.MapActivity;
import gogobike.egg.com.gogobike.R;
import gogobike.egg.com.gogobike.RouteListActivity;

import static android.content.Context.NOTIFICATION_SERVICE;
import static gogobike.egg.com.gogobike.AlarmSettingActivity.NOTIFICATION_ID;
import static gogobike.egg.com.gogobike.AlarmSettingActivity.NOTIFICATION_NAME;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        long timeInMillis = intent.getLongExtra(AlarmSettingActivity.INTENT_LONG_CALENDAR_MILLIS, 0);
        Intent mapIntent = new Intent(context, MapActivity.class);
        mapIntent.putExtras(intent.getExtras());
        mapIntent.putExtra(RouteListActivity.INTENT_INT_ROUTE_LIST_MODE, 2);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mapIntent, 0);
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification  = builder.setSmallIcon(R.drawable.logo)
                .setWhen(timeInMillis)
                .setContentTitle("GoGoBike")
                .setContentText("即將到達預定騎乘時間")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND).build();
        manager.notify(NOTIFICATION_NAME, NOTIFICATION_ID, notification);
    }
}
