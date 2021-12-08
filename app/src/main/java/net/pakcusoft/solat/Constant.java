package net.pakcusoft.solat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Constant {
    public static final String IMSAK = "imsak";
    public static final String SUBUH = "subuh";
    public static final String SYURUK = "syuruk";
    public static final String ZOHOR = "zohor";
    public static final String ASAR = "asar";
    public static final String MAGHRIB = "maghrib";
    public static final String ISYAK = "isyak";
    public static final List solat = new LinkedList() {{
        add(SUBUH);
        add(SYURUK);
        add(ZOHOR);
        add(ASAR);
        add(MAGHRIB);
        add(ISYAK);
    }};
    public static final String getNext(String solah) {
        Iterator<String> iter = solat.iterator();
        while (iter.hasNext()) {
            if (iter.next().equalsIgnoreCase(solah)) {
                if (iter.hasNext()) {
                    return iter.next();
                } else {
                    return SUBUH;
                }
            }
        }
        return null;
    }

    public static final Map<String, String> bulanIslam = new HashMap<>();
    static {
        bulanIslam.put("01", "Muharram");
        bulanIslam.put("02", "Safar");
        bulanIslam.put("03", "Rabi'ulawal");
        bulanIslam.put("04", "Rabi'ulakhir");
        bulanIslam.put("05", "Jamadilawwal");
        bulanIslam.put("06", "Jamadilakhir");
        bulanIslam.put("07", "Rejab");
        bulanIslam.put("08", "Sha'ban");
        bulanIslam.put("09", "Ramadhan");
        bulanIslam.put("10", "Syawal");
        bulanIslam.put("11", "Zulqa'idah");
        bulanIslam.put("12", "Zullhijjah");
    }
    public static final Map<String, String> bulan = new HashMap<>();
    static {
        bulan.put("01", "Januari");
        bulan.put("02", "Febuari");
        bulan.put("03", "Mac");
        bulan.put("04", "April");
        bulan.put("05", "Mei");
        bulan.put("06", "Jun");
        bulan.put("07", "Julai");
        bulan.put("08", "Ogos");
        bulan.put("09", "September");
        bulan.put("10", "Oktober");
        bulan.put("11", "November");
        bulan.put("12", "Disember");
    }
}
