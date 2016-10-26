package edu.usc.sunset.team7.www.parkhere.Services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.SearchResult;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kunal on 10/24/16.
 */

public class SearchService extends IntentService {

    public SearchService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        long latitude = intent.getLongExtra(Consts.LATITUDE_EXTRA, 0);
        long longitude = intent.getLongExtra(Consts.LONGITUDE_EXTRA, 0);
        long startTime = intent.getLongExtra(Consts.START_TIME_EXTRA, 0);
        long stopTime = intent.getLongExtra(Consts.STOP_TIME_EXTRA, 0);

        Gson gson = new GsonBuilder().create();

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        HttpLoggingInterceptor httpLogger = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i("interceptor:", message);
            }
        });

        httpLogger.setLevel(HttpLoggingInterceptor.Level.BASIC);

        clientBuilder.addInterceptor(httpLogger);

        OkHttpClient okClient = clientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Consts.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okClient)
                .build();

        URLEndpoints urlService = retrofit.create(URLEndpoints.class);

        Call<SearchResult> searchResultReq = urlService.getResults(latitude, longitude, startTime, stopTime);

        try {
            Response<SearchResult> searchResultResponse = searchResultReq.execute();
            if (searchResultResponse.isSuccessful()) {
                SearchResult searchResult = searchResultResponse.body();

            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
