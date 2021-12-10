package net.pakcusoft.solat;

import static net.pakcusoft.solat.AlarmReceiver.CHANNEL_AZAN_ID;
import static net.pakcusoft.solat.AlarmReceiver.CHANNEL_REMINDER_ID;

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

import com.google.gson.Gson;

import net.pakcusoft.solat.data.DataPrayerTime;
import net.pakcusoft.solat.databinding.ActivityMainBinding;
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

public class MainActivity extends AppCompatActivity {

    public static final String GLOBAL = "global";
    public static final String DEFAULT_STATE = "def_state";
    public static final String DEFAULT_ZONE = "def_zone";
    public static final String DEFAULT_DATE = "def_date";
    public static final String DEFAULT_PRAYER_TIME = "def_prayer_time";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setElevation(0);

        createNotificationChannel(this);
        //setupData(); //FIXME: onResume will call this??
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
        String state = sharedPref.getString(DEFAULT_STATE, "selangor");
        String zone = sharedPref.getString(DEFAULT_ZONE, "petaling");
        Log.d("XXX", "Current state: " + state);
        Log.d("XXX", "Current zone: " + zone);
        binding.txtZoneInfo.setText(String.format("%s (%s)", Utils.capitalize(state), Utils.capitalize(zone)));
        final String today = LocalDate.now().toString();
        String[] part = today.split("-");
        String todayDate = String.format("%s %s %s", part[2], Constant.bulan.get(part[1]), part[0]);
        String defPrayerTime = null;
        String defDate = sharedPref.getString(DEFAULT_DATE, null);
        if (defDate == null || !defDate.startsWith(todayDate) || !defDate.contains("|")) {
            binding.txtDate.setText(todayDate);
            EsolatService esolatService = JakimService.getService();
            Call<ResponseBody> call = esolatService.getHijriDate(today);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject obj = new JSONObject(response.body().string());
                            String hdate = obj.getJSONObject("takwim").getString(today);
                            String[] hpart = hdate.split("-");
                            String todayHDate = String.format("%s %s %s", hpart[2], Constant.bulanIslam.get(hpart[1]), hpart[0]);
                            String displayDate = todayDate + " | " + todayHDate;
                            binding.txtDate.setText(displayDate);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(DEFAULT_DATE, displayDate);
                            editor.apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        } else {
            binding.txtDate.setText(defDate);
            defPrayerTime = sharedPref.getString(DEFAULT_PRAYER_TIME, null);
        }
        final Context ctx = this;
        if (defPrayerTime == null) {
            //get prayer time based on default data
            Call<DataPrayerTime> prayerTimeCall = MainService.getService().getPrayerTime(state, zone);
            prayerTimeCall.enqueue(new Callback<DataPrayerTime>() {
                @Override
                public void onResponse(Call<DataPrayerTime> call, Response<DataPrayerTime> response) {
                    if (response.isSuccessful()) {
                        DataPrayerTime dataPrayerTime = response.body();
                        setTiming(dataPrayerTime);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        Gson gson = new Gson();
                        editor.putString(DEFAULT_PRAYER_TIME, gson.toJson(dataPrayerTime));
                        editor.apply();
                        String ret = ReminderScheduler.nextSchedule(ctx);
                        if (ret != null) {
                            binding.txtReminderSts.setText(ret);
                        }
                        updateWidget(true);
                    }
                }

                @Override
                public void onFailure(Call<DataPrayerTime> call, Throwable t) {
                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Gson gson = new Gson();
            DataPrayerTime dataPrayerTime = gson.fromJson(defPrayerTime, DataPrayerTime.class);
            setTiming(dataPrayerTime);
            String ret = ReminderScheduler.nextSchedule(ctx);
            if (ret != null) {
                binding.txtReminderSts.setText(ret);
            }
            updateWidget(false);
        }
    }

    private void setTiming(DataPrayerTime dataPrayerTime) {
        HashMap<String, String> pt = new HashMap<>();
        for (DataPrayerTime.WaktuSolat waktuSolat : dataPrayerTime.data.zon.get(0).waktu_solat) {
            pt.put(waktuSolat.name.toLowerCase(), Utils.toDisplayTime(waktuSolat.time));
        }
        binding.txtValueSubuh.setText(pt.get(Constant.SUBUH));
        binding.txtValueSyuruk.setText(pt.get(Constant.SYURUK));
        binding.txtValueZohor.setText(pt.get(Constant.ZOHOR));
        binding.txtValueAsar.setText(pt.get(Constant.ASAR));
        binding.txtValueMaghrib.setText(pt.get(Constant.MAGHRIB));
        binding.txtValueIsyak.setText(pt.get(Constant.ISYAK));
        setCurrentWaktu(dataPrayerTime, pt);
    }

    private void setCurrentWaktu(DataPrayerTime dataPrayerTime, HashMap<String, String> pt) {
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
                        temp.get(waktuSolat.name).setTextColor(ContextCompat.getColor(this, R.color.dark_red));
                        break;
                    }
                }
            }
        }
    }

    private void updateWidget(boolean dataChanged) {
        Intent fromIntent = getIntent();
        if (fromIntent != null || dataChanged) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.solat_widget);
            SolatWidget.setupData(this, views);
            appWidgetManager.updateAppWidget(new ComponentName(this.getPackageName(), SolatWidget.class.getName()), views);
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