package net.pakcusoft.solat.service;

import android.util.Log;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class EsolatService {
    static final SSLSocketFactory getFactory() {
        try {
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            final TrustManager[] trusts = new TrustManager[]{ new TrustAllCerts() };
            sslContext.init(null, trusts, new SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            return sslSocketFactory;
        } catch (Exception ex) {
        }
        return null;
    }

    static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .sslSocketFactory(getFactory(), new TrustAllCerts())
            .build();

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
                Log.d("XXX", "error okhttp", e);
                listener.failure(e);
            }
        });
    }

    public void getSolatTime(String zone, EsolatServiceListener listener) {
        getTomorrowSolatTime(zone, null, listener);
    }
}
