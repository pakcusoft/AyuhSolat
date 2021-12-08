package net.pakcusoft.solat.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JakimService {
    static Retrofit retrofit;
    static {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.e-solat.gov.my/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public static EsolatService getService() {
        return retrofit.create(EsolatService.class);
    }
}
