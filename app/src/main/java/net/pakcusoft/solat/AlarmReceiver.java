package net.pakcusoft.solat;

import static net.pakcusoft.solat.MainActivity.GLOBAL;
import static net.pakcusoft.solat.SettingActivity.SETTING_REMINDER_AZAN;
import static net.pakcusoft.solat.SettingActivity.SETTING_REMINDER_EARLY;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String CHANNEL_AZAN_ID = "AZAN";
    public static final String CHANNEL_REMINDER_ID = "REMINDER";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(GLOBAL, Context.MODE_PRIVATE);
        boolean remAzan = sharedPref.getBoolean(SETTING_REMINDER_AZAN, true);
        boolean remEarly = sharedPref.getBoolean(SETTING_REMINDER_EARLY, true);

        String[] time = intent.getStringExtra("time").split("\\|");
        boolean reminder = intent.getBooleanExtra("reminder", false);
        boolean specialAlarm = intent.getBooleanExtra("specialAlarm", false);
        if (!reminder) {
            updateWidget(ctx);
        }
        if (reminder) {
            if (specialAlarm) {
                ReminderScheduler.nextSchedule(ctx);
                updateWidget(ctx);
                return;
            } else {
                if (!remEarly || Constant.SYURUK.equalsIgnoreCase(time[0])) {
                    ReminderScheduler.nextSchedule(ctx);
                    return;
                }
            }
        } else {
            if (!remAzan || Constant.SYURUK.equalsIgnoreCase(time[0])) {
                ReminderScheduler.nextSchedule(ctx);
                return;
            }
        }
        String title = (reminder? "Ingat " : "Waktu ") + Utils.capitalize(time[0]);
        String description = (reminder? "Akan masuk pada " : "Telah masuk pada ") + Utils.toDisplayTime(time[1]);

        Intent newIntent = new Intent(ctx, MainActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, newIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
        NotificationCompat.Builder builder;
        if (reminder) {
            builder = new NotificationCompat.Builder(ctx, CHANNEL_REMINDER_ID);
        } else {
            builder = new NotificationCompat.Builder(ctx, CHANNEL_AZAN_ID);
        }
        builder = builder.setSmallIcon(R.drawable.ic_stat_jom_solat_notification)
                .setContentTitle(title)
                .setContentText(description)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        notificationManager.notify(new Random().nextInt(Integer.MAX_VALUE), builder.build());
        ReminderScheduler.nextSchedule(ctx);
        Log.d("XXX", "Sending notification");
        Log.d("XXX", "time: " + time);
        Log.d("XXX", "reminder: " + reminder);
    }

    private void updateWidget(Context ctx) {
        SolatWidget.setupData(ctx);
    }

}