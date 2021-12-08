package net.pakcusoft.solat.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainService {
    static Retrofit retrofit;
    static {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://waktu-solat-api.herokuapp.com/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public static SolatService getService() {
        return retrofit.create(SolatService.class);
    }
}
