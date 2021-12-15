package net.pakcusoft.solat.service;

import android.util.Log;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class EsolatService {
    static OkHttpClient client = new OkHttpClient();

    public OkHttpClient getClient() {
        return client;
    }

    public void getTomorrowSolatTime(String zone, String date, EsolatServiceListener listener) {
        HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                .scheme("https")
                .host("www.e-solat.gov.my")
                .addPathSegment("index.php")
                .addQueryParameter("r", "esolatApi/TakwimSolat")
                .addQueryParameter("zone", zone);
        if (date != null) {
            urlBuilder = urlBuilder
                    .addQueryParameter("period", "date")
                    .addQueryParameter("date", date);
        } else {
            urlBuilder = urlBuilder
                    .addQueryParameter("period", "today");
        }
        Log.d("XXX", urlBuilder.toString());
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();

        getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful()) {
                        listener.complete(responseBody.string());
                    } else {
                        listener.failure(new Exception("Response Error"));
                    }
                }
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                listener.failure(e);
            }
        });
    }

    public void getSolatTime(String zone, EsolatServiceListener listener) {
        getTomorrowSolatTime(zone, null, listener);
    }
}
