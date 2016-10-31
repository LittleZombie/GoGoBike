package gogobike.egg.com.gogobike;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Liou on 2016/10/31.
 */

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Intent intent = new Intent(this, RegistrationIntentService.class);
        intent.putExtra("token", refreshedToken);
        startService(intent);
    }
}
