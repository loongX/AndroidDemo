
package com.example.basedemo.sample.json.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ulist {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("attributes")
    @Expose
    private Attributes attributes;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Ulist() {
    }

    /**
     * 
     * @param attributes
     * @param type
     */
    public Ulist(String type, Attributes attributes) {
        super();
        this.type = type;
        this.attributes = attributes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Ulist withType(String type) {
        this.type = type;
        return this;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public Ulist withAttributes(Attributes attributes) {
        this.attributes = attributes;
        return this;
    }

}
