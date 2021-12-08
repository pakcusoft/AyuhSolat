package net.pakcusoft.solat.service;

import net.pakcusoft.solat.data.DataNegeri;
import net.pakcusoft.solat.data.DataPrayerTime;
import net.pakcusoft.solat.data.DataZone;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SolatService {
    @GET("states.json")
    Call<DataNegeri> getStates();

    @GET("states.json")
    Call<DataZone> getZones(@Query("negeri") String negeri);

    @GET("prayer_times.json")
    Call<DataPrayerTime> getPrayerTime(@Query("negeri") String negeri, @Query("zon") String zon);
}
