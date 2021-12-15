package net.pakcusoft.solat;

import static net.pakcusoft.solat.AlarmReceiver.CHANNEL_AZAN_ID;
import static net.pakcusoft.solat.AlarmReceiver.CHANNEL_REMINDER_ID;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import net.pakcusoft.solat.data.ESolatData;
import net.pakcusoft.solat.data.WaktuSolat;
import net.pakcusoft.solat.databinding.ActivityMainBinding;
import net.pakcusoft.solat.service.EsolatService;
import net.pakcusoft.solat.service.EsolatServiceListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String GLOBAL = "global";
    public static final String DEFAULT_STATE = "def_state";
    public static final String DEFAULT_ZONE = "def_zone";
    public static final String DEFAULT_DATE = "def_date";
    public static final String DEFAULT_PRAYER_TIME = "def_prayer_time";
    public static final String DEFAULT_STATE_SELECTED = "Wilayah Persekutuan";
    public static final String DEFAULT_ZONE_SELECTED = "Kuala Lumpur";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setElevation(0);

        createNotificationChannel(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting_menu:
                Intent i = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupData() {
        SharedPreferences sharedPref = getSharedPreferences(GLOBAL, Context.MODE_PRIVATE);
        String state = sharedPref.getString(DEFAULT_STATE, DEFAULT_STATE_SELECTED);
        String zone = sharedPref.getString(DEFAULT_ZONE, DEFAULT_ZONE_SELECTED);
        Log.d("XXX", "Current state: " + state);
        Log.d("XXX", "Current zone: " + zone);
        if (zone.indexOf("(") > 0) {
            binding.txtZoneInfo.setText(String.format("%s - %s", state, zone));
        } else {
            binding.txtZoneInfo.setText(String.format("%s (%s)", state, zone));
        }
        final String today = LocalDate.now().toString();
        String[] part = today.split("-");
        String todayDate = String.format("%s %s %s", part[2], Constant.bulan.get(part[1]), part[0]);
        String defDate = sharedPref.getString(DEFAULT_DATE, null);
        String defPrayerTime = sharedPref.getString(DEFAULT_PRAYER_TIME, null);
        final Context ctx = this;
        if (defDate == null || !defDate.startsWith(todayDate) || defPrayerTime == null) {
            new EsolatService().getSolatTime(Constant.getZoneId(state, zone), new EsolatServiceListener() {
                @Override
                public void complete(String response) {
                    ESolatData data = Utils.convertJson(response);
                    String hdate = data.getDate();
                    String[] hpart = hdate.split("-");
                    String todayHDate = String.format("%s %s %s", hpart[2], Constant.bulanIslam.get(hpart[1]), hpart[0]);
                    String displayDate = todayDate + " | " + todayHDate;
                    setTiming(data.getWaktuSolatList());
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(DEFAULT_PRAYER_TIME, response);
                    editor.putString(DEFAULT_DATE, displayDate);
                    editor.apply();
                    String ret = ReminderScheduler.nextSchedule(ctx);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.txtDate.setText(displayDate);
                            if (ret != null) {
                                binding.txtReminderSts.setText(ret);
                            }
                        }
                    });
                    updateWidget(ctx, true);
                }

                @Override
                public void failure(Throwable t) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        } else {
            binding.txtDate.setText(defDate);
            ESolatData data = Utils.convertJson(defPrayerTime);
            setTiming(data.getWaktuSolatList());
            String ret = ReminderScheduler.nextSchedule(ctx);
            if (ret != null) {
                binding.txtReminderSts.setText(ret);
            }
            updateWidget(ctx, false);
        }
    }

    private void setTiming(List<WaktuSolat> waktuSolatList) {
        HashMap<String, String> pt = new HashMap<>();
        for (WaktuSolat waktuSolat : waktuSolatList) {
            pt.put(waktuSolat.getName().toLowerCase(), Utils.toDisplayTime(waktuSolat.getTime()));
        }
        binding.txtValueSubuh.setText(pt.get(Constant.SUBUH));
        binding.txtValueSyuruk.setText(pt.get(Constant.SYURUK));
        binding.txtValueZohor.setText(pt.get(Constant.ZOHOR));
        binding.txtValueAsar.setText(pt.get(Constant.ASAR));
        binding.txtValueMaghrib.setText(pt.get(Constant.MAGHRIB));
        binding.txtValueIsyak.setText(pt.get(Constant.ISYAK));
        setCurrentWaktu(waktuSolatList, pt);
    }

    private void setCurrentWaktu(List<WaktuSolat> waktuSolatList, HashMap<String, String> pt) {
        HashMap<String, TextView> temp = new HashMap<>();
        temp.put(Constant.SUBUH, binding.txtValueSubuh);
        temp.put(Constant.SYURUK, binding.txtValueSyuruk);
        temp.put(Constant.ZOHOR, binding.txtValueZohor);
        temp.put(Constant.ASAR, binding.txtValueAsar);
        temp.put(Constant.MAGHRIB, binding.txtValueMaghrib);
        temp.put(Constant.ISYAK, binding.txtValueIsyak);
        //first, set all to black
        for (String waktu : pt.keySet()) {
            if (temp.containsKey(waktu)) {
                temp.get(waktu).setTextColor(ContextCompat.getColor(this, R.color.black_overlay));
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
                        temp.get(waktuSolat.getName()).setTextColor(ContextCompat.getColor(this, R.color.dark_red));
                        break;
                    }
                }
            }
        }
    }

    public static void updateWidget(Context ctx, boolean dataChanged) {
        Intent fromIntent = null;
        if (ctx instanceof Activity) {
            fromIntent = ((Activity) ctx).getIntent();
        }
        if (fromIntent != null || dataChanged) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ctx);
            RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.solat_widget);
            SolatWidget.setupData(ctx, views);
            appWidgetManager.updateAppWidget(new ComponentName(ctx.getPackageName(), SolatWidget.class.getName()), views);
        }
    }

    private static void createNotificationChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
            if (notificationManager.getNotificationChannel(CHANNEL_AZAN_ID) == null) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_AZAN_ID, "Azan", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Notifikasi masuk waktu solat");
                channel.enableVibration(true);
                channel.enableLights(true);
                channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), Notification.AUDIO_ATTRIBUTES_DEFAULT);
                Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ ctx.getApplicationContext().getPackageName() + "/" + R.raw.azanshortkfmi);
                channel.setSound(soundUri, Notification.AUDIO_ATTRIBUTES_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            if (notificationManager.getNotificationChannel(CHANNEL_REMINDER_ID) == null) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_REMINDER_ID, "Solat Reminder", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Notifikasi peringatan sebelum masuk waktu solat");
                channel.enableVibration(true);
                channel.enableLights(true);
                channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), Notification.AUDIO_ATTRIBUTES_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupData();
    }
}