package gogobike.egg.com.gogobike;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

import gogobike.egg.com.backend.registration.Registration;

/**
 * Created by Liou on 2016/10/31.
 */

public class RegistrationIntentService extends IntentService {
    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
            InstanceID instanceID = InstanceID.getInstance(this);
        try {
            String token = intent.getStringExtra("token");
            if(token == null || token.isEmpty()) {
                token = instanceID.getToken("526886560498", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.d("InstanceId token", token);
            }

            Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://gogobikebackend.appspot.com/_ah/api/");
            Registration registration = builder.build();
            registration.register(token).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
