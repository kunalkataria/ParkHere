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
import edu.usc.sunset.team7.www.parkhere.objectmodule.ParkingSpot;
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
    public String searchResultAsJSON;

    public double latitude;
    public double longitude;
    public long startTime;
    public long stopTime;

    public DataSnapshot db;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        pw = resp.getWriter();
        done = false;

        latitude = Double.parseDouble(req.getParameter("lat"));
        longitude = Double.parseDouble(req.getParameter("lon"));
        startTime = Long.parseLong(req.getParameter("startTime"));
        stopTime = Long.parseLong(req.getParameter("stopTime"));

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
                .getReference();

        resp.setStatus(HttpServletResponse.SC_OK);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                startSearch(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        while(!done) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        pw.write(searchResultAsJSON);
        pw.flush();
    }

    private void startSearch(DataSnapshot db) {
        this.db = db;
        DataSnapshot listingsDB = db.child("Listings");
        String userID;
        String spotID;

        searchResult = new SearchResult(avgParkingCost(latitude, longitude));

        for(DataSnapshot user : listingsDB.getChildren()){
            userID = user.getKey();
            for(DataSnapshot activeListing : user.child("Active Listings").getChildren()) {
                spotID = activeListing.child("ParkingID").getValue().toString();
                if(isWithinRadius(userID,spotID) && isWithinTimeConstraints(activeListing)) {
                    Listing listing = parseListing(activeListing);
                    listing.setParkingSpot(parseParkingSpot(userID, spotID));
                    listing.setProviderID(userID);
                    listing.setListingID(activeListing.getKey());
                    double distance = distance(listing.getLatitude(), listing.getLongitude(),
                            latitude, longitude);
                    ResultsPair pair = new ResultsPair(listing, distance);
                    searchResult.addListing(pair);
                }
            }
        }
        searchResultAsJSON = new Gson().toJson(searchResult);
        done = true;
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

    //checks if a parking spot is within 3 miles of the searched location
    private boolean isWithinRadius(String userID, String spotID) {
        System.out.println(userID + " " + spotID);
        DataSnapshot parkingSpot = db.child("Parking Spots").child(userID).child(spotID);
        if(!parkingSpot.child("Latitude").exists() || !parkingSpot.child("Longitude").exists()) return false;
        double latitude = Double.parseDouble(parkingSpot.child("Latitude").getValue().toString());
        double longitude = Double.parseDouble(parkingSpot.child("Longitude").getValue().toString());
        if (distance(latitude, longitude, this.latitude, this.longitude) <= 3 ) return true;
        return false;
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.getWriter().println("POST requests are not supported");
    }

    private boolean isWithinTimeConstraints(DataSnapshot activeListing) {
        if(!Boolean.parseBoolean(activeListing.child("Currently Active").getValue().toString())) return false;
        double startTime = Long.parseLong(activeListing.child("Start Time").getValue().toString());
        double stopTime = Long.parseLong(activeListing.child("End Time").getValue().toString());
        if(this.stopTime == -1) return (this.startTime >= startTime);
        else return (this.startTime >= startTime && this.stopTime <= stopTime);
    }

    private ParkingSpot parseParkingSpot (String providerID, String spotID) {
        DataSnapshot parkingSpotSnapshot = db.child("Parking Spots").child(providerID).child(spotID);
        ParkingSpot spot = new ParkingSpot();
        spot.setParkingSpotID(parkingSpotSnapshot.getKey());
        for(DataSnapshot param : parkingSpotSnapshot.getChildren()) {
            switch(param.getKey()) {
                case "Name":
                    spot.setName(param.getValue().toString());
                    break;
                case "Booking Count":
                    spot.setBookingCount(Integer.parseInt(param.getValue().toString()));
                    break;
                case "Compact":
                    spot.setCompact(Boolean.parseBoolean(param.getValue().toString()));
                    break;
                case "Covered":
                    spot.setCovered(Boolean.parseBoolean(param.getValue().toString()));
                    break;
                case "Handicap":
                    spot.setHandicap(Boolean.parseBoolean(param.getValue().toString()));
                    break;
                case "ImageURL":
                    spot.setImageURL(param.getValue().toString());
                    break;
                case "Latitude":
                    spot.setLatitude(Double.parseDouble(param.getValue().toString()));
                    break;
                case "Longitude":
                    spot.setLongitude(Double.parseDouble(param.getValue().toString()));
                    break;
            }
        }
        return spot;
    }

    private Listing parseListing (DataSnapshot activeListing) {
        Listing listing = new Listing();
        for (DataSnapshot child : activeListing.getChildren()) {
            switch (child.getKey()) {
                case "Listing Description":
                    listing.setDescription(child.getValue().toString());
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
                    break;
                case "Book Time Increment":
                    listing.setIncrement(Long.parseLong(child.getValue().toString()));
                    break;
                case "Active Times":
                    listing.setTimesAvailable(child.getValue().toString());
                    break;
                case "ParkingID":
                    listing.setParkingID(child.getValue().toString());
                    break;
            }
        }
        return listing;
    }

//    private void moveListingToInactive(DataSnapshot listing, String providerID, String listingID) {
//        Listing toMove = parseListing(listing);
//        //Move Listing to inactive
//        DatabaseReference inactiveListingRef = FirebaseDatabase.getInstance().getReference()
//                .child("Listings").child(providerID).child("Inactive Listings").child(listingID);
//        inactiveListingRef.child("Listing Name").setValue(toMove.getName());
//        inactiveListingRef.child("Listing Description").setValue(toMove.getDescription());
//        inactiveListingRef.child("Is Refundable").setValue(toMove.isRefundable());
//        inactiveListingRef.child("Price").setValue(toMove.getPrice());
//        inactiveListingRef.child("Compact").setValue(toMove.isCompact());
//        inactiveListingRef.child("Covered").setValue(toMove.isCovered());
//        inactiveListingRef.child("Handicap").setValue(toMove.isHandicap());
//        inactiveListingRef.child("Latitude").setValue(toMove.getLatitude());
//        inactiveListingRef.child("Longitude").setValue(toMove.getLongitude());
//        inactiveListingRef.child("Start Time").setValue(toMove.getStartTime());
//        inactiveListingRef.child("End Time").setValue(toMove.getStopTime());
//        inactiveListingRef.child("Paid").setValue(true);
//
//        //Remove listing from active
//        FirebaseDatabase.getInstance().getReference().child("Listings").child(providerID)
//                .child("Active Listings").child(listingID).removeValue();
//        Log.info("Listing moved from active to inactive due to invalid stop time");
//    }

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
            System.out.println(averagePrice);
            return averagePrice;
        } catch (Exception e) {
            Log.info(e.getMessage());
        }
        return -1;
    }

}
