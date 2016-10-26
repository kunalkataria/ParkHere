package edu.usc.sunset.team7.www.parkhere.objectmodule;

/**
 * Created by johnsonhui on 10/14/16.
 */

public class Payment {
    private String firstName;
    private String lastName;
    private String address;
    private int zipCode;
    private String city;
    private String ccNumber;
    private String expiration;
    private String ccv;
    private String bankAccountNumber;
    private String routingNumber;

    public Payment(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAddress(String address){
        this.address = address;
    }
    public void setCity(String city){
        this.city = city;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setCardNumber(String ccNumber){
        this.ccNumber = ccNumber;
    }

    public void setCardExpiration(String expiration){
        this.expiration = expiration;
    }

    public void setCCV(String ccv) {
        this.ccv = ccv;
    }

    public String getCCNumber() {
        return ccNumber;
    }

    public String getExpiration() {
        return expiration;
    }

    public String getCCV() {
        return ccv;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankingAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getRoutingNumber() {
        return routingNumber;
    }

    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }
}

