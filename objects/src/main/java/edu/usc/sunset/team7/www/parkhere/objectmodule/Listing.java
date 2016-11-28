package edu.usc.sunset.team7.www.parkhere.objectmodule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by kunal on 10/12/16.
 */

public class Listing implements Serializable {

    private static final long serialVersionUID = 1L;

    private ParkingSpot parkingSpot;
    private String name;
    private String description;
    private long startTime;
    private long stopTime;
    private ArrayList<Integer> timesAvailable;
    private boolean refundable;
    private String listingID;
    private double price;
    private String providerID;
    private long increment;

    public Listing() {
        parkingSpot = new ParkingSpot();
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public void setParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpot = parkingSpot;
    }

    public long getIncrement() {
        return increment;
    }

    public void setIncrement(long increment) {
        this.increment = increment;
    }

    public ArrayList<Integer> getTimesAvailable() {
        return timesAvailable;
    }

    public void setTimesAvailable(String timesAvailableString) {
        String[] timeAvailability = timesAvailableString.split(",");
        timesAvailable = new ArrayList<>();
        for (int i = 0; i < timeAvailability.length; i++) {
            int currTime = Integer.parseInt(timeAvailability[i]);
            timesAvailable.add(currTime);
        }
    }

    public void addTimeAvailable(int timeAvailable) {
        if (timesAvailable != null) {
            timesAvailable.add(timeAvailable);
            Collections.sort(timesAvailable);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return parkingSpot.getLatitude();
    }

    public double getLongitude() {
        return parkingSpot.getLongitude();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHandicap() {
        return parkingSpot.isHandicap();
    }

    public boolean isCovered() {
        return parkingSpot.isCovered();
    }

    public boolean isCompact() {
        return parkingSpot.isCompact();
    }

    public long getStartTime() {
        return startTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public void setStartTime(long startTime) { this.startTime = startTime; }

    public void setStopTime(long endTime) { this.stopTime = endTime; }

    public boolean isRefundable() {
        return refundable;
    }

    public void setRefundable(boolean refundable) {
        this.refundable = refundable;
    }

    public String getListingID() {
        return listingID;
    }

    public void setListingID(String listingID) {
        this.listingID = listingID;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageURL() {
        return parkingSpot.getImageURL();
    }

    public String getProviderID() {
        return providerID;
    }

    public void setProviderID(String providerID) {
        this.providerID = providerID;
    }

    public void setParkingID(String parkingID) {
        this.parkingSpot.setParkingSpotID(parkingID);
    }

    public String getParkingID() {
        return this.parkingSpot.getParkingSpotID();
    }
}
