package net.pakcusoft.solat.data;

import java.util.List;

public class ESolatData {
    private String date;
    private List<WaktuSolat> waktuSolatList;

    public ESolatData(String date, List<WaktuSolat> waktuSolatList) {
        this.date = date;
        this.waktuSolatList = waktuSolatList;
    }

    public String getDate() {
        return date;
    }

    public List<WaktuSolat> getWaktuSolatList() {
        return waktuSolatList;
    }
}
