package net.pakcusoft.solat;

import net.pakcusoft.solat.data.DataNegeri;
import net.pakcusoft.solat.data.DataPrayerTime;
import net.pakcusoft.solat.data.DataZone;
import net.pakcusoft.solat.service.MainService;
import net.pakcusoft.solat.service.SolatService;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import retrofit2.Call;

public class MainServiceUnitTest {
    @Test
    public void test_solatService() throws IOException {
        SolatService service = MainService.getService();
        Call<DataNegeri> dataNegeri = service.getStates();
        System.out.println(dataNegeri.execute().body());
        Call<DataZone> dataZone = service.getZones("selangor");
        System.out.println(dataZone.execute().body());
        Call<DataPrayerTime> dataPrayerTime = service.getPrayerTime("selangor", "petaling");
        System.out.println(dataPrayerTime.execute().body());
        Assert.assertEquals(2, 2);
    }
}
