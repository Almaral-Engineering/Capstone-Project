package com.almareng.earthquakemonitor.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.almareng.earthquakemonitor.R;
import com.almareng.earthquakemonitor.api.ApiClient;
import com.almareng.earthquakemonitor.list.Earthquake;
import com.almareng.earthquakemonitor.list.EarthquakeDataListener;

import java.util.ArrayList;

public final class SyncAdapter extends AbstractThreadedSyncAdapter {
    // Interval at which to sync with the weather, in milliseconds.
    // 60 seconds (1 minute) * 120 = 2 hours for lower than KITKAT and 1 hour for KITKAT and upper.
    public static final int SYNC_INTERVAL = 120;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/2;

    public SyncAdapter(final Context context, final boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(final Account account,
                              final Bundle extras,
                              final String authority,
                              final ContentProviderClient provider,
                              final SyncResult syncResult) {
        final Context context = getContext();

        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String postUrl = sharedPrefs.getString(context.getString(R.string.searching_time_key),
                                                     context.getString(R.string.searching_time_default));

        ApiClient.getEarthquakeData(context, postUrl, new EarthquakeDataListener() {
            @Override
            public void onResponse(final ArrayList<Earthquake> earthquakes) {

            }

            @Override
            public void onErrorResponse(final Exception error) {
                Toast.makeText(context, context.getString(R.string.volley_fetching_error_message), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(final Context context) {
        final Bundle bundle = new Bundle();

        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(final Context context) {
        // Get an instance of the Android account manager
        final AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        final Account newAccount = new Account(context.getString(R.string.app_name),
                                               context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(final Context context, final int syncInterval, final int flexTime) {
        final Account account = getSyncAccount(context);
        final String authority = context.getString(R.string.content_authority);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            final SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(final Account newAccount, final Context context) {
        /*
         * Since we've created an account
         */
        SyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(final Context context) {
        getSyncAccount(context);
    }
}
