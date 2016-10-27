package edu.usc.sunset.team7.www.parkhere.Services;

import edu.usc.sunset.team7.www.parkhere.objectmodule.SearchResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by kunal on 10/15/16.
 * This file should contain the URL endpoints for requests sent to our server
 */

public interface URLEndpoints {
    @GET("/search?lat={lat}?lon={lon}?startTime={startTime}?stopTime={stopTime}")
    Call<SearchResult> getResults(@Path("lat") long lat, @Path("lon") long lon,
                                  @Path("startTime") long startTime, @Path("stopTime") long stopTime);

}
