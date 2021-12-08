package net.pakcusoft.solat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public class Utils {
    private static final SimpleDateFormat t24f = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat t12f = new SimpleDateFormat("h:mm a");

    public static String capitalize(String str) {
        String retStr = str;
        try {
            retStr = str.substring(0, 1).toUpperCase() + str.substring(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retStr;
    }

    public static String toDisplayTime(String time) {
        try {
            return t12f.format(t24f.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static LocalTime toLocalTime(String time) {
        try {
            return LocalDateTime.ofInstant(t12f.parse(time).toInstant(),
                    ZoneId.systemDefault()).toLocalTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
