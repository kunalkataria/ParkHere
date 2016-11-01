/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package edu.usc.sunset.team7.www.parkhere.backend;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;
import edu.usc.sunset.team7.www.parkhere.objectmodule.ResultsPair;
import edu.usc.sunset.team7.www.parkhere.objectmodule.SearchResult;

public class SearchServlet extends HttpServlet {
    //parkhere-ceccb.appspot.com
    static Logger Log = Logger.getLogger("edu.usc.sunset.team7.www.parkhere.backend.SearchServlet");
    public DataSnapshot lastDataSnapshot;
    public DatabaseReference listingsReference;
    public SearchResult searchResult;
    public boolean isInitialized;
    public boolean done;
    public PrintWriter pw;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        pw = resp.getWriter();

        final double latitude = Double.parseDouble(req.getParameter("lat"));
        final double longitude = Double.parseDouble(req.getParameter("lon"));
        final long startTime = Long.parseLong(req.getParameter("startTime"));
        final long stopTime = Long.parseLong(req.getParameter("stopTime"));

        searchResult = new SearchResult(avgParkingCost(latitude, longitude));

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setServiceAccount(getServletContext().getResourceAsStream("/WEB-INF/ParkHere-9f6082855b14.json"))
                .setDatabaseUrl("https://parkhere-ceccb.firebaseio.com/")
                .build();
        try {
            FirebaseApp.getInstance();
        } catch (Exception error) {
            Log.info(error.getMessage());
            Log.info("get instance error");
        }

        if(!isInitialized) {
            try {
                FirebaseApp.initializeApp(options);
                isInitialized = true;
            } catch (Exception error) {
                Log.info(error.getMessage());
                Log.info("initialize app error");
            }
        }

        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference("Listings");

