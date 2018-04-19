
package com.example.basedemo.sample.json.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attributes {

    @SerializedName("street")
    @Expose
    private String street;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("first-name")
    @Expose
    private String firstName;
    @SerializedName("last-name")
    @Expose
    private String lastName;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Attributes() {
    }

    /**
     * 
     * @param lastName
     * @param street
     * @param firstName
     * @param country
     * @param city
     */
    public Attributes(String street, String city, String country, String firstName, String lastName) {
        super();
        this.street = street;
        this.city = city;
        this.country = country;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Attributes withStreet(String street) {
        this.street = street;
        return this;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Attributes withCity(String city) {
        this.city = city;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Attributes withCountry(String country) {
        this.country = country;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Attributes withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Attributes withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

}
