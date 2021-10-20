package it.bonny.app.wiseframe.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import it.bonny.app.wiseframe.R;
import it.bonny.app.wiseframe.manager.SliderActivity;

public class WidgetStartPresentation extends AppWidgetProvider {
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
// (1) carica layout nella RemoteViews
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_start_presentation);
// (2.1) gestione click su pulsante che attiva video
        Intent intent = new Intent(context, SliderActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity (context, 0, intent, 0);
        views.setOnClickPendingIntent (R.id.infoWidget, pendingIntent);
// (3) AppWidgetManager assegna la RemoteViews all'App Widget con id appWidgetId
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}
