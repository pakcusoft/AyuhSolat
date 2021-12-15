package net.pakcusoft.solat;

import android.text.TextUtils;

import net.pakcusoft.solat.data.ESolatData;
import net.pakcusoft.solat.data.WaktuSolat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static final SimpleDateFormat t24f = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat t12f = new SimpleDateFormat("h:mm a");

    public static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        String retStr = "";
        try {
            String[] parts = str.split(" ");
            for (String part : parts) {
                if (part.length() > 1) {
                    retStr += part.substring(0, 1).toUpperCase() + part.substring(1).toLowerCase() + " ";
                } else {
                    retStr += part;
                }
            }
            retStr = retStr.trim();
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

    public static ESolatData convertJson(String defPrayerTime) {
        List<WaktuSolat> waktuSolatList = new ArrayList<>();
        String dateHijri = "";
        try {
            JSONObject obj = new JSONObject(defPrayerTime);
            JSONArray jsonArray = obj.getJSONArray("prayerTime");
            JSONObject pObj = jsonArray.getJSONObject(0);
            dateHijri = pObj.getString("hijri");
            for (String waktu : Constant.solat) {
                waktuSolatList.add(new WaktuSolat(waktu, pObj.getString(Constant.getWaktuCode(waktu))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ESolatData(dateHijri, waktuSolatList);
    }
}
