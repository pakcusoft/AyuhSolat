package net.pakcusoft.solat;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "JOMSOLAT";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        boolean reminder = intent.getBooleanExtra("reminder", false);
        String[] time = intent.getStringExtra("time").split("\\|");
        String title = (reminder? "Ingat " : "Waktu ") + Utils.capitalize(time[0]);
        String description = (reminder? "Akan masuk pada " : "Telah masuk pada ") + Utils.toDisplayTime(time[1]);

        Intent newIntent = new Intent(ctx, MainActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, newIntent, 0);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_jom_solat_notification)
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

}