        resp.setStatus(HttpServletResponse.SC_OK);
        done = false;
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //looping through providers
                for(DataSnapshot provider : dataSnapshot.getChildren()) {
                    String providerID = provider.getKey();
                    //looping through active listings
                    for(DataSnapshot listing : provider.child("Active Listings").getChildren()) {
                        String listingID = listing.getKey();
                        processListing(listing, latitude, longitude,
                                startTime, stopTime, providerID, listingID);
                    }
                }
                done = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        while(!done) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }

        String searchResultAsJson = new Gson().toJson(searchResult);
        System.out.println(searchResultAsJson);
        pw.write(searchResultAsJson);
        pw.flush();
    }

    //returns distance in meters between two lat/long pairs
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = (lat2-lat1) * (Math.PI/180);
        double dLon = (lon2-lon1) * (Math.PI/180);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * (Math.PI/180)) * Math.cos(lat2 * (Math.PI/180)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c * 0.621371;
        return d;
    }

    //checks if a listing is within 3 miles of the searched location
    private boolean isWithinRadius(DataSnapshot child, double latitude, double longitude) {
        double listingLat = -1, listingLong = -1;
        for(DataSnapshot childSnap : child.getChildren()) {
            if(childSnap.getKey().equals("Latitude")) listingLat = Double.valueOf(childSnap.getValue().toString());
            else if(childSnap.getKey().equals("Longitude")) listingLong = Double.valueOf(childSnap.getValue().toString());
        }
        if(listingLat == -1 || listingLong == -1) return false;
        return (distance(listingLat, listingLong, latitude, longitude) <= 3);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.getWriter().println("POST requests are not supported");
    }
    //checks if a listing is within searched time constraints
    private boolean isWithinTimeConstraints(DataSnapshot child, long startTime, long stopTime,
                                            String providerID, String listingID) {
        long listingStartTime = -1;
        long listingStopTime = -1;
        for(DataSnapshot childSnap : child.getChildren()) {
            if(childSnap.getKey().equals("Start Time")) {
                listingStartTime = Long.parseLong(childSnap.getValue().toString());
            } else if(childSnap.getKey().equals("End Time")) {
                listingStopTime = Long.parseLong(childSnap.getValue().toString());
            }
        }
        if(listingStartTime == -1 || listingStopTime == -1) return false;
        if(listingStopTime < (System.currentTimeMillis()/1000)){
            moveListingToInactive(child, providerID, listingID);
            return false;
        }
        if(listingStartTime > startTime) return false;
        if(stopTime != -1 && listingStopTime < stopTime) return false;
        return true;
    }

    private Listing parseListing (DataSnapshot snapshot) {
        Listing listing = new Listing();
        for (DataSnapshot child : snapshot.getChildren()) {
            switch (child.getKey()) {
                case "Compact":
                    listing.setCompact(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case "Covered":
                    listing.setCovered(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case "Listing Description":
                    listing.setDescription(child.getValue().toString());
                    break;
                case "Handicap":
                    listing.setHandicap(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case "Image URL":
                    listing.setImageURL(child.getValue().toString());
                    break;
                case "Latitude":
                    listing.setLatitude(Double.parseDouble(child.getValue().toString()));
                    break;
                case "Longitude":
                    listing.setLongitude(Double.parseDouble(child.getValue().toString()));
                    break;
                case "Listing Name":
                    listing.setName(child.getValue().toString());
                    break;
                case "Is Refundable":
                    listing.setRefundable(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case "Start Time":
                    String startTime = child.getValue().toString();
                    listing.setStartTime(Long.valueOf(startTime));
                    break;
                case "End Time":
                    String stopTime = child.getValue().toString();
                    listing.setStopTime(Long.valueOf(stopTime));
                    break;
                case "Price":
                    listing.setPrice(Double.parseDouble(child.getValue().toString()));
            }
        }
        return listing;
    }

    private void processListing(DataSnapshot dataSnapshot, double latitude, double longitude,
                                long startTime, long stopTime, String providerID, String listingID) {
        if(isWithinRadius(dataSnapshot, latitude, longitude) &&
                isWithinTimeConstraints(dataSnapshot, startTime, stopTime, providerID, listingID)) {
            System.out.println("Listing processed");
            Listing listing = parseListing(dataSnapshot);
            listing.setProviderID(providerID);
            listing.setListingID(listingID);
            double distance = distance(listing.getLatitude(), listing.getLongitude(), latitude, longitude);
            ResultsPair resultsPair = new ResultsPair(listing, distance);
            searchResult.addListing(resultsPair);
        }
    }

    private void moveListingToInactive(DataSnapshot listing, String providerID, String listingID) {
        Listing toMove = parseListing(listing);
        //Move Listing to inactive
        DatabaseReference inactiveListingRef = FirebaseDatabase.getInstance().getReference()
                .child("Listings").child(providerID).child("Inactive Listings").child(listingID);
        inactiveListingRef.child("Listing Name").setValue(toMove.getName());
        inactiveListingRef.child("Listing Description").setValue(toMove.getDescription());
        inactiveListingRef.child("Is Refundable").setValue(toMove.isRefundable());
        inactiveListingRef.child("Price").setValue(toMove.getPrice());
        inactiveListingRef.child("Compact").setValue(toMove.isCompact());
        inactiveListingRef.child("Covered").setValue(toMove.isCovered());
        inactiveListingRef.child("Handicap").setValue(toMove.isHandicap());
        inactiveListingRef.child("Latitude").setValue(toMove.getLatitude());
        inactiveListingRef.child("Longitude").setValue(toMove.getLongitude());
        inactiveListingRef.child("Start Time").setValue(toMove.getStartTime());
        inactiveListingRef.child("End Time").setValue(toMove.getStopTime());
        inactiveListingRef.child("Paid").setValue(true);

        //Remove listing from active
        FirebaseDatabase.getInstance().getReference().child("Listings").child(providerID)
                .child("Active Listings").child(listingID).removeValue();
        Log.info("Listing moved from active to inactive due to invalid stop time");
    }

    private double avgParkingCost(double lat, double lon) {
        try {
            String url = "http://api.parkwhiz.com/search/";
            String charset = "UTF-8";
            String param1 = Double.toString(lat);
            String param2 = Double.toString(lon);
            String param3 = "b3178d71897bef674c87e96e597b6b54";
            String query = String.format("lat=%s&lng=%s&key=%s", URLEncoder.encode(param1, charset), URLEncoder.encode(param2, charset), URLEncoder.encode(param3, charset));

            URLConnection connection = new URL(url + "?" + query).openConnection();
            connection.setRequestProperty("Accept-Charset", charset);
            InputStream response = connection.getInputStream();
            Scanner scanner = new Scanner(response);
            String responseBody = scanner.useDelimiter("\\A").next();
            JSONObject jsonObj = new JSONObject(responseBody);
            JSONArray listings = jsonObj.getJSONArray("parking_listings");
            double totalPrice = 0;
            for (int i = 0; i < listings.length(); i++) {
                totalPrice += listings.getJSONObject(i).getDouble("price");
            }
            double averagePrice = totalPrice/listings.length();
            return averagePrice;
        } catch (Exception e) {
            Log.info(e.getMessage());
        }
        return -1;
    }

}
