package net.pakcusoft.solat;

import static android.content.Context.ALARM_SERVICE;
import static net.pakcusoft.solat.MainActivity.DEFAULT_DATE;
import static net.pakcusoft.solat.MainActivity.DEFAULT_PRAYER_TIME;
import static net.pakcusoft.solat.MainActivity.DEFAULT_STATE;
import static net.pakcusoft.solat.MainActivity.DEFAULT_STATE_SELECTED;
import static net.pakcusoft.solat.MainActivity.DEFAULT_ZONE;
import static net.pakcusoft.solat.MainActivity.DEFAULT_ZONE_SELECTED;
import static net.pakcusoft.solat.MainActivity.GLOBAL;
import static net.pakcusoft.solat.SettingActivity.SETTING_REMINDER_AZAN;
import static net.pakcusoft.solat.SettingActivity.SETTING_REMINDER_EARLY;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import net.pakcusoft.solat.data.ESolatData;
import net.pakcusoft.solat.data.WaktuSolat;
import net.pakcusoft.solat.service.EsolatService;
import net.pakcusoft.solat.service.EsolatServiceListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class ReminderScheduler {

    private static final int REQ_CODE = 7816;
    private static final int REMINDER_BEFORE_MINUTE = 10;

    public static String nextSchedule(Context ctx) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(GLOBAL, Context.MODE_PRIVATE);
        String defPrayerTime = sharedPref.getString(DEFAULT_PRAYER_TIME, null);
        final String today = LocalDate.now().toString();
        String[] part = today.split("-");
        String todayDate = String.format("%s %s %s", part[2], Constant.bulan.get(part[1]), part[0]);
        String defDate = sharedPref.getString(DEFAULT_DATE, null);
        if (defDate == null || !defDate.startsWith(todayDate) || defPrayerTime == null) {
            String state = sharedPref.getString(DEFAULT_STATE, DEFAULT_STATE_SELECTED);
            String zone = sharedPref.getString(DEFAULT_ZONE, DEFAULT_ZONE_SELECTED);
            new EsolatService().getSolatTime(Constant.getZoneId(state, zone), new EsolatServiceListener() {
                @Override
                public void complete(String response) {
                    ESolatData data = Utils.convertJson(response);
                    String hdate = data.getDate();
                    String[] hpart = hdate.split("-");
                    String todayHDate = String.format("%s %s %s", hpart[2], Constant.bulanIslam.get(hpart[1]), hpart[0]);
                    String displayDate = todayDate + " | " + todayHDate;
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(DEFAULT_PRAYER_TIME, response);
                    editor.putString(DEFAULT_DATE, displayDate);
                    editor.apply();
                    ReminderScheduler.nextSchedule(ctx);
                    MainActivity.updateWidget(ctx, true);
                    doNextSchedule(ctx, sharedPref, response);
                }

                @Override
                public void failure(Throwable t) {

                }
            });
        } else {
            return doNextSchedule(ctx, sharedPref, defPrayerTime);
        }
        return "";
    }

    private static String doNextSchedule(Context ctx, SharedPreferences sharedPref, String defPrayerTime) {
        try {
            ESolatData data = Utils.convertJson(defPrayerTime);
            AlarmData nextAlarm = getNext(data.getWaktuSolatList());
            if (nextAlarm != null) {
                AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(ALARM_SERVICE);

                Intent alarmIntent = new Intent(ctx, AlarmReceiver.class);
                alarmIntent.putExtra("reminder", nextAlarm.reminder);
                alarmIntent.putExtra("specialAlarm", nextAlarm.specialAlarm);
                alarmIntent.putExtra("time", nextAlarm.waktuSolat.toString());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, REQ_CODE,
                        alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Intent newIntent = new Intent(ctx, MainActivity.class);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent newPendingIntent = PendingIntent.getActivity(ctx, 0, newIntent, 0);
                alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(nextAlarm.time.getTimeInMillis(), newPendingIntent), pendingIntent);

                boolean remAzan = sharedPref.getBoolean(SETTING_REMINDER_AZAN, true);
                boolean remEarly = sharedPref.getBoolean(SETTING_REMINDER_EARLY, true);
                if (nextAlarm.reminder) {
                    if (remEarly && !nextAlarm.specialAlarm) {
                        return "Notis Seterusnya: " + REMINDER_BEFORE_MINUTE + " minit sebelum " + Utils.toDisplayTime(nextAlarm.waktuSolat.getTime());
                    }
                } else {
                    if (remAzan) {
                        return "Notis Seterusnya: " + Utils.toDisplayTime(nextAlarm.waktuSolat.getTime());
                    }
                }
            }
            return ""; //no alert for azan & reminder
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERROR: No Reminder";
    }

    private static AlarmData getNext(List<WaktuSolat> waktuSolatList) {
        LocalDateTime now = LocalDateTime.now();
        HashMap<String, WaktuSolat> pt = new HashMap<>();
        for (WaktuSolat w : waktuSolatList) {
            pt.put(w.getName().toLowerCase(), w);
        }
        for (WaktuSolat w : waktuSolatList) {
            if (w.getName().equalsIgnoreCase(Constant.IMSAK)) {
                continue;
            }
            String[] p = w.getTime().split(":");
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
                            true, false);
                } else {
                    return new AlarmData(w,
                            GregorianCalendar.from(ZonedDateTime.of(tsolat, ZoneId.systemDefault())),
                            false, false);
                }
            }
        }
        //if reach here, it means its after isyak, set reminder to after midnight so can get fresh schedule for tomorrow
        if (now.isBefore(now.withHour(23).withMinute(59).withSecond(59))) {
            LocalDateTime reminder = now
                    .plusDays(1)
                    .withHour(0)
                    .plusMinutes(1)
                    .withSecond(0);
            return new AlarmData(pt.get(Constant.SUBUH),
                    GregorianCalendar.from(ZonedDateTime.of(reminder, ZoneId.systemDefault())),
                    true, true);
        }
        return null; //cannot reach here!!!
    }

    static class AlarmData {
        AlarmData(WaktuSolat waktuSolat, Calendar time, boolean reminder, boolean specialAlarm) {
            this.waktuSolat = waktuSolat;
            this.time = time;
            this.reminder = reminder;
            this.specialAlarm = specialAlarm;
        }

        public WaktuSolat waktuSolat;
        public Calendar time;
        public boolean reminder;
        public boolean specialAlarm;
    }
}
