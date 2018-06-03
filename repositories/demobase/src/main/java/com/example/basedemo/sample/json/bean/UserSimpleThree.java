
package com.example.basedemo.sample.json.bean;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserSimpleThree {

    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("ulist")
    @Expose
    private List<Ulist> ulist = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public UserSimpleThree() {
    }

    /**
     * 
     * @param total
     * @param ulist
     */
    public UserSimpleThree(Integer total, List<Ulist> ulist) {
        super();
        this.total = total;
        this.ulist = ulist;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public UserSimpleThree withTotal(Integer total) {
        this.total = total;
        return this;
    }

    public List<Ulist> getUlist() {
        return ulist;
    }

    public void setUlist(List<Ulist> ulist) {
        this.ulist = ulist;
    }

    public UserSimpleThree withUlist(List<Ulist> ulist) {
        this.ulist = ulist;
        return this;
    }

}
