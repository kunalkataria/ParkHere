package edu.usc.sunset.team7.www.parkhere.Services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kunal on 11/15/16.
 */

public class EmailService extends IntentService {

    public EmailService() {
        super(Consts.EMAIL_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("EMAIL SERVICE", "**********EMAIL SERVICE ON HANDLE INTENT");
        String email = intent.getStringExtra(Consts.EMAIL_EXTRA);
        String textBody = intent.getStringExtra(Consts.TEXT_BODY_EXTRA);

        Gson gson = new GsonBuilder().create();

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        HttpLoggingInterceptor httpLogger = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {

            @Override
            public void log(String message) {
                Log.i("EmailService intercept:", message);
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

        URLEndpoints urlEndpoint = retrofit.create(URLEndpoints.class);

        Call<Void> emailReq = urlEndpoint.emailConfirmation(email, textBody);

        try {
            Response<Void> emailRequestResponse = emailReq.execute();
            if (emailRequestResponse.isSuccessful()) {
                Intent finishedIntent = new Intent(Consts.EMAIL_INTENT_FILTER);
                LocalBroadcastManager.getInstance(EmailService.this).sendBroadcast(finishedIntent);
            } else {
                Log.i("TESTING*********", "REQUEST UNSUCCESSFUL");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
