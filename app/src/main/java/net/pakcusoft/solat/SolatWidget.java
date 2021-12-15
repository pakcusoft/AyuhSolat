package net.pakcusoft.solat;

import static net.pakcusoft.solat.MainActivity.DEFAULT_DATE;
import static net.pakcusoft.solat.MainActivity.DEFAULT_PRAYER_TIME;
import static net.pakcusoft.solat.MainActivity.DEFAULT_STATE;
import static net.pakcusoft.solat.MainActivity.DEFAULT_ZONE;
import static net.pakcusoft.solat.MainActivity.GLOBAL;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

import net.pakcusoft.solat.data.ESolatData;
import net.pakcusoft.solat.data.WaktuSolat;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class SolatWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.solat_widget);
        setupData(context, views);

        Intent configIntent = new Intent(context, MainActivity.class);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
        views.setOnClickPendingIntent(R.id.txt_date_w, configPendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void setupData(Context context, RemoteViews views) {
        SharedPreferences sharedPref = context.getSharedPreferences(GLOBAL, Context.MODE_PRIVATE);
        String state = sharedPref.getString(DEFAULT_STATE, "Wilayah Persekutuan");
        String zone = sharedPref.getString(DEFAULT_ZONE, "Kuala Lumpur");
        String defDate = sharedPref.getString(DEFAULT_DATE, null);
        if (defDate != null) {
            views.setTextViewText(R.id.txt_date_w, defDate.split("\\|")[1] + " | " + zone);
            String defPrayerTime = sharedPref.getString(DEFAULT_PRAYER_TIME, null);
            if (defPrayerTime != null) {
                ESolatData data = Utils.convertJson(defPrayerTime);
                setTiming(context, data.getWaktuSolatList(), views);
            }
        }
    }

    public static void setTiming(Context context, List<WaktuSolat> waktuSolatList, RemoteViews views) {
        HashMap<String, String> pt = new HashMap<>();
        for (WaktuSolat waktuSolat : waktuSolatList) {
            pt.put(waktuSolat.getName().toLowerCase(), Utils.toDisplayTime(waktuSolat.getTime()).toLowerCase());
        }
        views.setTextViewText(R.id.txt_value_subuh_w, pt.get(Constant.SUBUH));
        views.setTextViewText(R.id.txt_value_syuruk_w, pt.get(Constant.SYURUK));
        views.setTextViewText(R.id.txt_value_zohor_w, pt.get(Constant.ZOHOR));
        views.setTextViewText(R.id.txt_value_asar_w, pt.get(Constant.ASAR));
        views.setTextViewText(R.id.txt_value_maghrib_w, pt.get(Constant.MAGHRIB));
        views.setTextViewText(R.id.txt_value_isyak_w, pt.get(Constant.ISYAK));
        setCurrentWaktu(context, waktuSolatList, pt, views);
    }

    public static void setCurrentWaktu(Context context, List<WaktuSolat> waktuSolatList, HashMap<String, String> pt, RemoteViews views) {
        HashMap<String, Integer> temp = new HashMap<>();
        temp.put(Constant.SUBUH, R.id.txt_value_subuh_w);
        temp.put(Constant.SYURUK, R.id.txt_value_syuruk_w);
        temp.put(Constant.ZOHOR, R.id.txt_value_zohor_w);
        temp.put(Constant.ASAR, R.id.txt_value_asar_w);
        temp.put(Constant.MAGHRIB, R.id.txt_value_maghrib_w);
        temp.put(Constant.ISYAK, R.id.txt_value_isyak_w);
        //first, set all to black
        for (String waktu : pt.keySet()) {
            if (temp.containsKey(waktu)) {
                views.setTextColor(temp.get(waktu), ContextCompat.getColor(context, R.color.black_overlay));
                views.setInt(temp.get(waktu), "setBackgroundResource", R.drawable.widget_solat_box);
            }
        }
        LocalTime now = LocalTime.now();
        for (WaktuSolat waktuSolat : waktuSolatList) {
            if (waktuSolat.getName().equalsIgnoreCase(Constant.IMSAK)) {
                continue;
            }
            if (waktuSolat.getName().equalsIgnoreCase(Constant.SYURUK)) {
                continue;
            }
            LocalTime solatTime = LocalTime.parse(waktuSolat.getTime());
            if (now.isAfter(solatTime)) {
                LocalTime nextSolatTime;
                if (waktuSolat.getName().equalsIgnoreCase(Constant.ISYAK)) {
                    nextSolatTime = LocalTime.MAX;
                } else if (waktuSolat.getName().equalsIgnoreCase(Constant.SUBUH)) {
                    nextSolatTime = Utils.toLocalTime(pt.get(Constant.SYURUK));
                } else {
                    String nw = Constant.getNext(waktuSolat.getName());
                    nextSolatTime = Utils.toLocalTime(pt.get(nw));
                }
                if (now.isBefore(nextSolatTime)) {
                    if (temp.containsKey(waktuSolat.getName())) {
                        views.setTextColor(temp.get(waktuSolat.getName()), ContextCompat.getColor(context, R.color.white));
                        views.setInt(temp.get(waktuSolat.getName()), "setBackgroundColor", ContextCompat.getColor(context, R.color.dark_blue));
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}