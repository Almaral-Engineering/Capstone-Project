package com.almareng.earthquakemonitor.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.almareng.earthquakemonitor.R;
import com.almareng.earthquakemonitor.api.ApiClient;
import com.almareng.earthquakemonitor.list.Earthquake;
import com.almareng.earthquakemonitor.list.EarthquakeDataListener;
import com.almareng.earthquakemonitor.list.EarthquakeListActivity;

import java.util.ArrayList;

public final class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String ACTION_DATA_UPDATED = "com.almareng.earthquakemonitor.ACTION_DATA_UPDATED";

    // Interval at which to sync with the weather, in milliseconds.
    // 60 seconds (1 minute) * 120 = 2 hours for lower than KITKAT and 1 hour for KITKAT and upper.
//    private static final int SYNC_INTERVAL = 60 * 120;
    private static final int SYNC_INTERVAL = 60;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL/2;
    private static final int EARTHQUAKE_NOTIFICATION_ID = 0;
    public static final int MAX_EARTHQUAKES_TO_DISPLAY_ON_NOTIFICATION = 5;

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
                if (earthquakes != null && !earthquakes.isEmpty()) {
                    updateWidgets();
                    displayNotification(earthquakes);
                }
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

    private void updateWidgets() {
        final Context context = getContext();
        // Setting the package ensures that only components in our app will receive the broadcast
        final Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED).setPackage(context.getPackageName());

        context.sendBroadcast(dataUpdatedIntent);
    }

    private void displayNotification(final ArrayList<Earthquake> earthquakes) {
        final Context context = getContext();
        //checking the last update and notify if it' the first of the day
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String displayNotificationsKey = context.getString(R.string.display_notifications_key);
        final String displayNotificationsDefaultString = context.getString(R.string.display_notifications_default);
        final boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
                                                              Boolean.parseBoolean(displayNotificationsDefaultString));

        if (displayNotifications) {
            final String title = context.getString(R.string.app_name);

            final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setColor(ContextCompat.getColor(context, R.color.silver))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(context.getString(R.string.new_earthquakes_registered));

            final NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

            inboxStyle.setBigContentTitle(context.getString(R.string.last_registered_earthquakes));

            final int numberOfEarthquakesToShow;

            if (earthquakes.size() < MAX_EARTHQUAKES_TO_DISPLAY_ON_NOTIFICATION) {
                numberOfEarthquakesToShow = earthquakes.size();
            } else {
                numberOfEarthquakesToShow = MAX_EARTHQUAKES_TO_DISPLAY_ON_NOTIFICATION;
            }

            for (int i = 0; i < numberOfEarthquakesToShow; i++) {
                final Earthquake earthquake = earthquakes.get(i);
                final String contentText = earthquake.getMagnitude() + " - " + earthquake.getPlace();

                inboxStyle.addLine(contentText);
            }

            builder.setStyle(inboxStyle);

            final Intent intent = new Intent(context, EarthquakeListActivity.class);
            final TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

            stackBuilder.addNextIntent(intent);

            final PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(pendingIntent);

            final NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(EARTHQUAKE_NOTIFICATION_ID, builder.build());
        }
    }
}
