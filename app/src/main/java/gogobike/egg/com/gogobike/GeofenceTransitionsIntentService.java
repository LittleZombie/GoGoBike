package gogobike.egg.com.gogobike;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.List;

public class GeofenceTransitionsIntentService extends IntentService {
    public static final String BROADCAST_ACTION_GEOFENCE_TRIGGER = "BROADCAST_ACTION_GEOFENCE_TRIGGER";
    public static final String INTENT_GEOFENCE_ID_LIST = "INTENT_GEOFENCE_ID_LIST";


    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GeofenceTransitionsIntentService(String name) {
        super(name);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GooglePlayServicesUtil.getErrorString(geofencingEvent.getErrorCode());
            Log.e("GeofencingEvent", errorMessage);
            return;
        }

        List<Geofence> geofenceList =  geofencingEvent.getTriggeringGeofences();
        List<String> idList = new LinkedList<>();
        for (Geofence geofence : geofenceList) {
            Log.e("Geofence", geofence.getRequestId());
            String id = geofence.getRequestId();
            String userName = id.substring(0, MapActivity.userName.length());
            if (!userName.equals(MapActivity.userName)) {
                idList.add(id);
            }
        }
        Intent broadcastIntent = new Intent(BROADCAST_ACTION_GEOFENCE_TRIGGER);
        broadcastIntent.putExtra(INTENT_GEOFENCE_ID_LIST, new Gson().toJson(idList));
        sendBroadcast(broadcastIntent);
    }
}
