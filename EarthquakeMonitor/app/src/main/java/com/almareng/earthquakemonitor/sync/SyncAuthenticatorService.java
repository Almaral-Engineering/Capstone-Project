package com.almareng.earthquakemonitor.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public final class SyncAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private SyncAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new SyncAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(final Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
