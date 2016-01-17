package com.almareng.earthquakemonitor.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.almareng.earthquakemonitor.R;
import com.almareng.earthquakemonitor.details.DetailActivity;
import com.almareng.earthquakemonitor.list.EarthquakeListActivity;
import com.almareng.earthquakemonitor.sync.SyncAdapter;

public final class WidgetProvider extends AppWidgetProvider {
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (final int appWidgetId : appWidgetIds) {
            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            final Intent intent = new Intent(context, EarthquakeListActivity.class);
            final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            views.setOnClickPendingIntent(R.id.widget_title, pendingIntent);
            views.setRemoteAdapter(R.id.widget_list, new Intent(context, WidgetRemoteViewsService.class));

            final boolean useDetailActivity = context.getResources().getBoolean(R.bool.use_detail_activity);
            final Intent clickIntentTemplate = useDetailActivity
                    ? new Intent(context, DetailActivity.class) : new Intent(context, EarthquakeListActivity.class);
            final PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);
            views.setEmptyView(R.id.widget_list, R.id.widget_empty);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(@NonNull final Context context, @NonNull final Intent intent) {
        super.onReceive(context, intent);
        if (SyncAdapter.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        }
    }
}
