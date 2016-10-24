/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package edu.usc.sunset.team7.www.parkhere.backend;

import com.google.appengine.repackaged.com.google.api.client.json.Json;
import com.google.appengine.repackaged.com.google.gson.stream.JsonReader;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import java.util.logging.Logger;

import javax.servlet.http.*;

public class SearchServlet extends HttpServlet {

    static Logger Log = Logger.getLogger("edu.usc.sunset.team7.www.parkhere.backend.ServerServlet");
    public DataSnapshot lastDataSnapshot;
    public DatabaseReference listingsReference;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
//        resp.getWriter().println("Please use the form to POST to this url");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        listingsReference = database.getReference("listings");

        // get that data and return it :)
        String latitude = req.getParameter("lat");
        String longitude = req.getParameter("long");

        listingsReference.orderByChild("name").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    private boolean isWithinRadius(Location a, Location b) {
        return (a.distanceTo(b) <= 4828.02); //number of meters in 3 miles
    }

    private double avgParkingCost(double lat, double lon) {
        try {
            URL parkWhizURL = new URL("http://api.parkwhiz.com/search/");
            HttpURLConnection connection = (HttpURLConnection) parkWhizURL.openConnection();
            connection.setRequestMethod("GET");

            StringBuilder sb = new StringBuilder();
            sb.append("?lat="+lat);
            sb.append("&long="+lon);
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
