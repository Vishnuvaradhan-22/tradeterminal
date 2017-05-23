package com.tradeterminal.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vish on 5/05/2017.
 */
public class BusinessLocation implements Serializable {

    private Long id;
    private String businessName;
    private Long businessId;
    private String businessLocationName;
    private boolean isMainLocation;
    private String addressLine1;
    private String addressLine2;
    private String suburb;
    private String stateName;
    private String postCode;
    private String countryName;
    private String phoneNumber;
    private Calendar createdDate;
    private String createdBy;
    private Map<String,ArrayList<Calendar>> businessHours;

    public BusinessLocation(){
        businessHours = new HashMap<>();
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getBusinessLocationName() {
        return businessLocationName;
    }

    public void setBusinessLocationName(String businessLocationName) {
        this.businessLocationName = businessLocationName;
    }

    public boolean isMainLocation() {
        return isMainLocation;
    }

    public void setMainLocation(boolean mainLocation) {
        isMainLocation = mainLocation;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Calendar getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Calendar createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Map getBusinessHours(){
        return this.businessHours;
    }

    public void addBusinessHour(String day, ArrayList<Calendar> hours){
        businessHours.put(day,hours);
    }

    public void setBusinessHours(Map<String, ArrayList<Calendar>> businessHours) {
        this.businessHours = businessHours;
    }
}
