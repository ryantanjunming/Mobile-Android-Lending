package com.crowdo.p2pconnect.oauth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by cwdsg05 on 24/3/17.
 */

public class CrowdoAuthService extends Service{

    private static final String LOG_TAG = CrowdoAuthService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        //service extended for AccountManager
        CrowdoAccountAuthenticator authAuthenticator = new CrowdoAccountAuthenticator(this);
        return authAuthenticator.getIBinder();
    }


}
