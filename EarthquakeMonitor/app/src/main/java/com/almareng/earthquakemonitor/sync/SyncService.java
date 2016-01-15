package com.almareng.earthquakemonitor.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public final class SyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static SyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
