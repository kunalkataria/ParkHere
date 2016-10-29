/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package edu.usc.sunset.team7.www.parkhere.backend;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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
    public boolean isInitialized;
    public SearchResult searchResult;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        final double latitude = Double.parseDouble(req.getParameter("lat"));
        final double longitude = Double.parseDouble(req.getParameter("lon"));

        System.out.println("Lat: " + latitude + " Long: " + longitude);

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
                .getReference("listings");

        final PrintWriter pw = resp.getWriter();

        ref.orderByChild("latitude").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                for(DataSnapshot childSnap : dataSnapshot.getChildren()) {
                    if(childSnap.hasChildren()) {
                        if(isWithinRadius(childSnap, latitude, longitude)) {
                            Listing listing = parseListing(childSnap);
                            double distance = distance(listing.getLatitude(), listing.getLongitude(), latitude, longitude);
                            ResultsPair resultsPair = new ResultsPair(listing, distance);
                            searchResult.addListing(resultsPair);
                        }
                    }
                }
                String json = new Gson().toJson(searchResult);
                System.out.println(json);
                pw.println(json);
                pw.flush();
                pw.close();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error: " + databaseError);
            }
        });

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
        double d = R * c;
        Log.info(Double.toString(d));
        return d;
    }

    private boolean isWithinRadius(DataSnapshot child, double latitude, double longitude) {
        double listingLat = -1, listingLong = -1;
        for(DataSnapshot childSnap : child.getChildren()) {
            if(childSnap.getKey().equals("latitude")) listingLat = Double.valueOf(childSnap.getValue().toString());
            else if(childSnap.getKey().equals("longitude")) listingLong = Double.valueOf(childSnap.getValue().toString());
        }
        if(listingLat == -1 || listingLong == -1) return false;
        return (distance(listingLat, listingLong, latitude, longitude) < 4828.03);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String name = req.getParameter("name");
        resp.setContentType("text/plain");
        if (name == null) {
            resp.getWriter().println("Please enter a name");
        }
        resp.getWriter().println("Hello " + name);
    }

    private Listing parseListing (DataSnapshot snapshot) {
        Listing listing = new Listing();
        for (DataSnapshot child : snapshot.getChildren()) {
            switch (child.getKey()) {
                case "active":
                    if (!Boolean.parseBoolean(child.getValue().toString())) continue;
                    break;
                case "compact":
                    listing.setCompact(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case "covered":
                    listing.setCovered(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case "description":
                    listing.setDescription(child.getValue().toString());
                    break;
                case "handicap":
                    listing.setHandicap(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case "latitude":
                    listing.setLatitude(Double.parseDouble(child.getValue().toString()));
                    break;
                case "longitude":
                    listing.setLongitude(Double.parseDouble(child.getValue().toString()));
                    break;
                case "name":
                    listing.setName(child.getValue().toString());
                    break;
                case "ownerID":
                    break;
                case "refundable":
                    listing.setRefundable(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case "startTime":
                    listing.setStartTime(Long.getLong(child.getValue().toString()));
                    break;
                case "stopTime":
                    listing.setStopTime(Long.getLong(child.getValue().toString()));
                    break;
            }
        }
        return listing;
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
