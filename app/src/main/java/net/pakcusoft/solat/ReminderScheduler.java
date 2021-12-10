package net.pakcusoft.solat;

import static android.content.Context.ALARM_SERVICE;
import static net.pakcusoft.solat.MainActivity.DEFAULT_PRAYER_TIME;
import static net.pakcusoft.solat.MainActivity.GLOBAL;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import net.pakcusoft.solat.data.DataPrayerTime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ReminderScheduler {

    private static final int REQ_CODE = 7816;
    private static final int REMINDER_BEFORE_MINUTE = 10;

    public static String nextSchedule(Context ctx) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(GLOBAL, Context.MODE_PRIVATE);
        String defPrayerTime = sharedPref.getString(DEFAULT_PRAYER_TIME, null);
        if (defPrayerTime != null) {
            Gson gson = new Gson();
            DataPrayerTime dataPrayerTime = gson.fromJson(defPrayerTime, DataPrayerTime.class);
            AlarmData nextAlarm = getNext(dataPrayerTime);
            AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(ALARM_SERVICE);

            Intent alarmIntent = new Intent(ctx, AlarmReceiver.class);
            alarmIntent.putExtra("reminder", nextAlarm.reminder);
            alarmIntent.putExtra("time", nextAlarm.waktuSolat.toString());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, REQ_CODE,
                    alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextAlarm.time.getTimeInMillis(), pendingIntent);

            Intent newIntent = new Intent(ctx, MainActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent newPendingIntent = PendingIntent.getActivity(ctx, 0, newIntent, 0);
            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(nextAlarm.time.getTimeInMillis(), newPendingIntent), pendingIntent);

            if (nextAlarm.reminder) {
                return "Notis Seterusnya: " + REMINDER_BEFORE_MINUTE + " minit sebelum " + Utils.toDisplayTime(nextAlarm.waktuSolat.time);
            } else {
                return "Notis Seterusnya: " + Utils.toDisplayTime(nextAlarm.waktuSolat.time);
            }
        }
        return "ERROR: No Reminder";
    }

    private static AlarmData getNext(DataPrayerTime dataPrayerTime) {
        DataPrayerTime.Data data = dataPrayerTime.data;
        if (data != null) {
            if (data.zon != null && !data.zon.isEmpty())  {
                if (data.zon.get(0).waktu_solat != null) {
                    LocalDateTime now = LocalDateTime.now();
                    HashMap<String, DataPrayerTime.WaktuSolat> pt = new HashMap<>();
                    for (DataPrayerTime.WaktuSolat w : data.zon.get(0).waktu_solat) {
                        pt.put(w.name.toLowerCase(), w);
                    }
                    for (DataPrayerTime.WaktuSolat w : data.zon.get(0).waktu_solat) {
                        if (w.name.equalsIgnoreCase(Constant.IMSAK)) {
                            continue;
                        }
                        String[] p = w.time.split(":");
                        LocalDateTime tsolat = LocalDateTime.now()
                                .withHour(Integer.parseInt(p[0]))
                                .withMinute(Integer.parseInt(p[1]))
                                .withSecond(0)
                                .withNano(0);
                        if (now.isBefore(tsolat)) {
                            LocalDateTime reminder = tsolat.minusMinutes(REMINDER_BEFORE_MINUTE);
                            if (now.plusMinutes(1).isBefore(reminder)) {
                                return new AlarmData(w,
                                        GregorianCalendar.from(ZonedDateTime.of(reminder, ZoneId.systemDefault())),
                                        true);
                            } else {
                                return new AlarmData(w,
                                        GregorianCalendar.from(ZonedDateTime.of(tsolat, ZoneId.systemDefault())),
                                        false);
                            }
                        }
                    }
                    //if reach here, it means its after isyak, so default to subuh
                    DataPrayerTime.WaktuSolat w = pt.get(Constant.SUBUH);
                    String[] p = w.time.split(":");
                    LocalDateTime tsolat = LocalDateTime.now();
                    if (now.isBefore(now.withHour(23).withMinute(59).withSecond(59))) {
                        tsolat = tsolat.plusDays(1);
                    }
                    tsolat = tsolat
                            .withHour(Integer.parseInt(p[0]))
                            .withMinute(Integer.parseInt(p[1]))
                            .withSecond(0)
                            .withNano(0);
                    LocalDateTime reminder = tsolat.minusMinutes(REMINDER_BEFORE_MINUTE);
                    if (now.isBefore(reminder)) {
                        return new AlarmData(w,
                                GregorianCalendar.from(ZonedDateTime.of(reminder, ZoneId.systemDefault())),
                                true);
                    } else {
                        return new AlarmData(w,
                                GregorianCalendar.from(ZonedDateTime.of(tsolat, ZoneId.systemDefault())),
                                false);
                    }
                }
            }
        }
        return null;
    }

    static class AlarmData {
        AlarmData(DataPrayerTime.WaktuSolat waktuSolat, Calendar time, boolean reminder) {
            this.waktuSolat = waktuSolat;
            this.time = time;
            this.reminder = reminder;
        }

        public DataPrayerTime.WaktuSolat waktuSolat;
        public Calendar time;
        public boolean reminder;
    }
}
