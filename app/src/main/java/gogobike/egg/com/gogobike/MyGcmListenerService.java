package gogobike.egg.com.gogobike;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Liou on 2016/10/30.
 */

public class MyGcmListenerService extends FirebaseMessagingService {
    private static final String TAG = "MyGcmListenerService";
    private static GcmMessageReceivedListener listener;
    private static boolean isRepeat = false;
    private static String lastMessage = "";

    public void setListener(GcmMessageReceivedListener listener) {
        MyGcmListenerService.listener = listener;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> map = remoteMessage.getData();
        String message = map.get("message");
        synchronized (lastMessage) {
            if (isRepeat && lastMessage.equals(message)) {
                isRepeat = false;
                return;
            }
            lastMessage= message;
            isRepeat = true;
        }
//        for (Map.Entry<String, String> entry: map.entrySet()) {
//        Log.d(TAG, "Message: " + message);
//        }

        if(listener != null) {
            listener.onGcmMessageReceived(message);
        }

    }
//    @Override
//    public void onMessageReceived(String s, Bundle bundle) {
//        String message = bundle.getString("message");
//        Log.d(TAG, "From: " + s);
//        Log.d(TAG, "Message: " + message);
//        listener.onGcmMessageReceived(message);
//    }

    public interface GcmMessageReceivedListener {
        void onGcmMessageReceived(String message);
    }
}
