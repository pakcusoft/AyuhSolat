package net.pakcusoft.solat.data;

import androidx.annotation.NonNull;

import java.util.List;

public class DataZone {
    public Data data;

    public class Data {
        public Negeri negeri;
    }

    public class Negeri {
        public String nama;
        public List<String> zon;
    }
}
