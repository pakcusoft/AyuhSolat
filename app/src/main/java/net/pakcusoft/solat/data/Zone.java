package net.pakcusoft.solat.data;

public class Zone {
    private String code;
    private String name;

    public Zone(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
