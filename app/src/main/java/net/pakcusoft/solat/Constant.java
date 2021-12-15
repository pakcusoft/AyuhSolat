package net.pakcusoft.solat;

import net.pakcusoft.solat.data.Zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    public static final List<String> solat = new ArrayList() {{
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
    public static final String getWaktuCode(String waktu) {
        switch (waktu) {
            case IMSAK:
                return "imsak";
            case SUBUH:
                return "fajr";
            case SYURUK:
                return "syuruk";
            case ZOHOR:
                return "dhuhr";
            case ASAR:
                return "asr";
            case MAGHRIB:
                return "maghrib";
            case ISYAK:
                return "isha";
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
    public static List<String> negeris = new ArrayList<>();
    public static Map<String, List<Zone>> zones = new HashMap<>();
    static {
        negeris.add("Johor");
        negeris.add("Kedah");
        negeris.add("Kelantan");
        negeris.add("Melaka");
        negeris.add("Negeri Sembilan");
        negeris.add("Pahang");
        negeris.add("Perlis");
        negeris.add("Pulau Pinang");
        negeris.add("Perak");
        negeris.add("Sabah");
        negeris.add("Selangor");
        negeris.add("Sarawak");
        negeris.add("Terengganu");
        negeris.add("Wilayah Persekutuan");
    }
    static {
        List<Zone> zone0 = new ArrayList<>();
        zone0.add(new Zone("JHR01", "Pulau Aur"));
        zone0.add(new Zone("JHR01", "Pulau Pemanggil"));
        zone0.add(new Zone("JHR02", "Johor Bahru"));
        zone0.add(new Zone("JHR02", "Kota Tinggi"));
        zone0.add(new Zone("JHR02", "Mersing"));
        zone0.add(new Zone("JHR03", "Kluang"));
        zone0.add(new Zone("JHR03", "Pontian"));
        zone0.add(new Zone("JHR04", "Batu Pahat"));
        zone0.add(new Zone("JHR04", "Muar"));
        zone0.add(new Zone("JHR04", "Segamat"));
        zone0.add(new Zone("JHR04", "Gemas Johor"));
        zones.put("Johor", zone0);
        List<Zone> zone1 = new ArrayList<>();
        zone1.add(new Zone("KDH01", "Kota Setar"));
        zone1.add(new Zone("KDH01", "Kubang Pasu"));
        zone1.add(new Zone("KDH01", "Pokok Sena"));
        zone1.add(new Zone("KDH02", "Kuala Muda"));
        zone1.add(new Zone("KDH02", "Yan"));
        zone1.add(new Zone("KDH02", "Pendang"));
        zone1.add(new Zone("KDH03", "Padang Terap"));
        zone1.add(new Zone("KDH03", "Sik"));
        zone1.add(new Zone("KDH04", "Baling"));
        zone1.add(new Zone("KDH05", "Bandar Baharu"));
        zone1.add(new Zone("KDH05", "Kulim"));
        zone1.add(new Zone("KDH06", "Langkawi"));
        zone1.add(new Zone("KDH07", "Puncak Gunung Jerai"));
        zones.put("Kedah", zone1);
        List<Zone> zone2 = new ArrayList<>();
        zone2.add(new Zone("KTN01", "Bachok"));
        zone2.add(new Zone("KTN01", "Kota Bharu"));
        zone2.add(new Zone("KTN01", "Machang"));
        zone2.add(new Zone("KTN01", "Pasir Mas"));
        zone2.add(new Zone("KTN01", "Pasir Puteh"));
        zone2.add(new Zone("KTN01", "Tanah Merah"));
        zone2.add(new Zone("KTN01", "Tumpat"));
        zone2.add(new Zone("KTN01", "Kuala Krai"));
        zone2.add(new Zone("KTN01", "Mukim Chiku"));
        zone2.add(new Zone("KTN03", "Gua Musang"));
        zone2.add(new Zone("KTN03", "Jeli"));
        zone2.add(new Zone("KTN03", "Jajahan Kecil Lojing"));
        zones.put("Kelantan", zone2);
        List<Zone> zone3 = new ArrayList<>();
        zone3.add(new Zone("MLK01", "Melaka"));
        zones.put("Melaka", zone3);
        List<Zone> zone4 = new ArrayList<>();
        zone4.add(new Zone("NGS01", "Tampin"));
        zone4.add(new Zone("NGS01", "Jempol"));
        zone4.add(new Zone("NGS02", "Jelebu"));
        zone4.add(new Zone("NGS02", "Kuala Pilah"));
        zone4.add(new Zone("NGS02", "Port Dickson"));
        zone4.add(new Zone("NGS02", "Rembau"));
        zone4.add(new Zone("NGS02", "Seremban"));
        zones.put("Negeri Sembilan", zone4);
        List<Zone> zone5 = new ArrayList<>();
        zone5.add(new Zone("PHG01", "Pulau Tioman"));
        zone5.add(new Zone("PHG02", "Kuantan"));
        zone5.add(new Zone("PHG02", "Pekan"));
        zone5.add(new Zone("PHG02", "Rompin"));
        zone5.add(new Zone("PHG02", "Muadzam Shah"));
        zone5.add(new Zone("PHG03", "Jerantut"));
        zone5.add(new Zone("PHG03", "Temerloh"));
        zone5.add(new Zone("PHG03", "Maran"));
        zone5.add(new Zone("PHG03", "Bera"));
        zone5.add(new Zone("PHG03", "Chenor"));
        zone5.add(new Zone("PHG03", "Jengka"));
        zone5.add(new Zone("PHG04", "Bentong"));
        zone5.add(new Zone("PHG04", "Lipis"));
        zone5.add(new Zone("PHG04", "Raub"));
        zone5.add(new Zone("PHG05", "Genting Sempah"));
        zone5.add(new Zone("PHG05", "Janda Baik"));
        zone5.add(new Zone("PHG05", "Bukit Tinggi"));
        zone5.add(new Zone("PHG06", "Cameron Highlands"));
        zone5.add(new Zone("PHG06", "Genting Higlands"));
        zone5.add(new Zone("PHG06", "Bukit Fraser"));
        zones.put("Pahang", zone5);
        List<Zone> zone6 = new ArrayList<>();
        zone6.add(new Zone("PLS01", "Kangar"));
        zone6.add(new Zone("PLS01", "Padang Besar"));
        zone6.add(new Zone("PLS01", "Arau"));
        zones.put("Perlis", zone6);
        List<Zone> zone7 = new ArrayList<>();
        zone7.add(new Zone("PNG01", "Pulau Pinang"));
        zones.put("Pulau Pinang", zone7);
        List<Zone> zone8 = new ArrayList<>();
        zone8.add(new Zone("PRK01", "Tapah"));
        zone8.add(new Zone("PRK01", "Slim River"));
        zone8.add(new Zone("PRK01", "Tanjung Malim"));
        zone8.add(new Zone("PRK02", "Kuala Kangsar"));
        zone8.add(new Zone("PRK02", "Sg. Siput"));
        zone8.add(new Zone("PRK02", "Ipoh"));
        zone8.add(new Zone("PRK02", "Batu Gajah"));
        zone8.add(new Zone("PRK02", "Kampar"));
        zone8.add(new Zone("PRK03", "Lenggong"));
        zone8.add(new Zone("PRK03", "Pengkalan Hulu"));
        zone8.add(new Zone("PRK03", "Grik"));
        zone8.add(new Zone("PRK04", "Temengor"));
        zone8.add(new Zone("PRK04", "Belum"));
        zone8.add(new Zone("PRK05", "Kg Gajah"));
        zone8.add(new Zone("PRK05", "Teluk Intan"));
        zone8.add(new Zone("PRK05", "Bagan Datuk"));
        zone8.add(new Zone("PRK05", "Seri Iskandar"));
        zone8.add(new Zone("PRK05", "Beruas"));
        zone8.add(new Zone("PRK05", "Parit"));
        zone8.add(new Zone("PRK05", "Lumut"));
        zone8.add(new Zone("PRK05", "Sitiawan"));
        zone8.add(new Zone("PRK05", "Pulau Pangkor"));
        zone8.add(new Zone("PRK06", "Selama"));
        zone8.add(new Zone("PRK06", "Taiping"));
        zone8.add(new Zone("PRK06", "Bagan Serai"));
        zone8.add(new Zone("PRK06", "Parit Buntar"));
        zone8.add(new Zone("PRK07", "Bukit Larut"));
        zones.put("Perak", zone8);
        List<Zone> zone9 = new ArrayList<>();
        zone9.add(new Zone("SBH01", "Bahagian Sandakan (Timur)"));
        zone9.add(new Zone("SBH01", "Bukit Garam"));
        zone9.add(new Zone("SBH01", "Semawang"));
        zone9.add(new Zone("SBH01", "Temanggong"));
        zone9.add(new Zone("SBH01", "Tambisan"));
        zone9.add(new Zone("SBH01", "Bandar Sandakan"));
        zone9.add(new Zone("SBH01", "Sukau"));
        zone9.add(new Zone("SBH02", "Beluran"));
        zone9.add(new Zone("SBH02", "Telupid"));
        zone9.add(new Zone("SBH02", "Pinangah"));
        zone9.add(new Zone("SBH02", "Terusan"));
        zone9.add(new Zone("SBH02", "Kuamut"));
        zone9.add(new Zone("SBH02", "Bahagian Sandakan (Barat)"));
        zone9.add(new Zone("SBH03", "Lahad Datu"));
        zone9.add(new Zone("SBH03", "Silabukan"));
        zone9.add(new Zone("SBH03", "Kunak"));
        zone9.add(new Zone("SBH03", "Sahabat"));
        zone9.add(new Zone("SBH03", "Semporna"));
        zone9.add(new Zone("SBH03", "Tungku"));
        zone9.add(new Zone("SBH03", "Bahagian Tawau  (Timur)"));
        zone9.add(new Zone("SBH04", "Bandar Tawau"));
        zone9.add(new Zone("SBH04", "Balong"));
        zone9.add(new Zone("SBH04", "Merotai"));
        zone9.add(new Zone("SBH04", "Kalabakan"));
        zone9.add(new Zone("SBH04", "Bahagian Tawau (Barat)"));
        zone9.add(new Zone("SBH05", "Kudat"));
        zone9.add(new Zone("SBH05", "Kota Marudu"));
        zone9.add(new Zone("SBH05", "Pitas"));
        zone9.add(new Zone("SBH05", "Pulau Banggi"));
        zone9.add(new Zone("SBH05", "Bahagian Kudat"));
        zone9.add(new Zone("SBH06", "Gunung Kinabalu"));
        zone9.add(new Zone("SBH07", "Kota Kinabalu"));
        zone9.add(new Zone("SBH07", "Ranau"));
        zone9.add(new Zone("SBH07", "Kota Belud"));
        zone9.add(new Zone("SBH07", "Tuaran"));
        zone9.add(new Zone("SBH07", "Penampang"));
        zone9.add(new Zone("SBH07", "Papar"));
        zone9.add(new Zone("SBH07", "Putatan"));
        zone9.add(new Zone("SBH07", "Bahagian Pantai Barat"));
        zone9.add(new Zone("SBH08", "Pensiangan"));
        zone9.add(new Zone("SBH08", "Keningau"));
        zone9.add(new Zone("SBH08", "Tambunan"));
        zone9.add(new Zone("SBH08", "Nabawan"));
        zone9.add(new Zone("SBH08", "Bahagian Pendalaman (Atas)"));
        zone9.add(new Zone("SBH09", "Beaufort"));
        zone9.add(new Zone("SBH09", "Kuala Penyu"));
        zone9.add(new Zone("SBH09", "Sipitang"));
        zone9.add(new Zone("SBH09", "Tenom"));
        zone9.add(new Zone("SBH09", "Long Pa Sia"));
        zone9.add(new Zone("SBH09", "Membakut"));
        zone9.add(new Zone("SBH09", "Weston"));
        zone9.add(new Zone("SBH09", "Bahagian Pendalaman (Bawah)"));
        zones.put("Sabah", zone9);
        List<Zone> zone10 = new ArrayList<>();
        zone10.add(new Zone("SGR01", "Gombak"));
        zone10.add(new Zone("SGR01", "Petaling"));
        zone10.add(new Zone("SGR01", "Sepang"));
        zone10.add(new Zone("SGR01", "Hulu Langat"));
        zone10.add(new Zone("SGR01", "Hulu Selangor"));
        zone10.add(new Zone("SGR01", "Shah Alam"));
        zone10.add(new Zone("SGR02", "Kuala Selangor"));
        zone10.add(new Zone("SGR02", "Sabak Bernam"));
        zone10.add(new Zone("SGR03", "Klang"));
        zone10.add(new Zone("SGR03", "Kuala Langat"));
        zones.put("Selangor", zone10);
        List<Zone> zone11 = new ArrayList<>();
        zone11.add(new Zone("SWK01", "Limbang"));
        zone11.add(new Zone("SWK01", "Lawas"));
        zone11.add(new Zone("SWK01", "Sundar"));
        zone11.add(new Zone("SWK01", "Trusan"));
        zone11.add(new Zone("SWK02", "Miri"));
        zone11.add(new Zone("SWK02", "Niah"));
        zone11.add(new Zone("SWK02", "Bekenu"));
        zone11.add(new Zone("SWK02", "Sibuti"));
        zone11.add(new Zone("SWK02", "Marudi"));
        zone11.add(new Zone("SWK03", "Pandan"));
        zone11.add(new Zone("SWK03", "Belaga"));
        zone11.add(new Zone("SWK03", "Suai"));
        zone11.add(new Zone("SWK03", "Tatau"));
        zone11.add(new Zone("SWK03", "Sebauh"));
        zone11.add(new Zone("SWK03", "Bintulu"));
        zone11.add(new Zone("SWK04", "Sibu"));
        zone11.add(new Zone("SWK04", "Mukah"));
        zone11.add(new Zone("SWK04", "Dalat"));
        zone11.add(new Zone("SWK04", "Song"));
        zone11.add(new Zone("SWK04", "Igan"));
        zone11.add(new Zone("SWK04", "Oya"));
        zone11.add(new Zone("SWK04", "Balingian"));
        zone11.add(new Zone("SWK04", "Kanowit"));
        zone11.add(new Zone("SWK04", "Kapit"));
        zone11.add(new Zone("SWK05", "Sarikei"));
        zone11.add(new Zone("SWK05", "Matu"));
        zone11.add(new Zone("SWK05", "Julau"));
        zone11.add(new Zone("SWK05", "Rajang"));
        zone11.add(new Zone("SWK05", "Daro"));
        zone11.add(new Zone("SWK05", "Bintangor"));
        zone11.add(new Zone("SWK05", "Belawai"));
        zone11.add(new Zone("SWK06", "Lubok Antu"));
        zone11.add(new Zone("SWK06", "Sri Aman"));
        zone11.add(new Zone("SWK06", "Roban"));
        zone11.add(new Zone("SWK06", "Debak"));
        zone11.add(new Zone("SWK06", "Kabong"));
        zone11.add(new Zone("SWK06", "Lingga"));
        zone11.add(new Zone("SWK06", "Engkelili"));
        zone11.add(new Zone("SWK06", "Betong"));
        zone11.add(new Zone("SWK06", "Spaoh"));
        zone11.add(new Zone("SWK06", "Pusa"));
        zone11.add(new Zone("SWK06", "Saratok"));
        zone11.add(new Zone("SWK07", "Serian"));
        zone11.add(new Zone("SWK07", "Simunjan"));
        zone11.add(new Zone("SWK07", "Samarahan"));
        zone11.add(new Zone("SWK07", "Sebuyau"));
        zone11.add(new Zone("SWK07", "Meludam"));
        zone11.add(new Zone("SWK08", "Kuching"));
        zone11.add(new Zone("SWK08", "Bau"));
        zone11.add(new Zone("SWK08", "Lundu"));
        zone11.add(new Zone("SWK08", "Sematan"));
        zone11.add(new Zone("SWK09", "Zon Khas (Kampung Patarikan)"));
        zones.put("Sarawak", zone11);
        List<Zone> zone12 = new ArrayList<>();
        zone12.add(new Zone("TRG01", "Kuala Terengganu"));
        zone12.add(new Zone("TRG01", "Marang"));
        zone12.add(new Zone("TRG01", "Kuala Nerus"));
        zone12.add(new Zone("TRG02", "Besut"));
        zone12.add(new Zone("TRG02", "Setiu"));
        zone12.add(new Zone("TRG03", "Hulu Terengganu"));
        zone12.add(new Zone("TRG04", "Dungun"));
        zone12.add(new Zone("TRG04", "Kemaman"));
        zones.put("Terengganu", zone12);
        List<Zone> zone13 = new ArrayList<>();
        zone13.add(new Zone("WLY01", "Kuala Lumpur"));
        zone13.add(new Zone("WLY01", "Putrajaya"));
        zone13.add(new Zone("WLY02", "Labuan"));
        zones.put("Wilayah Persekutuan", zone13);
    }

    public static String getZoneId(String state, String zone) {
        for (Zone _zone : zones.get(state)) {
            if (_zone.getName().equalsIgnoreCase(zone)) {
                return _zone.getCode();
            }
        }
        return null;
    }
}
