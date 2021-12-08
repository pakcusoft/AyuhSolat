package net.pakcusoft.solat.data;

import java.util.List;

public class DataPrayerTime {
    public Data data;

    public class Data {
        public String negeri;
        public List<PrayerTimeZone> zon;
    }

    public class PrayerTimeZone {
        public String nama;
        public List<WaktuSolat> waktu_solat;
    }

    public class WaktuSolat {
        public String name;
        public String time;

        @Override
        public String toString() {
            return name + "|" + time;
        }
    }
}
