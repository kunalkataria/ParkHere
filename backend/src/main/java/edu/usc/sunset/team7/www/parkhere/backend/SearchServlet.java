/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package edu.usc.sunset.team7.www.parkhere.backend;

import com.google.appengine.repackaged.com.google.gson.stream.JsonReader;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchServlet extends HttpServlet {
    //parkhere-ceccb.appspot.com
    static Logger Log = Logger.getLogger("edu.usc.sunset.team7.www.parkhere.backend.SearchServlet");
    public DataSnapshot lastDataSnapshot;
    public DatabaseReference listingsReference;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");

        String latitude = req.getParameter("lat");
        String longitude = req.getParameter("lon");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setServiceAccount(getServletContext().getResourceAsStream("/WEB-INF/ParkHere-9f6082855b14.json"))
                .setDatabaseUrl("https://parkhere-ceccb.firebaseio.com/")
                .build();
        try {
            FirebaseApp.initializeApp(options);
        } catch(Exception error) {
            Log.info("already exists...");
        }

        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference("listings");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    System.out.println(child);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error: " + databaseError);
            }
        });
        

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

    private boolean isWithinRadius(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = (lat2-lat1) * (Math.PI/180);
        double dLon = (lon2-lon1) * (Math.PI/180);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(lat1 * (Math.PI/180)) * Math.cos(lat2 * (Math.PI/180)) *
                   Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
        return (d <= 4.82803); //distance less than 3 miles converted to kilometers
    }

    private double avgParkingCost(double lat, double lon) {
        try {
            URL parkWhizURL = new URL("http://api.parkwhiz.com/search/");
            HttpURLConnection connection = (HttpURLConnection) parkWhizURL.openConnection();
            connection.setRequestMethod("GET");

            StringBuilder sb = new StringBuilder();
            sb.append("?lat="+lat);
            sb.append("&lon="+lon);
            sb.append("&key=b3178d71897bef674c87e96e597b6b54");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            String urlParameters = sb.toString();

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //read response into json objects
            InputStream is = connection.getInputStream();
            JsonReader reader = new JsonReader(new InputStreamReader(is));
            double totalCost = 0;
            int numberOfCosts = 0;
            reader.beginArray();
            while(reader.hasNext()) {
                reader.beginObject();
                String name = reader.nextName();
                if(name.equals("price")){
                   totalCost += reader.nextDouble();
                    numberOfCosts++;
                } else {
                    reader.skipValue();
                }
            }
            return (totalCost/(double)numberOfCosts);
        } catch (Exception e) {
            e.getMessage();
        }
        return -1;
    }

}
