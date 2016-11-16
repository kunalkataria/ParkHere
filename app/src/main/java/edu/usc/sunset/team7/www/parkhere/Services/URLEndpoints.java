package edu.usc.sunset.team7.www.parkhere.Services;

import edu.usc.sunset.team7.www.parkhere.objectmodule.SearchResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by kunal on 10/15/16.
 * This file should contain the URL endpoints for requests sent to our server
 */

public interface URLEndpoints {
    @GET("/search")
    Call<SearchResult> getResults(@Query("lat") double lat,
                                  @Query("lon") double lon,
                                  @Query("startTime") long startTime,
                                  @Query("stopTime") long stopTime);

    @GET("/mail")
    Call<Void> emailConfirmation(@Query("email") String email,
                                 @Query("textBody") String textBody);
}
