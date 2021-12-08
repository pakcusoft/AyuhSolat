package net.pakcusoft.solat.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EsolatService {
    @GET("index.php?r=esolatApi/tarikhtakwim&period=today&datetype=miladi")
    Call<ResponseBody> getHijriDate(@Query("date") String date);
}
