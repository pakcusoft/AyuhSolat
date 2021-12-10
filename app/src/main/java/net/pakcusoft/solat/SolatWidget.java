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
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import net.pakcusoft.solat.data.DataPrayerTime;
import net.pakcusoft.solat.service.EsolatService;
import net.pakcusoft.solat.service.JakimService;
import net.pakcusoft.solat.service.MainService;

import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        String state = sharedPref.getString(DEFAULT_STATE, "selangor");
        String zone = sharedPref.getString(DEFAULT_ZONE, "petaling");
//        String textZone = String.format("%s (%s)", Utils.capitalize(state), Utils.capitalize(zone));
        String textZone = Utils.capitalize(zone);
//        views.setTextViewText(R.id.txt_zone_w, String.format("%s (%s)", Utils.capitalize(state), Utils.capitalize(zone)));
        final String today = LocalDate.now().toString();
        String[] part = today.split("-");
        String todayDate = String.format("%s %s %s", part[2], Constant.bulan.get(part[1]), part[0]);
        String defDate = sharedPref.getString(DEFAULT_DATE, todayDate);
        if (defDate.indexOf("|") > 0) {
            views.setTextViewText(R.id.txt_date_w, defDate.split("\\|")[1] + " | " + textZone);
        } else {
            views.setTextViewText(R.id.txt_date_w, textZone);
        }
        String defPrayerTime = sharedPref.getString(DEFAULT_PRAYER_TIME, null);
        if (defPrayerTime != null) {
            Gson gson = new Gson();
            DataPrayerTime dataPrayerTime = gson.fromJson(defPrayerTime, DataPrayerTime.class);
            setTiming(context, dataPrayerTime, views);
        }
    }

    public static void setTiming(Context context, DataPrayerTime dataPrayerTime, RemoteViews views) {
        HashMap<String, String> pt = new HashMap<>();
        for (DataPrayerTime.WaktuSolat waktuSolat : dataPrayerTime.data.zon.get(0).waktu_solat) {
            pt.put(waktuSolat.name.toLowerCase(), Utils.toDisplayTime(waktuSolat.time).toLowerCase());
        }
        views.setTextViewText(R.id.txt_value_subuh_w, pt.get(Constant.SUBUH));
        views.setTextViewText(R.id.txt_value_syuruk_w, pt.get(Constant.SYURUK));
        views.setTextViewText(R.id.txt_value_zohor_w, pt.get(Constant.ZOHOR));
        views.setTextViewText(R.id.txt_value_asar_w, pt.get(Constant.ASAR));
        views.setTextViewText(R.id.txt_value_maghrib_w, pt.get(Constant.MAGHRIB));
        views.setTextViewText(R.id.txt_value_isyak_w, pt.get(Constant.ISYAK));
        setCurrentWaktu(context, dataPrayerTime, pt, views);
    }

    public static void setCurrentWaktu(Context context, DataPrayerTime dataPrayerTime, HashMap<String, String> pt, RemoteViews views) {
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
            }
        }
        LocalTime now = LocalTime.now();
        for (DataPrayerTime.WaktuSolat waktuSolat : dataPrayerTime.data.zon.get(0).waktu_solat) {
            if (waktuSolat.name.equalsIgnoreCase(Constant.IMSAK)) {
                continue;
            }
            if (waktuSolat.name.equalsIgnoreCase(Constant.SYURUK)) {
                continue;
            }
            LocalTime solatTime = LocalTime.parse(waktuSolat.time);
            if (now.isAfter(solatTime)) {
                LocalTime nextSolatTime;
                if (waktuSolat.name.equalsIgnoreCase(Constant.ISYAK)) {
                    nextSolatTime = LocalTime.MAX;
                } else if (waktuSolat.name.equalsIgnoreCase(Constant.SUBUH)) {
                    nextSolatTime = Utils.toLocalTime(pt.get(Constant.SYURUK));
                } else {
                    String nw = Constant.getNext(waktuSolat.name);
                    nextSolatTime = Utils.toLocalTime(pt.get(nw));
                }
                if (now.isBefore(nextSolatTime)) {
                    if (temp.containsKey(waktuSolat.name)) {
                        views.setTextColor(temp.get(waktuSolat.name), ContextCompat.getColor(context, R.color.white));
                        views.setInt(temp.get(waktuSolat.name), "setBackgroundColor", ContextCompat.getColor(context, R.color.dark_blue));
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